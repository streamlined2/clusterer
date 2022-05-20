package luxoft.ch.clusterer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

class SegmentList implements Iterable<Segment> {

	private final List<Segment> segments;

	public SegmentList() {
		segments = new ArrayList<>();
	}

	public SegmentList(int size) {
		segments = new ArrayList<>(size);
	}

	public void clear() {
		segments.clear();
	}

	public void addSegment(Segment segment) {
		segments.add(segment);
	}

	public void addSegments(SegmentList segmentList) {
		segments.addAll(segmentList.segments);
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
