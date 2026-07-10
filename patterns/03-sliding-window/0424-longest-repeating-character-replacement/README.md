# 0424 - Longest Repeating Character Replacement

**Difficulty:** Medium
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/longest-repeating-character-replacement/

## Concepts used

- **Array** (as a frequency table) -- a row of numbered slots; here 26 slots, one per uppercase letter, each holding that letter's count inside the window. [glossary](../../../docs/10-glossary.md#array)
- **Sliding window** -- expand `right` to grow the window; when a budget is exceeded, shrink `left` until it is valid again. [glossary](../../../docs/10-glossary.md#sliding-window)
- **Invariant** -- a condition always true at each loop step; here, "the window needs at most `k` replacements to become one repeated letter". [glossary](../../../docs/10-glossary.md#invariant)

## Problem

You are given a string `s` consisting of **uppercase English letters** and an integer
`k`. You may perform at most `k` operations on any substring of `s`; each operation
changes one character in the substring to any other uppercase letter. Return the length
of the **longest substring** that can be turned into a string of all identical letters
using at most `k` changes.

Signature:

    int characterReplacement(String s, int k)

Example 1:

    Input:  s = "ABAB", k = 2
    Output: 4
    (Replace either the two A's or the two B's; the whole string becomes one letter.)

Example 2:

    Input:  s = "AABABBA", k = 1
    Output: 4
    (Replace the one B in the middle to get "AABABBA" -> "AAAA" of length 4.)

## Intuition

You have a row of letters and a budget of `k` erasers. One eraser turns one letter into any
other letter. You want the longest stretch of the row you can turn into all-the-same-letter
using at most `k` erasers. Slide a magnifying glass across the row; whenever making the
letters inside all match would cost more than `k` erasers, slide the glass's left edge
forward until you are back under budget.

The key insight: for any window, the fewest changes needed to make every letter the same is

    (window length) - (count of the most common letter in the window)

because you keep the most common letter and erase all the others. So a window is "valid"
exactly when `window length - max count <= k`.

Smallest example, `s = "ABAB", k = 2`:

- right=0 'A': window "A", max count 1, cost 1 - 1 = 0 <= 2. Length 1.
- right=1 'A': "AB", max count 1, cost 2 - 1 = 1 <= 2. Length 2.
- right=2 'B': "ABA", max count 2, cost 3 - 2 = 1 <= 2. Length 3.
- right=3 'B': "ABAB", max count 2, cost 4 - 2 = 2 <= 2. Length 4. Best = 4.

The second example, `s = "AABABBA", k = 1`, gives best 4 (replace one middle 'B' to get a
window of four 'A's).

General rule: keep a frequency array of 26 counts. Expand `right` (add one letter, bump its
count). While `window length - max count > k`, shrink `left` (remove one letter, drop its
count). After each step the window is valid, so update the best length.

The **invariant** is: *after the shrink loop, the window needs at most `k` replacements*. We
only stop shrinking once the cost is back within budget, so every `best` update sees a valid
window. Because the alphabet is fixed at 26 letters, recomputing the max count each step is
O(26) -- constant time -- and keeps the budget test honest.

This is a variable-size sliding window like [0003 Longest Substring Without Repeating Characters](../0003-longest-substring-without-repeating-characters/),
but the validity check counts frequencies instead of testing for zero repeats.

### Checkpoint A -- The replacement budget

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** A window is "valid" (needs at most `k` replacements) exactly when which condition holds?
- a) `window length == max count`
- b) `window length - max count <= k`
- c) `max count <= k`

<details><summary>Show answer</summary>

**(b)** -- keep the most frequent letter and erase all others, so the cost is `length - max count`; the window is valid while that cost stays within `k`.

</details>

**Q2 (comprehend).** Why use `max(freq)` (the most frequent letter's count) rather than `freq[s[right]]` (the just-arrived letter's count)?
- a) The optimal letter to keep is whichever is most common, which may not be the one that just arrived
- b) The current letter is always the most common
- c) It avoids an array lookup

<details><summary>Show answer</summary>

**(a)** -- to minimise replacements you keep the majority letter. Basing the cost on the current letter alone would over-count erasures whenever some other letter dominates the window.

</details>

## Pseudocode

    function characterReplacement(s, k):
        freq = array of 26 zeros                 # frequency of each letter in the window
        left = 0
        best = 0
        for right from 0 to len(s)-1:
            freq[index of s[right]] += 1         # EXPAND
            while (right - left + 1) - max(freq) > k:    # SHRINK while over budget
                freq[index of s[left]] -= 1
                left = left + 1
            best = max(best, right - left + 1)   # window is now valid; record it
        return best

The shrink loop runs while the window violates the budget; once it stops, the window is
valid, and `best` is updated -- exactly the variable-window template.

## Java Solution

```java
class Solution {
    public int characterReplacement(String s, int k) {
        int[] freq = new int[26];
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            freq[s.charAt(right) - 'A']++;
            while ((right - left + 1) - maxFreq(freq) > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    private int maxFreq(int[] freq) {
        int m = 0;
        for (int c : freq) {
            m = Math.max(m, c);
        }
        return m;
    }
}
```

A fixed `int[26]` is simpler and faster than a `HashMap` because the alphabet is bounded
and known up front. `maxFreq` scans all 26 letters each call, which is O(1) and keeps the
window's state honest: after every shrink the maximum is recomputed from real data, so
the budget test is always exact. The window size is `right - left + 1` (both ends
inclusive). This straightforward version mirrors the variable-window template exactly;
an elegant O(n) optimisation (track a single running `maxCount` that never decreases, and
slide `left` by one instead of looping) is mentioned in the common-mistakes section.

## Complexity

    Time:  O(n)   -- `right` advances n times; `maxFreq` is O(26) = O(1) per step, and
                     each index is shrunk out at most once.
    Space: O(1)   -- the frequency array is fixed size 26.

## Dry-Run

Step-by-step on `s = "AABABBA", k = 1` (A=0, B=1):

| right | ch | freq after add       | max | win size | win - max | > 1? | shrink actions                                                | left (end) | best |
|-------|----|----------------------|-----|----------|-----------|------|---------------------------------------------------------------|------------|------|
| init  | -  | [0,0]                | -   | -        | -         | -    | -                                                             | 0          | 0    |
| 0     | A  | [1,0]                | 1   | 1        | 0         | no   | -                                                             | 0          | 1    |
| 1     | A  | [2,0]                | 2   | 2        | 0         | no   | -                                                             | 0          | 2    |
| 2     | B  | [2,1]                | 2   | 3        | 1         | no   | -                                                             | 0          | 3    |
| 3     | A  | [3,1]                | 3   | 4        | 1         | no   | -                                                             | 0          | 4    |
| 4     | B  | [3,2]                | 3   | 5        | 2         | yes  | freq[s[0]=A]-- -> [2,2]; left=1; win=4; 4-2=2>1 yes; freq[s[1]=A]-- -> [1,2]; left=2; win=3; 3-2=1>1 no, stop | 2 | 4 |
| 5     | B  | [1,3]                | 3   | 4        | 1         | no   | -                                                             | 2          | 4    |
| 6     | A  | [2,3]                | 3   | 5        | 2         | yes  | freq[s[2]=B]-- -> [2,2]; left=3; win=4; 4-2=2>1 yes; freq[s[3]=A]-- -> [1,2]; left=4; win=3; 3-2=1>1 no, stop | 4 | 4 |

Return `4`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `s = "ABBC", k = 1`. What is returned?
- a) 2
- b) 3
- c) 4

<details><summary>Show answer</summary>

**(b)** -- right=0..2 build "ABB" (max count 2, cost 1, best 3). At right=3 'C' the window "ABBC" has cost 4-2=2 > 1, so `left` shrinks past 'A' to give window "BBC" of size 3 (cost 3-2=1). Best stays 3.

</details>

**Q2 (analyze).** Trace `s = "AAAA", k = 2`. Does any shrink happen, and what is returned?
- a) No shrink ever fires; cost is always 0, so best climbs to 4
- b) It shrinks at every step and returns 1
- c) It returns 2

<details><summary>Show answer</summary>

**(a)** -- every added 'A' keeps max count equal to the window length, so `length - max = 0 <= 2` at all times; the while-loop body never runs and best reaches 4.

</details>

**Q3 (transfer).** How would you adapt the solution for a string of lowercase letters (a-z) plus digits?

<details><summary>Show answer</summary>

Swap `int[26]` for a frequency container indexed by the actual character, e.g. `int[128]` indexed by the char's ASCII value (or a `HashMap<Character,Integer>`). The `window - maxFreq <= k` logic is identical; only the alphabet width changes.

</details>

## Common mistakes

- Tracking a single running `maxCount` (max frequency ever seen) but **also** using a
  `while` loop to shrink. The running-max trick only works with `if` (slide `left` by
  one) and only because the window never shrinks below its best-ever size; mixing the
  two records invalid windows. Either recompute `maxFreq` each step (this solution) or
  commit fully to the `if`-and-running-max version.
- Computing the replacement cost as `window - freq[s[right]]` (the current char's
  count) instead of `window - max(freq)`. The optimal letter to keep is whatever is most
  frequent, not whatever just arrived.
- Using a `HashMap<Character,Integer>` when `int[26]` is simpler, faster, and avoids
  autoboxing.
- Off-by-one on the window size (`right - left` instead of `right - left + 1`).
- Applying the algorithm to arbitrary alphabets without changing the frequency
  container; for lowercase or any-charset input, swap `int[26]` for a `HashMap` or
  `int[128]`.

## Related problems

- [0003 - Longest Substring Without Repeating Characters](../0003-longest-substring-without-repeating-characters/) --
  another "longest substring with a constraint" window, with a different validity check.
- [0076 - Minimum Window Substring](../0076-minimum-window-substring/) -- the capstone
  frequency-map window; combine everything learned here.
