package falseresync.vivatech.common.power;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.Traverser;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PowerSystem {
    private final UUID uuid = UUID.randomUUID();
    private final MutableNetwork<PowerNodeReference, Wire> graph;
    private final PowerSystemsManager powerSystemsManager;
    private final World world;
    private final Map<ChunkPos, Set<Wire>> unsyncedWires = PowerUtil.createWireMap();

    public PowerSystem(PowerSystemsManager powerSystemsManager, World world) {
        this.powerSystemsManager = powerSystemsManager;
        this.world = world;
        this.graph = NetworkBuilder
                .undirected()
                .allowsParallelEdges(false)
                .allowsSelfLoops(false)
                .build();
    }

    public PowerSystem(PowerSystemsManager powerSystemsManager, World world, Set<Wire> wires) {
        this(powerSystemsManager, world);
        wires.forEach(this::add);
    }

    public PowerSystemSnapshot createSnapshot() {
        return new PowerSystemSnapshot(graph.edges());
    }

    private void onWireAdded(Wire wire) {
        unsyncedWires.merge(wire.chunkPos(), ObjectOpenHashSet.of(wire), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    private void onWireRemoved(Wire wire) {
        unsyncedWires.merge(wire.chunkPos(), ObjectOpenHashSet.of(wire.withRemoved(true)), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    public Map<ChunkPos, Set<Wire>> getUnsyncedWires() {
        return unsyncedWires;
    }

    public void markSynced() {
        unsyncedWires.clear();
    }

    public boolean requiresSyncing() {
        return !unsyncedWires.isEmpty();
    }

    public boolean add(BlockPos from, BlockPos to) {
        return add(new Wire(from, to));
    }

    public boolean add(Wire wire) {
        var nodeU = PowerSystemsManager.POWER_NODE.find(world, wire.from(), null);
        var nodeV = PowerSystemsManager.POWER_NODE.find(world, wire.to(), null);

        if (nodeU != null && nodeV != null) {
            var wasModified = graph.addEdge(nodeU.asReference(), nodeV.asReference(), wire);
            if (wasModified) {
                onWireAdded(wire);
                initOrMerge(nodeU);
                initOrMerge(nodeV);
            }
            return wasModified;
        }

        return false;
    }

    private void initOrMerge(PowerNode node) {
        var other = node.getPowerSystem();
        if (other != null) {
            if (!other.equals(this)) {
                merge(other);
            }
        } else {
            node.setPowerSystem(this);
        }
    }

    private void merge(PowerSystem other) {
        other.graph.nodes().forEach(reference -> {
            graph.addNode(reference);
            reference.powerNode().setPowerSystem(this);
        });
        other.graph.edges().forEach(edge -> {
            var nodes = other.graph.incidentNodes(edge);
            graph.addEdge(nodes.nodeU(), nodes.nodeV(), edge);
        });
        powerSystemsManager.getAll(world).remove(other);
    }

    public void cut(BlockPos from, BlockPos to) {
        cut(new Wire(from, to));
    }

    public void cut(Wire wire) {
        var nodes = incidentNodesIgnoreDirection(wire);
        if (nodes == null) {
            return;
        }
        if (!graph.removeEdge(wire)) {
            return;
        }
        onWireRemoved(wire);
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
        // TODO: still doesn't work properly :p
        partition(nodes.nodeU());
        partition(nodes.nodeV());

        powerSystemsManager.getAll(world).remove(this);
    }

    private void partition(PowerNodeReference startingNode) {
        var visitedEdges = new ObjectOpenHashSet<Wire>();
        for (var node : Traverser.forGraph(graph).breadthFirst(startingNode)) {
            for (var edge : graph.incidentEdges(node)) {
                if (!visitedEdges.contains(edge)) {
                    visitedEdges.add(edge);
                }
            }
        }

        if (!visitedEdges.isEmpty()) {
            var other = powerSystemsManager.create(world);
            visitedEdges.forEach(other::add);
        }
    }

    @Nullable
    private EndpointPair<PowerNodeReference> incidentNodesIgnoreDirection(Wire wire) {
        try {
            return graph.incidentNodes(wire);
        } catch (IllegalArgumentException e) {
           try {
               return graph.incidentNodes(new Wire(wire.to(), wire.from()));
           } catch (IllegalArgumentException _ignored) {
               return null;
           }
        }
    }

    public void tick() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PowerSystem that)) return false;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
