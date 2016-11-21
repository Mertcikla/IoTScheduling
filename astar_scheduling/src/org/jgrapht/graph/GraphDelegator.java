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

import java.io.*;
import java.util.*;

import org.jgrapht.*;

/**
 * A graph backed by the the graph specified at the constructor, which delegates all its methods to
 * the backing graph. Operations on this graph "pass through" to the to the backing graph. Any
 * modification made to this graph or the backing graph is reflected by the other.
 *
 * <p>
 * This graph does <i>not</i> pass the hashCode and equals operations through to the backing graph,
 * but relies on <tt>Object</tt>'s <tt>equals</tt> and <tt>hashCode</tt> methods.
 * </p>
 *
 * <p>
 * This class is mostly used as a base for extending subclasses.
 * </p>
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Barak Naveh
 * @since Jul 20, 2003
 */
public class GraphDelegator<V, E>
    extends AbstractGraph<V, E>
    implements Graph<V, E>, Serializable
{
    private static final long serialVersionUID = 3257005445226181425L;

    /**
     * The graph to which operations are delegated.
     */
    private Graph<V, E> delegate;

    /**
     * Constructor for GraphDelegator.
     *
     * @param g the backing graph (the delegate).
     * @throws IllegalArgumentException iff <code>g==null</code>
     */
    public GraphDelegator(Graph<V, E> g)
    {
        super();

        if (g == null) {
            throw new IllegalArgumentException("g must not be null.");
        }

        delegate = g;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        return delegate.getAllEdges(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        return delegate.getEdge(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return delegate.getEdgeFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        return delegate.addEdge(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        return delegate.addEdge(sourceVertex, targetVertex, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(V v)
    {
        return delegate.addVertex(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(E e)
    {
        return delegate.containsEdge(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        return delegate.containsVertex(v);
    }

    /**
     * Returns the degree of the specified vertex.
     *
     * @param vertex vertex whose degree is to be calculated
     * @return the degree of the specified vertex
     * @see UndirectedGraph#degreeOf(Object)
     */
    public int degreeOf(V vertex)
    {
        return ((UndirectedGraph<V, E>) delegate).degreeOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgeSet()
    {
        return delegate.edgeSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        return delegate.edgesOf(vertex);
    }

    /**
     * Returns the "in degree" of the specified vertex.
     *
     * @param vertex vertex whose in degree is to be calculated
     * @return the in degree of the specified vertex
     * @see DirectedGraph#inDegreeOf(Object)
     */
    public int inDegreeOf(V vertex)
    {
        return ((DirectedGraph<V, ? extends E>) delegate).inDegreeOf(vertex);
    }

    /**
     * Returns a set of all edges incoming into the specified vertex.
     *
     * @param vertex the vertex for which the list of incoming edges to be returned
     * @return a set of all edges incoming into the specified vertex
     * @see DirectedGraph#incomingEdgesOf(Object)
     */
    public Set<E> incomingEdgesOf(V vertex)
    {
        return ((DirectedGraph<V, E>) delegate).incomingEdgesOf(vertex);
    }

    /**
     * Returns the "out degree" of the specified vertex.
     *
     * @param vertex vertex whose out degree is to be calculated
     * @return the out degree of the specified vertex
     * @see DirectedGraph#outDegreeOf(Object)
     */
    public int outDegreeOf(V vertex)
    {
        return ((DirectedGraph<V, ? extends E>) delegate).outDegreeOf(vertex);
    }

    /**
     * Returns a set of all edges outgoing from the specified vertex.
     *
     * @param vertex the vertex for which the list of outgoing edges to be returned
     * @return a set of all edges outgoing from the specified vertex
     * @see DirectedGraph#outgoingEdgesOf(Object)
     */
    public Set<E> outgoingEdgesOf(V vertex)
    {
        return ((DirectedGraph<V, E>) delegate).outgoingEdgesOf(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        return delegate.removeEdge(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        return delegate.removeEdge(sourceVertex, targetVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        return delegate.removeVertex(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return delegate.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        return delegate.vertexSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeSource(E e)
    {
        return delegate.getEdgeSource(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeTarget(E e)
    {
        return delegate.getEdgeTarget(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E e)
    {
        return delegate.getEdgeWeight(e);
    }

    /**
     * Assigns a weight to an edge.
     *
     * @param e edge on which to set weight
     * @param weight new weight for edge
     */
    public void setEdgeWeight(E e, double weight)
    {
        ((WeightedGraph<V, E>) delegate).setEdgeWeight(e, weight);
    }
}

// End GraphDelegator.java
