package luxoft.ch.cluster;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Queue;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

class SegmentList implements Iterable<Segment> {

	private final Queue<Segment> segments;

	public SegmentList() {
		segments = new ArrayDeque<>();
	}

	public SegmentList(int size) {
		segments = new ArrayDeque<>(size);
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

	public boolean isEmpty() {
		return segments.isEmpty();
	}

	public Optional<Segment> peek() {
		return Optional.ofNullable(segments.peek());
	}

	public Segment remove() {
		return segments.remove();
	}

	public void copySegments(SegmentList segmentList) {
		clear();
		addSegments(segmentList);
	}

	// TODO needs optimization
	public void changeCluster(int originalClusterNumber, int newClusterNumber) {
		for (var segment : segments) {
			if (segment.getCluster() == originalClusterNumber) {
				segment.setCluster(newClusterNumber);
			}
		}
	}

	public SortedSet<Segment> getSortedBy(Comparator<Segment> comparator) {
		SortedSet<Segment> sortedSet = new TreeSet<>(comparator);
		sortedSet.addAll(segments);
		return sortedSet;
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
