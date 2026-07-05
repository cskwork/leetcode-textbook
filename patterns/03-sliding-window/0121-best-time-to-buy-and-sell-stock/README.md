# 0121 - Best Time to Buy and Sell Stock

**Difficulty:** Easy
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/best-time-to-buy-and-sell-stock/

## Problem

You are given an array `prices` where `prices[i]` is the price of a given stock on day
`i`. You want to maximize your profit by choosing **one day to buy** and **a different
day in the future to sell**. Return the maximum profit you can achieve. If no profit is
possible, return `0`.

Signature:

    int maxProfit(int[] prices)

Example 1:

    Input:  prices = [7,1,5,3,6,4]
    Output: 5
    (Buy at 1, sell at 6.)

Example 2:

    Input:  prices = [7,6,4,3,1]
    Output: 0
    (Prices only fall; no profitable transaction.)

## Intuition

The trigger signal is "best subarray ending here with a property" -- the property being
"buy earlier, sell later, maximize the difference". A brute-force pair loop is O(n^2).
The single-pass trick is a sliding *minimum*: as you scan, remember the lowest price seen
so far, because the best sell-day for any future price is the lowest buy-day before it.
At each day, the best possible profit **ending today** is `today - minimum-so-far`; the
global answer is the max of those daily bests. This is the simplest possible instance of
"maintain a running piece of state and update the answer as the window moves".

## Pseudocode

    function maxProfit(prices):
        min-price-so-far = +infinity
        best-profit = 0
        for each price in prices:
            # sell today at `price`, having bought at the best earlier price
            best-profit = max(best-profit, price - min-price-so-far)
            # today becomes a candidate buy-day for future sells
            min-price-so-far = min(min-price-so-far, price)
        return best-profit

Note the order: update the answer BEFORE updating the minimum. That way `min-price-so-far`
always refers to a strictly earlier day, honouring "buy before you sell".

## Java Solution

```java
class Solution {
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int best = 0;
        for (int price : prices) {
            best = Math.max(best, price - minPrice);
            minPrice = Math.min(minPrice, price);
        }
        return best;
    }
}
```

`minPrice` is initialised to `Integer.MAX_VALUE` so the very first day's `price - minPrice`
is hugely negative and cannot beat `best = 0`. We update `best` before `minPrice` so that
today is never used as both buy and sell. Returning `0` for an empty or always-falling
array falls out for free: `best` never moves off zero. The array is scanned exactly once,
so this is O(n) time and O(1) space.

## Complexity

    Time:  O(n)   -- one pass through the array; each day does O(1) work.
    Space: O(1)   -- only two integers of extra memory.

## Dry-Run

Step-by-step on `prices = [7,1,5,3,6,4]`:

| step | price | min-price-so-far (before) | price - min-price-so-far | best | min-price-so-far (after) |
|------|-------|---------------------------|--------------------------|------|--------------------------|
| init | -     | +infinity                 | -                        | 0    | +infinity                |
| 0    | 7     | +infinity                 | huge negative            | 0    | 7                        |
| 1    | 1     | 7                         | -6                       | 0    | 1                        |
| 2    | 5     | 1                         | 4                        | 4    | 1                        |
| 3    | 3     | 1                         | 2                        | 4    | 1                        |
| 4    | 6     | 1                         | 5                        | 5    | 1                        |
| 5    | 4     | 1                         | 3                        | 5    | 1                        |

Return `5`.

## Common mistakes

- Writing two nested loops (every buy/sell pair) -- correct but O(n^2), will Time-Limit-
  Exceed on large inputs.
- Updating `minPrice` **before** computing `price - minPrice`. Harmless for correctness
  on this problem (you just always get 0 for "today vs today"), but it muddies the
  "buy before sell" intuition and breaks analogues like LC 122.
- Initialising `best` to `Integer.MIN_VALUE` instead of `0`. The problem asks for `0`
  when no profit is possible; starting at MIN_VALUE returns a negative answer.
- Skipping the empty-array case. The loop simply does not run and `0` is returned --
  but only because `best` started at `0`.

## Related problems

- [0209 - Minimum Size Subarray Sum](../0209-minimum-size-subarray-sum/) -- same
  one-pass + running-accumulator idea, this time a sum that grows and shrinks.
- [0003 - Longest Substring Without Repeating Characters](../0003-longest-substring-without-repeating-characters/) --
  the same "track the running best as the window moves" reflex, applied to a string.
