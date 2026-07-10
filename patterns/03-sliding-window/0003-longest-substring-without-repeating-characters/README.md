# 0003 - Longest Substring Without Repeating Characters

**Difficulty:** Medium
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/longest-substring-without-repeating-characters/

## Concepts used

- **Hash map** -- a key->value lookup table, O(1) average; here the key is a character and the value is its most recent index. [glossary](../../../docs/10-glossary.md#hash-map)
- **Sliding window** -- maintain a moving window `[left..right]` that always holds a valid substring; expand `right`, jump `left` when a repeat appears. [glossary](../../../docs/10-glossary.md#sliding-window)
- **Invariant** -- a condition that is always true at the start of every loop iteration; here, "the window contains no repeated characters". [glossary](../../../docs/10-glossary.md#invariant)

## Problem

Given a string `s`, find the length of the **longest substring** (contiguous) that
contains no repeating characters.

Signature:

    int lengthOfLongestSubstring(String s)

Example 1:

    Input:  s = "abcabcbb"
    Output: 3
    (The answer is "abc", length 3.)

Example 2:

    Input:  s = "bbbbb"
    Output: 1

Example 3:

    Input:  s = "pwwkew"
    Output: 3
    (The answer is "wke", length 3. Note "pwke" is a subsequence, not a substring.)

## Intuition

Picture a magnifying glass sliding left-to-right along a row of letters. Inside the glass
you want every letter to be different. When the glass reaches a letter that is *already
inside it*, you slide the glass's left edge past the earlier copy so the duplicate
disappears, then keep going.

Two terms to nail down. A **substring** is a contiguous run of neighbours (like "abc" inside
"abcabcbb"), not a subset that skips letters. A **window** `[left..right]` means "the letters
at positions left, left+1, ..., right".

Smallest example, `s = "abcabcbb"`, walking `right` from 0:

- right=0 'a': window "a", no repeat. Length 1.
- right=1 'b': window "ab". Length 2.
- right=2 'c': window "abc". Length 3.
- right=3 'a': 'a' is already in the window (at position 0)! Jump `left` past it, to 1. Window "bca". Length 3.
- right=4 'b': 'b' is at position 1, now still inside (left=1). Jump `left` to 2. Window "cab". Length 3.
- right=5 'c': 'c' at 2, inside. Jump `left` to 3. Window "abc". Length 3.
- right=6 'b': 'b' last seen at 4, inside (left=3). Jump `left` to 5. Window "cb". Length 2.
- right=7 'b': 'b' last seen at 6, inside. Jump `left` to 7. Window "b". Length 1.

Best length reached: 3.

General rule: keep a hash map from each character to its most recent index. For each new
character at `right`: if we have seen it before AND its last position is still inside the
window (>= left), jump `left` to just past that position. Then record the character's new
position. The window is now guaranteed repetition-free, so update the best length.

The **invariant** we maintain is: *at the start of each step, the window `[left..right-1]`
has no repeated letters*. Adding the character at `right` either keeps the invariant (new
letter) or breaks it (a repeat inside the window) -- and we immediately restore it by
jumping `left` past the old copy, which removes the duplicate. We jump instead of shrinking
one step at a time because the map tells us exactly where the duplicate sits. The check
`prev >= left` matters: the map remembers every character ever seen, but only an occurrence
*still inside the window* forces a jump -- one that `left` has already passed must be ignored
(see the `"abba"` dry-run below).

This is the classic variable-size sliding window. [0219 Contains Duplicate II](../0219-contains-duplicate-ii/)
reuses the "hash structure tracks the recent window" reflex with a fixed-width window, and
[0424 Longest Repeating Character Replacement](../0424-longest-repeating-character-replacement/)
uses a window with a richer validity check.

### Checkpoint A -- The jump-left reflex

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What does the hash map store for each character?
- a) The total count of that character in the whole string
- b) The most recent index where that character was seen
- c) The length of the longest run ending at that character

<details><summary>Show answer</summary>

**(b)** -- each key maps to its most recent index, so a repeat can be located and `left` jumped straight past it in O(1).

</details>

**Q2 (comprehend).** On `s = "abba"`, when `right` reaches the final `'a'`, why does `left` stay put instead of jumping back?
- a) Because the map has no entry for 'a'
- b) Because the earlier 'a' sits at index 0, which is now left of `left`, so the `prev >= left` check is false
- c) Because the window is already invalid

<details><summary>Show answer</summary>

**(b)** -- the earlier 'a' lives at index 0, but `left` has already advanced to 2. Since 0 < 2, that occurrence is outside the window and must be ignored; jumping to it would shrink the window wrongly.

</details>

## Pseudocode

    function lengthOfLongestSubstring(s):
        last-seen = empty map from character to index
        left = 0
        best = 0
        for right from 0 to len(s)-1:
            ch = s[right]
            if ch in last-seen and last-seen[ch] >= left:
                left = last-seen[ch] + 1        # jump past the previous occurrence
            last-seen[ch] = right
            best = max(best, right - left + 1)
        return best

The check `last-seen[ch] >= left` is essential: the map remembers every character ever
seen, but only an occurrence **inside the current window** forces a jump. An occurrence
to the left of `left` is no longer in the window and must be ignored.

## Java Solution

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int left = 0;
        int best = 0;
        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            Integer prev = lastSeen.get(ch);
            if (prev != null && prev >= left) {
                left = prev + 1;
            }
            lastSeen.put(ch, right);
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
```

A `HashMap<Character, Integer>` stores the most recent index of each character; this lets
`left` jump straight past a repeat rather than creeping one step at a time (which would
still be O(n) overall, but the jump is cleaner). `prev` is typed `Integer` so that
`null` ("never seen") is distinguishable from index `0`. The guard `prev >= left` ignores
occurrences that are already outside the window -- without it, inputs like `"abba"`
mis-jump `left` backwards and produce wrong answers. `best` is updated every step because
every window after the (possible) jump is guaranteed to be repetition-free.

## Complexity

    Time:  O(n)   -- `right` advances n times; each map op is O(1) amortized.
    Space: O(k)   -- the map holds at most k entries, where k is the alphabet size
                     (min(n, charset size); 65536 for full Java char).

## Dry-Run

Step-by-step on `s = "abcabcbb"`:

| right | ch | prev (last-seen[ch]) | prev >= left? | left (after) | last-seen[ch]=right | best |
|-------|----|----------------------|---------------|--------------|---------------------|------|
| init  | -  | -                    | -             | 0            | -                   | 0    |
| 0     | a  | null                 | -             | 0            | a:0                 | 1    |
| 1     | b  | null                 | -             | 0            | b:1                 | 2    |
| 2     | c  | null                 | -             | 0            | c:2                 | 3    |
| 3     | a  | 0                    | 0 >= 0 yes    | 1            | a:3                 | 3    |
| 4     | b  | 1                    | 1 >= 1 yes    | 2            | b:4                 | 3    |
| 5     | c  | 2                    | 2 >= 2 yes    | 3            | c:5                 | 3    |
| 6     | b  | 4                    | 4 >= 3 yes    | 5            | b:6                 | 3    |
| 7     | b  | 6                    | 6 >= 5 yes    | 7            | b:7                 | 3    |

Return `3`.

A second trace on the tricky input `s = "abba"`, where ignoring `prev >= left` would
break:

| right | ch | prev | prev >= left? | left | best | window      |
|-------|----|------|---------------|------|------|-------------|
| 0     | a  | null | -             | 0    | 1    | "a"         |
| 1     | b  | null | -             | 0    | 2    | "ab"        |
| 2     | b  | 1    | 1 >= 0 yes    | 2    | 2    | "b"         |
| 3     | a  | 0    | 0 >= 2 **no** | 2    | 2    | "ba"        |

Without the `prev >= left` guard, step 3 would jump `left` to `1`, shrinking the window
to "a" instead of correctly staying at "ba".

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `s = "abcb"`. What length is returned?
- a) 2
- b) 3
- c) 4

<details><summary>Show answer</summary>

**(b)** -- "abc" gives best 3; at index 3 the second 'b' was last seen at index 1 (still inside), so `left` jumps to 2, leaving window "cb" of length 2. Best stays 3.

</details>

**Q2 (analyze).** If you deleted the `prev >= left` guard, on which input would the answer first go wrong?
- a) "abc"
- b) "abba"
- c) "aaaa"

<details><summary>Show answer</summary>

**(b)** -- at the final 'a' the stale index 0 would drag `left` back to 1, shrinking the valid window "ba" down to "a". Inputs where no old occurrence is left behind are unaffected.

</details>

**Q3 (transfer).** Suppose the question asked for the substring itself, not just its length. What one piece of bookkeeping would you add?

<details><summary>Show answer</summary>

Track a `bestStart` index: whenever a new best length is found, save the current `left`. Return `s.substring(bestStart, bestStart + best)` (empty string if best is 0). The scan logic is unchanged.

</details>

## Common mistakes

- Skipping the `prev >= left` check and jumping `left` backwards on inputs like
  `"abba"` or `"tmmzuxt"`.
- Forgetting to update the map after the jump, so the next repeat reads a stale index.
- Returning `right - left` instead of `right - left + 1`.
- Using a `HashSet` and shrinking one index at a time -- correct but slower to reason
  about than the index-jump version.
- Using `==` to compare characters stored as boxed `Character` values; the map returns
  an index, so this does not bite here, but it bites the analogous HashSet approach.

## Related problems

- [0219 - Contains Duplicate II](../0219-contains-duplicate-ii/) -- the same
  "hash structure tracking recent positions" reflex, for a fixed-distance window.
- [0424 - Longest Repeating Character Replacement](../0424-longest-repeating-character-replacement/) --
  another "longest substring with constraint" window, with a richer validity check.
