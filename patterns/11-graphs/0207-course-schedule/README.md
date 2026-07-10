# 0207 - Course Schedule

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/course-schedule/

## Concepts used

- **Graph** -- a set of nodes connected by edges; here each course is a node and each prerequisite
  rule is an edge. [glossary](../../../docs/10-glossary.md#graph)
- **Cycle** -- a path that returns to its starting node; in a course list a cycle is a deadlock where
  no course in the loop can ever be taken. [glossary](../../../docs/10-glossary.md#cycle)
- **Topological sort** -- an ordering of a directed graph's nodes so every edge points forward; it
  exists only when the graph has no cycle.
  [glossary](../../../docs/10-glossary.md#topological-sort)

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

You are planning which courses to take over many semesters. Some courses require a prerequisite:
"you may take course *a* only after course *b*". Now ask: *can every course eventually be taken?*
The answer is yes -- unless the rules contain a deadlock. A deadlock is when course A needs B, B
needs C, and C needs A: each waits on another, so none can ever be taken first. That loop is the
whole obstacle; everything else can be sequenced.

Model this as a [graph](../../../docs/10-glossary.md#graph): each course is a **node** (a single
item), and each rule "b before a" is a one-way **edge** drawn from b to a. Because the edges point
one way only (b unlocks a, not vice-versa), this is a *directed* graph -- an edge has a direction. A
[cycle](../../../docs/10-glossary.md#cycle) in that graph (a path that loops back to its start) is
exactly a prerequisite deadlock. So "can I finish all courses?" becomes "does this directed graph
have a cycle?". If it does, the courses on the loop can never be started, so finishing is impossible;
if it does not, a valid order exists (that order is called a
[topological sort](../../../docs/10-glossary.md#topological-sort) -- an arrangement where every edge
points forward, which is only possible when there is no cycle).

How do we detect a cycle? Walk the graph with [DFS](../../../docs/10-glossary.md#dfs-depth-first-search)
(depth-first search: follow edges as deep as you can, then back up). The trick is to colour each node
with one of three states: *unvisited* (never reached), *visiting* (we are currently inside its chain
of follow-the-edges -- it is on the active path), and *done* (we finished exploring everything
reachable from it). The decisive moment: if, while following edges, we ever step onto a node that is
already *visiting*, we have walked in a loop back to a node we are currently inside -- that is a
cycle. When we finish a node and back out, we mark it *done* so it is never mistaken for a cycle
later.

Trace `numCourses = 4` with rules `[[1,0],[2,1],[3,2],[1,3]]` -- edges `0 -> 1`, `1 -> 2`, `2 -> 3`,
`3 -> 1`. Start at 0 (visiting) -> 1 (visiting) -> 2 (visiting) -> 3 (visiting) -> 1. But 1 is
already *visiting* (it is on our active path 0 -> 1 -> 2 -> 3). That back-step onto an in-progress
node is the cycle: `1 -> 2 -> 3 -> 1`. Return false -- the schedule is impossible.

### Checkpoint A -- Spot the deadlock

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** In the 3-color DFS, what does state `1` ("visiting") mean?
- a) The node is fully processed and cleared
- b) The node is currently on the active recursion path -- entered but not yet finished
- c) The node has never been visited

<details><summary>Show answer</summary>

**(b)** -- `1` marks nodes on the current DFS stack; reaching a node that is already `1` means you walked in a loop back to an ancestor, i.e. a cycle.

</details>

**Q2 (comprehend).** Why is a two-state `boolean[] visited` not enough here?
- a) Booleans use more memory
- b) With only seen/not-seen you cannot tell "currently on the stack" from "fully done", so an edge to a finished node gets misreported as a cycle
- c) Booleans are slower than ints

<details><summary>Show answer</summary>

**(b)** -- the third state lets a cross-edge to an already-finished node be recognized as harmless, not as a cycle. Two states cannot make that distinction.

</details>

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

### Checkpoint B -- Trace a new schedule

**Q1 (apply).** Trace `numCourses = 3`, `prerequisites = [[0,1],[1,2]]` (edges `1 -> 0`, `2 -> 1`, a chain). What is returned?
- a) `false` -- a cycle
- b) `true` -- the chain `2 -> 1 -> 0` has no back edge, so the schedule is valid (take 2, then 1, then 0)
- c) an error

<details><summary>Show answer</summary>

**(b)** -- DFS walks `0 -> 1 -> 2` with no node ever revisiting a `visiting` ancestor, so every node reaches state `2` and no cycle is reported.

</details>

**Q2 (analyze).** What breaks if you forget to set `state[node] = 2` after the neighbor loop?
- a) Nothing changes
- b) Nodes never reach "done", so later calls re-explore them and may misreport cycles; it also wastes work
- c) It always returns `true`

<details><summary>Show answer</summary>

**(b)** -- without the flip to `2`, a node stays `1` and any later edge into it looks like a back edge, producing false cycle reports.

</details>

**Q3 (transfer).** How would you also RETURN a valid order to take the courses (Course Schedule II)?

<details><summary>Show answer</summary>

Append each node to a list at the moment its state flips to `2` (postorder), then reverse that list -- the reversal is a valid topological order.

</details>

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
