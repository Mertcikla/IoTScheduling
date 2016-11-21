package mert;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

public class InstanceGenerator  {
	
	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g;
	double fieldLength = 100;
	static double fieldWidth = 5;
	static double sensorRange = 30;
	static int m = 10;
	static int k = 1;
	Sink sourceSink;
	Sink destSink;

	InstanceGenerator() {


		g = new ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Sink sourceSink = new Sink();
		Sink destSink = new Sink();

		sourceSink.id = 0;
		sourceSink.depth = 0;
		sourceSink.xCoord = 0;
		sourceSink.yCoord = 25;
		sourceSink.k = k;
		sourceSink.next = destSink;

		destSink.id = 1;
		destSink.depth = m + 1;
		destSink.xCoord = 10000;
		destSink.yCoord = 25;
		destSink.k = k;
		System.out.println("generate");
		generateSensors();


	}

	public void generateSensors() {
		for (int i = 0; i < m; i++) {
			double xrv = Math.random() * fieldLength;
			double yrv = Math.random() * fieldWidth;
			Sensor s = new Sensor();
			s.xCoord = xrv;
			s.yCoord = yrv;
			s.range = sensorRange;
			s.id=i;
			g.addVertex(s);
		}
		Sensor[] vSet = g.vertexSet().toArray(new Sensor[m]);
		Sensor s1, s2;
		DefaultWeightedEdge edge;
		double distance;
		
		for (int i = 0; i < m; i++) {
			s1 = vSet[i];
			for (int j = 0; j < m; j++) {
				if (i != j) {
					s2 = vSet[j];
					if (s1.xCoord < s2.xCoord) {
						distance = Math.sqrt((s1.xCoord - s2.xCoord) * (s1.xCoord - s2.xCoord)
								+ (s1.yCoord - s2.yCoord) * (s1.yCoord - s2.yCoord));
						 System.out.println(distance);
						if (distance <= 30) {
							edge = g.addEdge(s1, s2);
							g.setEdgeWeight(edge, distance);
							System.out.println(distance);
						}
					}
				}
			}
		}
	}
}
