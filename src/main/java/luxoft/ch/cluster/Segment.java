package luxoft.ch.cluster;

import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

class Segment implements Comparable<Segment> {

	public static final Comparator<Segment> BY_ROW_START_COLUMN_COMPARATOR = Comparator.comparingInt(Segment::getRow)
			.thenComparingInt(Segment::getStartColumn);
	public static final Comparator<Segment> BY_CLUSTER_ROW_START_COLUMN_COMPARATOR = Comparator
			.comparingInt(Segment::getCluster).thenComparingInt(Segment::getRow)
			.thenComparingInt(Segment::getStartColumn);

	private final int row;
	private final int startColumn;
	private final int endColumn;
	private int cluster;

	public Segment(int row, int startColumn, int endColumn) {
		if (startColumn >= endColumn) {
			throw new IllegalArgumentException(
					"start column %d should be less than end column %d".formatted(startColumn, endColumn));
		}
		this.row = row;
		this.startColumn = startColumn;
		this.endColumn = endColumn;
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	public int getRow() {
		return row;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public boolean contains(int row, int column) {
		return this.row == row && startColumn <= column && column < endColumn;
	}

	public boolean containsIndex(int index, int width) {
		final int production = row * width;
		final int startIndex = production + startColumn;
		final int endIndex = production + endColumn;
		return startIndex <= index && index < endIndex;
	}

	public int getCellCount() {
		return endColumn - startColumn;
	}

	@Override
	public int compareTo(Segment segment) {
		return BY_ROW_START_COLUMN_COMPARATOR.compare(this, segment);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Segment segment) {
			return row == segment.row && startColumn == segment.startColumn;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, startColumn);
	}

	@Override
	public String toString() {
		return new StringJoiner(",", "[", "]").add("cluster: " + cluster).add("row: " + row)
				.add("start column: " + startColumn).add("end column: " + endColumn).toString();
	}

}
