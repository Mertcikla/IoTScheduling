/*
 * (C) Copyright 2015-2016, by Joris Kinable. and Contributors.
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
import org.jgrapht.util.*;

/**
 * Read-only union of an undirected and a directed graph.
 * 
 * @param <V> the vertex type
 * @param <E> the edge type
 * 
 */
public class MixedGraphUnion<V, E>
    extends GraphUnion<V, E, Graph<V, E>>
    implements DirectedGraph<V, E>
{
    private static final long serialVersionUID = -1961714127770731054L;

    private final UndirectedGraph<V, E> undirectedGraph;
    private final DirectedGraph<V, E> directedGraph;

    /**
     * Construct a new graph union.
     * 
     * @param g1 the undirected graph
     * @param g2 the directed graph
     * @param operator the weight combiner (policy for edge weight calculation)
     */
    public MixedGraphUnion(
        UndirectedGraph<V, E> g1, DirectedGraph<V, E> g2, WeightCombiner operator)
    {
        super(g1, g2, operator);
        this.undirectedGraph = g1;
        this.directedGraph = g2;
    }

    /**
     * Construct a new graph union. The union will use the {@link WeightCombiner#SUM} weight
     * combiner.
     * 
     * @param g1 the undirected graph
     * @param g2 the directed graph
     */
    public MixedGraphUnion(UndirectedGraph<V, E> g1, DirectedGraph<V, E> g2)
    {
        super(g1, g2);
        this.undirectedGraph = g1;
        this.directedGraph = g2;
    }

    @Override
    public int inDegreeOf(V vertex)
    {
        Set<E> res = incomingEdgesOf(vertex);
        return res.size();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex)
    {
        Set<E> res = new LinkedHashSet<>();
        if (directedGraph.containsVertex(vertex)) {
            res.addAll(directedGraph.incomingEdgesOf(vertex));
        }
        if (undirectedGraph.containsVertex(vertex)) {
            res.addAll(undirectedGraph.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }

    @Override
    public int outDegreeOf(V vertex)
    {
        Set<E> res = outgoingEdgesOf(vertex);
        return res.size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex)
    {
        Set<E> res = new LinkedHashSet<>();
        if (directedGraph.containsVertex(vertex)) {
            res.addAll(directedGraph.outgoingEdgesOf(vertex));
        }
        if (undirectedGraph.containsVertex(vertex)) {
            res.addAll(undirectedGraph.edgesOf(vertex));
        }
        return Collections.unmodifiableSet(res);
    }
}

// End MixedGraphUnion.java
