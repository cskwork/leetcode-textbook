# 0704 - Binary Search

**Difficulty:** Easy
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/binary-search/

## Concepts used

- **Array** -- a row of numbered slots holding values, read instantly by position. [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- putting values in order, so each value tells you about its neighbours. [glossary](../../../docs/10-glossary.md#sorting)
- **Binary search** -- finding a target in sorted data by repeatedly halving the search space. [glossary](../../../docs/10-glossary.md#binary-search)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

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

Imagine looking up a word in a paper dictionary. You don't start on page 1
and read every page -- you open the book roughly in the middle. If the word you
want comes *after* the page you opened, you tear off the left half and throw it
away; if it comes *before*, you throw away the right half. You repeat on the
half that's left. Each step throws out half the pages, so a 1000-page dictionary
takes only about 10 openings to search.

This only works because the dictionary is **sorted** (in order): on any page
you know that everything to the left is smaller and everything to the right is
bigger. Binary search is the same trick on a sorted **array** (a row of
numbered slots). Let's trace `nums = [-1, 0, 3, 5, 9, 12]`, `target = 9`. We
keep two markers, `lo` and `hi`, on the ends of the range that could still hold
the target -- initially the whole array, `lo = 0, hi = 5`.

1. Middle slot is index 2, value `3`. Is `3` our `9`? No, `3 < 9`, so `9` must
   be to the right. Move `lo` up to 3 and throw away the left.
2. New range is `[5, 9, 12]`. Middle is index 4, value `9` -- that's our
   target. Return `4`.

Two checks found an element a left-to-right scan would take five checks to
reach.

The whole algorithm rests on one **invariant** -- a condition that is *always
true at the start of every loop iteration*. Here the invariant is: *if the
target is anywhere in the array, it lies in the closed range `[lo, hi]`.* Each
branch of the loop preserves it. When `nums[mid]` is too small, the target
cannot be at or left of `mid`, so `lo = mid + 1` keeps the target inside the new
range; the too-large case is the mirror image. The range shrinks every step and
the target never escapes it, so the loop either finds the target or empties the
range -- in which case the target is absent and we return `-1`.

### Checkpoint A -- Why halving works

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Binary search only works on one kind of input. Which?
- a) A sorted array
- b) An array of distinct even numbers
- c) An array stored in a hash set

<details><summary>Show answer</summary>

**(a)** -- halving is valid because order tells you which half the target must be in. On unsorted input that decision is just a guess.

</details>

**Q2 (comprehend).** On `nums = [-1,0,3,5,9,12]`, `target = 9`, the first probe reads `nums[2] = 3`. Why does the loop then move `lo` up into the right half?
- a) Because `3 < 9`, and in a sorted array the target 9 cannot lie at or left of index 2
- b) Because index 2 is the middle and we always go right first
- c) Because 9 is larger than the array length

<details><summary>Show answer</summary>

**(a)** -- sorting means everything at or left of a too-small middle value is also smaller than the target, so that whole half is safely discarded.

</details>

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

### Checkpoint B -- Trace and break it

**Q1 (apply).** Trace `nums = [2, 4, 6, 8]`, `target = 6`. What is returned, and at which step?
- a) `2`, on the second probe (mid index 2 holds 6)
- b) `1`, on the first probe
- c) `-1`, the target is never found

<details><summary>Show answer</summary>

**(a)** -- probe 1: `lo=0,hi=3,mid=1,nums[1]=4<6`, so `lo=2`; probe 2: `lo=2,hi=3,mid=2,nums[2]=6==6`, return `2`.

</details>

**Q2 (analyze).** What goes wrong if you write `mid = (lo + hi) / 2` and `lo + hi` exceeds `Integer.MAX_VALUE`?
- a) The sum wraps to a negative number, so `mid` goes negative and `nums[mid]` throws
- b) The search returns `-1` immediately and safely
- c) Nothing; Java rounds the overflow back into range

<details><summary>Show answer</summary>

**(a)** -- overflow produces a negative index. That is exactly why the solution uses `lo + (hi - lo) / 2`, which never adds the two bounds.

</details>

**Q3 (transfer).** Suppose the array is NOT sorted. Would this algorithm still reliably find the target? Why or why not?

<details><summary>Show answer</summary>

No. The "discard the left half when `nums[mid] < target`" step is only valid because order guarantees the target would be to the right. Without sorting the target could be in the discarded half, so the result is unreliable.

</details>

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
