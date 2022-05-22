package luxoft.ch.cluster;

import java.util.Random;

public class FieldGenerator {
	private int width;
	private int height;
	private int counter = 0;
	private int n;
	private Random rand = new Random(0);

	public FieldGenerator(int width, int height, double perc) {
		this.width = width;
		this.height = height;
		this.n = (int) perc * width * height / 100;
	}

	public int getNextIndex() {
		if (counter < n) {
			counter++;
			return rand.nextInt(width * height);
		} else {
			return -1;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public int getIndex(int x, int y) {
		return getWidth() * y + x;
	}
}
