/*
 * (C) Copyright 2006-2016, by John V Sichi and Contributors.
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

import org.jgrapht.*;

/**
 * A default implementation for edges in a {@link WeightedGraph}. All access to the weight of an
 * edge must go through the graph interface, which is why this class doesn't expose any public
 * methods.
 *
 * @author John V. Sichi
 */
public class DefaultWeightedEdge
    extends DefaultEdge
{
    private static final long serialVersionUID = 229708706467350994L;

    double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;

    /**
     * Retrieves the weight of this edge. This is protected, for use by subclasses only (e.g. for
     * implementing toString).
     *
     * @return weight of this edge
     */
    protected double getWeight()
    {
        return weight;
    }
}

// End DefaultWeightedEdge.java
