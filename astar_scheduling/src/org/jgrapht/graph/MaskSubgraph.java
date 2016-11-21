/*
 * (C) Copyright 2007-2016, by France Telecom and Contributors.
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
 * An unmodifiable subgraph induced by a vertex/edge masking function. The subgraph will keep track
 * of edges being added to its vertex subset as well as deletion of edges and vertices. When
 * iterating over the vertices/edges, it will iterate over the vertices/edges of the base graph and
 * discard vertices/edges that are masked (an edge with a masked extremity vertex is discarded as
 * well).
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
public class MaskSubgraph<V, E>
    extends AbstractGraph<V, E>
{
    private static final String UNMODIFIABLE = "this graph is unmodifiable";

    private Graph<V, E> base;

    private Set<E> edges;

    private MaskFunctor<V, E> mask;

    private Set<V> vertices;

    /**
     * Creates a new induced subgraph. Running-time = O(1).
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param mask vertices and edges to exclude in the subgraph. If a vertex/edge is masked, it is
     *        as if it is not in the subgraph.
     */
    public MaskSubgraph(Graph<V, E> base, MaskFunctor<V, E> mask)
    {
        super();
        this.base = base;
        this.mask = mask;

        this.vertices = new MaskVertexSet<>(base.vertexSet(), mask);
        this.edges = new MaskEdgeSet<>(base, base.edgeSet(), mask);
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
    public boolean addEdge(V sourceVertex, V targetVertex, E edge)
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
    public boolean containsEdge(E e)
    {
        return edgeSet().contains(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        return vertexSet().contains(v);
    }

    /**
     * Returns the degree of the specified vertex.
     *
     * @param vertex vertex whose degree is to be calculated
     * @return the degree of the specified vertex.
     */
    public int degreeOf(V vertex)
    {
        return edgesOf(vertex).size();
    }

    @Override
    public Set<E> edgeSet()
    {
        return this.edges;
    }

    @Override
    public Set<E> edgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return new MaskEdgeSet<>(this.base, this.base.edgesOf(vertex), this.mask);
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        if (containsVertex(sourceVertex) && containsVertex(targetVertex)) {
            return new MaskEdgeSet<>(
                this.base, this.base.getAllEdges(sourceVertex, targetVertex), this.mask);
        } else
            return null;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        Set<E> edges = getAllEdges(sourceVertex, targetVertex);

        if ((edges == null) || edges.isEmpty()) {
            return null;
        } else {
            return edges.iterator().next();
        }
    }

    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return this.base.getEdgeFactory();
    }

    @Override
    public V getEdgeSource(E edge)
    {
        assert (edgeSet().contains(edge));

        return this.base.getEdgeSource(edge);
    }

    @Override
    public V getEdgeTarget(E edge)
    {
        assert (edgeSet().contains(edge));

        return this.base.getEdgeTarget(edge);
    }

    @Override
    public double getEdgeWeight(E edge)
    {
        assert (edgeSet().contains(edge));

        return this.base.getEdgeWeight(edge);
    }

    /**
     * Returns a set of all edges incoming into the specified vertex.
     *
     * @param vertex the vertex for which the list of incoming edges to be returned
     * @return a set of all edges incoming into the specified vertex
     */
    public Set<E> incomingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return new MaskEdgeSet<>(
            this.base, ((DirectedGraph<V, E>) this.base).incomingEdgesOf(vertex), this.mask);
    }

    /**
     * Returns the "in degree" of the specified vertex.
     *
     * @param vertex vertex whose in degree is to be calculated
     * @return the in degree of the specified vertex.
     */
    public int inDegreeOf(V vertex)
    {
        return incomingEdgesOf(vertex).size();
    }

    /**
     * Returns the "out degree" of the specified vertex.
     *
     * @param vertex vertex whose out degree is to be calculated
     * @return the out degree of the specified vertex.
     */
    public int outDegreeOf(V vertex)
    {
        return outgoingEdgesOf(vertex).size();
    }

    /**
     * Returns a set of all edges outgoing from the specified vertex.
     *
     * @param vertex the vertex for which the list of outgoing edges to be returned
     * @return a set of all edges outgoing from the specified vertex
     */
    public Set<E> outgoingEdgesOf(V vertex)
    {
        assertVertexExist(vertex);

        return new MaskEdgeSet<>(
            this.base, ((DirectedGraph<V, E>) this.base).outgoingEdgesOf(vertex), this.mask);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        return this.vertices;
    }
}

// End MaskSubgraph.java
