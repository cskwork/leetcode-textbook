# 0746 - Min Cost Climbing Stairs

**Difficulty:** Easy
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/min-cost-climbing-stairs/

## Problem

You are given an integer array `cost` where `cost[i]` is the cost of
standing on the i-th step. Once you pay the cost you can climb **1** or
**2** steps forward. You may start from index `0` or index `1`. The
"top" of the stairs is one position past the last index. Return the
**minimum total cost** to reach the top.

Signature:

    int minCostClimbingStairs(int[] cost)

Examples (verbatim from LeetCode):

    Input:  cost = [10,15,20]
    Output: 15
    Explanation: start at index 1, pay 15, climb two steps -> top.

    Input:  cost = [1,100,1,1,1,100,1,1,100,1]
    Output: 6

Constraints: `2 <= cost.length <= 1000`, `0 <= cost[i] <= 999`.

## Intuition

Same shape as Climbing Stairs, but we *minimise* a cost rather than
count ways. The trigger is "minimum cost to reach".

Define `dp[i]` = "minimum total cost to reach step i" (where step i is
one of the array indices, and the "top" is index `n`). To arrive at
step `i` you either came from `i-1` (paying `cost[i-1]`) or from `i-2`
(paying `cost[i-2]`). Take the cheaper predecessor:

    dp[i] = min(dp[i-1] + cost[i-1],  dp[i-2] + cost[i-2])

Base cases: you may start on step 0 or step 1 for free (no cost to
*stand* on your starting step before paying), so `dp[0] = 0` and
`dp[1] = 0`. The answer is `dp[n]` — the cost to reach the top.

Because the recurrence only looks back two steps, two rolling variables
suffice.

## Pseudocode

```text
function minCostClimbingStairs(cost):
    n = length of cost
    prev2 = 0          # dp[0]: cost to stand on step 0
    prev1 = 0          # dp[1]: cost to stand on step 1
    for i from 2 to n:
        current = min(prev1 + cost[i-1],    # climbed 1 step from i-1
                      prev2 + cost[i-2])    # climbed 2 steps from i-2
        prev2 = prev1
        prev1 = current
    return prev1       # dp[n], cost to reach the top
```

The loop runs from `2` to `n` inclusive, so after the last iteration
`prev1` holds `dp[n]` — the cost to stand on the (virtual) top step.

## Java Solution

```java
class Solution {
    public int minCostClimbingStairs(int[] cost) {
        int prev2 = 0;          // dp[0]
        int prev1 = 0;          // dp[1]
        for (int i = 2; i <= cost.length; i++) {
            // dp[i] = cheaper of: arrive from i-1 (pay cost[i-1]) or i-2 (pay cost[i-2])
            int current = Math.min(prev1 + cost[i - 1], prev2 + cost[i - 2]);
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
```

The recurrence indexes `cost[i-1]` and `cost[i-2]` — the cost of the
step you are *leaving*, not the step you land on (you pay when you
stand, then leave for free). The loop bound `i <= cost.length` lets
the final iteration compute `dp[n]`, which represents the virtual
"top" position past the array. As with Climbing Stairs, two variables
replace an O(n) array because the window is size 2.

## Complexity

    Time:  O(n)   -- single pass, one constant-time min per index.
    Space: O(1)   -- only two rolling variables.

## Dry-Run

Step-by-step on `cost = [10, 15, 20]`:

| i | prev2 (dp[i-2]) | prev1 (dp[i-1]) | from i-1: prev1+cost[i-1] | from i-2: prev2+cost[i-2] | current = min | meaning           |
|---|-----------------|-----------------|---------------------------|---------------------------|---------------|-------------------|
| - | 0               | 0               | -                         | -                         | -             | init dp[0]=dp[1]=0|
| 2 | 0               | 0               | 0 + cost[1]=0+15=15       | 0 + cost[0]=0+10=10       | 10            | dp[2] = 10        |
| 3 | 0               | 10              | 10 + cost[2]=10+20=30     | 0 + cost[1]=0+15=15       | 15            | dp[3] = 15        |

After the loop, `prev1 = 15` = `dp[3]` (top). Path: start at index 1
(free), pay 15, climb 2 steps directly to the top. Total cost 15.

## Common mistakes

- **Confusing which cost gets added.** `dp[i]` adds `cost[i-1]` or
  `cost[i-2]` — the cost of the step being left — NOT `cost[i]`. The
  top has no cost; landing there is free.
- **Wrong base cases.** Starting on step 0 or 1 is free, so `dp[0] =
  dp[1] = 0`. Setting them to `cost[0]`/`cost[1]` double-charges.
- **Loop bound `i < n` instead of `i <= n`.** You must compute `dp[n]`
  (the top), so the loop includes `n`. Stopping at `n-1` returns the
  cost to reach the last step, not the top.
- **Reusing Climbing Stairs' base case `dp[1] = 1`.** This problem's
  base cases are both 0; the semantics differ.
- **Mixing index conventions.** Decide whether `dp[i]` is "cost to
  reach step i" or "cost up to and including step i" and stick with
  one. The solution above uses "to reach" (no cost until you leave).

## Related problems

- [0070 - Climbing Stairs](../0070-climbing-stairs/) - same recurrence
  window, but counts ways instead of minimising cost.
- [0198 - House Robber](../0198-house-robber/) - similar two-step
  window with a `max` instead of `min`.
- [0322 - Coin Change](../0322-coin-change/) - a deeper minimisation
  DP where the predecessors are a whole set of coins.
