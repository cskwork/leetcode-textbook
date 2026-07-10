# 1143 - Longest Common Subsequence

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/longest-common-subsequence/

## Problem

Given two strings `text1` and `text2`, return the length of their **longest
common subsequence** (LCS). A subsequence is a sequence derived by deleting some
characters without changing the order of the remaining ones. The LCS need not
be contiguous.

Signature:

    int longestCommonSubsequence(String text1, String text2)

Examples (verbatim from LeetCode):

    Input:  text1 = "abcde", text2 = "ace"
    Output: 3
    Explanation: "ace" is the LCS.

    Input:  text1 = "abc", text2 = "abc"
    Output: 3

    Input:  text1 = "abc", text2 = "def"
    Output: 0

## Intuition

This is *the* textbook two-string DP. The trigger is right in the name -- two
strings, "longest common ...". The state needs two indices because a
sub-problem is "the LCS of the first `i` characters of text1 and the first `j`
characters of text2". Define `dp[i][j]` as exactly that, and the recurrence
falls out by looking at the last character of each prefix.

If the two last characters match, that character is part of the LCS -- extend
the diagonal. If they do not match, the LCS is the better of two smaller
sub-problems: drop text1's last character, or drop text2's last character,
whichever keeps the longer common subsequence. The empty prefix (row 0 or
column 0) matches nothing, so the base is all zeros.

### Checkpoint A -- The two-string state

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What does `dp[i][j]` represent?
- a) The LCS length of the first `i` characters of `text1` and the first `j` characters of `text2`
- b) Whether `text1[i]` equals `text2[j]`
- c) The total number of characters common to both strings

<details><summary>Show answer</summary>

**(a)** -- `dp[i][j]` is the longest common subsequence length between those two prefixes; (b) is just one character comparison and (c) ignores order.

</details>

**Q2 (comprehend).** When `text1[i-1] != text2[j-1]`, why is `dp[i][j] = max(dp[i-1][j], dp[i][j-1])`?
- a) Because a mismatch adds one to the LCS
- b) Because the mismatched characters cannot both belong to the LCS, so we carry forward the better of "drop text1's char" or "drop text2's char"
- c) Because the table must stay non-decreasing

<details><summary>Show answer</summary>

**(b)** -- at most one of the two mismatched chars can stay in the common subsequence, so the answer is the longer of the two smaller sub-problems that each drops one.

</details>

## Pseudocode

    function longestCommonSubsequence(text1, text2):
        let m = length of text1, n = length of text2
        create a dp table of size (m+1) by (n+1), filled with 0    # row 0 and col 0 are zeros

        for i from 1 to m:
            for j from 1 to n:
                if text1[i-1] equals text2[j-1]:                  # chars 1-indexed in dp, 0-indexed in string
                    dp[i][j] = dp[i-1][j-1] + 1                   # extend the diagonal match
                else:
                    dp[i][j] = max(dp[i-1][j], dp[i][j-1])        # carry the best neighbour

        return dp[m][n]

## Java Solution

```java
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }
}
```

The table is `(m+1) x (n+1)` so that `dp[0][*]` and `dp[*][0]` (the empty-prefix
row and column) are valid sentinel zeros -- no base loop needed, and the
recurrence works for every real cell. The `+1` shift between the table index and
the string index is the whole trick: `dp[i][j]` talks about `text1[0..i-1]`, so
the character under comparison is `text1.charAt(i - 1)`. On a match we extend the
diagonal `dp[i-1][j-1]`; on a mismatch we take the longer of "skip this char of
text1" (`dp[i-1][j]`) or "skip this char of text2" (`dp[i][j-1]`). The answer is
`dp[m][n]`, the full strings.

## Complexity

    Time:  O(m * n)  -- one cell per character pair.
    Space: O(m * n)  -- the dp table. Reducible to O(min(m, n)) with a rolling
                        row, since each row reads only the row above.

## Dry-Run

On `text1 = "abcde"`, `text2 = "ace"` (expected `3`). Rows are indexed by `text1`
(`a b c d e`), columns by `text2` (`a c e`). The table is `6 x 4` (the top row
and left column are the zero sentinels). Fill top-to-bottom, left-to-right:

| `dp[i][j]` | j=0 (empty) | j=1 `a` | j=2 `c` | j=3 `e` |
|-----------:|:-----------:|:-------:|:-------:|:-------:|
| i=0 (empty)| 0           | 0       | 0       | 0       |
| i=1 `a`    | 0           | **1**   | 1       | 1       |
| i=2 `b`    | 0           | 1       | 1       | 1       |
| i=3 `c`    | 0           | 1       | **2**   | 2       |
| i=4 `d`    | 0           | 1       | 2       | 2       |
| i=5 `e`    | 0           | 1       | 2       | **3**   |

Walk the three bolded matches to see the LCS building: `dp[1][1] = 1` matches
`a`; `dp[3][2] = 2` matches `c` (extends the diagonal from `dp[2][1] = 1`);
`dp[5][3] = 3` matches `e` (extends the diagonal from `dp[4][2] = 2`). The three
matched characters spell `a c e` -- the LCS itself. `dp[5][3] = 3` is the
answer.

### Checkpoint B -- Trace a fresh pair

**Q1 (apply).** Trace `text1 = "abc"`, `text2 = "ac"`. What is `dp[3][2]` (the answer)?
- a) 1
- b) 2
- c) 3

<details><summary>Show answer</summary>

**(b)** -- `dp[1][1] = dp[0][0] + 1 = 1` (`a` matches); `dp[2][*]` stays 1 (`b` matches nothing in `ac`); `dp[3][2] = dp[2][1] + 1 = 2` (`c` matches, extends the diagonal). The LCS is `"ac"`.

</details>

**Q2 (analyze).** On a match, why do we use `dp[i-1][j-1] + 1` instead of `max(dp[i-1][j], dp[i][j-1]) + 1`?
- a) The matched character extends the LCS of the two prefixes WITHOUT this character (the diagonal), not either shorter prefix separately
- b) The diagonal is always the largest value
- c) It saves memory

<details><summary>Show answer</summary>

**(a)** -- both matched chars are consumed together, so we extend the sub-problem that excludes both (the diagonal); adding `+1` to a neighbour that still holds one of the chars would double-count it.

</details>

**Q3 (transfer).** Suppose you must RETURN the actual LCS string, not just its length. In one sentence, how do you recover it?

<details><summary>Show answer</summary>

After filling the table, walk back from `dp[m][n]`: on a match, prepend the character and move diagonally to `(i-1, j-1)`; otherwise move to whichever neighbour (above or left) holds the larger value, until you reach a border.

</details>

## Common mistakes

- Forgetting the `-1` when indexing the strings. `dp[i][j]` describes the first
  `i` characters, so the last one is at `i - 1`. Writing `charAt(i)` reads one
  past the prefix and often throws or compares the wrong pair.
- Allocating an `m x n` table instead of `(m+1) x (n+1)`. Then the empty-prefix
  row and column do not exist, the base cases need a separate loop, and every
  index shifts -- easy to get wrong.
- On a match, taking `max(dp[i-1][j], dp[i][j-1]) + 1` instead of the diagonal.
  The matched character extends the LCS of the two prefixes **without** it
  (diagonal), not the LCS of either shorter pair.
- Using `==` on `String` substrings instead of `==` on `char`. We compare
  `char` values here, where `==` is correct -- but the same algorithm written
  with `substring().equals(...)` works and is slower.

## Related problems

- [0072 - Edit Distance](../0072-edit-distance/) -- the same `(m+1) x (n+1)`
  table, but mismatch produces three candidate neighbours (insert, delete,
  replace) instead of one.
- [0518 - Coin Change II](../0518-coin-change-ii/) -- another two-axis DP, but
  with axes "item index / capacity" instead of "two string prefixes".
