/*
 * (C) Copyright 2009-2016, by Soren Davidsen and Contributors.
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
package org.jgrapht.util;

import java.io.*;
import java.util.*;

/**
 * Representation of a pair of vertices; to be replaced by Pair&lt;V,V&gt; if Sun ever gets around
 * to adding Pair to java.util.
 * 
 * @param <V> the graph vertex type
 *
 * @author Soren (soren@tanesha.net)
 * @author Joris Kinable
 */
public class VertexPair<V>
    implements Serializable
{
    private static final long serialVersionUID = -852258620031566794L;

    protected final V n1;
    protected final V n2;

    /**
     * Create a new vertex pair
     * 
     * @param n1 the first vertex
     * @param n2 the second vertex
     */
    public VertexPair(V n1, V n2)
    {
        this.n1 = n1;
        this.n2 = n2;
    }

    /**
     * Get the first vertex of the pair
     * 
     * @return the first vertex of the pair
     */
    public V getFirst()
    {
        return n1;
    }

    /**
     * Get the second vertex of the pair
     * 
     * @return the second vertex of the pair
     */
    public V getSecond()
    {
        return n2;
    }

    /**
     * Assess if this pair contains the vertex.
     *
     * @param v The vertex in question
     *
     * @return true if contains, false otherwise
     */
    public boolean hasVertex(V v)
    {
        return v.equals(n1) || v.equals(n2);
    }

    /**
     * Given on of the vertices of the pair, get the other.
     * 
     * @param one one of the vertices
     * @return the other vertex of the pair
     */
    public V getOther(V one)
    {
        if (one.equals(n1)) {
            return n2;
        } else if (one.equals(n2)) {
            return n1;
        } else {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return "(" + n1 + "," + n2 + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof VertexPair))
            return false;

        @SuppressWarnings("unchecked") VertexPair<V> other = (VertexPair<V>) o;

        return (elementEquals(n1, other.n1) && elementEquals(n2, other.n2));
    }

    /**
     * Compares two elements. Returns true if they are both null, or when they are equal.
     * 
     * @param element1 the first element
     * @param element2 the second element
     * @return true if they are both null, or when they are equal, false otherwise.
     */
    protected boolean elementEquals(V element1, V element2)
    {
        if (element1 == null)
            return element2 == null;
        else
            return element1.equals(element2);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(n1, n2);
    }
}

// End VertexPair.java
