# 0322 - Coin Change

**Difficulty:** Medium
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/coin-change/

## Problem

You are given an integer array `coins` of available denominations and
an integer `amount`. Return the **fewest number of coins** needed to
make up that amount. If the amount cannot be made, return `-1`. You
may use each coin denomination **unlimited** times.

Signature:

    int coinChange(int[] coins, int amount)

Examples (verbatim from LeetCode):

    Input:  coins = [1,2,5], amount = 11
    Output: 3
    Explanation: 11 = 5 + 5 + 1 (3 coins).

    Input:  coins = [2], amount = 3
    Output: -1

    Input:  coins = [1], amount = 0
    Output: 0

Constraints: `1 <= coins.length <= 12`, `1 <= coins[i] <= 2^31 - 1`,
`0 <= amount <= 10^4`.

## Intuition

Trigger: "minimum coins to make amount" — a minimisation with a choice
set, and the same sub-amount is recomputed many times by the naive
recursive solution. This is the classic "unbounded knapsack" DP.

Define `dp[a]` = "fewest coins needed to make amount `a`". Ask: *what
was the last coin added?* It was some coin `c`, which means the rest
of the amount `a - c` was already optimal. So:

    dp[a] = 1 + min over all coins c <= a of dp[a - c]

Base cases: `dp[0] = 0` (zero coins make amount 0). Every other entry
starts at `+infinity` ("unreachable so far"). If `dp[amount]` is still
`+infinity` after the loops, no combination exists and we return `-1`.

Note the loop nesting: the **amount is the outer loop**, the **coins
are the inner loop**. This computes `dp[a]` once for each `a`, using
already-final values of `dp[a-c]`. Swapping the loops would change the
semantics (it would solve "number of combinations" instead — see
related problems).

### Checkpoint A -- The last coin

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** Coin Change's recurrence is `dp[a] = 1 + min over coins c of dp[a-c]`. In plain English, what is `dp[a]`?
- a) The value of coin a
- b) The fewest coins needed to make exactly amount a
- c) The number of distinct combinations summing to a

<details><summary>Show answer</summary>

**(b)** -- dp[a] is the minimum coin count to reach exactly amount a; adding one coin c to the best way to make a-c gives the recurrence.

</details>

**Q2 (comprehend).** Why is the sentinel `amount + 1` used instead of `Integer.MAX_VALUE`?
- a) It is faster to compare
- b) Adding 1 to MAX_VALUE overflows to a huge negative, which would then wrongly win every `min`
- c) amount+1 is always zero

<details><summary>Show answer</summary>

**(b)** -- the recurrence does `dp[a-c] + 1`; if that value were MAX_VALUE it overflows negative and silently becomes the "minimum", corrupting the result. amount+1 is safely larger than any real answer and never overflows.

</details>

## Pseudocode

```text
function coinChange(coins, amount):
    INF = amount + 1                      # "unreachable" sentinel (> any valid coin count)
    create dp array of size amount+1, each entry = INF
    dp[0] = 0                             # 0 coins make amount 0
    for a from 1 to amount:
        for each coin c in coins:
            if c <= a:
                dp[a] = min(dp[a], dp[a - c] + 1)
    if dp[amount] is INF: return -1
    return dp[amount]
```

We use `amount + 1` (not a literal infinity) as the sentinel because
the worst valid coin count is `amount` (all 1's); any value strictly
larger is a safe "unreachable" marker and avoids overflow when we add
1 to it. (Adding 1 to `amount+1` is fine in `int` for `amount <= 10^4`.)

## Java Solution

```java
class Solution {
    public int coinChange(int[] coins, int amount) {
        int inf = amount + 1;
        int[] dp = new int[amount + 1];
        java.util.Arrays.fill(dp, inf);
        dp[0] = 0;
        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (c <= a) {
                    // Reaching amount a by adding coin c to the optimal solution for a-c.
                    dp[a] = Math.min(dp[a], dp[a - c] + 1);
                }
            }
        }
        return dp[amount] == inf ? -1 : dp[amount];
    }
}
```

`inf = amount + 1` is a safe "unreachable" marker because no valid
answer can exceed `amount` (all-1's coins). The outer loop walks
amounts in increasing order so that `dp[a - c]` is always finalised
before it is read — this is what makes the solution bottom-up. The
inner `if (c <= a)` guard prevents reading `dp[a - c]` at a negative
index. Using `Integer.MAX_VALUE` instead of `amount + 1` would risk
overflow when we add 1 inside the `min`.

## Complexity

    Time:  O(amount * coins.length)   -- nested loops; constant work per pair.
    Space: O(amount)                  -- one dp array of size amount+1.

For the worst LeetCode input (`amount = 10^4`, 12 coins) this is
120,000 operations — trivial.

## Dry-Run

Step-by-step on `coins = [1, 2, 5]`, `amount = 11`:

Initial `dp = [0, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf, inf]`
(`inf = 12`).

For each amount `a`, we try each coin and keep the min:

| a  | try c=1 (dp[a-1]+1) | try c=2 (dp[a-2]+1) | try c=5 (dp[a-5]+1) | dp[a] |
|----|---------------------|---------------------|---------------------|-------|
| 1  | dp[0]+1 = 1         | -                   | -                   | 1     |
| 2  | dp[1]+1 = 2         | dp[0]+1 = 1         | -                   | 1     |
| 3  | dp[2]+1 = 2         | dp[1]+1 = 2         | -                   | 2     |
| 4  | dp[3]+1 = 3         | dp[2]+1 = 2         | -                   | 2     |
| 5  | dp[4]+1 = 3         | dp[3]+1 = 3         | dp[0]+1 = 1         | 1     |
| 6  | dp[5]+1 = 2         | dp[4]+1 = 3         | dp[1]+1 = 2         | 2     |
| 7  | dp[6]+1 = 3         | dp[5]+1 = 2         | dp[2]+1 = 2         | 2     |
| 8  | dp[7]+1 = 3         | dp[6]+1 = 3         | dp[3]+1 = 3         | 3     |
| 9  | dp[8]+1 = 4         | dp[7]+1 = 3         | dp[4]+1 = 3         | 3     |
| 10 | dp[9]+1 = 4         | dp[8]+1 = 4         | dp[5]+1 = 2         | 2     |
| 11 | dp[10]+1 = 3        | dp[9]+1 = 4         | dp[6]+1 = 3         | 3     |

`dp[11] = 3`, matching `5 + 5 + 1`. At `a = 5`, the 5-coin branch
first beats the 1-coin branch (1 < 3); at `a = 10`, the 5-coin branch
again wins (`dp[5] + 1 = 2`). Each entry only depends on smaller
entries, so the increasing-amount loop is correct.

### Checkpoint B -- Trace the amount table

**Q1 (apply).** Trace `coins = [1, 3, 4]`, `amount = 6`. (A greedy "biggest first" picks 4+1+1.) What does `coinChange` return?
- a) 2
- b) 3
- c) -1

<details><summary>Show answer</summary>

**(a)** -- dp[3]=1 (coin 3), dp[4]=1 (coin 4), dp[6]=dp[3]+1=2 (two 3-coins). The optimal 3+3 beats the greedy 4+1+1; this is exactly why greedy fails on non-canonical coins.

</details>

**Q2 (analyze).** Why must the AMOUNT be the outer loop and the COINS the inner loop?
- a) So the code runs faster
- b) So each dp[a] is finalized once, reading only already-final smaller entries dp[a-c]; swapping would count combinations instead
- c) It is purely stylistic

<details><summary>Show answer</summary>

**(b)** -- outer-amount ensures dp[a-c] (a smaller amount) is already computed; swapping to outer-coin changes the meaning to "number of combinations" (a different problem).

</details>

**Q3 (transfer).** If you needed to return the actual coins used (not just the count), what extra table would you add, in one sentence?

<details><summary>Show answer</summary>

Add a `choice[a]` array recording which coin c won the `min` at each amount, then walk backwards from the full amount subtracting choice[amount] until you reach 0 to reconstruct the list.

</details>

## Common mistakes

- **Using `Integer.MAX_VALUE` as the sentinel.** Then `dp[a-c] + 1`
  overflows to `Integer.MIN_VALUE` and silently wins every `min`,
  producing nonsense. Use `amount + 1` instead.
- **Swapping the loops** (coins outer, amount inner). That computes
  the *number of combinations* (a different problem, LC 518), not the
  minimum coin count. Order matters.
- **Forgetting `dp[0] = 0`.** Without it every entry stays at `inf`
  because `dp[a-c]` never reaches 0 through the chain.
- **Returning `inf` instead of `-1`.** The problem explicitly wants
  `-1` for unreachable amounts; convert at the return.
- **Greedy by largest coin.** For `coins = [1, 3, 4]`, `amount = 6`,
  greedy picks `4 + 1 + 1` (3 coins); the optimal is `3 + 3` (2
  coins). Greedy fails on non-canonical denominations — this is why
  the problem is DP.

## Related problems

- [0198 - House Robber](../0198-house-robber/) - a simpler
  maximisation DP with a fixed two-predecessor window.
- [0139 - Word Break](../0139-word-break/) - another "scan over
  choices" DP (here: words instead of coins), using boolean reachability.
- [0070 - Climbing Stairs](../0070-climbing-stairs/) - the same
  "amount = outer loop" structure with steps as the "coins".
