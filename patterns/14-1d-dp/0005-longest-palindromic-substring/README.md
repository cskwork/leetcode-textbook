# 0005 - Longest Palindromic Substring

**Difficulty:** Medium
**Pattern:** 1-D DP (expand-around-center practical solution)
**LeetCode:** https://leetcode.com/problems/longest-palindromic-substring/

## Problem

Given a string `s`, return the **longest palindromic substring** of
`s`. A palindrome reads the same forwards and backwards.

Signature:

    String longestPalindrome(String s)

Examples (verbatim from LeetCode):

    Input:  s = "babad"
    Output: "bab"   (or "aba"; either is accepted)

    Input:  s = "cbbd"
    Output: "bb"

Constraints: `1 <= s.length <= 1000`, `s` consists of digits and
English letters.

## Intuition

This problem has a clean DP formulation (`dp[i][j]` = "is `s[i..j]` a
palindrome", a 2-D DP — see Pattern 15), but the *practical* solution
is **expand around center**, which is O(n^2) time and O(1) space.
That is what most interviewers want and what we present here. The DP
is mentioned at the end for completeness.

A palindrome mirrors around its center. There are `2n - 1` possible
centers in a string of length `n`: each character (odd-length
palindromes) and each gap between characters (even-length
palindromes). For each center we expand outward as long as both ends
match, recording the longest palindrome found. The single pass over
centers is the "1-D" index; the inner expansion is what replaces the
DP table.

Why is expand-around-center correct? Every palindrome has a unique
center, so iterating over all centers and greedily expanding is
guaranteed to find the longest one. We just keep the best.

## Pseudocode

```text
function longestPalindrome(s):
    if length of s is 0: return ""

    start = 0       # start index of the best palindrome found
    best  = 1       # length of the best palindrome found (at least 1: any single char)

    for center from 0 to length of s - 1:
        # Odd-length palindrome: center is a character.
        lenOdd  = expandAroundCenter(s, center, center)
        # Even-length palindrome: center is the gap between center and center+1.
        lenEven = expandAroundCenter(s, center, center + 1)
        len = max(lenOdd, lenEven)
        if len > best:
            best  = len
            start = center - (len - 1) / 2     # floor division; left end of the palindrome

    return s[start .. start + best)


# Expand outward while the two endpoints match; return the palindrome length.
function expandAroundCenter(s, left, right):
    while left >= 0 AND right < length of s AND s[left] == s[right]:
        left  = left - 1
        right = right + 1
    # When the loop exits, s[left] and s[right] no longer match (or we hit an edge),
    # so the actual palindrome is the slice (left+1 .. right-1), of length (right - left - 1).
    return right - left - 1
```

The `start` formula `center - (len - 1) / 2` works for both parities
because integer division floors. For an odd palindrome of length 3
centered at index 2, `start = 2 - 1 = 1`. For an even palindrome of
length 4 centered between indices 2 and 3, `start = 2 - 1 = 1` (covers
indices 1..4). Both correct.

## Java Solution

```java
class Solution {
    public String longestPalindrome(String s) {
        int n = s.length();
        if (n == 0) {
            return "";
        }
        int start = 0;
        int best = 1;
        for (int center = 0; center < n; center++) {
            int lenOdd = expand(s, center, center);
            int lenEven = expand(s, center, center + 1);
            int len = Math.max(lenOdd, lenEven);
            if (len > best) {
                best = len;
                start = center - (len - 1) / 2;
            }
        }
        return s.substring(start, start + best);
    }

    private int expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        // After the loop, s[left] != s[right] (or an edge was hit),
        // so the palindrome is (left+1 .. right-1), length (right - left - 1).
        return right - left - 1;
    }
}
```

`expand` takes two indices — equal for odd centers, adjacent for even
centers — and walks outward while characters match. The returned
length formula `right - left - 1` accounts for the loop having
over-stepped by one on each side. We update `start` only when a longer
palindrome is found, so the first-encountered maximum wins on ties
(this is why "babad" returns "bab" and not "aba" — both are length 3,
but the loop reaches center 1 first). `substring(start, start + best)`
extracts the answer in one allocation.

## Complexity

    Time:  O(n^2)   -- n centers, each expansion is O(n) in the worst case.
    Space: O(1)     -- only a few indices; the result substring is the only allocation.

For `n = 1000` this is at most ~1 million character comparisons — fast
in practice. The DP variant below is the same time complexity but uses
O(n^2) space, which is why expand-around-center is preferred.

### Note: the 2-D DP formulation

Define `dp[i][j] = true` if `s[i..j]` is a palindrome. Recurrence:
`dp[i][j] = (s[i] == s[j]) AND (j - i < 2 OR dp[i+1][j-1])`. Fill by
increasing substring length. Track the longest `true` entry. This is
O(n^2) time and O(n^2) space — correct but heavier than
expand-around-center, and it belongs more naturally to Pattern 15
(2-D DP). We mention it so you recognise it on sight.

## Dry-Run

Step-by-step on `s = "babad"`:

We track `(start, best)` initialised to `(0, 1)` (any single char is
a length-1 palindrome).

| center | expand(center, center) odd        | expand(center, center+1) even | best len here | new (start, best) |
|--------|-----------------------------------|-------------------------------|---------------|-------------------|
| 0 'b'  | "b" len 1                          | "ba" -> no match, len 0        | 1             | (0, 1) unchanged  |
| 1 'a'  | "bab" len 3                        | "ba"/"ab" no match, len 0      | 3             | (0, 3)            |
| 2 'b'  | "aba" len 3 (tie, not > 3)         | "ba"/"ab" no, len 0            | 3             | (0, 3) unchanged  |
| 3 'a'  | "a" len 1 (s[2]!=s[4]: 'b'!='d')   | "ad" no match, len 0           | 3             | (0, 3) unchanged  |
| 4 'd'  | "d" len 1                          | (right=5 out of range) len 0   | 3             | (0, 3) unchanged  |

Final `start = 0`, `best = 3` -> `s.substring(0, 3) = "bab"`.

Detail of `expand(s, 1, 1)` (odd, center 'a'):
- left=1, right=1: s[1]='a' == s[1]='a' -> left=0, right=2.
- left=0, right=2: s[0]='b' == s[2]='b' -> left=-1, right=3.
- left=-1 < 0: stop. Length = 3 - (-1) - 1 = 3.

Detail of `expand(s, 1, 2)` (even, between 'a' and 'b'):
- left=1, right=2: s[1]='a' != s[2]='b' -> stop. Length = 2 - 1 - 1 = 0.

## Common mistakes

- **Forgetting even-length centers.** A palindrome like `"bb"` has its
  center in the *gap* between two characters. If you only try
  `(center, center)` you miss all even-length palindromes. Always run
  both `(c, c)` and `(c, c+1)`.
- **Wrong length formula after expansion.** When the loop exits,
  `left` and `right` have already moved one step too far on each side.
  The palindrome is `(left+1 .. right-1)`, length `right - left - 1`.
  Returning `right - left + 1` is off by two.
- **Off-by-one in the `start` formula.** `start = center - (len - 1)
  / 2`. Using `center - len / 2` is wrong for even lengths (places
  the start one too far right).
- **Returning the wrong slice.** `substring(start, start + best)` —
  the second argument is exclusive. Writing `substring(start, best)`
  is a classic typo.
- **Initialising `best = 0`.** The empty string is not a valid answer
  for `s.length >= 1`; start with `best = 1` and `start = 0` so a
  single-character input returns that character.

## Related problems

- [0300 - Longest Increasing Subsequence](../0300-longest-increasing-subsequence/)
  - another "scan an index, do work at each position" pattern, here
    with an inner expansion instead of an inner DP scan.
- [0198 - House Robber](../0198-house-robber/) - a strict 1-D DP for
    contrast; this problem's DP formulation is 2-D.
- [0139 - Word Break](../0139-word-break/) - the other substring-heavy
    problem in this section, solved with a 1-D boolean DP.
