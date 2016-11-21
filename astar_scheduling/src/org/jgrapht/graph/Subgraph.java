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
package org.jgrapht.graph;

import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.event.*;
import org.jgrapht.util.*;

/**
 * A subgraph is a graph that has a subset of vertices and a subset of edges with respect to some
 * base graph. More formally, a subgraph G(V,E) that is based on a base graph Gb(Vb,Eb) satisfies
 * the following <b><i>subgraph property</i></b>: V is a subset of Vb and E is a subset of Eb. Other
 * than this property, a subgraph is a graph with any respect and fully complies with the
 * <code>Graph</code> interface.
 *
 * <p>
 * If the base graph is a {@link org.jgrapht.ListenableGraph}, the subgraph listens on the base
 * graph and guarantees the subgraph property. If an edge or a vertex is removed from the base
 * graph, it is automatically removed from the subgraph. Subgraph listeners are informed on such
 * removal only if it results in a cascaded removal from the subgraph. If the subgraph has been
 * created as an induced subgraph it also keeps track of edges being added to its vertices. If
 * vertices are added to the base graph, the subgraph remains unaffected.
 * </p>
 *
 * <p>
 * If the base graph is <i>not</i> a ListenableGraph, then the subgraph property cannot be
 * guaranteed. If edges or vertices are removed from the base graph, they are <i>not</i> removed
 * from the subgraph.
 * </p>
 *
 * <p>
 * Modifications to Subgraph are allowed as long as the subgraph property is maintained. Addition of
 * vertices or edges are allowed as long as they also exist in the base graph. Removal of vertices
 * or edges is always allowed. The base graph is <i>never</i> affected by any modification made to
 * the subgraph.
 * </p>
 *
 * <p>
 * A subgraph may provide a "live-window" on a base graph, so that changes made to its vertices or
 * edges are immediately reflected in the base graph, and vice versa. For that to happen, vertices
 * and edges added to the subgraph must be <i>identical</i> (that is, reference-equal and not only
 * value-equal) to their respective ones in the base graph. Previous versions of this class enforced
 * such identity, at a severe performance cost. Currently it is no longer enforced. If you want to
 * achieve a "live-window"functionality, your safest tactics would be to NOT override the
 * <code>equals()</code> methods of your vertices and edges. If you use a class that has already
 * overridden the <code>equals()</code> method, such as <code>String</code>, than you can use a
 * wrapper around it, or else use it directly but exercise a great care to avoid having
 * different-but-equal instances in the subgraph and the base graph.
 * </p>
 *
 * <p>
 * This graph implementation guarantees deterministic vertex and edge set ordering (via
 * {@link LinkedHashSet}).
 * </p>
 *
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <G> the type of the base graph
 *
 * @author Barak Naveh
 * @see Graph
 * @see Set
 * @since Jul 18, 2003
 */
public class Subgraph<V, E, G extends Graph<V, E>>
    extends AbstractGraph<V, E>
    implements Serializable
{
    private static final long serialVersionUID = 3208313055169665387L;
    private static final String NO_SUCH_EDGE_IN_BASE = "no such edge in base graph";
    private static final String NO_SUCH_VERTEX_IN_BASE = "no such vertex in base graph";

    //
    Set<E> edgeSet = new LinkedHashSet<>(); // friendly to improve performance
    Set<V> vertexSet = new LinkedHashSet<>(); // friendly to improve

    // performance

    //
    private transient Set<E> unmodifiableEdgeSet = null;
    private transient Set<V> unmodifiableVertexSet = null;
    private G base;
    private boolean isInduced = false;

    /**
     * Creates a new Subgraph.
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>
     * null</code> then all vertices are included.
     * @param edgeSubset edges to in include in the subgraph. If <code>
     * null</code> then all the edges whose vertices found in the graph are included.
     */
    public Subgraph(G base, Set<V> vertexSubset, Set<E> edgeSubset)
    {
        super();

        this.base = base;

        if (edgeSubset == null) {
            isInduced = true;
        }

        if (base instanceof ListenableGraph<?, ?>) {
            ((ListenableGraph<V, E>) base).addGraphListener(new BaseGraphListener());
        }

        addVerticesUsingFilter(base.vertexSet(), vertexSubset);
        addEdgesUsingFilter(base.edgeSet(), edgeSubset);
    }

    /**
     * Creates a new induced Subgraph. The subgraph will keep track of edges being added to its
     * vertex subset as well as deletion of edges and vertices. If base it not listenable, this is
     * identical to the call Subgraph(base, vertexSubset, null) .
     *
     * @param base the base (backing) graph on which the subgraph will be based.
     * @param vertexSubset vertices to include in the subgraph. If <code>
     * null</code> then all vertices are included.
     */
    public Subgraph(G base, Set<V> vertexSubset)
    {
        this(base, vertexSubset, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex)
    {
        Set<E> edges = null;

        if (containsVertex(sourceVertex) && containsVertex(targetVertex)) {
            edges = new ArrayUnenforcedSet<>();

            Set<E> baseEdges = base.getAllEdges(sourceVertex, targetVertex);

            for (E e : baseEdges) {
                if (edgeSet.contains(e)) { // add if subgraph also contains
                    // it
                    edges.add(e);
                }
            }
        }

        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex)
    {
        Set<E> edges = getAllEdges(sourceVertex, targetVertex);

        if ((edges == null) || edges.isEmpty()) {
            return null;
        } else {
            return edges.iterator().next();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeFactory<V, E> getEdgeFactory()
    {
        return base.getEdgeFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex)
    {
        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        if (!base.containsEdge(sourceVertex, targetVertex)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_IN_BASE);
        }

        Set<E> edges = base.getAllEdges(sourceVertex, targetVertex);

        for (E e : edges) {
            if (!containsEdge(e)) {
                edgeSet.add(e);

                return e;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e)
    {
        if (e == null) {
            throw new NullPointerException();
        }

        if (!base.containsEdge(e)) {
            throw new IllegalArgumentException(NO_SUCH_EDGE_IN_BASE);
        }

        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        assert (base.getEdgeSource(e) == sourceVertex);
        assert (base.getEdgeTarget(e) == targetVertex);

        if (containsEdge(e)) {
            return false;
        } else {
            edgeSet.add(e);

            return true;
        }
    }

    /**
     * Adds the specified vertex to this subgraph.
     *
     * @param v the vertex to be added.
     *
     * @return <code>true</code> if the vertex was added, otherwise <code>
     * false</code>.
     *
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if the base graph does not contain the vertex
     *
     * @see Subgraph
     * @see Graph#addVertex(Object)
     */
    @Override
    public boolean addVertex(V v)
    {
        if (v == null) {
            throw new NullPointerException();
        }

        if (!base.containsVertex(v)) {
            throw new IllegalArgumentException(NO_SUCH_VERTEX_IN_BASE);
        }

        if (containsVertex(v)) {
            return false;
        } else {
            vertexSet.add(v);

            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(E e)
    {
        return edgeSet.contains(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V v)
    {
        return vertexSet.contains(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgeSet()
    {
        if (unmodifiableEdgeSet == null) {
            unmodifiableEdgeSet = Collections.unmodifiableSet(edgeSet);
        }

        return unmodifiableEdgeSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> edgesOf(V vertex)
    {
        assertVertexExist(vertex);

        Set<E> edges = new ArrayUnenforcedSet<>();
        Set<E> baseEdges = base.edgesOf(vertex);

        for (E e : baseEdges) {
            if (containsEdge(e)) {
                edges.add(e);
            }
        }

        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(E e)
    {
        return edgeSet.remove(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex)
    {
        E e = getEdge(sourceVertex, targetVertex);

        return edgeSet.remove(e) ? e : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V v)
    {
        // If the base graph does NOT contain v it means we are here in
        // response to removal of v from the base. In such case we don't need
        // to remove all the edges of v as they were already removed.
        if (containsVertex(v) && base.containsVertex(v)) {
            removeAllEdges(edgesOf(v));
        }

        return vertexSet.remove(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> vertexSet()
    {
        if (unmodifiableVertexSet == null) {
            unmodifiableVertexSet = Collections.unmodifiableSet(vertexSet);
        }

        return unmodifiableVertexSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeSource(E e)
    {
        return base.getEdgeSource(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getEdgeTarget(E e)
    {
        return base.getEdgeTarget(e);
    }

    private void addEdgesUsingFilter(Set<E> edgeSet, Set<E> filter)
    {
        E e;
        boolean containsVertices;
        boolean edgeIncluded;

        for (E anEdgeSet : edgeSet) {
            e = anEdgeSet;

            V sourceVertex = base.getEdgeSource(e);
            V targetVertex = base.getEdgeTarget(e);
            containsVertices = containsVertex(sourceVertex) && containsVertex(targetVertex);

            // note the use of short circuit evaluation
            edgeIncluded = (filter == null) || filter.contains(e);

            if (containsVertices && edgeIncluded) {
                addEdge(sourceVertex, targetVertex, e);
            }
        }
    }

    private void addVerticesUsingFilter(Set<V> vertexSet, Set<V> filter)
    {
        for (V v : vertexSet) {
            // note the use of short circuit evaluation
            if ((filter == null) || filter.contains(v)) {
                addVertex(v);
            }
        }
    }

    /**
     * Get the base graph.
     * 
     * @return the base graph
     */
    public G getBase()
    {
        return base;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeWeight(E e)
    {
        return base.getEdgeWeight(e);
    }

    /**
     * Assigns a weight to an edge.
     *
     * @param e edge on which to set weight
     * @param weight new weight for edge
     * @see WeightedGraph#setEdgeWeight(Object, double)
     */
    public void setEdgeWeight(E e, double weight)
    {
        ((WeightedGraph<V, E>) base).setEdgeWeight(e, weight);
    }

    /**
     * An internal listener on the base graph.
     *
     * @author Barak Naveh
     * @since Jul 20, 2003
     */
    private class BaseGraphListener
        implements GraphListener<V, E>, Serializable
    {
        private static final long serialVersionUID = 4343535244243546391L;

        /**
         * {@inheritDoc}
         */
        @Override
        public void edgeAdded(GraphEdgeChangeEvent<V, E> e)
        {
            if (isInduced) {
                E edge = e.getEdge();
                V source = e.getEdgeSource();
                V target = e.getEdgeTarget();
                if (containsVertex(source) && containsVertex(target)) {
                    addEdge(source, target, edge);
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void edgeRemoved(GraphEdgeChangeEvent<V, E> e)
        {
            E edge = e.getEdge();

            removeEdge(edge);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexAdded(GraphVertexChangeEvent<V> e)
        {
            // we don't care
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexRemoved(GraphVertexChangeEvent<V> e)
        {
            V vertex = e.getVertex();

            removeVertex(vertex);
        }
    }
}

// End Subgraph.java
