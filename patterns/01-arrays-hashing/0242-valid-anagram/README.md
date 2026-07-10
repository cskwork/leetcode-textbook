# 0242 - Valid Anagram

**Difficulty:** Easy
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/valid-anagram/

## Concepts used

- **Array** -- a row of numbered slots, each holding one value, reached instantly by position.
  We use one slot per letter of the alphabet. [glossary](../../../docs/10-glossary.md#array)
- **Linear scan** -- walking a string one character at a time, from start to end.
  [glossary](../../../docs/10-glossary.md#linear-scan)
- **Sorting** -- putting items in order. Sorting both strings and comparing also solves this, but
  in O(n log n) instead of O(n). [glossary](../../../docs/10-glossary.md#sorting)

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

Two friends each get the same bag of Scrabble tiles, jumble them, and lay out a word. Are they
showing the same word? Reading left-to-right won't tell you, because the tiles are in different
orders. But tip both friends' tiles onto the table and sort each pile alphabetically: the two
sorted piles will look identical if and only if they started with the same letters. Two strings
work the same way -- they are anagrams exactly when sorting their characters gives the same
result.

Walk the smallest mismatch, `s = "rat"`, `t = "car"`. Count letters: `s` has one `r`, one `a`, one
`t`; `t` has one `c`, one `a`, one `r`. The `t` in `s` has no partner in `t`, and the `c` in `t`
has no partner in `s`, so they are not anagrams -- answer `false`.

The general rule: the real question is "do both strings contain the same letters in the same
amounts?" So count letters. Instead of sorting (which costs O(n log n)), use 26 tally marks -- one
per letter of the alphabet, stored as an [array](../../../docs/10-glossary.md#array) of 26 slots.
For each letter of `s`, add one to its tally; for each letter of `t`, subtract one. If the strings
truly are rearrangements, every tally lands back at zero; if even one tally is off, they differ.

Why does add-for-`s`, subtract-for-`t` work? Because a rearrangement means each letter appears the
same number of times in both strings. Adding and subtracting the same amount nets to zero, so a
matching pair cancels out. Any letter that appears a different number of times leaves a non-zero
tally, which is exactly the signal of "not anagrams".

### Checkpoint A -- The counting idea

Pause before expanding.

**Q1 (recall).** Why add 1 for each letter of `s` and subtract 1 for each letter of `t` into the same array?
- a) To sort both strings
- b) So matching letter-counts cancel to zero, exposing any mismatch
- c) To count the total number of letters

<details><summary>Show answer</summary>

**(b)** -- if both strings share the same letters in the same amounts, every increment is matched by a decrement and all slots end at 0. A non-zero slot means a mismatch.

</details>

**Q2 (comprehend).** Why does the code check `s.length() != t.length()` first and return `false`?
- a) It makes sorting faster afterward
- b) Different lengths can never be anagrams, and equality lets one combined loop index both strings safely
- c) To avoid a null-pointer exception

<details><summary>Show answer</summary>

**(b)** -- anagrams must have equal length, so a mismatch is an instant `false`. The equality also guarantees both strings can be indexed at the same `i` in one loop.

</details>

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

### Checkpoint B -- Trace and stretch

**Q1 (apply).** Using the count-array method, what are the final counts for `s = "aab"`, `t = "abb"`?
- a) All zeros
- b) `a:-1, b:+1`
- c) `a:+1, b:-1`

<details><summary>Show answer</summary>

**(c)** -- `s` adds a,a,b; `t` subtracts a,b,b. Net: a = +2-1 = +1, b = +1-2 = -1. Non-zero, so the answer is `false`.

</details>

**Q2 (analyze).** The `- 'a'` trick maps 'a'->0, 'b'->1, etc. What breaks if the input can contain uppercase letters or digits?
- a) Nothing, it still works
- b) `'A' - 'a'` is negative and a digit maps out of range, causing a bad index
- c) It just becomes slower

<details><summary>Show answer</summary>

**(b)** -- the 26-slot array assumes lowercase a-z only. Uppercase or digits index outside 0..25. For general input, fall back to a `HashMap<Character,Integer>`.

</details>

**Q3 (transfer).** How would you solve "group anagrams" (LC 49) using this counting idea as a building block?

<details><summary>Show answer</summary>

Turn each word's 26 counts (or its sorted letters) into a signature key, then drop every word sharing a signature into the same bucket. The count array becomes a key instead of a yes/no check.

</details>

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
