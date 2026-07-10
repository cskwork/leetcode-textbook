# 0416 - Partition Equal Subset Sum

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/partition-equal-subset-sum/

## Problem

Given an integer array `nums`, determine whether it can be partitioned into two
subsets with **equal sum**. Equivalently: can you pick some subset of `nums`
whose elements add up to exactly half of the total sum?

Signature:

    boolean canPartition(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [1,5,11,5]
    Output: true
    Explanation: the subsets [1,5,5] and [11] both sum to 11.

    Input:  nums = [1,2,3,5]
    Output: false
    Explanation: the total is 11, which cannot be split evenly.

## Intuition

The trigger is "subset that sums to target" -- this is **0/1 knapsack** in
disguise. Two observations reduce it to that form. First, if the total sum is
odd, an equal split is impossible: return `false` immediately. Second, if the
total is even, the question becomes "is there a subset summing to `total / 2`?"
-- and that is exactly the 0/1 knapsack decision problem with each number usable
at most once.

The two-axis state is `dp[i][t]` = "can the first `i` numbers form sum `t`?".
The recurrence for each cell: either skip number `i` (`dp[i-1][t]` survives) or
include it (only possible when `t >= nums[i-1]`, and then we need
`dp[i-1][t - nums[i-1]]`). The base `dp[i][0] = true` for every `i` because the
empty subset always sums to 0. We return `dp[n][target]`.

### Checkpoint A -- The 0/1 knapsack decision

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What does `dp[i][t]` represent?
- a) The number of subsets that sum to `t`
- b) Whether the first `i` numbers can form a subset summing to exactly `t`
- c) The maximum sum achievable using `i` numbers

<details><summary>Show answer</summary>

**(b)** -- `dp[i][t]` is a true/false answer: can the first `i` numbers reach sum `t`? (a) would be a counting problem (Coin Change II), and (c) is a different optimisation.

</details>

**Q2 (comprehend).** Why does the code return `false` immediately when the total sum is odd?
- a) Because the table would be too large to allocate
- b) Because two equal halves must each sum to `total/2`, which is not an integer when `total` is odd -- so an even split is impossible
- c) Because odd totals always contain a duplicate value

<details><summary>Show answer</summary>

**(b)** -- equal partition needs integer halves; an odd `total` has no such split, so we can quit before building any table.

</details>

## Pseudocode

    function canPartition(nums):
        total = sum of all nums
        if total is odd:
            return false
        target = total / 2

        let n = length of nums
        create a boolean table dp of size (n+1) by (target+1)
        for i from 0 to n:                          # base: sum 0 is always reachable (empty subset)
            dp[i][0] = true

        for i from 1 to n:
            for t from 1 to target:
                dp[i][t] = dp[i-1][t]               # skip nums[i-1]
                if t >= nums[i-1]:
                    dp[i][t] = dp[i][t] or dp[i-1][t - nums[i-1]]   # or include nums[i-1]

        return dp[n][target]

The 1-D space-optimised form collapses the `i` axis into a single row and runs
the inner `t` loop **descending** so that `dp[t - nums[i-1]]` still reflects the
*previous* row at the moment it is read -- that descending direction is what
guarantees each number is used at most once. The 2-D version below is kept for
clarity; the optimisation is a one-line change.

## Java Solution

```java
class Solution {
    public boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        if (total % 2 != 0) {
            return false;
        }
        int target = total / 2;

        int n = nums.length;
        boolean[][] dp = new boolean[n + 1][target + 1];
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            for (int t = 1; t <= target; t++) {
                dp[i][t] = dp[i - 1][t];
                if (t >= nums[i - 1]) {
                    dp[i][t] = dp[i][t] || dp[i - 1][t - nums[i - 1]];
                }
            }
        }

        return dp[n][target];
    }
}
```

The early `total % 2` check is the single biggest optimisation: an odd total
cannot be split, and we avoid allocating any table. With `target = total / 2`
fixed, the table is `(n+1) x (target+1)`, where row `i` answers "using only the
first `i` numbers". The base loop seeds column 0 with `true` because the empty
subset trivially hits sum 0. The recurrence is "skip OR include": `dp[i-1][t]`
covers skipping `nums[i-1]`, and when `t` is large enough we OR in
`dp[i-1][t - nums[i-1]]` to cover including it once. Returning `dp[n][target]`
asks whether all `n` numbers together can hit exactly half the total.

## Complexity

    Time:  O(n * target)  -- one constant-time check per (item, sum) pair; target = total/2.
    Space: O(n * target)  -- the dp table. The 1-D rolling form uses O(target) by
                              iterating t descending.

## Dry-Run

On `nums = [1, 5, 11, 5]` (expected `true`). `total = 22`, even, `target = 11`.
The table is `5 x 12` (rows `0..4`, columns `0..11`). Column 0 is all `true`;
every other cell starts `false`.

Row by row (`T` = true, `.` = false):

```
Row 0 (no items):  T . . . . . . . . . . .     # only sum 0 reachable
Row 1 (item 1):    T T . . . . . . . . . .     # can hit {1}
Row 2 (item 5):    T T . . . . T T . . . .     # add {5}, {1,5}
Row 3 (item 11):   T T . . . . T T . . . T     # add {11} and {1,11}
Row 4 (item 5):    T T . . . . T T . . . T     # adds {1,5,5} (col 11) -- see below
```

Two cells worth tracing in the final row (`item 5`, the second `5`):

| (i, t) | skip `dp[i-1][t]` | include `dp[i-1][t-5]` | result |
|:------:|:-----------------:|:----------------------:|:------:|
| (4, 5) | `dp[3][5]` = T    | `dp[3][0]` = T         | T      |
| (4, 10) | `dp[3][10]` = .  | `dp[3][5]` = T         | **T**  |
| (4, 11) | `dp[3][11]` = T  | `dp[3][6]` = T         | **T**  |

`dp[4][11] = true`, the answer. The witnessing subset is `{1, 5, 5}` (the `1`
from row 1, plus the two `5`s): it sums to `11`, exactly half of `22`, and the
complement `{11}` makes the other half.

### Checkpoint B -- Trace a fresh input

**Q1 (apply).** Trace `nums = [1, 2, 5]` (`total = 8`, `target = 4`). After processing the first two numbers (`1` and `2`), what is `dp[2][3]`?
- a) true (`{1, 2}` sums to 3)
- b) false
- c) true only if `3` itself is in the array

<details><summary>Show answer</summary>

**(a)** -- using just `{1, 2}` we can hit sum 3 as `{1, 2}`, so the skip-OR-include recurrence sets `dp[2][3] = dp[1][3] OR dp[1][1] = false OR true = true`.

</details>

**Q2 (analyze).** In the 1-D space-optimised version, why must the inner `t` loop run DESCENDING?
- a) So that large sums are computed before small ones
- b) So that `dp[t - nums[i-1]]` still reflects the previous row when read, preventing any number from being spent twice
- c) To make the loop run faster

<details><summary>Show answer</summary>

**(b)** -- descending reads still-unupdated slots from the previous row, so each number is included at most once; ascending would let the same number reuse itself within one pass.

</details>

**Q3 (transfer).** How would the approach change if each number could be used an UNLIMITED number of times (still asking "can we hit `target = total/2`")?

<details><summary>Show answer</summary>

It becomes unbounded knapsack: flip the inner loop to ASCENDING so `dp[t - nums[i-1]]` may include the current number again. The recurrence stays "skip OR include"; only the direction flips, exactly as in Coin Change II.

</details>

## Common mistakes

- Skipping the odd-total check. The recurrence still runs but never finds a
  valid `target`, wasting `O(n * total)` time before returning `false`.
- Using `dp[i-1][t - nums[i-1]]` without the `t >= nums[i-1]` guard. The column
  goes negative and throws `ArrayIndexOutOfBoundsException`.
- Running the inner `t` loop **ascending** in the 1-D optimised form. Then
  `dp[t - nums[i-1]]` already includes the current item, and a single `5` can
  be "spent" twice -- you would report `true` on inputs like `[5]` against
  `target = 10`. Descending prevents reuse.
- Initialising only `dp[0][0] = true` and forgetting the rest of column 0. Every
  row must allow the empty subset, so `dp[i][0] = true` for all `i`.
- Confusing this with Coin Change II. Both are knapsack, but here items are
  **0/1** (each number used at most once) while there each coin is unbounded --
  which flips the inner loop direction.

## Related problems

- [0518 - Coin Change II](../0518-coin-change-ii/) -- the unbounded sibling:
  items reusable, so the inner loop runs **ascending** and counts combinations.
- [0062 - Unique Paths](../0062-unique-paths/) -- another `(rows, columns)` 2-D
  table, but with an additive recurrence instead of an OR.
