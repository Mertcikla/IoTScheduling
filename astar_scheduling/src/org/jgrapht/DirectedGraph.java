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
package org.jgrapht;

import java.util.*;

/**
 * A graph whose all edges are directed. This is the root interface of all directed graphs.
 *
 * <p>
 * See <a href="http://mathworld.wolfram.com/DirectedGraph.html">
 * http://mathworld.wolfram.com/DirectedGraph.html</a> for more on directed graphs.
 * </p>
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Barak Naveh
 * @since Jul 14, 2003
 */
public interface DirectedGraph<V, E>
    extends Graph<V, E>
{
    /**
     * Returns the "in degree" of the specified vertex. An in degree of a vertex in a directed graph
     * is the number of inward directed edges from that vertex. See
     * <a href="http://mathworld.wolfram.com/Indegree.html">
     * http://mathworld.wolfram.com/Indegree.html</a>.
     *
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return the degree of the specified vertex.
     */
    int inDegreeOf(V vertex);

    /**
     * Returns a set of all edges incoming into the specified vertex.
     *
     * @param vertex the vertex for which the list of incoming edges to be returned.
     *
     * @return a set of all edges incoming into the specified vertex.
     */
    Set<E> incomingEdgesOf(V vertex);

    /**
     * Returns the "out degree" of the specified vertex. An out degree of a vertex in a directed
     * graph is the number of outward directed edges from that vertex. See
     * <a href="http://mathworld.wolfram.com/Outdegree.html">
     * http://mathworld.wolfram.com/Outdegree.html</a>.
     *
     * @param vertex vertex whose degree is to be calculated.
     *
     * @return the degree of the specified vertex.
     */
    int outDegreeOf(V vertex);

    /**
     * Returns a set of all edges outgoing from the specified vertex.
     *
     * @param vertex the vertex for which the list of outgoing edges to be returned.
     *
     * @return a set of all edges outgoing from the specified vertex.
     */
    Set<E> outgoingEdgesOf(V vertex);
}

// End DirectedGraph.java
