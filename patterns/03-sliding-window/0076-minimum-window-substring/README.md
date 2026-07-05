# 0076 - Minimum Window Substring

**Difficulty:** Hard (the canonical sliding-window capstone)
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/minimum-window-substring/

## Problem

Given two strings `s` and `t`, return the minimum-length contiguous substring of `s`
that contains every character of `t` (including duplicates). If no such substring
exists, return the empty string `""`.

Signature:

    String minWindow(String s, String t)

The test cases are generated so that the answer is unique.

Example 1:

    Input:  s = "ADOBECODEBANC", t = "ABC"
    Output: "BANC"

Example 2:

    Input:  s = "a", t = "a"
    Output: "a"

Example 3:

    Input:  s = "a", t = "aa"
    Output: ""
    (Not enough a's in s.)

## Intuition

This is the capstone of the pattern. The trigger signal is "shortest substring that
satisfies a frequency property". Use a variable-size window over `s`. The window is
"valid" when it contains at least the multiset of characters in `t`. Expand `right`
until valid; once valid, shrink `left` as far as possible while staying valid, and
record the smallest valid window seen.

The mechanic that makes this O(n) is a **formed counter**. Maintain two frequency
arrays, `need` (from `t`) and `window` (the current window). `required` is the number of
distinct characters that `t` needs (those with `need > 0`). `formed` is the number of
those characters whose window count has reached their need. When `formed == required`,
the window is valid. Each character enters and leaves the window at most once, so the
whole scan is linear.

## Pseudocode

    function minWindow(s, t):
        need = frequency map of every char in t
        required = number of distinct chars in t with need > 0
        window = empty frequency map
        formed = 0
        left = 0
        best-length = +infinity
        best-start = 0
        for right from 0 to len(s)-1:
            ch = s[right]
            if need[ch] > 0:                       # only t-chars matter
                window[ch] += 1
                if window[ch] == need[ch]:         # just satisfied this char
                    formed = formed + 1
            while formed == required:              # window is valid; try to shrink
                if (right - left + 1) < best-length:
                    best-length = right - left + 1
                    best-start = left
                out = s[left]
                if need[out] > 0:
                    if window[out] == need[out]:   # about to drop below need
                        formed = formed - 1
                    window[out] -= 1
                left = left + 1
        return "" if best-length == +infinity
                   else substring of s from best-start length best-length

Three subtleties to internalise:

1. `formed` increments only on the **exact** transition `window[ch] == need[ch]`. Use
   `==`, never `>=` -- surplus copies of a char do not advance `formed`.
2. `formed` decrements only when the shrinking step pushes a char **from `== need` to
   `< need`**. Removing a surplus copy (window had more than needed) does not change
   `formed`.
3. Characters not in `t` are "free": they ride along in the window without affecting
   `formed`, so the `need[out] > 0` guards skip them entirely.

## Java Solution

```java
class Solution {
    public String minWindow(String s, String t) {
        int[] need = new int[128];
        int[] window = new int[128];
        for (char c : t.toCharArray()) {
            need[c]++;
        }
        int required = 0;
        for (int n : need) {
            if (n > 0) required++;
        }

        int formed = 0;
        int left = 0;
        int bestLen = Integer.MAX_VALUE;
        int bestStart = 0;

        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (need[ch] > 0) {
                window[ch]++;
                if (window[ch] == need[ch]) {
                    formed++;
                }
            }
            while (formed == required) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestStart = left;
                }
                char out = s.charAt(left);
                if (need[out] > 0) {
                    if (window[out] == need[out]) {
                        formed--;
                    }
                    window[out]--;
                }
                left++;
            }
        }
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
    }
}
```

Two `int[128]` arrays (one slot per ASCII character) replace a `HashMap`; this avoids
autoboxing and makes the "only update if `need[ch] > 0`" guard trivial. `required`
counts distinct characters in `t`, while `formed` counts how many of those are currently
satisfied -- the window is valid exactly when the two are equal. `bestLen` starts at
`Integer.MAX_VALUE` ("shortest window" identity) and is translated to `""` at the end if
no valid window was found. Note the symmetry of the increment/decrement blocks: the
order of the three lines (test, decrement `formed`, decrement `window`) is mirrored on
entry (increment `window`, test, increment `formed`), which is what keeps `formed`
honest.

## Complexity

    Time:  O(n + m)  -- n = len(s), m = len(t). `right` and `left` each move at most n
                        steps; each does O(1) array work. Building `need` is O(m).
    Space: O(1)      -- the two frequency arrays are fixed size 128.

## Dry-Run

Step-by-step on `s = "ADOBECODEBANC"`, `t = "ABC"` (need: A=1, B=1, C=1; required=3):

| right | ch | formed (before) | window change             | valid? | shrink actions taken                                                                                                                | bestLen | bestStart | left (end) |
|-------|----|-----------------|---------------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------|---------|-----------|------------|
| 0     | A  | 0               | A:1 -> formed=1           | no     | -                                                                                                                                   | inf     | 0         | 0          |
| 1     | D  | 1               | (free)                    | no     | -                                                                                                                                   | inf     | 0         | 0          |
| 2     | O  | 1               | (free)                    | no     | -                                                                                                                                   | inf     | 0         | 0          |
| 3     | B  | 1               | B:1 -> formed=2           | no     | -                                                                                                                                   | inf     | 0         | 0          |
| 4     | E  | 2               | (free)                    | no     | -                                                                                                                                   | inf     | 0         | 0          |
| 5     | C  | 2               | C:1 -> formed=3           | yes    | best=6,start=0; out=A, formed->2, A:0; left=1                                                                                       | 6       | 0         | 1          |
| 6     | O  | 2               | (free)                    | no     | -                                                                                                                                   | 6       | 0         | 1          |
| 7     | D  | 2               | (free)                    | no     | -                                                                                                                                   | 6       | 0         | 1          |
| 8     | E  | 2               | (free)                    | no     | -                                                                                                                                   | 6       | 0         | 1          |
| 9     | B  | 2               | B:2 (no formed bump)      | no     | -                                                                                                                                   | 6       | 0         | 1          |
| 10    | A  | 2               | A:1 -> formed=3           | yes    | no improve (len 10); out=D (free), left=2; no improve (len 9); out=O (free), left=3; out=B, B->1 (still >= need, no formed bump), left=4; out=E (free), left=5; out=C, formed->2, C:0, left=6 | 6 | 0 | 6 |
| 11    | N  | 2               | (free)                    | no     | -                                                                                                                                   | 6       | 0         | 6          |
| 12    | C  | 2               | C:1 -> formed=3           | yes    | no improve (len 7); out=O (free), left=7; no improve (len 6); out=D (free), left=8; len 5 < 6 -> best=5,start=8; out=E (free), left=9; len 4 < 5 -> best=4,start=9; out=B, formed->2, B:0, left=10 | 4 | 9 | 10 |

Return `s.substring(9, 13)` = `"BANC"`.

## Common mistakes

- Incrementing `formed` with `window[ch] >= need[ch]` instead of `==`. Surplus copies
  would inflate `formed` past `required`, breaking the validity test.
- Decrementing `formed` in the wrong order, or without the `window[out] == need[out]`
  guard, so a surplus removal wrongly drops `formed`.
- Not guarding character updates with `need[ch] > 0`. Free characters would otherwise
  pollute `window` and -- if the guards on `formed` are wrong -- silently inflate it.
- Returning `s.substring(bestStart, bestStart + bestLen)` without checking the
  no-window case -> `StringIndexOutOfBoundsException` or a wrong non-empty string.
  Always translate `+infinity` to `""`.
- Tracking every character in a `HashMap` instead of `int[128]`. Correct, but slower
  and more error-prone for an ASCII-only problem.

## Related problems

- [0424 - Longest Repeating Character Replacement](../0424-longest-repeating-character-replacement/) --
  the sibling frequency-map window, optimising "longest" rather than "shortest".
- [0209 - Minimum Size Subarray Sum](../0209-minimum-size-subarray-sum/) -- a simpler
  "shortest valid window" where the validity check is a numeric sum, not a multiset.
