# 0036 - Valid Sudoku

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/valid-sudoku/

## Concepts used

- **Hash set** -- a container that remembers seen values and refuses a repeat; we keep one per
  row, one per column, and one per 3x3 box. [glossary](../../../docs/10-glossary.md#hash-set)
- **Array** -- numbered slots; we use nine of them to hold nine sets.
  [glossary](../../../docs/10-glossary.md#array)
- **Linear scan** -- walking all 81 cells of the board once.
  [glossary](../../../docs/10-glossary.md#linear-scan)

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

A bouncer checks three guest lists at the door -- one for the row, one for the column, one for the
booth. Each guest (a digit) must be new on ALL three lists. The instant a name shows up twice on
any single list, the alarm sounds and entry is refused.

Look at the smallest conflict. Suppose the top-left cell holds a `5`, and the cell directly below
it (still inside the same 3x3 box) also holds a `5`. The box's guest list already has a `5`, so
the second `5` trips the alarm -- the board is invalid, even though no row and no column repeats.

The general rule: a valid Sudoku demands three independent "no repeats" rules -- each row, each
column, and each of the nine 3x3 boxes must contain the digits 1-9 at most once. For each rule we
keep a [hash set](../../../docs/10-glossary.md#hash-set) that refuses a second sighting of the
same digit. Scan all 81 cells once; for each filled cell, try to add its digit to three sets --
its row's set, its column's set, and its box's set. If any add fails (the digit was already
there), the board is invalid.

How do we know which 3x3 box a cell belongs to? Number the nine boxes 0 through 8 in reading order
(left-to-right, top-to-bottom). A cell at row `r`, column `c` lives in box `(r / 3) * 3 + (c / 3)`.
Here `r / 3` is whole-number division (Java's `int / int`), giving which band of three rows the
cell is in (0, 1, or 2); `c / 3` gives which band of three columns. Multiplying the row-band by 3
and adding the column-band flattens the 3x3 grid of boxes into a single number 0-8.

### Checkpoint A -- Three rules, three lists

Pause before expanding.

**Q1 (recall).** How many independent "no repeats" rules must each digit satisfy?
- a) One (just the row)
- b) Three: row, column, and 3x3 box
- c) Nine

<details><summary>Show answer</summary>

**(b)** -- a digit must be new on its row, its column, AND its 3x3 box. Failing any one invalidates the board.

</details>

**Q2 (comprehend).** A cell at row `r`, column `c` belongs to which box index?
- a) `r + c`
- b) `(r / 3) * 3 + (c / 3)`
- c) `(r + c) / 3`

<details><summary>Show answer</summary>

**(b)** -- `r/3` is the row-band (0,1,2), `c/3` is the column-band; multiply the row-band by 3 and add the column-band to flatten the 3x3 layout of boxes into 0..8.

</details>

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

### Checkpoint B -- Read the conflict

**Q1 (apply).** Cell (2, 4) has a `7`, and cell (2, 7) also has a `7`. Which rule catches this as invalid?
- a) The 3x3 box rule
- b) The row rule (both in row 2)
- c) The column rule

<details><summary>Show answer</summary>

**(b)** -- both cells are in row 2, so row 2's set already holds a `7` when the second is added; `add` returns false and the board is invalid.

</details>

**Q2 (analyze).** Why is `Set.add(ch)` used as the check, rather than calling `contains` then `add`?
- a) `add` is the only method that works on Characters
- b) `add` returns `false` exactly when the element was already present, so one call both tests and inserts -- half the work
- c) `contains` would throw on duplicates

<details><summary>Show answer</summary>

**(b)** -- `add` returns true if newly inserted, false if already there. Using its boolean return combines the check and the insert into one operation.

</details>

**Q3 (transfer).** How would you generalize this to an n x n board (with sqrt(n) x sqrt(n) boxes)? What changes about the complexity?

<details><summary>Show answer</summary>

Loop bounds become n instead of 9, the box formula stays `(r / s) * s + (c / s)` with `s = sqrt(n)`, and you allocate n sets per dimension. Time and space both become O(n^2), since there are n^2 cells and up to n distinct symbols.

</details>

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
