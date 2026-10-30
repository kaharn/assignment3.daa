### Problem 6 - Software Dependency Cycles (5 points)

A software company maintains a collection of software modules.
Some modules depend on others: if module $A$ depends on module $B$, then $B$ must be available for $A$ to function correctly.

Unfortunately, the dependency structure may contain circular dependencies.

For example, module $A$ depends on module $B$,
module $B$ depends on module $C$,
and module $C$ depends on module $A$.

A group of modules forms a _circular dependency group_ if every module in the group can reach every other module by following dependency links.

You are given $n$ modules numbered from $0$ to $n-1$ and a list of directed dependencies.
Each dependency $[a, b]$ means that module $a$ depends on module $b$.

Your task is to determine the number of circular dependency groups.

Note: A module with no circular dependency may still form a group containing only itself.

Solve the problem in [Problem05.java](src/Problem05.java) using **Tarjan**'s Algorithm.

```java
public int countDependencyGroups(int n, int[][] dependencies){
  ...
}
```

#### Example 1
+ Input: `n = 5, dependencies = [[0,1], [1,2], [2,0], [2,3], [3,4]]`
+ Output: `3`
+ Explanation: there are `3` groups `{0, 1, 2}, {3}, {4}`.

#### Example 2
+ Input: `n = 4, dependencies = [[0,1], [1,0], [2,3], [3,2]]`
+ Output: `2`

#### Example 3
+ Input: `n = 4, dependencies = [[0,1], [1,2], [2,3]]`
+ Output: `4`

#### Constraints
+ $1 \leq n \leq 10^5$
+ $0 \leq dependencies.length \leq 2 \cdot 10^5$
+ $0 \leq dependencies[i][0], dependencies[i][1] < n$

Good Luck!
