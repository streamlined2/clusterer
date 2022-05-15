package luxoft.ch.clusterer;

import java.util.Comparator;

record Position(int y, int x) {

	public static final Comparator<Position> BY_ROW_COLUMN_COMPARATOR = Comparator.comparingInt(Position::y)
			.thenComparingInt(Position::x);

	@Override
	public String toString() {
		return new StringBuilder().append("{ y=").append(y).append(", x=").append(x).append(" }").toString();
	}

}
