/*
 * (C) Copyright 2009-2016, by John V Sichi and Contributors.
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
package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.*;

/**
 * GraphPathImpl is a default implementation of {@link GraphPath}.
 *
 * @author John Sichi
 * @version $Id$
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @deprecated use {@link GraphWalk} instead
 */
@Deprecated
public class GraphPathImpl<V, E>
    implements GraphPath<V, E>
{
    private Graph<V, E> graph;

    private List<E> edgeList;

    private V startVertex;

    private V endVertex;

    private double weight;

    /**
     * Create a new graph path
     * 
     * @param graph the graph
     * @param startVertex the start vertex
     * @param endVertex the end vertex
     * @param edgeList the edge list of the path
     * @param weight the total weight of the path
     */
    public GraphPathImpl(
        Graph<V, E> graph, V startVertex, V endVertex, List<E> edgeList, double weight)
    {
        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    // implement GraphPath
    @Override
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    // implement GraphPath
    @Override
    public V getStartVertex()
    {
        return startVertex;
    }

    // implement GraphPath
    @Override
    public V getEndVertex()
    {
        return endVertex;
    }

    // implement GraphPath
    @Override
    public List<E> getEdgeList()
    {
        return edgeList;
    }

    // implement GraphPath
    @Override
    public double getWeight()
    {
        return weight;
    }

    // override Object
    @Override
    public String toString()
    {
        return edgeList.toString();
    }
}

// End GraphPathImpl.java
