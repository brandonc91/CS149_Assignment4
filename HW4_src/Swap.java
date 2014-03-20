import java.io.*;
import java.util.*;

/**
 * FCFS to simulate the memory swapping algorithms First Fit, Next Fit, and Best Fit.
 * Spring 2014, CS149-02 Mak
 * @author HotCheetOS
 */
public class Swap {
	static int minPartitionSize = 4; // process partitions (4, 8, 16, 32MB)
	static int maxDuration = 5;    // process runtime duration (1-5 sec)
	static int memorySize = 100; // 100MB of main memory
	
	static int[] physicalMemory = new int[memorySize];
	static Queue<HashMap<String, Integer>> processList = new LinkedList<HashMap<String, Integer>>();
	static Queue<HashMap<String, Integer>> processListBackup = new LinkedList<HashMap<String, Integer>>();
	static ArrayList<HashMap<String, Integer>> runList = new ArrayList<HashMap<String, Integer>>();
	static ArrayList<HashMap<String, Integer>> holeList = new ArrayList<HashMap<String, Integer>>();
	
	static final int simulationTime = 60;
	static final int firstFit = 0x01;
	static final int nextFit = 0x02;
	static final int bestFit = 0x03;
	
	static File outputFile;
	

    public static int makeSize() {
        int sizeRangeMin = 0;
        int sizeRangeMax = 4;
        int sizes[] = {5, 11, 17, 31};
        int partitionSize = sizes[sizeRangeMin + (int) (Math.random() * sizeRangeMax)];
        return partitionSize;
    }

	private static void emptyAll() {
		for (int i = 0; i < memorySize; i++) { // empty memory
			physicalMemory[i] = -1;
		}
	
		processList.clear(); // empty processList
		runList.clear(); // empty run list
		holeList.clear(); // empty hole list
	}
	
    public static void processGenerator() {
        Random random = new Random();
        
        for (int i = 0; i < 200; i++) {
        	HashMap<String, Integer> process = new HashMap<String, Integer>();
        	int power = random.nextInt(4);
        	int partitionSize = makeSize();
        	int time = random.nextInt(maxDuration) + 1;
        	
            process.put("processName", i);
            process.put("totalRunTime", time);
            process.put("runTime", 0);
            process.put("partitionSize", partitionSize);
            processList.offer(process);
            processListBackup.offer(process);
        }
    }
    
    public static int fillEmptyMemory() {
    	int fillCount = 0;
    	int swapCount = 0;
    	int memoryLeft = memorySize;
    	boolean fillable = true;
    	
    	while (fillable) {
    		HashMap<String, Integer> process = processList.poll();
    		runList.add(process);
    		int pSize = process.get("partitionSize").intValue();
    		int originalFillCount = fillCount;
    		
    		while (fillCount < originalFillCount + pSize) {
    			physicalMemory[fillCount] = process.get("processName");
    			fillCount++;
    		}
    		memoryLeft = memoryLeft - pSize;
    		swapCount++;
    		
    		HashMap<String, Integer> nextP = processList.peek();
    		int nextPSize = nextP.get("partitionSize");
    		
    		if ((memoryLeft - nextPSize) <= 0) {
    			fillable = false;
    		}
    	}
    	return swapCount;
    }
    
    protected static void checkProcess() {
    	for (int i = 0; i < runList.size(); ) {
    		HashMap<String, Integer> p = runList.get(i);
    		int pRunLimit = p.get("totalRunTime");
    		int pRunTime = p.get("runTime");
    		
    		if (pRunTime < pRunLimit) {
    			i++;
    			continue;
    		}
    		else {
    			runList.remove(i);
    			
    			int pName = p.get("processName");
    			
    			for (int j = 0; j < memorySize; j++) {
    				if (physicalMemory[j] == pName) physicalMemory[j] = -1;
    			}
    		}
    	}
    }
    
    protected static void runProcess() {
    	for (HashMap<String, Integer> p : runList) {
    		int runTime = p.get("runTime");
    		runTime++;
    		p.put("runTime", runTime);
    	}
    }
    
    public static void findHoles(int type) {
    	holeList.clear();
    	int startPoint = 0;
    	int i;
    	
    	switch (type) {
	    	case firstFit:
	    		break;
	    		
	    	case nextFit:
	    		int previousProcessName = 0;
	    		for (i = 0; i < memorySize; i++) {
	    			if (previousProcessName < physicalMemory[i]) {
	    				previousProcessName = physicalMemory[i];
	    			}
	    		}
	    		int previousEnd = 0;
	    		for (i = memorySize - 1; i >= 0; i--) {
	    			if (physicalMemory[i] == previousProcessName) {
	    				previousEnd = i;
	    				break;
	    			}
	    		}
	    		startPoint = previousEnd;
	    		break;
	    		
	    	case bestFit:
	    		break;
	    		
	    	default: 
	    		break;
    	}
    	
    	for (i = startPoint; i < memorySize; i++) {
    		if (physicalMemory[i] == -1) {
    			for (int j = i; j < memorySize; j++) {
    				if (physicalMemory[j] > -1) {
    					int holeSize = j - i;
    					HashMap<String, Integer> hole = new HashMap<String, Integer>();
    					
    					hole.put("start", i);
    					hole.put("size", holeSize);
    					holeList.add(hole);
    					i = j;
    					break;
    				}
    				else if (j == (memorySize - 1) && physicalMemory[j] == -1) {
    					int holeSize = j - i + 1;
    					HashMap<String, Integer> hole = new HashMap<String, Integer>();
    					
    					hole.put("start", i);
    					hole.put("size", holeSize);
    					holeList.add(hole);
    					i = j;
    					break;
    				}
    			}
    		}
    	}
    }
    
    public static int fillHolesFirstFit() {
    	int swapCount = 0;
    	boolean fillable = true;
    	
    	while (fillable) {
    		//check if the first waiting process can fill into any holes
    		findHoles(firstFit);
    		HashMap<String, Integer> process = processList.peek();
    		int fitHoleCount = 0;
    		
    		while (fitHoleCount < holeList.size()) {
    			HashMap<String, Integer> h = holeList.get(fitHoleCount);
    			
    			if (process.get("partitionSize") <= h.get("size")) {
    				break;
    			}
    			fitHoleCount++;
    		}

    		if (fitHoleCount == holeList.size()) {
    			fillable = false;
    			continue;
    		}
    		
    		// To start holes by first fit rule
    		HashMap<String, Integer> hole = new HashMap<String, Integer>();
    		for (int i = 0; i < holeList.size(); i++) {
    			HashMap<String, Integer> h = holeList.get(i);
    			
    			if (process.get("partitionSize") <= h.get("size")) {
    				hole = h;
    				holeList.remove(i);
    				break;
    			}
    		}
    		runList.add(processList.poll());
    		int startPoint = hole.get("start");
    		int partitionSize = process.get("partitionSize");
    		
    		for (int i = startPoint; i < startPoint + partitionSize; i++) {
    			physicalMemory[i] = process.get("processName");
    		}
    		swapCount++;
    	}
    	return swapCount;
    }
    
    public static int fillHolesNextFit() {
    	int swapCount = 0;
    	boolean fillable = true;
    	
    	while (fillable) {
    		// check if the first waiting process can fill into any holes
    		findHoles(nextFit);
    		HashMap<String, Integer> process = processList.peek();
    		int fitHoleCount = 0;
    		
    		while (fitHoleCount < holeList.size()) {
    			HashMap<String, Integer> h = holeList.get(fitHoleCount);
    			if (process.get("partitionSize") <= h.get("size")) {
    				break;
    			}
    			fitHoleCount++;
    		}

    		if (fitHoleCount == holeList.size()) {
    			fillable = false;
    			continue;
    		}
    		
    		// To start holes by first fit rule
    		HashMap<String, Integer> hole = new HashMap<String, Integer>();
    		for (int i = 0; i < holeList.size(); i++) {
    			HashMap<String, Integer> h = holeList.get(i);
    			if (process.get("partitionSize") <= h.get("size")) {
    				hole = h;
    				holeList.remove(i);
    				break;
    			}
    		}
    		runList.add(processList.poll());
    		int startPoint = hole.get("start");
    		int pSize = process.get("partitionSize");
    		for (int i = startPoint; i < startPoint + pSize; i++) {
    			physicalMemory[i] = process.get("processName");
    		}
    		swapCount++;
    	}
    	return swapCount;
    }
    
    public static int fillHolesBestFit() {
    	int swapCount = 0; 
    	boolean fillable = true;
    	
    	while (fillable) {
    		// check if the first waiting process can fill into any holes
    		findHoles(bestFit);
    		HashMap<String, Integer> process = processList.peek();
    		int fitHoleCount = 0;
    		
    		while (fitHoleCount < holeList.size()) {
    			HashMap<String, Integer> h = holeList.get(fitHoleCount);
    			if (process.get("partitionSize") <= h.get("size")) {
    				break;
    			}
    			fitHoleCount++;
    		}
    		
    		if (fitHoleCount == holeList.size()) {
    			fillable = false;
    			continue;
    		}
    		
    		// To start holes by best fit rule
    		int fitSizeMin = 10000;
    		for (int i = 0; i < holeList.size(); i++) {
    			HashMap<String, Integer> h = holeList.get(i);
    			if (process.get("partitionSize") <= h.get("size")) {
    				if (fitSizeMin > h.get("size")) {
    					fitSizeMin = h.get("size");
    				}
    			}
    		}
    		
    		HashMap<String, Integer> hole = new HashMap<String, Integer>();
    		for (int i = 0; i < holeList.size(); i++) {
    			HashMap<String, Integer> h = holeList.get(i);
    			if (h.get("size") == fitSizeMin) {
    				hole = h;
    				holeList.remove(i);
    				break;
    			}
    		}
    		runList.add(processList.poll());
    		int startPoint = hole.get("start");
    		int pSize = process.get("partitionSize");
    		
    		for (int i = startPoint; i < startPoint + pSize; i++) {
    			physicalMemory[i] = process.get("processName");
    		}
    		swapCount++;
    	}
    	return swapCount;
    }
    
    public static int firstFit() {
    	emptyAll();
    	processGenerator();
    	int swapCount = fillEmptyMemory();
    	
    	for (int i = 0; i < simulationTime; i++) {
    		checkProcess();
    		runProcess();
    		swapCount += fillHolesFirstFit();
    		printMap();
    	}
    	return swapCount;
    }
    
    public static int nextFit() {
    	emptyAll();
    	processGenerator();
    	int swapCount = fillEmptyMemory();
    	
    	for (int i = 0; i < simulationTime; i++) {
    		checkProcess();
    		runProcess();
    		swapCount += fillHolesNextFit();
    		printMap();
    	}
    	return swapCount;
    	
    }
    
    public static int bestFit() {
    	emptyAll();
    	processGenerator();
    	int swapCount = fillEmptyMemory();
    	
    	for (int i = 0; i < simulationTime; i++) {
    		checkProcess();
    		runProcess();
    		swapCount += fillHolesBestFit();
    		printMap();
    	}
    	
    	return swapCount;
    }
    
    public static void printMap() {
    	for (int i = 0; i < memorySize; i++) {
			StringBuilder s = new StringBuilder();
    		if (physicalMemory[i] != -1) {
    			if (physicalMemory[i] < 10) {
    				s.append("|00" + physicalMemory[i] + "|");
    			}
    			else if (physicalMemory[i] >= 10 && physicalMemory[i] < 100) {
    				s.append("|0" + physicalMemory[i] + "|");
    			}
    			else {
    				s.append("|" + physicalMemory[i] + "|");
    			}
    		}
    		else {
    			s.append("|...|");
    		}
    		
    		if (i < memorySize - 1) {
				s.append("-");
			}
			else {
				s.append("");
			}
    		String finalString = s.toString();
    		System.out.print(finalString);
    	}
    	
    	// write output to a file
    	try {
    		
    		FileWriter fw = new FileWriter(outputFile.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for (int i = 0; i < memorySize; i++) {
	    		if (physicalMemory[i] != -1) {
	    			StringBuilder string = new StringBuilder();
	    			if (physicalMemory[i] < 10) {
	    				string.append("|00" + physicalMemory[i] + "|");
	    			}
	    			else if (physicalMemory[i] >= 10 && physicalMemory[i] < 100) {
	    				string.append("|0" + physicalMemory[i] + "|");
	    			}
	    			else {
	    				string.append("|" + physicalMemory[i] + "|");
	    			}
	    			
	    			if (i < memorySize - 1) {
	    				string.append("-");
	    			}
	    			else {
	    				string.append("");
	    			}
	    			String finalString = string.toString();
	    			bw.write(finalString);
	    			bw.flush();
	    		}
	    		else {
	    			if (i < memorySize - 1) {
	    				bw.write("|...|-");
	    			}
	    			else {
	    				bw.write("|...|");
	    			}
	    			bw.flush();
	    		}
	    	}
			bw.write("\n");
			
			bw.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	System.out.println();
    }
    
    public static void main(String[] args) {
    	FileWriter fw;
    	BufferedWriter bw;
    	
    	try {
    		outputFile = new File("swapOutput.txt");
		
	    	// if file does not exist, then create it
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			else {
				outputFile.delete();
				outputFile.createNewFile();
			}
			
			fw = new FileWriter(outputFile.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
    	
	    	final double SIM_COUNT = 5.0;
	    	int ffResult = 0;
	    	int nfResult = 0;
	    	int bfResult = 0;
	    	
	    	for (int i = 0; i < 5; i++) {
	    		System.out.println("\nFirst Fit Simulation: Time " + (i + 1));
	    		bw.write("\nFirst Fit Simulation: Time " + (i + 1) + "\n");
	    		bw.flush();
	    		ffResult += firstFit();
	    		
	    		System.out.println("\nNext Fit Simulation: Time " + (i + 1));
	    		bw.write("\nNext Fit Simulation: Time " + (i + 1) + "\n");
	    		bw.flush();
	    		nfResult += nextFit();
	    		
	    		System.out.println("\nBest Fit Simulation: Time " + (i + 1));
	    		bw.write("\nBest Fit Simulation: Time " + (i + 1) + "\n");
	    		bw.flush();
	    		bfResult += bestFit();
	    	}
	    	
	    	System.out.println("\nCalculated Simulation Results");
	    	bw.write("\nCalculated Simulation Results\n");
	    	bw.flush();
	    	
	    	double ffAvg = (double) ffResult / SIM_COUNT;
	    	double nfAvg = (double) nfResult / SIM_COUNT;
	    	double bfAvg = (double) bfResult / SIM_COUNT;
	    	
	    	
	    	System.out.println("First fit swap in count average " + ffAvg);
	    	bw.write("First fit swap in count average " + ffAvg + "\n");
	    	bw.flush();
	    	
	    	System.out.println("Next fit swap in count average " + nfAvg);
	    	bw.write("Next fit swap in count average " + nfAvg + "\n");
	    	bw.flush();
	    	
	    	System.out.print("Best fit swap in count average " + bfAvg);
	    	bw.write("Best fit swap in count average " + bfAvg);
	    	bw.flush();
	    	
	    	bw.close();
    	}
    	catch (IOException e) {
			e.printStackTrace();
		}
    }
}
