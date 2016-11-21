/*
 * (C) Copyright 2016-2016, by Barak Naveh and Contributors.
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
package org.jgrapht.experimental.alg;

import java.util.*;

/**
 * An interface for an exact algorithm.
 *
 * @param <ResultType> type of the result
 * @param <V> type of the input
 */
public interface ExactAlgorithm<ResultType, V>
{
    /**
     * Get the result.
     * 
     * @param optionalData optional data
     * @return the result
     */
    ResultType getResult(Map<V, Object> optionalData);
}

// End ExactAlgorithm.java
