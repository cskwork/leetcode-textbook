# 0684 - Redundant Connection

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/redundant-connection/

## Concepts used

- **Graph** -- a set of nodes connected by edges; here each node is a person/vertex and each given
  pair `[u, v]` is an edge. [glossary](../../../docs/10-glossary.md#graph)
- **Cycle** -- a path that returns to its starting node; the one extra edge in this input creates
  exactly one cycle. [glossary](../../../docs/10-glossary.md#cycle)
- **Union-Find (Disjoint Set Union)** -- a structure that tracks which items are in the same group,
  answering "are these two already connected?" in near-O(1) time.
  [glossary](../../../docs/10-glossary.md#union-find-disjoint-set-union)

## Problem

A tree on `n` nodes has exactly `n - 1` edges. You are given a graph that started as a tree with
`n` nodes labeled `1..n`, plus **one extra edge** that turned it into a graph with exactly one
cycle. The input is `edges`, an array of `[u, v]` pairs added one at a time. Return the **edge**
that, if removed, leaves a tree. If multiple answers are possible, return the **last** such edge in
the input order.

Signature:

    int[] findRedundantConnection(int[][] edges)

Examples:

    Input:  edges = [[1,2],[1,3],[2,3]]
    Output: [2,3]    (the third edge closes the cycle 1-2-3)

    Input:  edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]
    Output: [1,4]    (the fourth edge closes the cycle 1-2-3-4)

## Intuition

Think about friend circles. We add friendships one at a time. After a few friendships, some people
belong to the same circle: A knows B, B knows C, so A, B, and C are one circle even though A and C
were never directly introduced. Now suppose the next "friendship" to add is a direct link between A
and C. But A and C are *already* in the same circle (connected through B) -- so this new direct link
adds nothing and is **redundant**. That redundant link is exactly the edge we must remove.

Model this as a [graph](../../../docs/10-glossary.md#graph): each person is a **node** (a single
item), each friendship an **edge** (a link between two nodes). The setup starts as a tree -- a graph
with no loops -- and one extra edge is added, which creates exactly one
[cycle](../../../docs/10-glossary.md#cycle) (a path that returns to its start). Walking the edges in
the order they were given, the first edge whose two endpoints are *already connected* through
earlier edges is the one that closes the cycle -- and that is our redundant edge. Returning the
instant we find it also satisfies the "last edge in input order" requirement, because we walk in
order and stop at the first (and guaranteed-unique) cycle-closer.

To answer "are these two already in the same circle?" cheaply, we use
[Union-Find](../../../docs/10-glossary.md#union-find-disjoint-set-union). Start with every node in its
own group. For each edge `[u, v]`, run `find(u)` and `find(v)` -- *which group is each one in?* If
they report the same group, u and v are already connected, so this edge is redundant: return it. If
they differ, `union` the two groups into one (now u's whole circle and v's whole circle merge) and
move on.

Trace `edges = [[1,2],[1,3],[2,3]]`: everyone starts alone. `[1,2]`: 1 and 2 are in different groups
-> union them into one circle `{1,2}`. `[1,3]`: 1 and 3 differ -> union into `{1,2,3}`. `[2,3]`:
`find(2)` and `find(3)` now report the *same* group -- 2 and 3 are already connected through 1 -- so
`[2,3]` is redundant; return it.

### Checkpoint A -- Already in the same circle?

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** When is an edge `[u, v]` the redundant one we return?
- a) When `u` equals `v`
- b) When `find(u) == find(v)` -- the two endpoints are already connected through earlier edges
- c) When it is the very last element of the array

<details><summary>Show answer</summary>

**(b)** -- if the endpoints already share a root, adding this edge would close a loop, so it is the redundant connection.

</details>

**Q2 (comprehend).** Why does returning the FIRST edge where `find(u) == find(v)` also satisfy "return the last such edge in input order"?
- a) It does not -- you must scan the whole array first
- b) The problem guarantees exactly one cycle, so there is exactly one cycle-closing edge; walking in order and stopping at it is correct
- c) Because the edges arrive sorted

<details><summary>Show answer</summary>

**(b)** -- with a unique redundant edge guaranteed, the first `find(u) == find(v)` encountered while scanning in order is the answer.

</details>

## Pseudocode

    function findRedundantConnection(edges):
        n = number of nodes (= number of edges, since one extra)
        parent = array where parent[i] = i for all i
        rank   = array of zeros

        for each [u, v] in edges:
            ru = find(u)
            rv = find(v)
            if ru == rv:
                return [u, v]                  # already connected -> this edge is redundant
            union(ru, rv)                      # merge the two sets

    function find(x):
        if parent[x] != x:
            parent[x] = find(parent[x])        # path compression
        return parent[x]

    function union(ra, rb):                    # ra, rb are roots
        if rank[ra] < rank[rb]: swap(ra, rb)   # attach shorter under taller
        parent[rb] = ra
        if rank[ra] == rank[rb]: rank[ra] += 1 # grew the taller tree by one

Returning the edge the instant `find(u) == find(v)` guarantees the "last edge in input order that
closes a cycle" -- we walk in input order and stop at the first cycle-closer, which by problem
guarantee is the unique redundant edge.

## Java Solution

```java
class Solution {
    private int[] parent;
    private int[] rank;

    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        parent = new int[n + 1];
        rank = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
        }

        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            int ru = find(u);
            int rv = find(v);
            if (ru == rv) {
                return edge;
            }
            union(ru, rv);
        }
        return new int[]{-1, -1};              // unreachable per problem guarantees
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    private void union(int ra, int rb) {
        if (rank[ra] < rank[rb]) {
            int t = ra; ra = rb; rb = t;
        }
        parent[rb] = ra;
        if (rank[ra] == rank[rb]) {
            rank[ra]++;
        }
    }
}
```

The arrays are sized `n + 1` because nodes are labeled `1..n`, not `0..n-1`. `find` recurses with
`parent[x] = find(parent[x])` -- a single line that both walks to the root and flattens the path on
the way back up (path compression). `union` swaps the roots so the taller tree always becomes the
parent, then increments rank only when the two trees were equally tall (union by rank). The trailing
`return new int[]{-1,-1}` is dead code per the problem guarantee (there is always a redundant edge)
but keeps the compiler happy about a missing return.

## Complexity

    Time:  O(n * alpha(n)) ~= O(n)  -- n edges, each find/union is amortised inverse-Ackermann
    Space: O(n)                     -- parent and rank arrays

## Dry-Run

Input `edges = [[1,2],[1,3],[2,3]]` (n = 3). Initial: `parent = [_,1,2,3]` (index 0 unused),
`rank = [0,0,0,0]`.

| Step | Edge    | find(u) | find(v) | Already same root? | Action        | parent after | rank after |
|-----:|---------|---------|---------|--------------------|---------------|--------------|------------|
| 1    | [1,2]   | 1       | 2       | no                 | union(1,2)    | [_,1,1,3]    | [0,1,0,0]  |
| 2    | [1,3]   | 1       | 3       | no                 | union(1,3)    | [_,1,1,1]    | [0,1,0,0]  |
| 3    | [2,3]   | find(2)=1 (path compressed) | 1 | **yes** | **return [2,3]** | -- | -- |

At step 3, both `2` and `3` already root at `1`, so the edge `[2,3]` is redundant -- it would close
the triangle. Return `[2,3]`.

For input `[[1,2],[2,3],[3,4],[1,4],[1,5]]`: the first three edges link `1-2-3-4` into one set
(rooted at `1`). The fourth edge `[1,4]` would re-connect two nodes already in that set -- it is
returned as the redundant edge. The fifth edge `[1,5]` is never reached.

### Checkpoint B -- Union a new edge list

**Q1 (apply).** Trace `edges = [[1,2],[2,3],[3,1]]` (a triangle). Which edge is returned?
- a) `[1,2]`
- b) `[2,3]`
- c) `[3,1]` -- after `[1,2]` and `[2,3]`, nodes 1, 2, 3 share one root; `[3,1]` joins two already-connected nodes

<details><summary>Show answer</summary>

**(c)** -- `[1,2]` unions into {1,2}; `[2,3]` unions into {1,2,3}; for `[3,1]`, `find(3) == find(1)`, so it closes the triangle and is returned.

</details>

**Q2 (analyze).** What goes wrong if you omit path compression and union by rank?
- a) Wrong answers
- b) The structure can degenerate into a tall linked list, making each `find` `O(n)` and the whole solution `O(n^2)` on adversarial inputs
- c) It throws `NullPointerException`

<details><summary>Show answer</summary>

**(b)** -- the two optimizations keep the trees shallow; without them a bad input order builds a chain and every `find` walks its full length.

</details>

**Q3 (transfer).** How would you adapt Union-Find to COUNT the number of separate friend groups after all edges?

<details><summary>Show answer</summary>

After processing every edge, count the nodes `i` where `find(i) == i` -- each set has exactly one root, so the number of roots equals the number of groups.

</details>

## Common mistakes

- Omitting path compression or union by rank. A naive Union-Find degenerates to a linked list and
  hits `O(n^2)` on adversarial inputs. Both optimizations are 3 lines each; never skip them.
- Implementing `find` iteratively but forgetting to compress. The recursive form
  `parent[x] = find(parent[x])` compresses for free; the iterative form needs a second pass to
  re-point each node on the path.
- Allocating arrays of size `n` instead of `n + 1`. Nodes are `1`-indexed, so `parent[n]` would
  throw `ArrayIndexOutOfBoundsException`.
- Building an adjacency list and running DFS for cycle detection per edge -- correct but `O(n^2)`.
  Union-Find's incremental "are they already connected?" check is exactly the right tool.
- Returning the first edge of the cycle instead of the last in input order. The problem guarantees
  a unique answer *when edges are processed in order*, so walking in order and returning on the
  first `find(u) == find(v)` is correct.

## Related problems

- [0547 - Number of Provinces](https://leetcode.com/problems/number-of-provinces/) - Union-Find to
  count connected components.
- [0128 - Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/)
  - alternative use of Union-Find for value grouping.
- [0207 - Course Schedule](../0207-course-schedule/) - cycle detection on a *directed* graph; DFS
  with 3-color states is required there, Union-Find only works for undirected cycles.
