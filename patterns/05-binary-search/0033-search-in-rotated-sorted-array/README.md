# 0033 - Search in Rotated Sorted Array

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/search-in-rotated-sorted-array/

## Problem

Given the array `nums` of **distinct** integers sorted in ascending order and
then rotated an unknown number of times, and an integer `target`, return the
index of `target` if it is in the array, or `-1` if it is not.

You must write an `O(log n)` solution.

Signature:

    int search(int[] nums, int target)

Examples (verbatim from LeetCode):

    Input:  nums = [4,5,6,7,0,1,2], target = 0
    Output: 4

    Input:  nums = [4,5,6,7,0,1,2], target = 3
    Output: -1

    Input:  nums = [1], target = 0
    Output: -1

## Intuition

This is LC 153 with an extra twist: instead of just *finding the minimum*, we
*find a target*. The trigger signals are "rotated sorted array" and "O(log n)".
The challenge is identical to 153 -- a rotated array is not globally sorted, so
a naive midpoint comparison does not tell you which half to keep. But the same
invariant saves us: **at every step, at least one of the two halves is sorted**,
and a sorted half is the only thing we need to decide where the target lives.

The recipe at each step is therefore:

1. Compute `mid`. If `nums[mid] == target`, done.
2. Otherwise, figure out **which half is sorted** by comparing `nums[mid]`
   against `nums[lo]`.
   - If `nums[lo] <= nums[mid]`, the **left** half `[lo..mid]` is sorted. With a
     sorted half in hand, we can test membership with two comparisons: if
     `nums[lo] <= target < nums[mid]`, the target *must* be in that left half,
     so search there; otherwise the target *cannot* be in the left half, so
     search the right half.
   - Else the **right** half `[mid..hi]` is sorted, and we use the symmetric
     test: if `nums[mid] < target <= nums[hi]`, search the right half;
     otherwise search the left half.

The crucial habit -- and the classic beginner mistake -- is to **first** decide
which half is sorted, and *only then* ask where the target is. Comparing
`nums[mid]` against `target` before you know which half is sorted sends you into
the wrong half on rotated inputs.

This is Template A (`while (lo <= hi)`, both pointers move by `±1`), because we
want an *exact* index and we return from inside the loop on a hit.

## Pseudocode

    function search(nums, target):
        low  <- 0
        high <- length(nums) - 1

        while low <= high:
            mid <- low + (high - low) / 2
            if nums[mid] equals target:
                return mid

            if nums[low] <= nums[mid]:                  # LEFT half is sorted
                if nums[low] <= target < nums[mid]:
                    high <- mid - 1                     # target in sorted left half
                else:
                    low  <- mid + 1                     # target in the other half
            else:                                       # RIGHT half is sorted
                if nums[mid] < target <= nums[high]:
                    low  <- mid + 1                     # target in sorted right half
                else:
                    high <- mid - 1                     # target in the other half

        return -1

## Java Solution

```java
import java.util.*;

class Solution {
    public int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            if (nums[lo] <= nums[mid]) {
                if (nums[lo] <= target && target < nums[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (nums[mid] < target && target <= nums[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }
}
```

This is Template A in shape (`while (lo <= hi)`, `±1` moves), but the body does
the two-step "which half is sorted, then where is the target" reasoning. The
test `nums[lo] <= nums[mid]` (with `<=`, not `<`) handles the degenerate range
where `lo == mid`, which happens on two-element ranges -- the equality ensures
we still classify a one-element left half as "sorted". The membership bounds
deliberately exclude `nums[mid]` itself (`target < nums[mid]`, not `<=`),
because the `nums[mid] == target` case already returned at the top of the loop.
Distinct values guarantee `nums[lo] <= nums[mid]` cleanly partitions the cases;
with duplicates (LC 81) the boundary becomes ambiguous and the worst case
degrades to O(n). The midpoint uses the overflow-safe form. As usual, the empty
case never reaches the loop and we fall through to `return -1`.

## Complexity

    Time:  O(log n)  -- the range halves each iteration.
    Space: O(1)      -- three integer variables.

## Dry-Run

Step-by-step on `nums = [4, 5, 6, 7, 0, 1, 2]`, `target = 0` (expected `4`).
"target in [a, b)?" reads as `a <= target && target < b`:

| Iter | lo | hi | mid | nums[mid] | which half sorted? | target in sorted half? | action        |
|-----:|---:|---:|----:|----------:|--------------------|------------------------|---------------|
| 1    | 0  | 6  | 3   | 7         | left [4,5,6,7]     | 0 in [4,7)? no         | lo = mid+1=4  |
| 2    | 4  | 6  | 5   | 1         | left [0,1]         | 0 in [0,1)? yes        | hi = mid-1=4  |
| 3    | 4  | 4  | 4   | 0         | -                  | nums[mid]==0           | return **4**  |

Walk the reasoning. Iteration 1: `nums[lo]=4 <= nums[mid]=7`, so the left half
`[4,5,6,7]` is sorted. The target `0` is not in the sorted half's range
`[4, 7)`, so it cannot be there -- discard the left half, set `lo = 4`.
Iteration 2: now `nums[lo]=0 <= nums[mid]=1`, so the left half `[0, 1]` is
sorted (this is just indices 4..5). The target `0` *is* in `[0, 1)`, so it must
be at or left of `mid` -- set `hi = 4`. Iteration 3: `lo == hi == 4`, `mid` is
also 4, `nums[mid] == 0 == target`, return.

Dry-run for a miss, `nums = [4, 5, 6, 7, 0, 1, 2]`, `target = 3`
(expected `-1`):

| Iter | lo | hi | mid | nums[mid] | sorted half    | target in sorted half? | action        |
|-----:|---:|---:|----:|----------:|----------------|------------------------|---------------|
| 1    | 0  | 6  | 3   | 7         | left [4,5,6,7] | 3 in [4,7)? no         | lo = mid+1=4  |
| 2    | 4  | 6  | 5   | 1         | left [0,1]     | 3 in [0,1)? no         | lo = mid+1=6  |
| 3    | 6  | 6  | 6   | 2         | left [2]       | 3 in [2,2)? no         | lo = mid+1=7  |
| exit | 7  | 6  | -   | -         | -              | lo > hi                | return **-1** |

Each iteration the target is found *not* in the sorted half, so we go to the
other half until the range empties.

## Common mistakes

- Comparing `nums[mid]` against `target` *before* deciding which half is
  sorted. On a rotated array that comparison is meaningless -- the target could
  be on either side of `mid`. Always classify the half first.
- Using `nums[lo] < nums[mid]` (strict). On a two-element range where `lo == mid`,
  this wrongly concludes "the right half is sorted" and sends the search down
  the wrong path. Use `<=`.
- Including `nums[mid]` in the membership test (`target <= nums[mid]`). Since
  the `== target` case already returned, including `mid` in the bound is
  harmless on a hit but muddies the reasoning; keep `target < nums[mid]`.
- Applying this code to arrays **with duplicates**. Duplicates break the clean
  `nums[lo] <= nums[mid]` classification -- the worst case becomes O(n)
  (LC 81). The distinct-values guarantee is what keeps this O(log n).
- Returning `mid` from the membership branch instead of `-1` on a miss. The
  only place we return an index is the exact-match line; everything else either
  narrows the range or, at the end, returns `-1`.

## Related problems

- [0153 - Find Minimum in Rotated Sorted Array](../0153-find-minimum-in-rotated-sorted-array/) --
  the easier sibling; do it first to internalise the "which half is sorted"
  invariant without the target logic.
- [0035 - Search Insert Position](../0035-search-insert-position/) -- the same
  Template-A loop on a *plain* sorted array, for contrast.
- [0704 - Binary Search](../0704-binary-search/) -- the foundation; 33 changes
  only the loop body, not the loop shape.
