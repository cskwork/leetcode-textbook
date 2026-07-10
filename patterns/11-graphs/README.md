# Pattern 11 - Graphs

## What this pattern is

A graph is a set of **nodes** (also called vertices) connected by **edges**. Two nodes joined by
an edge are **neighbors**. LeetCode rarely hands you a literal "Graph" object; instead you get one
of two disguises:

- **An implicit graph -- a grid.** A 2-D array of characters or ints is a graph in disguise: each
  cell is a node, and its (up to four) orthogonal neighbors are the edges. `['1' / '0']` grids
  (Number of Islands), elevation grids (Pacific Atlantic Water Flow), and color grids (Flood Fill)
  all work this way. You do not build an adjacency list -- the grid itself *is* the graph, and a
  cell's neighbors are just `(row +/- 1, col)` and `(row, col +/- 1)`.
- **An explicit graph -- an adjacency list.** When the input is a list of edges (e.g.
  `[[0,1],[1,2],[2,3]]`) or a list of prerequisites (`[[1,0]]` means "course 1 needs course 0"),
  you are expected to build `Map<Integer, List<Integer>>` (or `List<List<Integer>>`) yourself,
  where each key maps to its neighbors. Redundant Connection and Course Schedule work this way.

The whole pattern is built on three primitives: **DFS** (recurse or stack -- good for connectivity,
counting, cycle detection), **BFS** (queue -- good for shortest path and level-by-level spreading),
and **Union-Find** (good for "are these two things already connected?"). Master these three and the
9 problems below become variations on a theme.

## When to apply it (trigger signals)

Scan the problem statement for any of these phrases:

| Trigger signal                                          | Likely technique                |
|---------------------------------------------------------|---------------------------------|
| "grid", "matrix", "islands", "connected regions"        | Grid DFS / BFS                  |
| "network", "friends", "connected components"            | Adjacency-list DFS / BFS        |
| "shortest path", "minimum steps", "minutes until"       | BFS (unweighted)                |
| "course schedule", "prerequisites", "build order"       | Topological sort / cycle detect |
| "can reach", "from X to Y"                              | DFS or BFS reachability         |
| "redundant", "already connected", "union"               | Union-Find                      |
| "clone", "deep copy graph"                              | DFS/BFS + visited map           |

Two words in particular are dead giveaways: **islands** (grid DFS, mark visited by mutating the
cell) and **shortest path on an unweighted graph** (BFS, never DFS -- DFS finds *a* path, BFS finds
the *shortest* one).

## The two core traversals

Every traversal needs a **visited** mechanism to avoid infinite loops on cycles. For grid problems
the cheapest visited-set is to mutate the cell in place (`'1' -> '2'`, or `'O' -> 'S'`), which costs
`O(1)` extra space and saves you from allocating a parallel `boolean[][]`. The trade-off: it
destroys the input, so do not do it if you will need the grid again afterwards.

### Technique A -- DFS (connectivity, counting, cycle detection)

Pick a start node, mark it visited, then recurse into every unvisited neighbor. Implemented either
recursively (simplest) or with an explicit stack (avoids stack overflow on huge inputs).

Pseudocode template (recursive, works for grid or adjacency list):

    function dfs(start):
        mark start as visited
        for each neighbor of start:
            if neighbor is not visited and neighbor is in bounds / exists:
                dfs(neighbor)

For a grid, "neighbors" are the four orthogonal cells; "visited" is the cell-mutation trick; "in
bounds" is `0 <= row < rows and 0 <= col < cols`.

For cycle detection on a directed graph (Course Schedule), upgrade "visited" to a **three-color
state**: `0 = unvisited`, `1 = visiting` (currently on the recursion stack), `2 = done` (fully
processed). If DFS reaches a node that is `visiting`, you just closed a cycle -- abort.

    function hasCycle(node):
        if state[node] == 1: return true        # back edge -> cycle
        if state[node] == 2: return false       # already cleared
        state[node] = 1                         # mark on the stack
        for each neighbor of node:
            if hasCycle(neighbor): return true
        state[node] = 2                         # fully processed
        return false

### Technique B -- BFS (shortest path, level-by-level spreading)

Push the start node into a **queue**, mark it visited, then repeat: pull one node out, look at its
neighbors, and any neighbor not yet visited gets marked, pushed, and (for shortest-path problems)
its distance recorded. The queue's FIFO order guarantees you visit nodes in increasing distance
order, which is *why* BFS finds shortest paths on unweighted graphs.

Pseudocode template:

    function bfs(start):
        queue = new queue
        push start, mark start as visited
        while queue is not empty:
            node = queue.pop()
            for each neighbor of node:
                if neighbor is not visited and in bounds / exists:
                    mark neighbor as visited
                    push neighbor

For "shortest path" or "minutes until done", snapshot the queue size at the start of each iteration
of the outer loop and drain exactly that many nodes -- each such pass is one **level** (one unit of
distance). The number of levels you drain is the answer.

### Multi-source BFS

Some problems (Rotting Oranges, Pacific Atlantic Water Flow) start not from one node but from *all
nodes that satisfy a property at once* (all rotten oranges; all cells touching an ocean). The trick:
push **every** source into the queue at the start, mark them all visited, then run ordinary BFS. The
first level drained is "1 step from *some* source", the second is "2 steps", and so on. This is the
only way to get correct shortest-distance-from-any-source behavior in `O(V+E)`.

    function multiSourceBFS(sources):
        queue = new queue
        for s in sources:
            mark s visited, push s into queue
        while queue not empty:
            ...same as BFS...

## Union-Find (disjoint set)

For problems phrased as "are these two already in the same group?" or "find the edge that creates a
cycle" (Redundant Connection), use **Union-Find**. Each node starts in its own set; `find(x)` returns
the representative (root) of x's set, and `union(a, b)` merges the sets containing a and b.

Two optimizations make it effectively constant-time per operation:

- **Path compression**: during `find`, point every node on the path straight at the root, so future
  queries are one hop.
- **Union by rank**: always attach the shorter tree under the taller one, so depth stays small.

Pseudocode:

    function find(x):
        if parent[x] != x:
            parent[x] = find(parent[x])     # path compression
        return parent[x]

    function union(a, b):
        ra = find(a), rb = find(b)
        if ra == rb: return false           # already same set -> this edge is redundant
        if rank[ra] < rank[rb]: swap(ra, rb)
        parent[rb] = ra                     # attach shorter under taller
        if rank[ra] == rank[rb]: rank[ra]++
        return true

With both optimizations, the amortized cost per operation is the inverse Ackermann function --
effectively `O(1)` for any realistic input.

## Topological sort (Kahn's BFS)

For "given prerequisites, can all courses finish?" problems, the alternative to DFS cycle detection
is Kahn's algorithm: repeatedly remove nodes with zero in-degree. If you can drain every node, the
graph is acyclic; if some nodes remain (all with non-zero in-degree), there is a cycle.

    function canFinish(numNodes, prerequisites):
        build adjacency list and in-degree array from prerequisites
        queue = all nodes with in-degree 0
        visitedCount = 0
        while queue not empty:
            node = queue.pop()
            visitedCount++
            for each neighbor of node:
                in-degree[neighbor] -= 1
                if in-degree[neighbor] == 0: push neighbor
        return visitedCount == numNodes

This folder's Course Schedule solution uses the 3-color DFS form (see Technique A) because it needs
no in-degree bookkeeping; the Kahn form is the natural choice when the problem also asks you to emit
the order itself (LC 210 Course Schedule II).

## The 9 problems in this pattern

| #    | Problem                              | Difficulty | Teaser                                                            |
|-----:|--------------------------------------|------------|-------------------------------------------------------------------|
| 0733 | [Flood Fill](./0733-flood-fill/)                  | Easy       | Change a connected region's color: simplest DFS warm-up.          |
| 0200 | [Number of Islands](./0200-number-of-islands/)    | Medium     | Count `1`-regions: DFS flood-fill, mutating the grid.             |
| 0695 | [Max Area of Island](./0695-max-area-of-island/)  | Medium     | DFS that *returns* the area of each region; track the max.        |
| 0130 | [Surrounded Regions](./0130-surrounded-regions/)  | Medium     | DFS from border `O`s to mark safe ones; flip the rest.            |
| 0994 | [Rotting Oranges](./0994-rotting-oranges/)         | Medium     | Multi-source BFS: all rotten oranges spread at once.              |
| 0417 | [Pacific Atlantic Water Flow](./0417-pacific-atlantic-water-flow/) | Medium | Two DFS passes from each ocean inward; intersect reachable sets.  |
| 0133 | [Clone Graph](./0133-clone-graph/)                | Medium     | DFS with a `visited -> clone` map; copy nodes and edges.          |
| 0207 | [Course Schedule](./0207-course-schedule/)        | Medium     | 3-color DFS cycle detection on the prerequisite graph.            |
| 0684 | [Redundant Connection](./0684-redundant-connection/) | Medium   | Union-Find with path compression and union by rank.               |

Read them roughly in order: 733, 200, 695 build the grid-DFS reflex; 130 and 417 add multi-pass
thinking; 994 introduces multi-source BFS; 133 turns the grid into an explicit node graph; 207
adds cycle detection; 684 finishes with Union-Find.

## Common pitfalls

- **Forgetting to mark visited before recursing / enqueuing.** If you mark *after* the recursive
  call returns (DFS) or *after* you pop from the queue (BFS), the same node gets pushed many times
  and the algorithm becomes exponential or infinite. Always mark the instant you decide to visit.
- **Out-of-bounds access.** The single most common grid bug. Check `0 <= r < rows && 0 <= c < cols`
  *before* indexing `grid[r][c]`, never after. Java throws `ArrayIndexOutOfBoundsException`.
- **Mutating the grid when you shouldn't.** In-place marking (`'1' -> '0'`) is fast and clean, but
  if a later part of your algorithm needs the original values (or if the caller reuses the input),
  you must allocate a separate `boolean[][] visited` instead.
- **Using DFS for shortest path.** On an unweighted graph DFS finds *a* path, not the *shortest*
  one. Use BFS for "minimum steps" / "minutes until" problems -- its level-by-level order is exactly
  what gives you shortest paths for free.
- **Confusing cycle-detection states.** In the 3-color DFS, `1` means "on the current recursion
  stack" and `2` means "fully processed". Forgetting to flip a node from `1` to `2` after its
  neighbors are done will report false cycles later.
- **Union-Find without path compression.** A naive Union-Find with no rank and no path compression
  degenerates into a linked list and gives `O(n)` per `find` -- worst case `O(n^2)` overall. Always
  implement both optimizations; they are 3 extra lines.
- **Multi-source BFS seeded one source at a time.** Pushing sources one-by-one and running BFS to
  completion between pushes gives wrong (too large) distances. Seed the queue with *all* sources in
  one shot before the main loop.
- **Deep recursion on huge grids.** A 1000x1000 all-`1` grid is one DFS call of depth 1,000,000 --
  Java's default stack will overflow. LeetCode test cases are small enough that recursion is fine,
  but be aware the iterative-stack form exists for pathological cases.

---

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** This whole pattern rests on three core primitives. Which set?
- a) Sorting, hashing, two pointers
- b) DFS, BFS, and Union-Find
- c) Recursion, loops, and arrays

<details><summary>Show answer</summary>

**(b)** -- DFS for connectivity/counting/cycles, BFS for shortest paths and level spreading, Union-Find for "are these already connected?".

</details>

**Q2 (pattern recognition).** New problem: "given a grid of rooms and a start cell, find the FEWEST STEPS to reach the exit." Which technique?
- a) DFS -- it finds a path
- b) BFS -- its level-by-level order gives the shortest path on an unweighted graph
- c) Union-Find

<details><summary>Show answer</summary>

**(b)** -- on an unweighted graph BFS reaches each cell at its minimum distance; DFS finds *a* path, not the shortest.

</details>

**Q3 (pattern recognition).** New problem: "a list of friendships; how many separate friend groups exist?" Which technique?
- a) Union-Find -- merge groups, then count the distinct roots
- b) BFS for shortest path
- c) 3-color cycle detection

<details><summary>Show answer</summary>

**(a)** -- each union merges two groups; the number of remaining roots (`find(i) == i`) is the group count.

</details>

**Q4 (apply).** On the grid below (4-directional), how many islands are there?

    1 0 1
    0 0 0
    1 0 1

- a) `1` -- the `1`s connect diagonally
- b) `4` -- no two `1`s share an edge, so each is its own island
- c) `2`

<details><summary>Show answer</summary>

**(b)** -- diagonals do not count, so none of the four `1`s touches another; each forms its own single-cell island.

</details>

**Q5 (design).** Sketch (in words, not code) how to solve "find every cell reachable from a start cell, moving only through neighbors whose value is at least the start cell's value."

<details><summary>Show answer</summary>

Run a DFS or BFS from the start, marking each visited cell; only step into a neighbor whose value is `>=` the start cell's value (pass that start value as the threshold). The marked cells are the answer.

</details>

---

Next problem: [0733 - Flood Fill](./0733-flood-fill/).
