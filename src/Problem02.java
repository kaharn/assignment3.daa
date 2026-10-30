public class Problem02 {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }

        int components = n;
        for (int i = 0; i < n; i++) {
            // The matrix is symmetric, so examine each undirected edge once.
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    int rootA = find(parent, i);
                    int rootB = find(parent, j);
                    if (rootA != rootB) {
                        if (size[rootA] < size[rootB]) {
                            int temp = rootA;
                            rootA = rootB;
                            rootB = temp;
                        }
                        parent[rootB] = rootA;
                        size[rootA] += size[rootB];
                        components--;
                    }
                }
            }
        }
        return components;
    }

    private int find(int[] parent, int vertex) {
        while (parent[vertex] != vertex) {
            // Path halving compresses the route to the representative.
            parent[vertex] = parent[parent[vertex]];
            vertex = parent[vertex];
        }
        return vertex;
    }
}
