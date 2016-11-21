/*
 * (C) Copyright 2013-2016, by Alexey Kudinkin and Contributors.
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
package org.jgrapht.alg.interfaces;

/**
 * Allows to derive weighted matching from <i>general</i> graph
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @see MatchingAlgorithm
 */
public interface WeightedMatchingAlgorithm<V, E>
    extends MatchingAlgorithm<V, E>
{
    /**
     * Returns weight of a matching found
     *
     * @return weight of a matching found
     */
    double getMatchingWeight();
}

// End WeightedMatchingAlgorithm.java
