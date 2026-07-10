# 0994 - Rotting Oranges

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/rotting-oranges/

## Concepts used

- **Graph** -- a set of nodes connected by edges. The grid is an implicit graph: each cell is a node,
  its up/down/left/right neighbors are its edges.
  [glossary](../../../docs/10-glossary.md#graph)
- **BFS (breadth-first search)** -- a traversal that visits all neighbors at the current distance
  before going any farther; it finds the shortest path on an unweighted graph.
  [glossary](../../../docs/10-glossary.md#bfs-breadth-first-search)
- **Queue** -- a first-in-first-out container (like a store line); BFS uses it to handle cells in the
  order they were discovered. [glossary](../../../docs/10-glossary.md#queue)

## Problem

Every minute, any fresh orange (`1`) that is 4-directionally adjacent to a rotten orange (`2`)
becomes rotten. Given an `m x n` `grid`, return the **minimum number of minutes** that must elapse
until no cell has a fresh orange. If that is impossible (some fresh orange can never be reached),
return `-1`. A `0` cell is empty.

Signature:

    int orangesRotting(int[][] grid)

Examples:

    Input:  grid = [[2,1,1],[1,1,0],[0,1,1]]
    Output: 4

    Input:  grid = [[2,1,1],[0,1,1],[1,0,1]]
    Output: -1    (the bottom-left orange is walled off and never rots)

    Input:  grid = [[0,2]]
    Output: 0     (no fresh oranges -> zero minutes)

## Intuition

Drop a stone into a still pond: a ripple spreads outward, one ring at a time, reaching farther edges
each second. Rot works the same way. The moment a rotten orange sits next to a fresh one, that fresh
one is doomed -- and one minute later it turns rotten and *becomes* a new source of rot that reaches
its own neighbors the minute after. So rot spreads in expanding rings, one minute per ring. The
question "how many minutes until everything that can rot has rotted?" is exactly "how many rings
until the ripple stops?".

Treat the grid as an implicit [graph](../../../docs/10-glossary.md#graph) (each cell a node, its
up/down/left/right cells its **neighbors** -- the cells directly beside it, joined by an edge).
Spreading "one ring per minute, reaching the closest untouched cells first" is precisely
[BFS](../../../docs/10-glossary.md#bfs-breadth-first-search) -- breadth-first search, which visits
all nodes at the current distance before any node farther away. BFS uses a
[queue](../../../docs/10-glossary.md#queue) (a first-in-first-out line, like at a store): cells enter
the queue in the order they are discovered and are processed in that same order, which is what keeps
the ripple even. Because BFS always reaches a cell via its shortest route, the minute-count it
produces is the true minimum -- a depth-first walk would not give the minimum, since it dives deep
and may reach a cell by a long detour.

One twist: rot does not start from one orange, it starts from *every* initially rotten orange at
once. So we seed the queue with all rotten cells together (this is called *multi-source* BFS), and we
count the fresh oranges up front so we know when the job is done.

Trace a 3x3 grid minute by minute:

```
2 1 1
1 1 0
0 1 1
```

Minute 0: the lone `2` at (0,0) is the seed; 6 fresh oranges exist. Minute 1: the ring reaches its
neighbors (0,1) and (1,0) -> they rot, fresh = 4. Minute 2: from those, (0,2) and (1,1) rot,
fresh = 2. Minute 3: (2,1) rots, fresh = 1. Minute 4: (2,2) rots, fresh = 0. Answer: 4. If the queue
empties while fresh oranges remain, those orphans are walled off by empty cells and can never rot --
return `-1`.

### Checkpoint A -- Ripples and minutes

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why BFS (not DFS) for "minimum minutes until all reachable oranges rot"?
- a) DFS is too slow for grids
- b) BFS visits cells in increasing-distance order, so the level count IS the minimum time; DFS dives deep and may reach a cell by a long detour
- c) BFS uses less memory

<details><summary>Show answer</summary>

**(b)** -- the queue's FIFO order drains one distance-ring per level, so the number of levels equals the true minimum time. DFS has no such distance ordering.

</details>

**Q2 (comprehend).** Why does `minutes` start at `-1` rather than `0`?
- a) To flag an error state
- b) The first while-loop pass drains the SEED level (the initially rotten oranges), during which no rot has spread yet; starting at `-1` makes that level end at `0`
- c) Because the grid may be empty

<details><summary>Show answer</summary>

**(b)** -- the seed level is not a spreading wave; the `-1` start cancels it out so the first real wave ends at `1`, the second at `2`, and so on.

</details>

## Pseudocode

    function orangesRotting(grid):
        queue = empty queue
        fresh = 0
        for each cell (r, c):
            if grid[r][c] == 2: enqueue (r, c)        # seed every rotten source
            else if grid[r][c] == 1: fresh += 1

        if fresh == 0: return 0                        # nothing to rot

        minutes = -1                                    # becomes 0 after draining the seed level
        while queue is not empty:
            levelSize = current queue size
            repeat levelSize times:                     # drain exactly one "wave"
                (r, c) = dequeue
                for each neighbor (nr, nc):
                    if in bounds and grid[nr][nc] == 1:
                        grid[nr][nc] = 2                # rot it (= mark visited)
                        fresh -= 1
                        enqueue (nr, nc)
            minutes += 1                                # one minute elapsed for this wave

        if fresh > 0: return -1                         # orphans remain
        return minutes

The `minutes = -1` start is deliberate: the first iteration of the while loop drains the *seed*
level (the initially rotten oranges), and rotting has not actually spread yet, so we should *not*
count that level. Starting at `-1` and incrementing at the end of each loop means the seed level
ends at `0` (correct -- no time elapsed just from having rotten sources), the first real wave ends
at `1`, and so on.

## Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    public int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        int fresh = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) {
            return 0;
        }

        int minutes = -1;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                int[] cell = queue.poll();
                int r = cell[0], c = cell[1];
                for (int k = 0; k < 4; k++) {
                    int nr = r + DR[k], nc = c + DC[k];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        fresh--;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }
            minutes++;
        }

        return fresh > 0 ? -1 : minutes;
    }
}
```

`ArrayDeque<int[]>` is the recommended queue (see `03-java-crash-course.md` section 4 -- faster
than the legacy `LinkedList`). Each queue entry is a two-element `int[]` holding `(row, col)`.
Snapshotting `levelSize = queue.size()` before the inner loop is what makes "one wave = one minute"
work -- without it you would drain the whole queue as if it were one level and lose the time
dimension. Mutating the grid (`1 -> 2`) replaces a visited array AND decrements `fresh`, so a single
write does triple duty. The final `fresh > 0 ? -1 : minutes` collapses two return paths into one.

## Complexity

    Time:  O(m * n)  -- each cell enters the queue at most once
    Space: O(m * n)  -- queue can hold every cell in the worst (all-rotten) case

## Dry-Run

Input:

```
2 1 1
1 1 0
0 1 1
```

Initial scan: seed `(0,0)` into the queue, `fresh = 6`.

| Minute | Queue before drain    | Cells rotted this wave        | fresh after |
|-------:|-----------------------|-------------------------------|------------:|
| seed   | [(0,0)]               | (none -- this is the seed)    | 6           |
| 0->1   | [(0,1),(1,0)]         | (0,1) and (1,0) rot           | 4           |
| 1->2   | [(0,2),(1,1)]         | (0,2) and (1,1) rot           | 2           |
| 2->3   | [(2,1)]               | (2,1) rots                    | 1           |
| 3->4   | [(2,2)]               | (2,2) rots                    | 0           |

Trace: `minutes` starts at `-1`. After draining seed level, `minutes = 0`. After wave 1, `minutes =
1`. ... After wave 4, `minutes = 4`. Queue empties; `fresh == 0`. Return `4`.

For the unreachable case `[[2,1,1],[0,1,1],[1,0,1]]`: BFS rots `(0,1),(1,0? no),(0,2),(1,1),(2,1?)`
-- but `(2,0)` is surrounded by `0` cells and the wall, so it never gets a rotten neighbor. BFS
ends with `fresh == 1` (the `(2,0)` orange), so we return `-1`.

### Checkpoint B -- Rot a new grid

**Q1 (apply).** Trace `grid = [[2,1]]` -- one rotten orange, one fresh orange beside it. What is returned?
- a) `0`
- b) `1` -- one wave rots the fresh cell at (0,1) after a single minute
- c) `-1`

<details><summary>Show answer</summary>

**(b)** -- seed (0,0); `fresh = 1`. Wave 1 rots (0,1), `fresh` becomes 0, `minutes` becomes 1. Queue empties, return `1`.

</details>

**Q2 (analyze).** What breaks if you seed only ONE rotten orange and ignore the rest?
- a) Nothing -- the BFS catches up
- b) Times come out too large, because each ignored source would have started its own clock later; all sources must be seeded up front (multi-source BFS)
- c) It always returns `-1`

<details><summary>Show answer</summary>

**(b)** -- multi-source BFS only works when every source shares one queue and one clock; seeding one at a time makes each source start from minute 0 independently and overcounts.

</details>

**Q3 (transfer).** Suppose the answer must instead be the LIST of coordinates of oranges that can NEVER rot. What changes?

<details><summary>Show answer</summary>

After the BFS empties, scan the grid and collect every cell still equal to `1` (the unreachable fresh oranges), instead of returning `-1`.

</details>

## Common mistakes

- Using DFS instead of BFS. DFS finds *a* path to each orange but not the *minimum time*, because
  rotting from different sources reaches different oranges at different times.
- Counting the seed level as minute 1. Initialise `minutes = -1` (or count levels *after* the seed
  drain) to avoid an off-by-one.
- Seeding only one rotten orange and looping BFS for each -- this gives wrong (too large) times
  because each source would start its clock independently. Seed *all* sources up front.
- Forgetting to check `grid[nr][nc] == 1` before rotting, which lets you re-enqueue already-rotten
  or empty cells and inflate the wave count.
- Returning `0` only when the grid is all-zero. The correct early-return condition is `fresh == 0`
  -- any grid with no fresh oranges (including all-rotten or all-empty) takes 0 minutes.

## Related problems

- [0417 - Pacific Atlantic Water Flow](../0417-pacific-atlantic-water-flow/) - same multi-source
  idea but DFS for reachability (no time dimension).
- [0200 - Number of Islands](../0200-number-of-islands/) - the grid DFS counterpart; good
  contrast for "when to pick BFS over DFS".
- [0286 - Walls and Gates](https://leetcode.com/problems/walls-and-gates/) - multi-source BFS that
  records distance into each cell.
