package falseresync.lib.graph;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Network;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GraphUtil {
    /**
     * Check if a graph has multiple edges (parallel edges), that is, whether the graph contains two
     * or more edges connecting the same pair of vertices.
     *
     * @param graph a graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return true if a graph has multiple edges, false otherwise
     *
     * @throws NullPointerException if graph is {@code null}
     */
    public static <V, E> boolean hasMultipleEdges(Network<V, E> graph)
    {
        Objects.requireNonNull(graph, "Graph cannot be null");

        if (!graph.allowsParallelEdges()) {
            return false;
        }

        // no luck, we have to check
        for (V v : graph.nodes()) {
            Set<V> neighbors = new HashSet<>();
            for (E e : graph.outEdges(v)) {
                V u = getOppositeVertex(graph, e, v);
                if (!neighbors.add(u)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Gets the vertex opposite another vertex across an edge.
     *
     * @param g graph containing e and v
     * @param e edge in g
     * @param v vertex in g
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return vertex opposite to v across e
     */
    public static <V, E> V getOppositeVertex(Network<V, E> g, E e, V v)
    {
        EndpointPair<V> incidentNodes = g.incidentNodes(e);
        V source = incidentNodes.source();
        V target = incidentNodes.target();
        if (v.equals(source)) {
            return target;
        } else if (v.equals(target)) {
            return source;
        } else {
            throw new IllegalArgumentException("no such vertex: " + v.toString());
        }
    }
}
