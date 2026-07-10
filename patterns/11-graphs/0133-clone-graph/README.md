# 0133 - Clone Graph

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/clone-graph/

## Concepts used

- **Graph** -- a set of nodes connected by edges. Here each person/value is a node and each "these
  are linked" relationship is an edge. [glossary](../../../docs/10-glossary.md#graph)
- **Cycle** -- a path that returns to its starting node. In an undirected graph a cycle will send a
  naive copier into an infinite loop. [glossary](../../../docs/10-glossary.md#cycle)
- **Recursion** -- a function that calls itself on a smaller version of the same problem; needs a
  base case to stop. [glossary](../../../docs/10-glossary.md#recursion)

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

Imagine a hand-drawn map of a friendship circle: each person is a dot, each "these two are friends"
is a line between two dots. You want to photocopy the map onto a fresh sheet so that the copy shares
nothing with the original -- new dots, new lines -- yet has exactly the same shape. You cannot just
copy one dot and stop: a dot is meaningless without its friendship lines, so you must copy a dot
*and* re-tie all its lines to other copied dots.

In [graph](../../../docs/10-glossary.md#graph) terms: each person is a **node** (a single item),
each friendship is an **edge** (a link between two nodes), and a node's friends are its **neighbors**
(the nodes directly connected to it by an edge). This graph is *undirected* -- if A is B's neighbor,
B is A's. The trap with an undirected graph is the **[cycle](../../../docs/10-glossary.md#cycle)**:
a path that loops back to its start (A friends B, B friends C, C friends A). If we naively walk the
graph copying as we go, a cycle sends us around forever: A -> B -> A -> B...

The cure is a **visited** record -- but for copying it must remember more than "I saw this node". It
must remember *"which copy did I make for this original?"*, because every edge in the original has to
be mirrored by an edge between the matching copies. So the record is a map from *original node -> its
clone*. The [recursive](../../../docs/10-glossary.md#recursion) plan for one node: make its clone,
*register the clone in the map before doing anything else* (this is the cycle-breaker -- when the walk
wraps back around to a node already in the map, we just hand back its existing clone instead of
recursing), then for each original neighbor look up or build its clone and link it to this clone.

Trace a triangle (nodes 1-2-3, all friends). `clone(1)`: make 1', register `{1: 1'}`. Neighbor 2 --
`clone(2)`: make 2', register `{1: 1', 2: 2'}`. 2's neighbors are 1 and 3. `clone(1)`? 1 is already a
key in the map -> return 1' (cycle broken, no infinite loop). `clone(3)`: make 3', register
`{1: 1', 2: 2', 3: 3'}`. 3's neighbors are 1 and 2, both already in the map -> link 3'-1' and 3'-2'.
Back in `clone(2)`: link 2'-1' and 2'-3'. Back in `clone(1)`: link 1'-2'. Result: 1', 2', 3' form a
perfect triangle copy that shares no nodes with the original.

### Checkpoint A -- Photocopy the map

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why is the visited record a `Map<Node, Node>` (original -> clone), not a `Set<Node>`?
- a) Maps are faster than sets
- b) Every back-edge must be re-tied to the matching CLONE, so we must remember "which clone did I make for this original"
- c) Sets do not allow nodes as members

<details><summary>Show answer</summary>

**(b)** -- a set only says "I've seen this node"; copying must also wire each original edge to a clone edge, which requires looking up the clone by its original.

</details>

**Q2 (comprehend).** On the triangle 1-2-3, the walk `clone(1) -> clone(2) -> clone(1)` must not loop forever. What stops it?
- a) The triangle has no cycle
- b) When `dfs(2)` tries to recurse into node 1, node 1 is already a key in the map, so the early return hands back the existing clone 1'
- c) A global depth counter

<details><summary>Show answer</summary>

**(b)** -- registering each clone in the map BEFORE recursing means the back-edge hits `containsKey` and returns the existing clone instead of recursing. That is the cycle-breaker.

</details>

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

### Checkpoint B -- Wire a new graph

**Q1 (apply).** Two-node graph: node 1's only neighbor is node 2, and node 2's only neighbor is node 1. After `cloneGraph(node1)`, what is `clone1.neighbors[0]`?
- a) The original node 2
- b) The clone 2'
- c) `null`

<details><summary>Show answer</summary>

**(b)** -- `dfs(1)` makes 1', then for neighbor 2 calls `dfs(2)` which makes 2' and links it into 1'.neighbors. The clone shares no references with the original.

</details>

**Q2 (analyze).** What happens if you `put` the clone into the map AFTER the neighbor loop instead of before?
- a) The output is identical
- b) On any cycle the back-edge re-enters `dfs` on an unregistered node, recursing forever until `StackOverflowError`
- c) It returns the original node

<details><summary>Show answer</summary>

**(b)** -- put-before-recurse is the cycle-breaker; doing it after means the back-edge never finds the node in the map and the recursion never terminates.

</details>

**Q3 (transfer).** How would you clone a linked list whose nodes also have a random pointer (Copy List with Random Pointer)?

<details><summary>Show answer</summary>

Use the same original->clone map: first pass copy every node into the map, second pass wire `.next` and `.random` by looking up the clone of each original pointer in the map.

</details>

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
