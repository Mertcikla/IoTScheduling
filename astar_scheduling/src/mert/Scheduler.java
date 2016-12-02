package mert;

import javax.swing.SwingUtilities;

public class Scheduler {
	int k;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				InstanceGenerator IG = null;
				int repetition = 1;
				double avgFirstFailCycle = 0;
				double avgEnergyRemaining = 0;
				double avgFirstFailK = 0;
				double avgFullBatterySensors = 0;
				for (int i = 0; i < repetition; i++) {
					IG = new InstanceGenerator();
					avgFirstFailCycle += IG.firstFailCycle;
					avgEnergyRemaining += IG.avgEnergyAtFirstFail;
					avgFirstFailK += IG.firstFailK;
					avgFullBatterySensors += IG.fullBatterySensors;
					System.out.println(IG.firstFailCycle);
					System.out.println(IG.avgEnergyAtFirstFail);
					System.out.println(IG.firstFailK);
					System.out.println(IG.fullBatterySensors);

				}
				System.out.println("L=" + IG.fieldLength + " m=" + IG.m + " k=" + IG.k);
				System.out.println(avgFirstFailCycle / repetition);
				System.out.println(avgEnergyRemaining / repetition);
				System.out.println(avgFirstFailK / repetition);
				System.out.println(avgFullBatterySensors / repetition);

			}
		});

		// Pathfinder
		// Sleep Rest

	}

}
