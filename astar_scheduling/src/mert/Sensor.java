package mert;

import java.util.List;

public class Sensor implements Comparable<Sensor> {
	float currentBattery;
	double xCoord;
	double yCoord;
	double range;
	int depth;
	int id;
	boolean isActive;
	List<Sensor> nodesInRange;
	
	Sensor(){
		isActive=false;
		currentBattery=1;
	}
	public String toString(){
		return "id: "+Integer.toString(id)+" depth: "+Integer.toString(depth);
	}
	@Override
	public int compareTo(Sensor o) {
		// TODO Auto-generated method stub
		return (int) (xCoord-o.xCoord);
	}
}
