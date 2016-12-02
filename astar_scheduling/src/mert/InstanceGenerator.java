package mert;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

public class InstanceGenerator extends JFrame {

	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g;

	double fieldLength = 1000;
	double fieldWidth = 5;
	static double sensorRange = 100; // 802.11ay has max range 1000
	int m = 200; // Number of sensors
	int k = 1; // Number of paths
	int totalCycle = 1000;
	int retry = 1; // number of retries to find a path
	int avgConsumption = 10;
	int firstFailCycle;
	int fullBatterySensors;
	int firstFailK;
	double avgEnergyAtFirstFail;

	Sensor sourceSink;
	Sensor destSink;

	private static final long serialVersionUID = 2202072534703043194L;
	private static final Dimension DEFAULT_SIZE = new Dimension(830, 620);
	private JGraphXAdapter<Sensor, DefaultWeightedEdge> graphAdapter;

	public InstanceGenerator() {

		g = new ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		sourceSink = new Sensor();
		destSink = new Sensor();

		sourceSink.id = 0;
		sourceSink.depth = 0;
		sourceSink.xCoord = 0;
		sourceSink.yCoord = fieldWidth / 2;
		sourceSink.range = sensorRange;
		destSink.id = m + 2;

		destSink.xCoord = fieldLength;
		destSink.yCoord = fieldWidth / 2;
		destSink.range = sensorRange;

		generateSensors();
		cycle();

		// JFrame frame = new JFrame("demo"); graphAdapter = new
		// JGraphXAdapter<Sensor, DefaultWeightedEdge>(g);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		// mxCompactTreeLayout layout = new mxCompactTreeLayout(graphAdapter);
		// layout.setVertexLocation(sourceSink,fieldLength,fieldWidth);
		// layout.execute(graphAdapter.getDefaultParent());
		//
		// frame.add(new mxGraphComponent(graphAdapter));
		//
		// frame.pack(); frame.setLocationByPlatform(true);
		// frame.setVisible(true);
		//
		// System.out.println("frame set");

	}

	private void cycle() {
		double distance;
		for (int c = 0; c <= totalCycle; c++) {
		//	 System.out.println("%--------------% CYCLE " + (c+1) + " %--------------%");
			for (int i = 0; i < k; i++) {
				int r = retry;
				GraphPath<Sensor, DefaultWeightedEdge> path = null;
				while (r != 0) {
					r--;
					path = pathfinder(g, sourceSink, destSink);
					if (path == null && r == 0) {
						firstFailK = i;
						firstFailCycle = c;

						 System.out.println("cant find path k: " + i + " at cycle: " + c + " retry: " + r + "/" + retry);
						Sensor[] resultState = g.vertexSet().toArray(new Sensor[m]);
						double totalEnergy = 0;
						fullBatterySensors=0;
						for (int h = 2; h <= m + 1; h++) {
							totalEnergy += resultState[h].currentBattery;
							if(resultState[h].currentBattery==1)
								fullBatterySensors++;
						}
						
						avgEnergyAtFirstFail = totalEnergy / m;
						return;
					} else if(c==totalCycle){
						 System.out.println("cycles completed with k: " + i + " cycle: " + c);
							Sensor[] resultState = g.vertexSet().toArray(new Sensor[m]);
							double totalEnergy = 0;
							for (int h = 2; h <= m + 1; h++) {
								totalEnergy += resultState[h].currentBattery;
								if(resultState[h].currentBattery>=0.8)
									fullBatterySensors++;
							}
							
							avgEnergyAtFirstFail = totalEnergy / m;
							return;
						
					}
						else if (path != null)
						break;
				}

				List<Sensor> l = path.getVertexList();
				Sensor[] gl = g.vertexSet().toArray(new Sensor[m]);
		//		 System.out.println("Path Length: "+path.getLength());
				int pI = l.size() - 2;
				while (l.size() != 2) {
					Sensor s = gl[l.get(pI).id + 1];
					double energyCost =0.02 * Math.abs((l.get(pI - 1).xCoord - s.xCoord) / (sensorRange));
					s.currentBattery -= energyCost;
					
					if (s.currentBattery < 0)
						s.currentBattery = 0;
				//	 System.out.print(s.id + "(" + s.currentBattery + ") ->");
					s.isActive = true;

					gl[l.get(pI).id + 1] = s;
					l.remove(pI);
					pI--;

				}
		//		System.out.println();
			}
			Iterator<Sensor> vertexItr = g.vertexSet().iterator();
			while (vertexItr.hasNext()) {
				Sensor nextS = vertexItr.next();
				nextS.currentBattery-=0.0001;
				
				if (nextS.currentBattery > 0)
					nextS.isActive = false;
				else
					nextS.currentBattery=0;
			}
			Sensor s1,s2;
			
			Iterator<DefaultWeightedEdge> edgeItr = g.edgeSet().iterator();
			while (edgeItr.hasNext()) {
				DefaultWeightedEdge nextE = edgeItr.next();
				s1=g.getEdgeSource(nextE);
				s2=g.getEdgeTarget(nextE);
				double EdgeWeight = (1- s2.currentBattery);
				g.setEdgeWeight(nextE, EdgeWeight);
			}
		}

	}

	public void generateSensors() {
		destSink.depth = (int) destSink.xCoord;
		g.addVertex(sourceSink);
		g.addVertex(destSink);

		for (int i = 0; i < m + 1; i++) {
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
						if (distance <= sensorRange) {
							edge = g.addEdge(s1, s2);
							double EdgeWeight = (1- s2.currentBattery);
							g.setEdgeWeight(edge, EdgeWeight);
						}
					}
				}
			}
		}
		Arrays.sort(vSet);

	}

	public GraphPath<Sensor, DefaultWeightedEdge> pathfinder(
			ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> gr, Sensor source, Sensor destination) {
		DistanceHeuristic h = new DistanceHeuristic();
		ModifiedAStar<Sensor, DefaultWeightedEdge> aStar = new ModifiedAStar<Sensor, DefaultWeightedEdge>(gr);
		return aStar.getShortestPath(source, destination, h);

	}

	public static class DistanceHeuristic implements AStarAdmissibleHeuristic<Sensor> {

		public double getCostEstimate(Sensor s1, Sensor s2) {

			return 1-(Math.abs(s1.xCoord-s2.xCoord)/sensorRange);
		}

	}

	public void init(InstanceGenerator instance) {
		// create a JGraphT graph

		// create a visualization using JGraph, via an adapter

		// that's all there is to it!...
	}
}
