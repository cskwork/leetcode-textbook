# 0133 - Clone Graph

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/clone-graph/

## Problem

Given a reference to a `node` in a **connected undirected graph**, return a **deep copy** (clone)
of the entire graph. Each node has an integer `val` and a `List<Node> neighbors`. The clone must
share no references with the original -- every node and every edge must be newly allocated. The
input may be `null` (empty graph), in which case return `null`.

Signature:

    Node cloneGraph(Node node)

Node class (defined in `Solution.java` per the book's node convention):

    class Node {
        public int val;
        public List<Node> neighbors;
    }

Example:

    Input:  adjList = [[2,4],[1,3],[2,4],[1,3]]   (4 nodes in a square with diagonals)
    Output: a deep copy whose structure equals the input

## Intuition

Same connectivity reflex as Number of Islands, but the graph is **explicit** (nodes with neighbor
lists) rather than implicit (a grid). The danger: an undirected graph has cycles, so naive DFS
loops forever. The fix is a `visited` map -- but here "visited" must remember not just *that* a node
was seen, but *which clone corresponds to it*, because every edge in the original must be mirrored
by an edge between the corresponding clones. So the map is `original -> clone`.

The recursive contract: `clone(node)` returns the clone of `node`, having already wired up all its
edges. To do that, allocate the clone, register it in the map *before* recursing (so cycles see it),
then for each original neighbor recurse to get (or look up) the neighbor's clone and append it to
this clone's neighbor list.

## Pseudocode

    function cloneGraph(start):
        if start is null: return null
        clones = empty map: original node -> clone node
        return clone(start, clones)

    function clone(node, clones):
        if node is already a key in clones:
            return clones[node]                  # already built -- avoids infinite loop on cycles
        newNode = make a node with node.val and empty neighbor list
        clones[node] = newNode                   # register BEFORE recursing (cycle break)
        for each neighbor of node:
            add clone(neighbor, clones) to newNode.neighbors
        return newNode

Registering the clone in the map *before* the loop is the cycle-breaker: when DFS wraps back around
to a node already in the map, the early return hands back the existing clone instead of recursing.

## Java Solution

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node {
    public int val;
    public List<Node> neighbors;
    public Node() { val = 0; neighbors = new ArrayList<>(); }
    public Node(int val) { this.val = val; neighbors = new ArrayList<>(); }
    public Node(int val, List<Node> neighbors) {
        this.val = val;
        this.neighbors = neighbors;
    }
}

class Solution {
    public Node cloneGraph(Node node) {
        if (node == null) {
            return null;
        }
        return dfs(node, new HashMap<>());
    }

    private Node dfs(Node node, Map<Node, Node> clones) {
        if (clones.containsKey(node)) {
            return clones.get(node);
        }
        Node copy = new Node(node.val);
        clones.put(node, copy);
        for (Node neighbor : node.neighbors) {
            copy.neighbors.add(dfs(neighbor, clones));
        }
        return copy;
    }
}
```

`Node` is defined at top level in `Solution.java` (per the book's node convention, section "Node
class convention" of TEMPLATE.md) so the test file can compile against it without redefining it.
The `HashMap<Node, Node>` is the visited map -- it doubles as a record of "which clone did I make
for this original". The `containsKey` check is the only thing preventing infinite recursion on the
back-edge of a cycle, so the order matters: `put` *then* recurse, never the other way around. We
pass the map down the recursion rather than storing it in a field so the method is reentrant -- two
calls on the same `Solution` instance won't poison each other.

## Complexity

    Time:  O(V + E)  -- each node is cloned once; each edge is followed twice (once from each end)
    Space: O(V)      -- the clones map plus recursion depth up to V

## Dry-Run

Input graph (square with diagonals):

```
    1 --- 2
    |  \  |
    |   \ |
    4 --- 3
```

`adjList = [[2,4],[1,3],[2,4],[1,3]]`. Call `cloneGraph(node1)`.

| Call            | clones map after put      | neighbors appended                  | Returns |
|-----------------|---------------------------|--------------------------------------|---------|
| dfs(1)          | {1:1'}                     | dfs(2), dfs(4) (in progress)        | 1'      |
| dfs(2)          | {1:1', 2:2'}               | dfs(1) -> hit map -> 1'; dfs(3)     | 2'      |
| dfs(3)          | {1:1', 2:2', 3:3'}         | dfs(2) -> hit map -> 2'; dfs(4)     | 3'      |
| dfs(4)          | {1:1', 2:2', 3:3', 4:4'}   | dfs(1) -> hit map -> 1'; dfs(3) -> 3'| 4'      |

(prime `1'` means "the clone of node 1".) The cycle `1 -> 2 -> 1` is broken because when `dfs(2)`
tries to recurse into node `1`, node `1` is already in the map and the early return hands back
`1'`. After everything settles, `1'.neighbors = [2', 4']`, `2'.neighbors = [1', 3']`, etc. -- a
perfect structural copy sharing no references with the original.

## Common mistakes

- Recursing before putting the clone in the map. The back-edge of any cycle then re-enters
  `cloneGraph` on an unregistered node and the program loops forever (then `StackOverflowError`).
- Using a `Set<Node>` for visited instead of a `Map<Node, Node>`. A set tells you "I've seen this
  node" but not "which clone did I make for it", so you cannot wire up the back-edge.
- Allocating nodes lazily (only when first needed) and forgetting to populate `neighbors`. The
  clone must have *all* its edges; check by walking `clone.neighbors` after the call.
- Returning the original node by mistake. The whole point is a deep copy; verify with
  `clone != original` for the root.
- Reusing a `Map` field across calls. Two calls to `cloneGraph` on the same `Solution` would share
  state and the second call could read stale clones. Pass the map as a parameter instead.

## Related problems

- [0138 - Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/)
  - same clone-with-a-map idea, applied to a linked list with extra pointers.
- [0200 - Number of Islands](../0200-number-of-islands/) - same DFS-over-neighbors reflex, on an
  implicit grid graph instead of explicit nodes.
- [0207 - Course Schedule](../0207-course-schedule/) - DFS over an explicit adjacency list with a
  visited structure, but for cycle detection rather than copying.
