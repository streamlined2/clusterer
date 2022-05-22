package luxoft.ch.cluster;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

public class Runner implements ClusterFinder {

	private static final double SATURATION_DEGREE = 0.35D;

	private Board board;
	private int clusterNumber;

	private Runner() {
		clusterNumber = 1;
	}

	public Runner(int rows, int columns) {
		this();
		board = new Board(rows, columns);
	}

	public Runner(FieldGenerator fieldGenerator) {
		this();
		initialize(fieldGenerator);
	}

	private void initialize(FieldGenerator fieldGenerator) {
		board = new Board(fieldGenerator.getHeight(), fieldGenerator.getWidth());
		int index;
		while ((index = fieldGenerator.getNextIndex()) != -1) {
			board.set(index);
		}
	}

	public Runner mark(int row, int column) {
		board.set(row, column);
		return this;
	}

	private int getNextClusterNumber() {
		return clusterNumber++;
	}

	public SortedSet<Segment> getSegmentsSortedBy(Comparator<Segment> comparator) {
		SegmentList segments = collectSegments();
		return segments.getSortedBy(comparator);
	}

	public SegmentList collectSegments() {

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
				final int newCombinedClusterNumber = mergeAdjacentSegmentsInOneCluster(segment, previousRowSegments,
						collectedSegments);
				segment.setCluster(newCombinedClusterNumber);
				currentRowSegments.addSegment(segment);
				startColumn = segment.getEndColumn();
			} while (startColumn < board.getColumnCount());

			collectedSegments.addSegments(previousRowSegments);
			previousRowSegments.copySegments(currentRowSegments);
			currentRowSegments.clear();
		}
		
		collectedSegments.addSegments(previousRowSegments);

		return collectedSegments;
	}

	private int getRowSegmentsPrognosedCount() {
		return (int) Math.round(SATURATION_DEGREE * board.getColumnCount());
	}

	private int getTotalSegmentsPrognosedCount() {
		return board.getRowCount() * getRowSegmentsPrognosedCount();
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
					previousRowSegments.moveToAnotherCluster(candidateClusterNumber, newCombinedClusterNumber);
					collectedSegments.moveToAnotherCluster(candidateClusterNumber, newCombinedClusterNumber);
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

	@Override
	public List<Cluster> findClusters(FieldGenerator fieldGenerator) {
		initialize(fieldGenerator);
		SegmentList segments = collectSegments();
		Map<Integer, ClusterImpl> map = new HashMap<>();
		while (!segments.isEmpty()) {
			Segment segment = segments.remove();
			ClusterImpl cluster = map.get(segment.getCluster());
			if (cluster == null) {
				cluster = new ClusterImpl(segment.getCluster(), fieldGenerator.getWidth());
			}
			cluster.addSegment(segment);
			map.put(segment.getCluster(), cluster);
		}
		List<Cluster> list = new ArrayList<>(map.size());
		list.addAll(map.values());
		return list;
	}

	public static void main(String[] args) {
		Runner runner = new Runner(7, 7).mark(1, 1).mark(2, 1).mark(3, 2).mark(2, 4).mark(3, 6).mark(4, 5).mark(5, 4)
				.mark(5, 5);
		System.out.printf("Original board:%n%n%s%n", runner.toString());
		System.out.printf("List of segments:%n%s%n%n",
				runner.getSegmentsSortedBy(Segment.BY_CLUSTER_ROW_START_COLUMN_COMPARATOR));

		Runner runner3 = new Runner(7, 7).mark(0, 0).mark(0, 2).mark(0, 6).mark(2, 0).mark(2, 1).mark(3, 1).mark(3, 4)
				.mark(5, 2).mark(6, 2).mark(6, 4).mark(6, 5);
		System.out.printf("Original board:%n%n%s%n", runner3.toString());
		System.out.printf("List of segments:%n%s%n%n",
				runner3.getSegmentsSortedBy(Segment.BY_CLUSTER_ROW_START_COLUMN_COMPARATOR));

		FieldGenerator fieldGenerator = new FieldGenerator(7, 7, 30);
		Runner runner2 = new Runner();
		List<Cluster> clusters = runner2.findClusters(fieldGenerator);
		System.out.printf("Original board:%n%n%s%n", runner2.toString());
		System.out.printf("Clusters:%n");
		clusters.forEach(System.out::println);

	}

}
