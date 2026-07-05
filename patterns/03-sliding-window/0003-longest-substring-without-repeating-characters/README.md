# 0003 - Longest Substring Without Repeating Characters

**Difficulty:** Medium
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/longest-substring-without-repeating-characters/

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

Trigger signal: "longest substring with property X", where X here is "all characters
distinct". Use a variable-size window `[left..right]` that always holds a substring with
no repeats. Expand `right`; when the new character is already inside the window, jump
`left` past the previous occurrence. The trick that makes this O(n) is to keep a hash
map from character to its most recent index, so a collision can be resolved in O(1) by
moving `left` directly, instead of shrinking one step at a time.

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
