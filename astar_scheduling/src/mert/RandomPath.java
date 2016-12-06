package mert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.traverse.RandomWalkIterator;

public class RandomPath {
	RandomWalkIterator<Sensor, DefaultWeightedEdge> itr;
	GraphWalk<Sensor, DefaultWeightedEdge> walk;
	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g;
	List<Sensor> vertexList;

	public RandomPath(ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g, Sensor start,Sensor destination) {
		walk =null;
		Sensor node;
		DefaultWeightedEdge e;
		List<Sensor> sensorList = new ArrayList<Sensor>();
		Sensor target;
		int index;
		node=start;
		sensorList.add(start);
		List<DefaultWeightedEdge> edges= new ArrayList<DefaultWeightedEdge>( g.outgoingEdgesOf(node));

	
		while(!edges.isEmpty()){
			index=(int)(Math.random()*(edges.size()-1));
			e=edges.get(index);
			target=g.getEdgeTarget(e);
			if(target==destination){
				sensorList.add(target);
				walk=new GraphWalk<Sensor, DefaultWeightedEdge>(g,sensorList, 0);
				return ;
			}
			else if(g.getEdgeWeight(e)>0 && target.isActive==false){
				sensorList.add(target);
				edges= new ArrayList<DefaultWeightedEdge>( g.outgoingEdgesOf(target));				
			}
			else
				edges.remove(index);
			
		}
				
		
	}

	
}

