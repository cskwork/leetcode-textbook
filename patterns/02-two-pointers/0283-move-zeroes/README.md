# 0283 - Move Zeroes

**Difficulty:** Easy
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/move-zeroes/

## Problem

Given an integer array `nums`, move all `0`'s to the end of it **in-place**
while maintaining the relative order of the non-zero elements. You must do this
without making a copy of the array, and the operation should run in O(n) time.

Signature:

    void moveZeroes(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [0,1,0,3,12]
    Output: [1,3,12,0,0]

    Input:  nums = [0]
    Output: [0]

## Intuition

This problem introduces the **second** Two-Pointer variant: fast/slow (also
called read/write). Both pointers start at the left, but they move at different
rates.

- A `read` pointer scans every element from left to right.
- A `write` pointer marks the slot where the next *kept* (non-zero) element
  should go.

Whenever `read` sees a non-zero, we copy it into the `write` slot and advance
`write`. Zeroes are simply skipped, leaving "holes" behind `write`. After the
scan, every position from `write` to the end of the array is stale — we fill
them with zeroes. The non-zeroes keep their relative order because both
pointers only ever move forward and `write` never overtakes `read`.

The trigger signal is the phrase "in-place" combined with "maintain order":
that is the signature of a fast/slow partition.

## Pseudocode

```text
function moveZeroes(nums):
    set write to first index
    for read from first index to last index:
        if nums[read] is not zero:
            copy nums[read] into nums[write]
            advance write
    # everything from write onward is now stale -> fill with zeroes
    while write <= last index:
        set nums[write] to zero
        advance write
```

## Java Solution

```java
class Solution {
    public void moveZeroes(int[] nums) {
        int write = 0;
        for (int read = 0; read < nums.length; read++) {
            if (nums[read] != 0) {
                nums[write] = nums[read];
                write++;
            }
        }
        while (write < nums.length) {
            nums[write] = 0;
            write++;
        }
    }
}
```

`write` only advances on a kept element, so after the loop `[0, write)` holds
every non-zero value in its original order. The trailing `while` then pads the
rest with zeroes. We write unconditionally with `nums[write] = nums[read]`
rather than swapping: overwriting is fine because every value is preserved
(whatever was at `write` has already been copied forward, or is a zero we do
not need). This keeps the code branch-free and obviously O(n). The space cost
is O(1) — we reuse the input array and use only two index variables.

## Complexity

    Time:  O(n)  -- the read loop visits each element once; the zero-fill loop visits at most the rest.
    Space: O(1)  -- in-place; no auxiliary array, just the `write` index.

## Dry-Run

Step-by-step on `nums = [0,1,0,3,12]`. We track `read`, `write`, and the array
after each read iteration.

| read | nums[read] | write | action | nums after |
|------|------------|-------|--------|------------|
| 0 | 0 | 0 | zero -> skip | [0,1,0,3,12] |
| 1 | 1 | 0 | copy nums[1]->nums[0]; write=1 | [1,1,0,3,12] |
| 2 | 0 | 1 | zero -> skip | [1,1,0,3,12] |
| 3 | 3 | 1 | copy nums[3]->nums[1]; write=2 | [1,3,0,3,12] |
| 4 | 12 | 2 | copy nums[4]->nums[2]; write=3 | [1,3,12,3,12] |
| (fill) | - | 3 | nums[3]=0; write=4 | [1,3,12,0,12] |
| (fill) | - | 4 | nums[4]=0; write=5 | [1,3,12,0,0] |

Final `nums = [1,3,12,0,0]`. The non-zero prefix `[1,3,12]` is in original
order, and the tail `[0,0]` holds exactly the two zeroes that were skipped.

## Common mistakes

- **Allocating a second array.** Tempting and correct, but it violates the
  in-place / O(1) space requirement that the problem explicitly states.
- **Swapping instead of overwriting, and swapping a non-zero with itself.**
  Harmless here, but adding an unnecessary `if (read != write)` guard is a
  premature optimisation that obscures the simple write-then-fill flow.
- **Filling zeroes from the wrong end.** Some beginners zero out from index 0,
  wiping out the non-zeroes they just collected. The fill always starts at
  `write`.
- **Stopping the read loop early.** The loop must reach the last index; a
  `read < write` condition would skip the tail.
- **Returning something.** The signature is `void`; the answer is left in the
  mutated input array.

## Related problems

- [0026 - Remove Duplicates from Sorted Array] - the same fast/slow shape, with
  a "keep if different from the last kept" condition.
- [0027 - Remove Element] - near-identical; remove a given value instead of 0.
- [0977 - Squares of a Sorted Array](../0977-squares-of-a-sorted-array/) - the
  opposite-ends variant, for contrast.
