/*
 * (C) Copyright 2005-2016, by Assaf Lehr and Contributors.
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
package org.jgrapht.generate;

/**
 * This Generator creates a random-topology graph of a specified number of vertexes and edges. An
 * instance of this generator will always return the same graph-topology in calls to
 * generateGraph(). The vertexes can be different (depends on the VertexFactory implementation)
 *
 * <p>
 * However, two instances which use the same constructor parameters will produce two different
 * random graphs (note: as with any random generator, there is always a small possibility that two
 * instances will create the same results).
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Assaf Lehr
 * @since Aug 6, 2005
 */
@Deprecated
public class RandomGraphGenerator<V, E>
    extends GnmRandomGraphGenerator<V, E>
{
    /**
     * Create a new random graph generator
     * 
     * @param aNumOfVertexes number of vertices
     * @param aNumOfEdges number of edges
     */
    public RandomGraphGenerator(int aNumOfVertexes, int aNumOfEdges)
    {
        super(aNumOfVertexes, aNumOfEdges);
    }

    /**
     * Create a new random graph generator
     * 
     * @param aNumOfVertexes number of vertices
     * @param aNumOfEdges number of edges
     * @param seed seed for the random number generator
     */
    public RandomGraphGenerator(int aNumOfVertexes, int aNumOfEdges, long seed)
    {
        super(aNumOfVertexes, aNumOfEdges, seed);
    }
}

// End RandomGraphGenerator.java
