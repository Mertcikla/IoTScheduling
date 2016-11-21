/*
 * (C) Copyright 2009-2016, by Tom Larkworthy and Contributors.
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
package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * The <a href="http://en.wikipedia.org/wiki/Floyd-Warshall_algorithm"> Floyd-Warshall algorithm</a>
 * finds all shortest paths (all n^2 of them) in O(n^3) time. It can also calculate the graph
 * diameter. Note that during construction time, no computations are performed! All computations are
 * performed the first time one of the member methods of this class is invoked. The results are
 * stored, so all subsequent calls to the same method are computationally efficient. Warning: This
 * code has not been tested (and probably doesn't work) on multi-graphs. Code should be updated to
 * work properly on multi-graphs.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Tom Larkworthy
 * @author Soren Davidsen (soren@tanesha.net)
 * @author Joris Kinable
 */
public class FloydWarshallShortestPaths<V, E>
{
    private final Graph<V, E> graph;
    private final List<V> vertices;
    private final Map<V, Integer> vertexIndices;

    private int nShortestPaths = 0;
    private double diameter = Double.NaN;
    private double[][] d = null;
    private int[][] backtrace = null;
    private int[][] lastHopMatrix = null;
    private Map<V, List<GraphPath<V, E>>> paths = null;

    /**
     * Create a new instance of the Floyd-Warshall all-pairs shortest path algorithm.
     * 
     * @param graph the input graph
     */
    public FloydWarshallShortestPaths(Graph<V, E> graph)
    {
        this.graph = graph;
        this.vertices = new ArrayList<>(graph.vertexSet());
        this.vertexIndices = new HashMap<>(this.vertices.size());
        int i = 0;
        for (V vertex : vertices) {
            vertexIndices.put(vertex, i++);
        }
    }

    /**
     * @return the graph on which this algorithm operates
     */
    public Graph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * @return total number of shortest paths
     */
    public int getShortestPathsCount()
    {
        lazyCalculatePaths();
        return nShortestPaths;
    }

    /**
     * Calculates the matrix of all shortest paths, but does not populate the paths map.
     */
    private void lazyCalculateMatrix()
    {
        if (d != null) {
            // already done
            return;
        }

        int n = vertices.size();

        // init the backtrace matrix
        backtrace = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(backtrace[i], -1);
        }

        // initialize matrix, 0
        d = new double[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(d[i], Double.POSITIVE_INFINITY);
        }

        // initialize matrix, 1
        for (int i = 0; i < n; i++) {
            d[i][i] = 0.0;
        }

        // initialize matrix, 2
        if (graph instanceof UndirectedGraph<?, ?>) {
            for (E edge : graph.edgeSet()) {
                int v_1 = vertexIndices.get(graph.getEdgeSource(edge));
                int v_2 = vertexIndices.get(graph.getEdgeTarget(edge));
                d[v_1][v_2] = d[v_2][v_1] = graph.getEdgeWeight(edge);
                backtrace[v_1][v_2] = v_2;
                backtrace[v_2][v_1] = v_1;
            }
        } else { // This works for both Directed and Mixed graphs! Iterating over
                 // the arcs and querying source/sink does not suffice for graphs
                 // which contain both edges and arcs
            DirectedGraph<V, E> directedGraph = (DirectedGraph<V, E>) graph;
            for (V v1 : directedGraph.vertexSet()) {
                int v_1 = vertexIndices.get(v1);
                for (V v2 : Graphs.successorListOf(directedGraph, v1)) {
                    int v_2 = vertexIndices.get(v2);
                    d[v_1][v_2] = directedGraph.getEdgeWeight(directedGraph.getEdge(v1, v2));
                    backtrace[v_1][v_2] = v_2;
                }
            }
        }

        // run fw alg
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double ik_kj = d[i][k] + d[k][j];
                    if (ik_kj < d[i][j]) {
                        d[i][j] = ik_kj;
                        backtrace[i][j] = backtrace[i][k];
                    }
                }
            }
        }
    }

    /**
     * Get the length of a shortest path.
     *
     * @param a first vertex
     * @param b second vertex
     *
     * @return shortest distance between a and b
     */
    public double shortestDistance(V a, V b)
    {
        lazyCalculateMatrix();

        return d[vertexIndices.get(a)][vertexIndices.get(b)];
    }

    /**
     * @return the diameter (longest of all the shortest paths) computed for the graph. If the graph
     *         is vertexless, return 0.0.
     */
    public double getDiameter()
    {
        lazyCalculateMatrix();

        if (Double.isNaN(diameter)) {
            diameter = 0.0;
            int n = vertices.size();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!Double.isInfinite(d[i][j]) && (d[i][j] > diameter)) {
                        diameter = d[i][j];
                    }
                }
            }
        }
        return diameter;
    }

    /**
     * Get the shortest path between two vertices.
     *
     * @param a from vertex
     * @param b to vertex
     *
     * @return the path, or null if none found
     */
    public GraphPath<V, E> getShortestPath(V a, V b)
    {
        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == -1) { // No path exists
            return null;
        }

        // Reconstruct the path
        List<V> pathVertexList = new ArrayList<>();
        pathVertexList.add(a);
        List<E> edges = new ArrayList<>();
        int u = v_a;
        while (u != v_b) {
            int v = backtrace[u][v_b];
            edges.add(graph.getEdge(vertices.get(u), vertices.get(v)));
            pathVertexList.add(vertices.get(v));
            u = v;
        }
        return new GraphWalk<>(graph, a, b, pathVertexList, edges, d[v_a][v_b]);
    }

    /**
     * Get the shortest path between two vertices as a list of vertices.
     * 
     * @param a from vertex
     * @param b to vertex
     * @return the path, or null if none found
     */
    public List<V> getShortestPathAsVertexList(V a, V b)
    {
        lazyCalculateMatrix();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == -1) { // No path exists
            return null;
        }

        // Reconstruct the path
        List<V> pathVertexList = new ArrayList<>();
        pathVertexList.add(a);
        int u = v_a;
        while (u != v_b) {
            int v = backtrace[u][v_b];
            pathVertexList.add(vertices.get(v));
            u = v;
        }
        return pathVertexList;
    }

    /**
     * Get shortest paths from a vertex to all other vertices in the graph.
     *
     * @param v the originating vertex
     *
     * @return List of paths
     */
    public List<GraphPath<V, E>> getShortestPaths(V v)
    {
        lazyCalculatePaths();
        return Collections.unmodifiableList(paths.get(v));
    }

    /**
     * Get all shortest paths in the graph.
     *
     * @return List of paths
     */
    public List<GraphPath<V, E>> getShortestPaths()
    {
        lazyCalculatePaths();
        List<GraphPath<V, E>> allPaths = new ArrayList<>();
        for (List<GraphPath<V, E>> pathSubset : paths.values()) {
            allPaths.addAll(pathSubset);
        }

        return allPaths;
    }

    /**
     * Returns the first hop, i.e., the second node on the shortest path from a to b. Lookup time is
     * O(1). If the shortest path from a to b is a,c,d,e,b, this method returns c. If the next
     * invocation would query the first hop on the shortest path from c to b, vertex d would be
     * returned, etc. This method is computationally cheaper than
     * getShortestPathAsVertexList(a,b).get(0);
     * 
     * @param a source vertex
     * @param b target vertex
     * @return next hop on the shortest path from a to b, or null when there exists no path from a
     *         to b.
     */
    public V getFirstHop(V a, V b)
    {
        lazyCalculatePaths();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == -1) // No path exists
            return null;
        else
            return vertices.get(backtrace[v_a][v_b]);
    }

    /**
     * Returns the last hop, i.e., the second to last node on the shortest path from a to b. Lookup
     * time is O(1). If the shortest path from a to b is a,c,d,e,b, this method returns e. If the
     * next invocation would query the next hop on the shortest path from c to e, vertex d would be
     * returned, etc. This method is computationally cheaper than
     * getShortestPathAsVertexList(a,b).get(shortestPathListSize-1);. The first invocation of this
     * method populates a last hop matrix.
     * 
     * @param a source vertex
     * @param b target vertex
     * @return last hop on the shortest path from a to b, or null when there exists no path from a
     *         to b.
     */
    public V getLastHop(V a, V b)
    {
        lazyCalculatePaths();

        int v_a = vertexIndices.get(a);
        int v_b = vertexIndices.get(b);

        if (backtrace[v_a][v_b] == -1) // No path exists
            return null;
        else {
            this.populateLastHopMatrix();
            return vertices.get(lastHopMatrix[v_a][v_b]);
        }
    }

    /**
     * Populate the last hop matrix, using the earlier computed backtrace matrix.
     */
    private void populateLastHopMatrix()
    {
        if (lastHopMatrix != null)
            return;

        // Initialize matrix
        lastHopMatrix = new int[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++)
            Arrays.fill(lastHopMatrix[i], -1);

        // Populate matrix
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (i == j || lastHopMatrix[i][j] != -1 || backtrace[i][j] == -1)
                    continue;

                // Reconstruct the path from i to j
                List<Integer> pathIndexList = new ArrayList<>();
                pathIndexList.add(i);
                int u = i, v;
                while (u != j) {
                    v = backtrace[u][j];
                    pathIndexList.add(v);
                    u = v;
                }
                // Iterate over the path, setting the lastHopMatrix from i to path[v] equal to
                // path[v-1], for all v=1...pathLength.
                for (int w = 0; w < pathIndexList.size() - 1; w++) {
                    u = pathIndexList.get(w);
                    v = pathIndexList.get(w + 1);
                    lastHopMatrix[i][v] = u;
                }
            }
        }
    }

    /**
     * Calculate the shortest paths (not done per default) TODO: This method can be optimized.
     * Instead of calculating each path individidually, use a constructive method. TODO: I.e. if we
     * have a shortest path from i to j: [i,....j] and we know that the shortest path from j to k,
     * we can simply glue the paths together to obtain the shortest path from i to k
     */
    private void lazyCalculatePaths()
    {
        // already we have calculated it once.
        if (paths != null) {
            return;
        }

        lazyCalculateMatrix();

        paths = new LinkedHashMap<>();
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            V v_i = vertices.get(i);
            paths.put(v_i, new ArrayList<>());
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }

                V v_j = vertices.get(j);

                GraphPath<V, E> path = getShortestPath(v_i, v_j);

                if (path != null) {
                    paths.get(v_i).add(path);
                    nShortestPaths++;
                }
            }
        }
    }
}

// End FloydWarshallShortestPaths.java
