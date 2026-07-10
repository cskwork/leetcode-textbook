# 0074 - Search a 2D Matrix

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/search-a-2d-matrix/

## Concepts used

- **Binary search** -- finding a target in sorted data by repeatedly halving the search space. [glossary](../../../docs/10-glossary.md#binary-search)
- **Array** -- a row of numbered slots holding values, read instantly by position. [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- putting values in order so each value tells you about its neighbours. [glossary](../../../docs/10-glossary.md#sorting)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

You are given an `m x n` integer matrix `matrix` with two properties:

1. Each row is sorted in non-decreasing order.
2. The first integer of each row is greater than the last integer of the
   previous row.

Given an integer `target`, return `true` if `target` is in the matrix, or
`false` otherwise. You must write an `O(log(m * n))` solution.

Signature:

    boolean searchMatrix(int[][] matrix, int target)

Examples (verbatim from LeetCode):

    Input:  matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
    Output: true

    Input:  matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 13
    Output: false

## Intuition

Imagine writing a long list of numbers in order -- 1, 2, 3, 4, ... -- but the
paper is narrow, so after every 4 numbers you start a new line. You haven't
changed the order; you've just *wrapped* one sorted list onto several lines.
This matrix is exactly that: each row is sorted, *and* the first number of each
row is bigger than the last number of the row above, so reading row-by-row
produces one long sorted list.

Concretely, the matrix

    [[ 1,  3,  5,  7],
     [10, 11, 16, 20],
     [23, 30, 34, 60]]

is, for all ordering purposes, the flat array
`[1, 3, 5, 7, 10, 11, 16, 20, 23, 30, 34, 60]`. So LC 704 solves this problem
-- we only need a way to translate a position in that long list back into a
`(row, column)` so we can read the actual number. If each line holds `n`
numbers, then flat position `i` lives at row `i / n` and column `i % n` (integer
division and remainder).

Trace the search for `target = 3`. The flat list has 12 entries, so the search
range is `lo = 0, hi = 11`:

1. `mid = 5` -> row `5/4 = 1`, col `5%4 = 1` -> value `11`. `11 > 3`, so
   `hi = 4`.
2. `mid = 2` -> row `0`, col `2` -> value `5`. `5 > 3`, so `hi = 1`.
3. `mid = 0` -> value `1`. `1 < 3`, so `lo = 1`.
4. `mid = 1` -> value `3`. Match -- return `true`.

The **invariant** is the same as LC 704's: *if the target is in the matrix, it
lies in the flat-index range `[lo, hi]`.* The only new idea is the row/column
translation; everything else is the exact LC 704 loop.

### Checkpoint A -- Flatten, then search

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** The solution treats the matrix as one long sorted array. Given flat index `i` and `n` columns, which pair gives the (row, col)?
- a) row `= i / n`, col `= i % n`
- b) row `= i % n`, col `= i / n`
- c) row `= i / m`, col `= i % m` (where m = number of rows)

<details><summary>Show answer</summary>

**(a)** -- the column count is the divisor: each row holds `n` entries, so `i / n` is the row and the remainder `i % n` is the column.

</details>

**Q2 (comprehend).** Why is flattening valid here -- why is the resulting flat list actually sorted?
- a) Each row is sorted AND each row's first element is greater than the previous row's last element
- b) Because every matrix is sorted by default
- c) Because binary search sorts the data first

<details><summary>Show answer</summary>

**(a)** -- the two guarantees together mean reading row-by-row produces one globally increasing sequence, so a single binary search is correct.

</details>

## Pseudocode

    function searchMatrix(matrix, target):
        rows <- number of rows in matrix
        cols <- number of columns in matrix
        if rows = 0 or cols = 0:
            return false

        low  <- 0
        high <- rows * cols - 1            # treat the matrix as one sorted array

        while low <= high:
            mid  <- low + (high - low) / 2
            r    <- mid / cols             # translate flat index -> row
            c    <- mid mod cols           # translate flat index -> col
            value <- matrix[r][c]

            if value equals target:
                return true
            else if value < target:
                low  <- mid + 1
            else:
                high <- mid - 1

        return false

## Java Solution

```java
import java.util.*;

class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int lo = 0, hi = m * n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int value = matrix[mid / n][mid % n];
            if (value == target) {
                return true;
            } else if (value < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return false;
    }
}
```

This is Template A exactly, with one twist: instead of indexing `nums[mid]` we
translate the flat index `mid` into a 2-D coordinate with `mid / n` (row) and
`mid % n` (column). The search space is `[0, m*n - 1]`, the closed range of all
flat indices. The empty-matrix guard lives in the test driver rather than the
method here: LeetCode guarantees a non-empty `m x 1`-or-larger matrix, so the
guard would be dead code on the judge. We use `lo + (hi - lo) / 2` both for
overflow safety and because the search space can be very large (a 1000x1000
matrix has a million cells, and `lo + hi` could in principle exceed `int` range
on huge inputs). Because the value lookup is O(1), the total work is still
logarithmic in the number of cells.

## Complexity

    Time:  O(log(m * n))  -- one binary search over all m * n cells; each step does O(1) work.
    Space: O(1)           -- only a few integer variables.

## Dry-Run

Step-by-step on `matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]]`,
`target = 3` (expected `true`). Here `m = 3`, `n = 4`, so the flat range is
`[0, 11]` and the matrix in flat order is
`[1, 3, 5, 7, 10, 11, 16, 20, 23, 30, 34, 60]`.

| Iter | lo | hi | mid | (r,c)   | value | comparison | action        |
|-----:|---:|---:|----:|---------|------:|------------|---------------|
| 1    | 0  | 11 | 5   | (1, 1)  | 11    | 11 > 3     | hi = mid-1=4  |
| 2    | 0  | 4  | 2   | (0, 2)  | 5     | 5 > 3      | hi = mid-1=1  |
| 3    | 0  | 1  | 0   | (0, 0)  | 1     | 1 < 3      | lo = mid+1=1  |
| 4    | 1  | 1  | 1   | (0, 1)  | 3     | 3 == 3     | return **true** |

Verify the index mapping on iteration 1: `mid = 5`, `n = 4`, so `row = 5/4 = 1`
and `col = 5%4 = 1` -- that is `matrix[1][1] = 11`. Correct.

Dry-run for the miss case `target = 13` (expected `false`):

| Iter | lo | hi | mid | (r,c)   | value | comparison | action        |
|-----:|---:|---:|----:|---------|------:|------------|---------------|
| 1    | 0  | 11 | 5   | (1, 1)  | 11    | 11 < 13    | lo = mid+1=6  |
| 2    | 6  | 11 | 8   | (2, 0)  | 23    | 23 > 13    | hi = mid-1=7  |
| 3    | 6  | 7  | 6   | (1, 2)  | 16    | 16 > 13    | hi = mid-1=5  |
| exit | 6  | 5  | -   | -       | -     | lo > hi    | return **false** |

### Checkpoint B -- Trace and break it

**Q1 (apply).** `matrix = [[1,3,5],[7,9,11]]` (so `m = 2`, `n = 3`), `target = 11`. The flat list is `[1,3,5,7,9,11]`. What is returned, and at which step?
- a) `true`, on the third probe (flat index 5 maps to `matrix[1][2] = 11`)
- b) `false`, 11 is never checked
- c) It throws -- flat index 5 is out of bounds

<details><summary>Show answer</summary>

**(a)** -- probes read 5 (`< 11`, go right), 9 (`< 11`, go right), then `matrix[1][2] = 11 == 11`, return `true`.

</details>

**Q2 (analyze).** What happens if you swap the mapping to `matrix[mid % m][mid / m]`?
- a) You read the wrong cell (still in bounds), so answers look plausible but are silently incorrect
- b) It throws immediately on every input
- c) Nothing; the two formulas are equivalent

<details><summary>Show answer</summary>

**(a)** -- the indices stay within the matrix, so no crash, but the value read is from the wrong cell and the search logic is silently corrupted.

</details>

**Q3 (transfer).** Suppose rows stayed sorted but each row's first element was NOT greater than the previous row's last (rows overlap). Would the flatten trick still work? Why not?

<details><summary>Show answer</summary>

No. The flat list would no longer be globally sorted, so a single binary search over all cells would be invalid. You would have to binary-search each row independently.

</details>

## Common mistakes

- Running two *linear* scans (a row scan then a column scan). That is O(m + n),
  not logarithmic, and silently violates the `O(log(m*n))` requirement.
- Getting the index mapping backwards (`mid % m` / `mid / m`). The column count
  is the *divisor* because each row has `n` entries. Swap them and you read the
  wrong cell while staying in bounds, producing answers that look plausible but
  are wrong.
- Forgetting the empty-matrix guard when running locally. On LeetCode the input
  is never empty, but a local test with `new int[0][0]` will throw on
  `matrix[0].length`. Either guard it or restrict your tests to non-empty input.
- Using two separate binary searches (row-then-column) and forgetting that the
  row search must find the *last* row whose first element is `<= target`. The
  flatten-it approach sidesteps this entirely.
- Comparing the row's first element to decide "is target in this row" with
  equality. That misses targets that live mid-row. The flatten approach never
  needs that decision.

## Related problems

- [0704 - Binary Search](../0704-binary-search/) -- the identical loop on a
  genuine 1-D array; do this first.
- [0035 - Search Insert Position](../0035-search-insert-position/) -- the same
  flatten trick with an insert-position return is a common variant.
- [0033 - Search in Rotated Sorted Array](../0033-search-in-rotated-sorted-array/) --
  another "the array isn't a plain sorted array" twist on the same loop.
