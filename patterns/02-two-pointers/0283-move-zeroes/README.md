# 0283 - Move Zeroes

**Difficulty:** Easy
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/move-zeroes/

## Concepts used

- **Two pointers** -- placing two indices into an array and moving them based on a rule; here both start at the left and advance at different rates (a "fast/slow" or "read/write" pair). [glossary](../../../docs/10-glossary.md#two-pointers)
- **Array** -- a row of numbered slots holding values, each read or written in O(1) time by its index. [glossary](../../../docs/10-glossary.md#array)
- **In-place** -- modifying the input array directly using only O(1) extra memory, instead of building a copy. [glossary](../../../docs/10-glossary.md#in-place)

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

Picture sorting a hand of cards: you want all the real cards pushed to the front
in their original order, and every empty slot pushed to the back. You look at the
cards one by one from left to right; each time you spot a real card, you place it
into the next free front slot; blanks you simply skip past. When you finish
scanning, any slot you never filled is a leftover blank, so you stamp it as empty.
That mental model -- a reading finger and a slower writing finger -- is the
second flavor of [two pointers](../../../docs/10-glossary.md#two-pointers), called
**fast/slow** (or read/write). Both start at the left, but they advance at
different rates.

Let's watch it on the tiniest case, `nums = [0, 1, 2]`:

- `read=0` sees `0` -- a blank, skip it. `write` stays at 0.
- `read=1` sees `1` -- a real value. Copy it into slot `write=0`, then bump
  `write` to 1. Array is now `[1, 1, 2]`.
- `read=2` sees `2` -- real. Copy into slot `write=1`, bump `write` to 2. Array
  is `[1, 2, 2]`.
- Scan done. Slots from `write=2` onward were never filled -- stamp them with
  `0`. Array becomes `[1, 2, 0]`.

The general rule falls straight out of that walk: one loop copies every non-zero
into the next `write` slot; a second short loop stamps zeros into everything
`write` did not reach. Why does the relative order of the non-zeros survive?
Because `write` only ever moves forward and never overtakes `read` -- each kept
value lands in a slot at or before its original position, so it can never leapfrog
a value that came after it.

This is an [in-place](../../../docs/10-glossary.md#in-place) solution: we reuse
the input array and keep only two index variables, so the extra memory is O(1).
(The other two-pointer flavor -- one finger at each end, moving inward -- is what
[Valid Palindrome](../0125-valid-palindrome/) and
[Squares of a Sorted Array](../0977-squares-of-a-sorted-array/) use; both flavors
share the same core idea of "two indices, moved by a rule, no nested loop".)

### Checkpoint A -- Read fast, write slow

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** In the fast/slow pair, when does the `write` pointer advance?
- a) Only when `read` sees a non-zero value
- b) Every iteration, right alongside `read`
- c) Only when `read` sees a zero

<details><summary>Show answer</summary>

**(a)** -- `write` is the "next free front slot" and only moves forward when a real value is copied into it. Zeros are skipped, so `write` waits while `read` passes over them.

</details>

**Q2 (comprehend).** Why does the relative order of the non-zero elements survive the move?
- a) Because `write` only ever moves forward and never overtakes `read`, so each kept value lands at or before its original position, in order
- b) Because the array is sorted beforehand
- c) Because zeros are gathered into a separate list and reattached

<details><summary>Show answer</summary>

**(a)** -- `read` sweeps left to right and `write` trails it, so values are copied in the same sequence they appear. No kept value can leapfrog a later one.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [2,0,4,0,5]`. What is the array after the algorithm runs?
- a) `[2,4,5,0,0]`
- b) `[2,4,5,5,0]`
- c) `[0,0,2,4,5]`

<details><summary>Show answer</summary>

**(a)** -- non-zeros 2,4,5 are copied into slots 0,1,2 (`write` ends at 3), then the tail from index 3 onward is filled with 0. Option (b) forgets to zero the last cell; (c) moves zeros to the front instead of the back.

</details>

**Q2 (analyze).** What goes wrong if the final zero-fill loop starts at index 0 instead of at `write`?
- a) It overwrites the non-zero values you just collected at the front, wiping them out
- b) Nothing -- it fills exactly the same cells
- c) It fills too few zeros

<details><summary>Show answer</summary>

**(a)** -- the front `[0 .. write)` holds the kept non-zeros; the stale cells are only those from `write` onward. Zeroing from 0 destroys the collected prefix.

</details>

**Q3 (transfer).** How would you adapt this to "remove all copies of a value k in-place and return the new length" (LC 27)?

<details><summary>Show answer</summary>

Same fast/slow skeleton; only the keep-condition changes, from "!= 0" to "!= k", and you drop the trailing zero-fill entirely. The returned length is `write` -- everything past it is ignored. The two-pointer shape is unchanged.

</details>

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
