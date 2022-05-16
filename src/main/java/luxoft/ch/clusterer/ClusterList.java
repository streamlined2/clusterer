package luxoft.ch.clusterer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringJoiner;

class ClusterList implements Iterable<Cluster> {

	private final Queue<Cluster> clusters;

	public ClusterList() {
		clusters = new LinkedList<>();
	}

	public void addCluster(Cluster cluster) {
		clusters.add(cluster);
	}

	@Override
	public Iterator<Cluster> iterator() {
		return clusters.iterator();
	}

	@Override
	public String toString() {
		StringJoiner join = new StringJoiner("\n");
		forEach(cluster -> join.add(cluster.toString()));
		return join.toString();
	}

}
