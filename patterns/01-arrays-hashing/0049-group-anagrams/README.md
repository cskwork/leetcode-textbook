# 0049 - Group Anagrams

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/group-anagrams/

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

The trigger signal is "group ... by". The only question is: group *by what key*? Two words are
anagrams exactly when they share the same multiset of letters. So if we derive a canonical
signature for each word -- one that is identical for every anagram of it -- we can use that
signature as a HashMap key and let every word drop into its bucket. The simplest signature is the
word with its letters sorted: `"eat"`, `"tea"`, `"ate"` all sort to `"aet"`. One pass, one
`computeIfAbsent` per word, and the map's values are the answer.

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
