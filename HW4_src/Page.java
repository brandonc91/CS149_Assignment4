import java.util.*;


// NEED TO DO LFU


/**
 * Simulate the paging algorithms FIFO, LFU, LRU, and Random Pick.
 * Spring 2014, CS149-02 Mak
 * @author HotCheetOS
 */

public class Page {
	static final int pageNumber = 4; // 4 pages frames
	static final int pageReference = 10; // 10 pages total (0-9)
	static final int totalPageReference = 100; // 100 page references each run time (5 total)
	static final boolean printTable = true;
	
	protected static int[] randomGen() {
		Random random = new Random();
		int[] referenceArray = new int[totalPageReference];
		for (int i = 0; i < totalPageReference; i++) {
			if (i == 0) {
				referenceArray[i] = random.nextInt(pageReference);
			}
			else {
				int deltaI = 0;
				int prevReference = referenceArray[i-1];
				if (prevReference >= 0 && prevReference < 7) {
					if (random.nextDouble() <= 0.7){
						deltaI = random.nextInt(3) - 1;
						referenceArray[i] = Math.abs((prevReference + deltaI) % 9); 
					}
					else {
						deltaI = random.nextInt(pageReference);
						referenceArray[i] = Math.abs((prevReference + deltaI) % 9); 
					}
				}
				else {
					deltaI = random.nextInt(8) + 2;
					referenceArray[i] = Math.abs((prevReference + deltaI) % 9);
				}
			}
		}
		return referenceArray;
	}
	
	/**
	 * Simulate the FIFO Page Replacement Algorithm
	 * @param referenceArrayy Array with 100 references 
	 * @return Hit ratio of FIFO
	 */
	protected static double FIFO(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0;
		int faultCount = 0;
		
		// To store the simulation results at each time slices
		int[] simTable = new int[pageNumber];
		
		// Initialize all elements of the simulation table to -1
		for (int i = 0; i < pageNumber; i++) {
			simTable[i] = -1;
		}
		for (int i = 0; i < totalPageReference; i++) {
			int position = faultCount % pageNumber;
			boolean isHit = checkHit(referenceArray[i], simTable);
			
			if (isHit) {
				hitCount++;
				int hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					if (simTable[j] == referenceArray[i]) {
						hitPos = j;
						break;
					}
				}
				printSimulation(i, simTable, isHit, referenceArray[i], simTable[hitPos]);
				continue;
			}
			int evicted = simTable[position];
			simTable[position] = referenceArray[i];
			printSimulation(i, simTable, isHit, referenceArray[i], evicted);
			faultCount++;
		}
		
		double hitRatio =  (double) hitCount / (double) totalPageReference;
		return hitRatio;
	}
	
	// when implementing, change method to: protected static double LFU(int[] referenceArray) {
	protected static void LFU(int[] referenceArray) {
		// need to implement
		System.out.println("LFU code");
	}
	protected static double LRU(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0;
		
		//To store the simulation results at each time slices
		int[] simTable = new int[pageNumber];
		
		//To store used record
		int[] usedTime = new int[pageNumber];
		
		for (int i = 0; i < pageNumber; i++) {
			simTable[i] = -1;
			usedTime[i] = -1;
		}
		
		for (int i = 0; i < totalPageReference; i++) {
			boolean isHit = checkHit(referenceArray[i], simTable);
			
			if (isHit) {
				hitCount++;
				int hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					if (simTable[j] == referenceArray[i]) {
						hitPos = j;
						break;
					}
				}
				usedTime[hitPos] = i;
				printSimulation(i, simTable, isHit, referenceArray[i], simTable[hitPos]);
				continue;
			}
			int position = 0;
			
			for (int j = 0; j < pageNumber; j++) {
				if (usedTime[position] > usedTime[j]) {
					position = j;
				}
			}
			int evicted = simTable[position];
			simTable[position] = referenceArray[i];
			usedTime[position] = i;
			printSimulation(i, simTable, isHit, referenceArray[i], evicted);
		}
		
		double hitRatio = (double) hitCount / (double) totalPageReference;
		return hitRatio;
	}
	
	
	protected static double Random(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0;
		Random random = new Random();
		
		//To store the simulation results at each time slices
		int[] simTable = new int[pageNumber];
		for (int i = 0; i < pageNumber; i++) {
			simTable[i] = -1;
		}
		
		for (int i = 0; i < totalPageReference; i++) {
			boolean isHit = checkHit(referenceArray[i], simTable);
			
			if (isHit) {
				hitCount++;
				int hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					if (simTable[j] == referenceArray[i]) {
						hitPos = j;
						break;
					}
				}
				printSimulation(i, simTable, isHit, referenceArray[i], simTable[hitPos]);
				continue;
			}
			int position = 0;
			int simTableCheck = 0;
			while (simTableCheck < pageNumber) {
				if(simTable[simTableCheck] == -1) break;
				simTableCheck++;
			}
			
			if (simTableCheck < pageNumber) {
				position = simTableCheck;
			}
			else {
				position = random.nextInt(pageNumber);
			}
			int evicted = simTable[position];
			simTable[position] = referenceArray[i];
			printSimulation(i, simTable, isHit, referenceArray[i], evicted);
		}
		
		double hitRatio = (double) hitCount / (double) totalPageReference;
		return hitRatio;
	}
	
	private static void printSimulation(int time, int[] simTable, boolean isHit, int curRef, int evicted) {
		if (!printTable) {
			return;
		}
		if (simTable.length == pageNumber) {
			System.out.print(time+"\t\t");
			
			for (int i = 0; i < pageNumber; i++) {
				System.out.print(simTable[i]+"\t\t");
			}
			if (isHit) {
				System.out.print("Hit\t\t");
			} 
			else {
				System.out.print("Fault\t\t");
			}
			System.out.println(curRef + "\t\t" + evicted);
		}
		else {
			System.out.println("This is not the Simulation Table.");
		}
	}
	
	private static boolean checkHit(int ref, int[] simTable) {
		for (int i = 0; i < simTable.length; i++) {
			if (ref == simTable[i]) {
				return true;
			}
		}
		return false;
	}
	
	private static double getAverage(double[] array) {
		double result = 0;
		
		for (int i = 0; i < array.length; i++) {
			result += array[i];
		}
		result = result / array.length;
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println("-1 indicates empty page frame");
		final int SIM_TIME = 5;
		int counter;
		
		double[] fifoArray = new double[5];
		double[] lfuArray = new double[5];
		double[] lruArray = new double[5];
		double[] randomArray = new double[5];
		
		counter = 1;
		System.out.print("\nFIFO Simulation\n------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < SIM_TIME; i++) {
			System.out.print("\n---------- Run " + counter + " ----------");
			int[] referenceArray = randomGen();
			fifoArray[i] = FIFO(referenceArray);
			counter++;
		}
		System.out.println("\n===================================================================================================================");
		
		counter = 1;
		System.out.print("\nLFU Simulation\n------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < SIM_TIME; i++) {
			System.out.print("\n---------- Run " + counter + " ----------");
			int[] referenceArray = randomGen();
			counter++;
			// lfuArray[i] = LFU(referenceArray); // uncomment after implementation
		}
		System.out.println("\nNot yet implemented"); // delete after implementation
		System.out.println("\n===================================================================================================================");
		
		counter = 1;
		System.out.print("LRU Simulation\n------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < SIM_TIME; i++) {
			System.out.print("\n---------- Run " + counter + " ----------");
			int[] referenceArray = randomGen();
			lruArray[i] = LRU(referenceArray);
			counter++;
		}
		System.out.println("\n===================================================================================================================");
		
		counter = 1;
		System.out.print("Random Simulation\n------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < SIM_TIME; i++) {
			System.out.print("\n---------- Run " + counter + " ----------");
			int[] referenceArray = randomGen();
			randomArray[i] = Random(referenceArray);
			counter++;
		}
		System.out.println("\n===================================================================================================================");
		
		// calculate averages
		double fifoAvg = getAverage(fifoArray);
		double lfuAvg = getAverage(lfuArray);
		double lruAvg = getAverage(lruArray);
		double randomAvg = getAverage(randomArray);

		System.out.println("\nAverage times for algorithms ...");
		System.out.println("FIFO:\t\t" + fifoAvg);
		System.out.println("LFU:\t\t" + lfuAvg + " !! not implemented !!"); // not yet implemented
		System.out.println("LRU:\t\t" + lruAvg);
		System.out.print("Random Pick:\t" + randomAvg);
	}
}