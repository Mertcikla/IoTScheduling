package mert;

public class Experiment {
	 int k=1;
	 int m=150;
	 int r=100;
	 int c=1000;
	 int repetition = 100;
	 double avgFirstFailCycle = 0;
	 double avgEnergyRemaining = 0;
	 double avgFirstFailK = 0;
	 double[] remainingLevels;
	 double minRemaining =10000;
	 double maxRemaining =0;
	 double minFailCycle = 1000;
	 double maxFailCycle =0 ;
	public Experiment() {
		// TODO Auto-generated constructor stub
		remainingLevels = new double[c];
	}

}
