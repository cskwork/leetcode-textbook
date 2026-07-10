# 0035 - Search Insert Position

**Difficulty:** Easy
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/search-insert-position/

## Concepts used

- **Binary search** -- finding a target (or its slot) in sorted data by repeatedly halving the search space. [glossary](../../../docs/10-glossary.md#binary-search)
- **Array** -- a row of numbered slots holding values, read instantly by position. [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- putting values in order so each value tells you about its neighbours. [glossary](../../../docs/10-glossary.md#sorting)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

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

This is LC 704 in a different hat. There you returned `-1` when the target was
missing; here you return *the index where the target would slide in to keep the
array sorted*. The dictionary analogy still fits: if you reach for a word and
find it isn't printed, you still know exactly which gap it belongs in -- between
the page before it and the page after.

Trace `nums = [1, 3, 5, 6]`, `target = 2`. We keep the same two markers `lo,
hi` as in LC 704 and halve the range:

1. `lo = 0, hi = 3`, middle index 1, value `3`. Since `3 > 2`, the target (if
   present) is left of here: `hi = 0`.
2. `lo = 0, hi = 0`, middle index 0, value `1`. Since `1 < 2`, the target is
   right of here: `lo = 1`.
3. Now `lo = 1, hi = 0` -- the range is empty, so `2` isn't in the array.

The loop ends with `lo = 1`, and `1` is exactly where `2` belongs: everything
before index 1 (`[1]`) is smaller than 2, and everything from index 1 on
(`[3, 5, 6]`) is bigger. Slide `2` in at index 1 and the array stays sorted.

This is no accident. The LC 704 **invariant** -- *if the target exists, it lies
in `[lo, hi]`* -- has a second payoff when the range empties: at exit, `lo` is
the index of the smallest element that is `>= target` (and `hi` is the largest
element that is `< target`). The first of those is exactly the insert position.
So we reuse the LC 704 loop verbatim and change a single line: `return lo`
instead of `return -1`.

### Checkpoint A -- The return value on a miss

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** When the target is NOT in the array, what does LC 35 return (unlike LC 704's `-1`)?
- a) The index `lo` where the target would slide in to keep the array sorted
- b) Always `0`
- c) `-1`, same as LC 704

<details><summary>Show answer</summary>

**(a)** -- on a miss the loop ends with `lo` pointing at the first index whose value is `>= target`, which is exactly the insert slot.

</details>

**Q2 (comprehend).** On `nums = [1,3,5,6]`, `target = 2`, the loop ends with `lo = 1, hi = 0`. Why is `1` the correct insert position?
- a) Every element before index 1 is `< 2`, and every element from index 1 on is `> 2`
- b) Because `lo + hi = 1`
- c) Because index 1 is the middle of the array

<details><summary>Show answer</summary>

**(a)** -- sliding `2` into index 1 keeps `[1, 2, 3, 5, 6]` sorted; that is precisely what `lo` records at exit.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [1, 3, 5, 6]`, `target = 7`. What is returned?
- a) `4` -- `lo` walks past the right end to `nums.length`
- b) `3` -- the last valid index
- c) `-1`

<details><summary>Show answer</summary>

**(a)** -- the loop keeps moving `lo` right (3 < 7, 5 < 7, 6 < 7) until `lo = 4, hi = 3` and exits; `4` is the correct append position.

</details>

**Q2 (analyze).** The loop body is byte-for-byte the LC 704 loop. What single line differs, and why must it be that exact value?
- a) The post-loop `return`: it is `lo`, not `-1` or `lo + 1`, because the invariant pins `lo` to the first index whose value is `>= target`
- b) The midpoint formula
- c) The `while` condition

<details><summary>Show answer</summary>

**(a)** -- every other candidate (`-1`, `hi`, `lo + 1`) is an off-by-one. The invariant guarantees `lo` is exactly right with no adjustment.

</details>

**Q3 (transfer).** If the array had duplicates, "return the index of target" becomes ambiguous. What does "first index `>= target`" still give you unambiguously?

<details><summary>Show answer</summary>

The lower bound -- the smallest index whose value is `>= target`. That is well-defined even with duplicates; what becomes ambiguous is which occurrence of an exact match counts as "the" index.

</details>

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
