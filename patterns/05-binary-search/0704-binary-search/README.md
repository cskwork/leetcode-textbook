# 0704 - Binary Search

**Difficulty:** Easy
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/binary-search/

## Problem

Given a **sorted** array of integers `nums` in ascending order and an integer
`target`, write a function that searches for `target` in `nums`. If `target`
exists, return its index. Otherwise, return `-1`.

You must write an algorithm with `O(log n)` runtime complexity.

Signature:

    int search(int[] nums, int target)

Examples (verbatim from LeetCode):

    Input:  nums = [-1,0,3,5,9,12], target = 9
    Output: 4

    Input:  nums = [-1,0,3,5,9,12], target = 2
    Output: -1

## Intuition

This is the *hello world* of binary search and the cleanest place to install
**Template A** from the pattern README. The trigger signals are unmistakable:
the array is explicitly **sorted**, the problem asks for **O(log n)** lookup,
and we want an **exact element**. All three point at classic binary search.

The mental picture is the phone-book trick: open the book in the middle. If the
name you want comes *after* the middle page, tear off and throw away the entire
left half; if it comes *before*, throw away the right half. Repeat on the
remaining half. Each step discards half the pages, so the work is logarithmic.

The invariant to hold in your head: *if the target is anywhere at all, it lies
in the closed range `[lo, hi]`.* Every branch of the loop preserves that
invariant by shrinking the range to the half that could still contain the
target.

## Pseudocode

    function search(nums, target):
        low  <- 0
        high <- length(nums) - 1

        while low <= high:
            mid <- low + (high - low) / 2       # overflow-safe midpoint
            if nums[mid] equals target:
                return mid                       # found it
            else if nums[mid] < target:
                low  <- mid + 1                  # target must be in the right half
            else:                                # nums[mid] > target
                high <- mid - 1                  # target must be in the left half

        return -1                                # range emptied, target absent

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
            } else if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return -1;
    }
}
```

This is Template A exactly: an inclusive `[lo, hi]` range, a `while (lo <= hi)`
loop, and both pointers moving by `±1`. The midpoint uses `lo + (hi - lo) / 2`
rather than `(lo + hi) / 2` so the sum of the bounds is never formed -- that
avoids the integer-overflow bug when `lo + hi` exceeds `Integer.MAX_VALUE`. Each
of the three branches shrinks the range by at least one element, so the loop is
guaranteed to terminate. The empty-array case needs no special handling: the
loop body never runs and we fall straight through to `return -1`.

## Complexity

    Time:  O(log n)  -- each iteration halves the search space.
    Space: O(1)      -- only two index variables and `mid`.

## Dry-Run

Step-by-step on `nums = [-1, 0, 3, 5, 9, 12]`, `target = 9` (expected `4`):

| Iter | lo | hi | mid | nums[mid] | comparison      | action       |
|-----:|---:|---:|----:|----------:|-----------------|--------------|
| 1    | 0  | 5  | 2   | 3         | 3 < 9           | lo = mid+1=3 |
| 2    | 3  | 5  | 4   | 9         | 9 == 9          | return **4** |

Two probes instead of a five-element scan. The range went from 6 elements to 3
to a hit.

Dry-run for the miss case `nums = [-1, 0, 3, 5, 9, 12]`, `target = 2`
(expected `-1`):

| Iter | lo | hi | mid | nums[mid] | comparison | action        |
|-----:|---:|---:|----:|----------:|------------|---------------|
| 1    | 0  | 5  | 2   | 3         | 3 > 2      | hi = mid-1=1  |
| 2    | 0  | 1  | 0   | -1        | -1 < 2     | lo = mid+1=1  |
| 3    | 1  | 1  | 1   | 0         | 0 < 2      | lo = mid+1=2  |
| exit | 2  | 1  | -   | -         | lo > hi    | return **-1** |

At the end `lo == 2`, which is exactly where `2` would go if it existed --
that observation is the seed of LC 35.

## Common mistakes

- Writing `mid = (lo + hi) / 2`. For the tiny inputs in tests it works, but the
  moment `lo + hi` overflows `int` it produces a negative index. Always use
  `lo + (hi - lo) / 2`.
- Using `while (lo < hi)` here. With the exclusive variant the loop ends one
  iteration early and you can miss the single-element range where `lo == hi`
  holds the answer. For an *exact* match, use `while (lo <= hi)`.
- Moving a pointer to `mid` instead of `mid ± 1`. If the target is not at
  `mid`, `mid` itself is ruled out, so both bounds must move *past* it --
  otherwise a one-element range loops forever.
- Returning `0` or `lo` instead of `-1` on a miss. LC 704 specifically wants
  `-1`; the insert-position convention is LC 35's job, not this one's.
- Forgetting that `nums` is sorted. The whole algorithm is wrong on an unsorted
  array -- the "throw away half" step is only valid because order guarantees
  which side the target must be on.

## Related problems

- [0035 - Search Insert Position](../0035-search-insert-position/) -- the same
  loop; the *return value on a miss* is the entire point.
- [0074 - Search a 2D Matrix](../0074-search-a-2d-matrix/) -- the same loop,
  stretched across two dimensions by flattening the matrix.
- [0033 - Search in Rotated Sorted Array](../0033-search-in-rotated-sorted-array/) --
  what happens when "sorted" is only *locally* true.
