package luxoft.ch.cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

public class ClusterFinderImpl implements ClusterFinder {

	private static final double SATURATION_DEGREE = 0.35D;

	private Board board;
	private int clusterNumber;

	public ClusterFinderImpl() {
		clusterNumber = 1;
	}

	public ClusterFinderImpl(int rows, int columns) {
		this();
		board = new Board(rows, columns);
	}

	public ClusterFinderImpl(FieldGenerator fieldGenerator) {
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

	public ClusterFinderImpl mark(int row, int column) {
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
				final int newCombinedClusterNumber = mergeAdjacentSegmentsInOneCluster(segment, currentRowSegments,
						previousRowSegments, collectedSegments);
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

	private int mergeAdjacentSegmentsInOneCluster(Segment segment, SegmentList currentRowSegments,
			SegmentList previousRowSegments, SegmentList collectedSegments) {

		final int commonRangeStartColumn = segment.getStartColumn() == 0 ? 0 : segment.getStartColumn() - 1;
		final int commonRangeEndColumn = segment.getEndColumn() < board.getColumnCount() ? segment.getEndColumn() + 1
				: board.getColumnCount();

		int newCombinedClusterNumber = NO_CLUSTER_NUMBER_ASSIGNED;

		Optional<Segment> previousProbableCandidate = Optional.empty();
		do {
			Optional<Segment> probableCandidate = previousRowSegments.peek();
			if (probableCandidate.isEmpty() || commonRangeEndColumn <= probableCandidate.get().getStartColumn()
					|| probableCandidate.equals(previousProbableCandidate)) {
				break;
			}
			Segment candidate = probableCandidate.get();
			if (isAdjacent(candidate, commonRangeStartColumn, commonRangeEndColumn)) {
				final int candidateClusterNumber = candidate.getCluster();
				if (newCombinedClusterNumber == NO_CLUSTER_NUMBER_ASSIGNED) {
					newCombinedClusterNumber = candidateClusterNumber;
				}
				if (newCombinedClusterNumber != candidateClusterNumber) {
					currentRowSegments.moveToAnotherCluster(candidateClusterNumber, newCombinedClusterNumber);
					previousRowSegments.moveToAnotherCluster(candidateClusterNumber, newCombinedClusterNumber);
					collectedSegments.moveToAnotherCluster(candidateClusterNumber, newCombinedClusterNumber);
				}
			}
			if (candidate.getEndColumn() < commonRangeEndColumn
					|| board.hasNoMoreSegments(segment.getRow(), segment.getEndColumn())) {
				collectedSegments.addSegment(previousRowSegments.remove());
			}
			previousProbableCandidate = probableCandidate;
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
	public List<Cluster> findClusters(FieldGenerator fieldGenerator) {// TODO must be optimized
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

	private static void printClusters(List<Cluster> clusters, FieldGenerator fg, PrintWriter p) {
		int[][] matrix = new int[fg.getHeight()][fg.getWidth()];
		for (var cluster : clusters) {
			ClusterImpl cl = (ClusterImpl) cluster;
			int clusterNumber = cl.getNumber();
			for (var segment : cl) {
				for (int column = segment.getStartColumn(); column < segment.getEndColumn(); column++) {
					matrix[segment.getRow()][column] = clusterNumber;
				}
			}
		}
		for (int row = 0; row < matrix.length; row++) {
			StringBuilder b = new StringBuilder();
			for (int column = 0; column < matrix[row].length; column++) {
				b.append("%5d".formatted(matrix[row][column]));
			}
			p.println(b.toString());
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		ClusterFinderImpl runner = new ClusterFinderImpl(7, 7).mark(1, 1).mark(2, 1).mark(3, 2).mark(2, 4).mark(3, 6)
				.mark(4, 5).mark(5, 4).mark(5, 5);
		System.out.printf("Original board:%n%n%s%n", runner.toString());
		System.out.printf("List of segments:%n%s%n%n",
				runner.getSegmentsSortedBy(Segment.BY_CLUSTER_ROW_START_COLUMN_COMPARATOR));

		ClusterFinderImpl runner3 = new ClusterFinderImpl(7, 7).mark(0, 0).mark(0, 2).mark(0, 6).mark(2, 0).mark(2, 1)
				.mark(3, 1).mark(3, 4).mark(5, 2).mark(6, 2).mark(6, 4).mark(6, 5);
		System.out.printf("Original board:%n%n%s%n", runner3.toString());
		System.out.printf("List of segments:%n%s%n%n",
				runner3.getSegmentsSortedBy(Segment.BY_CLUSTER_ROW_START_COLUMN_COMPARATOR));

		FieldGenerator fieldGenerator = new FieldGenerator(7, 7, 30);
		ClusterFinderImpl runner2 = new ClusterFinderImpl();
		List<Cluster> clusters = runner2.findClusters(fieldGenerator);
		System.out.printf("Original board:%n%n%s%n", runner2.toString());
		System.out.printf("Clusters:%n");
		clusters.forEach(System.out::println);
		System.out.println();

		int size = 256;
		final FieldGenerator fg = new FieldGenerator(size, size, 30);
		ClusterFinder finder = new ClusterFinderImpl();
		List<Cluster> clusters2 = finder.findClusters(fg);
		try (PrintWriter writer = new PrintWriter(new File("data.txt"))) {
			printClusters(clusters2, fg, writer);
		}
	}

}
