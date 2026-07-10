# 0977 - Squares of a Sorted Array

**Difficulty:** Easy
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/squares-of-a-sorted-array/

## Concepts used

- **Two pointers** -- placing two indices into an array and moving them based on a comparison; here one at each end, because the biggest squares live at the two ends. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Sorting** -- putting elements in non-decreasing order; the input is sorted and the output must be too, which drives the fill-from-the-back trick. [glossary](../../../docs/10-glossary.md#sorting)
- **Array** -- a row of numbered slots holding values, each read or written in O(1) time by its index. [glossary](../../../docs/10-glossary.md#array)

## Problem

Given an integer array `nums` sorted in **non-decreasing order**, return an
array of the squares of each element, also sorted in non-decreasing order. The
input may contain negative numbers, and the result must be produced in O(n)
time.

Signature:

    int[] sortedSquares(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [-4,-1,0,3,10]
    Output: [0,1,9,16,100]

    Input:  nums = [-7,-3,2,3,11]
    Output: [4,9,9,49,121]

## Intuition

Picture feeding a sorted list through a "squaring machine": every number becomes
its square. That sounds like it should stay sorted -- but negatives flip to
positive. The list `[-4, -1, 0, 3, 10]` becomes `[16, 1, 0, 9, 100]`, which is
*not* sorted, because a big negative like `-4` turned into a big positive `16`.
So squaring each element and leaving it in place does not work, and squaring then
re-sorting works but costs O(n log n) when we are asked for O(n).

The key shape: after squaring, the biggest values sit at the two *ends* and the
smallest (zero) sits in the middle -- like a valley. That is exactly the shape
that [two pointers](../../../docs/10-glossary.md#two-pointers) at opposite ends
handles. Put one finger on the left end and one on the right; at each step
compare the two squares, take the larger, and move that finger inward.

Let's watch the tiniest case, `nums = [-2, 0, 1]`:

- `left=0` (`-2`, square `4`), `right=2` (`1`, square `1`): `4` is bigger, so `4`
  is the biggest overall. Write it into the last slot of the result, move `left`
  inward.
- `left=1` (`0`, square `0`), `right=2` (`1`, square `1`): `1` is bigger, write
  `1` into the second-to-last slot, move `right` inward.
- `left=1`, `right=1` (both on `0`, square `0`): the fingers have met, write `0`
  into the first slot. Result: `[0, 1, 4]`.

Why write into the result from the *back*? Because we always pull the larger
square first, so the natural output order is biggest-to-smallest (descending).
Filling the result array from its last index backward flips that into
smallest-to-biggest -- exactly the [sorted](../../../docs/10-glossary.md#sorting)
ascending order we need -- in a single pass. We use `left <= right` (not `<`) so
that when the two fingers meet on one middle element, its square still gets
written rather than dropped.

### Checkpoint A -- Fill from the back

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why is the result array filled from the LAST index toward the first?
- a) Because we always pull the larger square first, so filling from the back turns the natural biggest-to-smallest order into ascending (sorted) order
- b) Because Java requires arrays to be filled backwards
- c) Because the input is sorted ascending

<details><summary>Show answer</summary>

**(a)** -- the larger square is written into `result[pos]` with `pos` starting at `n-1` and moving down, so what would be a descending write becomes an ascending result in a single pass.

</details>

**Q2 (comprehend).** Why does the loop use `left <= right` instead of `left < right`?
- a) When the two pointers meet on a single middle element, its square still needs a home in the result -- `<` would skip it and leave a gap
- b) To make the loop run one extra time for safety
- c) Because arrays are zero-indexed

<details><summary>Show answer</summary>

**(a)** -- for an odd-length array the two pointers collide on one real middle value. The inclusive `<=` ensures its square is written; the dry-run shows this happening to the middle `0` at step 5.

</details>

## Pseudocode

```text
function sortedSquares(nums):
    n = length of nums
    result = new array of length n
    set left to first index
    set right to last index
    set position to last index of result       # fill from the back
    while left <= right:
        leftSquare  = nums[left] squared
        rightSquare = nums[right] squared
        if leftSquare is greater than rightSquare:
            put leftSquare into result[position]
            advance left
        else:
            put rightSquare into result[position]
            move right back by one
        move position back by one
    return result
```

## Java Solution

```java
class Solution {
    public int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0, right = n - 1, pos = n - 1;
        while (left <= right) {
            int leftSquare = nums[left] * nums[left];
            int rightSquare = nums[right] * nums[right];
            if (leftSquare > rightSquare) {
                result[pos--] = leftSquare;
                left++;
            } else {
                result[pos--] = rightSquare;
                right--;
            }
        }
        return result;
    }
}
```

We allocate a fresh `result` array of the same length and fill it from index
`n-1` down to `0`, placing the larger square first so the array ends up in
ascending order. The loop condition is `left <= right` (note the `<=`) rather
than `<`: when the two pointers meet on a single middle element, that element's
square still needs a home, so we must process it. The constraint
`|nums[i]| <= 10^4` guarantees the square fits in `int` (max `10^8`), so no
`long` is needed.

## Complexity

    Time:  O(n)  -- each iteration writes one cell and moves one pointer; exactly n iterations.
    Space: O(n)  -- the output array. If we do not count the output (LeetCode convention), O(1) extra.

## Dry-Run

Step-by-step on `nums = [-4,-1,0,3,10]` (n = 5).

| Step | left | right | nums[left]^2 | nums[right]^2 | bigger | result so far (index:value) | move |
|------|------|-------|--------------|---------------|--------|------------------------------|------|
| 1 | 0 | 4 | 16 | 100 | 100 | [4]=100 | right-- |
| 2 | 0 | 3 | 16 | 9 | 16 | [3]=16 | left++ |
| 3 | 1 | 3 | 1 | 9 | 9 | [2]=9 | right-- |
| 4 | 1 | 2 | 1 | 0 | 1 | [1]=1 | left++ |
| 5 | 2 | 2 | 0 | 0 | 0 | [0]=0 | left++ (left>right, exit) |

Final `result = [0,1,9,16,100]`. The middle element `0` at index 2 is processed
at step 5 because of the `<=` condition — that is the whole reason for the
inclusive bound.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [-3,-1,2]`. What is the output array?
- a) `[1,4,9]`
- b) `[9,4,1]`
- c) `[4,9,1]`

<details><summary>Show answer</summary>

**(a)** -- squares: `left=0` (-3)^2=9 vs `right=2` (2)^2=4 -> 9 bigger, write `result[2]=9`, `left++`. Next `(-1)^2=1` vs `(2)^2=4` -> 4 bigger, write `result[1]=4`, `right--`. Pointers meet on `-1`, write `result[0]=1`. Result `[1,4,9]`. Option (b) is the descending trap from filling the front by mistake.

</details>

**Q2 (analyze).** What goes wrong if you write `while (left < right)` (no equals)?
- a) The middle element is never written, so one cell of `result` is left at its default 0 and a real value is dropped
- b) The loop runs one time too many and overwrites a cell
- c) Nothing changes for odd-length arrays

<details><summary>Show answer</summary>

**(a)** -- when the pointers meet on the center element, `<` exits before that element's square is placed. The result has a stray 0 and loses the real middle value -- exactly the Common-mistake called out below.

</details>

**Q3 (transfer).** Suppose the O(n) requirement still held, but you were FORBIDDEN from allocating a new output array (a truly in-place result). How would you reason about it?

<details><summary>Show answer</summary>

Push back on the constraint. The opposite-ends fill works precisely because it writes largest-first into a fresh array; doing that in-place requires shifting elements, which is O(n) per shift and breaks O(n) overall. The honest options are: keep the O(n) extra-space solution, or square in place and pay O(n log n) to re-sort. A true in-place O(n) is not generally achievable, so name the trade-off rather than guessing.

</details>

## Common mistakes

- **Using `left < right`.** This skips the middle element when the pointers meet,
  leaving one cell of `result` as `0` by accident and dropping a real value.
- **Squaring in place and then sorting.** That is O(n log n) and violates the
  O(n) requirement that LeetCode explicitly asks for.
- **Forgetting negatives make larger squares.** Writing the smaller square into
  the back of the result produces a *descending* array — the comparison must put
  the *larger* square at the back.
- **Comparing raw values, not squares.** `-7 > 3` as integers but `49 < 9` is
  false; always compare the squared values, not the original signs.

## Related problems

- [0283 - Move Zeroes](../0283-move-zeroes/) - the fast/slow Two-Pointer variant,
  contrasted with this opposite-ends one.
- [0088 - Merge Sorted Array] - another "fill the result from the back" problem.
- [0125 - Valid Palindrome](../0125-valid-palindrome/) - the simplest
  opposite-ends warm-up.
