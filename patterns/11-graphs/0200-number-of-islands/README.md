# 0200 - Number of Islands

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/number-of-islands/

## Concepts used

- **Graph** -- a set of items (called *nodes*) linked by connections (called *edges*). A grid is an
  *implicit* graph: each cell is a node, and its up/down/left/right neighbors are its edges.
  [glossary](../../../docs/10-glossary.md#graph)
- **Connected component** -- the largest group of nodes where you can walk from any one to any other
  through edges; one island = one connected component.
  [glossary](../../../docs/10-glossary.md#connected-component)
- **DFS (depth-first search)** -- a traversal that dives as deep as possible before backing up, like
  exploring a maze by always taking the next passage until a dead end.
  [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)

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

Imagine a field after heavy rain. The water gathers into separate puddles -- each puddle is one
connected patch of water, and you can tell two puddles apart because dry ground sits between them.
An *island* in this problem is exactly such a puddle, but made of land: a connected patch of `'1'`s
separated from other patches by `'0'`s (water). Counting islands is just counting those separate
land-puddles.

"Connected" here means *4-directional*: a cell's **neighbors** are only the cells directly up, down,
left, and right of it (no diagonal touching). So we can treat the grid as an implicit
[graph](../../../docs/10-glossary.md#graph): each land cell is a **node** (a single item in the
graph), and an invisible **edge** (a link) joins it to each of its four neighbors. The question "how
many islands?" is then "how many [connected components](../../../docs/10-glossary.md#connected-component)
does this graph have?" -- a connected component being a maximal group of nodes reachable from one
another, i.e. exactly one island.

Trace the smallest case:

```
1 1 0
1 0 0
0 0 1
```

Scan the grid top-to-bottom, left-to-right. The first `'1'` we meet is at (0,0) -- the start of
island #1. We **visit** it: overwrite that `'1'` with `'0'` (this "sinking" is our **visited** mark --
a flag so we never step on this cell again), then jump to each land neighbor and repeat. (0,1) is
land, sink and explore from there; (1,0) is land, sink and explore. When this wave of
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search) (depth-first search -- keep diving into the
next land neighbor before coming back) finishes, every cell of the top-left L-shape is now `'0'` --
we have traced exactly one whole island, so we add 1 to the count. Keep scanning: the cells we sank
are `'0'`, so we skip them. The next `'1'` we hit is at (2,2) -- it must be a *new* island, because
if it were reachable from island #1 it would already be `'0'`. Explore it, count becomes 2. Answer: 2.

General rule: scan every cell; the moment you find an unvisited `'1'`, you have discovered a
brand-new island -- increment the count, then sink the entire island so none of its cells can trigger
the count again. We do **not** need a separate `visited` array, because sinking *is* the visited
mark: once a land cell becomes `'0'`, it fails both the scan's "is this land?" check and the
recursion's "is this a land neighbor?" check, so it can never be re-entered. Each cell is therefore
touched exactly once across the whole algorithm.

### Checkpoint A -- Count the puddles

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Overwriting a land cell `'1'` with `'0'` (sinking) does what, besides changing the grid?
- a) It only tidies the grid visually
- b) It marks the cell as visited, so it can never be re-entered -- the sinking *is* the visited mark
- c) It adds one to the island count

<details><summary>Show answer</summary>

**(b)** -- once a cell is `'0'`, it fails both the scan's "is this land?" check and the recursion's neighbor check, so no code path can step on it again.

</details>

**Q2 (comprehend).** Why is `count` incremented only in the OUTER scan, never inside `sink`?
- a) Because `sink` is too slow to count
- b) Because one call to `sink` from the outer loop erases one whole island, so one increment per newly discovered `'1'` equals one per island
- c) Because the grid is small

<details><summary>Show answer</summary>

**(b)** -- every cell of an island gets sunk by a single `sink` call, so the next `'1'` the outer scan meets must belong to a different island. Counting inside `sink` would count cells, not islands.

</details>

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

### Checkpoint B -- Sink a new grid

**Q1 (apply).** Trace this new grid:

    1 0
    0 1

What is the output?
- a) `1` -- the two `1`s touch diagonally
- b) `2` -- connectivity is 4-directional, so diagonal cells do NOT connect; each `1` is its own island
- c) `0`

<details><summary>Show answer</summary>

**(b)** -- (0,0) and (1,1) share no edge, only a corner. Diagonals are not neighbors, so the scan discovers two separate islands.

</details>

**Q2 (analyze).** What breaks if you index `grid[r][c]` BEFORE the bounds check?
- a) Nothing -- Java clamps the index
- b) On a border cell the recursion steps off the edge and throws `ArrayIndexOutOfBoundsException` before the guard can fire
- c) It silently returns the wrong count

<details><summary>Show answer</summary>

**(b)** -- the fused guard checks bounds first; reversing the order means an out-of-range `r`/`c` indexes the array and crashes.

</details>

**Q3 (transfer).** If the problem used 8-directional connectivity (diagonals count), what is the smallest change to the solution?

<details><summary>Show answer</summary>

Expand the neighbor list from four to eight directions (add the four diagonal offsets); the scan-sink-count structure stays identical.

</details>

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
