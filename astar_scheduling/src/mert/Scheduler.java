package mert;

public class Scheduler {
	static int algorithm = 2; // 0 for novel, 1 for random
	static int k = 4;
	static int m = 200;
	static int r = 10;
	static int cycles = 3000;
	static int repetition = 100;
	static double avgFirstFailCycle = 0;
	static double avgEnergyRemaining = 0;
	static double avgFirstFailK = 0;
	static double minRemaining = 10000;
	static double maxRemaining = 0;
	static double minFailCycle = 10000;
	static double maxFailCycle = 0;
	static double[] histogramAvg ;

	public static void main(String[] args) {
		int u=10;
		while(u!=210){
				r=u;
				histogramAvg = new double[10];
				  avgFirstFailCycle = 0;
				  avgEnergyRemaining = 0;
				  avgFirstFailK = 0;
				  minRemaining = 10000;
				  maxRemaining = 0;
				  minFailCycle = 10000;
				  maxFailCycle = 0;
				
				InstanceGenerator IG = null;
				for (int i = 0; i < repetition; i++) {
					IG = new InstanceGenerator(m, k, r, algorithm, cycles);
					avgFirstFailCycle += IG.firstFailCycle;
					avgEnergyRemaining += IG.avgEnergyAtFirstFail;
					avgFirstFailK += IG.firstFailK;
//					System.out.println(IG.firstFailCycle);
//					System.out.println(IG.avgEnergyAtFirstFail);
//					System.out.println(IG.firstFailK);
					if (IG.firstFailCycle < minFailCycle)
						minFailCycle = IG.firstFailCycle;
					if (IG.firstFailCycle > maxFailCycle)
						maxFailCycle = IG.firstFailCycle;
					if (IG.avgEnergyAtFirstFail > maxRemaining)
						maxRemaining = IG.avgEnergyAtFirstFail;
					if (IG.avgEnergyAtFirstFail < minRemaining)
						minRemaining = IG.avgEnergyAtFirstFail;
				//	for(int j=0;j<10;j++)
				//		histogramAvg[j]+=IG.histogram[j];
					

				}
//				System.out.println("L=" + IG.fieldLength + " m=" + IG.m + " k=" + IG.k +" r="+r);

				System.out.print(maxFailCycle);
				System.out.print("\t "+minFailCycle);
				System.out.print("\t "+avgFirstFailCycle / repetition);

			//	for(int j =0 ;j<10 ;j++)
			//		System.out.print("\t" + histogramAvg[j]/repetition);
				System.out.println();
				u+=10;
			}
				
		}
		// Pathfinder
		// Sleep Rest

	}


