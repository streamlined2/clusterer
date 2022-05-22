package luxoft.ch.cluster;

import java.util.Optional;
import java.util.SortedSet;

public class Runner {

	private static final double SATURATION = 0.35D;

	private final Board board;
	private int clusterNumber;

	public Runner(int rows, int columns) {
		board = new Board(rows, columns);
		clusterNumber = 1;
	}

	public Runner mark(int row, int column) {
		board.set(row, column);
		return this;
	}

	private int getNextClusterNumber() {
		return clusterNumber++;
	}

	private int getRowSegmentsPrognosedCount() {
		return (int) Math.round(SATURATION * board.getColumnCount());
	}

	private int getTotalSegmentsPrognosedCount() {
		return board.getRowCount() * getRowSegmentsPrognosedCount();
	}

	public SortedSet<Segment> collectSegments() {

		SegmentList collectedSegments = new SegmentList(getTotalSegmentsPrognosedCount());
		SegmentList previousRowSegments = new SegmentList(getRowSegmentsPrognosedCount());
		SegmentList currentRowSegments = new SegmentList(getRowSegmentsPrognosedCount());

		for (int row = 0; row < board.getRowCount(); row++) {

			int startColumn = 0;
			do {
				Optional<Segment> candidateSegment = board.findNextSegment(row, startColumn);
				if (candidateSegment.isEmpty()) {
					break;
				}
				Segment segment = candidateSegment.get();
				int newCombinedClusterNumber = mergeAdjacentSegmentsInOneCluster(segment, previousRowSegments,
						collectedSegments);
				segment.setCluster(newCombinedClusterNumber);
				currentRowSegments.addSegment(segment);
				startColumn = segment.getEndColumn();
			} while (startColumn < board.getColumnCount());

			collectedSegments.addSegments(previousRowSegments);
			previousRowSegments.copySegments(currentRowSegments);
			currentRowSegments.clear();
		}
		
		return collectedSegments.getSortedBy(Segment.BY_CLUSTER_ROW_START_COLUMN_COMPARATOR);
	}

	private static final int NO_CLUSTER_NUMBER_ASSIGNED = -1;

	private int mergeAdjacentSegmentsInOneCluster(Segment segment, SegmentList previousRowSegments,
			SegmentList collectedSegments) {

		final int commonRangeStartColumn = segment.getStartColumn() == 0 ? 0 : segment.getStartColumn() - 1;
		final int commonRangeEndColumn = segment.getEndColumn() < board.getColumnCount() ? segment.getEndColumn() + 1
				: board.getColumnCount();

		int newCombinedClusterNumber = NO_CLUSTER_NUMBER_ASSIGNED;

		do {
			Optional<Segment> probableCandidate = previousRowSegments.peek();
			if (probableCandidate.isEmpty() || commonRangeEndColumn <= probableCandidate.get().getStartColumn()) {
				break;
			}
			Segment candidate = probableCandidate.get();
			if (isAdjacent(candidate, commonRangeStartColumn, commonRangeEndColumn)) {
				final int candidateClusterNumber = candidate.getCluster();
				if (newCombinedClusterNumber == NO_CLUSTER_NUMBER_ASSIGNED) {
					newCombinedClusterNumber = candidateClusterNumber;
				}
				if (newCombinedClusterNumber != candidateClusterNumber) {
					previousRowSegments.changeCluster(candidateClusterNumber, newCombinedClusterNumber);
					collectedSegments.changeCluster(candidateClusterNumber, newCombinedClusterNumber);
				}
			}
			if (candidate.getEndColumn() < commonRangeEndColumn
					|| board.hasNoMoreSegments(segment.getRow(), segment.getEndColumn())) {
				collectedSegments.addSegment(previousRowSegments.remove());
			}
		} while (!previousRowSegments.isEmpty());

		if (newCombinedClusterNumber == NO_CLUSTER_NUMBER_ASSIGNED) {
			newCombinedClusterNumber = getNextClusterNumber();
		}
		return newCombinedClusterNumber;
	}

	private boolean isAdjacent(Segment segment, int startColumn, int endColumn) {
		return !(segment.getEndColumn() <= startColumn || endColumn <= segment.getStartColumn());
	}

	@Override
	public String toString() {
		return board.toString();
	}

	public static void main(String[] args) {
		Runner runner = new Runner(7, 7);
		runner.mark(1, 1).mark(2, 1).mark(3, 2).mark(2, 4).mark(3, 6).mark(4, 5).mark(5, 4).mark(5, 5);
		System.out.printf("Original board:%n%n%s%n", runner.toString());
		System.out.printf("List of segments:%n%s%n", runner.collectSegments());
	}

}
