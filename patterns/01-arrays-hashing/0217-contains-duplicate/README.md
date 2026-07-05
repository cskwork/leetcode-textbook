# 0217 - Contains Duplicate

**Difficulty:** Easy
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/contains-duplicate/

## Problem

Given an integer array `nums`, return `true` if any value appears at least twice in the array,
and `false` if every element is distinct.

Signature:

    boolean containsDuplicate(int[] nums)

Examples:

    Input:  nums = [1,2,3,1]
    Output: true      # 1 appears twice

    Input:  nums = [1,2,3,4]
    Output: false     # every element is distinct

## Intuition

This is the canonical existence question: "have I seen this value before?" Scanning the array
with a second inner loop to compare each element against all earlier ones is O(n^2) -- far too
slow. A HashSet turns "have I seen x?" into an O(1) question. Walk left to right; the first
value already sitting in the set is your duplicate. This is the exact trigger signal "find
duplicate / appears twice" from the pattern overview.

## Pseudocode

    function containsDuplicate(nums):
        create an empty set called "seen"
        for each value x in nums:
            if x is already in seen:
                return true          # second sighting -> duplicate
            add x to seen
        return false                 # finished the loop, everything was distinct

## Java Solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int x : nums) {
            if (seen.contains(x)) {
                return true;
            }
            seen.add(x);
        }
        return false;
    }
}
```

We use a `HashSet` (not a `HashMap`) because we store only values, never data attached to them.
Membership is checked with `contains(x)` *before* `add(x)` so the current element is never
matched against itself -- harmless here, but the habit matters for Two Sum. The early
`return true` exits the instant a duplicate appears, so the average case is much faster than a
full scan. The default `return false` after the loop cleanly handles the empty and
single-element inputs.

## Complexity

    Time:  O(n)  -- one pass; each contains/add is O(1) average on a HashSet
    Space: O(n)  -- worst case (all distinct) the set holds every element

## Dry-Run

Input `nums = [2, 5, 1, 5]`:

| Step | x | seen (before) | Action            | seen (after) | Result |
|-----:|--:|---------------|-------------------|--------------|--------|
| 1    | 2 | {}            | 2 not seen; add   | {2}          | -      |
| 2    | 5 | {2}           | 5 not seen; add   | {2, 5}       | -      |
| 3    | 1 | {2, 5}        | 1 not seen; add   | {1, 2, 5}    | -      |
| 4    | 5 | {1, 2, 5}     | 5 IS seen         | -            | true   |

Output: `true`.

## Common mistakes

- Sorting first (`Arrays.sort`) then comparing neighbours. Correct but O(n log n); the hash
  solution is O(n) and is what interviewers expect.
- Using `seen.add(x)` and ignoring its boolean return, then *also* re-checking with `contains` --
  pick one idiom, do not do both.
- Returning `false` the moment something is *not* in the set. "Not a duplicate yet" is not the
  same as "no duplicates at all"; you must finish the loop.
- Crashing on empty or single-element input. The loop simply never hits a match and falls through
  to `return false`, which is the right answer.

## Related problems

- [0001 - Two Sum](../0001-two-sum/) - same "have I seen this value?" idea, but the set becomes a
  value -> index map.
- [0242 - Valid Anagram](../0242-valid-anagram/) - existence at the level of letter *counts*.
- [0128 - Longest Consecutive Sequence](../0128-longest-consecutive-sequence/) - loads every
  value into a set first, then asks a different question of it.
