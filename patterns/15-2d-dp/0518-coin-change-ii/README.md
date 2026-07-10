# 0518 - Coin Change II

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/coin-change-ii/

## Problem

Given an integer `amount` and an array `coins` of distinct denominations, return
the **number of combinations** that make up that amount. You may use each
denomination an unlimited number of times. The order of coins in a combination
does **not** matter (`{1, 2}` and `{2, 1}` count as one).

Signature:

    int change(int amount, int[] coins)

Examples (verbatim from LeetCode):

    Input:  amount = 5, coins = [1,2,5]
    Output: 4
    Explanation: 5=5, 5=2+2+1, 5=2+1+1+1, 5=1+1+1+1+1.

    Input:  amount = 3, coins = [2]
    Output: 0

    Input:  amount = 10, coins = [10]
    Output: 1

## Intuition

This is **unbounded knapsack** in disguise: each coin is an item of weight
`coins[k]`, the capacity is `amount`, and we count ways to fill the capacity
with items usable any number of times. The natural two-axis state is `dp[i][a]`
= number of ways to form amount `a` using only the first `i` coin types. The
recurrence for each cell chooses "skip coin `i`" (`dp[i-1][a]`) or "use one more
of coin `i`" (`dp[i][a - coins[i-1]]`, reusing the same row because the coin is
still available). The base `dp[*][0] = 1` because there is exactly one way to
make amount 0 -- pick no coins at all.

The standard implementation collapses the `i` axis into a single 1-D row because
each row reads only itself and the previous row. **The order of the two loops is
the whole subtlety**: coins must be the **outer** loop and amount the **inner**
loop. This forces every combination to be built in a fixed coin order, so
`{1, 2}` and `{2, 1}` collapse into a single count. Swap the loops and you
instead count every **permutation** -- a different problem (LC 377).

### Checkpoint A -- Counting combinations

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** After some coin types have been processed, what does `dp[a]` hold?
- a) The fewest coins needed to make amount `a`
- b) The number of combinations that form amount `a` using the coin types seen so far
- c) Whether amount `a` is reachable at all

<details><summary>Show answer</summary>

**(b)** -- `dp[a]` is a running count of combinations; (a) is Coin Change I and (c) is a boolean reachability question.

</details>

**Q2 (comprehend).** Why is `dp[0]` set to `1`?
- a) Because amount `0` is the goal
- b) Because there is exactly one way to make amount `0` -- choose no coins -- and every "use one more coin" chain reduces to it
- c) Because the smallest coin is worth `1`

<details><summary>Show answer</summary>

**(b)** -- the empty selection is the single way to reach 0, and it is the seed every `dp[a - coin]` chain eventually reads; without it everything collapses to 0.

</details>

## Pseudocode

    function change(amount, coins):
        create a 1-D table dp of size (amount + 1), filled with 0
        dp[0] = 1                                     # one way to make amount 0: no coins

        for each coin in coins:                       # OUTER loop: coin types
            for a from coin up to amount:             # INNER loop: ascending so a coin is reusable
                dp[a] = dp[a] + dp[a - coin]          # skip-or-use; the + adds the "use one more" ways

        return dp[amount]

The two-axis origin, for reference, is `dp[i][a] = dp[i-1][a] + dp[i][a -
coins[i-1]]` with base `dp[i][0] = 1`. The 1-D row is that recurrence with the
`i-1` row dropped, which is valid only because the inner loop runs **ascending**
-- so `dp[a - coin]` is the *current* row's value (coin already reusable), and
`dp[a]`'s old value is still the *previous* row's value (coin skipped).

## Java Solution

```java
class Solution {
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) {
                dp[a] += dp[a - coin];
            }
        }

        return dp[amount];
    }
}
```

`dp[a]` holds the number of combinations forming amount `a` using the coin types
processed so far. The outer loop feeds coins in one fixed order; the inner loop
runs `a` ascending so a coin can be applied repeatedly -- that ascending
direction is what makes this *unbounded* rather than 0/1 (compare Partition Equal
Subset Sum, where the inner loop runs descending). `dp[a] += dp[a - coin]`
adds, to the existing count of combinations that skip this coin, the count of
combinations that use one more of this coin (the latter already lives in
`dp[a - coin]`). The single-element base `dp[0] = 1` is the seed: every chain
of "use one more coin" eventually reduces to "make amount 0", which has exactly
one way.

## Complexity

    Time:  O(c * amount)  -- one constant-time add per (coin, amount) pair, c = coins.length.
    Space: O(amount)      -- the 1-D row. The full 2-D form is O(c * amount).

## Dry-Run

On `amount = 5`, `coins = [1, 2, 5]` (expected `4`). Start with `dp = [1, 0, 0,
0, 0, 0]` (index 0..5). Process each coin, updating `dp` in place.

**Coin 1** -- inner loop `a` from 1 to 5. Each `dp[a] += dp[a-1]`, so the row
becomes `[1, 1, 1, 1, 1, 1]`. (One way to make every amount using only 1s.)

**Coin 2** -- inner loop `a` from 2 to 5:

| a | before | `dp[a-2]` (current row) | `dp[a] += dp[a-2]` | after |
|:-:|:------:|:-----------------------:|:------------------:|:-----:|
| 2 | 1      | dp[0] = 1               | 1 + 1 = 2          | 2     |
| 3 | 1      | dp[1] = 1               | 1 + 1 = 2          | 2     |
| 4 | 1      | dp[2] = 2               | 1 + 2 = 3          | 3     |
| 5 | 1      | dp[3] = 2               | 1 + 2 = 3          | 3     |

Row after coin 2: `[1, 1, 2, 2, 3, 3]`.

**Coin 5** -- inner loop `a` from 5 to 5: `dp[5] += dp[0]` = `3 + 1 = 4`. Row
becomes `[1, 1, 2, 2, 3, 4]`.

`dp[5] = 4`, the answer. The four combinations are exactly the ones LeetCode
lists: `{5}`, `{2,2,1}`, `{2,1,1,1}`, `{1,1,1,1,1}`. Because the coin loop is
outer, each combination is recorded once, in its sorted order -- never as both
`{1,2,2}` and `{2,2,1}`.

### Checkpoint B -- Loop order matters

**Q1 (apply).** Trace `amount = 4`, `coins = [1, 2]`. After coin `1` the row is `[1,1,1,1,1]`. After coin `2`, what is `dp[4]`?
- a) 2
- b) 3
- c) 4

<details><summary>Show answer</summary>

**(b)** -- `dp[2] += dp[0]` -> `2`; `dp[3] += dp[1]` -> `2`; `dp[4] += dp[2]` -> `1 + 2 = 3`. The three combos are `{2,2}`, `{2,1,1}`, `{1,1,1,1}`.

</details>

**Q2 (analyze).** What happens if you swap the loops -- amount on the outside, coins on the inside?
- a) Same answer; loop order is irrelevant
- b) You count permutations instead of combinations (`{1,2}` and `{2,1}` counted separately), overcounting
- c) The code throws an exception

<details><summary>Show answer</summary>

**(b)** -- amount-outer lets each amount be built from any coin order, so different orderings of the same set register as distinct; that is LC 377 (permutations), a different problem.

</details>

**Q3 (transfer).** You now need the fewest NUMBER of coins to make an amount (Coin Change I), not the count of combinations. In one sentence, how does the DP change?

<details><summary>Show answer</summary>

Store a minimum instead of a sum: `dp[a] = min(dp[a], dp[a - coin] + 1)` over all coins, with `dp[0] = 0` and the rest initialised to infinity. The "coins outer, amount inner ascending" restriction can be dropped because you want the best coin at each amount.

</details>

## Common mistakes

- **Swapping the loops.** Put amount on the outside and coins on the inside and
  you count *permutations* (LC 377) -- for `amount = 5, coins = [1,2,5]` you
  would get `9`, because `{2,2,1}`, `{2,1,2}`, and `{1,2,2}` are counted as
  three separate ways.
- **Running the inner loop descending.** That makes each coin usable at most
  once (0/1 knapsack). With `amount = 5, coins = [1,2,5]` you would then return
  `3` (only `{1,2,2}`-style 0/1 picks survive, in fact `{5}` and... the counts
  shift) -- wrong for an *unbounded* problem.
- Initialising the whole row to `1`. Only `dp[0] = 1`; the rest start at `0` and
  are filled by the recurrence. A row of all-1s would treat every amount as
  reachable in one trivial way before any coin is considered.
- Forgetting `dp[0] = 1`. Without it the seed is gone and every `dp[a - coin]`
  chain bottoms out at 0, returning 0 for every input.
- Reading `dp[a - coin]` after it was already updated. With the ascending loop
  this is *intended* (it is how a coin gets reused). With a descending loop it
  would never reuse, which is the bug above -- so be deliberate about the
  direction.

## Related problems

- [0416 - Partition Equal Subset Sum](../0416-partition-equal-subset-sum/) --
  the 0/1 sibling: same scaffold but each item usable once, so the inner loop
  runs **descending**.
- [1143 - Longest Common Subsequence](../1143-longest-common-subsequence/) --
  another two-axis DP whose state is `(i, j)` rather than `(coin, amount)`.
