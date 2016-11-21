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

/**
 * A functor interface for masking out vertices and edges of a graph.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
public interface MaskFunctor<V, E>
{
    /**
     * Returns <code>true</code> if the edge is masked, <code>false</code> otherwise.
     *
     * @param edge an edge
     *
     * @return <code>true</code> if the edge is masked, <code>false</code> otherwise
     */
    boolean isEdgeMasked(E edge);

    /**
     * Returns <code>true</code> if the vertex is masked, <code>false</code> otherwise.
     *
     * @param vertex a vertex
     *
     * @return <code>true</code> if the vertex is masked, <code>false</code> otherwise
     */
    boolean isVertexMasked(V vertex);
}

// End MaskFunctor.java
