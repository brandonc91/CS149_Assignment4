package hw4;

public class Process {
	public char id;
	public int size;
	public int burst;

	public Process(char id) {
		this.id = id;
		size = makeSize();
		burst = makeBurst();
	}

	private static int makeSize() {
		int min = 0;
		int max = 4;
		int sizes[] = {5, 11, 17, 31};
		return sizes[min + (int) (Math.random() * max)];
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