# 0977 - Squares of a Sorted Array

**Difficulty:** Easy
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/squares-of-a-sorted-array/

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

Squaring each element is O(n), but the catch is that negatives become positives,
so the largest squares can sit at *either* end of the input (e.g. `-7` squares
to `49`, larger than `11`'s... wait, `11` squares to `121`; but `-7` is larger
than `2`'s `4`). The result is therefore **not** simply the input squared in
place.

The key observation: once squared, the values are largest at the two ends and
decrease toward the middle (where `0` would be). That is exactly the shape that
the opposite-ends Two-Pointer variant handles. Put one pointer at each end,
compare the two squares, write the **larger** square into the result array from
the back, and move that pointer inward. The result fills from largest to
smallest, which is just the reverse of what we want — so writing from the last
index backward gives the answer in sorted order in one pass.

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
