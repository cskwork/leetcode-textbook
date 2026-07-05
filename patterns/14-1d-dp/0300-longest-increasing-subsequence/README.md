# 0300 - Longest Increasing Subsequence

**Difficulty:** Medium
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/longest-increasing-subsequence/

## Problem

Given an integer array `nums`, return the **length** of the longest
strictly increasing subsequence (LIS). A subsequence is derived by
deleting some (possibly zero) elements without reordering the rest.

Signature:

    int lengthOfLIS(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [10,9,2,5,3,7,101,18]
    Output: 4
    Explanation: the LIS is [2,3,7,101] (length 4).

    Input:  nums = [0,1,0,3,2,3]
    Output: 4

    Input:  nums = [7,7,7,7,7,7,7]
    Output: 1

Constraints: `1 <= nums.length <= 2500`, `-10^4 <= nums[i] <= 10^4`.

## Intuition

Trigger: "longest subsequence with property X" where the property
(strict increase) is local to a pair. The brute force is "try every
subsequence" — `O(2^n)`. DP shrinks this by asking: for each position,
*what is the longest increasing subsequence that **ends** at that
position?*

Define `dp[i]` = "length of the longest strictly increasing
subsequence that **ends at index `i`** (and therefore includes
`nums[i]`)". To extend an existing subsequence into index `i`, we look
at every earlier index `j < i` with `nums[j] < nums[i]` — any of them
can be the predecessor, and we pick the longest:

    dp[i] = 1 + max over all j < i with nums[j] < nums[i] of dp[j]

If no `j < i` has `nums[j] < nums[i]`, the subsequence starts fresh at
`i`, so `dp[i] = 1`. Base case: every element is itself a length-1
subsequence, so initialise `dp[i] = 1` for all `i`. The answer is the
**max over all entries** of `dp[i]` (not `dp[n-1]`, because the LIS
need not end at the last element).

This is O(n^2) — for each `i` we scan all `j < i`. An O(n log n)
variant exists (patience sorting with binary search over tails);
see the note at the end.

## Pseudocode

```text
function lengthOfLIS(nums):
    n = length of nums
    create dp array of size n, every entry = 1     # single-element subsequence
    best = 1
    for i from 1 to n-1:
        for j from 0 to i-1:
            if nums[j] < nums[i]:
                dp[i] = max(dp[i], dp[j] + 1)
        best = max(best, dp[i])
    return best
```

The outer loop fixes the ending index `i`; the inner loop tries every
possible predecessor `j`. `best` tracks the maximum over all `dp[i]`
because the LIS may end anywhere.

## Java Solution

```java
class Solution {
    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        java.util.Arrays.fill(dp, 1);
        int best = 1;
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }
}
```

`Arrays.fill(dp, 1)` sets the base case (every index is at least a
length-1 subsequence). The inner loop only considers `j` where
`nums[j] < nums[i]` — strict inequality enforces "strictly
increasing"; changing it to `<=` would yield the longest
*non-decreasing* subsequence. `best` is updated inside the outer loop
(rather than scanning `dp` at the end) for clarity. The result is
O(n^2) in time and O(n) in space.

## Complexity

    Time:  O(n^2)   -- nested loops: for each i, scan all j < i.
    Space: O(n)     -- one dp array of size n.

For `n = 2500` this is ~3 million comparisons — well within limits.

### Note: the O(n log n) variant

A faster solution maintains a list `tails` where `tails[k]` is the
smallest possible tail value of any increasing subsequence of length
`k+1`. For each `nums[i]`, binary-search `tails` for the first entry
`>= nums[i]` and replace it (or append if `nums[i]` is larger than
all). The final length of `tails` is the LIS length. This is O(n log n)
and is the preferred solution in production, but it hides the DP
structure. The O(n^2) solution above is the one to teach first because
the recurrence is explicit.

## Dry-Run

Step-by-step on `nums = [10, 9, 2, 5, 3, 7, 101, 18]`:

Initial `dp = [1, 1, 1, 1, 1, 1, 1, 1]`.

| i | nums[i] | j scanned (nums[j] < nums[i])               | dp after i     | best |
|---|---------|---------------------------------------------|----------------|------|
| 0 | 10      | (none, base)                                | [1,1,1,1,1,1,1,1] | 1 |
| 1 | 9       | none < 9 among {10}                          | [1,1,1,1,1,1,1,1] | 1 |
| 2 | 2       | none < 2 among {10,9}                        | [1,1,1,1,1,1,1,1] | 1 |
| 3 | 5       | j=2 (nums[2]=2 < 5): dp[3] = dp[2]+1 = 2    | [1,1,1,2,1,1,1,1] | 2 |
| 4 | 3       | j=2 (nums[2]=2 < 3): dp[4] = dp[2]+1 = 2    | [1,1,1,2,2,1,1,1] | 2 |
| 5 | 7       | j=2,3,4 (2,5,3 < 7): max dp = 2 -> dp[5] = 3 | [1,1,1,2,2,3,1,1] | 3 |
| 6 | 101     | j=2,3,4,5 (2,5,3,7 < 101): max dp = 3 -> dp[6] = 4 | [1,1,1,2,2,3,4,1] | 4 |
| 7 | 18      | j=2,3,4,5 (2,5,3,7 < 18): max dp = 3 -> dp[7] = 4 | [1,1,1,2,2,3,4,4] | 4 |

Final `best = 4`. One LIS of length 4 is `[2, 3, 7, 101]` (or
`[2, 5, 7, 101]`, or `[2, 3, 7, 18]`). Note that `dp[6]` and `dp[7]`
both equal 4 — the LIS need not end at the last index.

## Common mistakes

- **Returning `dp[n-1]` instead of `max(dp)`.** The LIS may end at any
  index, not necessarily the last. Always track the running max.
- **Using `<=` instead of `<`.** That finds the longest
  *non-decreasing* subsequence, allowing equal elements. The problem
  says *strictly* increasing.
- **Forgetting to initialise `dp[i] = 1`.** Without it, isolated
  elements that have no smaller predecessor get `dp[i] = 0`, shrinking
  the answer.
- **Scanning `j` from `i` instead of `0`.** The inner loop must cover
  *all* earlier indices, not just adjacent ones. Subsequences are not
  contiguous.
- **Confusing "subsequence" with "subarray".** Subsequences allow
  gaps; subarrays do not. This problem permits gaps.

## Related problems

- [0322 - Coin Change](../0322-coin-change/) - another DP with an
  inner scan, but minimising over a fixed choice set.
- [0198 - House Robber](../0198-house-robber/) - the simplest
  two-predecessor DP; good warm-up before the n-predecessor LIS scan.
- [0139 - Word Break](../0139-word-break/) - a boolean reachability
  DP whose inner loop scans dictionary words.
