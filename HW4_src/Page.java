import java.util.*;
// NEED TO DO LFU
// AND MFU

/**
 * Simulate the paging algorithms FIFO, LFU, LRU, MRU and Random Pick.
 * Spring 2014, CS149-02 Mak
 * @author HotCheetOS
 */

public class Page {
	static final int pageNumber = 4; // 4 pages frames
	static final int pageReference = 10; // 10 pages total (0-9)
	static final int totalPageReference = 15; // 100 page references each run time (5 total)
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
	 * Simulate the FIFO page replacement algorithm.
	 */
	protected static double FIFO(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0;
		int faultCount = 0;
		
		// To store the simulation results at each time slices
		int[] simulationTable = new int[pageNumber];
		
		// Initialize all elements of the simulation table to -1
		for (int i = 0; i < pageNumber; i++) {
			simulationTable[i] = -1;
		}
		for (int i = 0; i < totalPageReference; i++) {
			int position = faultCount % pageNumber;
			boolean isHit = checkHit(referenceArray[i], simulationTable);
			
			if (isHit) {
				hitCount++;
				int hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					if (simulationTable[j] == referenceArray[i]) {
						hitPos = j;
						break;
					}
				}
				printSimulation(i, simulationTable, isHit, referenceArray[i], simulationTable[hitPos]);
				continue;
			}
			int evicted = simulationTable[position];
			simulationTable[position] = referenceArray[i];
			printSimulation(i, simulationTable, isHit, referenceArray[i], evicted);
			faultCount++;
		}
		return (double) hitCount / (double) totalPageReference;
	}
	
	/**
	 * Simulate the LFU page replacement algorithm.
	 */
	// when implementing, change method to: protected static double LFU(int[] referenceArray) {
	protected static double LFU(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0, page, hitPos, position, evicted;
		boolean isEvicted = false;
		
		// To store the simulation results at each time slices
		int[] simulationTable = new int[pageNumber];
				
		// To store used record
		int[] usedTime = new int[pageNumber];
		
		// Initialize everything to -1
		for (int i = 0; i < pageNumber; i++) {
			simulationTable[i] = -1;
			usedTime[i] = 0;
		}
		
		for (int i = 0; i < totalPageReference; i++) {
			page = referenceArray[i];
			boolean isHit = checkHit(page, simulationTable);
			
			
			if (isHit) {
				hitCount++;
				hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					//Check if page to be added is already in the simTable
					if (page == simulationTable[j]) {
						hitPos = j;
						break;
					}
				}
				usedTime[hitPos]++;
				printSimulation(i, simulationTable, isHit, page, simulationTable[hitPos]);
				continue;
			}
			position = 0;
			//Check which one is least frequently used
			for (int j = 0; j < pageNumber; j++) {
				if (usedTime[j] < usedTime[position]) {
					position = j;
					isEvicted = true;
				}
			}
			if (position == 0) {
				isEvicted = true;
			}
			//Evict the least frequently used one.
			evicted = simulationTable[position];
			//Reset it's usedTime position to 0
			if (isEvicted && usedTime[position] != 0) {
				usedTime[position] = 0;
				isEvicted = false;
			}
			else {
				usedTime[position]++;
				isEvicted = false;
			}
			simulationTable[position] = page;
			printSimulation(i, simulationTable, isHit, page, evicted);
		}
		return (double) hitCount / (double) totalPageReference;
	}
	
	/**
	 * Simulate the MFU page replacement algorithm.
	 */
	protected static void MFU(int[] referenceArray) {
		// need to implement
		System.out.println("MFU code");
	}
	
	/**
	 * Simulate the LRU page replacement algorithm.
	 */
	protected static double LRU(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0;
		
		// To store the simulation results at each time slices
		int[] simulationTable = new int[pageNumber];
		
		// To store used record
		int[] usedTime = new int[pageNumber];
		
		for (int i = 0; i < pageNumber; i++) {
			simulationTable[i] = -1;
			usedTime[i] = -1;
		}
		
		for (int i = 0; i < totalPageReference; i++) {
			boolean isHit = checkHit(referenceArray[i], simulationTable);
			
			if (isHit) {
				hitCount++;
				int hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					if (simulationTable[j] == referenceArray[i]) {
						hitPos = j;
						break;
					}
				}
				usedTime[hitPos] = i;
				printSimulation(i, simulationTable, isHit, referenceArray[i], simulationTable[hitPos]);
				continue;
			}
			int position = 0;
			
			for (int j = 0; j < pageNumber; j++) {
				if (usedTime[position] > usedTime[j]) {
					position = j;
				}
			}
			int evicted = simulationTable[position];
			simulationTable[position] = referenceArray[i];
			usedTime[position] = i;
			printSimulation(i, simulationTable, isHit, referenceArray[i], evicted);
		}
		return (double) hitCount / (double) totalPageReference;
	}
	
	/**
	 * Simulate the Random Pick page replacement algorithm.
	 */
	protected static double Random(int[] referenceArray) {
		System.out.println("\nTime\t\tFrame_1\t\tFrame_2\t\tFrame_3\t\tFrame_4\t\tHit?\t\tPage_In\t\tEvicted");
		int hitCount = 0;
		Random random = new Random();
		
		// To store the simulation results at each time slices
		int[] simulationTable = new int[pageNumber];
		for (int i = 0; i < pageNumber; i++) {
			simulationTable[i] = -1;
		}
		
		for (int i = 0; i < totalPageReference; i++) {
			boolean isHit = checkHit(referenceArray[i], simulationTable);
			
			if (isHit) {
				hitCount++;
				int hitPos = 0;
				
				for (int j = 0; j < pageNumber; j++) {
					if (simulationTable[j] == referenceArray[i]) {
						hitPos = j;
						break;
					}
				}
				printSimulation(i, simulationTable, isHit, referenceArray[i], simulationTable[hitPos]);
				continue;
			}
			
			int position = 0;
			int simulationTableCheck = 0;
			while (simulationTableCheck < pageNumber) {
				if(simulationTable[simulationTableCheck] == -1) break;
				simulationTableCheck++;
			}
			
			if (simulationTableCheck < pageNumber) {
				position = simulationTableCheck;
			}
			else {
				position = random.nextInt(pageNumber);
			}
			int evicted = simulationTable[position];
			simulationTable[position] = referenceArray[i];
			printSimulation(i, simulationTable, isHit, referenceArray[i], evicted);
		}
		return (double) hitCount / (double) totalPageReference;
	}
	
	/**
	 * Print the simulation table
	 */
	private static void printSimulation(int time, int[] simulationTable, boolean isHit, int currentReference, int evicted) {
		if (!printTable) {
			return;
		}
		if (simulationTable.length == pageNumber) {
			System.out.print(time+"\t\t");
			
			for (int i = 0; i < pageNumber; i++) {
				System.out.print(simulationTable[i]+"\t\t");
			}
			if (isHit) {
				System.out.print("Hit\t\t");
			} 
			else {
				System.out.print("Fault\t\t");
			}
			System.out.println(currentReference + "\t\t" + evicted);
		}
		else {
			System.out.println("Failed to generate the simulation table.");
		}
	}
	
	/**
	 * Check if there is a page hit.
	 */
	private static boolean checkHit(int ref, int[] simulationTable) {
		for (int val : simulationTable) {
			if (ref == val)
				return true;
		}
		return false;
	}
	
	/**
	 * Calculate the average.
	 */
	private static double getAverage(double[] array) {
		double result = 0;
		for (double item : array) {
			result += item;
		}
		return result / array.length;
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
			lfuArray[i] = LFU(referenceArray); // uncomment after implementation
		}
		//System.out.println("\nNot yet implemented"); // delete after implementation
		System.out.println("\n===================================================================================================================");
		
		/*counter = 1;
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
		*/
		// calculate averages
		//double fifoAvg = getAverage(fifoArray);
		double lfuAvg = getAverage(lfuArray);
		//double lruAvg = getAverage(lruArray);
		//double randomAvg = getAverage(randomArray);

		System.out.println("\nAverage times for algorithms ...");
		//System.out.println("FIFO:\t\t" + fifoAvg);
		System.out.println("LFU:\t\t" + lfuAvg + " !! not implemented !!"); // not yet implemented
		//System.out.println("LRU:\t\t" + lruAvg);
		//System.out.print("Random Pick:\t" + randomAvg);
	}
}