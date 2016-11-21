/*
 * (C) Copyright 2015-2016, by Alexey Kudinkin and Contributors.
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
package org.jgrapht.alg.util;

import java.io.Serializable;
import java.util.*;

/**
 * Generic pair.
 * 
 * <p>
 * Although the instances of this class are immutable, it is impossible to ensure that the
 * references passed to the constructor will not be modified by the caller.
 * 
 * @param <A> the first element type
 * @param <B> the second element type
 * 
 */
public class Pair<A, B>
    implements Serializable
{
    private static final long serialVersionUID = 8176288675989092842L;

    /**
     * The first pair element
     */
    public A first;

    /**
     * The second pair element
     */
    public B second;

    /**
     * Create a new pair
     * 
     * @param a the first element
     * @param b the second element
     */
    public Pair(A a, B b)
    {
        this.first = a;
        this.second = b;
    }

    @Override
    public boolean equals(Object other)
    {
        return (other instanceof Pair) && Objects.equals(this.first, ((Pair<A, B>) other).first)
            && Objects.equals(this.second, ((Pair<A, B>) other).second);
    }

    @Override
    public int hashCode()
    {
        return (this.first == null) ? ((this.second == null) ? 0 : (this.second.hashCode() + 1))
            : ((this.second == null) ? (this.first.hashCode() + 3)
                : ((this.first.hashCode() * 19) + this.second.hashCode()));
    }

    /**
     * Creates new pair of elements pulling of the necessity to provide corresponding types of the
     * elements supplied
     *
     * @param a first element
     * @param b second element
     * @param <A> the first element type
     * @param <B> the second element type
     * @return new pair
     */
    public static <A, B> Pair<A, B> of(A a, B b)
    {
        return new Pair<>(a, b);
    }
}

// End Pair.java
