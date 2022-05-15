package luxoft.ch.clusterer;

public class Runner {

	public static void main(String[] args) {

		Board board = new Board(10);
		for (int k = 0; k < 10; k++) {
			board.set(k, k);
		}
		for (int k = 0; k < 10; k++) {
			board.set(k, 10 - k - 1);
		}
		System.out.println(board.toString());

	}

}
