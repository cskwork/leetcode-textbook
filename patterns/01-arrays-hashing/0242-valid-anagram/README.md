# 0242 - Valid Anagram

**Difficulty:** Easy
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/valid-anagram/

## Problem

Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise.
Two strings are anagrams if they contain the same characters with the same counts (i.e. one is a
rearrangement of the other). The strings contain only lowercase English letters.

Signature:

    boolean isAnagram(String s, String t)

Examples:

    Input:  s = "anagram", t = "nagaram"
    Output: true

    Input:  s = "rat", t = "car"
    Output: false

## Intuition

An anagram is a statement about *character counts*: the two strings match if and only if every
letter appears the same number of times in each. That is exactly the "counting / frequency"
trigger signal. The cleanest trick is to count the alphabet in a fixed 26-slot array: add one for
every letter of `s`, subtract one for every letter of `t`. If the two strings are anagrams the
net counts all cancel to zero; if any slot is non-zero they differ. Sorting both strings and
comparing also works but costs O(n log n); the count array is O(n).

## Pseudocode

    function isAnagram(s, t):
        if the lengths differ:
            return false                 # different lengths can never match
        create an array "counts" of 26 zeros, one slot per letter
        for each index i from 0 to length-1:
            increment counts[s[i]]
            decrement counts[t[i]]
        for each slot c in counts:
            if c is not 0:
                return false             # some letter appeared a different number of times
        return true

## Java Solution

```java
class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        int[] counts = new int[26];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }
        for (int c : counts) {
            if (c != 0) {
                return false;
            }
        }
        return true;
    }
}
```

A 26-element `int[]` replaces a `Map<Character, Integer>` -- it avoids autoboxing, never resizes,
and runs in true O(1) space because the alphabet is fixed. Subtracting `'a'` maps the letter to a
slot index (`'a' -> 0`, `'b' -> 1`, ...). The early length check lets us safely index both strings
at the same position in one combined loop. The two strings need not be sorted, so this is O(n)
where n is the string length.

## Complexity

    Time:  O(n)       -- one pass over the strings plus a fixed 26-slot scan
    Space: O(1)       -- the count array has constant size 26 regardless of input

## Dry-Run

Input `s = "anagram"`, `t = "nagaram"` (both length 7, so we proceed).

Walking i = 0..6, here are the net `counts` after each step (only non-zero slots shown):

| i | s[i] | t[i] | effect             | counts (a:?, n:?, g:?, r:?, m:?)                |
|--:|------|------|--------------------|------------------------------------------------|
| 0 | a    | n    | a+1, n-1           | a:1, n:-1                                       |
| 1 | n    | a    | n+1, a-1           | a:0, n:0                                        |
| 2 | a    | g    | a+1, g-1           | a:1, g:-1                                       |
| 3 | g    | a    | g+1, a-1           | a:0, g:0                                        |
| 4 | r    | r    | r+1, r-1           | (no change)                                     |
| 5 | a    | a    | a+1, a-1           | (no change)                                     |
| 6 | m    | m    | m+1, m-1           | (no change)                                     |

Final scan: every slot is 0. Output: `true`.

## Common mistakes

- Using `==` to compare characters or strings. Use `.charAt(i)` on `String` and compare `char`
  values with `==` (those are fine), but never compare whole `String`s with `==`.
- Returning `false` on the first non-zero slot during the *add* loop -- you must finish both
  passes; counts are only meaningful once both strings are fully processed.
- Forgetting the length check. Without it, a longer `t` can subtract its extra letters after `s`
  is exhausted and still leave zeros, masking the mismatch (the combined loop would also throw
  on a longer `t`).
- Allocating a `Map<Character, Integer>` for a fixed alphabet -- works, but the `int[26]` is
  faster, simpler, and uses less memory.
- Handling Unicode / uppercase. This problem guarantees lowercase a-z; the `- 'a'` trick depends
  on that. For a general version, fall back to a `HashMap`.

## Related problems

- [0049 - Group Anagrams](../0049-group-anagrams/) - reuse this counting idea as a *signature*
  key to cluster many words at once.
- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the same existence-at-O(1) habit in
  a simpler setting.
- [0347 - Top K Frequent Elements](../0347-top-k-frequent/) - counting is step one; this problem
  then asks which values are most frequent.
