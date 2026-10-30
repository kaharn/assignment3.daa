import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Dependency-free tests. One test case checks an algorithm pair (or all four
 * course algorithms), including input preservation. Failures exit nonzero.
 */
public class Assignment3Tests {
    private static final long SEED = 20261030L;
    private static final Random RANDOM = new Random(SEED);
    // Reuse instances to detect state accidentally retained between calls.
    private static final Problem01 P01 = new Problem01();
    private static final Problem02 P02 = new Problem02();
    private static final Problem03 P03 = new Problem03();
    private static final Problem04 P04 = new Problem04();
    private static final Problem05 P05 = new Problem05();
    private static final Problem06 P06 = new Problem06();
    private static final Problem07 P07 = new Problem07();
    private static final Problem08 P08 = new Problem08();
    private static final Problem09 P09 = new Problem09();
    private static final Problem10 P10 = new Problem10();
    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        provinceTests();
        mstTests();
        sccTests();
        courseTests();
        System.out.println("Seed: " + SEED);
        System.out.println("Tests executed: " + (passed + failed)
                + ", passed: " + passed + ", failed: " + failed);
        if (failed != 0) {
            throw new AssertionError("Assignment tests failed");
        }
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            passed++;
        } catch (AssertionError | RuntimeException failure) {
            failed++;
            System.err.println("FAIL: " + name);
            failure.printStackTrace(System.err);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void equal(int expected, int actual, String algorithm) {
        require(expected == actual, algorithm + ": expected " + expected + ", got " + actual);
    }

    private static int[][] copy(int[][] input) {
        int[][] result = new int[input.length][];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i].clone();
        }
        return result;
    }

    private static void unchanged(int[][] original, int[][] input, String algorithm) {
        require(Arrays.deepEquals(original, input), algorithm + " modified its input");
    }

    private static int[][] identity(int n) {
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i][i] = 1;
        }
        return matrix;
    }

    private static void provinces(String name, int[][] matrix, int expected) {
        run("Provinces: " + name, () -> {
            int[][] original = copy(matrix);
            equal(expected, P01.findCircleNum(matrix), "DFS");
            unchanged(original, matrix, "DFS");
            equal(expected, P02.findCircleNum(matrix), "DSU");
            unchanged(original, matrix, "DSU");
        });
    }

    private static void provinceTests() {
        provinces("official 1", new int[][] {{1, 1, 0}, {1, 1, 0}, {0, 0, 1}}, 2);
        provinces("official 2", identity(3), 3);
        provinces("single", identity(1), 1);
        provinces("200 isolated", identity(200), 200);
        int[][] complete = new int[200][200];
        for (int[] row : complete) {
            Arrays.fill(row, 1);
        }
        provinces("200 complete", complete, 1);
        int[][] chain = identity(200);
        for (int i = 1; i < 200; i++) {
            chain[i - 1][i] = chain[i][i - 1] = 1;
        }
        provinces("200 chain", chain, 1);
        chain[99][100] = chain[100][99] = 0;
        provinces("two long components", chain, 2);
        for (int test = 0; test < 300; test++) {
            int n = 1 + RANDOM.nextInt(200);
            int[][] matrix = identity(n);
            double probability = (test % 6) * 0.08;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (RANDOM.nextDouble() < probability) {
                        matrix[i][j] = matrix[j][i] = 1;
                    }
                }
            }
            provinces("random " + test, matrix, provinceReference(matrix));
        }
    }

    // Independent breadth-first component counter for adjacency matrices.
    private static int provinceReference(int[][] matrix) {
        boolean[] seen = new boolean[matrix.length];
        int[] queue = new int[matrix.length];
        int count = 0;
        for (int start = 0; start < matrix.length; start++) {
            if (seen[start]) {
                continue;
            }
            count++;
            int head = 0;
            int tail = 0;
            queue[tail++] = start;
            seen[start] = true;
            while (head < tail) {
                int vertex = queue[head++];
                for (int next = 0; next < matrix.length; next++) {
                    if (matrix[vertex][next] == 1 && !seen[next]) {
                        seen[next] = true;
                        queue[tail++] = next;
                    }
                }
            }
        }
        return count;
    }

    private static void mst(String name, int[][] points, Integer expected) {
        run("MST: " + name, () -> {
            int[][] original = copy(points);
            int kruskal = P03.minCostConnectPoints(points);
            unchanged(original, points, "Kruskal");
            int prim = P04.minCostConnectPoints(points);
            unchanged(original, points, "Prim");
            equal(kruskal, prim, "MST agreement");
            require(kruskal >= 0, "Negative MST cost");
            if (expected != null) {
                equal(expected, kruskal, "MST reference");
            }
        });
    }

    private static void mstTests() {
        mst("official 1", new int[][] {{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}}, 20);
        mst("official 2", new int[][] {{3, 12}, {-2, 5}, {-4, 1}}, 18);
        mst("single", new int[][] {{-1000000, 1000000}}, 0);
        mst("opposite extremes", new int[][] {{-1000000, -1000000},
                {1000000, 1000000}}, 4000000);
        mst("four corners and equal weights", new int[][] {{-1000000, -1000000},
                {-1000000, 1000000}, {1000000, -1000000}, {1000000, 1000000}}, 6000000);
        for (int test = 0; test < 60; test++) {
            int[][] points = randomPoints(1 + RANDOM.nextInt(6), 10);
            mst("exhaustive tree reference " + test, points, exhaustiveMst(points));
        }
        for (int test = 0; test < 300; test++) {
            mst("random " + test, randomPoints(1 + RANDOM.nextInt(35), 1000000), null);
        }
        int[][] line = new int[1000][2];
        for (int i = 0; i < line.length; i++) {
            line[i][0] = -1000000 + 2000 * i;
        }
        mst("1000 collinear", line, 1998000);
        Collections.shuffle(Arrays.asList(line), RANDOM);
        mst("1000 shuffled collinear", line, 1998000);
        int[][] grid = new int[1000][2];
        for (int i = 0; i < grid.length; i++) {
            grid[i][0] = i % 40 - 20;
            grid[i][1] = i / 40 - 12;
        }
        mst("1000 grid with tied weights", grid, 999);
        mst("1000 random", randomPoints(1000, 1000000), null);
    }

    private static int[][] randomPoints(int n, int bound) {
        int[][] points = new int[n][2];
        Set<Long> used = new HashSet<>();
        int count = 0;
        while (count < n) {
            int x = RANDOM.nextInt(2 * bound + 1) - bound;
            int y = RANDOM.nextInt(2 * bound + 1) - bound;
            long key = ((long) x << 32) ^ (y & 0xffffffffL);
            if (used.add(key)) {
                points[count++] = new int[] {x, y};
            }
        }
        return points;
    }

    // Enumerate every labeled tree through its Prüfer sequence (only n <= 6).
    // This oracle does not use either greedy MST algorithm or DSU.
    private static int exhaustiveMst(int[][] points) {
        int n = points.length;
        if (n == 1) {
            return 0;
        }
        int sequences = 1;
        for (int i = 0; i < n - 2; i++) {
            sequences *= n;
        }
        long best = Long.MAX_VALUE;
        for (int code = 0; code < sequences; code++) {
            int[] sequence = new int[n - 2];
            int[] degree = new int[n];
            Arrays.fill(degree, 1);
            int remaining = code;
            for (int i = 0; i < sequence.length; i++) {
                sequence[i] = remaining % n;
                remaining /= n;
                degree[sequence[i]]++;
            }
            long cost = 0;
            for (int vertex : sequence) {
                int leaf = 0;
                while (degree[leaf] != 1) {
                    leaf++;
                }
                cost += distance(points, leaf, vertex);
                degree[leaf]--;
                degree[vertex]--;
            }
            int first = -1;
            for (int vertex = 0; vertex < n; vertex++) {
                if (degree[vertex] == 1) {
                    if (first == -1) {
                        first = vertex;
                    } else {
                        cost += distance(points, first, vertex);
                    }
                }
            }
            best = Math.min(best, cost);
        }
        return Math.toIntExact(best);
    }

    private static long distance(int[][] points, int a, int b) {
        return Math.abs((long) points[a][0] - points[b][0])
                + Math.abs((long) points[a][1] - points[b][1]);
    }

    private static void scc(String name, int n, int[][] edges, int expected) {
        run("SCC: " + name, () -> {
            int[][] original = copy(edges);
            equal(expected, P05.countDependencyGroups(n, edges), "Kosaraju");
            unchanged(original, edges, "Kosaraju");
            equal(expected, P06.countDependencyGroups(n, edges), "Tarjan");
            unchanged(original, edges, "Tarjan");
        });
    }

    private static void sccTests() {
        scc("official 1", 5, new int[][] {{0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}}, 3);
        scc("official 2", 4, new int[][] {{0, 1}, {1, 0}, {2, 3}, {3, 2}}, 2);
        scc("official 3", 4, new int[][] {{0, 1}, {1, 2}, {2, 3}}, 4);
        scc("single isolated", 1, new int[0][2], 1);
        scc("self-loops and duplicate edges", 3,
                new int[][] {{0, 0}, {0, 1}, {0, 1}, {1, 0}, {2, 2}}, 2);
        scc("edge to completed SCC", 3, new int[][] {{0, 1}, {0, 2}, {2, 1}}, 3);
        scc("cross edge to active SCC member", 3,
                new int[][] {{0, 1}, {1, 0}, {0, 2}, {2, 1}}, 1);
        // Every directed graph on three vertices, including self-loops.
        for (int mask = 0; mask < (1 << 9); mask++) {
            List<int[]> edges = new ArrayList<>();
            for (int bit = 0; bit < 9; bit++) {
                if ((mask & (1 << bit)) != 0) {
                    edges.add(new int[] {bit / 3, bit % 3});
                }
            }
            int[][] graph = edges.toArray(new int[edges.size()][]);
            scc("exhaustive graph " + mask, 3, graph, sccReference(3, graph));
        }
        for (int test = 0; test < 500; test++) {
            int n = 1 + RANDOM.nextInt(20);
            int[][] edges = new int[RANDOM.nextInt(81)][2];
            for (int[] edge : edges) {
                edge[0] = RANDOM.nextInt(n);
                edge[1] = RANDOM.nextInt(n);
            }
            scc("random " + test, n, edges, sccReference(n, edges));
        }
        int n = 100000;
        scc("100000 isolated", n, new int[0][2], n);
        scc("100000 chain", n, directedChain(n, false), n);
        scc("100000 cycle", n, directedChain(n, true), 1);
        int[][] bidirectional = new int[200000][2];
        int index = 0;
        for (int i = 1; i < n; i++) {
            bidirectional[index++] = new int[] {i - 1, i};
            bidirectional[index++] = new int[] {i, i - 1};
        }
        bidirectional[index++] = new int[] {0, 0};
        bidirectional[index] = new int[] {n - 1, n - 1};
        scc("100000 vertices, 200000 edges, one SCC", n, bidirectional, 1);
        int[][] dag = new int[200000][2];
        index = 0;
        for (int gap = 1; index < dag.length; gap++) {
            for (int from = 0; from + gap < n && index < dag.length; from++) {
                dag[index++] = new int[] {from, from + gap};
            }
        }
        scc("100000 vertices, 200000 edges, DAG", n, dag, n);
        int[][] pairs = new int[149999][2];
        index = 0;
        for (int i = 0; i < n; i += 2) {
            pairs[index++] = new int[] {i, i + 1};
            pairs[index++] = new int[] {i + 1, i};
            if (i + 2 < n) {
                pairs[index++] = new int[] {i + 1, i + 2};
            }
        }
        scc("50000 SCCs linked in a long chain", n, pairs, 50000);
    }

    // Floyd-Warshall reachability is independent of DFS/SCC implementations.
    private static boolean[][] reachability(int n, int[][] edges) {
        boolean[][] reach = new boolean[n][n];
        for (int[] edge : edges) {
            reach[edge[0]][edge[1]] = true;
        }
        for (int middle = 0; middle < n; middle++) {
            for (int from = 0; from < n; from++) {
                for (int to = 0; to < n; to++) {
                    reach[from][to] |= reach[from][middle] && reach[middle][to];
                }
            }
        }
        return reach;
    }

    private static int sccReference(int n, int[][] edges) {
        boolean[][] reach = reachability(n, edges);
        boolean[] counted = new boolean[n];
        int groups = 0;
        for (int vertex = 0; vertex < n; vertex++) {
            if (!counted[vertex]) {
                groups++;
                counted[vertex] = true;
                for (int other = 0; other < n; other++) {
                    if (reach[vertex][other] && reach[other][vertex]) {
                        counted[other] = true;
                    }
                }
            }
        }
        return groups;
    }

    private static int[][] directedChain(int n, boolean cycle) {
        int[][] edges = new int[cycle ? n : n - 1][2];
        for (int i = 0; i < n - 1; i++) {
            edges[i] = new int[] {i, i + 1};
        }
        if (cycle) {
            edges[n - 1] = new int[] {n - 1, 0};
        }
        return edges;
    }

    private static void courses(String name, int n, int[][] prerequisites,
                                boolean possible, boolean checkDecisions) {
        run("Courses: " + name, () -> {
            int[][] original = copy(prerequisites);
            if (checkDecisions) {
                require(P07.canFinish(n, prerequisites) == possible, "DFS decision");
                unchanged(original, prerequisites, "DFS decision");
                require(P08.canFinish(n, prerequisites) == possible, "Kahn decision");
                unchanged(original, prerequisites, "Kahn decision");
            }
            validateOrder(n, prerequisites, P09.findOrder(n, prerequisites), possible);
            unchanged(original, prerequisites, "DFS ordering");
            validateOrder(n, prerequisites, P10.findOrder(n, prerequisites), possible);
            unchanged(original, prerequisites, "Kahn ordering");
        });
    }

    private static void validateOrder(int n, int[][] prerequisites, int[] order,
                                      boolean possible) {
        require(order != null, "Null ordering");
        if (!possible) {
            equal(0, order.length, "Cyclic ordering length");
            return;
        }
        equal(n, order.length, "Ordering length");
        int[] position = new int[n];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.length; i++) {
            int course = order[i];
            require(course >= 0 && course < n, "Course outside valid range");
            require(position[course] == -1, "Repeated course");
            position[course] = i;
        }
        for (int[] prerequisite : prerequisites) {
            require(position[prerequisite[1]] < position[prerequisite[0]],
                    "Prerequisite must precede dependent course");
        }
    }

    private static boolean courseReference(int n, int[][] prerequisites) {
        // Reversing every edge preserves cycles, so pair orientation is irrelevant here.
        boolean[][] reach = reachability(n, prerequisites);
        for (int vertex = 0; vertex < n; vertex++) {
            if (reach[vertex][vertex]) {
                return false;
            }
        }
        return true;
    }

    private static void courseTests() {
        courses("official simple prerequisite", 2, new int[][] {{1, 0}}, true, true);
        courses("official cycle", 2, new int[][] {{1, 0}, {0, 1}}, false, true);
        courses("official diamond, multiple valid orders", 4,
                new int[][] {{1, 0}, {2, 0}, {3, 1}, {3, 2}}, true, true);
        courses("official single", 1, new int[0][2], true, true);
        courses("disconnected with isolates", 6, new int[][] {{1, 0}, {3, 2}}, true, true);
        courses("cycle in later component", 6,
                new int[][] {{1, 0}, {3, 2}, {2, 3}}, false, true);
        courses("finished neighbor is not a cycle", 3,
                new int[][] {{1, 0}, {2, 0}, {1, 2}}, true, true);
        // Course Schedule I permits self-loops; Course Schedule II excludes them.
        run("Course Schedule I: self-loop", () -> {
            int[][] edges = {{0, 0}};
            require(!P07.canFinish(1, edges), "DFS must reject self-loop");
            require(!P08.canFinish(1, edges), "Kahn must reject self-loop");
            unchanged(new int[][] {{0, 0}}, edges, "Self-loop decisions");
        });
        // All 4096 loop-free directed graphs on four labeled vertices.
        for (int mask = 0; mask < (1 << 12); mask++) {
            List<int[]> edges = new ArrayList<>();
            int bit = 0;
            for (int a = 0; a < 4; a++) {
                for (int b = 0; b < 4; b++) {
                    if (a != b && (mask & (1 << bit++)) != 0) {
                        edges.add(new int[] {a, b});
                    }
                }
            }
            int[][] graph = edges.toArray(new int[edges.size()][]);
            courses("exhaustive graph " + mask, 4, graph, courseReference(4, graph), true);
        }
        for (int test = 0; test < 400; test++) {
            int n = 1 + RANDOM.nextInt(20);
            List<int[]> edges = new ArrayList<>();
            for (int a = 0; a < n; a++) {
                for (int b = 0; b < n; b++) {
                    if (a != b && RANDOM.nextDouble() < (test % 5) * 0.1) {
                        edges.add(new int[] {a, b});
                    }
                }
            }
            Collections.shuffle(edges, RANDOM);
            int[][] graph = edges.toArray(new int[edges.size()][]);
            courses("random directed " + test, n, graph, courseReference(n, graph), true);
        }
        for (int test = 0; test < 400; test++) {
            int n = 1 + RANDOM.nextInt(40);
            List<Integer> permutation = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                permutation.add(i);
            }
            Collections.shuffle(permutation, RANDOM);
            List<int[]> edges = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (RANDOM.nextBoolean()) {
                        edges.add(new int[] {permutation.get(j), permutation.get(i)});
                    }
                }
            }
            Collections.shuffle(edges, RANDOM);
            courses("random permuted DAG " + test, n,
                    edges.toArray(new int[edges.size()][]), true, true);
        }
        courses("2000 isolated", 2000, new int[0][2], true, true);
        // Reverse the dependency pairs so traversal follows a 2000-deep path from 0.
        int[][] chain = directedChain(2000, false);
        for (int[] edge : chain) {
            int temp = edge[0];
            edge[0] = edge[1];
            edge[1] = temp;
        }
        courses("2000 chain", 2000, chain, true, true);
        int[][] cycle = Arrays.copyOf(chain, 2000);
        cycle[1999] = new int[] {0, 1999};
        courses("2000 cycle", 2000, cycle, false, true);
        courses("70 complete directed graph", 70, completeDirected(70, false), false, true);
        int[][] maxEdges = new int[5000][2];
        int index = 0;
        for (int gap = 1; index < maxEdges.length; gap++) {
            for (int from = 0; from + gap < 2000 && index < maxEdges.length; from++) {
                maxEdges[index++] = new int[] {from + gap, from};
            }
        }
        courses("2000 vertices, 5000 edges, DAG", 2000, maxEdges, true, true);
        maxEdges[maxEdges.length - 1] = new int[] {0, 1999};
        courses("2000 vertices, 5000 edges, cyclic", 2000, maxEdges, false, true);
        // These edge counts are valid only for Course Schedule II.
        courses("II maximum dense DAG", 2000, completeDirected(2000, true), true, false);
        courses("II maximum 3998000 edges", 2000, completeDirected(2000, false), false, false);
    }

    private static int[][] completeDirected(int n, boolean acyclic) {
        int edgeCount = acyclic ? n * (n - 1) / 2 : n * (n - 1);
        int[][] edges = new int[edgeCount][2];
        int index = 0;
        for (int from = 0; from < n; from++) {
            for (int to = 0; to < n; to++) {
                if (from != to && (!acyclic || from < to)) {
                    edges[index][0] = to;
                    edges[index++][1] = from;
                }
            }
        }
        return edges;
    }
}
