package hw4;

public class Process {
	public char id;
	public int size;
	public int burst;
	public int availability;

	public Process(char id) {
		this.id = id;
		size = makeSize();
		burst = makeBurst();
		availability = 0;
	}

	public void freeMemory() {
		availability = 1;
	}
	
	private static int makeSize() {
		int sizeRangeMin = 0;
		int sizeRangeMax = 4;
		int sizes[] = {5, 11, 17, 31};
		int partitionSize = sizes[sizeRangeMin + (int) (Math.random() * sizeRangeMax)];
		return partitionSize;
	}

	private static int makeBurst() {
		return (int) (Math.random() * 5) + 1;
	}

	public void print() {
		System.out.println("ID: " + id);
		System.out.println("Size: " + size + " MB");
		System.out.println("Burst time: " + burst + " sec");
		System.out.println("----");
	}
}