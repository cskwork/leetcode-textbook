# 0733 - Flood Fill

**Difficulty:** Easy
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/flood-fill/

## Concepts used

- **Graph** -- a set of nodes connected by edges. The image grid is an implicit graph: each pixel is
  a node, its up/down/left/right neighbors are its edges.
  [glossary](../../../docs/10-glossary.md#graph)
- **Connected component** -- the largest group of nodes reachable from one another; the pixels the
  paint-bucket fills form one connected component.
  [glossary](../../../docs/10-glossary.md#connected-component)
- **DFS (depth-first search)** -- a traversal that goes as deep as possible before backing up.
  [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)

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

This is the "paint bucket" tool from any drawing program. You click a pixel, and the fill spreads to
every pixel reachable from it through same-colored neighbors -- never crossing into a different
color -- and repaints them all. The problem is to implement exactly that.

Treat the image as an implicit [graph](../../../docs/10-glossary.md#graph): each pixel is a **node**
(a single item), and an invisible **edge** (a link) joins a pixel to each of its up/down/left/right
**neighbors** -- the cells directly beside it; diagonals do not count. The pixels reachable from the
start through same-colored neighbors form one
[connected component](../../../docs/10-glossary.md#connected-component), and flood fill repaints
exactly that component and nothing else.

Spread with [DFS](../../../docs/10-glossary.md#dfs-depth-first-search): from the start pixel, repaint
it, then hand each same-colored neighbor the same job. Repainting *is* the **visited** mark -- once a
pixel's color changes, it no longer equals the original color, so the "is this a same-colored
neighbor?" test naturally turns it away and we never revisit it. No separate `visited` array needed.

Smallest trace, `image = [[1,1,1],[1,1,0],[1,0,1]]`, click (1,1), newColor 2 (originalColor 1):

```
start:        result:
1 1 1         2 2 2
1 1 0   -->   2 2 0
1 0 1         2 0 1
```

From (1,1) the fill spreads to every `1` connected to it -- the whole top block and the left column
-- turning them to 2. The lone `1` at (2,2) is *not* 4-connected to the start (its only neighbor
toward the start is the `0` at (2,1)), so it stays 1. One edge case matters: if the start pixel is
already `newColor`, do nothing and return -- otherwise repainted pixels would still match "same color
as the original" and the fill would loop forever.

### Checkpoint A -- Paint bucket

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Besides changing a pixel's color, what else does repainting it to `newColor` achieve?
- a) It counts how many pixels were filled
- b) It is the visited mark -- once repainted, the pixel no longer equals `originalColor`, so it is never re-entered
- c) It acts as a bounds check

<details><summary>Show answer</summary>

**(b)** -- the `!= originalColor` guard naturally turns away repainted pixels, so no separate `visited` array is needed.

</details>

**Q2 (comprehend).** Why must we return early when `originalColor == newColor`?
- a) To save memory
- b) If they are equal, repainted pixels still match "same color as original", so the guard can never become true and the fill recurses forever
- c) It is only a style choice

<details><summary>Show answer</summary>

**(b)** -- with the two colors equal, the repaint cannot change whether a cell matches `originalColor`, so visited-detection breaks and the recursion never stops.

</details>

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

### Checkpoint B -- Fill a new image

**Q1 (apply).** Trace `image = [[1,1,0],[0,1,1]]`, `sr=0, sc=0`, `newColor=9`. Which cells become `9`?
- a) Only `(0,0)` and `(0,1)`
- b) `(0,0)`, `(0,1)`, `(1,1)`, and `(1,2)` -- the whole connected region of `1`s reachable from `(0,0)`
- c) All five cells

<details><summary>Show answer</summary>

**(b)** -- from (0,0) the fill reaches (0,1) (right), then (1,1) (down from (0,1)), then (1,2) (right). The `0` at (1,0) blocks the path, so (1,0) stays `0`.

</details>

**Q2 (analyze).** What happens if you index `image[r][c]` BEFORE checking bounds on a corner pixel?
- a) Nothing -- Java handles it
- b) When the recursion steps off the edge, `image[r][c]` throws `ArrayIndexOutOfBoundsException` before the guard can fire
- c) It silently fills the wrong region

<details><summary>Show answer</summary>

**(b)** -- bounds must be tested first; indexing an out-of-range cell crashes before the guard gets a chance to return.

</details>

**Q3 (transfer).** How would you change the fill to spread through 8-directional neighbors (diagonals included)?

<details><summary>Show answer</summary>

Add the four diagonal offsets to the `DR/DC` direction arrays (4 offsets become 8); the repaint-as-visited and same-color guards are unchanged.

</details>

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
