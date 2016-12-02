package mert;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.traverse.RandomWalkIterator;

public class RandomPath {
	RandomWalkIterator<Sensor, DefaultWeightedEdge> itr;
	GraphWalk<Sensor, DefaultWeightedEdge> walk;
	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g;
	List<Sensor> vertexList;

	public RandomPath(ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> graph, Sensor start) {
		this.g = graph;
		itr = new RandomWalkIterator<Sensor, DefaultWeightedEdge>(g, start);
		vertexList = new ArrayList<Sensor>();
		vertexList.add(start);
	}

	public GraphWalk<Sensor, DefaultWeightedEdge> findPath(Sensor end) {
		while (itr.hasNext()) {
			Sensor NextNode = (Sensor) itr.next();
			if (NextNode.currentBattery > 0 && NextNode.isActive==false)
				vertexList.add(NextNode);
			if (NextNode.id == end.id) {
				walk = new GraphWalk<Sensor, DefaultWeightedEdge>(this.g, vertexList, 0);
				return walk;
			}
		}
		return null;
	}
}
