package falseresync.vivatech.common.power;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.Traverser;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Grid {
    private final UUID uuid = UUID.randomUUID();
    private final MutableNetwork<GridNode, GridEdge> graph;
    private final GridsManager gridsManager;
    private final World world;

    public Grid(GridsManager gridsManager, World world) {
        this.gridsManager = gridsManager;
        this.world = world;
        this.graph = NetworkBuilder
                .undirected()
                .allowsParallelEdges(false)
                .allowsSelfLoops(false)
                .build();
    }

    public Grid(GridsManager gridsManager, World world, Set<GridEdge> edges) {
        this(gridsManager, world);
        edges.forEach(this::add);
    }

    public GridSnapshot createSnapshot() {
        return new GridSnapshot(graph.edges());
    }

    public boolean add(BlockPos from, BlockPos to) {
        return add(new GridEdge(from, to));
    }

    public boolean add(GridEdge edge) {
        var applianceU = PowerSystem.APPLIANCE.find(world, edge.u(), null);
        var applianceV = PowerSystem.APPLIANCE.find(world, edge.v(), null);

        if (applianceU != null && applianceV != null) {
            var wasModified = graph.addEdge(applianceU.asGridNode(), applianceV.asGridNode(), edge);
            if (wasModified) {
                gridsManager.onWireAdded(edge.toServerWire());
                initOrMerge(applianceU);
                initOrMerge(applianceV);
            }
            return wasModified;
        }

        return false;
    }

    private void initOrMerge(Appliance appliance) {
        var other = gridsManager.getGridLookup().get(appliance.getGridUuid());
        if (other != null) {
            if (!other.equals(this)) {
                merge(other);
            }
        } else {
            appliance.onGridConnected();
            updateGridLookup(appliance, this);
        }
    }

    private void merge(Grid other) {
        other.graph.nodes().forEach(node -> {
            graph.addNode(node);
            updateGridLookup(node.appliance(), this);
        });
        other.graph.edges().forEach(edge -> {
            var nodes = other.graph.incidentNodes(edge);
            graph.addEdge(nodes.nodeU(), nodes.nodeV(), edge);
        });
        gridsManager.getGrids().remove(other);
    }

    public void cut(BlockPos from, BlockPos to) {
        cut(new GridEdge(from, to));
    }

    public void cut(GridEdge edge) {
        var nodes = incidentNodesIgnoreDirection(edge);
        if (nodes == null) {
            return;
        }
        if (!graph.removeEdge(edge)) {
            return;
        }
        gridsManager.onWireRemoved(edge.toServerWire());
        // I assume that between two disconnected nodes in an electrical grid a bypass path will be nearby
        // hence breadFirst search. But this is merely an assumption
        for (var reference : Traverser.forGraph(graph).breadthFirst(nodes.nodeU())) {
            if (reference.equals(nodes.nodeV())) {
                // There's a bypass route, don't do anything
                return;
            }
        }

        // Couldn't find a bypass - must create a new Power systems
        // Here we don't really care if it's breadth or depth
        partition(nodes.nodeU());
        partition(nodes.nodeV());

        gridsManager.getGrids().remove(this);
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
                pair.left().forEach(node -> updateGridLookup(node.appliance(), other));
            });
        } else {
            startingNode.appliance().onGridDisconnected();
            gridsManager.getGridLookup().remove(startingNode.uuid());
            findProxied(startingNode.appliance()).ifPresent(proxied -> gridsManager.getGridLookup().remove(proxied.getGridUuid()));
        }
    }

    private void updateGridLookup(Appliance appliance, Grid grid) {
        gridsManager.getGridLookup().put(appliance.getGridUuid(), grid);
        findProxied(appliance).ifPresent(proxied -> gridsManager.getGridLookup().put(proxied.getGridUuid(), grid));
    }

    private Optional<Appliance> findProxied(Appliance appliance) {
        if (appliance instanceof ApplianceProxy proxy) {
            return Optional.ofNullable(proxy.getProxiedAppliance());
        }
        return Optional.empty();
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
        for (var node : graph.nodes()) {
            node.appliance().gridTick(0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grid that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
