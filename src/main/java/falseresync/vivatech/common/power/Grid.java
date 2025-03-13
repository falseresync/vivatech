package falseresync.vivatech.common.power;

import falseresync.vivatech.common.VivatechUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.SimpleGraph;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class Grid {
    private final SimpleGraph<GridVertex, GridEdge> graph;
    private final Map<BlockPos, Appliance> appliances;
    private final Map<ChunkPos, Set<BlockPos>> trackedChunks;
    private final Set<ChunkPos> unloadedChunks;
    private final GridsManager gridsManager;
    private final ServerWorld world;
    private final WireType wireType;
    private int overcurrentTicks = 0;
    private float lastVoltage = 0;
    private float lastCurrent = 0;
    private boolean frozen = false;

    public Grid(GridsManager gridsManager, ServerWorld world, WireType wireType) {
        this.gridsManager = gridsManager;
        this.world = world;
        this.wireType = wireType;
        graph = new SimpleGraph<>(GridEdge.class);
        appliances = new Object2ReferenceRBTreeMap<>();
        trackedChunks = PowerSystem.createChunkPosKeyedMap();
        unloadedChunks = PowerSystem.createChunkPosSet();
    }

    public Grid(GridsManager gridsManager, ServerWorld world, WireType wireType, Set<GridEdge> edges) {
        this(gridsManager, world, wireType);
        edges.forEach(this::connect);
    }

    public GridSnapshot createSnapshot() {
        return new GridSnapshot(wireType, graph.edgeSet());
    }

    public WireType getWireType() {
        return wireType;
    }

    public boolean connect(GridEdge edge) {
        var vertexU = PowerSystem.GRID_VERTEX.find(world, edge.u(), null);
        var vertexV = PowerSystem.GRID_VERTEX.find(world, edge.v(), null);
        return connect(vertexU, vertexV, () -> edge);
    }

    public boolean connect(GridVertex vertexU, GridVertex vertexV) {
        return connect(vertexU, vertexV, () -> new GridEdge(vertexU.pos(), vertexV.pos()));
    }

    public boolean connect(GridVertex vertexU, GridVertex vertexV, Supplier<GridEdge> edgeSupplier) {
        if (frozen) {
            return false;
        }
        if (vertexU != null && vertexV != null) {
            initOrMerge(vertexU);
            initOrMerge(vertexV);
            var edge = edgeSupplier.get();
            var wasModified = graph.addEdge(vertexU, vertexV, edge);
            if (wasModified) {
                gridsManager.onWireAdded(edge.toServerWire());
            }
            return wasModified;
        }
        return false;
    }

    private void initOrMerge(GridVertex vertex) {
        var otherGrid = gridsManager.getGridLookup().get(vertex.pos());
        if (otherGrid == null) {
            graph.addVertex(vertex);
            onVertexAdded(vertex, false);
        } else if (otherGrid != this) {
            merge(otherGrid.graph);
            gridsManager.getGrids().remove(otherGrid);
        }
    }

    private void merge(Graph<GridVertex, GridEdge> otherGraph) {
        Graphs.addGraph(this.graph, otherGraph);
        for (var vertex : otherGraph.vertexSet()) {
            onVertexAdded(vertex, true);
        }
    }

    public boolean remove(BlockPos pos, BlockState state) {
        var vertex = PowerSystem.GRID_VERTEX.find(world, pos, state, null, null);
        if (vertex == null) {
            onVertexRemoved(pos, null);
            return false;
        }

        graph.edgesOf(vertex).forEach(this::onWireRemoved);
        if (!graph.removeVertex(vertex)) {
            return false;
        }

        onVertexRemoved(vertex);
        partition();
        return true;
    }

    public boolean disconnect(GridVertex vertexU, GridVertex vertexV) {
        return disconnect(new GridEdge(vertexU.pos(), vertexV.pos()));
    }

    public boolean disconnect(GridEdge edge) {
        if (!graph.removeEdge(edge)) {
            return false;
        }

        onWireRemoved(edge);
        partition();
        return true;
    }

    private void partition() {
        var inspector = new BiconnectivityInspector<>(graph);
        if (inspector.isConnected()) {
            return;
        }

        for (var isolatedGraph : inspector.getConnectedComponents()) {
            if (isolatedGraph.vertexSet().size() <= 1) {
                isolatedGraph.vertexSet().forEach(this::onVertexRemoved);
            } else {
                var other = gridsManager.create(wireType);
                other.merge(isolatedGraph);
                var iterator = unloadedChunks.iterator();
                while (iterator.hasNext()) {
                    var chunkPos = iterator.next();
                    if (other.tracksChunk(chunkPos)) {
                        other.onChunkUnloaded(chunkPos);
                        iterator.remove();
                    }
                }
            }
        }

        gridsManager.getGrids().remove(this);
    }

    private void onVertexAdded(GridVertex vertex, boolean transferred) {
        gridsManager.getGridLookup().put(vertex.pos(), this);
        if (vertex.appliance() != null) {
            if (!appliances.containsValue(vertex.appliance())) {
                onApplianceAdded(vertex.appliance(), transferred);
                var appliancePos = vertex.appliance().getPos();
                trackedChunks.computeIfAbsent(new ChunkPos(appliancePos), key -> new ReferenceOpenHashSet<>()).add(appliancePos);
            }
        }
    }

    private void onApplianceAdded(Appliance appliance, boolean transferred) {
        appliances.put(appliance.getPos(), appliance);
        if (!transferred) {
            appliance.onGridConnected();
        }
    }

    private void onVertexRemoved(GridVertex vertex) {
        onVertexRemoved(vertex.pos(), vertex.appliance() != null ? vertex.appliance().getPos() : null);
    }

    private void onVertexRemoved(BlockPos pos, @Nullable BlockPos appliancePos) {
        gridsManager.getGridLookup().remove(pos);

        if (appliancePos != null) {
            onApplianceRemoved(appliancePos);
        } else {
            for (var direction : Direction.values()) {
                if (onApplianceRemoved(pos.offset(direction))) {
                    break;
                }
            }
        }
    }

    private boolean onApplianceRemoved(BlockPos appliancePos) {
        if (appliances.containsKey(appliancePos)) {
            var appliance = appliances.remove(appliancePos);
            if (appliance != null) {
                appliance.onGridDisconnected();
            }
            var applianceChunkPos = new ChunkPos(appliancePos);
            if (trackedChunks.containsKey(applianceChunkPos)) {
                var trackedPositionsInChunk = trackedChunks.get(applianceChunkPos);
                trackedPositionsInChunk.remove(appliancePos);
                if (trackedPositionsInChunk.isEmpty()) {
                    trackedChunks.remove(applianceChunkPos);
                }
            }
            return true;
        }
        return false;
    }

    private void onWireRemoved(GridEdge edge) {
        var serverWire = edge.toServerWire();
        gridsManager.onWireRemoved(serverWire);
        serverWire.drop(world, wireType);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean tracksChunk(ChunkPos chunkPos) {
        return trackedChunks.containsKey(chunkPos);
    }

    public void onChunkLoaded(ChunkPos chunkPos) {
        unloadedChunks.remove(chunkPos);
        refreshApplianceReferences(chunkPos);
        if (frozen && unloadedChunks.isEmpty()) {
            frozen = false;
            appliances.values().forEach(Appliance::onGridUnfrozen);
        }
    }

    private void refreshApplianceReferences(ChunkPos chunkPos) {
        for (var pos : trackedChunks.get(chunkPos)) {
            graph.vertexSet().stream().filter(it -> it.pos().equals(pos)).findAny().ifPresent(oldVertex -> {
                var appliance = PowerSystem.APPLIANCE.find(world, pos, null);
                VivatechUtil.replaceVertex(graph, oldVertex, new GridVertex(pos, appliance));
                onApplianceAdded(appliance, false);
            });
        }
    }

    public void onChunkUnloaded(ChunkPos chunkPos) {
        unloadedChunks.add(chunkPos);
        if (!frozen) {
            frozen = true;
            appliances.values().forEach(Appliance::onGridFrozen);
        }
    }

    public void tick() {
        if (frozen) {
            return;
        }

        float generation = 0;
        float consumption = 0;
        for (var appliance : appliances.values()) {
            float current = appliance.getElectricalCurrent();
            if (current > 0) {
                generation += current;
            } else {
                consumption -= current;
            }
        }

        float voltage = 0;
        if (consumption != 0 && generation != 0) {
            var balance = generation / consumption;
            voltage = wireType.voltage();
            if (balance < 1) {
                voltage *= MathHelper.sqrt(balance);
            } else {
                voltage *= 0.5f + balance / 2f;
            }
        }

        var current = Math.max(generation, consumption);
        if (current >= wireType.maxCurrent()) {
            overcurrentTicks += 1;
        } else if (overcurrentTicks > 0) {
            overcurrentTicks -= 1;
        }

        if (overcurrentTicks > wireType.overcurrentToleranceTime()) {
            overcurrentTicks = 0;
            onOvercurrent();
        }

        for (var appliance : appliances.values()) {
            appliance.gridTick(voltage);
        }

        lastVoltage = voltage;
        lastCurrent = current;
    }

    private void onOvercurrent() {
        var inspector = new BetweennessCentrality<>(graph);
        var mostCentralVertex = inspector.getScores().entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue));
        mostCentralVertex.map(Map.Entry::getKey).ifPresent(vertex -> {
            var edges = graph.edgesOf(vertex);
            int randomEntry = world.getRandom().nextInt(edges.size());
            int currentEntry = 0;
            for (var edge : edges) {
                if (currentEntry == randomEntry) {
                    burn(edge);
                    break;
                }
                currentEntry++;
            }
        });
    }

    private void burn(GridEdge edge) {
        disconnect(edge);
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            spreadFire(edge.u());
            spreadFire(edge.v());
        }
    }

    private void spreadFire(BlockPos pos) {
        int ignitedBlocks = 0;
        int blocksToIgnite = world.getRandom().nextBetween(1, 4);
        for (int j = 0; j < 10 && ignitedBlocks < blocksToIgnite; j++) {
            var nearbyPos = pos.add(world.getRandom().nextBetween(-2, 2), world.getRandom().nextBetween(-2, 2), world.getRandom().nextBetween(-2, 2));
            if (!world.canSetBlock(nearbyPos)) {
                continue;
            }

            if (world.isAir(nearbyPos)) {
                ignitedBlocks += 1;
                world.setBlockState(nearbyPos, AbstractFireBlock.getState(world, nearbyPos));
            }
        }
    }

    public float getLastVoltage() {
        return lastVoltage;
    }

    public float getLastCurrent() {
        return lastCurrent;
    }
}
