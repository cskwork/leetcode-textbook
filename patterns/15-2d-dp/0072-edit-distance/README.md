# 0072 - Edit Distance

**Difficulty:** Medium
**Pattern:** 2-D DP
**LeetCode:** https://leetcode.com/problems/edit-distance/

## Problem

Given two strings `word1` and `word2`, return the minimum number of operations
required to convert `word1` into `word2`. The allowed operations are:

- **Insert** one character,
- **Delete** one character,
- **Replace** one character.

Signature:

    int minDistance(String word1, String word2)

Examples (verbatim from LeetCode):

    Input:  word1 = "horse", word2 = "ros"
    Output: 3
    Explanation: horse -> rorse (replace h) -> rose (remove r) -> ros (remove e)

    Input:  word1 = "intention", word2 = "execution"
    Output: 5

## Intuition

This is the most famous two-string DP -- "edit distance" is one of the trigger
phrases in the overview table. The state mirrors LCS: `dp[i][j]` is the answer
for the first `i` characters of `word1` and the first `j` characters of `word2`.
But now `dp[i][j]` is the *minimum cost to transform* one prefix into the other,
and the recurrence has **three** candidate neighbours instead of two, one per
operation.

If the two current characters already match, no operation is needed -- the cost
is just `dp[i-1][j-1]`. Otherwise we try each operation and keep the cheapest:
**insert** a character (consumes one of `word2`, look left), **delete** a
character (consumes one of `word1`, look above), or **replace** (consumes one of
each, look diagonally). The base case: transforming into or out of the empty
string costs its full length (all inserts, or all deletes).

## Pseudocode

    function minDistance(word1, word2):
        let m = length of word1, n = length of word2
        create a dp table of size (m+1) by (n+1)

        for i from 0 to m:                          # base column: i deletions to reach empty word1
            dp[i][0] = i
        for j from 0 to n:                          # base row: j insertions to build word2 from empty
            dp[0][j] = j

        for i from 1 to m:
            for j from 1 to n:
                if word1[i-1] equals word2[j-1]:
                    dp[i][j] = dp[i-1][j-1]         # chars already agree -- free
                else:
                    insert  = dp[i][j-1]            # add word2[j-1] to word1
                    delete  = dp[i-1][j]            # remove word1[i-1]
                    replace = dp[i-1][j-1]          # swap word1[i-1] for word2[j-1]
                    dp[i][j] = 1 + min(insert, delete, replace)

        return dp[m][n]

## Java Solution

```java
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int insert = dp[i][j - 1];
                    int del = dp[i - 1][j];
                    int replace = dp[i - 1][j - 1];
                    dp[i][j] = 1 + Math.min(insert, Math.min(del, replace));
                }
            }
        }

        return dp[m][n];
    }
}
```

The base loops set the cost of converting to/from an empty string: `dp[i][0] = i`
(delete every char of `word1`) and `dp[0][j] = j` (insert every char of `word2`).
The `(m+1) x (n+1)` sizing keeps the recurrence uniform -- no special cases once
the base row and column are filled. On a character match the cost carries over
from the diagonal for free; on a mismatch we name the three operations explicitly
(`insert`, `del`, `replace`) and pick the cheapest plus one. Naming them is more
readable than cramming three `dp[...]` lookups into a single `Math.min`, and it
mirrors the prose explanation of the algorithm.

## Complexity

    Time:  O(m * n)  -- one constant-time decision per character pair.
    Space: O(m * n)  -- the dp table. Reducible to O(n) with a rolling row plus
                        a prev variable to preserve the diagonal.

## Dry-Run

On `word1 = "horse"`, `word2 = "ros"` (expected `3`). Rows index `word1` (rows
1..5 = `h o r s e`), columns index `word2` (cols 1..3 = `r o s`). After the base
loops the first row is `0 1 2 3` and the first column is `0 1 2 3 4 5`.

Filled table:

| `dp[i][j]` | j=0 | j=1 `r` | j=2 `o` | j=3 `s` |
|-----------:|:---:|:-------:|:-------:|:-------:|
| i=0        | 0   | 1       | 2       | 3       |
| i=1 `h`    | 1   | 1       | 2       | 3       |
| i=2 `o`    | 2   | 2       | 1       | 2       |
| i=3 `r`    | 3   | 2       | 2       | 2       |
| i=4 `s`    | 4   | 3       | 3       | 2       |
| i=5 `e`    | 5   | 4       | 4       | **3**   |

Two cells worth tracing:

- `dp[1][1]`: `h` vs `r` differ. `min(insert=dp[1][0]=1, del=dp[0][1]=1,
  replace=dp[0][0]=0) = 0`, plus 1 = `1`. (Replace `h` with `r`.)
- `dp[3][1]`: `r` vs `r` match. Cost is the diagonal `dp[2][0] = 2`. Free ride.
- `dp[5][3]`: `e` vs `s` differ. `min(insert=dp[5][2]=4, del=dp[4][3]=2,
  replace=dp[4][2]=3) = 2`, plus 1 = `3`. Delete the trailing `e` and we are
  done. Final answer `dp[5][3] = 3`.

## Common mistakes

- Omitting the match branch. If you always add 1 you charge for a replace that
  never happens on matching characters, over-counting the distance.
- Reading the wrong neighbour for an operation. Remember the mapping: **insert**
  looks **left** (`dp[i][j-1]`), **delete** looks **above** (`dp[i-1][j]`),
  **replace** looks **diagonal** (`dp[i-1][j-1]`). A common slip is to attach
  insert to the cell above.
- Setting the base row/column to `0` or to `i-1`. The cost of the empty
  conversion is exactly the length: `i` deletions or `j` insertions.
- Using `==` to compare `String` slices instead of `char`. We compare `char`
  values directly with `==` -- correct and fast -- but the same logic on
  `.substring(...)` would need `.equals(...)`.

## Related problems

- [1143 - Longest Common Subsequence](../1143-longest-common-subsequence/) --
  the same `(m+1) x (n+1)` scaffold; the diagonal `+1` reappears but only when
  the two characters match.
- [0064 - Minimum Path Sum](../0064-minimum-path-sum/) -- another "min over
  neighbours" recurrence, in a grid rather than across two strings.
