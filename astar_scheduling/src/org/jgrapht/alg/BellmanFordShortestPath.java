/*
 * (C) Copyright 2006-2016, by France Telecom and Contributors.
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

/**
 * <a href="http://www.nist.gov/dads/HTML/bellmanford.html">Bellman-Ford algorithm</a>: weights
 * could be negative, paths could be constrained by a maximum number of edges.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class BellmanFordShortestPath<V, E>
{
    private static final double DEFAULT_EPSILON = 0.000000001;

    /**
     * Graph on which shortest paths are searched.
     */
    protected Graph<V, E> graph;

    /**
     * Start vertex.
     */
    protected V startVertex;

    private BellmanFordIterator<V, E> iter;

    /**
     * Maximum number of edges of the calculated paths.
     */
    private int nMaxHops;

    private int passNumber;

    private double epsilon;

    /**
     * Creates an object to calculate shortest paths between the start vertex and others vertices
     * using the Bellman-Ford algorithm.
     *
     * @param graph the graph
     * @param startVertex the start vertex
     */
    public BellmanFordShortestPath(Graph<V, E> graph, V startVertex)
    {
        this(graph, startVertex, graph.vertexSet().size() - 1);
    }

    /**
     * Creates an object to calculate shortest paths between the start vertex and others vertices
     * using the Bellman-Ford algorithm.
     *
     * @param graph the graph
     * @param startVertex the start vertex
     * @param nMaxHops maximum number of edges of the calculated paths
     */
    public BellmanFordShortestPath(Graph<V, E> graph, V startVertex, int nMaxHops)
    {
        this(graph, startVertex, nMaxHops, DEFAULT_EPSILON);
    }

    /**
     * Creates an object to calculate shortest paths between the start vertex and others vertices
     * using the Bellman-Ford algorithm.
     *
     * @param graph the graph
     * @param startVertex the start vertex
     * @param nMaxHops maximum number of edges of the calculated paths.
     * @param epsilon tolerance factor when comparing floating point values
     */
    public BellmanFordShortestPath(Graph<V, E> graph, V startVertex, int nMaxHops, double epsilon)
    {
        this.startVertex = startVertex;
        this.nMaxHops = nMaxHops;
        this.graph = graph;
        this.passNumber = 1;
        this.epsilon = epsilon;
    }

    /**
     * Get the cost of the shortest path to a vertex.
     * 
     * @param endVertex the end vertex
     * @return the cost of the shortest path between the start vertex and the end vertex.
     */
    public double getCost(V endVertex)
    {
        assertGetPath(endVertex);

        lazyCalculate();

        BellmanFordPathElement<V, E> pathElement = this.iter.getPathElement(endVertex);

        if (pathElement == null) {
            return Double.POSITIVE_INFINITY;
        }

        return pathElement.getCost();
    }

    /**
     * Get the shortest path to a vertex.
     * 
     * @param endVertex the end vertex
     * @return list of edges, or null if no path exists between the start vertex and the end vertex
     */
    public List<E> getPathEdgeList(V endVertex)
    {
        assertGetPath(endVertex);

        lazyCalculate();

        BellmanFordPathElement<V, E> pathElement = this.iter.getPathElement(endVertex);

        if (pathElement == null) {
            return null;
        }

        return pathElement.createEdgeListPath();
    }

    private void assertGetPath(V endVertex)
    {
        if (endVertex.equals(this.startVertex)) {
            throw new IllegalArgumentException("The end vertex is the same as the start vertex!");
        }

        if (!this.graph.containsVertex(endVertex)) {
            throw new IllegalArgumentException("Graph must contain the end vertex!");
        }
    }

    private void lazyCalculate()
    {
        if (this.iter == null) {
            this.iter = new BellmanFordIterator<>(this.graph, this.startVertex, epsilon);
        }

        // at the i-th pass the shortest paths with less (or equal) than i edges
        // are calculated.
        for (; (this.passNumber <= this.nMaxHops) && this.iter.hasNext(); this.passNumber++) {
            this.iter.next();
        }
    }

    /**
     * Convenience method to find the shortest path via a single static method call. If you need a
     * more advanced search (e.g. limited by hops, or computation of the path length), use the
     * constructor instead.
     *
     * @param graph the graph to be searched
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     *
     * @return List of Edges, or null if no path exists
     */
    public static <V, E> List<E> findPathBetween(Graph<V, E> graph, V startVertex, V endVertex)
    {
        BellmanFordShortestPath<V, E> alg = new BellmanFordShortestPath<>(graph, startVertex);

        return alg.getPathEdgeList(endVertex);
    }
}

// End BellmanFordShortestPath.java
