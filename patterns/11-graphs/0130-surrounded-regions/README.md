# 0130 - Surrounded Regions

**Difficulty:** Medium
**Pattern:** Graphs
**LeetCode:** https://leetcode.com/problems/surrounded-regions/

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

The trap is the word "surrounded": you cannot decide whether an `'O'` should flip just by looking at
its local neighborhood -- you have to know whether *any* cell in its region reaches the border. Flip
that around: a region is safe if and only if it touches the border. So instead of asking "is this O
surrounded?", ask "is this O connected to a border O?". The trigger -- "grid", "connected" -- is
classic grid DFS, but applied from the **border inward**: start a DFS from every `'O'` on the four
edges and mark every `'O'` reachable from them as `SAFE`. After that pass, any `'O'` still on the
board was provably not reachable from a border, so it must be fully surrounded -- flip it. Finally
restore the `SAFE` markers back to `'O'`.

We use a third character `'S'` for the temporary safe mark, which doubles as the visited-set.

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
