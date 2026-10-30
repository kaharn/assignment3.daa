public class Problem06 {
    public int countDependencyGroups(int n, int[][] dependencies) {
        int[][] graph = buildGraph(n, dependencies);
        int[] discovery = new int[n]; // Zero means undiscovered; times start at one.
        int[] low = new int[n];
        boolean[] onStack = new boolean[n];
        int[] componentStack = new int[n];
        int componentTop = 0;
        int[] dfsStack = new int[n];
        int[] nextEdge = new int[n];
        int time = 0;
        int groups = 0;

        for (int start = 0; start < n; start++) {
            if (discovery[start] != 0) {
                continue;
            }
            int top = 0;
            dfsStack[top++] = start;
            discovery[start] = low[start] = ++time;
            componentStack[componentTop++] = start;
            onStack[start] = true;

            while (top > 0) {
                int vertex = dfsStack[top - 1];
                if (nextEdge[vertex] < graph[vertex].length) {
                    int next = graph[vertex][nextEdge[vertex]++];
                    if (discovery[next] == 0) {
                        discovery[next] = low[next] = ++time;
                        dfsStack[top++] = next;
                        componentStack[componentTop++] = next;
                        onStack[next] = true;
                    } else if (onStack[next]) {
                        // An active non-tree edge uses discovery time, not low[next].
                        low[vertex] = Math.min(low[vertex], discovery[next]);
                    }
                    // Edges into an already completed SCC cannot lower low-link.
                } else {
                    top--;
                    if (low[vertex] == discovery[vertex]) {
                        int member;
                        do {
                            member = componentStack[--componentTop];
                            onStack[member] = false;
                        } while (member != vertex);
                        groups++;
                    }
                    if (top > 0) {
                        // Simulate returning from a recursive DFS child.
                        int parent = dfsStack[top - 1];
                        low[parent] = Math.min(low[parent], low[vertex]);
                    }
                }
            }
        }
        return groups;
    }

    private int[][] buildGraph(int n, int[][] dependencies) {
        int[] degree = new int[n];
        for (int[] edge : dependencies) {
            degree[edge[0]]++;
        }
        int[][] graph = new int[n][];
        for (int vertex = 0; vertex < n; vertex++) {
            graph[vertex] = new int[degree[vertex]];
        }
        int[] position = new int[n];
        for (int[] edge : dependencies) {
            graph[edge[0]][position[edge[0]]++] = edge[1];
        }
        return graph;
    }
}
