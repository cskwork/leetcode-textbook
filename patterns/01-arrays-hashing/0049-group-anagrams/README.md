# 0049 - Group Anagrams

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/group-anagrams/

## Concepts used

- **Hash map** -- a key->value lookup table; here each "value" is a whole list, so the map becomes
  a set of labeled buckets.
  [glossary](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary)
- **Sorting** -- putting items in order; we sort each word's letters to produce its signature.
  [glossary](../../../docs/10-glossary.md#sorting)

## Problem

Given an array of strings `strs`, group the anagrams together. Return the answer in any order. An
anagram is a word formed by rearranging the letters of another (same letters, same counts).

Signature:

    List<List<String>> groupAnagrams(String[] strs)

Examples:

    Input:  strs = ["eat","tea","tan","ate","nat","bat"]
    Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
            # order of groups and order within a group do not matter

    Input:  strs = [""]
    Output: [[""]]

## Intuition

A mailroom has a wall of labeled bins. Each incoming letter carries a ZIP code; the clerk reads it
and drops the letter into the matching bin. At day's end every bin holds all the letters that
share a ZIP. Grouping anagrams is the same chore -- we just need a "ZIP code" that is identical
for every anagram of the same word.

Walk the smallest case, `words = ["eat", "tea", "ate"]`. Sort each word's letters: `"eat"` ->
`"aet"`, `"tea"` -> `"aet"`, `"ate"` -> `"aet"`. All three share the sorted form `"aet"`, so they
all drop into one bin and form a single group.

The general rule: two words are anagrams exactly when sorting their letters gives the same result.
Call that sorted form the word's signature -- a single agreed-on shape that every anagram of the
word shares. For each word, compute its signature by
[sorting](../../../docs/10-glossary.md#sorting) its letters, then drop the word into a
[hash map](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary) bucket keyed by that
signature. The map's buckets, read out at the end, are the grouped anagrams.

Why is sorting the letters a valid signature? Because sorting throws away the original order but
keeps exactly which letters are present and how many of each -- and that is precisely what
"anagram" means. Any two anagrams, however scrambled, sort to the identical string; any two
non-anagrams differ in at least one letter and sort differently.

### Checkpoint A -- The signature idea

Pause before expanding.

**Q1 (recall).** What "signature" do we compute for each word so that all its anagrams share it?
- a) The word itself
- b) Its letters sorted
- c) Its length

<details><summary>Show answer</summary>

**(b)** -- sorting the letters throws away order but keeps exactly which letters and how many -- the precise definition of "anagram". The word itself (a) would never group differently-spelled anagrams.

</details>

**Q2 (comprehend).** Why is a hash map from signature -> list the right structure, rather than comparing every pair of words?
- a) Maps are required by Java
- b) One pass drops each word into its bucket in O(1) amortized; pairwise checks are O(n^2) word-comparisons
- c) To sort the output

<details><summary>Show answer</summary>

**(b)** -- the map buckets words by signature in a single pass. Pairwise anagram testing is O(n^2) and far too slow for large inputs.

</details>

## Pseudocode

    function groupAnagrams(words):
        create an empty map from signature -> list of words, called "groups"
        for each word in words:
            signature <- the letters of word, sorted
            append word to groups[signature]          # create the list if absent
        return all the lists in groups, as a list of lists

## Java Solution

```java
import java.util.*;

class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : strs) {
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groups.values());
    }
}
```

`computeIfAbsent(key, k -> new ArrayList<>())` is the idiomatic Java one-liner for "create the
bucket on first sight, then append" -- it replaces a manual contains/put dance. We sort a `char[]`
because `Arrays.sort` works on arrays, not `String`s, and `new String(chars)` converts back. The
map's value type is `List<String>` (interface on the left), instantiated with the diamond
`new ArrayList<>()`. `new ArrayList<>(groups.values())` copies the map's values into a `List`,
which is exactly the required return type. No ordering is imposed, which is fine because LeetCode
accepts any group order.

## Complexity

    Time:  O(n * k log k)  -- n words, each sorted in O(k log k); k = longest word length
    Space: O(n * k)        -- the map stores every word once

## Dry-Run

Input `strs = ["eat","tea","tan","ate","nat","bat"]`:

| word | sorted signature | groups after the step                                 |
|------|------------------|-------------------------------------------------------|
| eat  | "aet"            | {"aet": [eat]}                                        |
| tea  | "aet"            | {"aet": [eat, tea]}                                   |
| tan  | "ant"            | {"aet": [eat, tea], "ant": [tan]}                     |
| ate  | "aet"            | {"aet": [eat, tea, ate], "ant": [tan]}                |
| nat  | "ant"            | {"aet": [eat, tea, ate], "ant": [tan, nat]}           |
| bat  | "abt"            | {"aet": [eat, tea, ate], "ant": [tan, nat], "abt": [bat]} |

Return the three lists: `[[eat, tea, ate], [tan, nat], [bat]]` (order may vary).

### Checkpoint B -- Trace and extend

**Q1 (apply).** For `strs = ["tan", "nat"]`, what is the signature of each, and how many groups result?
- a) Both "ant", one group `["tan","nat"]`
- b) "tan" and "nat" differ, two groups
- c) Both "tan", one group

<details><summary>Show answer</summary>

**(a)** -- sorting "tan" and "nat" both give "ant", so they land in one bucket together.

</details>

**Q2 (analyze).** Why must the bucket key be a fresh `String` built from the sorted `char[]`, not the `char[]` itself?
- a) Arrays are faster
- b) Java's HashMap uses `.equals`/`hashCode`; arrays compare by identity, so two equal sorted arrays would NOT match as keys
- c) Strings use less memory

<details><summary>Show answer</summary>

**(b)** -- `char[]` inherits `equals` from Object (reference identity), so two separately-built equal arrays would be treated as different keys. A `String` compares by content.

</details>

**Q3 (transfer).** Sorting each word costs O(k log k). How could you build the signature in O(k) instead, and what's the trade-off?

<details><summary>Show answer</summary>

Count letters into a length-26 array and use that count-array (or its string form) as the key -- O(k) per word. The trade-off is a more complex key object; sorting is simpler to write and usually fast enough.

</details>

## Common mistakes

- Using the word itself as the key. Anagrams have different spellings, so they would never group.
- Comparing signatures with `==`. Always build the key as a fresh `String` and let the map's
  `.equals`-based lookup do the work (HashMap already does, but never roll your own `==`).
- Counting letters into a `Map<Character, Integer>` per word as the signature. Works, but a
  `Map` is not hashable and not a clean key; the sorted-`char[]` -> `String` is the standard.
- Mutating the original word. `toCharArray()` makes a copy, so the input array is untouched.
- O(n^2) pairwise anagram checks: for each word, scan all previous groups. Far too slow.

## Related problems

- [0242 - Valid Anagram](../0242-valid-anagram/) - the single-pair version; same counting idea.
- [0347 - Top K Frequent Elements](../0347-top-k-frequent/) - another map-then-collect pipeline.
- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the simplest "use the value as a
  key" warm-up.
