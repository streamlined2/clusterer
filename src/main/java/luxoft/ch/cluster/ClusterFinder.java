package luxoft.ch.cluster;

import java.util.List;

public interface ClusterFinder {

	List<Cluster> findClusters(FieldGenerator fg);

}