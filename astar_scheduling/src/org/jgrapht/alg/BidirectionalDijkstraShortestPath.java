/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

/**
 * A bidirectional version of Dijkstra's algorithm.
 * 
 * <p>
 * See the Wikipedia article for details and references about
 * <a href="https://en.wikipedia.org/wiki/Bidirectional_search">bidirectional search</a>. This
 * technique does not change the worst-case behavior of the algorithm but reduces, in some cases,
 * the number of visited vertices in practice. This implementation alternatively constructs forward
 * and reverse paths from the source and target vertices respectively.
 * </p>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @see DijkstraShortestPath
 *
 * @author Dimitrios Michail
 * @since July 2016
 */
public final class BidirectionalDijkstraShortestPath<V, E>
{

    private final GraphPath<V, E> path;

    /**
     * Creates the instance and executes the bidirectional Dijkstra shortest path algorithm. An
     * instance is only good for a single search; after construction, it can be accessed to retrieve
     * information about the found path.
     *
     * @param graph the input graph
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     */
    public BidirectionalDijkstraShortestPath(Graph<V, E> graph, V startVertex, V endVertex)
    {
        this(graph, startVertex, endVertex, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates the instance and executes the bidirectional Dijkstra shortest path algorithm. An
     * instance is only good for a single search; after construction, it can be accessed to retrieve
     * information about the found path.
     *
     * @param graph the input graph
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     * @param radius limit on weighted path length, or Double.POSITIVE_INFINITY for unbounded search
     */
    public BidirectionalDijkstraShortestPath(
        Graph<V, E> graph, V startVertex, V endVertex, double radius)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Input graph cannot be null");
        }
        if (startVertex == null || !graph.containsVertex(startVertex)) {
            throw new IllegalArgumentException("Invalid graph vertex as source");
        }
        if (endVertex == null || !graph.containsVertex(endVertex)) {
            throw new IllegalArgumentException("Invalid graph vertex as target");
        }
        if (radius < 0.0) {
            throw new IllegalArgumentException("Radius must be non-negative");
        }

        this.path = new AlgorithmDetails(graph, startVertex, endVertex, radius).run();
    }

    /**
     * Return the edges making up the path.
     *
     * @return List of edges, or null if no path exists
     */
    public List<E> getPathEdgeList()
    {
        if (path == null) {
            return null;
        } else {
            return path.getEdgeList();
        }
    }

    /**
     * Return the path found.
     *
     * @return path representation, or null if no path exists
     */
    public GraphPath<V, E> getPath()
    {
        return path;
    }

    /**
     * Return the weighted length of the path found.
     *
     * @return path length, or Double.POSITIVE_INFINITY if no path exists
     */
    public double getPathLength()
    {
        if (path == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return path.getWeight();
        }
    }

    /**
     * Convenience method to find the shortest path via a single static method call. If you need a
     * more advanced search (e.g. limited by radius, or computation of the path length), use the
     * constructor instead.
     *
     * @param graph the graph to be searched
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     *
     * @return List of edges, or null if no path exists
     * 
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     */
    public static <V, E> List<E> findPathBetween(Graph<V, E> graph, V startVertex, V endVertex)
    {
        return new BidirectionalDijkstraShortestPath<>(graph, startVertex, endVertex)
            .getPathEdgeList();
    }

    /**
     * The implementation details
     */
    class AlgorithmDetails
    {

        private final SearchFrontier forwardFrontier;
        private final SearchFrontier backwardFrontier;

        private final V source;
        private final V target;
        private final double radius;

        public AlgorithmDetails(Graph<V, E> graph, V source, V target, double radius)
        {
            this.forwardFrontier = new SearchFrontier(graph);
            if (graph instanceof DirectedGraph) {
                this.backwardFrontier =
                    new SearchFrontier(new EdgeReversedGraph<>(((DirectedGraph<V, E>) graph)));
            } else {
                this.backwardFrontier = new SearchFrontier(graph);
            }
            this.source = source;
            this.target = target;
            this.radius = radius;
        }

        public GraphPath<V, E> run()
        {
            // handle special case if source equals target
            if (source.equals(target)) {
                return new GraphWalk<>(
                    forwardFrontier.graph, source, target, Collections.singletonList(source),
                    Collections.emptyList(), 0d);
            }

            assert !source.equals(target);

            // initialize both frontiers
            forwardFrontier.updateDistance(source, null, 0d);
            backwardFrontier.updateDistance(target, null, 0d);

            // initialize best path
            double bestPath = Double.POSITIVE_INFINITY;
            V bestPathCommonVertex = null;

            SearchFrontier frontier = forwardFrontier;
            SearchFrontier otherFrontier = backwardFrontier;

            while (true) {
                // stopping condition
                if (frontier.heap.isEmpty() || otherFrontier.heap.isEmpty()
                    || frontier.heap.min().getKey()
                        + otherFrontier.heap.min().getKey() >= bestPath)
                {
                    break;
                }

                // frontier scan
                FibonacciHeapNode<QueueEntry> node = frontier.heap.removeMin();
                V v = node.getData().v;
                double vDistance = node.getKey();

                for (E e : frontier.specifics.edgesOf(v)) {

                    V u = Graphs.getOppositeVertex(frontier.graph, e, v);

                    double eWeight = frontier.graph.getEdgeWeight(e);

                    frontier.updateDistance(u, e, vDistance + eWeight);

                    // check path with u's distance from the other frontier
                    double pathDistance = vDistance + eWeight + otherFrontier.getDistance(u);

                    if (pathDistance < bestPath) {
                        bestPath = pathDistance;
                        bestPathCommonVertex = u;
                    }

                }

                // swap frontiers
                SearchFrontier tmpFrontier = frontier;
                frontier = otherFrontier;
                otherFrontier = tmpFrontier;

            }

            // create path if found
            if (Double.isFinite(bestPath) && bestPath <= radius) {
                return createPath(bestPath, bestPathCommonVertex);
            }

            return null;
        }

        private GraphPath<V, E> createPath(double weight, V commonVertex)
        {
            LinkedList<E> edgeList = new LinkedList<>();
            LinkedList<V> vertexList = new LinkedList<>();

            // add common vertex
            vertexList.add(commonVertex);

            // traverse forward path
            V v = commonVertex;
            while (true) {
                E e = forwardFrontier.getTreeEdge(v);

                if (e == null) {
                    break;
                }

                edgeList.addFirst(e);
                v = Graphs.getOppositeVertex(forwardFrontier.graph, e, v);
                vertexList.addFirst(v);
            }

            // traverse reverse path
            v = commonVertex;
            while (true) {
                E e = backwardFrontier.getTreeEdge(v);

                if (e == null) {
                    break;
                }

                edgeList.addLast(e);
                v = Graphs.getOppositeVertex(backwardFrontier.graph, e, v);
                vertexList.addLast(v);
            }

            return new GraphWalk<>(
                forwardFrontier.graph, source, target, vertexList, edgeList, weight);
        }

        /**
         * Helper class to maintain the search frontier
         */
        class SearchFrontier
        {
            final Graph<V, E> graph;
            final Specifics specifics;

            final FibonacciHeap<QueueEntry> heap;
            final Map<V, FibonacciHeapNode<QueueEntry>> seen;

            public SearchFrontier(Graph<V, E> graph)
            {
                this.graph = graph;
                if (graph instanceof DirectedGraph) {
                    this.specifics = new DirectedSpecifics((DirectedGraph<V, E>) graph);
                } else {
                    this.specifics = new UndirectedSpecifics(graph);
                }
                this.heap = new FibonacciHeap<>();
                this.seen = new HashMap<>();
            }

            public void updateDistance(V v, E e, double distance)
            {
                FibonacciHeapNode<QueueEntry> node = seen.get(v);
                if (node == null) {
                    node = new FibonacciHeapNode<>(new QueueEntry(e, v));
                    heap.insert(node, distance);
                    seen.put(v, node);
                } else {
                    if (distance < node.getKey()) {
                        heap.decreaseKey(node, distance);
                        node.getData().e = e;
                    }
                }
            }

            public double getDistance(V v)
            {
                FibonacciHeapNode<QueueEntry> node = seen.get(v);
                if (node == null) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return node.getKey();
                }
            }

            public E getTreeEdge(V v)
            {
                FibonacciHeapNode<QueueEntry> node = seen.get(v);
                if (node == null) {
                    return null;
                } else {
                    return node.getData().e;
                }
            }

        }

        abstract class Specifics
        {
            public abstract Set<? extends E> edgesOf(V vertex);
        }

        class DirectedSpecifics
            extends Specifics
        {

            private DirectedGraph<V, E> graph;

            public DirectedSpecifics(DirectedGraph<V, E> g)
            {
                graph = g;
            }

            @Override
            public Set<? extends E> edgesOf(V vertex)
            {
                return graph.outgoingEdgesOf(vertex);
            }
        }

        class UndirectedSpecifics
            extends Specifics
        {

            private Graph<V, E> graph;

            public UndirectedSpecifics(Graph<V, E> g)
            {
                graph = g;
            }

            @Override
            public Set<E> edgesOf(V vertex)
            {
                return graph.edgesOf(vertex);
            }
        }

        class QueueEntry
        {
            E e;
            V v;

            public QueueEntry(E e, V v)
            {
                this.e = e;
                this.v = v;
            }
        }

    }

}

// End BidirectionalDijkstraShortestPath.java
