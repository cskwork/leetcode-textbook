# 0647 - Palindromic Substrings

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/palindromic-substrings/

## Problem

Given a string `s`, return the number of substrings of `s` that are
**palindromes**. A palindrome reads the same forwards and backwards. Substrings
are contiguous; single characters count.

Signature:

    int countSubstrings(String s)

Examples (verbatim from LeetCode):

    Input:  s = "abc"
    Output: 3
    Explanation: "a", "b", "c".

    Input:  s = "aaa"
    Output: 6
    Explanation: "a", "a", "a", "aa", "aa", "aaa".

## Intuition

The trigger is "is `s[i..j]` a palindrome" -- a question about a **subrange** of
the string, which is interval DP. The state `dp[i][j]` is true exactly when the
substring from index `i` to index `j` (inclusive) is a palindrome. The
recurrence reads a single neighbour, but a diagonal one: `s[i..j]` is a
palindrome when its two ends match (`s[i] == s[j]`) **and** the inside
`s[i+1..j-1]` is already known to be a palindrome. The only subtlety is the fill
order: because `dp[i][j]` depends on `dp[i+1][j-1]` (a row *below* it), the
usual top-down row sweep is illegal. You must fill either by substring length,
or with `i` descending and `j` ascending.

The shortcut for length 1 and 2: a single character is trivially a palindrome,
and two equal characters are too -- so the "inside palindrome" check is skipped
when `j - i < 2`.

## Pseudocode

    function countSubstrings(s):
        let n = length of s
        create a boolean table dp of size n by n
        count = 0

        for i from n-1 down to 0:                  # start descending so the inside is filled first
            for j from i up to n-1:                # end ascending; j >= i
                if s[i] equals s[j] and (j - i < 2 or dp[i+1][j-1] is true):
                    dp[i][j] = true
                    count = count + 1

        return count

## Java Solution

```java
class Solution {
    public int countSubstrings(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int count = 0;

        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i < 2 || dp[i + 1][j - 1])) {
                    dp[i][j] = true;
                    count++;
                }
            }
        }

        return count;
    }
}
```

The outer loop walks the **start** index `i` **downwards** and the inner loop
walks the **end** index `j` **upwards**, so when we reach cell `(i, j)` the
shorter inner substring `(i+1, j-1)` has already been resolved. The boolean
recurrence is one expression: the two ends must match, and either the substring
is so short that there is no "inside" to check (`j - i < 2`), or the inside has
already been marked palindromic. We count every cell we set true, so `count`
ends up holding the answer. The alternative "expand around centres" approach is
`O(1)` space, but this table version makes the DP structure explicit and shares
the shape you will see in every interval DP problem.

## Complexity

    Time:  O(n^2)   -- one constant-time check per (start, end) pair.
    Space: O(n^2)   -- the dp table. The expand-around-centres variant uses O(1)
                        extra space at the cost of recomputing overlaps.

## Dry-Run

On `s = "aaa"` (expected `6`). Indices are `0..2`, all `a`. Fill `i` descending
(2, then 1, then 0); within each `i`, `j` ascending.

| (i, j) | substring | `s[i]==s[j]` | inside `dp[i+1][j-1]` | palindrome? | count |
|:------:|:---------:|:------------:|:---------------------:|:-----------:|:-----:|
| (2, 2) | `a`       | yes          | (len 1, skipped)      | yes         | 1     |
| (1, 2) | `aa`      | yes          | (len 2, skipped)      | yes         | 2     |
| (1, 1) | `a`       | yes          | (len 1, skipped)      | yes         | 3     |
| (0, 2) | `aaa`     | yes          | `dp[1][1]` = true     | yes         | 4     |
| (0, 1) | `aa`      | yes          | (len 2, skipped)      | yes         | 5     |
| (0, 0) | `a`       | yes          | (len 1, skipped)      | yes         | 6     |

Notice `(0, 2)` is processed **before** `(0, 1)` and `(0, 0)` -- that is fine
because it depends only on `(1, 1)`, which the `i = 1` pass already filled. Final
table (`T` = true):

```
        j=0    j=1    j=2
i=0   [  T  ,  T  ,  T  ]
i=1   [  .  ,  T  ,  T  ]
i=2   [  .  ,  .  ,  T  ]
```

The upper triangle holds six `T` cells; `count = 6` is the answer.

## Common mistakes

- Filling `i` ascending. Then when you reach `(i, j)`, the inside `(i+1, j-1)`
  is in the row below, which you have not computed yet -- every length-3+
  palindrome is missed.
- Forgetting the `j - i < 2` shortcut. Length-1 substrings have no inside, and
  length-2 substrings are palindromes iff the two chars match -- the general
  recurrence alone would read `dp[i+1][j-1]` with `i+1 > j-1`, an empty range
  that must be treated as trivially true.
- Iterating `j` from `0` instead of from `i`. Cells below the diagonal (`j < i`)
  are meaningless (empty substring) and waste time; start at `j = i`.
- Counting each palindrome once per centre instead of once per `(i, j)` cell.
  The table gives you exactly one entry per substring, so a single `count++`
  inside the `if` is correct.

## Related problems

- [1143 - Longest Common Subsequence](../1143-longest-common-subsequence/) --
  the other two-axis table in this pattern, but filled top-down (no diagonal
  dependency from below).
- [0062 - Unique Paths](../0062-unique-paths/) -- the same "fill a 2-D table"
  reflex, with the dependency always pointing up and left.
