package luxoft.ch.clusterer;

import java.util.Optional;

public class Runner {

	private static final double SATURATION = 0.3D;

	private final Board board;

	public Runner(int rows, int columns) {
		board = new Board(rows, columns);
	}

	public Runner mark(int row, int column) {
		board.set(row, column);
		return this;
	}

	private static int getIinitialSize(int count) {
		return (int) Math.round(SATURATION * count);
	}

	public SegmentList collectSegments(Board board) {

		SegmentList collectedSegments = new SegmentList(board.getRowCount());
		SegmentList previousRowSegments = new SegmentList(getIinitialSize(board.getColumnCount()));
		SegmentList currentRowSegments = new SegmentList(getIinitialSize(board.getColumnCount()));

		for (int row = 0; row < board.getRowCount(); row++) {
			int startColumn = 0;
			currentRowSegments.clear();
			do {
				Optional<Segment> candidateSegment = board.findNextSegment(row, startColumn);
				if (candidateSegment.isEmpty()) {
					break;
				}
				Segment segment = candidateSegment.get();
				// TODO
				startColumn = segment.getEndColumn();
				currentRowSegments.addSegment(segment);// TODO set cluster number before adding segment
			} while (startColumn < board.getColumnCount());
			// dismantle previous row segment list
			collectedSegments.addSegments(previousRowSegments);
			// switch segment lists
			previousRowSegments.clear();
			previousRowSegments.addSegments(currentRowSegments);
		}
		return collectedSegments;
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
