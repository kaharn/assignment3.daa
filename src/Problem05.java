public class Problem05 {
    public int countDependencyGroups(int n, int[][] dependencies) {
        int[][] graph = buildGraph(n, dependencies, false);
        int[][] reversed = buildGraph(n, dependencies, true);
        boolean[] visited = new boolean[n];
        int[] nextEdge = new int[n];
        int[] stack = new int[n];
        int[] finishOrder = new int[n];
        int finished = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            int top = 0;
            stack[top++] = start;
            visited[start] = true;
            while (top > 0) {
                int vertex = stack[top - 1];
                if (nextEdge[vertex] < graph[vertex].length) {
                    int next = graph[vertex][nextEdge[vertex]++];
                    if (!visited[next]) {
                        visited[next] = true;
                        stack[top++] = next;
                    }
                } else {
                    // Finish only after every outgoing edge has been explored.
                    finishOrder[finished++] = vertex;
                    top--;
                }
            }
        }

        java.util.Arrays.fill(visited, false);
        int groups = 0;
        for (int i = finished - 1; i >= 0; i--) {
            int start = finishOrder[i];
            if (visited[start]) {
                continue;
            }
            groups++;
            int top = 0;
            stack[top++] = start;
            visited[start] = true;
            while (top > 0) {
                int vertex = stack[--top];
                for (int next : reversed[vertex]) {
                    if (!visited[next]) {
                        visited[next] = true;
                        stack[top++] = next;
                    }
                }
            }
        }
        return groups;
    }

    private int[][] buildGraph(int n, int[][] dependencies, boolean reverse) {
        int from = reverse ? 1 : 0;
        int to = reverse ? 0 : 1;
        int[] degree = new int[n];
        for (int[] edge : dependencies) {
            degree[edge[from]]++;
        }
        int[][] graph = new int[n][];
        for (int vertex = 0; vertex < n; vertex++) {
            graph[vertex] = new int[degree[vertex]];
        }
        int[] position = new int[n];
        for (int[] edge : dependencies) {
            graph[edge[from]][position[edge[from]]++] = edge[to];
        }
        return graph;
    }
}
