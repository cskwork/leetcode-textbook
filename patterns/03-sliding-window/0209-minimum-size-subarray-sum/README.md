# 0209 - Minimum Size Subarray Sum

**Difficulty:** Medium
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/minimum-size-subarray-sum/

## Concepts used

- **Array** -- the input `nums`, every element a positive integer. [glossary](../../../docs/10-glossary.md#array)
- **Sliding window** -- expand `right` to add value, shrink `left` to find the shortest window whose sum reaches the target. [glossary](../../../docs/10-glossary.md#sliding-window)
- **Invariant** -- a condition always true at each loop step; here, "`sum` always equals the total of exactly the elements in `[left..right]`". [glossary](../../../docs/10-glossary.md#invariant)

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

Two gardeners hold a rope across a row of bushes, each bush bearing a number of berries.
The right gardener walks forward to include more bushes (the running total grows); whenever
the berries between them reach the target, the left gardener walks forward too, trying to
drop bushes while still meeting the target -- finding the shortest stretch that is enough.

This trick only works because every number is positive. Adding a bush (moving `right`) always
*increases* the sum, and dropping one (moving `left`) always *decreases* it. That one-way
behaviour is what lets two pointers replace the brute-force O(n^2) "try every pair of ends".
If negatives were allowed, dropping a bush could *increase* the sum and the whole idea
collapses (a different, harder technique would be needed).

Smallest example, `target = 7, nums = [2, 3, 1, 2, 4, 3]`:

- right=0, add 2: sum = 2. Not enough.
- right=1, add 3: sum = 5. Not enough.
- right=2, add 1: sum = 6. Not enough.
- right=3, add 2: sum = 8 >= 7! Record length 4. Shrink: drop 2 -> sum = 6 < 7, stop.
- right=4, add 4: sum = 10 >= 7! Length 4. Shrink: drop 3 -> sum = 7 >= 7, length 3. Drop 1 -> sum = 6 < 7, stop.
- right=5, add 3: sum = 9 >= 7! Length 3. Shrink: drop 2 -> sum = 7 >= 7, length 2. Drop 4 -> sum = 3 < 7, stop.
- Best = 2 (the subarray [4, 3]).

General rule: expand `right` and add `nums[right]` to a running sum. Whenever `sum >= target`,
the window is valid -- record `right - left + 1`, then shrink from the left (subtract
`nums[left]`, move `left` forward) and check again, because a *smaller* valid window may
hide inside the current one.

The **invariant** we maintain is: *after each step, `sum` equals the total of exactly the
elements in `[left..right]`*. Forgetting `sum -= nums[left]` inside the shrink loop breaks
this and the loop never ends. Despite the nested `while`, the total work is O(n): each index
is added once by `right` and removed at most once by `left`.

This is the variable-size window in its purest form. [0121 Best Time to Buy and Sell Stock](../0121-best-time-to-buy-and-sell-stock/)
uses a one-ended version (a running minimum rather than a sum), and [0076 Minimum Window Substring](../0076-minimum-window-substring/)
swaps the numeric sum for a letter-frequency check.

### Checkpoint A -- The shrinking sum

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What invariant must the running `sum` always satisfy?
- a) `sum` equals the total of exactly the elements currently in `[left..right]`
- b) `sum` equals `target`
- c) `sum` is always even

<details><summary>Show answer</summary>

**(a)** -- every add (move `right`) and every remove (move `left`) keeps `sum` in sync with the window's contents; forget one and the loop never ends.

</details>

**Q2 (comprehend).** Why does this sliding window break if the array may contain negative numbers?
- a) Negatives are slower to add
- b) Dropping a negative could *increase* the sum, so shrinking no longer reliably moves toward a smaller valid window
- c) Negatives cause integer overflow

<details><summary>Show answer</summary>

**(b)** -- the whole trick rests on "add grows the sum, remove shrinks it". A negative breaks that one-way behaviour, so the two-pointer idea collapses (a prefix-sum + deque is needed instead).

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `target = 6, nums = [1, 2, 3, 4]`. What is returned?
- a) 1
- b) 2
- c) 3

<details><summary>Show answer</summary>

**(b)** -- right=0 sum 1, right=1 sum 3, right=2 sum 6 (>=6): best=3, shrink to sum 5 left=1. right=3 sum 9 (>=6): best stays 3, shrink drops 2 -> sum 7 still valid -> best=2 (window [3,4]), shrink drops 3 -> sum 4 left=3 stop. Return 2.

</details>

**Q2 (analyze).** What goes wrong if you record `best` in the outer `for` instead of inside the inner `while`?
- a) Nothing -- the answer is identical
- b) You measure only the largest valid window ending at each `right`, missing the smaller valid windows hiding inside it
- c) The code throws

<details><summary>Show answer</summary>

**(b)** -- each shrink step produces a new, smaller valid window. Measuring once per `right` (before shrinking) records the widest one and never sees the tighter ones, so the true minimum is lost.

</details>

**Q3 (transfer).** How would the approach change if the array could contain zeros (but still no negatives)?

<details><summary>Show answer</summary>

It would still work: zeros neither grow nor shrink the sum, so the monotonic "add grows, remove shrinks" property is preserved (the sum just stays flat on a zero). The algorithm needs no change; only true negatives break it.

</details>

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
