/*
 * (C) Copyright 2003-2016, by Joris Kinable and Contributors.
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

/**
 * Representation of an unordered pair of vertices. For a given pair of vertices V u, V w,
 * UnorderedVertexPair(u,w) equals UnorderedVertexPair(w,u).
 *
 * @param <V> the graph vertex type
 *
 * @author Joris Kinable
 */
public class UnorderedVertexPair<V>
    extends VertexPair<V>
{
    private static final long serialVersionUID = -959200511994439665L;

    /**
     * Create a unordered new vertex pair
     * 
     * @param n1 the first vertex
     * @param n2 the second vertex
     */
    public UnorderedVertexPair(V n1, V n2)
    {
        super(n1, n2);
    }

    @Override
    public String toString()
    {
        return "{" + n1 + "," + n2 + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof UnorderedVertexPair))
            return false;

        @SuppressWarnings("unchecked") UnorderedVertexPair<V> other = (UnorderedVertexPair<V>) o;

        return (elementEquals(n1, other.n1) && elementEquals(n2, other.n2))
            || (elementEquals(n1, other.n2) && elementEquals(n2, other.n1));
    }

    @Override
    public int hashCode()
    {
        int hash1 = n1.hashCode();
        int hash2 = n2.hashCode();
        return hash1 > hash2 ? hash1 * 31 + hash2 : hash2 * 31 + hash1;
    }
}
