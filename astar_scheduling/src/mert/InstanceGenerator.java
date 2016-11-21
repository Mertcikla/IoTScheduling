package mert;

import java.util.Arrays;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

public class InstanceGenerator {

	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g;
	double fieldLength = 100;
	static double fieldWidth = 5;
	static double sensorRange = 30;
	static int m = 10;
	static int k = 1;
	static int avgConsumption=10;
	Sensor sourceSink;
	Sensor destSink;

	InstanceGenerator() {

		g = new ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		sourceSink = new Sensor();
		destSink = new Sensor();

		sourceSink.id = 0;
		sourceSink.depth = 0;
		sourceSink.xCoord = 0;
		sourceSink.yCoord = fieldWidth / 2;
		sourceSink.range = sensorRange;
		destSink.id = m + 2;
		destSink.depth = m + 1;
		destSink.xCoord = fieldLength;
		destSink.yCoord = fieldWidth / 2;
		destSink.range = sensorRange;

		System.out.println("generate");
		generateSensors();

	}

	public void generateSensors() {

		g.addVertex(sourceSink);
		g.addVertex(destSink);

		for (int i = 0; i < m; i++) {
			double xrv = Math.random() * fieldLength;
			double yrv = Math.random() * fieldWidth;
			Sensor s = new Sensor();
			s.xCoord = xrv;
			s.yCoord = yrv;
			s.range = sensorRange;
			s.id = i + 1;
			s.depth = (int) xrv;
			g.addVertex(s);
		}
		Sensor[] vSet = g.vertexSet().toArray(new Sensor[m]);
		Sensor s1, s2;
		DefaultWeightedEdge edge;
		double distance;

		for (int i = 0; i < m + 2; i++) {
			s1 = vSet[i];
			for (int j = 0; j < m + 2; j++) {
				if (i != j) {
					s2 = vSet[j];
					if (s1.xCoord < s2.xCoord) {
						distance = Math.sqrt((s1.xCoord - s2.xCoord) * (s1.xCoord - s2.xCoord)
								+ (s1.yCoord - s2.yCoord) * (s1.yCoord - s2.yCoord));
						if (distance <= 30) {
							edge = g.addEdge(s1, s2);
							g.setEdgeWeight(edge, s2.currentBattery);
						}
					}
				}
			}
		}
		Arrays.sort(vSet);
		for (int i = 0; i < m + 2; i++)
			System.out.println(vSet[i].xCoord);

	}

	static GraphPath<Sensor, DefaultWeightedEdge> pathfinder(ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g,
			Sensor source, Sensor destination, AStarAdmissibleHeuristic<Sensor> heuristic) {
		AStarShortestPath<Sensor, DefaultWeightedEdge> aStar = new AStarShortestPath<Sensor, DefaultWeightedEdge>(g);
		return aStar.getShortestPath(source, destination, heuristic);

	}

	public static void main(String[] args) {
		InstanceGenerator instance = new InstanceGenerator();
		
		DistanceHeuristic h = null;
		ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> go = new ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		go=instance.g;
		for(int i=0;i<k;i++){
			@SuppressWarnings("unchecked")
			GraphPath<Sensor, DefaultWeightedEdge> path=pathfinder(go,instance.sourceSink,instance.destSink,h);
			if(path==null){
				System.out.println("cant find path");
				return;
			}
				
			List<Sensor> l = path.getVertexList();
			int s =l.size();
			while(s!=1){
				s--;
				go.removeVertex(l.get(s));
			}
			
		}
	}
	public abstract class DistanceHeuristic implements  AStarAdmissibleHeuristic<Sensor>{

		public double getCostEstimate(Sensor s1, Sensor s2) {

			return (s1.xCoord-s2.xCoord)*avgConsumption;
		}
		
	}
}
