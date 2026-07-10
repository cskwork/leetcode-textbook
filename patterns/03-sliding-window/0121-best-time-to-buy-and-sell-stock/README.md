# 0121 - Best Time to Buy and Sell Stock

**Difficulty:** Easy
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/best-time-to-buy-and-sell-stock/

## Concepts used

- **Array** -- a row of numbered slots holding values; here `prices[i]` is the price on day `i`. [glossary](../../../docs/10-glossary.md#array)
- **Sliding window** -- keeping a moving view over part of the data and a small piece of state about it; this problem is the family's simplest member. [glossary](../../../docs/10-glossary.md#sliding-window)
- **Time complexity** -- how runtime grows with input size; we want O(n), not the brute-force O(n^2). [glossary](../../../docs/10-glossary.md#time-complexity)

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

Imagine you walk down a street where each shop displays today's stock price on a sign, and
you may buy on exactly one day and sell on a later day. The clumsy way is to compare every
pair of shops (O(n^2)). The clever way: as you walk, keep one number in your head -- the
**cheapest price seen so far**. At each new shop, ask "if I sold today, having bought at
that cheapest earlier shop, what would I make?" The biggest of those daily answers is your
result.

Why is remembering only the cheapest price enough? Because the best sale on any day always
pairs with the lowest price *before* that day -- a more expensive earlier buy could never
beat a cheaper one. So a single running minimum captures everything we need.

Smallest example, `prices = [7, 1, 5, 3, 6, 4]`:

- Day 0, price 7: cheapest so far = 7. Selling today is impossible (nothing earlier), profit 0.
- Day 1, price 1: cheapest = 1. Profit 1 - 7 is negative, ignore. (1 is now the new cheapest.)
- Day 2, price 5: profit 5 - 1 = 4. Best = 4.
- Day 3, price 3: profit 3 - 1 = 2. Best stays 4.
- Day 4, price 6: profit 6 - 1 = 5. Best = 5.
- Day 5, price 4: profit 4 - 1 = 3. Best stays 5.

Answer: 5.

General rule: scan left to right; at each step update the answer using today's price minus
the running minimum, **then** update the running minimum with today's price. Updating the
answer *before* the minimum guarantees today is never used as both buy and sell -- the buy
must be strictly earlier. This is the simplest member of the sliding-window family: the
"window" is everything from the start up to today, and the only state we keep is its
minimum. Later problems keep more: [0209 Minimum Size Subarray Sum](../0209-minimum-size-subarray-sum/)
keeps a running *sum*, and [0003 Longest Substring Without Repeating Characters](../0003-longest-substring-without-repeating-characters/)
keeps a window with two explicit ends.

### Checkpoint A -- The running minimum

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** The solution keeps only one running value besides the answer. What is it?
- a) The sum of all prices so far
- b) The cheapest price seen so far
- c) The index of the highest price

<details><summary>Show answer</summary>

**(b)** -- remembering the minimum price seen so far is enough, because the best sale on any day always pairs with the lowest earlier buy.

</details>

**Q2 (comprehend).** Why does the code update `best` *before* updating `minPrice`?
- a) To avoid using today as both the buy and the sell day
- b) Because subtraction is faster before assignment
- c) It makes no difference and is just a style choice

<details><summary>Show answer</summary>

**(a)** -- computing `price - minPrice` first guarantees `minPrice` refers to a strictly earlier day, honouring "buy before you sell".

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `prices = [2, 4, 1]`. What is returned?
- a) 1
- b) 2
- c) 0

<details><summary>Show answer</summary>

**(b)** -- day 0 sets minPrice=2; day 1 gives profit 4-2=2 (best=2); day 2 gives 1-2=-1 (ignored) and lowers minPrice to 1. Best stays 2.

</details>

**Q2 (analyze).** What does the code return for an always-falling array like `[5, 4, 3, 2, 1]`, and why?
- a) 0 -- no `price - minPrice` is ever positive, so `best` never leaves its starting value of 0
- b) -1 -- the last day's loss
- c) 4 -- the biggest drop

<details><summary>Show answer</summary>

**(a)** -- every later price is below the running minimum, so each `price - minPrice` is negative and `Math.max` keeps `best` at 0, the correct "no profitable transaction" answer.

</details>

**Q3 (transfer).** If you could buy and sell on the *same* day (zero profit allowed) the answer would not change here, but how would you adapt the approach to "at most two transactions"?

<details><summary>Show answer</summary>

Split the one pass into two non-overlapping windows: do a left-to-right pass recording the best single transaction ending at or before each day, then a right-to-left pass for the best transaction starting at or after each day, and combine the two at each split point. The "running best" reflex stays, applied twice.

</details>

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
