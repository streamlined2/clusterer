package luxoft.ch.clusterer;

import java.util.Optional;

public class Runner {

	private final Board board;

	public Runner(int rows, int columns) {
		board = new Board(rows, columns);
	}

	public Runner mark(int row, int column) {
		board.set(row, column);
		return this;
	}

	public ClusterList collectClusters(Board board) {
		ClusterList clusterList = new ClusterList();
		for (int row = 0; row < board.getRowCount(); row++) {
			int startColumn = 0;
			do {
				Optional<Segment> candidateSegment = board.findNextSegment(row, startColumn);
				if (candidateSegment.isEmpty()) {
					break;
				}
				Segment segment = candidateSegment.get();
				startColumn = segment.endColumn();
			} while (startColumn < board.getColumnCount());
		}
		return clusterList;
	}

	@Override
	public String toString() {
		return board.toString();
	}

	public static void main(String[] args) {
		Runner runner = new Runner(7, 7);
		runner.mark(1, 1).mark(2, 1).mark(3, 2).mark(2, 4).mark(3, 6).mark(4, 5).mark(5, 4).mark(5, 5);
		System.out.println(runner.toString());
	}

}
