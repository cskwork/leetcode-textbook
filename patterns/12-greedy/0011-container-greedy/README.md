# 0011 - Container With Most Water (Greedy View)

**Difficulty:** Medium
**Pattern:** Greedy
**LeetCode:** https://leetcode.com/problems/container-with-most-water/

> A two-pointer version of this problem lives in
> [patterns/02-two-pointers/0011-container-with-most-water/](../../02-two-pointers/0011-container-with-most-water/).
> The code here is identical; the difference is *why* the algorithm is
> correct. The Two-Pointer chapter reads the move rule as "shrink toward
> the more promising side". This chapter reads the same move rule as a
> **greedy choice with a formal exchange argument** that proves moving the
> shorter line is safe.

## Problem

You are given an integer array `height` of length `n`, where `height[i]` is
the height of a vertical line at x-coordinate `i`. Pick two lines that,
together with the x-axis, form a container holding the most water. Return
the **maximum area** (you may not tilt the container).

Signature:

    int maxArea(int[] height)

Examples (verbatim from LeetCode):

    Input:  height = [1,8,6,2,5,4,8,3,7]
    Output: 49

    Input:  height = [1,1]
    Output: 1

## Intuition

The area between indices `left` and `right` is
`width * min(height[left], height[right])`, with `width = right - left`.
The brute-force of all O(n^2) pairs is the obvious baseline. A DP / memo
formulation is awkward because the state depends on two pointers. The
greedy insight collapses it to a single pass.

Start both pointers at the ends (the widest container) and shrink inward.
At every step you must drop one line. The greedy rule is:

> **Always move the pointer on the shorter line.**

The locally-best choice is "abandon the shorter line, because keeping it
can only hurt". The rest of this README is the proof that this local
choice is part of some optimal solution — the defining question of the
Greedy pattern.

### Exchange argument (why moving the shorter line is safe)

Suppose `height[left] <= height[right]`. Consider any container whose left
edge is at this `left`. We claim none of them can be the unique optimum,
so we may safely abandon `left`.

- The current container is `(left, right)` with area
  `width * height[left]`.
- Any other container `(left, k)` with `left < k < right` is *narrower*
  (`k - left < right - left`) and still capped by `height[left]` (the
  shorter side is at least as small as `height[left]` for every such
  `k`), so its area is **strictly smaller** than `(left, right)`.
- Therefore the best container that uses `left` as its left edge is
  exactly `(left, right)` — which we have already measured. Any further
  shrink that *keeps* `left` can only make things worse.

So leaving `left` behind loses no candidate we have not already recorded.
The same argument, mirrored, applies when `height[right] < height[left]`:
abandon `right`. By induction each move is safe, and after at most `n - 1`
moves we have considered the only candidates that can be optimal.

This is a textbook **exchange argument**: "any solution that does not make
the greedy choice can be transformed into one that does, without making it
worse". That is the exact proof template the Greedy pattern demands.

## Pseudocode

```text
function maxArea(height):
    left = first index
    right = last index
    best = 0
    while left < right:
        width = right - left
        h = the smaller of height[left] and height[right]
        area = width * h
        if area is greater than best:
            best = area
        # Greedy commit: abandon the shorter line.
        if height[left] is less than or equal to height[right]:
            advance left
        else:
            move right back by one
    return best
```

The area is recorded *before* the move so the widest configuration is
never skipped. On ties (`height[left] == height[right]`) either move is
safe; we choose to advance `left`, but `right--` works identically.

## Java Solution

```java
class Solution {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int best = 0;
        while (left < right) {
            int h = Math.min(height[left], height[right]);
            int width = right - left;
            int area = h * width;
            if (area > best) {
                best = area;
            }
            // Exchange argument: any container that keeps the shorter line
            // and shrinks width is strictly worse, so abandon the shorter line.
            if (height[left] <= height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return best;
    }
}
```

`Math.min` picks the limiting line because water would spill over the
shorter side. The `area > best` update is performed unconditionally before
the move, so every candidate the proof identifies as "potentially optimal"
is measured. The move rule `height[left] <= height[right]` uses `<=` so
that ties resolve deterministically; both choices are provably safe. We
keep everything in `int` because LeetCode's constraints
(`height[i], n <= 10^5`) keep the maximum area under `10^10`, but careful
readers should note that doubling either bound would force a `long`.

## Complexity

    Time:  O(n)   -- each iteration moves exactly one pointer, so at most n iterations.
    Space: O(1)   -- three integer variables.

## Dry-Run

Step-by-step on `height = [1,8,6,2,5,4,8,3,7]` (indices 0..8):

| Step | left | right | h = min | width | area | best | shorter side | move |
|------|------|-------|---------|-------|------|------|--------------|------|
| 1    | 0    | 8     | min(1,7)=1 | 8  | 8    | 8    | left (1)     | left++ |
| 2    | 1    | 8     | min(8,7)=7 | 7  | 49   | 49   | right (7)    | right-- |
| 3    | 1    | 7     | min(8,3)=3 | 6  | 18   | 49   | right (3)    | right-- |
| 4    | 1    | 6     | min(8,8)=8 | 5  | 40   | 49   | tie          | left++ |
| 5    | 2    | 6     | min(6,8)=6 | 4  | 24   | 49   | left (6)     | left++ |
| 6    | 3    | 6     | min(2,8)=2 | 3  | 6    | 49   | left (2)     | left++ |
| 7    | 4    | 6     | min(5,8)=5 | 2  | 10   | 49   | left (5)     | left++ |
| 8    | 5    | 6     | min(4,8)=4 | 1  | 4    | 49   | left (4)     | left++ |
| 9    | 6    | 6     | left == right -> exit | |      |      |              |      |

Final `best = 49`, achieved at step 2 with lines at indices 1 and 8
(heights 8 and 7, width 7). Notice how the algorithm abandons line 0
after measuring it once: keeping a height-1 line while shrinking the width
could only reduce the area, which is exactly the exchange argument.

## Common mistakes

- **Moving the taller line.** The central error. The exchange argument
  shows that keeping the shorter line and shrinking is strictly worse, so
  moving the taller line (and keeping the shorter) can never improve the
  answer. The algorithm either loops uselessly or misses the optimum.
- **Computing `width` as `right - left + 1`.** The water spans the *gap*
  between the two lines, so the width is `right - left`. Off-by-one here
  inflates every area.
- **Measuring area after the move.** The widest (often best) container is
  the starting configuration; if you move before updating `best` you may
  skip it entirely.
- **Treating the move rule as a heuristic rather than a proof.** Without
  the exchange argument it is tempting to add tweaks ("try both moves and
  take the better") which break the O(n) bound. The proof says one move
  is enough; trust it.
- **Using `long` where `int` suffices, or vice versa.** For LeetCode's
  constraints `int` is safe, but if you adapt this to a problem with
  larger heights/widths, switch the area computation to `long` to avoid
  silent overflow.

## Related problems

- [0011 - Container With Most Water (Two-Pointer view)](../../02-two-pointers/0011-container-with-most-water/) -
  the identical algorithm framed as a Two-Pointer pattern.
- [0053 - Maximum Subarray](../0053-maximum-subarray/) - another greedy
  single-pass with a "commit to the local choice" rule (Kadane's reset).
- [0042 - Trapping Rain Water] - a harder cousin that uses a similar
  shorter-side rule but with a per-cell accumulation.
