/*
 * (C) Copyright 2003-2016, by John V Sichi and Contributors.
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
import org.jgrapht.traverse.*;

/**
 * An implementation of <a href="http://mathworld.wolfram.com/DijkstrasAlgorithm.html">Dijkstra's
 * shortest path algorithm</a> using <code>ClosestFirstIterator</code>.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author John V. Sichi
 * @since Sep 2, 2003
 */
public final class DijkstraShortestPath<V, E>
{
    private GraphPath<V, E> path;

    /**
     * Creates and executes a new DijkstraShortestPath algorithm instance. An instance is only good
     * for a single search; after construction, it can be accessed to retrieve information about the
     * path found.
     *
     * @param graph the graph to be searched
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     */
    public DijkstraShortestPath(Graph<V, E> graph, V startVertex, V endVertex)
    {
        this(graph, startVertex, endVertex, Double.POSITIVE_INFINITY);
    }

    /**
     * Creates and executes a new DijkstraShortestPath algorithm instance. An instance is only good
     * for a single search; after construction, it can be accessed to retrieve information about the
     * path found.
     *
     * @param graph the graph to be searched
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     * @param radius limit on weighted path length, or Double.POSITIVE_INFINITY for unbounded search
     */
    public DijkstraShortestPath(Graph<V, E> graph, V startVertex, V endVertex, double radius)
    {
        if (!graph.containsVertex(endVertex)) {
            throw new IllegalArgumentException("graph must contain the end vertex");
        }

        ClosestFirstIterator<V, E> iter = new ClosestFirstIterator<>(graph, startVertex, radius);

        while (iter.hasNext()) {
            V vertex = iter.next();

            if (vertex.equals(endVertex)) {
                createEdgeList(graph, iter, startVertex, endVertex);
                return;
            }
        }

        path = null;
    }

    /**
     * Return the edges making up the path found.
     *
     * @return List of Edges, or null if no path exists
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
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return List of Edges, or null if no path exists
     */
    public static <V, E> List<E> findPathBetween(Graph<V, E> graph, V startVertex, V endVertex)
    {
        DijkstraShortestPath<V, E> alg = new DijkstraShortestPath<>(graph, startVertex, endVertex);

        return alg.getPathEdgeList();
    }

    private void createEdgeList(
        Graph<V, E> graph, ClosestFirstIterator<V, E> iter, V startVertex, V endVertex)
    {
        List<E> edgeList = new ArrayList<>();
        List<V> vertexList = new ArrayList<>();
        vertexList.add(endVertex);

        V v = endVertex;

        while (true) {
            E edge = iter.getSpanningTreeEdge(v);

            if (edge == null) {
                break;
            }

            edgeList.add(edge);
            v = Graphs.getOppositeVertex(graph, edge, v);
            vertexList.add(v);
        }

        Collections.reverse(edgeList);
        Collections.reverse(vertexList);
        double pathLength = iter.getShortestPathLength(endVertex);
        path = new GraphWalk<>(graph, startVertex, endVertex, vertexList, edgeList, pathLength);
    }
}

// End DijkstraShortestPath.java
