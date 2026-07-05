# 0074 - Search a 2D Matrix

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/search-a-2d-matrix/

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

The two given properties are exactly what makes a 1-D array sorted: read the
matrix row by row and you get one long ascending sequence. So a 3x4 matrix

    [[ 1,  3,  5,  7],
     [10, 11, 16, 20],
     [23, 30, 34, 60]]

is, for all ordering purposes, the array

    [1, 3, 5, 7, 10, 11, 16, 20, 23, 30, 34, 60]

That means LC 704 solves this problem -- we just need a way to translate a
1-D index into the corresponding `(row, col)` so we can read the value. The
standard mapping is division and modulo by the row length:

- `row = index / n` (where `n` is the number of columns),
- `col = index % n`.

The trigger signals are "sorted rows", "first of each row > last of previous",
and `O(log(m*n))` -- all pointing at classic binary search (Template A) on a
flattened index range `[0, m*n - 1]`.

(There is also a two-search variant: binary-search the first column to find the
candidate row, then binary-search that row. It is correct and also logarithmic,
but the flatten-it approach is shorter, has only one loop to reason about, and
teaches the index-mapping trick that recurs in heap-backed 2-D structures.)

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
