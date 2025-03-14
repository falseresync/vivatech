package falseresync.vivatech.common.power;

import falseresync.vivatech.common.VivatechUtil;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceObjectPair;
import net.minecraft.block.AbstractFireBlock;
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
    }

    public Grid(GridsManager gridsManager, ServerWorld world, WireType wireType, Set<GridEdge> edges) {
        this(gridsManager, world, wireType);
        edges.forEach(this::connect);
    }

    public GridSnapshot createSnapshot() {
        return new GridSnapshot(wireType, Set.copyOf(graph.edgeSet()));
    }

    public WireType getWireType() {
        return wireType;
    }

    public boolean isEmpty() {
        return graph.vertexSet().isEmpty();
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
            onVertexAdded(vertex, true);
        } else if (otherGrid != this) {
            merge(otherGrid.graph);
            gridsManager.getGrids().remove(otherGrid);
        }
    }

    private void merge(Graph<GridVertex, GridEdge> otherGraph) {
        Graphs.addGraph(this.graph, otherGraph);
        for (var vertex : otherGraph.vertexSet()) {
            onVertexAdded(vertex, false);
        }
    }

    public boolean remove(BlockPos pos) {
        var vertex = findVertex(pos);
        if (vertex == null) {
            onVertexRemoved(pos, null, true);
            return false;
        }

        var edges = Set.copyOf(graph.edgesOf(vertex));
        if (!graph.removeVertex(vertex)) {
            return false;
        }
        for (GridEdge edge : edges) {
            onWireRemoved(edge);
        }

        onVertexRemoved(vertex, true);
        partition();
        return true;
    }

    @Nullable
    private GridVertex findVertex(BlockPos pos) {
        for (GridVertex vertex : graph.vertexSet()) {
            if (vertex.pos().equals(pos)) {
                return vertex;
            }
        }
        return null;
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
                isolatedGraph.vertexSet().forEach(vertex -> onVertexRemoved(vertex, false));
            } else {
                var other = gridsManager.create(wireType);
                other.merge(isolatedGraph);
            }
        }

        gridsManager.getGrids().remove(this);
    }

    private void onVertexAdded(GridVertex vertex, boolean shouldInitialize) {
        gridsManager.getGridLookup().put(vertex.pos(), this);
        if (vertex.appliance() != null) {
            if (!appliances.containsValue(vertex.appliance())) {
                onApplianceAdded(vertex.appliance(), shouldInitialize);
                var appliancePos = vertex.appliance().getAppliancePos();
                trackedChunks.computeIfAbsent(new ChunkPos(appliancePos), key -> new ObjectOpenHashSet<>()).add(appliancePos);
            }
        }
    }

    private void onApplianceAdded(Appliance appliance, boolean shouldInitialize) {
        appliances.put(appliance.getAppliancePos(), appliance);
        if (shouldInitialize) {
            appliance.onGridConnected();
        }
    }

    private void onVertexRemoved(GridVertex vertex, boolean removeGridIfEmpty) {
        onVertexRemoved(vertex.pos(), vertex.appliance() != null ? vertex.appliance().getAppliancePos() : null, removeGridIfEmpty);
    }

    private void onVertexRemoved(BlockPos pos, @Nullable BlockPos appliancePos, boolean removeGridIfEmpty) {
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

        if (removeGridIfEmpty) {
            if (graph.vertexSet().size() <= 1) {
                graph.vertexSet().forEach(vertex -> onVertexRemoved(vertex, false));
                gridsManager.getGrids().remove(this);
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

    private void pollChunks() {
        var becameFrozen = false;
        for (ChunkPos chunkPos : trackedChunks.keySet()) {
            if (!world.shouldTickBlocksInChunk(chunkPos.toLong())) {
                becameFrozen = true;
                break;
            }
        }

        if (becameFrozen) {
            if (!frozen) {
                for (Appliance appliance : appliances.values()) {
                    appliance.onGridFrozen();
                }
            }
            frozen = true;
            return;
        }

        if (frozen) {
            frozen = false;
            refreshApplianceReferences();
            for (Appliance appliance : appliances.values()) {
                appliance.onGridUnfrozen();
            }
        }
    }

    private void refreshApplianceReferences() {
        // Because CME
        var appliancesToUpdate = new ObjectOpenHashSet<ReferenceObjectPair<Appliance, GridVertex>>();
        for (GridVertex oldVertex : graph.vertexSet()) {
            if (oldVertex.appliance() != null) {
                var appliance = PowerSystem.APPLIANCE.find(world, oldVertex.appliance().getAppliancePos(), null);
                appliancesToUpdate.add(ReferenceObjectPair.of(appliance, oldVertex));
            }
        }
        for (var pair : appliancesToUpdate) {
            var oldVertex = pair.right();
            var appliance = pair.left();
            VivatechUtil.replaceVertexIgnoringUndirectedEdgeEquality(graph, oldVertex, new GridVertex(oldVertex.pos(), appliance));
            onApplianceAdded(appliance, true);
        }
    }

    public void tick() {
        pollChunks();
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
