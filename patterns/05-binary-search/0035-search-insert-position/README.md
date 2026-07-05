# 0035 - Search Insert Position

**Difficulty:** Easy
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/search-insert-position/

## Problem

Given a **sorted** array of distinct integers `nums` and an integer `target`,
return the index where `target` should be inserted to keep the array sorted. If
`target` is already present, return its index (you may assume no duplicates).

You must write an algorithm with `O(log n)` runtime complexity.

Signature:

    int searchInsert(int[] nums, int target)

Examples (verbatim from LeetCode):

    Input:  nums = [1,3,5,6], target = 5
    Output: 2

    Input:  nums = [1,3,5,6], target = 2
    Output: 1

    Input:  nums = [1,3,5,6], target = 7
    Output: 4

## Intuition

This problem is LC 704 in disguise, with one twist: when the target is *absent*,
704 returns a sentinel `-1`, but here we must return *where it would go*. The
wonderful fact about Template A is that those two answers fall out of the **same
loop** -- the only difference is the return value after the loop.

Recall the invariant of the classic search: *if the target exists, it lies in
`[lo, hi]`*. Every iteration preserves this by moving `lo` up past elements that
are definitely too small, and `hi` down past elements that are definitely too
large. When the loop ends with `lo > hi`, two things are simultaneously true:

- `hi` is the index of the largest element strictly less than `target`.
- `lo` is the index of the smallest element greater than or equal to `target`.

The second statement is exactly the **insert position**. So we keep the LC 704
loop verbatim and change only one line: replace `return -1` with `return lo`.

The trigger signals are the same as 704 -- sorted array, O(log n) lookup -- plus
the phrase "where would it be inserted", which is the textbook signature of a
**lower bound** search.

## Pseudocode

    function searchInsert(nums, target):
        low  <- 0
        high <- length(nums) - 1

        while low <= high:
            mid <- low + (high - low) / 2
            if nums[mid] equals target:
                return mid                       # exact hit, same as 704
            else if nums[mid] < target:
                low  <- mid + 1
            else:
                high <- mid - 1

        return low                               # the insert position

## Java Solution

```java
import java.util.*;

class Solution {
    public int searchInsert(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }
}
```

The body is byte-for-byte the LC 704 solution; only the post-loop return
changes. We return `lo` rather than `-1`, `hi`, or `lo + 1`, because the
invariant guarantees `lo` is the first index whose value is `>= target`. The
empty array is handled implicitly: the loop never runs, `lo` stays `0`, which is
correctly the insert position for an empty array. No special cases are needed
for "target smaller than everything" or "larger than everything" -- the loop
naturally pushes `lo` to `0` in the former case and to `nums.length` in the
latter.

## Complexity

    Time:  O(log n)  -- the range halves each iteration.
    Space: O(1)      -- only three integer variables.

## Dry-Run

Step-by-step on `nums = [1, 3, 5, 6]`, `target = 2` (expected `1` -- insert at
index 1, shifting `3, 5, 6` right):

| Iter | lo | hi | mid | nums[mid] | comparison | action        |
|-----:|---:|---:|----:|----------:|------------|---------------|
| 1    | 0  | 3  | 1   | 3         | 3 > 2      | hi = mid-1=0  |
| 2    | 0  | 0  | 0   | 1         | 1 < 2      | lo = mid+1=1  |
| exit | 1  | 0  | -   | -         | lo > hi    | return **1**  |

At the end `lo = 1` and `hi = 0`. Index 1 is exactly where `2` belongs: every
element before it (`[1]`) is `< 2`, and every element from it on (`[3,5,6]`) is
`> 2`.

Dry-run for the "past the end" case `nums = [1, 3, 5, 6]`, `target = 7`
(expected `4`):

| Iter | lo | hi | mid | nums[mid] | comparison | action        |
|-----:|---:|---:|----:|----------:|------------|---------------|
| 1    | 0  | 3  | 1   | 3         | 3 < 7      | lo = mid+1=2  |
| 2    | 2  | 3  | 2   | 5         | 5 < 7      | lo = mid+1=3  |
| 3    | 3  | 3  | 3   | 6         | 6 < 7      | lo = mid+1=4  |
| exit | 4  | 3  | -   | -         | lo > hi    | return **4**  |

`lo` walks off the right end to `nums.length`, which is the correct append
position.

## Common mistakes

- Returning `-1`, `hi`, or `lo + 1` on a miss. The invariant pins the answer to
  exactly `lo`. Any other value is an off-by-one.
- Switching to a `while (lo < hi)` variant out of caution. It works too, but
  then you must reason about a different invariant and a different `mid`
  convention. Sticking with Template A (`lo <= hi`) keeps this problem identical
  to LC 704.
- Forgetting the empty-array case. It happens to work for free here because
  `lo` never moves from `0`, but a manual `nums[0]` access before the loop
  would throw `ArrayIndexOutOfBoundsException`.
- Assuming duplicates behave the same. The problem promises distinct integers;
  with duplicates the "first index `>= target`" still holds, but "return the
  index of target" becomes ambiguous. (LC 34 explores that case.)
- Treating the two edge cases (`target < nums[0]`, `target > nums[n-1]`) as
  special. They are not special -- the loop already covers them. Adding `if`
  guards just clutters the code and risks the wrong constant.

## Related problems

- [0704 - Binary Search](../0704-binary-search/) -- the identical loop with a
  `-1` miss return; do this first.
- [0278 - First Bad Version](../0278-first-bad-version/) -- the boundary
  variant: instead of "where would it go", "where does the property first flip".
- [0074 - Search a 2D Matrix](../0074-search-a-2d-matrix/) -- the same
  lower-bound reasoning, but in a flattened 2-D index space.
