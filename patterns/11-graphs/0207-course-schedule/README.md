# 0207 - Course Schedule

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/course-schedule/

## Problem

There are `numCourses` courses labeled `0` to `numCourses - 1`. You are given `prerequisites` where
`prerequisites[i] = [a, b]` means you must take course **b before** course **a**. Return `true` if
it is possible to finish every course (i.e. the prerequisite graph has **no cycle**), `false`
otherwise.

Signature:

    boolean canFinish(int numCourses, int[][] prerequisites)

Examples:

    Input:  numCourses = 2, prerequisites = [[1,0]]
    Output: true    (take 0, then 1)

    Input:  numCourses = 2, prerequisites = [[1,0],[0,1]]
    Output: false   (0 needs 1 and 1 needs 0 -> deadlock cycle)

## Intuition

Model the courses as nodes and each prerequisite pair `[a, b]` as a directed edge `b -> a` ("b is a
prerequisite of a", so you can only walk forward once b is done). The question "can every course be
finished?" reduces to "does this directed graph have a cycle?" -- if it does, the courses in the
cycle can never be started, so finishing is impossible.

Cycle detection in a directed graph is the textbook 3-color DFS. Each node is in one of three
states:

- `0` -- **unvisited**: never reached.
- `1` -- **visiting**: on the current recursion stack (we entered it but have not finished all its
  descendants yet).
- `2` -- **done**: fully processed, all paths out explored.

The crucial observation: a **cycle exists iff DFS reaches a node that is currently `visiting`** (a
"back edge"). When DFS finishes a node and unwinds, flip it from `1` to `2` so it is never mistaken
for a cycle partner again.

Trigger signals: "prerequisites", "course schedule", "build order" -- all point to topological
sort / cycle detection.

## Pseudocode

    function canFinish(numCourses, prerequisites):
        build adjacency list: for each [a, b], add edge b -> a
        state = array of 0s, length numCourses

        for each course c in 0..numCourses-1:
            if hasCycle(c): return false
        return true

    function hasCycle(node):
        if state[node] == 1: return true        # back edge -> cycle
        if state[node] == 2: return false       # already cleared, skip
        state[node] = 1                         # entering the recursion stack
        for each neighbor of node:
            if hasCycle(neighbor): return true
        state[node] = 2                         # fully processed
        return false

The two early returns are the whole algorithm: hitting a `1` proves a cycle, hitting a `2` is just
memoisation (no need to re-explore a cleared node).

## Java Solution

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    private List<List<Integer>> adj;
    private int[] state;

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : prerequisites) {
            adj.get(edge[1]).add(edge[0]);          // edge[1] is a prerequisite of edge[0]
        }

        state = new int[numCourses];                // all 0 = unvisited by default
        for (int c = 0; c < numCourses; c++) {
            if (hasCycle(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCycle(int node) {
        if (state[node] == 1) {
            return true;
        }
        if (state[node] == 2) {
            return false;
        }
        state[node] = 1;
        for (int neighbor : adj.get(node)) {
            if (hasCycle(neighbor)) {
                return true;
            }
        }
        state[node] = 2;
        return false;
    }
}
```

The adjacency list is built from each edge `b -> a` (not `a -> b`) because `[a, b]` means "b comes
first" -- walking from b reaches everything that becomes available after b. The `state` array is
`int[]` (not `boolean[]`) because we need three values, and Java zero-initialises `int[]` to `0`
(unvisited) for free. We store `adj` and `state` in fields so `hasCycle` takes only the node index,
matching the recursive-helper convention from the crash course. Short-circuiting the loop with
`return true` as soon as a back edge is found means we never explore the rest of a doomed branch.

## Complexity

    Time:  O(V + E)  -- each node is finished (state 2) at most once; each edge is walked once
    Space: O(V + E)  -- adjacency list O(V + E) + state array O(V) + recursion up to O(V)

## Dry-Run

Input `numCourses = 4`, `prerequisites = [[1,0],[2,1],[3,2],[1,3]]`.

Edges after build: `0 -> 1`, `1 -> 2`, `2 -> 3`, `3 -> 1`. (Course 1 has two prerequisites: 0 and 3.)

Adjacency list:
- `0: [1]`
- `1: [2]`
- `2: [3]`
- `3: [1]`

Call `canFinish`. Start outer loop at `c = 0`, call `hasCycle(0)`.

| Step | hasCycle(node) | state before | Visited neighbor? | state after | Returns |
|-----:|----------------|--------------|-------------------|-------------|---------|
| 1    | hasCycle(0)    | s=[0,0,0,0]  | enter 0           | s=[1,0,0,0] | recurse |
| 2    | hasCycle(1)    | s=[1,0,0,0]  | enter 1           | s=[1,1,0,0] | recurse |
| 3    | hasCycle(2)    | s=[1,1,0,0]  | enter 2           | s=[1,1,1,0] | recurse |
| 4    | hasCycle(3)    | s=[1,1,1,0]  | enter 3           | s=[1,1,1,1] | recurse |
| 5    | hasCycle(1)    | s=[1,1,1,1]  | state[1] == 1     | --          | **true (cycle!)** |

Step 5 hits node `1`, which is already `1` (visiting, on the stack). That is the back edge -- the
edge `3 -> 1` closes the cycle `1 -> 2 -> 3 -> 1`. `hasCycle` returns `true`, which propagates all
the way up, and `canFinish` returns `false`. No course in the cycle can be started, so the schedule
is impossible.

For a cycle-free input like `[[1,0],[2,1],[3,2]]` (chain `0 -> 1 -> 2 -> 3`), no back edge is ever
hit: every node reaches state `2`, `hasCycle` always returns `false`, and `canFinish` returns
`true`.

## Common mistakes

- Confusing the edge direction. `prerequisites[i] = [a, b]` means b unlocks a, so the edge is
  `b -> a`. Reversing it (`a -> b`) still detects *a* cycle if one exists, but on asymmetric inputs
  the wrong direction can mask or invent cycles. Pick one convention and stick to it.
- Using a `boolean[] visited` (two states) instead of a three-state `int[]`. With only two states
  you cannot tell "currently on the stack" from "fully done", so cross-edges to finished nodes are
  misreported as cycles, producing false negatives.
- Forgetting to flip state from `1` to `2` after the loop. Subsequent calls then re-explore the node
  and may misreport cycles.
- Returning `true` (cycle found) inside the neighbor loop but continuing to iterate -- the early
  return is the whole point; without it you do pointless work.
- Initialising `state` to a non-zero sentinel. Java zero-fills `int[]`, which is exactly the
  "unvisited" code -- no manual init needed.

## Related problems

- [0210 - Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) - same graph, but
  also return the topological order (use Kahn's BFS or postorder DFS with reversal).
- [0133 - Clone Graph](../0133-clone-graph/) - same DFS-over-adjacency-list reflex, different goal.
- [0684 - Redundant Connection](../0684-redundant-connection/) - cycle detection on an *undirected*
  graph, solved with Union-Find instead of DFS.
