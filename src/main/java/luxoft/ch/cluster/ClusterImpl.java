package luxoft.ch.cluster;

import java.util.Iterator;

class ClusterImpl implements Cluster, Iterable<Segment> {

	private final int number;
	private final SegmentList segments;
	private final int width;

	public ClusterImpl(int number, int width) {
		this.number = number;
		this.width = width;
		this.segments = new SegmentList();
	}

	public int getNumber() {
		return number;
	}

	public void addSegment(Segment segment) {
		segments.addSegment(segment);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClusterImpl cluster) {
			return number == cluster.number;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return number;
	}

	@Override
	public boolean containsCell(int index) {
		for (var segment : segments) {
			if (segment.containsIndex(index, width)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getCellsCount() {
		return segments.getCellCount();
	}
	
	@Override
	public String toString() {
		return "%d: %s".formatted(number, segments.toString());
	}

	@Override
	public Iterator<Segment> iterator() {
		return segments.iterator();
	}

}
