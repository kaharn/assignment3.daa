public class Problem08 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[][] graph = buildGraph(numCourses, prerequisites);
        int[] indegree = new int[numCourses];
        for (int[] prerequisite : prerequisites) {
            indegree[prerequisite[0]]++;
        }
        // Each course is enqueued once; this array also records the ordering.
        int[] queue = new int[numCourses];
        int head = 0;
        int tail = 0;
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue[tail++] = course;
            }
        }
        while (head < tail) {
            int course = queue[head++];
            for (int next : graph[course]) {
                if (--indegree[next] == 0) {
                    queue[tail++] = next;
                }
            }
        }
        return head == numCourses;
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
