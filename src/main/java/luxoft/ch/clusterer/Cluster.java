package luxoft.ch.clusterer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class Cluster {

	private final List<Segment> segments;

	public Cluster() {
		segments = new ArrayList<>();
	}

	public void addSegment(Segment segment) {
		segments.add(segment);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cluster cluster) {
			return segments.equals(cluster.segments);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return segments.hashCode();
	}

	@Override
	public String toString() {
		StringJoiner join = new StringJoiner(",");
		segments.forEach(segment -> join.add(segment.toString()));
		return join.toString();
	}

}
