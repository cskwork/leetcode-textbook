# 0033 - Search in Rotated Sorted Array

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/search-in-rotated-sorted-array/

## Concepts used

- **Binary search** -- narrowing a range by halves to find a target index. [glossary](../../../docs/10-glossary.md#binary-search)
- **Array** -- a row of numbered slots holding values, read instantly by position. [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- putting values in order so each value tells you about its neighbours. [glossary](../../../docs/10-glossary.md#sorting)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

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

A *rotated* sorted array is a sorted array with the last few elements moved to
the front, so it looks like two sorted chunks glued at one drop:
`[4, 5, 6, 7, 0, 1, 2]` is `[4,5,6,7]` followed by `[0,1,2]`. (LC 153 explains
rotation in full.) This problem asks the same thing as LC 704 -- find a target's
index -- but on a rotated array, so a plain middle-comparison no longer tells
you which half to keep.

The saving grace is the same as in LC 153: **at every step, at least one of the
two halves is normally sorted.** A sorted half is the only thing that lets us
reason about where the target is, so each step asks two questions, always in
this order:

1. *Which half is sorted?* Compare `nums[mid]` against `nums[lo]`.
2. *Is the target inside that sorted half?* If yes, search there; if no, it must
   be in the other half.

Trace `nums = [4, 5, 6, 7, 0, 1, 2]`, `target = 0` (answer index `4`):

1. `lo = 0, hi = 6, mid = 3`, `nums[mid] = 7`. Which half is sorted?
   `nums[lo] = 4 <= nums[mid] = 7`, so the *left* half `[4,5,6,7]` is sorted.
   Is `0` inside `[4, 7)`? No -- so `0` must be in the other half. Discard the
   left: `lo = 4`.
2. `lo = 4, hi = 6, mid = 5`, `nums[mid] = 1`. Which half is sorted?
   `nums[lo] = 0 <= nums[mid] = 1`, so the left half `[0, 1]` is sorted. Is `0`
   inside `[0, 1)`? Yes -- search there: `hi = 4`.
3. `lo = 4, hi = 4, mid = 4`, `nums[mid] = 0 == target` -- return `4`.

The **invariant** is LC 704's: *if the target is in the array, it lies in
`[lo, hi]`.* The only difference is that, because the array is rotated, we
cannot decide which half to discard just by comparing `nums[mid]` to the target
-- we must first identify the sorted half and test the target against *its*
known range. The classic beginner mistake is to compare `nums[mid]` to the
target *before* finding the sorted half; on a rotated array that comparison is
meaningless and sends you into the wrong half.

### Checkpoint A -- Find the sorted half first

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** After the exact-match check fails, what is the FIRST thing the loop decides each iteration?
- a) Which half is sorted, by testing `nums[lo] <= nums[mid]`
- b) Whether the target is bigger than `nums[mid]`
- c) Whether the array is rotated

<details><summary>Show answer</summary>

**(a)** -- on a rotated array a bare `nums[mid]` vs `target` comparison tells you nothing, so you first locate the one sorted half and reason against its known range.

</details>

**Q2 (comprehend).** On `nums = [4,5,6,7,0,1,2]`, `target = 0`, iteration 1 finds `nums[mid] = 7` and the left half `[4,5,6,7]` is sorted. Why is the left half then DISCARDED (`lo = mid + 1`)?
- a) The target 0 is not in the sorted left half's range `[4, 7)`, so it must be in the other half
- b) Because the left half has already been searched
- c) Because 7 is the largest value in the array

<details><summary>Show answer</summary>

**(a)** -- once a half is known sorted, you can test the target against its range; if it is outside, the target can only be in the other half.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [6, 7, 0, 1, 2, 3, 4]`, `target = 6`. What is returned?
- a) `0` (index of 6)
- b) `-1`, target not found
- c) `1`

<details><summary>Show answer</summary>

**(a)** -- iter 1: `mid=3 (1)`, right half sorted, `6` not in `(1,4]` -> `hi=2`; iter 2: `mid=1 (7)`, left half `[6,7]` sorted, `6` in `[6,7)` -> `hi=0`; iter 3: `nums[0]=6==6`, return `0`.

</details>

**Q2 (analyze).** Why is the test `nums[lo] <= nums[mid]` written with `<=`, not `<`? Think about a two-element range where `lo == mid`.
- a) On a two-element range `lo == mid`, so strict `<` would wrongly call the right half sorted and send the search the wrong way
- b) To avoid integer overflow
- c) It makes no difference; `<` would work identically

<details><summary>Show answer</summary>

**(a)** -- when `lo == mid` the left "half" is a single element and is sorted; the `<=` correctly classifies it instead of misrouting into the right branch.

</details>

**Q3 (transfer).** Why does this algorithm degrade toward O(n) on an array WITH duplicates? What comparison becomes uninformative?

<details><summary>Show answer</summary>

With duplicates, `nums[lo] <= nums[mid]` can be true even when the left half is not really sorted (equal values spanning the rotation), so the "which half is sorted" test becomes unreliable and you can no longer guarantee halving every step.

</details>

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
