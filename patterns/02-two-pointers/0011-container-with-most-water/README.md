# 0011 - Container With Most Water

**Difficulty:** Medium
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/container-with-most-water/

## Concepts used

- **Two pointers** -- placing two indices into an array and moving them based on a comparison; here one at each end, shrinking inward. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Array** -- a row of numbered slots holding values, each read in O(1) time by its index. [glossary](../../../docs/10-glossary.md#array)
- **Greedy** -- making the locally-best choice at each step and never revisiting it; here we always drop the shorter line, a choice we prove can never lose. [glossary](../../../docs/10-glossary.md#greedy)

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

Picture a row of fences of different heights standing on flat ground, and you
want the two fences that trap the most rainwater between them. The water rises
only as high as the *shorter* fence, then spills over -- so the puddle between two
fences depends on how far apart they are and on the shorter of their two heights.
Which pair makes the biggest puddle? That is the whole problem.

Let's start with the tiniest case, `height = [1, 1]`: the fences are `1` apart
(width = 1) and both are height `1`, so the area is `1 * min(1, 1) = 1`. That
gives us the area formula: `width * min(height[left], height[right])`, where
`width = right - left`.

A brute-force check of every pair is O(n^2). The [two pointers](../../../docs/10-glossary.md#two-pointers)
shortcut: start as wide as possible -- one finger on the leftmost fence, one on
the rightmost -- then shrink inward, always moving the finger that sits on the
**shorter** fence. Here is the crucial reasoning. The area is capped by the
shorter fence, and shrinking the width can only *reduce* the area unless the new
shorter fence is taller. So if we moved the *taller* finger, the cap (the short
fence) would stay the same while the width got smaller -- guaranteed no better,
never an improvement. The only move with any chance of helping is to move the
*shorter* finger and hope its replacement is taller. That one-way argument is why
we can safely drop the short side and never look back -- a [greedy](../../../docs/10-glossary.md#greedy)
choice: locally best, and provably never worse than the alternative.

Each step we record the area *before* moving, so even the widest (first)
configuration is counted, and we stop when the fingers meet. The result is one
O(n) pass with two index variables -- the same opposite-ends skeleton as
[Two Sum II](../0167-two-sum-ii/), just with a height comparison instead of a sum.

### Checkpoint A -- Move the short side

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** At each step, which pointer do you move?
- a) The one sitting on the taller line
- b) The one sitting on the shorter line
- c) Both, alternating

<details><summary>Show answer</summary>

**(b)** -- the area is capped by the shorter line, so the only move with any chance of helping is to replace the short side and hope its neighbour is taller.

</details>

**Q2 (comprehend).** Why is moving the TALLER line never useful?
- a) The area is limited by the shorter line, so keeping it while shrinking the width can only reduce (or keep equal) the area -- never improve it
- b) Because taller lines are rare and should be saved for later
- c) Because moving the taller line throws an exception

<details><summary>Show answer</summary>

**(a)** -- the cap stays at the short line's height, and the width gets smaller, so the area cannot grow. That one-way argument is what makes dropping the short side a safe greedy choice.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `height = [2,3,4,5]`. What is the maximum area found?
- a) 6
- b) 9
- c) 4

<details><summary>Show answer</summary>

**(a)** -- step 1: `left=0` (2), `right=3` (5), area `min(2,5)*3 = 6`, best=6, move left. step 2: `left=1` (3), `right=3` (5), area `min(3,5)*2 = 6`. step 3: `left=2` (4), `right=3` (5), area `min(4,5)*1 = 4`. Max stays 6. Option (b) comes from using `max` instead of `min` (`3*3`); (c) is a real but smaller area.

</details>

**Q2 (analyze).** What goes wrong if width is computed as `right - left + 1` instead of `right - left`?
- a) Every area is inflated by one unit of width (the first pair reports 9 instead of 8), giving wrong maxima
- b) Nothing -- the two formulas are equivalent
- c) The loop never terminates

<details><summary>Show answer</summary>

**(a)** -- the water spans the GAP between the two lines, which is `right - left`. Adding 1 counts one extra column of water that is not there.

</details>

**Q3 (transfer).** How would you approach the harder cousin "Trapping Rain Water" -- the TOTAL water trapped between ALL the bars after it rains?

<details><summary>Show answer</summary>

The single-pair idea no longer fits, because water sits above every bar, held by its neighbours. For each position you need the tallest bar to its left and the tallest to its right; the water above it is `min(leftMax, rightMax) - height`. Two pointers can compute this in one pass by keeping a running left-max and right-max and always moving the side with the smaller max -- the same "move the shorter side" instinct, just accumulating water instead of one area.

</details>

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
