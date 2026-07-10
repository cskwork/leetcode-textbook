# 0417 - Pacific Atlantic Water Flow

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/pacific-atlantic-water-flow/

## Concepts used

- **Graph** -- a set of nodes connected by edges. The height grid is an implicit graph: each cell is
  a node, its up/down/left/right cells are its neighbors (the edges).
  [glossary](../../../docs/10-glossary.md#graph)
- **Connected component** -- a group of nodes reachable from one another; here we compute a
  reachability set from the ocean's edge using the same idea.
  [glossary](../../../docs/10-glossary.md#connected-component)
- **DFS (depth-first search)** -- a traversal that goes as deep as possible before backing up.
  [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)

## Problem

Given an `m x n` rectangular matrix `heights` where `heights[r][c]` is the elevation of the cell at
`(r, c)`, return a list of `[r, c]` coordinates from which **rain water can flow to both the Pacific
and the Atlantic ocean**. The Pacific touches the **top and left** edges; the Atlantic touches the
**bottom and right** edges. Water flows from a cell to an orthogonal neighbor only if that neighbor's
height is **less than or equal to** the current cell's height (water runs downhill or level).

Signature:

    List<List<Integer>> pacificAtlantic(int[][] heights)

Example:

    Input:  heights = [
              [1,2,2,3,5],
              [3,2,3,4,4],
              [2,4,5,3,1],
              [6,7,1,4,5],
              [5,1,1,2,4]
            ]
    Output: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]

## Intuition

Picture a ridgeline of hills in the rain. A drop falling on a cell runs to whichever neighbor is at
the same height or lower. We want every hilltop from which a drop can reach *both* oceans -- the
Pacific along the top/left edges, the Atlantic along the bottom/right.

Treat the grid as an implicit [graph](../../../docs/10-glossary.md#graph): each cell is a **node**,
its up/down/left/right cells are its **neighbors** (nodes joined to it by an edge). The naive plan --
for every cell, simulate the drop and check whether it reaches both oceans -- is far too slow (we
would re-trace the same paths thousands of times). Flip the question: instead of "which cells can
reach the ocean?", ask "which cells can the ocean reach?". Start from the shore and walk *inward,
uphill or level* (a neighbor counts as reachable only if its height is `>=` the current cell's). A
cell the ocean can reach this way is exactly a cell whose water can flow down to that ocean -- the
same relation, walked backwards. This is [DFS](../../../docs/10-glossary.md#dfs-depth-first-search)
seeded from the border, and the set of cells it touches is a reachability set -- the same idea as a
[connected component](../../../docs/10-glossary.md#connected-component), just with an uphill rule for
who counts as a neighbor.

Run that DFS twice -- once from the Pacific's edges (top + left), once from the Atlantic's (bottom +
right) -- into two separate "reachable" grids (each grid doubles as its own **visited** record).
A cell marked reachable in *both* grids can flow to both oceans; that is our answer.

Smallest trace, a 3x3 of heights:

```
1 2 3
8 9 4
7 6 5
```

From the Pacific (top row + left column) we may climb up or stay level, so we reach essentially every
cell -- the centre 9 is the peak, climbable from several borders, and everything lower sits below it.
Pacific grid: all true. From the Atlantic (bottom row + right column) the same climb reaches every
cell *except* the top-left valley (0,0)=1 and (0,1)=2 -- their only neighbors are lower or are cells
the Atlantic never reached. Atlantic grid misses those two. The cells true in *both* are everyone
except (0,0) and (0,1). That matches the expected answer.

### Checkpoint A -- Walk from the shore

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** The DFS walks INWARD from the ocean. Which neighbor comparison is correct?
- a) `heights[nr][nc] <= heights[r][c]` (move downhill)
- b) `heights[nr][nc] >= heights[r][c]` (move uphill or level)
- c) `heights[nr][nc] == heights[r][c]` (only equal heights)

<details><summary>Show answer</summary>

**(b)** -- walking inland uphill-or-level is exactly the reverse of water flowing downhill to the shore, so a cell the ocean can climb to is a cell whose water reaches that ocean.

</details>

**Q2 (comprehend).** Why run the DFS from the ocean inward, instead of from each cell outward?
- a) The problem requires it
- b) One DFS from the border reaches every cell that can reach that ocean; doing it per cell would re-trace the same paths thousands of times
- c) To avoid recursion

<details><summary>Show answer</summary>

**(b)** -- reversing the search turns `O((m*n)^2)` per-cell simulations into two `O(m*n)` border-seeded passes.

</details>

## Pseudocode

    function pacificAtlantic(heights):
        rows, cols = dimensions

        pacReachable  = empty boolean grid
        atlReachable  = empty boolean grid

        # Seed Pacific sources: top row + left column.
        for each cell on top row:    dfs(heights, cell, pacReachable)
        for each cell on left column: dfs(heights, cell, pacReachable)

        # Seed Atlantic sources: bottom row + right column.
        for each cell on bottom row: dfs(heights, cell, atlReachable)
        for each cell on right column: dfs(heights, cell, atlReachable)

        result = []
        for each cell (r, c):
            if pacReachable[r][c] and atlReachable[r][c]:
                add [r, c] to result
        return result

    function dfs(heights, (r, c), reachable):
        if (r, c) out of bounds or reachable[r][c] is already true:
            return
        mark reachable[r][c] = true              # visited set = the reachable grid itself
        for each neighbor (nr, nc):
            if (nr, nc) in bounds and heights[nr][nc] >= heights[r][c]:
                dfs(heights, (nr, nc), reachable)

The critical flip is `heights[nr][nc] >= heights[r][c]`: walking from the shore inward, we may only
climb up or stay level, which mirrors exactly "water can flow downhill from the cell to the shore".

## Java Solution

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private int[][] heights;
    private int rows, cols;

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        this.heights = heights;
        this.rows = heights.length;
        this.cols = heights[0].length;

        boolean[][] pac = new boolean[rows][cols];
        boolean[][] atl = new boolean[rows][cols];

        for (int r = 0; r < rows; r++) {
            dfs(r, 0, pac);            // Pacific = left edge
            dfs(r, cols - 1, atl);     // Atlantic = right edge
        }
        for (int c = 0; c < cols; c++) {
            dfs(0, c, pac);            // Pacific = top edge
            dfs(rows - 1, c, atl);     // Atlantic = bottom edge
        }

        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (pac[r][c] && atl[r][c]) {
                    result.add(List.of(r, c));
                }
            }
        }
        return result;
    }

    private void dfs(int r, int c, boolean[][] reachable) {
        if (reachable[r][c]) {
            return;
        }
        reachable[r][c] = true;
        for (int k = 0; k < 4; k++) {
            int nr = r + DR[k];
            int nc = c + DC[k];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && heights[nr][nc] >= heights[r][c]) {
                dfs(nr, nc, reachable);
            }
        }
    }
}
```

The `reachable[r][c]` check at the top of `dfs` doubles as the visited-set, so we never need to
pass a separate visited grid. Note we check `reachable[r][c]` *before* the bounds check on the
recursive entry -- but the caller already validated bounds, and we never call `dfs` on an out-of-
bounds cell (the `if` guard in the loop is checked first). The border-seeding loops deliberately
call `dfs` on corner cells twice (once for each ocean); the second call is a no-op because
`reachable[r][c]` is already true. `List.of(r, c)` makes an immutable two-element list which matches
LeetCode's expected `List<Integer>` element type.

## Complexity

    Time:  O(m * n)  -- two DFS passes, each cell visited at most once per ocean
    Space: O(m * n)  -- two boolean grids plus recursion depth up to m*n

## Dry-Run

Heights (3x3, small enough to trace by hand):

```
1 2 3
8 9 4
7 6 5
```

Pacific sources: top row `(0,0)(0,1)(0,2)` and left column `(1,0)(2,0)`. Atlantic sources: bottom
row `(2,0)(2,1)(2,2)` and right column `(0,2)(1,2)`.

**Pacific pass.** From each seed we climb to neighbors with height `>=` current. The center `9` is
the peak, reachable from `(0,1)` (height 2, climbs to 9), `(1,0)` (height 8, climbs to 9),
`(1,2)` (height 4, climbs to 9). The valley cells `(2,1)=6` and `(2,2)=5` are reached via
`(1,2)=4 -> (2,2)=5 -> (2,1)=6`. So Pacific reaches **every** cell:

```
T T T          pac:
T T T
T T T
```

**Atlantic pass.** Seeds are the bottom row and right column. From `(2,1)=6` we climb to `(1,1)=9`,
then from `(1,0)=8` (reached via `(2,0)=7`) nothing more climbs (its only higher neighbor `(1,1)`
is already done). The top-left valley `(0,0)=1` and `(0,1)=2` have all neighbors either lower or
already-lower Atlantic cells, so they are never reached:

```
F F T          atl:
T T T
T T T
```

**Intersection** (both grids true) -- every cell except `(0,0)` and `(0,1)`:

Output: `[[0,2],[1,0],[1,1],[1,2],[2,0],[2,1],[2,2]]`.

### Checkpoint B -- Two oceans, one answer

**Q1 (apply).** For `heights = [[9,8,9],[8,1,8],[9,8,9]]`, is the center cell `(1,1)` (height 1) reachable from the Pacific DFS?
- a) Yes -- every cell is reachable
- b) No -- the inland DFS may only move up or level, but the center (1) is strictly lower than all its neighbors (8), so no ocean can step into it
- c) Only the Pacific reaches it, not the Atlantic

<details><summary>Show answer</summary>

**(b)** -- to enter the center a neighbor of height 8 would have to step DOWN to 1, but the rule allows only `>=`. So neither ocean can reach it.

</details>

**Q2 (analyze).** What goes wrong if you write `heights[nr][nc] <= heights[r][c]` (downhill) instead of `>=`?
- a) Nothing -- it is symmetric
- b) You simulate water flowing the wrong way (inland-uphill), so the reachable sets no longer mean "can flow to this ocean"
- c) It throws on equal heights

<details><summary>Show answer</summary>

**(b)** -- the flipped comparison walks downhill from the ocean, which is the opposite of the relation we need; the resulting sets are meaningless for the problem.

</details>

**Q3 (transfer).** Why does running ONE merged DFS from all four edges and returning every reached cell give the WRONG answer?

<details><summary>Show answer</summary>

A single merged set forgets WHICH ocean reached each cell. You need two independent reachable grids and return only the cells true in BOTH -- the intersection, not the union.

</details>

## Common mistakes

- Forgetting to flip the direction: writing `heights[nr][nc] <= heights[r][c]` would have water flow
  uphill in the real problem. Always walk *inward/uphill* from the ocean: `>=`.
- Running one DFS per cell (the naive `O((m*n)^2)` approach). It works on tiny inputs but TLEs.
- Seeding from only one edge per ocean. Pacific needs *both* top and left; Atlantic needs both
  bottom and right. Missing one strands entire regions.
- Reusing a single reachable grid for both oceans. You need two -- the intersection requires both
  sets to be tracked independently.
- Using BFS vs DFS interchangeably. Both are correct here; DFS is shorter and the depth is bounded
  by grid size, so recursion is fine for LeetCode constraints.

## Related problems

- [0130 - Surrounded Regions](../0130-surrounded-regions/) - same "DFS from the border inward"
  pattern, but with a binary reachable/unsafe decision per cell.
- [0994 - Rotting Oranges](../0994-rotting-oranges/) - same "seed from many sources at once" idea,
  but multi-source BFS to track distance, not just reachability.
- [0286 - Walls and Gates](https://leetcode.com/problems/walls-and-gates/) - multi-source BFS from
  gates inward, recording distance.
