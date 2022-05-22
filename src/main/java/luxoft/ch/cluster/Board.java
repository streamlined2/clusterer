package luxoft.ch.cluster;

import java.util.BitSet;
import java.util.Optional;

class Board {

	private final BitSet[] data;
	private final int rowCount;
	private final int columnCount;

	public Board(int rowCount, int columnCount) {
		if (rowCount < 1 || columnCount < 1) {
			throw new IllegalArgumentException(
					"wrong dimension parameters %d, %d supplied".formatted(rowCount, columnCount));
		}
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		data = new BitSet[rowCount];
		for (int k = 0; k < rowCount; k++) {
			data[k] = new BitSet(columnCount);
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void set(int row, int column) {
		data[row].set(column);
	}

	public void set(int index) {
		final int row = index / columnCount;
		final int column = index % columnCount;
		set(row, column);
	}

	public Optional<Segment> findNextSegment(int row, int startColumn) {
		final int beginIndex = data[row].nextSetBit(startColumn);
		if (beginIndex == -1) {
			return Optional.empty();
		}
		final int endIndex = data[row].nextClearBit(beginIndex);
		if (endIndex == -1) {
			return Optional.of(new Segment(row, beginIndex, columnCount));
		}
		return Optional.of(new Segment(row, beginIndex, endIndex));
	}

	public boolean hasNoMoreSegments(int row, int startColumn) {
		return findNextSegment(row, startColumn).isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < rowCount; row++) {
			int column = 0;
			do {
				int index = data[row].nextSetBit(column);
				if (index == -1) {
					addZeros(builder, columnCount - column);
					break;
				} else {
					addZeros(builder, index - column);
					builder.append('1');
					column = index + 1;
				}
			} while (true);
			builder.append('\n');
		}
		return builder.toString();
	}

	private void addZeros(StringBuilder builder, int count) {
		for (int k = count; k > 0; k--) {
			builder.append('0');
		}
	}

}
