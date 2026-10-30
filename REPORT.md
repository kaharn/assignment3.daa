# DAA Assignment 3 — Report and Defense

Solutions preserve signatures and inputs and support Java 8. Explicit stacks avoid recursion limits. V denotes vertices, E edges, and n cities or points. Space excludes inputs but includes constructed graphs.

## Problems 1–2: Number of Provinces

**Problem 1 — DFS.** Count components using a visited array and integer stack. For each unvisited city, increment the count and explore matrix neighbors, marking cities when pushed. Each search reaches exactly one component; visitation prevents recounting. Scanning every row takes O(n²) time and O(n) auxiliary space. Test: `[[1,1,0],[1,1,0],[0,0,1]]` returns `2`.

**Problem 2 — DSU.** Parent and size arrays initially represent n singleton sets. Examine the upper matrix triangle and union roots of connected cities, attaching the smaller tree to the larger. Path halving compresses searches. Decrement the component count only after merging distinct roots. Sets represent connectivity through processed edges, so the final count is correct. Time is O(n² α(n)), with inverse Ackermann function α; space is O(n). The same matrix returns `2`.

DFS explores reachability directly; DSU merges sets. Both require scanning the matrix.

## Problems 3–4: Minimum Spanning Tree

**Problem 3 — Kruskal.** Construct Manhattan-distance edges for every point pair. Sort the edge array, then use DSU parent and size arrays to reject cycles. Add successful union costs; stop after n−1 edges. Each chosen edge is a cheapest connection across components; the cut property makes it safe for an MST. Time is O(E log E) = O(n² log n); space is O(n²). Test: `[[0,0],[2,2],[3,10],[5,2],[7,0]]` returns `20`.

**Problem 4 — Prim.** A membership array records the growing tree; a distance array stores each outside point's cheapest connection to it. Start with point zero, repeatedly select the minimum distance, and update remaining distances. Every selection is a cheapest edge crossing the tree boundary, so the cut property proves correctness. Linear selection and updates give O(n²) time and O(n) space. The same five points return `20`.

Kruskal sorts all edges; Prim calculates distances as needed. Both accumulate in long. Distances fit int; an x-sorted spanning path bounds the optimum by 2,000,000n, also within int for n ≤ 1000.

## Problems 5–6: Software Dependency Cycles

**Problem 5 — Kosaraju.** Build forward and reversed adjacency arrays. A DFS stack, visited array, and neighbor cursors produce finishing order. Reset visitation and explore the reversed graph in decreasing finish order, counting searches. This ordering prevents crossing into another unprocessed SCC while preserving internal reachability. Each search therefore counts one SCC, including singletons. Time and space are O(V+E). Test: n=5, edges `[[0,1],[1,2],[2,0],[2,3],[3,4]]` returns `3`.

**Problem 6 — Tarjan.** Use adjacency arrays, discovery times, low-links, membership flags, neighbor cursors, and separate DFS and component stacks. Discover vertices, propagate child low-links on return, and use discovery times for edges to vertices still on the component stack. Ignore completed SCCs. When low[v] equals discovery[v], pop through v: these vertices form one maximal mutually reachable group. Count that group. Time and space are O(V+E). The same dependency example returns `3`.

Kosaraju needs two traversals and a reversed graph; Tarjan needs one traversal with careful low-link bookkeeping.

## Problems 7–8: Course Schedule

Both store prerequisite pairs [a,b] as edges b→a.

**Problem 7 — DFS.** Adjacency arrays, neighbor cursors, a stack, and unseen/active/finished states detect cycles. Explore every unseen course; an active-ancestor edge closes a cycle and returns false. Every directed cycle creates such an edge, so completing all searches proves feasibility. Time and space are O(V+E). Test: n=2, `[[1,0],[0,1]]` returns `false`.

**Problem 8 — Kahn.** Build adjacency arrays and indegrees. Enqueue zero-indegree courses, remove them, and decrement their successors' indegrees. Return true only if all courses are processed. Every removed course has satisfied prerequisites; any remaining subgraph without a zero-indegree vertex contains a cycle. Time and space are O(V+E). The same cycle returns `false`.

DFS detects an active-path cycle; Kahn detects incomplete processing.

## Problems 9–10: Course Schedule II

**Problem 9 — DFS ordering.** Use adjacency arrays, three states, neighbor cursors, a stack, and an output array. Reject active-ancestor edges; otherwise write each finished course from the output array's end. In a DAG, every edge's destination finishes before its source, so reverse finishing order respects prerequisites. Time and space are O(V+E). Test: n=2, `[[1,0]]` returns `[0,1]`.

**Problem 10 — Kahn ordering.** Use adjacency arrays, indegrees, and an array queue that also stores the answer. Append courses when their indegrees reach zero and process outgoing edges. Each appended course follows its prerequisites, proving validity. Return an empty array if processing is incomplete. Time and space are O(V+E). The same example returns `[0,1]`.

DFS reverses completion order; Kahn records availability order. Different valid answers are acceptable.

## Testing

`tests/Assignment3Tests.java` passed **6,613 cases; zero failed**, with seed 20261030. Cases check multiple algorithms, official examples, unchanged inputs, random and exhaustive graphs, reachability references, exhaustive tiny spanning trees, and prerequisite positions. Stress cases include 200 cities, 1000 points, 100,000 SCC vertices with 200,000 edges, 2000-course chains, and 3,998,000 Course Schedule II edges, under a 256 KB thread stack and 512 MB heap limit.

Reproduce with JDK 9 or later, targeting Java 8:

```sh
mkdir -p /private/tmp/assignment3-daa-build
javac --release 8 -encoding UTF-8 -Xlint:all,-options -Werror -d /private/tmp/assignment3-daa-build src/*.java tests/Assignment3Tests.java
java -ea -Xss256k -Xmx512m -cp /private/tmp/assignment3-daa-build Assignment3Tests
```

On JDK 8, omit `--release 8`.

## Conclusion

The paired solutions independently implement the assigned algorithms. Their invariants explain correctness, while automated comparisons and boundary tests support the implementations.
