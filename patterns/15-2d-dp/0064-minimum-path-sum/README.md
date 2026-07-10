# 0064 - Minimum Path Sum

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/minimum-path-sum/

## Problem

Given an `m x n` grid of non-negative integers, find the path from the top-left
cell to the bottom-right cell that **minimises the sum** of the numbers along
the path. You may only move **down** or **right** at any point.

Signature:

    int minPathSum(int[][] grid)

Examples (verbatim from LeetCode):

    Input:  grid = [[1,3,1],[1,5,1],[4,2,1]]
    Output: 7
    Explanation: 1 -> 3 -> 1 -> 1 -> 1 sums to 7.

    Input:  grid = [[1,2,3],[4,5,6]]
    Output: 12

## Intuition

Same grid and same moves as Unique Paths (0062), but now each cell carries a
cost and we minimise the total instead of counting paths. The trigger -- "robot
moves only right/down in a grid" plus "minimum cost to reach" -- is grid-path DP
again. The state `dp[i][j]` is the cheapest total cost to reach cell `(i, j)`,
and the recurrence reads the same two neighbours: the cell above and the cell to
the left. The only difference from Unique Paths is the combine step -- `min`
instead of `+`, and each cell **adds its own** grid value on top.

### Checkpoint A -- Cost to reach a cell

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What does `dp[i][j]` represent?
- a) The value stored at `grid[i][j]`
- b) The minimum total cost to reach cell `(i, j)` from the top-left
- c) The number of paths to cell `(i, j)`

<details><summary>Show answer</summary>

**(b)** -- `dp[i][j]` is the cheapest running total along any right/down path to that cell, cell value included; (a) is the raw input and (c) is Unique Paths.

</details>

**Q2 (comprehend).** On the top row (`i = 0`), why is `dp[0][j]` computed as `dp[0][j-1] + grid[0][j]` rather than a `min` over two neighbours?
- a) Because top-row cells are reachable only from the left, so there is no "above" neighbour to choose
- b) Because the top row always has the smallest costs
- c) Because `grid[0][j]` is always zero

<details><summary>Show answer</summary>

**(a)** -- the top row has no cell above it, so every top-row cell inherits one forced running sum from its left neighbour; the `min` only appears once both neighbours exist.

</details>

## Pseudocode

    function minPathSum(grid):
        let m = number of rows, n = number of columns
        create a dp table of size m by n

        dp[0][0] = grid[0][0]
        for j from 1 to n-1:                        # first row: only reachable from the left
            dp[0][j] = dp[0][j-1] + grid[0][j]
        for i from 1 to m-1:                        # first column: only reachable from above
            dp[i][0] = dp[i-1][0] + grid[i][0]

        for i from 1 to m-1:
            for j from 1 to n-1:
                dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1])

        return dp[m-1][n-1]

## Java Solution

```java
class Solution {
    public int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];

        dp[0][0] = grid[0][0];
        for (int j = 1; j < n; j++) {
            dp[0][j] = dp[0][j - 1] + grid[0][j];
        }
        for (int i = 1; i < m; i++) {
            dp[i][0] = dp[i - 1][0] + grid[i][0];
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = grid[i][j] + Math.min(dp[i - 1][j], dp[i][j - 1]);
            }
        }

        return dp[m - 1][n - 1];
    }
}
```

The base case is richer than Unique Paths: the top-left cell pays its own cost,
and then the first row is a running sum left-to-right (each cell reachable only
from the left) and the first column a running sum top-to-bottom. The recurrence
adds `grid[i][j]` to the cheaper of the two ways in -- `Math.min` over the cell
above and the cell to the left. We allocate a fresh `dp[][]` so the original
`grid` is left untouched, which keeps the dry-run table readable. The in-place
variant (write back into `grid[i][j]`) is one less array but destroys the input.

## Complexity

    Time:  O(m * n)  -- one constant-time min + add per cell.
    Space: O(m * n)  -- the dp table. Reducible to O(n) with a rolling row, or
                        O(1) extra if you reuse grid[][] in place.

## Dry-Run

On `grid = [[1,3,1],[1,5,1],[4,2,1]]` (expected `7`).

Step 1 -- base: `dp[0][0] = 1`, then fill the first row and first column as
running sums:

```
dp[0][*]:  1 , 1+3=4 , 4+1=5
dp[*][0]:  1 , 1+1=2 , 2+4=6
```

After the base loops:

```
        j=0   j=1   j=2
i=0   [  1  ,  4  ,  5 ]
i=1   [  2  ,  ?  ,  ? ]
i=2   [  6  ,  ?  ,  ? ]
```

Step 2 -- fill the interior:

| (i, j) | grid[i][j] | min(above, left) | dp[i][j] |
|:------:|:----------:|:-----------------:|:--------:|
| (1, 1) | 5          | min(4, 2) = 2     | 7        |
| (1, 2) | 1          | min(5, 7) = 5     | 6        |
| (2, 1) | 2          | min(7, 6) = 6     | 8        |
| (2, 2) | 1          | min(6, 8) = 6     | 7        |

Final table:

```
[  1  ,  4  ,  5 ]
[  2  ,  7  ,  6 ]
[  6  ,  8  ,  7 ]
```

Bottom-right `dp[2][2] = 7`, the answer. Reconstructing the path: from `(2,2)`
walk back to whichever neighbour was cheaper -- `(1,2)` (6) beats `(2,1)` (8),
then `(0,2)` (5) beats `(1,1)` (7), then left along row 0 to the start:
`(0,0) -> (0,1) -> (0,2) -> (1,2) -> (2,2)`, cells `1,3,1,1,1` summing to `7`.

### Checkpoint B -- Trace a fresh grid

**Q1 (apply).** Trace the fresh grid `[[2,1],[1,3]]`. What is `dp[1][1]` (the answer)?
- a) 4
- b) 5
- c) 6

<details><summary>Show answer</summary>

**(c)** -- `dp[0][0]=2`; `dp[0][1]=2+1=3`; `dp[1][0]=2+1=3`; then `dp[1][1] = grid[1][1] + min(dp[0][1], dp[1][0]) = 3 + min(3,3) = 6`.

</details>

**Q2 (analyze).** What goes wrong if you set the base row to `dp[0][j] = grid[0][j]` (single cell value) instead of the running sum `dp[0][j-1] + grid[0][j]`?
- a) Nothing; the answer is unchanged
- b) The top row ignores the cost of every cell to its left, so paths that run along the top row are undercounted
- c) The code throws an exception

<details><summary>Show answer</summary>

**(b)** -- a top-row cell is reached by passing through all cells to its left; dropping the running sum pretends the journey is free, so any best path using the top row comes back too cheap.

</details>

**Q3 (transfer).** Suppose moves were allowed down, right, AND up. Would this same row-by-row DP still work? Why or why not?

<details><summary>Show answer</summary>

No -- an "up" move lets the path revisit cells, creating cycles, so a single top-to-bottom fill can no longer capture every path. You would need a shortest-path method (for example Dijkstra on the grid) instead of a one-pass DP.

</details>

## Common mistakes

- Setting the base row/column to `grid[0][j]` instead of the **running sum**
  `dp[0][j-1] + grid[0][j]`. Each cell on the edge has exactly one way in, and
  that way passes through every preceding edge cell.
- Using `grid[i][j] + dp[i-1][j] + dp[i][j-1]` (sum of both) instead of `+ min`
  of both. Adding both neighbours double-counts and never gives the minimum.
- Indexing `grid[j][i]` -- the rows/columns are swapped and produce a wrong,
  though often close, answer. Keep `i` = row (first index) consistently.
- Mutating `grid` in place by mistake and then reading it again. Fine for the
  LeetCode judge, fatal if your caller reuses the array.

## Related problems

- [0062 - Unique Paths](../0062-unique-paths/) -- the same grid and moves, but
  counting paths instead of summing costs.
- [0072 - Edit Distance](../0072-edit-distance/) -- another "min over two
  neighbours" recurrence, applied to two strings instead of a grid.
