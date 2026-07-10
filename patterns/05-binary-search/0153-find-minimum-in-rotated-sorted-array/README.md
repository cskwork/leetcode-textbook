# 0153 - Find Minimum in Rotated Sorted Array

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/

## Concepts used

- **Binary search** -- narrowing a range by halves; here we hunt the single "drop" in a rotated array. [glossary](../../../docs/10-glossary.md#binary-search)
- **Array** -- a row of numbered slots holding values, read instantly by position. [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- putting values in order so each value tells you about its neighbours. [glossary](../../../docs/10-glossary.md#sorting)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

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

First, what does "rotated" mean? Take a sorted array
`[0, 1, 2, 3, 4, 5, 6, 7]` and imagine it written around a clock face. A
*rotation* pops the last element off and sticks it on the front; repeating this
rotates the array. Rotate four times and you get `[4, 5, 6, 7, 0, 1, 2, 3]`. The
numbers are still "almost sorted": they are two sorted chunks (`[4,5,6,7]` then
`[0,1,2,3]`) glued at a single *drop* (the only spot where a bigger number is
followed by a smaller one). Given such an array with no duplicates, find its
smallest element in O(log n) time.

Trace `nums = [4, 5, 6, 7, 0, 1, 2]`, whose answer is `0`. The first sorted
chunk is `[4,5,6,7]`, the second is `[0,1,2]`, and the drop sits between `7` and
`0`; the minimum is the number just after the drop.

The key insight: **no matter where you slice a rotated array in half, at least
one of the two halves is normally sorted** (the drop is in the other half). That
sorted half is the clue, and we read it by comparing the middle value
`nums[mid]` against the *right* end `nums[hi]`:

1. `lo = 0, hi = 6`, `mid = 3`, `nums[mid] = 7`, `nums[hi] = 2`. Here `7 > 2` --
   a bigger number to the left of a smaller one means the drop (and the
   minimum) is in the right half. Discard the left: `lo = 4`.
2. `lo = 4, hi = 6`, `mid = 5`, `nums[mid] = 1`, `nums[hi] = 2`. Now `1 < 2` --
   the right half `[1, 2]` is sorted, so the minimum is at `mid` or to its
   left. Pull the top down (keeping `mid`): `hi = 5`.
3. `lo = 4, hi = 5`, `mid = 4`, `nums[mid] = 0`, `nums[hi] = 1`. `0 < 1` --
   right half sorted again, so `hi = 4`.
4. `lo = 4, hi = 4` -- they meet at index 4, whose value is `0`. That's the
   minimum.

The **invariant** is *the minimum is always inside `[lo, hi]`.* When the right
half is sorted (`nums[mid] < nums[hi]`), the minimum cannot be to the right of
`mid`, so we pull `hi` down to `mid` -- keeping `mid`, because `mid` might *be*
the minimum. When `nums[mid] > nums[hi]`, the drop is definitely between `mid`
and `hi`, so the minimum is strictly right of `mid` and we push `lo` past it.
The range shrinks every step, and when `lo == hi` that index holds the minimum.

### Checkpoint A -- Read the right end

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** This solution decides which half to keep by comparing `nums[mid]` against which other element?
- a) `nums[hi]` (the right end)
- b) `nums[lo]` (the left end)
- c) `target`

<details><summary>Show answer</summary>

**(a)** -- comparing `nums[mid]` to `nums[hi]` tells whether the right portion is sorted (min is at or left of `mid`) or contains the drop (min is right of `mid`).

</details>

**Q2 (comprehend).** On `nums = [4,5,6,7,0,1,2]`, the first probe has `nums[mid] = 7` and `nums[hi] = 2`. Since `7 > 2`, what does that prove, and which half is discarded?
- a) A "drop" (big followed by small) exists to the right of `mid`, so the minimum is right of `mid`; discard the left half (`lo = mid + 1`)
- b) The array is fully sorted, so the minimum is at `lo`
- c) The minimum is exactly at `mid`

<details><summary>Show answer</summary>

**(a)** -- a value larger than the right end can only exist left of the drop, so the drop (and the min just after it) must be to the right.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [5, 1, 2, 3, 4]` (expected min `1`). What is the final returned value?
- a) `1`
- b) `5`
- c) `2`

<details><summary>Show answer</summary>

**(a)** -- `mid=2 (2 < 4)` -> `hi=2`; `mid=1 (1 < 2)` -> `hi=1`; `mid=0 (5 > 1)` -> `lo=1`; loop ends, returns `nums[1] = 1`.

</details>

**Q2 (analyze).** This code uses `while (lo < hi)` with `hi = mid`. Why would switching to `while (lo <= hi)` with `hi = mid - 1` give a wrong answer?
- a) `mid` might BE the minimum, so excluding it with `mid - 1` can discard the answer
- b) It would cause integer overflow
- c) It would still be correct, just one iteration slower

<details><summary>Show answer</summary>

**(a)** -- the whole point of `hi = mid` (not `mid - 1`) is to keep `mid` in the range because it could be the minimum; `mid - 1` throws that candidate away.

</details>

**Q3 (transfer).** Why does this exact code fail on a rotated array WITH duplicates? Which case becomes ambiguous?

<details><summary>Show answer</summary>

With duplicates, `nums[mid] == nums[hi]` is reachable and ambiguous -- you cannot tell from a tie which half holds the drop. The strict two-way branch has no home for the tie; it needs a third, cautious move (`hi = hi - 1`), which is LC 154.

</details>

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
