package mert;

import javax.swing.SwingUtilities;

public class Scheduler {
	static int algorithm=1; //0 for novel, 1 for random
	static int k=5;
	static int m=300;
	static int r=100;
	static int cycles=2000;
	static int repetition = 2;
	static double avgFirstFailCycle = 0;
	static double avgEnergyRemaining = 0;
	static double avgFirstFailK = 0;
	static double minRemaining =10000;
	static double maxRemaining =0;
	static double minFailCycle = 1000;
	static double maxFailCycle =0 ;
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				InstanceGenerator IG = null;
				for (int i = 0; i < repetition; i++) {
					IG = new InstanceGenerator(m,k,r,algorithm,cycles);
					avgFirstFailCycle += IG.firstFailCycle;
					avgEnergyRemaining += IG.avgEnergyAtFirstFail;
					avgFirstFailK += IG.firstFailK;
					System.out.println(IG.firstFailCycle);
					System.out.println(IG.avgEnergyAtFirstFail);
					System.out.println(IG.firstFailK);
					if(IG.firstFailCycle<minFailCycle)
						minFailCycle=IG.firstFailCycle;
					if(IG.firstFailCycle>maxFailCycle)
						maxFailCycle=IG.firstFailCycle;
					if(IG.avgEnergyAtFirstFail>maxRemaining)
						maxRemaining=IG.avgEnergyAtFirstFail;
					if(IG.avgEnergyAtFirstFail<minRemaining)
						minRemaining=IG.avgEnergyAtFirstFail;

				}
				System.out.println("L=" + IG.fieldLength + " m=" + IG.m + " k=" + IG.k);
				System.out.print(avgFirstFailCycle / repetition);
				System.out.print(" | "+ minFailCycle);
				System.out.print("/");
				System.out.println(maxFailCycle);


				System.out.print(avgEnergyRemaining / repetition);
				System.out.print(" | "+ minRemaining);
				System.out.print("/");
				System.out.println(maxRemaining);

				System.out.println(avgFirstFailK / repetition);
				
				
			}
		});

		// Pathfinder
		// Sleep Rest

	}

}
