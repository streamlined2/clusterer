package luxoft.ch.clusterer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

class SegmentList implements Iterable<Segment> {

	private final List<Segment> segments;

	public SegmentList(int size) {
		segments = new ArrayList<>(size);
	}

	public void addSegment(Segment segment) {
		segments.add(segment);
	}

	@Override
	public Iterator<Segment> iterator() {
		return segments.iterator();
	}

	@Override
	public String toString() {
		StringJoiner join = new StringJoiner("\n");
		forEach(segment -> join.add(segment.toString()));
		return join.toString();
	}

}
