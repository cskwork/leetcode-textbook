# 0079 - Word Search

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/word-search/

## Problem

Given an `m x n` grid of characters `board` and a string `word`, return `true`
if `word` exists in the grid. The word can be constructed from letters of
**sequentially adjacent** cells, where "adjacent" cells are horizontally or
vertically neighboring. The same cell may not be used more than once in the
same word.

Signature:

    boolean exist(char[][] board, String word)

Example (verbatim from LeetCode):

    board = [["A","B","C","E"],
             ["S","F","C","S"],
             ["A","D","E","E"]]
    Input:  word = "ABCCED"
    Output: true

    Input:  word = "SEE"
    Output: true

    Input:  word = "ABCB"
    Output: false

## Intuition

This is the problem that takes backtracking *off a list and onto a grid*. The
skeleton is unchanged -- choose, explore, un-choose -- but the "choices" are
now the four directions from the current cell, the "path" is the prefix of
`word` matched so far, and the "used" mask is the set of cells we cannot
revisit on this branch.

The trigger signals are stacked: "find a word" (try the Trie pattern) but on a
grid with "sequentially adjacent" and "each cell at most once" -- that is
backtracking on a graph. We start a DFS from every cell whose character
matches `word[0]`, and at each step we try to extend the matched prefix by one
character in each of the four directions.

The two ideas that make this problem tractable:

1. **Mark visited cells in place.** A separate `boolean[][] visited` works but
   doubles the per-cell state. Cleaner: overwrite `board[r][c]` with a
   sentinel character (`'#'`) before recursing and restore it after. The cell
   is its own visited flag.
2. **Prune early.** If at any point the next character does not match the
   cell, return false immediately. And before doing any work at all, a quick
   frequency check -- if `word` uses a character the board doesn't have enough
   of, return false in `O(m*n)` instead of doing the search.

The base case is "prefix index reached the end of `word`" -- at that point
every character has been matched and we return true.

## Pseudocode

    function exist(board, word):
        # Optional but powerful prune: count chars; bail out fast.
        if any char in word occurs more times than on the board: return false
        rows = number of rows, cols = number of cols
        for r from 0 to rows-1:
            for c from 0 to cols-1:
                if board[r][c] == word[0]:
                    if backtrack(board, r, c, word, index = 0): return true
        return false

    function backtrack(board, r, c, word, index):
        if index == length(word) - 1:              # last char already matched
            return true
        save = board[r][c]
        board[r][c] = '#'                          # CHOOSE: mark visited in place
        for each (nr, nc) in {(r-1,c),(r+1,c),(r,c-1),(r,c+1)}:
            if nr, nc in bounds
               and board[nr][nc] == word[index + 1]:
                if backtrack(board, nr, nc, word, index + 1): return true
        board[r][c] = save                         # UN-CHOOSE: restore the cell
        return false

A few structural notes:

- The "CHOOSE" is *one* line -- overwriting `board[r][c]` -- and the
  "UN-CHOOSE" is *one* line -- restoring it. The mechanism is different from
  the `path.add`/`path.remove` of the list problems, but the discipline is
  identical: every change made on the way down is undone on the way up.
- The early `return true` on the first successful neighbour propagates
  immediately, so the search stops as soon as one valid path is found -- we
  do not need to enumerate all of them.
- Restoring `board[r][c]` happens *after* the loop, not inside it. The cell
  was visited for the duration of this whole frame; only when the frame is
  done exploring all four directions does it release the cell back.

## Java Solution

```java
import java.util.*;

class Solution {
    public boolean exist(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;

        // Cheap prune: if the board lacks enough of some character, bail out.
        int[] freq = new int[128];
        for (char[] row : board) for (char ch : row) freq[ch]++;
        for (char ch : word.toCharArray()) {
            if (--freq[ch] < 0) return false;
        }

        // Walk every cell; start a DFS wherever the first char matches.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == word.charAt(0)
                        && backtrack(board, r, c, word, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean backtrack(char[][] board, int r, int c, String word, int index) {
        if (index == word.length() - 1) return true;       // last char already matched
        char saved = board[r][c];
        board[r][c] = '#';                                  // CHOOSE: mark visited in place
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int d = 0; d < 4; d++) {
            int nr = r + dr[d], nc = c + dc[d];
            if (nr >= 0 && nr < board.length
                    && nc >= 0 && nc < board[0].length
                    && board[nr][nc] == word.charAt(index + 1)
                    && backtrack(board, nr, nc, word, index + 1)) {
                return true;                                // EXPLORE: first hit wins
            }
        }
        board[r][c] = saved;                               // UN-CHOOSE: restore the cell
        return false;
    }
}
```

The frequency-count prune at the top is the single biggest real-world speedup
on this problem: when `word` contains a character the board doesn't have
enough of, the whole search is impossible and we say so in `O(m*n)` without
ever recursing. Marking visited cells in place with `'#'` avoids allocating a
separate `boolean[][] visited` per search path -- the cell itself is the flag,
and restoring it with `board[r][c] = saved` is the un-choose step that lets
sibling branches see the original board. The neighbour check
`board[nr][nc] == word.charAt(index + 1)` doubles as both the bounds-safe
match *and* the visited check: a visited cell holds `'#'`, which never equals
any real next character, so the same condition rejects revisits for free.
Returning `true` the moment any neighbour succeeds propagates the success up
without exploring the remaining directions -- we need one valid path, not all
of them.

## Complexity

    Time:  O(m * n * 3^L) where L = word.length. We start a DFS from each of the
           m*n cells, and at each step we have at most 3 unvisited neighbours
           (you cannot go back to the cell you came from), branching up to L
           levels deep. The frequency prune and the early-exit make the real
           constant much smaller than the worst case.
    Space: O(L) recursion depth. The board is mutated in place, so no extra
           visited matrix is allocated.

## Dry-Run

On `board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]`,
`word = "SEE"`. Frequency prune: board has 4 `E`s and 1 `S`, word needs 1 `S`
and 2 `E`s -- pass. `exist` walks every cell looking for `word[0]='S'`.

The only `S` cells are `(1,0)` and `(1,3)`. We try `(1,0)` first; none of its
neighbours (`A`, `A`, oob, `F`) equal `word[1]='E'`, so that frame restores
`(1,0)='S'` and returns false. Then we try `(1,3)`:

```
backtrack((1,3), index=0)        # board[1][3]='S' == word[0]='S'
  saved='S', board[1][3]='#'      # CHOOSE: mark visited in place
  try neighbours of (1,3):
    (0,3)=E   word[1]='E'? YES
        backtrack((0,3), index=1)
          saved='E', board[0][3]='#'
          try neighbours of (0,3):
            (-1,3) oob
            (1,3)='#'  word[2]='E'? no    <- the in-place marker blocks the revisit
            (0,2)=C   word[2]='E'? no
            (0,4) oob
          -> all fail, restore board[0][3]='E' (UN-CHOOSE), return false
    (2,3)=E   word[1]='E'? YES
        backtrack((2,3), index=1)
          saved='E', board[2][3]='#'
          try neighbours of (2,3):
            (1,3)='#'  word[2]='E'? no    <- blocked again
            (3,3) oob
            (2,2)=E   word[2]='E'? YES
                backtrack((2,2), index=2)
                  index == word.length()-1 (2==2) -> RETURN TRUE
  true propagates: backtrack((2,3)) -> backtrack((1,3)) -> exist returns true
```

So the answer is `true`, found via the path `(1,3) -> (2,3) -> (2,2)` spelling
`S-E-E`. Notice how the in-place `'#'` at `(1,3)` correctly blocked both
attempts to walk back onto the starting cell -- the marker *is* the visited
mask, no separate array needed.

## Common mistakes

- **Forgetting to restore `board[r][c]`.** The cell stays `'#'` after the
  frame returns, so sibling searches and *other starting cells* see a
  corrupted board and miss valid paths. The un-choose is a single line but it
  is load-bearing.
- **Restoring inside the loop instead of after it.** If you put
  `board[r][c] = saved` inside the `for d` loop, you un-mark the cell before
  trying the next direction, and a direction can now walk back onto the cell
  you are supposedly standing on. Restore exactly once, after the loop.
- **Checking the character *after* recursing, or recursing *before* the bounds
  check.** Either dereferences out-of-bounds memory or wastes a stack frame.
  Bounds-check first, character-match second, recurse third.
- **Treating the cell as visited via a separate `visited[][]` that you forget
  to clear.** Workable, but error-prone; the in-place overwrite is simpler
  because the un-choose is a single assignment paired with the choose.
- **Base case off-by-one.** The condition is `index == word.length() - 1`,
  meaning "we have just matched the last character"; not `== word.length()`
  (which would require one extra recursion past the end of the word).
- **No early termination on success.** If you keep exploring after finding a
  valid path you do `m*n*3^L` work instead of stopping at the first hit.

## Related problems

- [0078 - Subsets](../0078-subsets/) -- the parent skeleton; Word Search is
  the same choose/explore/un-choose shape, applied to a grid instead of a list.
- [0046 - Permutations](../0046-permutations/) -- another "mark used, recurse,
  un-mark" discipline; here the `used` set is cells on the grid instead of
  indices in the array.
- [0017 - Letter Combinations of a Phone Number](../0017-letter-combinations-of-a-phone-number/)
  -- the other grid-ish backtracker in this pattern; cartesian product over a
  digit->letters map.
