/*
 * (C) Copyright 2020-2023, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package falseresync.lib.graph;

import com.google.common.graph.Network;

import java.util.*;

/**
 * Edge betweenness centrality.
 *
 * <p>
 * A natural extension of betweenness to edges by counting the total shortest paths that pass
 * through an edge. See the paper: Ulrik Brandes: On Variants of Shortest-Path Betweenness
 * Centrality and their Generic Computation. Social Networks 30(2):136-145, 2008, for a nice
 * discussion of different variants of betweenness centrality. Note that this implementation does
 * not work for graphs which have multiple edges. Self-loops do not influence the result and are
 * thus ignored.
 *
 * <p>
 * This implementation allows the user to compute centrality contributions only from a subset of the
 * graph vertices, i.e. to start the shortest path computations only from a subset of the vertices. This
 * allows centrality approximations in big graphs. Note that in this case, the user is responsible
 * for any normalization necessary due to duplicate shortest paths that might occur in undirected
 * graphs.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 * @author Dimitrios Michail
 * @implNote Adapted to use without the rest of the jGraphT library
 */
public class EdgeBetweennessCentrality<V, E> {
    private final Network<V, E> graph;
    private final Iterable<V> startVertices;
    private final boolean divideByTwo;
    private final OverflowStrategy overflowStrategy;
    private Map<E, Double> scores;

    /**
     * Construct a new instance.
     *
     * @param graph the input graph
     */
    public EdgeBetweennessCentrality(Network<V, E> graph) {
        this(graph, OverflowStrategy.IGNORE_OVERFLOW, null);
    }

    /**
     * Construct a new instance.
     *
     * @param graph            the input graph
     * @param overflowStrategy strategy to use if overflow is detected
     */
    public EdgeBetweennessCentrality(Network<V, E> graph, OverflowStrategy overflowStrategy) {
        this(graph, overflowStrategy, null);
    }

    /**
     * Construct a new instance.
     *
     * @param graph            the input graph
     * @param overflowStrategy strategy to use if overflow is detected
     * @param startVertices    vertices from which to start the shortest path computations. This parameter
     *                         allows the user to compute edge centrality contributions only from a subset of the
     *                         vertices of the graph. If null the whole graph vertex set is used.
     */
    public EdgeBetweennessCentrality(
            Network<V, E> graph, OverflowStrategy overflowStrategy, Iterable<V> startVertices) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (GraphUtil.hasMultipleEdges(graph)) {
            throw new IllegalArgumentException("Graphs with multiple edges not supported");
        }
        this.scores = null;
        this.overflowStrategy = overflowStrategy;
        if (startVertices == null) {
            this.startVertices = graph.nodes();
            // divide by two only if all pairs are used
            this.divideByTwo = graph.isDirected();
        } else {
            this.startVertices = startVertices;
            // the user is responsible for duplicate shortest paths
            this.divideByTwo = false;
        }
    }

    public Map<E, Double> getScores() {
        if (scores == null) {
            scores = new Algorithm().getScores();
        }
        return Collections.unmodifiableMap(scores);
    }

    public Double getEdgeScore(E e) {
        if (!graph.edges().contains(e)) {
            throw new IllegalArgumentException("Cannot return score of unknown edge");
        }
        if (scores == null) {
            scores = new Algorithm().getScores();
        }
        return scores.get(e);
    }

    /**
     * Strategy followed when counting paths.
     */
    public enum OverflowStrategy {
        /**
         * Do not check for overflow in counters. This means that on certain instances the results
         * might be wrong due to counters being too large to fit in a long.
         */
        IGNORE_OVERFLOW,
        /**
         * An exception is thrown if an overflow in counters is detected.
         */
        THROW_EXCEPTION_ON_OVERFLOW,
    }

    /*
     * The basic algorithm
     */
    private class Algorithm {
        protected Map<E, Double> scores = new HashMap<>();
        protected Deque<V> stack = new ArrayDeque<>();

        public Map<E, Double> getScores() {
            for (E e : graph.edges()) {
                scores.put(e, 0d);
            }
            for (V v : startVertices) {
                singleVertexUpdate(v);
            }
            if (divideByTwo) {
                scores.forEach((e, score) -> scores.put(e, score / 2d));
            }
            return scores;
        }

        protected void singleVertexUpdate(V source) {
            // initialization
            Map<V, List<E>> predecessors = new HashMap<>();
            Map<V, Double> distances = new HashMap<>();
            Map<V, Long> shortestPaths = new HashMap<>();
            Deque<V> queue = new ArrayDeque<>();

            for (V v : graph.nodes()) {
                shortestPaths.put(v, 0L);
            }
            shortestPaths.put(source, 1L);
            distances.put(source, 0d);
            queue.add(source);

            // main loop
            while (!queue.isEmpty()) {
                V v = queue.remove();
                stack.push(v);
                double vDistance = distances.get(v);

                for (E e : graph.outEdges(v)) {
                    V w = GraphUtil.getOppositeVertex(graph, e, v);

                    if (w.equals(v)) {
                        // ignore self-loops
                        continue;
                    }

                    // path discovery
                    if (!distances.containsKey(w)) {
                        distances.put(w, vDistance + 1d);
                        queue.add(w);
                    }

                    // path counting
                    double wDistance = distances.get(w);
                    if (Double.compare(wDistance, vDistance + 1d) == 0) {
                        long wCounter = shortestPaths.get(w);
                        long vCounter = shortestPaths.get(v);
                        long sum = wCounter + vCounter;
                        if (overflowStrategy.equals(OverflowStrategy.THROW_EXCEPTION_ON_OVERFLOW) && sum < 0) {
                            throw new ArithmeticException("long overflow");
                        }
                        shortestPaths.put(w, sum);
                        predecessors.computeIfAbsent(w, k -> new ArrayList<>()).add(e);
                    }
                }
            }

            // accumulation
            accumulate(predecessors, shortestPaths);
        }

        protected void accumulate(Map<V, List<E>> predecessors, Map<V, Long> shortestPaths) {
            Map<V, Double> delta = new HashMap<>();
            for (V v : graph.nodes()) {
                delta.put(v, 0d);
            }
            while (!stack.isEmpty()) {
                V w = stack.pop();
                List<E> wPredecessors = predecessors.get(w);
                if (wPredecessors != null) {
                    for (E e : wPredecessors) {
                        V v = GraphUtil.getOppositeVertex(graph, e, w);
                        double c = (shortestPaths.get(v).doubleValue() / shortestPaths.get(w).doubleValue()) * (1 + delta.get(w));
                        scores.put(e, scores.get(e) + c);
                        delta.put(v, delta.get(v) + c);
                    }
                }
            }
        }

    }
}
