package mert;

import javax.swing.SwingUtilities;

public class Scheduler {
	int k;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				InstanceGenerator IG = null;
				int repetition = 50;
				double avgFirstFailCycle = 0;
				double avgEnergyRemaining = 0;
				double avgFirstFailK = 0;
				double minRemaining =10000;
				double maxRemaining =0;
				double minFailCycle = 1000;
				double maxFailCycle =0 ;
				for (int i = 0; i < repetition; i++) {
					IG = new InstanceGenerator();
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
