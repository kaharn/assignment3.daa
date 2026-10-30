public class Problem07 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[][] graph = buildGraph(numCourses, prerequisites);
        int[] state = new int[numCourses]; // 0 = unseen, 1 = active, 2 = finished.
        int[] nextEdge = new int[numCourses];
        int[] stack = new int[numCourses];

        for (int start = 0; start < numCourses; start++) {
            if (state[start] != 0) {
                continue;
            }
            int top = 0;
            stack[top++] = start;
            state[start] = 1;
            while (top > 0) {
                int course = stack[top - 1];
                if (nextEdge[course] < graph[course].length) {
                    int next = graph[course][nextEdge[course]++];
                    if (state[next] == 1) {
                        // An edge to an active ancestor closes a directed cycle.
                        return false;
                    }
                    if (state[next] == 0) {
                        state[next] = 1;
                        stack[top++] = next;
                    }
                } else {
                    state[course] = 2;
                    top--;
                }
            }
        }
        return true;
    }

    private int[][] buildGraph(int numCourses, int[][] prerequisites) {
        int[] degree = new int[numCourses];
        for (int[] prerequisite : prerequisites) {
            degree[prerequisite[1]]++;
        }
        int[][] graph = new int[numCourses][];
        for (int course = 0; course < numCourses; course++) {
            graph[course] = new int[degree[course]];
        }
        int[] position = new int[numCourses];
        for (int[] prerequisite : prerequisites) {
            // [a, b] means b must precede a: store b -> a.
            graph[prerequisite[1]][position[prerequisite[1]]++] = prerequisite[0];
        }
        return graph;
    }
}
