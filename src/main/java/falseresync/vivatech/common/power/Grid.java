package falseresync.vivatech.common.power;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.function.Supplier;

public class Grid {
    private final SimpleGraph<GridVertex, GridEdge> graph;
    private final Map<BlockPos, BlockApiCache<GridVertex, Void>> vertexCaches;
    private final Map<BlockPos, Appliance> appliances;
    private final GridsManager gridsManager;
    private final ServerWorld world;

    public Grid(GridsManager gridsManager, ServerWorld world) {
        this.gridsManager = gridsManager;
        this.world = world;
        this.graph = new SimpleGraph<>(GridEdge.class);
        this.vertexCaches = new Object2ObjectRBTreeMap<>(Comparator.comparingLong(BlockPos::asLong));
        this.appliances = new Object2ReferenceRBTreeMap<>(Comparator.comparingLong(BlockPos::asLong));
    }

    public Grid(GridsManager gridsManager, ServerWorld world, Set<GridEdge> edges) {
        this(gridsManager, world);
        edges.forEach(this::connect);
    }

    public GridSnapshot createSnapshot() {
        return new GridSnapshot(graph.edgeSet());
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
            vertexCaches.remove(pos);
            return false;
        }

        var vertex = cache.find(state, null);
        if (vertex == null) {
            vertexCaches.remove(pos);
            gridsManager.getGridLookup().remove(pos);
            return false;
        }

        for (var edge : graph.edgesOf(vertex)) {
            gridsManager.onWireRemoved(edge.toServerWire());
        }
        if (!graph.removeVertex(vertex)) {
            return false;
        }

        partition();
        return true;
    }

    public boolean disconnect(GridVertex vertexU, GridVertex vertexV) {
        var edge = new GridEdge(vertexU.pos(), vertexV.pos());
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
            if (isolatedGraph.vertexSet().size() == 1) {
                isolatedGraph.vertexSet().forEach(this::onVertexRemoved);
            } else {
                var other = gridsManager.create();
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
        gridsManager.getGridLookup().remove(vertex.pos());
        if (vertex.appliance() != null) {
            vertex.appliance().onGridDisconnected();
        }
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
            voltage = 230 * generation / consumption;
        }

        for (var appliance : appliances.values()) {
            appliance.gridTick(voltage);
        }
    }
}
