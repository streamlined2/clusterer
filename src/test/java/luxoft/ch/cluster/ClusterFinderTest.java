package luxoft.ch.cluster;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ClusterFinderTest {

	private static final String ClusterFindeImpl = "luxoft.ch.cluster.ClusterFinderImpl";

	@Test
	void test5() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int size = 5;
		final FieldGenerator fg = new FieldGenerator(size, size, 30);
		ClusterFinder finder = (ClusterFinder) Class.forName(ClusterFindeImpl).getDeclaredConstructor().newInstance();
		List<Cluster> clusters = finder.findClusters(fg);
		printClusters(fg, clusters);

		assertEquals(2, clusters.size());

		validateCluster(clusters, new int[] { 3, 4 });
		validateCluster(clusters, new int[] { 10, 15, 16, 22, 23 });
	}

	@Test
	void test8() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int size = 8;
		final FieldGenerator fg = new FieldGenerator(size, size, 30);
		ClusterFinder finder = (ClusterFinder) Class.forName(ClusterFindeImpl).getDeclaredConstructor().newInstance();
		List<Cluster> clusters = finder.findClusters(fg);

		printClusters(fg, clusters);

		assertEquals(10, clusters.size());

		validateCluster(clusters, new int[] { 1 });
		validateCluster(clusters, new int[] { 7, 15 });
		validateCluster(clusters, new int[] { 16, 24 });
		validateCluster(clusters, new int[] { 38, 39, 46, 53, 60, 62, 63 });
	}

	@Test
	void test256() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		int size = 256;
		final FieldGenerator fg = new FieldGenerator(size, size, 30);
		ClusterFinder finder = (ClusterFinder) Class.forName(ClusterFindeImpl).getDeclaredConstructor().newInstance();
		List<Cluster> clusters = finder.findClusters(fg);

		PrintWriter out = new PrintWriter("d:\\sp.txt");
		printClustersCSV(fg, clusters, out);
		out.close();

		assertEquals(3961, clusters.size());
	}

	private static Cluster findClusterByCell(List<Cluster> clusters, int index) {
		for (Cluster cluster : clusters) {
			if (cluster.containsCell(index)) {
				return cluster;
			}
		}
		return null;
	}

	private void validateCluster(List<Cluster> clusters, int[] indexes) {
		Cluster cluster = findClusterByCell(clusters, indexes[0]);
		assertEquals(indexes.length, cluster.getCellsCount());
		for (int i : indexes) {
			if (!cluster.containsCell(i)) {
				fail("Expected cell index is not found in cluster " + i);
			}
		}

	}

	private void printClusters(final FieldGenerator fg, List<Cluster> clusters) {
		int[] board = new int[fg.getWidth() * fg.getHeight()];
		for (int c = 0; c < clusters.size(); c++) {
			Cluster cluster = clusters.get(c);
			for (int i = 0; i < board.length; i++) {
				if (cluster.containsCell(i)) {
					board[i] = c + 1;
				}
			}
		}
		for (int i = 0; i < fg.getHeight(); i++) {
			for (int j = 0; j < fg.getWidth(); j++) {
				int b = board[fg.getIndex(j, i)];
				System.out.print(b == 0 ? " " : Integer.toHexString(b));
			}
			System.out.println();
		}
	}

	private void printClustersCSV(final FieldGenerator fg, List<Cluster> clusters, PrintWriter out) {
		List<Cluster> lc = new ArrayList<>(clusters.size());
		lc.addAll(clusters);
		int[] board = new int[fg.getWidth() * fg.getHeight()];
		int c = 1;
		for (int i = 0; i < board.length; i++) {
			Cluster cluster = findClusterByCell(lc, i);
			if (cluster != null) {
				lc.remove(cluster);
				for (int j = i; j < board.length; j++) {
					if (cluster.containsCell(j)) {
						board[j] = c;
					}
				}
				c++;
			}
		}

		for (int i = 0; i < fg.getHeight(); i++) {
			int j = 0;
			for (; j < fg.getWidth() - 1; j++) {
				int b = board[fg.getIndex(j, i)];
				out.print(b == 0 ? "   " : String.format("%3h", b));
				out.print(',');
			}
			int b = board[fg.getIndex(j, i)];
			out.print(b == 0 ? " " : Integer.toHexString(b));
			out.println();
		}
	}

}
