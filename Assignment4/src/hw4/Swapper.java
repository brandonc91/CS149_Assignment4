package hw4;
import java.util.*;

public class Swapper {

	public final int MEMSIZE = 100;

	
	public final int CAPITAL_A = 65;
	public final int CAPITAL_Z = 90;
	public final int SMALL_A = 97;
	public final int SMALL_Z = 122;
	public final int UNI_0 = 48;
	public final int UNI_9 = 57;
	
	private LinkedList<Integer> main_memory;
	private ArrayList<Process> processList;		// the main list of processes
	private LinkedList<Process> processQueue;

	public Swapper(int processCount) {
		this.processList = makeProcessList(processCount);
		main_memory = new LinkedList<Integer>();
		main_memory.add(MEMSIZE);
	}
	
	public ArrayList<Process> makeProcessList(int processCount) {
		// Make process name list for x amount of processes in the processList, A-Z
		// if processCount > 26 it rolls over
		int i = CAPITAL_A;
		ArrayList<Integer> names = new ArrayList<Integer>();
		for (int count = 0; count < processCount; count++) {
			switch (i) {
				case CAPITAL_Z + 1: i = SMALL_A;
									break;
				case SMALL_Z + 1:	i = UNI_0;
									break;
				case UNI_9 + 1:		i = CAPITAL_A;
									break;
				default:			break;
			}
			names.add(i);
			i++;
		}
		ArrayList<Process> processes = new ArrayList<Process>();
		// Make processes with IDs from ID list for x amount of processes in processList
		for (i = 0; i < processCount; i++) {
			char name = (char) names.get(i).intValue();
			processes.add(new Process(name));
		}
		return processes;
	}
	
	public void buildProcessQueue(int num) {
		for (int i = 0; i < 0; i++) {
			processQueue.add(processList.get(i));
		}
	}
	

	public void firstFit() {
		// keep list of free blocks of memory
		// when receiving request for memory, scan list for first block that is large enough to satisfy request
		// if the block is significantly larger than requested, split block, add remainder to list as a free block
		int availableMemory = MEMSIZE;
		if (main_memory.size() == 1) {
			main_memory.add(processList.get(0).size);
			availableMemory -= processList.get(0).size;
			main_memory.set(0, availableMemory);
		}
		while (availableMemory > 0) {
			
		}
		
	}	

	public void nextFit() {

	}

	public void bestFit() {

	}


	// Run each algorithm 5 times simulating 1 minute each time to get an avg of the # of processes 
	// successfully swapped into memory
	
	
	public static void main(String[] args)
	{
		Swapper test = new Swapper(10);
		for (Process p : test.processList) {
			p.print();
		}
	}
	
	
}
