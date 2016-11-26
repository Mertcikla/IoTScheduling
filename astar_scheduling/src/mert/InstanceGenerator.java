package mert;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.orthogonal.mxOrthogonalLayout;
import com.mxgraph.layout.orthogonal.model.mxPointPair;
import com.mxgraph.swing.mxGraphComponent;

public class InstanceGenerator extends JFrame {

	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g;
	ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g0;

	double fieldLength = 100;
	static double fieldWidth = 5;
	static double sensorRange = 30;
	static int m = 300; // Number of sensors
	static int k = 1; // Number of paths
	static int totalCycle = 200;
	static int avgConsumption = 10;
	Sensor sourceSink;
	Sensor destSink;

	private static final long serialVersionUID = 2202072534703043194L;
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
	private JGraphXAdapter<Sensor, DefaultWeightedEdge> graphAdapter;

	public InstanceGenerator() {
	

		g = new ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		g0 = new ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge>(DefaultWeightedEdge.class);

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

		System.out.println("generate");
		
		generateSensors();
		cycle();
		
		
		/*
		JFrame frame = new JFrame("demo");
		graphAdapter = new JGraphXAdapter<Sensor, DefaultWeightedEdge>(this.g0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mxGraphLayout layout = new mxOrthogonalLayout(graphAdapter);
        mxPointPair pointPair = new mxPointPair();
        layout.setVertexLocation(sourceSink,fieldLength,fieldWidth);
        layout.execute(graphAdapter.getDefaultParent());

        frame.add(new mxGraphComponent(graphAdapter));

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        */
		System.out.println("frame set");

		
		

	}

	private void cycle() {
		for (int c = 0; c < totalCycle; c++) {
			System.out.println("%%%%%%%%%%%%%%%%%%%%  CYCLE "+c+"  %%%%%%%%%%%%%%%%%%%%");
			this.g0 = this.g;
			for (int i = 0; i < k; i++) {
				GraphPath<Sensor, DefaultWeightedEdge> path = pathfinder(this.g0, this.sourceSink,
						this.destSink);
				if (path == null) {
					System.out.println("cant find path k: "+i+" at cycle: "+c);
					Sensor[] resultState = this.g.vertexSet().toArray(new Sensor[m+2]);
					for (int h = 0; h < m+1; h++) {
						System.out.print("id: "+h+" ");
						System.out.print("current battery: ");
						System.out.print(resultState[h].currentBattery);
						System.out.println();
						}
					return;
				}

				List<Sensor> l = path.getVertexList();
				List<Sensor> removedList;
				int pathItr = l.size();
				while (pathItr != 1) {
					pathItr--;
					Sensor s = l.get(pathItr);
					System.out.println(s.xCoord);
					if (s.id != 0 && s.id != m + 2) {
						System.out.println("old lvl: "+s.currentBattery);
					    double energyCost=((l.get(pathItr+1).xCoord-s.xCoord)*(l.get(pathItr+1).xCoord-s.xCoord))/(fieldLength*fieldLength);
						s.currentBattery-=energyCost;
						System.out.println("new lvl: "+s.currentBattery);
						this.g0.removeVertex(s);
						if(s.currentBattery<=0){
							System.out.println();
							System.out.print(s.currentBattery);
							System.out.print(" removed");
							System.out.println();
							
						}
					}
				}

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
							g.setEdgeWeight(edge, s2.currentBattery);
						}
					}
				}
			}
		}
		Arrays.sort(vSet);

	}

	public GraphPath<Sensor, DefaultWeightedEdge> pathfinder(
			ListenableDirectedWeightedGraph<Sensor, DefaultWeightedEdge> g, Sensor source, Sensor destination) {
		DistanceHeuristic h = new DistanceHeuristic();
		ModifiedAStar<Sensor, DefaultWeightedEdge> aStar = new ModifiedAStar<Sensor, DefaultWeightedEdge>(g);
		return aStar.getShortestPath(source, destination, h);

	}

	public static class DistanceHeuristic implements AStarAdmissibleHeuristic<Sensor> {

		public double getCostEstimate(Sensor s1, Sensor s2) {

			return Math.abs(s1.xCoord - s2.xCoord);
		}

	}


	public static void main(String[] args) {SwingUtilities.invokeLater(new Runnable() {
        public void run() {
        	new InstanceGenerator();
        }
    });
		

	}

	public void init(InstanceGenerator instance) {
		// create a JGraphT graph

		// create a visualization using JGraph, via an adapter


		// that's all there is to it!...
	}
}
