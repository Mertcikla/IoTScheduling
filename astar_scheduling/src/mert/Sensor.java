package mert;

import java.util.List;

public class Sensor {
	float currentBattery;
	double xCoord;
	double yCoord;
	double range;
	int depth;
	int id;
	List<Sensor> nodesInRange;
	
	Sensor(){
		currentBattery=100;
	}
	
}
