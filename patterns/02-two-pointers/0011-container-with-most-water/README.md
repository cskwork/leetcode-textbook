# 0011 - Container With Most Water

**Difficulty:** Medium
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/container-with-most-water/

## Problem

You are given an integer array `height` of length `n`, where `height[i]` is the
height of a vertical line drawn at x-coordinate `i`. Pick two lines that,
together with the x-axis, form a container that holds the most water. Return the
**maximum area** of water the container can hold (you may not tilt the
container).

Signature:

    int maxArea(int[] height)

Examples (verbatim from LeetCode):

    Input:  height = [1,8,6,2,5,4,8,3,7]
    Output: 49

    Input:  height = [1,1]
    Output: 1

## Intuition

The area between two lines at indices `left` and `right` is
`width * min(height[left], height[right])`, where `width = right - left`. A
brute-force check of every pair is O(n^2). The Two-Pointer trick: start with
the widest possible container (one pointer at each end), and then shrink the
width — but when you shrink, you must drop the **shorter** line.

Why drop the shorter one? Because the area is capped by the shorter line, and
shrinking the width can only reduce area *unless* the new shorter line is
taller. Moving the taller line cannot help: the cap is still the same short
line, and the width just got smaller. So at every step the only move that could
possibly improve the answer is to advance the pointer on the shorter side.

This is the classic opposite-ends Two-Pointer with a greedy move rule.

## Pseudocode

```text
function maxArea(height):
    set left to first index
    set right to last index
    set best to 0
    while left < right:
        width = right - left
        h = the smaller of height[left] and height[right]
        best = the larger of best and (width * h)
        if height[left] is less than height[right]:
            advance left
        else:
            move right back by one
    return best
```

## Java Solution

```java
class Solution {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int best = 0;
        while (left < right) {
            int h = Math.min(height[left], height[right]);
            int width = right - left;
            best = Math.max(best, h * width);
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }
}
```

`Math.min` picks the limiting line because water would spill over the shorter
side. The move rule `if (height[left] < height[right]) left++` is the heart of
the algorithm: we always abandon the shorter line, since keeping it while
shrinking width could only reduce area. When the heights are equal it does not
matter which side moves, so the `else` branch (moving `right`) is fine. The
loop computes the area *before* moving so the very first (widest) configuration
is never skipped.

## Complexity

    Time:  O(n)  -- each iteration moves exactly one pointer, so at most n iterations.
    Space: O(1)  -- only three integer variables are used.

## Dry-Run

Step-by-step on `height = [1,8,6,2,5,4,8,3,7]` (indices 0..8).

| Step | left | right | h = min | width | area | best | move |
|------|------|-------|---------|-------|------|------|------|
| 1 | 0 | 8 | min(1,7)=1 | 8 | 8 | 8 | left shorter -> left++ |
| 2 | 1 | 8 | min(8,7)=7 | 7 | 49 | 49 | right shorter -> right-- |
| 3 | 1 | 7 | min(8,3)=3 | 6 | 18 | 49 | right shorter -> right-- |
| 4 | 1 | 6 | min(8,8)=8 | 5 | 40 | 49 | equal -> right-- |
| 5 | 1 | 5 | min(8,4)=4 | 4 | 16 | 49 | right shorter -> right-- |
| 6 | 1 | 4 | min(8,5)=5 | 3 | 15 | 49 | right shorter -> right-- |
| 7 | 1 | 3 | min(8,2)=2 | 2 | 4 | 49 | right shorter -> right-- |
| 8 | 1 | 2 | min(8,6)=6 | 1 | 6 | 49 | right shorter -> right-- |
| 9 | left==right -> exit | | | | | | |

Final `best = 49`. The maximum was found at step 2 (lines at indices 1 and 8:
heights 8 and 7, width 7).

## Common mistakes

- **Moving the taller line.** This is the central error: it can never improve
  the area, so the algorithm either loops uselessly or misses the optimum.
- **Computing `width` as `right - left + 1`.** The lines are at integer
  positions; the water spans the gap between them, so width is `right - left`.
- **Comparing heights with `<=` and advancing `right` on ties in a way that
  skips a candidate.** Ties are harmless either way *as long as* the area is
  recorded before moving — double-check that the `best` update comes first.
- **Using `int` area and overflowing.** Heights and width are both up to 10^5,
  so the product is up to 10^10, which **does** overflow `int` (max ~2.1 * 10^9).
  LeetCode's official constraints keep it under 10^9 here, but if the bounds
  were larger you would need `long`.

## Related problems

- [0167 - Two Sum II](../0167-two-sum-ii/) - same opposite-ends skeleton; the
  move rule differs (sum vs. height comparison).
- [0042 - Trapping Rain Water] - a harder cousin that also uses pointer movement
  based on the shorter side.
- [0015 - 3Sum](../0015-3sum/) - the other flagship opposite-ends problem.
