# 0695 - Max Area of Island

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/max-area-of-island/

## Concepts used

- **Graph** -- a set of nodes connected by edges. A grid is an implicit graph: each cell is a node,
  its up/down/left/right neighbors are its edges.
  [glossary](../../../docs/10-glossary.md#graph)
- **Connected component** -- the largest group of nodes reachable from one another; one island is one
  connected component. [glossary](../../../docs/10-glossary.md#connected-component)
- **DFS (depth-first search)** -- a traversal that goes as deep as possible before backing up.
  [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)

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

This is Number of Islands' twin, with one twist: instead of *how many* land-puddles, we want the
*size of the biggest* one. Same puddle picture -- after rain each puddle is a connected patch of
water; here each island is a connected patch of land -- but now we measure the largest puddle rather
than count them.

Smallest case:

```
0 1 1 0
1 0 0 0
1 1 1 0
```

Two patches of land. The top one has 2 cells; the bottom-left one has 5 cells. The answer is 5.

How do we measure one patch? When we find an unvisited land cell, we don't just mark it and move on
-- we want to *count* every cell in its island. The natural shape is a
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search) that **returns** the size of the region it
explores: `1` (for the current cell itself) plus whatever each of its four land neighbors adds.
Treat the grid as an implicit [graph](../../../docs/10-glossary.md#graph) (each cell a node, its
up/down/left/right cells its neighbors/edges); one island is one
[connected component](../../../docs/10-glossary.md#connected-component). The recursion dives into
each land neighbor, sinking cells to `0` as it goes -- and sinking is the **visited** mark: once a
cell is `0` it is never counted again.

Now the reasoning (no "trust the recursion" handwave). When `area(r, c)` sinks cell (r, c) and then
calls `area` on each neighbor, *every land cell of this island will be reached by exactly one of
those calls*, because 4-directional reachability defines the island in the first place. Sinking
*before* recursing guarantees no cell is entered twice, so no cell is counted twice. The total the
recursion adds up is therefore exactly the island's cell count -- no more, no less.

General rule: scan every cell; on each unvisited `1`, run the area-DFS and keep the largest result it
returns. If the grid is all water, the scan never finds a `1`, the best stays at its initial value,
and the answer is 0.

### Checkpoint A -- Measure the biggest puddle

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** `area` returns an int. What does the `1` in `return 1 + area(up) + area(down) + ...` count?
- a) The recursion depth
- b) The current cell itself -- one cell of area
- c) The number of neighbors explored

<details><summary>Show answer</summary>

**(b)** -- the `1` is the cell just sunk; the four recursive calls each contribute the area of the sub-region beyond that neighbor.

</details>

**Q2 (comprehend).** Why does sinking the cell to `0` BEFORE the recursive calls prevent double-counting?
- a) It sorts the grid
- b) Once a cell is `0`, no neighbor's recursion can re-enter it, so every cell is counted exactly once
- c) It frees memory

<details><summary>Show answer</summary>

**(b)** -- the `!= 1` guard turns away any later call that reaches an already-sunk cell, so the recursion adds each cell's `1` exactly once.

</details>

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

### Checkpoint B -- Measure a new grid

**Q1 (apply).** Trace this new grid:

    1 1
    1 1

What is the maximum area returned?
- a) `1`
- b) `2`
- c) `4` -- it is one connected 2x2 island, so four cells

<details><summary>Show answer</summary>

**(c)** -- all four cells are 4-connected into a single island; `area` returns `4` and `best` becomes `4`.

</details>

**Q2 (analyze).** What goes wrong if `area` does `return 1;` and forgets to add the recursive neighbor areas?
- a) Infinite recursion
- b) You sink the whole island correctly but report area `1` for every island, so `best` is wrong
- c) `StackOverflowError`

<details><summary>Show answer</summary>

**(b)** -- the cells are still sunk (visited marking works), but the size never accumulates up the recursion, so each island reports `1`.

</details>

**Q3 (transfer).** How would you adapt this to find the area of the SMALLEST island (treating no-island as no answer)?

<details><summary>Show answer</summary>

Swap the max for a min that starts large; call `area` on each unvisited `1`; whenever it returns a positive value, update `min = min(min, area)`. Leave `min` at its large start if the grid is all water.

</details>

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
