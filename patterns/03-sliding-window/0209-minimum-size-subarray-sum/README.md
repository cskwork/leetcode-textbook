# 0209 - Minimum Size Subarray Sum

**Difficulty:** Medium
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/minimum-size-subarray-sum/

## Problem

Given an array of **positive integers** `nums` and a positive integer `target`, return
the minimal length of a contiguous subarray of which the sum is greater than or equal to
`target`. If there is no such subarray, return `0`.

Signature:

    int minSubArrayLen(int target, int[] nums)

Example 1:

    Input:  target = 7, nums = [2,3,1,2,4,3]
    Output: 2
    (The subarray [4,3] has sum 7.)

Example 2:

    Input:  target = 4, nums = [1,4,4]
    Output: 1

Example 3:

    Input:  target = 11, nums = [1,1,1,1,1,1,1,1]
    Output: 0

## Intuition

This is the textbook variable-size window. The trigger signal is "shortest contiguous
subarray whose sum hits a target" -- the window must cover a contiguous run. Because all
numbers are positive, growing the window to the right only ever *increases* the sum, and
shrinking from the left only ever *decreases* it; that monotonicity is what lets two
pointers replace the brute-force O(n^2) pair loop. Expand `right` to add value; whenever
the running sum reaches `target`, the window is valid, so record its size and then shrink
`left` to see if a smaller valid window still exists.

## Pseudocode

    function minSubArrayLen(target, nums):
        left = 0
        running-sum = 0
        best = +infinity                    # "shortest" -> start huge
        for right from 0 to len(nums)-1:
            running-sum = running-sum + nums[right]      # EXPAND
            while running-sum >= target:                 # SHRINK while still valid
                best = min(best, right - left + 1)
                running-sum = running-sum - nums[left]
                left = left + 1
        return 0 if best == +infinity else best

The `best` update lives **inside** the `while`, because every shrink step produces a new
valid window whose size might be smaller than the current best.

## Java Solution

```java
class Solution {
    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int best = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                best = Math.min(best, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }
}
```

`best` starts at `Integer.MAX_VALUE` (the "shortest window" identity) and is converted to
`0` at the end if no valid window was ever found. The window size is `right - left + 1`
because both ends are inclusive. Crucially, `sum -= nums[left]` happens **inside** the
shrink loop -- forgetting it would make `sum` lie and the loop would never terminate.
The algorithm is O(n) because each index is added once by `right` and removed at most
once by `left`, even though the code has a nested loop.

## Complexity

    Time:  O(n)   -- the inner while-loop runs at most n times *in total*, not per step.
    Space: O(1)   -- only a few integers.

## Dry-Run

Step-by-step on `target = 7, nums = [2,3,1,2,4,3]`:

| right | nums[r] | sum (after add) | sum >= 7? | shrink actions                                              | best | left | sum (after shrink) |
|-------|---------|-----------------|-----------|-------------------------------------------------------------|------|------|--------------------|
| init  | -       | 0               | -         | -                                                           | +inf | 0    | 0                  |
| 0     | 2       | 2               | no        | -                                                           | +inf | 0    | 2                  |
| 1     | 3       | 5               | no        | -                                                           | +inf | 0    | 5                  |
| 2     | 1       | 6               | no        | -                                                           | +inf | 0    | 6                  |
| 3     | 2       | 8               | yes       | best=min(+inf,4)=4; sum-=2 ->6; left=1; 6<7 stop            | 4    | 1    | 6                  |
| 4     | 4       | 10              | yes       | best=min(4,4)=4; sum-=3 ->7; left=2; 7>=7: best=min(4,3)=3; sum-=1 ->6; left=3; 6<7 stop | 3 | 3 | 6 |
| 5     | 3       | 9               | yes       | best=min(3,3)=3; sum-=2 ->7; left=4; 7>=7: best=min(3,2)=2; sum-=4 ->3; left=5; 3<7 stop | 2 | 5 | 3 |

Return `2`.

## Common mistakes

- Forgetting `sum -= nums[left]` inside the shrink loop -> infinite loop or silently
  wrong sum.
- Recording `best` in the outer `for` instead of the inner `while` -> only the largest
  valid window at each `right` is measured, missing smaller valid windows inside it.
- Initialising `best = 0` for a "minimum" problem -> the answer is always 0. Use
  `+infinity` and convert to `0` only at the end.
- Computing the window size as `right - left` (off by one). With both ends inclusive it
  is `right - left + 1`.
- Applying this to arrays that may contain **negatives**. The monotonicity that makes
  shrinking safe is lost with negatives; you need a prefix-sum + monotonic-deque
  approach instead.

## Related problems

- [0121 - Best Time to Buy and Sell Stock](../0121-best-time-to-buy-and-sell-stock/) --
  the same single-pass + running-accumulator idea in its simplest form.
- [0076 - Minimum Window Substring](../0076-minimum-window-substring/) -- a harder
  "shortest valid window" where the validity check is a frequency match, not a sum.
