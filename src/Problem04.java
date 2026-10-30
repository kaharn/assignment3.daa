import java.util.Arrays;

public class Problem04 {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        boolean[] inTree = new boolean[n];
        int[] minDistance = new int[n];
        Arrays.fill(minDistance, Integer.MAX_VALUE);
        minDistance[0] = 0;
        long cost = 0;

        for (int added = 0; added < n; added++) {
            int closest = -1;
            for (int vertex = 0; vertex < n; vertex++) {
                if (!inTree[vertex]
                        && (closest == -1 || minDistance[vertex] < minDistance[closest])) {
                    closest = vertex;
                }
            }
            inTree[closest] = true;
            cost += minDistance[closest];
            for (int vertex = 0; vertex < n; vertex++) {
                if (!inTree[vertex]) {
                    int distance = Math.abs(points[closest][0] - points[vertex][0])
                            + Math.abs(points[closest][1] - points[vertex][1]);
                    // Store only the cheapest edge from the growing tree.
                    minDistance[vertex] = Math.min(minDistance[vertex], distance);
                }
            }
        }
        return Math.toIntExact(cost);
    }
}
