public class Problem01 {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int provinces = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            provinces++;
            int top = 0;
            stack[top++] = start;
            visited[start] = true;
            while (top > 0) {
                int city = stack[--top];
                for (int next = 0; next < n; next++) {
                    if (isConnected[city][next] == 1 && !visited[next]) {
                        // Mark on insertion so each city enters the stack once.
                        visited[next] = true;
                        stack[top++] = next;
                    }
                }
            }
        }
        return provinces;
    }
}
