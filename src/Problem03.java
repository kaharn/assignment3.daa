import java.util.Arrays;

public class Problem03 {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        Edge[] edges = new Edge[n * (n - 1) / 2];
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int distance = Math.abs(points[i][0] - points[j][0])
                        + Math.abs(points[i][1] - points[j][1]);
                edges[index++] = new Edge(i, j, distance);
            }
        }
        Arrays.sort(edges, (a, b) -> Integer.compare(a.weight, b.weight));

        DisjointSet sets = new DisjointSet(n);
        int selected = 0;
        long cost = 0;
        for (Edge edge : edges) {
            if (sets.union(edge.from, edge.to)) {
                cost += edge.weight;
                if (++selected == n - 1) {
                    break;
                }
            }
        }
        // Coordinate bounds make each distance safe in int; accumulate in long.
        return Math.toIntExact(cost);
    }

    private static class Edge {
        final int from;
        final int to;
        final int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private static class DisjointSet {
        final int[] parent;
        final int[] size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int vertex) {
            while (parent[vertex] != vertex) {
                parent[vertex] = parent[parent[vertex]];
                vertex = parent[vertex];
            }
            return vertex;
        }

        boolean union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            if (rootA == rootB) {
                return false;
            }
            if (size[rootA] < size[rootB]) {
                int temp = rootA;
                rootA = rootB;
                rootB = temp;
            }
            parent[rootB] = rootA;
            size[rootA] += size[rootB];
            return true;
        }
    }
}
