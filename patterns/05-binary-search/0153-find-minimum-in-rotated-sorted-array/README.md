# 0153 - Find Minimum in Rotated Sorted Array

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/

## Problem

Given the array `nums` of **unique** elements sorted in ascending order and then
**rotated** between `1` and `n` times (a rotation = moving the last element to
the front), return the smallest element.

You must write an `O(log n)` solution.

Signature:

    int findMin(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [3,4,5,1,2]
    Output: 1

    Input:  nums = [4,5,6,7,0,1,2]
    Output: 0

    Input:  nums = [11,13,15,17]
    Output: 11

## Intuition

The trigger signals are "rotated sorted array" and "find the minimum in
O(log n)". The challenge is that a rotated sorted array is *not* globally sorted,
so a single midpoint comparison with the target does not tell you which half to
keep. But the array still has structure: **at every step, at least one of the
two halves is sorted**, and that sorted half is enough to decide where the
minimum lives.

The key invariant to keep in your head: compare `nums[mid]` against `nums[hi]`
(the *right* end of the current range). Two cases:

- `nums[mid] < nums[hi]`. Then the entire sub-range `nums[mid..hi]` is sorted
  ascending (a smaller left followed by a larger right). So the minimum of the
  *current* range is at or to the left of `mid` -- it might *be* `nums[mid]`.
  Pull the upper bound down: `hi = mid` (keep `mid` in the range).
- `nums[mid] > nums[hi]`. Then somewhere between `mid` and `hi` the array
  drops, so the minimum must be strictly to the right of `mid`. Push the lower
  bound past `mid`: `lo = mid + 1`.

Because the problem has no duplicates, the `nums[mid] == nums[hi]` case never
arises when `mid != hi`, so the two-way branch is exhaustive.

This is Template B in shape (`while (lo < hi)`, `hi = mid`), but its
"predicate" is the comparison between two array elements rather than an
external yes/no function. The loop ends with `lo == hi`, which is the index of
the minimum.

Why compare with `nums[hi]` instead of `nums[lo]`? The `nums[mid] vs nums[hi]`
test is robust against the un-rotated case: if the array was never rotated,
`nums[mid] < nums[hi]` holds forever and `hi` simply walks all the way down to
`lo = 0`, correctly identifying the first element. A `nums[mid] vs nums[lo]`
test would need extra bookkeeping for that case.

## Pseudocode

    function findMin(nums):
        low  <- 0
        high <- length(nums) - 1

        while low < high:
            mid <- low + (high - low) / 2
            if nums[mid] < nums[high]:
                # right half [mid..high] is sorted -> min is at or left of mid
                high <- mid
            else:                              # nums[mid] > nums[high], no ties
                # a drop lives between mid and high -> min is right of mid
                low <- mid + 1

        return nums[low]                       # low == high == index of the min

## Java Solution

```java
import java.util.*;

class Solution {
    public int findMin(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < nums[hi]) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return nums[lo];
    }
}
```

The structure is the reducing loop of Template B (`while (lo < hi)`, `hi = mid`),
which is natural here because we want a *boundary* -- the index where the array
drops -- not an exact match. The decisive comparison is `nums[mid] < nums[hi]`:
it tells us whether the right portion is sorted (min is left-or-at `mid`) or
contains the drop (min is right of `mid`). We move only one pointer by `+1`
(`lo = mid + 1`) and the other by `= mid` (`hi = mid`), so the loop always
shrinks and the floor midpoint guarantees termination. The problem guarantees
unique elements, so the strict-inequality branch is exhaustive; if duplicates
were allowed (LC 154), we would need a third branch for the
`nums[mid] == nums[hi]` tie. The loop returns `nums[lo]` rather than `nums[hi]`
for readability -- at exit `lo == hi`, so the two are identical.

## Complexity

    Time:  O(log n)  -- the range halves each iteration.
    Space: O(1)      -- three integer variables.

## Dry-Run

Step-by-step on `nums = [4, 5, 6, 7, 0, 1, 2]` (expected `0`):

| Iter | lo | hi | mid | nums[mid] | nums[hi] | comparison   | action        |
|-----:|---:|---:|----:|----------:|---------:|--------------|---------------|
| 1    | 0  | 6  | 3   | 7         | 2        | 7 > 2        | lo = mid+1=4  |
| 2    | 4  | 6  | 5   | 1         | 2        | 1 < 2        | hi = mid=5    |
| 3    | 4  | 5  | 4   | 0         | 1        | 0 < 1        | hi = mid=4    |
| exit | 4  | 4  | -   | -         | -        | lo == hi     | return nums[4]=**0** |

Observe how each comparison picks the half that *could* still hold the minimum.
In iteration 1, `nums[mid]=7 > nums[hi]=2` proves a drop exists to the right of
`mid`, so `lo` jumps past `mid`. From iteration 2 onward, the right portion is
sorted, so `hi` walks left until it pins the minimum at index 4.

Dry-run on the un-rotated case `nums = [11, 13, 15, 17]` (expected `11`):

| Iter | lo | hi | mid | nums[mid] | nums[hi] | comparison | action       |
|-----:|---:|---:|----:|----------:|---------:|------------|--------------|
| 1    | 0  | 3  | 1   | 13        | 17       | 13 < 17    | hi = mid=1   |
| 2    | 0  | 1  | 0   | 11        | 13       | 11 < 13    | hi = mid=0   |
| exit | 0  | 0  | -   | -         | -        | lo == hi   | return **11** |

No special case for "the array was never rotated": the `nums[mid] < nums[hi]`
condition is always true, so `hi` simply descends to `0` and the first element
is returned.

## Common mistakes

- Comparing `nums[mid]` against `nums[lo]` instead of `nums[hi]`. That works on
  some inputs but mishandles the un-rotated case (where `nums[lo]` is already
  the minimum) and needs extra `if` guards. The `nums[hi]` comparison handles
  every case uniformly.
- Using `while (lo <= hi)` with `hi = mid - 1`. This is the exact-match form
  and produces off-by-one results because the minimum can *be* at `mid`, so you
  must not exclude `mid` (`hi = mid`, not `mid - 1`).
- Returning `lo - 1` or `mid`. At exit `lo == hi` and that index holds the
  minimum; no adjustment is needed.
- Applying this code verbatim to arrays **with duplicates**. With duplicates the
  case `nums[mid] == nums[hi]` is reachable and ambiguous -- you can only
  safely do `hi = hi - 1` (LC 154). The unique-element guarantee is what lets
  us use the strict two-way branch here.
- Initialising `hi = nums.length` (exclusive) by habit. With this algorithm
  `hi` starts at `nums.length - 1` (inclusive) and `nums[hi]` is read on the
  very first iteration, so an exclusive bound throws `ArrayIndexOutOfBounds`.

## Related problems

- [0033 - Search in Rotated Sorted Array](../0033-search-in-rotated-sorted-array/) --
  the harder sibling: instead of just finding the minimum, find a specific
  target by first working out which half is sorted.
- [0035 - Search Insert Position](../0035-search-insert-position/) -- the same
  `while (lo < hi)` / `hi = mid` shape, applied to a plain sorted array.
- [0704 - Binary Search](../0704-binary-search/) -- the foundation loop; 153
  changes only the comparison inside it.
