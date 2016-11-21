/*
 * (C) Copyright 2004-2016, by Michael Behrisch and Contributors.
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
package org.jgrapht.experimental;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.event.*;
import org.jgrapht.graph.*;

/**
 * A unmodifiable graph which is the squared graph of another.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Michael Behrisch
 * @since Sep 14, 2004
 */
public class GraphSquare<V, E>
    extends AbstractBaseGraph<V, E>
{
    private static final long serialVersionUID = -2642034600395594304L;
    private static final String UNMODIFIABLE = "this graph is unmodifiable";

    /**
     * Constructor for GraphSquare.
     *
     * @param g the graph of which a square is to be created.
     * @param createLoops whether to create self loops
     */
    public GraphSquare(final Graph<V, E> g, final boolean createLoops)
    {
        super(g.getEdgeFactory(), false, createLoops);
        Graphs.addAllVertices(this, g.vertexSet());
        addSquareEdges(g, createLoops);

        if (g instanceof ListenableGraph) {
            ((ListenableGraph<V, E>) g).addGraphListener(new GraphListener<V, E>()
            {
                @Override
                public void edgeAdded(GraphEdgeChangeEvent<V, E> e)
                {
                    E edge = e.getEdge();
                    addEdgesStartingAt(
                        g, g.getEdgeSource(edge), g.getEdgeTarget(edge), createLoops);
                    addEdgesStartingAt(
                        g, g.getEdgeTarget(edge), g.getEdgeSource(edge), createLoops);
                }

                @Override
                public void edgeRemoved(GraphEdgeChangeEvent<V, E> e)
                { // this is not a very efficient implementation
                    GraphSquare.super.removeAllEdges(edgeSet());
                    addSquareEdges(g, createLoops);
                }

                @Override
                public void vertexAdded(GraphVertexChangeEvent<V> e)
                {
                }

                @Override
                public void vertexRemoved(GraphVertexChangeEvent<V> e)
                {
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(V v)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllEdges(Collection<? extends E> edges)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        throw new UnsupportedOperationException(UNMODIFIABLE);
    }

    private void addEdgesStartingAt(final Graph<V, E> g, final V v, final V u, boolean createLoops)
    {
        if (!g.containsEdge(v, u)) {
            return;
        }

        final List<V> adjVertices = Graphs.neighborListOf(g, u);

        for (final V w : adjVertices) {
            if (g.containsEdge(u, w) && ((v != w) || createLoops)) {
                super.addEdge(v, w);
            }
        }
    }

    private void addSquareEdges(Graph<V, E> g, boolean createLoops)
    {
        for (V v : g.vertexSet()) {
            List<V> adjVertices = Graphs.neighborListOf(g, v);

            for (V adjVertice : adjVertices) {
                addEdgesStartingAt(g, v, adjVertice, createLoops);
            }
        }
    }
}

// End GraphSquare.java
