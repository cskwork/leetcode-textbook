# 0070 - Climbing Stairs

**Difficulty:** Easy
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/climbing-stairs/

## Problem

You are climbing a staircase. It takes `n` steps to reach the top.
Each time you can either climb **1** or **2** steps. Return the
**number of distinct ways** you can climb to the top.

Signature:

    int climbStairs(int n)

Examples (verbatim from LeetCode):

    Input:  n = 2
    Output: 2
    Explanation: 1+1 or 2.

    Input:  n = 3
    Output: 3
    Explanation: 1+1+1, 1+2, or 2+1.

Constraints: `1 <= n <= 45`.

## Intuition

This is the gateway DP problem — the trigger phrases are "how many
ways" and "answer for n depends on smaller n".

Ask: *what was the last move that landed me on step n?* It was either
a 1-step move from step `n-1`, or a 2-step move from step `n-2`. Every
way to reach `n` ends in exactly one of those two moves, and the two
cases do not overlap. So:

    ways(n) = ways(n-1) + ways(n-2)

That is exactly the Fibonacci recurrence. The base cases are direct:
`ways(1) = 1` (one single step) and `ways(2) = 2` (1+1 or 2).

A naive recursion recomputes `ways(2)` and `ways(1)` over and over —
exponential time. We cache each `ways(i)` once, left to right, in O(n)
time. Because the recurrence only looks back two steps, two variables
are enough instead of a whole array.

## Pseudocode

```text
function climbStairs(n):
    if n is 1: return 1
    if n is 2: return 2

    # prev2 = ways(i-2), prev1 = ways(i-1)
    prev2 = 1            # ways(1)
    prev1 = 2            # ways(2)
    for i from 3 to n:
        current = prev1 + prev2     # ways(i) = ways(i-1) + ways(i-2)
        prev2 = prev1
        prev1 = current
    return prev1
```

The state is just `(prev2, prev1)` — a 2-element window that slides
rightward. We return `prev1` because after the last update it holds
`ways(n)`.

## Java Solution

```java
class Solution {
    public int climbStairs(int n) {
        if (n <= 2) {
            return n;
        }
        int prev2 = 1;          // ways(1)
        int prev1 = 2;          // ways(2)
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;   // ways(i) = ways(i-1) + ways(i-2)
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
```

`if (n <= 2) return n` folds the two base cases into one check
(`ways(1) = 1`, `ways(2) = 2`). The loop invariant is "after iteration
`i`, `prev1 = ways(i)` and `prev2 = ways(i-1)`". We update `prev2`
before `prev1` so neither old value is lost. With `n <= 45` the
largest result (1,836,311,903) fits in `int`; beyond n=46 it would
overflow and you would need `long`.

## Complexity

    Time:  O(n)   -- one loop, n-2 iterations, constant work each.
    Space: O(1)   -- only two rolling variables; no dp array.

## Dry-Run

Step-by-step on `n = 5`:

| i | prev2 (ways(i-2)) | prev1 (ways(i-1)) | current = prev1 + prev2 | meaning          |
|---|-------------------|-------------------|-------------------------|------------------|
| - | 1                 | 2                 | -                       | init: ways(1)=1, ways(2)=2 |
| 3 | 1                 | 2                 | 3                       | ways(3) = 3      |
| 4 | 2                 | 3                 | 5                       | ways(4) = 5      |
| 5 | 3                 | 5                 | 8                       | ways(5) = 8      |

After the loop, `prev1 = 8` = `ways(5)`. Verification by enumeration:
the 8 ways to climb 5 stairs are 11111, 1112, 1121, 1211, 2111, 122,
212, 221.

## Common mistakes

- **Base case `ways(0) = 1` confusion.** Some solutions define
  `ways(0) = 1` (the empty climb) and `ways(1) = 1` so the recurrence
  works from `i = 2`. Both conventions are correct *if used
  consistently*; mixing them produces off-by-one results.
- **Forgetting to update variables in the right order.** Swap the two
  assignments (`prev1 = current` before `prev2 = prev1`) and you
  overwrite `prev1` too early, corrupting every later step.
- **Returning `current` instead of `prev1`.** When `n <= 2` the loop
  never runs and `current` is undefined; `prev1` always holds the
  answer.
- **Naive recursion without memoisation.** `climbStairs(n) =
  climbStairs(n-1) + climbStairs(n-2)` compiles but is O(2^n) and
  times out around n = 40.
- **Using `int` for very large n.** `ways(46)` overflows `int`. The
  LeetCode constraint caps at 45, but check the constraint on similar
  problems before assuming `int` is safe.

## Related problems

- [0746 - Min Cost Climbing Stairs](../0746-min-cost-climbing-stairs/) -
  same `dp[i] = f(dp[i-1], dp[i-2])` shape, but you minimise a cost.
- [0198 - House Robber](../0198-house-robber/) - introduces a `max`
  over two choices instead of a sum.
- [0322 - Coin Change](../0322-coin-change/) - the recurrence scans a
  set of coins instead of two fixed predecessors.
