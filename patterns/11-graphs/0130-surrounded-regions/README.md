# 0130 - Surrounded Regions

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/surrounded-regions/

## Concepts used

- **Graph** -- a set of nodes connected by edges. The board is an implicit graph: each cell is a
  node, its up/down/left/right cells are its neighbors (the edges).
  [glossary](../../../docs/10-glossary.md#graph)
- **Connected component** -- the largest group of nodes reachable from one another; a region of `'O'`
  cells is one connected component. [glossary](../../../docs/10-glossary.md#connected-component)
- **DFS (depth-first search)** -- a traversal that goes as deep as possible before backing up.
  [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)

## Problem

Given an `m x n` matrix `board` containing `'X'` and `'O'`, **capture** every region of `'O'`s that
is fully surrounded by `'X'`s (4-directionally) by flipping all its `'O'`s to `'X'`s in place. An
`'O'` region is *not* surrounded if any of its cells touches the **border** of the board -- border-
touching regions are left unchanged.

Signature:

    void solve(char[][] board)

Examples:

    Input:  board = [
              ["X","X","X","X"],
              ["X","O","O","X"],
              ["X","X","O","X"],
              ["X","O","X","X"]
            ]
    Output: [
              ["X","X","X","X"],
              ["X","X","X","X"],
              ["X","X","X","X"],
              ["X","O","X","X"]
            ]
    (the bottom-row O at column 1 touches the border, so its region is spared)

## Intuition

An `'O'` region is a patch of `'O'`s sitting in a field of `'X'`s. The problem says to flip every
`'O'` region to `'X'` *unless* it touches the outer border of the board -- because a region that
touches the border is not truly "surrounded". The hard part: staring at one `'O'` deep inside, you
cannot tell locally whether its region eventually sneaks out to the border. You have to look at the
*whole* region.

So flip the question (this is the key move): instead of "is this `'O'` surrounded?", ask "is this
`'O'` connected to a border `'O'?". A region is *safe* exactly when at least one of its cells touches
the border. Treat the board as an implicit [graph](../../../docs/10-glossary.md#graph) (each cell a
node, its up/down/left/right cells its **neighbors**, the edges); an `'O'` region is one
[connected component](../../../docs/10-glossary.md#connected-component) of `'O'` nodes -- the largest
group reachable from one another. The safe regions are precisely the connected components that
include a border cell.

So: start a [DFS](../../../docs/10-glossary.md#dfs-depth-first-search) from every `'O'` sitting on
the four edges, and mark every `'O'` reachable from them as *safe* (we use a temporary marker `'S'`,
which doubles as our **visited** flag -- a mark so we never step on the same cell twice). After that
pass, any `'O'` still left on the board was provably *not* reachable from a border -- so it must be
fully surrounded, and we flip it to `'X'`. Finally we turn the `'S'` markers back into `'O'`.

Trace:

```
X X X X
X O O X
X X O X
X O X X
```

The only border `'O'` is at (3,1) -- mark it `'S'`; its neighbors are all `'X'` or out-of-bounds, so
the DFS stops there. Now the cleanup pass: the inner cluster (1,1), (1,2), (2,2) are still `'O'` ->
flip to `'X'`; the `'S'` at (3,1) -> back to `'O'`. The bottom `'O'` survives *because* it touched
the border; the inner cluster did not, so it was surrounded and flipped.

### Checkpoint A -- Flip the question

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why mark border-connected `'O'`s as `'S'` first, rather than testing each interior `'O'`?
- a) `'S'` is required by the problem statement
- b) An interior `'O'` cannot tell locally whether its region escapes to the border; marking from the border out identifies each whole safe region in one pass
- c) Border cells are fewer, so it is faster

<details><summary>Show answer</summary>

**(b)** -- "is this region surrounded?" is a whole-region question, so it is easiest to flip it: mark everything reachable FROM a border `'O'`, then whatever `'O'` remains is provably surrounded.

</details>

**Q2 (comprehend).** In the cleanup pass, why can `'O' -> 'X'` and `'S' -> 'O'` happen in a single pass with no conflict?
- a) They run in separate loops
- b) `'O'` and `'S'` are mutually exclusive marks -- each cell is exactly one of them -- so one if/else handles both
- c) The order of the two checks matters

<details><summary>Show answer</summary>

**(b)** -- a cell is either still `'O'` (surrounded -> flip to `'X'`) or `'S'` (safe -> restore to `'O'`); the two cases never overlap, so one pass suffices.

</details>

## Pseudocode

    function solve(board):
        if board is empty: return
        rows = number of rows, cols = number of columns

        # 1. Mark every border-reachable 'O' as SAFE.
        for each border cell (r, c):
            if board[r][c] == 'O':
                markSafe(r, c)

        # 2. Flip the rest: any 'O' is surrounded -> 'X', any 'S' is safe -> 'O'.
        for each cell (r, c):
            if board[r][c] == 'O': board[r][c] = 'X'
            else if board[r][c] == 'S': board[r][c] = 'O'

    function markSafe(r, c):
        if r or c out of bounds or board[r][c] != 'O':
            return
        board[r][c] = 'S'                      # mark safe + visited
        markSafe(r - 1, c)
        markSafe(r + 1, c)
        markSafe(r, c - 1)
        markSafe(r, c + 1)

`markSafe` is the same grid DFS as Flood Fill / Number of Islands, with `'S'` instead of `'0'` as
the target so we can later tell safe `'O'`s apart from surrounded ones. The cleanup loop in step 2
is a single pass that performs both flips in one go.

## Java Solution

```java
class Solution {
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};

    private char[][] board;
    private int rows, cols;

    public void solve(char[][] board) {
        if (board.length == 0) {
            return;
        }
        this.board = board;
        this.rows = board.length;
        this.cols = board[0].length;

        for (int r = 0; r < rows; r++) {
            markSafe(r, 0);            // left border
            markSafe(r, cols - 1);     // right border
        }
        for (int c = 0; c < cols; c++) {
            markSafe(0, c);            // top border
            markSafe(rows - 1, c);     // bottom border
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') {
                    board[r][c] = 'X';
                } else if (board[r][c] == 'S') {
                    board[r][c] = 'O';
                }
            }
        }
    }

    private void markSafe(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols || board[r][c] != 'O') {
            return;
        }
        board[r][c] = 'S';
        for (int k = 0; k < 4; k++) {
            markSafe(r + DR[k], c + DC[k]);
        }
    }
}
```

The two `for` loops only walk the four edges -- interior cells are ignored as DFS seeds because any
safe region will be discovered from its border-touched cell anyway. The fused guard
`board[r][c] != 'O'` makes `'S'`, `'X'`, and out-of-bounds all return instantly, so a single
condition covers visited/water/border. We restore `'S' -> 'O'` in the same pass as `'O' -> 'X'` --
they are mutually exclusive marks, so order within the loop does not matter.

## Complexity

    Time:  O(m * n)  -- every cell is touched by at most one DFS plus one cleanup inspection
    Space: O(m * n)  -- worst-case recursion depth if a safe region snakes across the board

## Dry-Run

Input:

```
X X X X
X O O X
X X O X
X O X X
```

Step 1 -- DFS from border `'O'`s. The only border `'O'` is at `(3,1)`.

| Call          | Action                                              |
|---------------|-----------------------------------------------------|
| markSafe(3,1) | set 'S', recurse into neighbors                     |
| markSafe(2,1) | cell is 'X' (board[2][1]), return                   |
| markSafe(4,1) | out of bounds, return                               |
| markSafe(3,0) | cell is 'X', return                                 |
| markSafe(3,2) | cell is 'X', return                                 |

After step 1 (only `(3,1)` got marked):

```
X X X X
X O O X
X X O X
X S X X
```

Step 2 -- single cleanup pass:

| Cell  | Before | Rule        | After |
|-------|--------|-------------|-------|
| (1,1) | O      | surrounded  | X     |
| (1,2) | O      | surrounded  | X     |
| (2,2) | O      | surrounded  | X     |
| (3,1) | S      | safe        | O     |
| (any) | X      | unchanged   | X     |

Final board:

```
X X X X
X X X X
X X X X
X O X X
```

The bottom-row `'O'` survives *because* it touched the border; the inner cluster did not, so it was
surrounded and flipped.

### Checkpoint B -- Mark a new board

**Q1 (apply).** Trace a new board that is a 2x2 of all `'O'` (every cell sits on the border). After `solve()`, what is the board?
- a) All `'X'` -- everything is captured
- b) All `'O'` -- every cell touches a border, so every `'O'` is marked safe and restored
- c) The corners become `'X'`, the rest stay `'O'`

<details><summary>Show answer</summary>

**(b)** -- the border-seeded DFS marks all four cells `'S'`, and the cleanup restores every `'S'` back to `'O'`. Nothing is surrounded because nothing is fully interior.

</details>

**Q2 (analyze).** What breaks if you seed DFS from only the TOP and LEFT edges?
- a) Nothing -- the other edges are redundant
- b) A region that escapes only through the bottom or right border never gets marked safe and is wrongly flipped to `'X'`
- c) It throws on rectangular boards

<details><summary>Show answer</summary>

**(b)** -- all four edges must seed the DFS; missing one leaves a whole escape route unmarked, so a genuinely border-connected region looks surrounded.

</details>

**Q3 (transfer).** How would you solve the inverse -- flip the `'O'` regions that DO touch the border to `'X'`, and leave surrounded regions alone?

<details><summary>Show answer</summary>

Run the same border DFS to mark safe cells, but in cleanup flip the `'S'` (border-connected) cells to `'X'` and leave the untouched interior `'O'`s as `'O'` -- the reverse of the original rules.

</details>

## Common mistakes

- Trying to detect "surrounded" with a single inward DFS -- you cannot know from the inside whether
  the region escapes to the border. Always flip the question: "is this reachable from a border O?"
- Forgetting to seed DFS from **all four** edges. Missing the bottom or right edge lets a region
  that escapes only through those borders get wrongly flipped.
- Using `'0'` (zero) or `'1'` as the safe marker instead of a clearly different character like
  `'S'`. With `'0'` you cannot distinguish safe from water during cleanup.
- Not restoring `'S' -> 'O'` -- the board is left with stray `'S'` marks.
- Skipping the empty-board guard. `board[0].length` throws on a `new char[0][]` input.

## Related problems

- [0200 - Number of Islands](../0200-number-of-islands/) - same DFS, different question (count vs
  flip), no border reasoning needed.
- [0417 - Pacific Atlantic Water Flow](../0417-pacific-atlantic-water-flow/) - same "two-pass DFS
  from the edges" idea, applied to elevation rather than X/O.
- [0286 - Walls and Gates](https://leetcode.com/problems/walls-and-gates/) - multi-source BFS from
  all gates inward, similar border-as-source thinking.
