/*
 * (C) Copyright 2003-2016, by Barak Naveh and Contributors.
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

import org.jgrapht.*;
import org.jgrapht.graph.builder.*;

/**
 * A simple directed graph. A simple directed graph is a directed graph in which neither multiple
 * edges between any two vertices nor loops are permitted.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 */
public class SimpleDirectedGraph<V, E>
    extends AbstractBaseGraph<V, E>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = 4049358608472879671L;

    /**
     * Creates a new simple directed graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public SimpleDirectedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass));
    }

    /**
     * Creates a new simple directed graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public SimpleDirectedGraph(EdgeFactory<V, E> ef)
    {
        super(ef, false, false);
    }

    /**
     * Create a builder for this kind of graph.
     * 
     * @param edgeClass class on which to base factory for edges
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
    public static <V,
        E> DirectedGraphBuilderBase<V, E, ? extends SimpleDirectedGraph<V, E>, ?> builder(
            Class<? extends E> edgeClass)
    {
        return new DirectedGraphBuilder<>(new SimpleDirectedGraph<>(edgeClass));
    }

    /**
     * Create a builder for this kind of graph.
     * 
     * @param ef the edge factory of the new graph
     * @param <V> the graph vertex type
     * @param <E> the graph edge type
     * @return a builder for this kind of graph
     */
    public static <V,
        E> DirectedGraphBuilderBase<V, E, ? extends SimpleDirectedGraph<V, E>, ?> builder(
            EdgeFactory<V, E> ef)
    {
        return new DirectedGraphBuilder<>(new SimpleDirectedGraph<>(ef));
    }
}

// End SimpleDirectedGraph.java
