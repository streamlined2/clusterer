package luxoft.ch.clusterer;

import java.util.BitSet;
import java.util.Optional;

class Board {

	private final BitSet[] data;
	private final int side;

	public Board(int side) {
		this.side = side;
		data = new BitSet[side];
		for (int k = 0; k < side; k++) {
			data[k] = new BitSet(side);
		}
	}

	public Board(boolean[][] flags) {
		this.side = flags.length;
		data = new BitSet[side];
		for (int row = 0; row < side; row++) {
			data[row] = new BitSet(flags[row].length);
			for (int column = 0; column < flags[row].length; column++) {
				if (flags[row][column]) {
					data[row].set(column);
				}
			}
		}
	}

	public void set(int row, int column) {
		data[row].set(column);
	}

	public Optional<Segment> findNextSegment(int row, int startColumn) {
		int beginIndex = data[row].nextSetBit(startColumn);
		if (beginIndex == -1) {
			return Optional.empty();
		}
		int endIndex = data[row].nextClearBit(beginIndex);
		if (endIndex == -1) {
			return Optional.of(new Segment(row, beginIndex, side));
		} else {
			return Optional.of(new Segment(row, beginIndex, endIndex));
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < side; row++) {
			int column = 0;
			do {
				int index = data[row].nextSetBit(column);
				if (index == -1) {
					addZeros(builder, side - column);
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
