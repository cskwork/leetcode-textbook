# 0076 - Minimum Window Substring

**Difficulty:** Hard (the canonical sliding-window capstone)
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/minimum-window-substring/

## Concepts used

- **Array** (as a frequency table) -- here two arrays of 128 slots (one per ASCII character) holding "how many of this letter does `t` need" and "how many are in the window". [glossary](../../../docs/10-glossary.md#array)
- **Sliding window** -- expand `right` to find any valid window, then shrink `left` to find the smallest valid one. [glossary](../../../docs/10-glossary.md#sliding-window)
- **Invariant** -- a condition always true at each loop step; here, "we always know exactly how many of `t`'s letters the window still lacks". [glossary](../../../docs/10-glossary.md#invariant)

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

You have a shopping list `t` (for example "1 A, 1 B, 1 C") and a long shelf `s`. You walk
along the shelf pushing a cart, grabbing a copy of any letter that is on your list. As soon
as the cart holds everything on the list, you start putting items *back* from the front of
the cart -- you want the smallest haul that still completes the list. This problem is the
hardest window in the pattern, so we go slowly.

One term first: when we say the window must contain every letter of `t` "including
duplicates", we mean the window must hold at least as many of each letter as `t` does --
`t = "aab"` needs two a's and one b, not just "an a and a b".

Smallest meaningful example, `s = "ADOBECODEBANC", t = "ABC"`. We need one A, one B, one C.

- Walk `right` forward, grabbing needed letters. At right=5 the window "ADOBEC" finally
  contains an A, a B, and a C -- valid! Length 6.
- Shrink from the left: dropping the first 'A' loses the A, so the smallest valid window
  ending here is the whole "ADOBEC", length 6.
- Keep walking. Around right=10 the window holds all three letters again; shrinking
  trims free letters (D, O, E) and surplus copies until the smallest valid window is
  "BANC", length 4.
- At right=12 another valid window appears, but nothing shorter than 4.
- Answer: "BANC".

General rule: keep two frequency arrays, `need` (built from `t`) and `window` (the current
window). Track `required` -- how many distinct letters `t` asks for -- and `formed` -- how
many of those the window currently satisfies. When `formed == required`, the window is
valid: record its length, then shrink `left` and keep recording as long as it stays valid.
Each letter enters and leaves the window at most once, so the whole scan is linear.

The trickiest detail is updating `formed` correctly. It ticks up *only* when a letter's
window count reaches its need exactly (`==`, never `>=` -- extra copies do not help). It
ticks down *only* when shrinking pushes a letter from "exactly enough" to "not enough".
Letters that are not on the shopping list are free: they ride along in the window but never
change `formed`, which is why every update is guarded by `need[ch] > 0`.

This combines ideas from [0424 Longest Repeating Character Replacement](../0424-longest-repeating-character-replacement/)
(a frequency-array window) and [0209 Minimum Size Subarray Sum](../0209-minimum-size-subarray-sum/)
(shrink to find the smallest valid window), but here we optimize for *shortest* rather than
*longest*.

### Checkpoint A -- The formed counter

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What do the two arrays `need` and `window` each track?
- a) `need` = frequencies of letters in `t`; `window` = frequencies of letters currently inside `[left..right]`
- b) `need` = indices of `t`; `window` = indices of `s`
- c) Both are copies of `s`

<details><summary>Show answer</summary>

**(a)** -- `need` is the shopping list (what `t` demands), `window` is the current cart (what the sliding frame holds).

</details>

**Q2 (comprehend).** Why does `formed` increment only on `window[ch] == need[ch]`, never on `>=`?
- a) Because `>=` would double-count letters not in `t`
- b) Because surplus copies of a char do not newly satisfy that char; only the exact transition from "not enough" to "enough" counts
- c) Because `>=` is slower to compute

<details><summary>Show answer</summary>

**(b)** -- a char is "satisfied" the instant its count reaches the requirement exactly. Extra copies beyond that add nothing, so counting them would let `formed` overshoot `required` and break the validity test.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `s = "cba", t = "ab"` (need A=1, B=1, required=2). What is returned?
- a) "cba"
- b) "ba"
- c) ""

<details><summary>Show answer</summary>

**(b)** -- 'c' is free; at 'b' formed=1; at 'a' formed=2 and the window "cba" is valid. Shrinking drops 'c' (free) then 'b' (pushes formed back to 1), so the smallest valid window recorded is "ba" of length 2.

</details>

**Q2 (analyze).** Trace `s = "a", t = "aa"`. Why does the code return `""` rather than throwing?
- a) `bestLen` stays at +infinity, never updated, so the final check returns `""`
- b) It throws `StringIndexOutOfBoundsException`
- c) `formed` reaches `required` and returns "a"

<details><summary>Show answer</summary>

**(a)** -- there is only one 'a', so `window['a']` (1) never equals `need['a']` (2), `formed` never reaches `required`=1, the while-loop never runs, and `bestLen` keeps `Integer.MAX_VALUE`, which the return translates to `""`.

</details>

**Q3 (transfer).** If `t` could contain duplicate letters (e.g. `t = "aab"` needing two a's), what part of the setup guarantees the requirement is tracked correctly?

<details><summary>Show answer</summary>

Building `need` as a frequency map already counts duplicates (need['a'] = 2), and `required` counts only distinct letters whose need is positive. No change to the algorithm is needed -- the `window[ch] == need[ch]` test enforces the right multiplicity automatically.

</details>

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
