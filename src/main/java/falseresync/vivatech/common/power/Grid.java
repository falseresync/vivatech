package falseresync.vivatech.common.power;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.function.Supplier;

public class Grid {
    private final SimpleGraph<GridVertex, GridEdge> graph;
    private final Map<BlockPos, BlockApiCache<GridVertex, Void>> vertexCaches;
    private final Map<BlockPos, Appliance> appliances;
    private final GridsManager gridsManager;
    private final ServerWorld world;
    private final WireType wireType;
    private int overcurrentTicks = 0;
    private float lastVoltage = 0;
    private float lastCurrent = 0;

    public Grid(GridsManager gridsManager, ServerWorld world, WireType wireType) {
        this.gridsManager = gridsManager;
        this.world = world;
        this.wireType = wireType;
        this.graph = new SimpleGraph<>(GridEdge.class);
        this.vertexCaches = new Object2ObjectRBTreeMap<>(Comparator.comparingLong(BlockPos::asLong));
        this.appliances = new Object2ReferenceRBTreeMap<>(Comparator.comparingLong(BlockPos::asLong));
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
        var cache = vertexCaches.get(pos);
        if (cache == null) {
            clearVertexAssociatedCollections(pos);
            return false;
        }

        var vertex = cache.find(state, null);
        if (vertex == null) {
            clearVertexAssociatedCollections(pos);
            return false;
        }

        for (var edge : graph.edgesOf(vertex)) {
            gridsManager.onWireRemoved(edge.toServerWire());
        }
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

        gridsManager.onWireRemoved(edge.toServerWire());
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
            }
        }

        gridsManager.getGrids().remove(this);
    }

    private void onVertexAdded(GridVertex vertex, boolean isTransferred) {
        gridsManager.getGridLookup().put(vertex.pos(), this);
        vertexCaches.put(vertex.pos(), BlockApiCache.create(PowerSystem.GRID_VERTEX, world, vertex.pos()));
        if (vertex.appliance() != null) {
            if (!appliances.containsValue(vertex.appliance())) {
                appliances.put(vertex.pos(), vertex.appliance());
                if (!isTransferred) {
                    vertex.appliance().onGridConnected();
                }
            }
        }
    }

    private void onVertexRemoved(GridVertex vertex) {
        clearVertexAssociatedCollections(vertex.pos());
        if (vertex.appliance() != null) {
            vertex.appliance().onGridDisconnected();
        }
    }

    private void clearVertexAssociatedCollections(BlockPos pos) {
        gridsManager.getGridLookup().remove(pos);
        vertexCaches.remove(pos);
        appliances.remove(pos);
    }

    public void tick() {
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
