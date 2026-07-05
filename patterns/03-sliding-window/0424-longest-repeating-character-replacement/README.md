# 0424 - Longest Repeating Character Replacement

**Difficulty:** Medium
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/longest-repeating-character-replacement/

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

Trigger signal: "longest substring with a constraint". For any window, the minimum
number of replacements needed to make it all one letter is

    (window length) - (count of the most frequent letter in the window)

because the most frequent letter is the one worth keeping; everything else must change.
So the problem becomes: find the longest window for which that quantity is `<= k`. A
variable-size window slides across `s`; whenever the replacement cost exceeds `k`,
shrink from the left until it is back in budget. Because the alphabet is fixed at 26
uppercase letters, the "most frequent letter in the window" is an O(26) scan of a small
frequency array -- constant time per step.

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
