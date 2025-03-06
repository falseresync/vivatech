package falseresync.vivatech.common.power;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.Traverser;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Grid {
    private final UUID uuid = UUID.randomUUID();
    private final MutableNetwork<GridNode, GridEdge> graph;
    private final Map<BlockPos, BlockApiCache<GridNode, Void>> nodeLookupsCache;
    private final Map<BlockPos, Appliance> appliances;
    private final GridsManager gridsManager;
    private final ServerWorld world;
    private float voltage = 0;

    public Grid(GridsManager gridsManager, ServerWorld world) {
        this.gridsManager = gridsManager;
        this.world = world;
        this.graph = NetworkBuilder
                .undirected()
                .allowsParallelEdges(false)
                .allowsSelfLoops(false)
                .build();
        this.nodeLookupsCache = new Object2ObjectRBTreeMap<>(Comparator.comparingLong(BlockPos::asLong));
        this.appliances = new Object2ObjectRBTreeMap<>(Comparator.comparingLong(BlockPos::asLong));
    }

    public Grid(GridsManager gridsManager, ServerWorld world, Set<GridEdge> edges) {
        this(gridsManager, world);
        edges.forEach(this::connect);
    }

    public GridSnapshot createSnapshot() {
        return new GridSnapshot(graph.edges());
    }

    public boolean connect(GridEdge edge) {
        var nodeU = PowerSystem.GRID_NODE.find(world, edge.u(), null);
        var nodeV = PowerSystem.GRID_NODE.find(world, edge.v(), null);
        return connect(nodeU, nodeV);
    }

    public boolean connect(GridNode nodeU, GridNode nodeV) {
        if (nodeU != null && nodeV != null) {
            var edge = new GridEdge(nodeU.pos(), nodeV.pos());
            var wasModified = graph.addEdge(nodeU, nodeV, edge);
            if (wasModified) {
                gridsManager.onWireAdded(edge.toServerWire());
                initOrMerge(nodeU);
                initOrMerge(nodeV);
            }
            return wasModified;
        }

        return false;
    }


    private void initOrMerge(GridNode node) {
        var other = gridsManager.getGridLookup().get(node.pos());
        if (other != null) {
            if (!other.equals(this)) {
                merge(other);
            }
        } else {
            if (node.appliance() != null) {
                node.appliance().onGridConnected();
            }
            onNodeAdded(node);
        }
    }

    private void merge(Grid other) {
        other.graph.nodes().forEach(node -> {
            graph.addNode(node);
            onNodeAdded(node);
            other.onNodeRemoved(node);
        });
        other.graph.edges().forEach(edge -> {
            var nodes = other.graph.incidentNodes(edge);
            graph.addEdge(nodes.nodeU(), nodes.nodeV(), edge);
        });
        gridsManager.getGrids().remove(other);
    }

    public boolean cut(BlockPos from, BlockPos to) {
        return cut(new GridEdge(from, to));
    }

    public boolean cut(GridEdge edge) {
        var nodes = incidentNodesIgnoreDirection(edge);
        if (nodes == null) {
            return false;
        }
        if (!graph.removeEdge(edge)) {
            return false;
        }
        gridsManager.onWireRemoved(edge.toServerWire());
        // I assume that between two disconnected nodes in an electrical grid a bypass path will be nearby
        // hence breadFirst search. But this is merely an assumption
        for (var reference : Traverser.forGraph(graph).breadthFirst(nodes.nodeU())) {
            if (reference.equals(nodes.nodeV())) {
                // There's a bypass route, don't do anything
                return true;
            }
        }

        // Couldn't find a bypass - must create a new Power systems
        // Here we don't really care if it's breadth or depth
        partition(nodes.nodeU());
        partition(nodes.nodeV());

        gridsManager.getGrids().remove(this);

        return true;
    }

    private void partition(GridNode startingNode) {
        var visitedEdges = new ObjectOpenHashSet<GridEdge>();
        var partitioned = new ObjectArrayList<Pair<EndpointPair<GridNode>, GridEdge>>();
        for (var node : Traverser.forGraph(graph).breadthFirst(startingNode)) {
            for (var edge : graph.incidentEdges(node)) {
                if (!visitedEdges.contains(edge)) {
                    partitioned.add(Pair.of(graph.incidentNodes(edge), edge));
                    visitedEdges.add(edge);
                }
            }
        }

        if (!partitioned.isEmpty()) {
            var other = gridsManager.create();
            partitioned.forEach(pair -> {
                other.graph.addEdge(pair.left(), pair.right());
                pair.left().forEach(node -> {
                    other.onNodeAdded(node);
                    onNodeRemoved(node);
                });
            });
        } else {
            if (startingNode.appliance() != null) {
                startingNode.appliance().onGridDisconnected();
            }
            onNodeRemoved(startingNode);
        }
    }

    public void remove(BlockPos pos, @Nullable BlockState state) {
        var node = nodeLookupsCache.get(pos).find(state, null);
        if (node != null) {
            var removableEdges = graph.incidentEdges(node).toArray(new GridEdge[] {});
            for (var edge : removableEdges) {
                cut(edge);
            }

            graph.removeNode(node);
            onNodeRemoved(node);
        } else {
            nodeLookupsCache.remove(pos);
        }
    }

    private void onNodeAdded(GridNode node) {
        gridsManager.getGridLookup().put(node.pos(), this);
        nodeLookupsCache.put(node.pos(), BlockApiCache.create(PowerSystem.GRID_NODE, world, node.pos()));
        if (node.appliance() != null) {
            appliances.put(node.pos(), node.appliance());
        }
    }

    private void onNodeRemoved(GridNode node) {
        gridsManager.getGridLookup().remove(node.pos());
        nodeLookupsCache.remove(node.pos());
        if (node.appliance() != null) {
            appliances.remove(node.pos());
        }
    }

    @Nullable
    private EndpointPair<GridNode> incidentNodesIgnoreDirection(GridEdge edge) {
        try {
            return graph.incidentNodes(edge);
        } catch (IllegalArgumentException e) {
           try {
               return graph.incidentNodes(new GridEdge(edge.u(), edge.v()));
           } catch (IllegalArgumentException _ignored) {
               return null;
           }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grid that)) return false;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
