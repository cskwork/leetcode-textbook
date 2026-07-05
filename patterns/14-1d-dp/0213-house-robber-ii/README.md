# 0213 - House Robber II

**Difficulty:** Medium
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/house-robber-ii/

## Problem

Same as House Robber, but the houses are arranged in a **circle**: the
first and last houses are also adjacent. So you cannot rob both house
0 and house `n-1`. Return the maximum money you can rob without
triggering the alarm.

Signature:

    int rob(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [2,3,2]
    Output: 3
    Explanation: house 0 and house 2 are adjacent in the circle, so rob only house 1.

    Input:  nums = [1,2,3,1]
    Output: 4
    Explanation: rob house 0 (1) and house 2 (3) = 4. House 3 is NOT robbed.

    Input:  nums = [1,2,3]
    Output: 3

Constraints: `1 <= nums.length <= 100`, `0 <= nums[i] <= 1000`.

## Intuition

The circular adjacency is the only new wrinkle; the inner problem is
still House Robber I. The trick is to split into the two linear cases
that the circle forbids simultaneously:

- You **never rob house `n-1`**. Then houses `0..n-2` form a normal
  linear street — solve House Robber I on `nums[0..n-2]`.
- You **never rob house `0`**. Then houses `1..n-1` form a normal
  linear street — solve House Robber I on `nums[1..n-1]`.

Every legal robbery falls into at least one of these two cases (you
rob the first, or you don't — and if you do, you can't rob the last).
So the answer is `max(rob_linear(0..n-2), rob_linear(1..n-1))`. The
single-house case must be handled separately because both slices would
be empty.

This is a recurring DP meta-pattern: **when a constraint makes the
state hard to define, split the input so the constraint disappears in
each piece**, then take the best over the pieces.

## Pseudocode

```text
function rob(nums):
    n = length of nums
    if n is 1: return nums[0]
    if n is 2: return max(nums[0], nums[1])

    # Case A: exclude the last house -> rob linear over [0..n-2]
    bestA = robLinear(nums, 0, n - 2)
    # Case B: exclude the first house -> rob linear over [1..n-1]
    bestB = robLinear(nums, 1, n - 1)
    return max(bestA, bestB)


# Standard House Robber I on the inclusive slice [lo..hi].
function robLinear(nums, lo, hi):
    prev2 = 0                       # best of empty prefix
    prev1 = nums[lo]                # best of first house
    for i from lo+1 to hi:
        current = max(prev1, prev2 + nums[i])
        prev2 = prev1
        prev1 = current
    return prev1
```

`robLinear` is exactly the House Robber I recurrence; the only change
is that we walk an explicit `[lo..hi]` slice instead of the whole
array.

## Java Solution

```java
class Solution {
    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) {
            return nums[0];
        }
        // Either skip the last house (range [0..n-2]) or skip the first ([1..n-1]).
        return Math.max(robLinear(nums, 0, n - 2), robLinear(nums, 1, n - 1));
    }

    private int robLinear(int[] nums, int lo, int hi) {
        int prev2 = 0;
        int prev1 = nums[lo];
        for (int i = lo + 1; i <= hi; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
```

The `n == 1` guard is essential: with one house the two slices would
be empty and `robLinear(nums, 0, -1)` would read `nums[0]` from an
empty range. `robLinear` is the House Robber I algorithm verbatim,
restricted to `[lo..hi]`. We pay for the helper with a second pass,
doubling the time constant, but the asymptotic complexity is still
O(n). No extra space is used.

## Complexity

    Time:  O(n)   -- two linear scans (one for each slice); each is O(n).
    Space: O(1)   -- the helper uses two rolling variables.

## Dry-Run

Step-by-step on `nums = [2, 3, 2]`:

**Case A: slice `[0..1]` = `[2, 3]`** (skip last house)

| i | prev2 | prev1 | skip: prev1 | rob: prev2 + nums[i] | current |
|---|-------|-------|-------------|----------------------|---------|
| - | 0     | 2     | -           | -                    | init    |
| 1 | 0     | 2     | 2           | 0 + 3 = 3            | 3       |

`bestA = 3`.

**Case B: slice `[1..2]` = `[3, 2]`** (skip first house)

| i | prev2 | prev1 | skip: prev1 | rob: prev2 + nums[i] | current |
|---|-------|-------|-------------|----------------------|---------|
| - | 0     | 3     | -           | -                    | init    |
| 2 | 0     | 3     | 3           | 0 + 2 = 2            | 3       |

`bestB = 3`.

**Answer:** `max(3, 3) = 3`. Rob only house 1.

Notice the two slices overlap on the middle house but never include
both ends at once, which is exactly the constraint the circle imposes.

## Common mistakes

- **Forgetting the single-house guard.** With `n == 1`, the slices
  become invalid (empty or negative-length). Handle `n == 1` before
  slicing.
- **Running the slice on `[0..n-1]` (the whole array).** That ignores
  the circular adjacency and answers House Robber I instead. For
  `[2,3,2]` it would wrongly return 4 (rob 2 and 2).
- **Off-by-one on slice bounds.** `[0..n-2]` has length `n-1`; `[1..n-1]`
  also has length `n-1`. Writing `[0..n-1]` or `[1..n-2]` loses a
  house.
- **Defining the helper on a *copy* of the array.** Slicing creates a
  new array per call — wasteful. Pass `(array, lo, hi)` indices.
- **Returning the result of only one case.** The circle forbids
  simultaneous first-and-last robbery; you must take the max of both
  exclusion cases.

## Related problems

- [0198 - House Robber](../0198-house-robber/) - the linear foundation;
  this problem reuses its recurrence verbatim via `robLinear`.
- [0322 - Coin Change](../0322-coin-change/) - another medium DP, but
  with a "scan over choices" recurrence instead of a fixed window.
- [0139 - Word Break](../0139-word-break/) - a different "reduce to a
  simpler sub-check" pattern (here: substring equality).
