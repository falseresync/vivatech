package falseresync.vivatech.common.power.grid;

import falseresync.vivatech.common.VivatechUtil;
import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.WorldPowerSystem;
import falseresync.vivatech.common.power.grid.Appliance;
import falseresync.vivatech.common.power.grid.GridAwareAppliance;
import falseresync.vivatech.common.power.grid.GridEdge;
import falseresync.vivatech.common.power.grid.GridSnapshot;
import falseresync.vivatech.common.power.grid.GridVertex;
import falseresync.vivatech.common.power.wire.Wire;
import falseresync.vivatech.common.power.wire.WireType;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceObjectPair;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.BaseFireBlock;

public class Grid {
    private final SimpleGraph<falseresync.vivatech.common.power.grid.GridVertex, falseresync.vivatech.common.power.grid.GridEdge> graph;
    private final Map<BlockPos, falseresync.vivatech.common.power.grid.Appliance> appliances;
    private final Map<ChunkPos, Set<BlockPos>> trackedChunks;
    private final WorldPowerSystem worldPowerSystem;
    private final ServerLevel world;
    private final WireType wireType;
    private int overcurrentTicks = 0;
    private float lastVoltage = 0;
    private float lastCurrent = 0;
    private boolean frozen = false;

    public Grid(WorldPowerSystem worldPowerSystem, ServerLevel world, WireType wireType) {
        this.worldPowerSystem = worldPowerSystem;
        this.world = world;
        this.wireType = wireType;
        graph = new SimpleGraph<>(falseresync.vivatech.common.power.grid.GridEdge.class);
        appliances = new Object2ReferenceRBTreeMap<>();
        trackedChunks = PowerSystem.createChunkPosKeyedMap();
    }

    public Grid(WorldPowerSystem worldPowerSystem, ServerLevel world, WireType wireType, Set<falseresync.vivatech.common.power.grid.GridEdge> edges) {
        this(worldPowerSystem, world, wireType);
        edges.forEach(this::connect);
    }

    public falseresync.vivatech.common.power.grid.GridSnapshot createSnapshot() {
        return new GridSnapshot(wireType, Set.copyOf(graph.edgeSet()));
    }

    public WireType getWireType() {
        return wireType;
    }

    public boolean isEmpty() {
        return graph.vertexSet().isEmpty();
    }

    public boolean connect(falseresync.vivatech.common.power.grid.GridEdge edge) {
        return connect(edge, false);
    }

    public boolean connect(falseresync.vivatech.common.power.grid.GridEdge edge, boolean noWire) {
        var vertexU = PowerSystem.GRID_VERTEX.find(world, edge.u(), null);
        var vertexV = PowerSystem.GRID_VERTEX.find(world, edge.v(), null);
        return connect(vertexU, vertexV, () -> edge, noWire);
    }

    public boolean connect(falseresync.vivatech.common.power.grid.GridVertex vertexU, falseresync.vivatech.common.power.grid.GridVertex vertexV) {
        return connect(vertexU, vertexV, () -> new falseresync.vivatech.common.power.grid.GridEdge(vertexU.pos(), vertexV.pos()), false);
    }

    public boolean connect(falseresync.vivatech.common.power.grid.GridVertex vertexU, falseresync.vivatech.common.power.grid.GridVertex vertexV, Supplier<falseresync.vivatech.common.power.grid.GridEdge> edgeSupplier, boolean noWire) {
        if (frozen) {
            return false;
        }
        if (vertexU != null && vertexV != null) {
            initOrMerge(vertexU);
            initOrMerge(vertexV);
            var edge = edgeSupplier.get();
            var wasModified = graph.addEdge(vertexU, vertexV, edge);
            if (wasModified && !noWire) {
                worldPowerSystem.addWire(edge.asWire(wireType, 0));
            }
            return wasModified;
        }
        return false;
    }

    private void initOrMerge(falseresync.vivatech.common.power.grid.GridVertex vertex) {
        var otherGrid = worldPowerSystem.getGridLookup().get(vertex.pos());
        if (otherGrid == null) {
            graph.addVertex(vertex);
            onVertexAdded(vertex, true);
        } else if (otherGrid != this) {
            merge(otherGrid.graph);
            worldPowerSystem.remove(otherGrid);
        }
    }

    private void merge(Graph<falseresync.vivatech.common.power.grid.GridVertex, falseresync.vivatech.common.power.grid.GridEdge> otherGraph) {
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
        for (falseresync.vivatech.common.power.grid.GridEdge edge : edges) {
            removeWire(edge, Wire.DropRule.FULL);
        }

        onVertexRemoved(vertex, true);
        partition();
        return true;
    }

    @Nullable
    private falseresync.vivatech.common.power.grid.GridVertex findVertex(BlockPos pos) {
        for (falseresync.vivatech.common.power.grid.GridVertex vertex : graph.vertexSet()) {
            if (vertex.pos().equals(pos)) {
                return vertex;
            }
        }
        return null;
    }

    public boolean disconnect(falseresync.vivatech.common.power.grid.GridVertex vertexU, falseresync.vivatech.common.power.grid.GridVertex vertexV) {
        return disconnect(new falseresync.vivatech.common.power.grid.GridEdge(vertexU.pos(), vertexV.pos()), Wire.DropRule.FULL);
    }

    public boolean disconnect(falseresync.vivatech.common.power.grid.GridEdge edge, Wire.DropRule dropRule) {
        if (!graph.removeEdge(edge)) {
            return false;
        }

        removeWire(edge, dropRule);
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
                var other = worldPowerSystem.create(wireType);
                other.merge(isolatedGraph);
            }
        }

        worldPowerSystem.remove(this);
    }

    private void onVertexAdded(falseresync.vivatech.common.power.grid.GridVertex vertex, boolean shouldInitialize) {
        worldPowerSystem.getGridLookup().put(vertex.pos(), this);
        if (vertex.appliance() != null) {
            if (!appliances.containsValue(vertex.appliance())) {
                addAppliance(vertex.appliance(), shouldInitialize);
            }
        }
    }

    private void addAppliance(falseresync.vivatech.common.power.grid.Appliance appliance, boolean shouldInitialize) {
        var pos = appliance.getAppliancePos();
        if (appliances.get(pos) == appliance) {
            throw new IllegalStateException("Cannot cache the same appliance twice");
        }

        appliances.put(pos, appliance);
        trackedChunks.computeIfAbsent(new ChunkPos(pos), key -> new ObjectOpenHashSet<>()).add(pos);
        if (shouldInitialize) {
            appliance.onGridConnected();
        }
    }

    private void onVertexRemoved(falseresync.vivatech.common.power.grid.GridVertex vertex, boolean removeGridIfEmpty) {
        onVertexRemoved(vertex.pos(), vertex.appliance() != null ? vertex.appliance().getAppliancePos() : null, removeGridIfEmpty);
    }

    private void onVertexRemoved(BlockPos pos, @Nullable BlockPos appliancePos, boolean removeGridIfEmpty) {
        worldPowerSystem.getGridLookup().remove(pos);

        if (appliancePos != null) {
            removeAppliance(appliancePos);
        } else {
            for (var direction : Direction.values()) {
                if (removeAppliance(pos.relative(direction))) {
                    break;
                }
            }
        }

        if (removeGridIfEmpty) {
            if (graph.vertexSet().size() <= 1) {
                graph.vertexSet().forEach(vertex -> onVertexRemoved(vertex, false));
                worldPowerSystem.remove(this);
            }
        }
    }

    private boolean removeAppliance(BlockPos appliancePos) {
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

    private void removeWire(falseresync.vivatech.common.power.grid.GridEdge edge, Wire.DropRule dropRule) {
        var serverWire = edge.asWire(wireType, 0);
        worldPowerSystem.removeWire(serverWire);
        serverWire.drop(world, wireType, dropRule);
    }

    private void pollChunks() {
        var becameFrozen = false;
        for (ChunkPos chunkPos : trackedChunks.keySet()) {
            if (!world.shouldTickBlocksAt(chunkPos.toLong())) {
                becameFrozen = true;
                break;
            }
        }

        if (becameFrozen) {
            if (!frozen) {
                for (falseresync.vivatech.common.power.grid.Appliance appliance : appliances.values()) {
                    appliance.onGridFrozen();
                }
            }
            frozen = true;
            return;
        }

        if (frozen) {
            frozen = false;
            refreshApplianceReferences();
            for (falseresync.vivatech.common.power.grid.Appliance appliance : appliances.values()) {
                appliance.onGridUnfrozen();
            }
        }
    }

    private void refreshApplianceReferences() {
        // Because CME
        var appliancesToUpdate = new ObjectOpenHashSet<ReferenceObjectPair<Appliance, falseresync.vivatech.common.power.grid.GridVertex>>();
        for (falseresync.vivatech.common.power.grid.GridVertex oldVertex : graph.vertexSet()) {
            if (oldVertex.appliance() != null) {
                var appliance = PowerSystem.APPLIANCE.find(world, oldVertex.appliance().getAppliancePos(), oldVertex.direction());
                if (oldVertex.appliance() != appliance) {
                    appliancesToUpdate.add(ReferenceObjectPair.of(appliance, oldVertex));
                }
            }
        }
        for (var pair : appliancesToUpdate) {
            var oldVertex = pair.right();
            var appliance = pair.left();
            VivatechUtil.replaceVertexIgnoringUndirectedEdgeEquality(graph, oldVertex, new GridVertex(oldVertex.pos(), oldVertex.direction(), appliance));
            addAppliance(appliance, true);
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
                voltage *= Mth.sqrt(balance);
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
            if (appliance instanceof GridAwareAppliance gridAware) {
                gridAware.gridAwareTick(this, voltage);
            } else {
                appliance.gridTick(voltage);
            }
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
        disconnect(edge, Wire.DropRule.PARTIAL);
        if (world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            spreadFire(edge.u());
            spreadFire(edge.v());
        }
    }

    private void spreadFire(BlockPos pos) {
        int ignitedBlocks = 0;
        int blocksToIgnite = world.getRandom().nextIntBetweenInclusive(1, 4);
        for (int j = 0; j < 10 && ignitedBlocks < blocksToIgnite; j++) {
            var nearbyPos = pos.offset(world.getRandom().nextIntBetweenInclusive(-2, 2), world.getRandom().nextIntBetweenInclusive(-2, 2), world.getRandom().nextIntBetweenInclusive(-2, 2));
            if (!world.isLoaded(nearbyPos)) {
                continue;
            }

            if (world.isEmptyBlock(nearbyPos)) {
                ignitedBlocks += 1;
                world.setBlockAndUpdate(nearbyPos, BaseFireBlock.getState(world, nearbyPos));
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
