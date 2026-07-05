# 0733 - Flood Fill

**Difficulty:** Easy
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/flood-fill/

## Problem

Given an `m x n` integer `image` of pixel colors, plus a starting pixel `(sr, sc)` and a
`newColor`, perform a **flood fill**: change the starting pixel *and* every pixel reachable from it
by 4-directional moves through pixels of the *same* original color, to `newColor`. Return the
modified image.

Signature:

    int[][] floodFill(int[][] image, int sr, int sc, int newColor)

Examples:

    Input:  image = [[1,1,1],[1,1,0],[1,0,1]], sr = 1, sc = 1, newColor = 2
    Output: [[2,2,2],[2,2,0],[2,0,1]]
    (the connected region of 1s touching (1,1) becomes 2; the lone 1 at (2,2) is untouched)

    Input:  image = [[0,0,0],[0,0,0]], sr = 0, sc = 0, newColor = 2
    Output: [[2,2,2],[2,2,2]]

## Intuition

The phrase "every pixel reachable through same-colored neighbors" is a connectivity question, and
connectivity on a grid is DFS by definition. The trigger signals -- "grid", "connected",
"4-directional" -- all point here.

The leap of faith: assume `floodFill` already correctly repaints any *smaller* same-color region.
Then for the current pixel, we (1) repaint it, (2) hand each of its four neighbors the same job. Two
edge cases matter: if the start pixel is already `newColor`, do nothing and return (otherwise the
"same color as original" check would loop forever, since after repainting the original color equals
`newColor`), and never step outside the grid.

## Pseudocode

    function floodFill(image, startRow, startCol, newColor):
        originalColor = image[startRow][startCol]
        if originalColor == newColor:
            return image
        dfs(startRow, startCol)
        return image

    function dfs(row, col):
        if row or col out of bounds:
            return
        if image[row][col] != originalColor:
            return
        image[row][col] = newColor              # mark visited by repainting
        dfs(row - 1, col)
        dfs(row + 1, col)
        dfs(row, col - 1)
        dfs(row, col + 1)

The early `originalColor == newColor` check doubles as the visited-set: once a cell becomes
`newColor`, it no longer equals `originalColor`, so the second guard turns it away. No separate
`visited` array is needed.

## Java Solution

```java
class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private int[][] image;
    private int originalColor;
    private int newColor;

    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        int orig = image[sr][sc];
        if (orig == newColor) {
            return image;
        }
        this.image = image;
        this.originalColor = orig;
        this.newColor = newColor;
        dfs(sr, sc);
        return image;
    }

    private void dfs(int r, int c) {
        if (r < 0 || r >= image.length || c < 0 || c >= image[0].length) {
            return;
        }
        if (image[r][c] != originalColor) {
            return;
        }
        image[r][c] = newColor;
        for (int k = 0; k < 4; k++) {
            dfs(r + DR[k], c + DC[k]);
        }
    }
}
```

The four direction offsets `DR/DC` replace four repetitive recursive calls with a small loop -- the
exact idiom used in every grid problem in this pattern. The `originalColor == newColor` guard at the
top is the most-missed line: without it, a pixel whose original color already equals the target
causes infinite recursion, because the "did I repaint this already?" check (`!= originalColor`) can
never become true. Stashing `image`, `originalColor`, and `newColor` in fields keeps the recursive
helper's signature clean (just `(r, c)`), which is the standard tree/grid DFS convention from
`03-java-crash-course.md` section 7.

## Complexity

    Time:  O(m * n)  -- in the worst case every pixel is part of one region and visited once
    Space: O(m * n)  -- recursion depth equals region size in the worst (single-region) case

## Dry-Run

Input `image = [[1,1,1],[1,1,0],[1,0,1]]`, `sr=1, sc=1`, `newColor=2` (so `originalColor = 1`):

```
start:        after dfs(1,1):
1 1 1         2 2 2
1 1 0   -->   2 2 0
1 0 1         2 0 1
```

Call sequence (the `(r,c)` value at each step, with the cell repainted the instant it is entered):

| Step | Call         | Cell before | Action                              |
|-----:|--------------|-------------|-------------------------------------|
| 1    | dfs(1,1)     | 1           | repaint to 2, try 4 neighbors       |
| 2    | dfs(0,1)     | 1           | repaint to 2                        |
| 3    | dfs(0,0)     | 1           | repaint to 2                        |
| 4    | dfs(0,2)     | 1           | repaint to 2                        |
| 5    | dfs(2,1)     | 1           | repaint to 2                        |
| 6    | dfs(-1,1)    | --          | out of bounds, return               |
| 7    | dfs(1,0)     | 2           | already newColor, return            |
| 8    | dfs(1,2)     | 0           | not originalColor, return           |
| 9    | dfs(2,2)     | 1           | never reached -- (2,1) never        |
|      |              |             | recursed there (its neighbor was 0) |

The lone `1` at `(2,2)` is not 4-connected to the start region, so it stays `1`. Final image is
`[[2,2,2],[2,2,0],[2,0,1]]`.

## Common mistakes

- Skipping the `originalColor == newColor` early return -- on an all-one-color grid you recurse
  forever because repainted cells still match the "is this an original-colored neighbor?" test.
- Checking bounds *after* indexing the grid (`image[r][c]` then `if out of bounds`) -- you crash
  with `ArrayIndexOutOfBoundsException` before the guard can fire.
- Allocating a `boolean[][] visited` unnecessarily. The repaint itself is the visited-set here.
- Forgetting that flood fill is 4-directional, not 8-directional. The problem says "4-directional";
  diagonals are NOT neighbors.

## Related problems

- [0200 - Number of Islands](../0200-number-of-islands/) - same DFS, but you count regions instead
  of repainting one.
- [0695 - Max Area of Island](../0695-max-area-of-island/) - same DFS, but you return the region's
  size and track the max.
- [0994 - Rotting Oranges](../0994-rotting-oranges/) - the BFS version of "spread a property to all
  reachable cells".
