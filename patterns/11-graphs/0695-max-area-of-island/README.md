# 0695 - Max Area of Island

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/max-area-of-island/

## Problem

Given a non-empty `m x n` binary `grid` (`0` water, `1` land, this time as `int` not `char`), find
the **maximum area** of an island in the grid. The area is the number of cells in the island. If
there is no island, return `0`. Connectivity is 4-directional, same as Number of Islands.

Signature:

    int maxAreaOfIsland(int[][] grid)

Examples:

    Input:  grid = [
              [0,0,1,0,0,0,0,1,0,0,0,0,0],
              [0,0,0,0,0,0,0,1,1,1,0,0,0],
              [0,1,1,0,1,0,0,0,0,0,0,0,0],
              [0,1,0,0,1,1,0,0,1,0,1,0,0],
              [0,1,0,0,1,1,0,0,1,1,1,0,0],
              [0,0,0,0,0,0,0,0,0,0,1,0,0],
              [0,0,0,0,0,0,0,1,1,1,0,0,0],
              [0,0,0,0,0,0,0,1,1,0,0,0,0]
            ]
    Output: 6   (the L-shaped island in the lower right)

    Input:  grid = [[0,0,0,0,0,0,0,0]]
    Output: 0

## Intuition

Identical setup to Number of Islands -- scan, find a `'1'`, discover its whole component -- but
instead of incrementing a counter we want the component's *size*. The natural shape is a DFS that
**returns the area** of the region it explores: `1` (for the current cell) plus the area of each
not-yet-visited land neighbor. The leap of faith: trust that `dfs(neighbor)` returns the correct
area of that neighbor's region; just sum them up. Sinking cells to `'0'` as we go is still the
in-place visited-set.

## Pseudocode

    function maxAreaOfIsland(grid):
        best = 0
        for each cell (r, c):
            if grid[r][c] == 1:
                best = max(best, area(r, c))
        return best

    function area(r, c):
        if r or c out of bounds or grid[r][c] != 1:
            return 0
        grid[r][c] = 0                                # mark visited by sinking
        return 1
             + area(r - 1, c)
             + area(r + 1, c)
             + area(r, c - 1)
             + area(r, c + 1)

The `1` in the return accounts for the current cell; the four recursive calls each contribute the
area of their sub-region. Sinking the cell *before* recursing is what keeps each cell counted once.

## Java Solution

```java
class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private int[][] grid;
    private int rows, cols;

    public int maxAreaOfIsland(int[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        int best = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    best = Math.max(best, area(r, c));
                }
            }
        }
        return best;
    }

    private int area(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != 1) {
            return 0;
        }
        grid[r][c] = 0;
        int size = 1;
        for (int k = 0; k < 4; k++) {
            size += area(r + DR[k], c + DC[k]);
        }
        return size;
    }
}
```

The only structural change from Number of Islands is that `area` returns an `int` instead of `void`
-- it propagates the size up the recursion. We accumulate into a local `size` variable rather than
writing `1 + area(...) + area(...)` so that adding a fifth direction (say, diagonals) is a one-line
change. Everything else -- fused bounds/visited guard, in-place sink, direction arrays -- is the
same grid-DFS idiom.

## Complexity

    Time:  O(m * n)  -- each cell is sunk (and therefore visited) at most once
    Space: O(m * n)  -- worst-case recursion depth for one giant snaking island

## Dry-Run

Input:

```
0 1 1 0
1 0 0 0
1 1 1 0
```

Outer scan finds first `1` at `(0,1)`. Trace `area(0,1)`:

| Call         | Cell before | After sink | Returns from neighbors              | Subtotal |
|--------------|-------------|------------|--------------------------------------|---------:|
| area(0,1)    | 1           | 0          | up OOB, down area(1,1)=0, left=0, right area(0,2) | 1 + 0 + 0 + 0 + ? |
| area(0,2)    | 1           | 0          | all neighbors 0/OOB                  | 1        |

So `area(0,1) = 1 + 0 + 0 + 0 + 1 = 2`. `best = max(0, 2) = 2`.

Continue scan. `(1,0)` is `1`. Trace `area(1,0)`:

| Call         | Cell before | After sink | Neighbors explored                   | Subtotal |
|--------------|-------------|------------|--------------------------------------|---------:|
| area(1,0)    | 1           | 0          | up area(0,0)=0, down area(2,0), ...  | ?        |
| area(2,0)    | 1           | 0          | right area(2,1)                      | ?        |
| area(2,1)    | 1           | 0          | right area(2,2)                      | ?        |
| area(2,2)    | 1           | 0          | neighbors all 0/OOB                  | 1        |

Walking back up: `area(2,1) = 1 + 1 = 2`, `area(2,0) = 1 + 2 = 3`, `area(1,0) = 1 + 3 = 4`.

`best = max(2, 4) = 4`.

The remaining cells are all `0` now. Output: `4`.

## Common mistakes

- Returning `1` instead of `1 + area(neighbor)` -- you sink the island correctly but report area 1.
- Sinking the cell *after* the recursive calls -- then the same cell gets re-entered from each
  neighbor and the area explodes (or, with the bounds check, you double-count).
- Comparing to `'1'` (char) -- this grid is `int[][]`, so the literal is `1`, no quotes.
- Forgetting to skip when `best` should default to `0` for an all-water grid. The `best = 0` init
  handles it; initialising to `Integer.MIN_VALUE` would be wrong.
- Using BFS and counting dequeues. Correct but more code; DFS recursion is shorter and equally
  `O(m*n)`.

## Related problems

- [0200 - Number of Islands](../0200-number-of-islands/) - identical DFS, returns a count of
  components instead of the max area.
- [0733 - Flood Fill](../0733-flood-fill/) - the single-region DFS that `area` generalises.
- [0463 - Island Perimeter](https://leetcode.com/problems/island-perimeter/) - same scan, but each
  land cell contributes `4 - (# land neighbors)`.
