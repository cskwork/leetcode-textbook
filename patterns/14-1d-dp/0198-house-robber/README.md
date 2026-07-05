# 0198 - House Robber

**Difficulty:** Medium
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/house-robber/

## Problem

You are a robber facing a row of `n` houses, each with money `nums[i]`.
You cannot rob two adjacent houses (the alarm triggers). Return the
**maximum total money** you can rob tonight.

Signature:

    int rob(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [1,2,3,1]
    Output: 4
    Explanation: rob house 0 (money 1) and house 2 (money 3): 1 + 3 = 4.

    Input:  nums = [2,7,9,3,1]
    Output: 12
    Explanation: rob houses 0, 2, 4: 2 + 9 + 1 = 12.

Constraints: `1 <= nums.length <= 100`, `0 <= nums[i] <= 400`.

## Intuition

Trigger: "maximum over choices" with a constraint that creates
overlapping subproblems. At each house we make a binary choice — rob
it or skip it — and the constraint (no two adjacent) couples that
choice to the previous one.

Define `dp[i]` = "maximum money robbed from the first i houses"
(`nums[0..i-1]`). At house `i` (1-indexed), two options:

- **Skip it.** Best you can do is `dp[i-1]`.
- **Rob it.** Then you must have skipped house `i-1`, so you add
  `nums[i-1]` to `dp[i-2]`.

Take the max:

    dp[i] = max(dp[i-1],  dp[i-2] + nums[i-1])

Base cases: `dp[0] = 0` (no houses, no money), `dp[1] = nums[0]`
(one house, rob it). The answer is `dp[n]`. The recurrence window is
size 2, so two rolling variables suffice.

## Pseudocode

```text
function rob(nums):
    if length of nums is 0: return 0
    if length of nums is 1: return nums[0]

    prev2 = 0                       # dp[0]: rob nothing
    prev1 = nums[0]                 # dp[1]: rob the only house
    for i from 2 to length of nums:
        current = max(prev1,        # skip house i, keep best of first i-1
                      prev2 + nums[i-1])   # rob house i, must skip i-1
        prev2 = prev1
        prev1 = current
    return prev1
```

The invariant after iteration `i`: `prev1 = dp[i]`, `prev2 = dp[i-1]`.
Returning `prev1` gives `dp[n]`.

## Java Solution

```java
class Solution {
    public int rob(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        int prev2 = 0;                 // dp[0]
        int prev1 = nums[0];           // dp[1]
        for (int i = 2; i <= nums.length; i++) {
            // Rob house i-1 (so add nums[i-1] to dp[i-2]) OR skip it (keep dp[i-1]).
            int current = Math.max(prev1, prev2 + nums[i - 1]);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
```

`nums[i-1]` is the money in house `i` (1-indexed `i`, 0-indexed
`nums`). The recurrence's two terms are exactly the two legal
choices; `max` picks the better. `prev2` represents "best of houses
before the immediately previous one" — the only state we may legally
rob from. With `nums[i] <= 400` and `n <= 100`, the max possible sum
(40000) fits comfortably in `int`.

## Complexity

    Time:  O(n)   -- one pass; each house does a single max.
    Space: O(1)   -- two rolling variables.

## Dry-Run

Step-by-step on `nums = [2, 7, 9, 3, 1]`:

| i | prev2 (dp[i-2]) | prev1 (dp[i-1]) | skip: prev1 | rob: prev2 + nums[i-1] | current = max | meaning               |
|---|-----------------|-----------------|-------------|------------------------|---------------|-----------------------|
| - | 0               | 2               | -           | -                      | -             | init dp[0]=0, dp[1]=2 |
| 2 | 0               | 2               | 2           | 0 + nums[1]=0+7=7      | 7             | dp[2] = 7             |
| 3 | 2               | 7               | 7           | 2 + nums[2]=2+9=11     | 11            | dp[3] = 11            |
| 4 | 7               | 11              | 11          | 7 + nums[3]=7+3=10     | 11            | dp[4] = 11            |
| 5 | 11              | 11              | 11          | 11 + nums[4]=11+1=12   | 12            | dp[5] = 12            |

After the loop, `prev1 = 12` = `dp[5]`. The chosen houses are indices
0, 2, 4 (`2 + 9 + 1 = 12`). Note step 4: skipping house 3 (`prev1=11`)
beats robbing it (`10`), so we keep the previous best.

## Common mistakes

- **Wrong `dp` definition.** Defining `dp[i]` as "rob house i" instead
  of "best of first i houses" loses the skip option. Always define the
  state as the *prefix optimum*, not a single-element decision.
- **Forgetting the "must skip i-1 when robbing i" constraint.** Writing
  `dp[i] = max(dp[i-1], dp[i-1] + nums[i-1])` allows robbing two
  adjacent houses — wrong.
- **Off-by-one on `nums[i-1]`.** `dp[i]` is 1-indexed (prefix length),
  but `nums` is 0-indexed, so the money for "house i" is `nums[i-1]`.
- **Initialising `prev2` to `nums[0]` instead of 0.** `dp[0]` is the
  empty prefix (money 0); `nums[0]` belongs to `dp[1]`.
- **Using `nums.length == 0` as the only edge case.** Length 1 must be
  handled before the loop, otherwise `nums[0]` is accessed twice.

## Related problems

- [0213 - House Robber II](../0213-house-robber-ii/) - the circular
  version; reduces to two runs of this solution.
- [0070 - Climbing Stairs](../0070-climbing-stairs/) - same two-step
  rolling-window shape, additive instead of maximising.
- [0322 - Coin Change](../0322-coin-change/) - a maximisation/minimisation
  DP whose recurrence scans a set rather than two predecessors.
