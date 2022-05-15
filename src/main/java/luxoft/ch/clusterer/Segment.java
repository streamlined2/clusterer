package luxoft.ch.clusterer;

import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;

record Segment(int row, int startColumn, int endColumn) implements Comparable<Segment> {

	public static final Comparator<Segment> BY_ROW_START_COLUMN_COMPARATOR = Comparator.comparingInt(Segment::row)
			.thenComparingInt(Segment::startColumn);

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
		return new StringJoiner(",", "[", "]").add("row: " + row).add("start column: " + startColumn)
				.add("end column: " + endColumn).toString();
	}

}
