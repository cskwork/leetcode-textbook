# 0684 - Redundant Connection

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/redundant-connection/

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

A tree cannot contain a cycle, so "the extra edge" is exactly the first edge whose two endpoints are
**already connected** by previously added edges. The data structure that answers "are these two in
the same group?" in (amortised) `O(1)` is **Union-Find** (disjoint set). Walk the edges in input
order; for each `[u, v]`, run `find(u)` and `find(v)` -- if they share a root, adding this edge
would close a cycle, so return it; otherwise `union` the two sets and continue.

Trigger signals: "redundant", "already connected", "cycle in an undirected graph" -- all point to
Union-Find. This problem is the canonical Union-Find introduction.

The two optimizations that make Union-Find fast are **path compression** (in `find`, point every
node straight at the root) and **union by rank** (always attach the shorter tree under the taller).
With both, each operation is amortised inverse-Ackermann -- effectively `O(1)`.

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
