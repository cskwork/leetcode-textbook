# 0200 - Number of Islands

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/number-of-islands/

## Problem

Given an `m x n` 2-D binary grid where `'1'` is land and `'0'` is water, count the number of
**islands**. An island is a group of `'1'`s connected 4-directionally (horizontal or vertical). You
may assume the grid is surrounded by water (its border is conceptually all `'0'`).

Signature:

    int numIslands(char[][] grid)

Examples:

    Input:  grid = [
              ["1","1","1","1","0"],
              ["1","1","0","1","0"],
              ["1","1","0","0","0"],
              ["0","0","0","0","0"]
            ]
    Output: 1

    Input:  grid = [
              ["1","1","0","0","0"],
              ["1","1","0","0","0"],
              ["0","0","1","0","0"],
              ["0","0","0","1","1"]
            ]
    Output: 3

## Intuition

Each island is exactly a connected component in the implicit grid graph. The trigger signals --
"grid", "islands", "connected" -- say grid DFS. The classic move: scan every cell; the moment you
find an unvisited `'1'`, you have discovered a brand-new island, so increment the count and then
*sink* the entire island (DFS-recursively turn every `'1'` in it into `'0'`) so it is never counted
again. Each cell is processed exactly once across the whole algorithm.

The "sink" trick is the in-place visited-set: by overwriting `'1'` with `'0'` we never need a
`boolean[][] visited` array. The trade-off (we destroy the input) is acceptable on LeetCode because
the judge never re-reads the grid after the answer is returned.

## Pseudocode

    function numIslands(grid):
        count = 0
        for each cell (r, c) in the grid:
            if grid[r][c] == '1':
                count += 1
                sink(r, c)              # erase this whole island so it is not counted again
        return count

    function sink(r, c):
        if r or c out of bounds:
            return
        if grid[r][c] != '1':
            return
        grid[r][c] = '0'                # mark visited by sinking
        sink(r - 1, c)
        sink(r + 1, c)
        sink(r, c - 1)
        sink(r, c + 1)

`sink` is exactly Flood Fill's DFS with `'0'` as the target color. The outer loop is what turns a
single fill into "count connected components".

## Java Solution

```java
class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private char[][] grid;
    private int rows, cols;

    public int numIslands(char[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    sink(r, c);
                }
            }
        }
        return count;
    }

    private void sink(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != '1') {
            return;
        }
        grid[r][c] = '0';
        for (int k = 0; k < 4; k++) {
            sink(r + DR[k], c + DC[k]);
        }
    }
}
```

The guard line `grid[r][c] != '1'` folds the bounds check, the water check, and the visited check
into a single condition -- the cell is sinkable only if it is in-bounds land that has not been
sunk yet. We mutate in place (`'1' -> '0'`) so no `visited` array is allocated, halving memory.
Note `char[][]` not `int[][]`: LeetCode's signature uses characters, so `'1'` and `'0'` are `char`
literals, not `String`s.

## Complexity

    Time:  O(m * n)  -- each cell is sunk at most once; the outer scan visits each cell once
    Space: O(m * n)  -- worst-case recursion depth if the whole grid is one island (a snaking path)

## Dry-Run

Input:

```
1 1 0
0 1 0
0 0 1
```

Outer scan finds the first `'1'` at `(0,0)`:

| Step | Outer cell | Action                                                | count |
|-----:|------------|-------------------------------------------------------|------:|
| 1    | (0,0)='1'  | count=1, call sink(0,0)                               | 1     |
| 2    | sink(0,0)  | sink it, recurse to (0,1) and (1,0)                   | 1     |
| 3    | sink(0,1)  | sink it, recurse to (1,1)                             | 1     |
| 4    | sink(1,1)  | sink it; (1,0) is '0' now so returns                 | 1     |
| 5    | (0,2)='0'  | skip                                                  | 1     |
| 6    | (1,0..2)   | all '0' now, skip                                     | 1     |
| 7    | (2,2)='1'  | count=2, call sink(2,2)                               | 2     |
| 8    | sink(2,2)  | sink it, neighbors are water/out-of-bounds           | 2     |

Grid after:

```
0 0 0
0 0 0
0 0 0
```

Output: `2`.

## Common mistakes

- Using DFS instead of marking visited before recursing, causing the same cell to be re-visited
  from multiple neighbors and blowing up to exponential time. Sinking the cell *first* prevents it.
- Comparing `grid[r][c] == 1` (int) instead of `== '1'` (char) -- LeetCode's signature is
  `char[][]`, so the literal must use single quotes.
- Forgetting the bounds check before indexing. The fused guard here handles it, but splitting the
  check out and indexing first will throw `ArrayIndexOutOfBoundsException`.
- Counting every `'1'` instead of every island. The count must increment only on the *outer* scan's
  discovery, not inside `sink`.
- Allocating a `boolean[][] visited` when sinking in place would do. Works, but doubles memory and
  the bookkeeping is unnecessary noise for an interviewer.

## Related problems

- [0733 - Flood Fill](../0733-flood-fill/) - the single-region DFS that `sink` is borrowed from.
- [0695 - Max Area of Island](../0695-max-area-of-island/) - same scan-and-sink loop, but track area.
- [0130 - Surrounded Regions](../0130-surrounded-regions/) - same grid DFS, but you must NOT sink
  border-connected cells.
