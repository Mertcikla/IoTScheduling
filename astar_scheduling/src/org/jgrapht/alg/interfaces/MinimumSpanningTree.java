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

import java.util.*;

/**
 * Allows to derive <a href=http://en.wikipedia.org/wiki/Minimum_spanning_tree> minimum spanning
 * tree</a> from given undirected connected graph. In the case of disconnected graphs it would
 * rather derive minimum spanning <i>forest</i>
 *
 * @param <V> vertex concept type
 * @param <E> edge concept type
 */
public interface MinimumSpanningTree<V, E>
{
    /**
     * Returns edges set constituting the minimum spanning tree/forest
     *
     * @return minimum spanning-tree edges set
     */
    Set<E> getMinimumSpanningTreeEdgeSet();

    /**
     * Returns total weight of the minimum spanning tree/forest.
     *
     * @return minimum spanning-tree total weight
     */
    double getMinimumSpanningTreeTotalWeight();
}

// End MinimumSpanningTree.java
