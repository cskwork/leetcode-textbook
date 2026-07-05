# 0417 - Pacific Atlantic Water Flow

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/pacific-atlantic-water-flow/

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

The naive approach -- "for each cell, can it reach both oceans?" -- is an `O((m*n)^2)` reachability
check per cell, far too slow. Flip the question the same way as Surrounded Regions: instead of
asking *which cells can reach the ocean*, ask *which cells the ocean can reach* by running DFS
**inward from the shore, against the flow direction**. A cell can flow to the Pacific iff the
Pacific can reach it walking *uphill or level* (`neighbor.height >= current.height`); same for the
Atlantic. Run two such DFS passes (one from each ocean's border), mark every reachable cell, and
intersect the two reachable sets -- those cells flow to both oceans.

Trigger signals: "grid", "reachable from ocean", "flow". The keyword "flow" plus "from edges
inward" is exactly the border-source DFS pattern.

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
