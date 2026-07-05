# 0062 - Unique Paths

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/unique-paths/

## Problem

A robot sits at the top-left corner of an `m x n` grid and can only move
**down** or **right**. How many distinct paths take it to the bottom-right
corner?

Signature:

    int uniquePaths(int m, int n)

Examples (verbatim from LeetCode):

    Input:  m = 3, n = 7
    Output: 28

    Input:  m = 3, n = 2
    Output: 3

## Intuition

This is the cleanest possible grid DP -- the recurrence reads only the cell
above and the cell to the left, with no values, costs, or matches to worry
about. The trigger is "robot moves only right/down in a grid" + "number of
ways", which the overview table maps straight to grid-path DP.

The defining observation: every path into cell `(i, j)` must arrive either from
`(i-1, j)` directly above, or from `(i, j-1)` directly to the left. So the
number of paths to `(i, j)` is just the **sum** of the paths to those two
neighbours. The top row and left column have only one path each (all-right or
all-down), which gives the base case.

## Pseudocode

    function uniquePaths(m, n):
        create a dp table of size m by n

        for i from 0 to m-1:
            dp[i][0] = 1            # first column: only one way (all-down)
        for j from 0 to n-1:
            dp[0][j] = 1            # first row: only one way (all-right)

        for i from 1 to m-1:
            for j from 1 to n-1:
                dp[i][j] = dp[i-1][j] + dp[i][j-1]   # paths from above + from left

        return dp[m-1][n-1]

## Java Solution

```java
class Solution {
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];

        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int j = 0; j < n; j++) {
            dp[0][j] = 1;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }

        return dp[m - 1][n - 1];
    }
}
```

The table is sized `m x n` (not `(m+1) x (n+1)`) because every grid cell is a
real cell with a value -- there is no "empty prefix" to represent. The two
base loops set the first column and first row to `1` before the nested loop,
since those cells have only one way in (a straight line from the start). The
recurrence itself is the whole algorithm: each interior cell sums the two ways
to reach it. The answer lives at `dp[m-1][n-1]`, the bottom-right corner.

## Complexity

    Time:  O(m * n)  -- one constant-time computation per cell.
    Space: O(m * n)  -- the full dp table. Reducible to O(n) with a single
                        rolling row, since each row reads only the row above.

## Dry-Run

On `m = 3, n = 3` (expected `6`). After the two base loops the first row and
first column are all `1`:

```
        j=0   j=1   j=2
i=0   [  1  ,  1  ,  1 ]
i=1   [  1  ,  ?  ,  ? ]
i=2   [  1  ,  ?  ,  ? ]
```

Fill the interior left-to-right, top-to-bottom:

| (i, j) | above `dp[i-1][j]` | left `dp[i][j-1]` | `dp[i][j]` |
|:------:|:------------------:|:-----------------:|:----------:|
| (1, 1) | 1                  | 1                 | 2          |
| (1, 2) | 1                  | 2                 | 3          |
| (2, 1) | 2                  | 1                 | 3          |
| (2, 2) | 3                  | 3                 | 6          |

Final table:

```
[  1  ,  1  ,  1 ]
[  1  ,  2  ,  3 ]
[  1  ,  3  ,  6 ]
```

Bottom-right `dp[2][2] = 6`, the answer. You can sanity-check it by hand: the
six paths correspond to the `2 down / 2 right` sequences DDRR, DRDR, DRRD,
RDDR, RDRD, RRDD.

## Common mistakes

- Initialising the base row/column to `0` instead of `1`. A grid of zeros sums
  to zero everywhere, returning `0` for every input.
- Sizing the table `(m+1) x (n+1)` and then returning `dp[m][n]` without ever
  filling the real cells -- the recurrence would need shifting by one to stay
  correct. Pick a sizing and stick with it.
- Forgetting one of the two base loops (e.g. only the row, not the column).
  The first column stays `0` and every cell below row 0 undercounts.
- Using `m` and `n` interchangeably. By convention `m` is rows (first index),
  `n` is columns (second index); swapping them in the array allocation
  transposes the answer -- which here happens to be symmetric, but is wrong in
  general.

## Related problems

- [0064 - Minimum Path Sum](../0064-minimum-path-sum/) -- same grid and moves,
  but each cell now carries a cost and you minimise the total.
- [1143 - Longest Common Subsequence](../1143-longest-common-subsequence/) --
  same "above + left" shape, but the combine step is `max` rather than `+`.
