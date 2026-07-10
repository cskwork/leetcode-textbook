# 0053 - Maximum Subarray

**Difficulty:** Medium
**Pattern:** Greedy
**LeetCode:** https://leetcode.com/problems/maximum-subarray/

## Concepts used

- **Greedy** -- make the locally-best choice at each step and never revisit it; it only counts as greedy if you can prove that choice is part of some optimal answer. [glossary](../../../docs/10-glossary.md#greedy)
- **Array** -- a row of numbered slots holding values, accessed by position in O(1). [glossary](../../../docs/10-glossary.md#array)
- **Running sum** -- one variable that keeps adding the next value as you walk, so it always holds the total of the current stretch "so far".
- **Invariant** -- a condition that is always true at the start of every loop iteration; stating it clearly is how you prove a loop correct. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

Given an integer array `nums`, find the contiguous subarray (containing at
least one element) which has the **largest sum**, and return that sum.

Signature:

    int maxSubArray(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [-2,1,-3,4,-1,2,1,-5,4]
    Output: 6
    Explanation: subarray [4,-1,2,1] has the largest sum 6.

    Input:  nums = [1]
    Output: 1

    Input:  nums = [5,4,-1,7,8]
    Output: 23

A subarray is a contiguous slice; the empty subarray is not allowed.

## Intuition

This is the canonical greedy problem. The trigger: "maximum sum over a
contiguous slice" with no constraint on length — a clear "best over a
range" phrasing that points at a single forward pass.

Kadane's insight is one sentence: **a negative prefix can never help a
future subarray**. If the sum of everything up to index `i` is negative,
then any subarray that starts after `i` would be strictly larger if we
simply dropped that prefix. So we keep a *running sum* of the best subarray
ending at the current position; the moment that running sum goes negative,
we throw it away (reset to 0) and start fresh from the next element.

The locally-best choice at each step is "either extend the previous best,
or start over here". The global answer is the maximum running sum seen.
No backtracking is needed because we never regret resetting — a negative
prefix is dead weight forever.

### Checkpoint A -- The negative-prefix rule

Pause and answer before expanding.

**Q1 (recall).** In Kadane's algorithm, what happens the moment the running sum drops below zero?
- a) The running sum is reset to 0
- b) The loop stops and returns immediately
- c) The array is re-scanned from the start

<details><summary>Show answer</summary>

**(a)** -- a negative prefix can only subtract from any subarray that extends it, so the algorithm drops it by resetting `running` to 0 and starting fresh.

</details>

**Q2 (comprehend).** Why is dropping a negative running sum safe -- it can never hide the optimal answer because:
- a) The array is guaranteed to be sorted
- b) Any subarray extending a negative prefix would be larger with that prefix removed
- c) The reset only happens after the loop ends

<details><summary>Show answer</summary>

**(b)** -- this is Kadane's whole insight. Recording `best` *before* the reset is the mechanism that makes the drop lossless.

</details>

## Pseudocode

```text
function maxSubArray(nums):
    best = negative infinity          # best sum seen across all subarrays
    running = 0                       # best sum of a subarray ending here
    for each x in nums:
        add x to running              # extend the current subarray
        if running is greater than best:
            best = running
        if running is less than 0:
            running = 0               # a negative prefix can never help; cut it
    return best
```

Note the order: we *record* the best before resetting. That way a single
large negative element still becomes `best` when every value is negative
(empty subarrays are forbidden).

## Java Solution

```java
class Solution {
    public int maxSubArray(int[] nums) {
        int best = Integer.MIN_VALUE;
        int running = 0;
        for (int x : nums) {
            running += x;
            if (running > best) {
                best = running;
            }
            // A negative prefix can only drag down any future subarray,
            // so discard it and start the next subarray fresh.
            if (running < 0) {
                running = 0;
            }
        }
        return best;
    }
}
```

`best` starts at `Integer.MIN_VALUE` (not 0) so an all-negative array still
returns its single largest element rather than 0. We use `int` because
LeetCode guarantees sums fit in 32 bits; for larger ranges `running` and
`best` would be `long`. The `if (running < 0) running = 0` line is the
greedy commit: having decided this prefix is harmful, we drop it
unconditionally — no second chance.

## Complexity

    Time:  O(n)   -- one pass over the array; each element is visited once.
    Space: O(1)   -- only two integer variables are kept.

## Dry-Run

Step-by-step on `nums = [-2,1,-3,4,-1,2,1,-5,4]`:

| Step | x  | running (after += x) | best | reset running to 0? |
|------|----|----------------------|------|---------------------|
| 1    | -2 | -2                   | -2   | yes (running = 0)   |
| 2    |  1 |  1                   |  1   | no                  |
| 3    | -3 | -2                   |  1   | yes (running = 0)   |
| 4    |  4 |  4                   |  4   | no                  |
| 5    | -1 |  3                   |  4   | no                  |
| 6    |  2 |  5                   |  5   | no                  |
| 7    |  1 |  6                   |  6   | no                  |
| 8    | -5 |  1                   |  6   | no                  |
| 9    |  4 |  5                   |  6   | no                  |

Final `best = 6`, achieved by the subarray `[4, -1, 2, 1]` (steps 4–7).
Notice step 3: even though running went to -2, `best` was already 1 from
step 2, so the reset does not lose the answer.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [-3, 2, -1, 5]`. What is returned?
- a) 6 (subarray [2, -1, 5])
- b) 2
- c) -3

<details><summary>Show answer</summary>

**(a)** -- step 1: running = -3, best = -3, reset to 0. Steps 2-4: running goes 2 -> 1 -> 6, so best ends at 6.

</details>

**Q2 (analyze).** If `best` were initialised to 0 instead of `Integer.MIN_VALUE`, what would `maxSubArray([-3, -1, -2])` return?
- a) 0 -- wrong, it corresponds to the forbidden empty subarray
- b) -1 -- correct
- c) -3

<details><summary>Show answer</summary>

**(a)** -- no running sum ever beats 0, so `best` stays 0. The correct answer is `-1` (the single largest element); use `Integer.MIN_VALUE` or `nums[0]`.

</details>

**Q3 (transfer).** Suppose you also had to return the start and end indices of the winning subarray. What extra bookkeeping would you add?

<details><summary>Show answer</summary>

Keep a `start` index reset to the next element's position whenever `running` resets. Whenever `best` is updated, snapshot `bestStart = start` and `bestEnd = currentIndex`. After the loop `[bestStart, bestEnd]` is the answer.

</details>

## Common mistakes

- **Initialising `best` to 0.** On an all-negative input (e.g.
  `[-3, -1, -2]`) the correct answer is `-1`, but a zero initialiser
  returns 0, which corresponds to the forbidden empty subarray. Use
  `Integer.MIN_VALUE` or `nums[0]`.
- **Resetting `running` before recording the best.** The order must be
  `running += x; update best; if (running < 0) reset`. Swapping the reset
  before the best update discards legitimate single-element peaks.
- **Resetting on `<= 0` instead of `< 0`.** A running sum of exactly 0 is
  harmless to extend; resetting on 0 is not wrong for correctness here but
  muddies the invariant and breaks the "empty subarray forbidden" rule on
  some inputs.
- **Using `long` carelessly and forgetting to cast.** Safe but produces a
  widening-primitive warning on return; not a bug, just untidy.
- **Returning the subarray indices when only the sum was asked.** Kadane
  can be extended to return indices, but the LeetCode signature wants the
  sum.

## Related problems

- [0055 - Jump Game](../0055-jump-game/) - same flavour: one running
  invariant ("farthest reachable") updated in a single pass.
- [0134 - Gas Station](../0134-gas-station/) - another "running tank that
  resets on failure" greedy, but with a global feasibility check layered on.
- [011 - Container Greedy](../0011-container-greedy/) - a different greedy
  flavour (move the shorter line) with a formal exchange-argument proof.
