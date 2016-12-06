package mert;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.FibonacciHeap;
import org.jgrapht.util.FibonacciHeapNode;

public class MinHopAStar<V, E> {

	
	    private final Graph<V, E>  graph;

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
	    public MinHopAStar(Graph<V, E> graph)
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
	            if (currentNode.getData().equals(targetVertex) && ((Sensor)currentNode.getData()).isActive==false && ((Sensor)currentNode.getData()).currentBattery>0) {
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

	    private void expandNode(FibonacciHeapNode<V> currentNode, V endVertex )
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
	            if ((successor == currentNode.getData()) || closedList.contains(successor) || ((Sensor)successor).isActive==true || ((Sensor)successor).currentBattery<=0 ) { // Ignore
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
	            double tentativeGScore = gScore_current + 1;

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
