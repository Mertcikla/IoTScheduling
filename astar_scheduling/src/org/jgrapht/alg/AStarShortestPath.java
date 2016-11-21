/*
 * (C) Copyright 2015-2016, by Joris Kinable, Jon Robison, Thomas Breitbart and Contributors.
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
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* shortest path
 * algorithm</a>. <a href="http://de.wikipedia.org/wiki/A*-Algorithmus">A* shortest path algorithm
 * german Wiki</a> . This class works for Directed and Undirected graphs, as well as Multi-Graphs
 * and Mixed-Graphs. It's ok if the graph changes in between invocations of the
 * {@link #getShortestPath(Object, Object, AStarAdmissibleHeuristic)} getShortestPath} method; no
 * new instance of this class has to be created. The heuristic is implemented using a FibonacciHeap
 * data structure to maintain the set of open nodes. However, there still exist several approaches
 * in literature to improve the performance of this heuristic which one could consider to implement.
 * Another issue to take into consideration is the following: given to candidate nodes, i, j to
 * expand, where f(i)=f(j), g(i)&gt;g(j), h(i)&lt;g(j), f(i)=g(i)+h(i), g(i) is the actual distance
 * from the source node to i, h(i) is the estimated distance from i to the target node. Usually a
 * depth-first search is desired, so ideally we would expand node i first. Using the FibonacciHeap,
 * this is not necessarily the case though. This could be improved in a later version.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Joris Kinable
 * @author Jon Robison
 * @author Thomas Breitbart
 * @since Aug, 2015
 */
public class AStarShortestPath<V, E>
{
    private final Graph<V, E> graph;

    // List of open nodes
    protected FibonacciHeap<V> openList;
    protected Map<V, FibonacciHeapNode<V>> vertexToHeapNodeMap;

    // List of closed nodes
    protected Set<V> closedList;

    // Mapping of nodes to their g-scores (g(x)).
    protected Map<V, Double> gScoreMap;

    // Predecessor map: mapping of a node to an edge that leads to its
    // predecessor on its shortest path towards the targetVertex
    protected Map<V, E> cameFrom;

    // Reference to the admissible heuristic
    protected AStarAdmissibleHeuristic<V> admissibleHeuristic;

    // Counter which keeps track of the number of expanded nodes
    protected int numberOfExpandedNodes;

    /**
     * Create a new instance of the A* shortest path algorithm.
     * 
     * @param graph the input graph
     */
    public AStarShortestPath(Graph<V, E> graph)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null!");
        }
        this.graph = graph;
    }

    /**
     * Initializes the data structures
     *
     * @param admissibleHeuristic admissible heuristic
     */
    private void initialize(AStarAdmissibleHeuristic<V> admissibleHeuristic)
    {
        this.admissibleHeuristic = admissibleHeuristic;
        openList = new FibonacciHeap<>();
        vertexToHeapNodeMap = new HashMap<>();
        closedList = new HashSet<>();
        gScoreMap = new HashMap<>();
        cameFrom = new HashMap<>();
        numberOfExpandedNodes = 0;
    }

    /**
     * Calculates (and returns) the shortest path from the sourceVertex to the targetVertex. Note:
     * each time you invoke this method, the path gets recomputed.
     *
     * @param sourceVertex source vertex
     * @param targetVertex target vertex
     * @param admissibleHeuristic admissible heuristic which estimates the distance from a node to
     *        the target node.
     *
     * @return the shortest path from sourceVertex to targetVertex
     */
    public GraphPath<V, E> getShortestPath(
        V sourceVertex, V targetVertex, AStarAdmissibleHeuristic<V> admissibleHeuristic)
    {
        if (!graph.containsVertex(sourceVertex) || !graph.containsVertex(targetVertex)) {
            throw new IllegalArgumentException(
                "Source or target vertex not contained in the graph!");
        }

        this.initialize(admissibleHeuristic);
        gScoreMap.put(sourceVertex, 0.0);
        FibonacciHeapNode<V> heapNode = new FibonacciHeapNode<>(sourceVertex);
        openList.insert(heapNode, 0.0);
        vertexToHeapNodeMap.put(sourceVertex, heapNode);

        do {
            FibonacciHeapNode<V> currentNode = openList.removeMin();

            // Check whether we reached the target vertex
            if (currentNode.getData().equals(targetVertex)) {
                // Build the path
                return this.buildGraphPath(sourceVertex, targetVertex, currentNode.getKey());
            }

            // We haven't reached the target vertex yet; expand the node
            expandNode(currentNode, targetVertex);
            closedList.add(currentNode.getData());
        } while (!openList.isEmpty());

        // No path exists from sourceVertex to TargetVertex
        return null;
    }

    private void expandNode(FibonacciHeapNode<V> currentNode, V endVertex)
    {
        numberOfExpandedNodes++;

        Set<E> outgoingEdges = null;
        if (graph instanceof UndirectedGraph) {
            outgoingEdges = graph.edgesOf(currentNode.getData());
        } else if (graph instanceof DirectedGraph) {
            outgoingEdges = ((DirectedGraph<V, E>) graph).outgoingEdgesOf(currentNode.getData());
        }

        for (E edge : outgoingEdges) {
            V successor = Graphs.getOppositeVertex(graph, edge, currentNode.getData());
            if ((successor == currentNode.getData()) || closedList.contains(successor)) { // Ignore
                                                                                          // self-loops
                                                                                          // or
                                                                                          // nodes
                                                                                          // which
                                                                                          // have
                                                                                          // already
                                                                                          // been
                                                                                          // expanded
                continue;
            }

            double gScore_current = gScoreMap.get(currentNode.getData());
            double tentativeGScore = gScore_current + graph.getEdgeWeight(edge);

            if (!vertexToHeapNodeMap.containsKey(successor)
                || (tentativeGScore < gScoreMap.get(successor)))
            {
                cameFrom.put(successor, edge);
                gScoreMap.put(successor, tentativeGScore);

                double fScore =
                    tentativeGScore + admissibleHeuristic.getCostEstimate(successor, endVertex);
                if (!vertexToHeapNodeMap.containsKey(successor)) {
                    FibonacciHeapNode<V> heapNode = new FibonacciHeapNode<>(successor);
                    openList.insert(heapNode, fScore);
                    vertexToHeapNodeMap.put(successor, heapNode);
                } else {
                    openList.decreaseKey(vertexToHeapNodeMap.get(successor), fScore);
                }
            }
        }
    }

    /**
     * Builds the graph path
     *
     * @param startVertex starting vertex of the path
     * @param targetVertex ending vertex of the path
     * @param pathLength length of the path
     *
     * @return the shortest path from startVertex to endVertex
     */
    private GraphPath<V, E> buildGraphPath(V startVertex, V targetVertex, double pathLength)
    {
        List<E> edgeList = new ArrayList<>();
        List<V> vertexList = new ArrayList<>();
        vertexList.add(targetVertex);

        V v = targetVertex;
        while (v != startVertex) {
            edgeList.add(cameFrom.get(v));
            v = Graphs.getOppositeVertex(graph, cameFrom.get(v), v);
            vertexList.add(v);
        }
        Collections.reverse(edgeList);
        Collections.reverse(vertexList);
        return new GraphWalk<>(graph, startVertex, targetVertex, vertexList, edgeList, pathLength);
    }

    /**
     * Returns how many nodes have been expanded in the A* search procedure in its last invocation.
     * A node is expanded if it is removed from the open list.
     *
     * @return number of expanded nodes
     */
    public int getNumberOfExpandedNodes()
    {
        return numberOfExpandedNodes;
    }
}

// End AStarShortestPath.java
