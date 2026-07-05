# 0036 - Valid Sudoku

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/valid-sudoku/

## Problem

Determine whether a 9 x 9 Sudoku board is valid. Only the filled cells need to be validated
according to three rules: each row, each column, and each of the nine 3 x 3 sub-boxes must
contain the digits 1-9 without repetition. Empty cells are marked `'.'`. The board does not need
to be solvable -- only that the current placements break no rule.

Signature:

    boolean isValidSudoku(char[][] board)

Example (a valid board):

    5 3 . . 7 . . . .
    6 . . 1 9 5 . . .
    . 9 8 . . . . 6 .
    8 . . . 6 . . . 3
    4 . . 8 . 3 . . 1
    7 . . . 2 . . . 6
    . 6 . . . . 2 8 .
    . . . 4 1 9 . . 5
    . . . . 8 . . 7 9

## Intuition

The trigger signal is "no repeats" -- existence checks across three different groupings (rows,
columns, boxes). For each group we want a HashSet that beeps the moment a digit tries to enter a
second time. The only wrinkle is the box grouping: a cell at `(r, c)` lives in box index
`(r / 3) * 3 + c / 3`, which squashes the 9x9 grid into nine 3x3 boxes numbered 0..8 in
row-major order. One pass over all 81 cells, three set lookups and three inserts per filled cell,
and we return `false` on the very first conflict.

## Pseudocode

    function isValidSudoku(board):
        create 9 empty sets "rows", 9 empty sets "cols", 9 empty sets "boxes"
        for each row r from 0 to 8:
            for each column c from 0 to 8:
                ch <- board[r][c]
                if ch is '.':
                    skip this cell
                boxIndex <- (r / 3) * 3 + (c / 3)
                if ch is already in rows[r]: return false
                if ch is already in cols[c]: return false
                if ch is already in boxes[boxIndex]: return false
                add ch to rows[r], cols[c], and boxes[boxIndex]
        return true

## Java Solution

```java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Solution {
    public boolean isValidSudoku(char[][] board) {
        List<Set<Character>> rows = new ArrayList<>();
        List<Set<Character>> cols = new ArrayList<>();
        List<Set<Character>> boxes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            rows.add(new HashSet<>());
            cols.add(new HashSet<>());
            boxes.add(new HashSet<>());
        }

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch == '.') {
                    continue;
                }
                int boxIndex = (r / 3) * 3 + c / 3;
                if (!rows.get(r).add(ch)
                        || !cols.get(c).add(ch)
                        || !boxes.get(boxIndex).add(ch)) {
                    return false;
                }
            }
        }
        return true;
    }
}
```

We hold nine sets of each kind in three `List<Set<Character>>` indexed by group number; a cell's
box index folds the 2-D grid into one of those nine slots via `(r / 3) * 3 + c / 3`. The
`Set.add(x)` idiom returns `false` exactly when `x` was already present, so a single `add` both
checks for a duplicate and inserts -- no separate `contains` call needed. The three adds are
chained with `||`, short-circuiting on the first conflict so we return `false` immediately and
leave the (now-invalid) sets untouched.

## Complexity

    Time:  O(1)        -- the board is always 9x9 = 81 cells; constant work per cell
    Space: O(1)        -- 27 sets, each holding at most 9 entries; constant

(For a generalized n x n board both bounds become O(n^2).)

## Dry-Run

Take a board whose top-left 3x3 box repeats a `5`:

```
5 . .  . . .  . . .
. 5 .  . . .  . . .
. . .  . . .  . . .
8 . .  . . .  . . 3
4 . .  8 . 3  . . 1
7 . .  . 2 .  . . 6
. 6 .  . . .  2 8 .
. . .  4 1 9  . . 5
. . .  . 8 .  . 7 9
```

Walking the first few filled cells:

| (r, c) | ch | boxIndex | rows[r]      | cols[c]      | boxes[box]   | result |
|-------:|----|---------:|--------------|--------------|--------------|--------|
| (0,0)  | 5  | 0        | add ok -> {5}| add ok -> {5}| add ok -> {5}| -      |
| (1,1)  | 5  | 0        | add ok -> {5}| add ok -> {5}| 5 ALREADY in box 0 | false |

`boxes[0].add('5')` returns `false` because the box already holds a `5` from (0,0), so the
function returns `false` at (1,1). Rows and columns each still have only one `5`, so without the
box check the board would wrongly pass.

## Common mistakes

- Treating `'.'` as a digit and inserting it (or failing to skip it), polluting the sets.
- Computing the box index wrong. Common wrong forms: `r + c`, `(r/3 + c/3)`. The correct one is
  `(r / 3) * 3 + c / 3` -- multiply the row-block by 3, then add the column-block.
- Reusing a single set across rows and columns, so a digit in row 0 "remembers" a digit in row 1.
  You need nine independent sets per dimension.
- Calling `contains` then `add` separately -- harmless but double the work; `add`'s boolean return
  already tells you whether it was new.
- Forgetting to return `false` and letting the loop finish, then only checking at the end. The
  early return is both faster and clearer.

## Related problems

- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the single-set "beep on second
  sighting" idea, applied nine times per dimension here.
- [0036 - Valid Sudoku (you are here)](./) generalizes existence checking to multiple simultaneous
  groupings.
- [0242 - Valid Anagram](../0242-valid-anagram/) - another "no repeats" rule, enforced via counts
  instead of sets.
