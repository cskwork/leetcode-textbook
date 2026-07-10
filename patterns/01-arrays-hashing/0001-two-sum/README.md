# 0001 - Two Sum

**Difficulty:** Easy
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/two-sum/

## Concepts used

- **Hash map** -- a key->value lookup table: give it a key, get back its value instantly (O(1)).
  We store each seen number as a key and its index as the value.
  [glossary](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary)
- **Linear scan** -- walking the array one index at a time.
  [glossary](../../../docs/10-glossary.md#linear-scan)

## Problem

Given an array of integers `nums` and an integer `target`, return the indices of the two numbers
such that they add up to `target`. Each input has exactly one solution, and you may not use the
same element twice. The answer may be returned in any order.

Signature:

    int[] twoSum(int[] nums, int target)

Examples:

    Input:  nums = [2,7,11,15], target = 9
    Output: [0,1]      # nums[0] + nums[1] = 2 + 7 = 9

    Input:  nums = [3,2,4], target = 6
    Output: [1,2]      # nums[1] + nums[2] = 2 + 4 = 6

## Intuition

You're at a potluck and the host says "two dishes here cost exactly $9 of ingredients between
them -- find the pair." Pick up one dish; suppose it cost $2. You instantly know its partner must
cost $7, because `9 - 2 = 7`. You don't re-examine all the other dishes one by one; you just need
to remember "have I already picked up a $7 dish?"

Walk the smallest case, `nums = [2, 7, 11, 15]`, `target = 9`. Start at index 0, value `2`. Its
partner must be `9 - 2 = 7`. Have I seen a `7` earlier? No -- this is the first element. Remember:
"I saw a `2` at index 0." Move to index 1, value `7`. Its partner is `9 - 7 = 2`. Have I seen a
`2` earlier? Yes -- at index 0. So the answer is `[0, 1]`.

The general rule: for every number `x`, its required partner is `target - x` (call this the
complement). The only question is "has the complement already appeared to my left?" A
[hash map](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary) -- a key->value lookup
table that answers in O(1) -- settles that instantly, and we store each seen value together with
its index so the answer is ready when the partner turns up. Walk the array once; for each element,
ask the map for its complement; if the map has it, return both indices, otherwise file the current
value and index away and continue.

Why check the map BEFORE storing the current element? Suppose `nums = [3, 3]`, `target = 6`. At
index 0, value `3`, complement `3` -- the map is empty, so store `3 -> 0`. At index 1, value `3`,
complement `3` -- the map has `3 -> 0`, so return `[0, 1]`, two distinct indices. Had we stored
first, index 1 would match its own just-written entry and wrongly return `[1, 1]`.
Checking-before-storing guarantees the two indices are always different.

### Checkpoint A -- The complement trick

Pause before expanding.

**Q1 (recall).** For a value `x` and a `target`, what value must its partner have?
- a) `target + x`
- b) `target - x` (the complement)
- c) `x * 2`

<details><summary>Show answer</summary>

**(b)** -- the pair must sum to `target`, so the partner of `x` is `target - x`. We store each seen value and look up that complement.

</details>

**Q2 (comprehend).** Why does the map store value -> INDEX (and not just value, as a set would)?
- a) Indexing is faster
- b) Because the answer must return the two positions, so we need to recall WHERE the partner lives
- c) To keep the array sorted

<details><summary>Show answer</summary>

**(b)** -- a set could say "a partner exists" but not where. We need both indices for the return value, so the stored value is the index.

</details>

## Pseudocode

    function twoSum(nums, target):
        create an empty map from value -> index, called "seen"
        for each index i from 0 to length-1:
            complement <- target - nums[i]
            if the complement is a key in seen:
                return [seen[complement], i]      # earlier index, current index
            store nums[i] -> i in seen
        return an empty pair                       # problem guarantees a solution; this is a safety net

## Java Solution

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[]{};
    }
}
```

A `HashMap<Integer, Integer>` maps each seen value to its index -- a set is not enough because we
need to report *where* the partner lives. The lookup happens *before* the insert on purpose: if
we stored `nums[i]` first, an element whose double equals `target` (e.g. `nums = [3,3]`,
`target = 6`) would match against itself. Checking first means we only ever find a partner to the
*left*, so the two indices are guaranteed distinct. `new int[]{a, b}` is Java's array-literal
syntax for returning a small fixed-size result.

## Complexity

    Time:  O(n)  -- single pass; each containsKey/put is O(1) average
    Space: O(n)  -- the map may hold up to every element before the pair is found

## Dry-Run

Input `nums = [2, 7, 11, 15]`, `target = 9`:

| i | nums[i] | complement | in seen? | action            | seen after            | result |
|--:|--------:|-----------:|----------|-------------------|-----------------------|--------|
| 0 | 2       | 9 - 2 = 7  | no       | put 2 -> 0        | {2: 0}                | -      |
| 1 | 7       | 9 - 7 = 2  | yes (0)  | return [0, 1]     | -                     | [0, 1] |

Output: `[0, 1]`.

For the self-pair guard, consider `nums = [3, 3]`, `target = 6`: at i=0, complement=3, seen is
empty, so we put `3 -> 0`. At i=1, complement=3, seen has `3 -> 0`, so we return `[0, 1]` --
distinct indices, correct.

### Checkpoint B -- Order, edge cases, variants

**Q1 (apply).** Run the algorithm on `nums = [3, 3]`, `target = 6`. What is returned, and why isn't it the same index twice?
- a) `[1, 1]`
- b) `[0, 1]` -- we look up the complement BEFORE storing, so index 1 finds the earlier index 0
- c) No solution

<details><summary>Show answer</summary>

**(b)** -- at i=0 the map is empty, so we store `3 -> 0`. At i=1 the complement 3 is already in the map at index 0, so we return `[0, 1]`. Storing-first would wrongly self-match.

</details>

**Q2 (analyze).** What goes wrong if you call `seen.get(complement)` and unbox straight into an `int` without checking `containsKey` first?
- a) Nothing, it returns 0 for missing keys
- b) A missing key returns `null`, and unboxing `null` throws `NullPointerException`
- c) It inserts a default value

<details><summary>Show answer</summary>

**(b)** -- `get` returns `null` for absent keys; converting `null` to `int` crashes. Always guard with `containsKey` (or use `getOrDefault`).

</details>

**Q3 (transfer).** If the input array is already SORTED, you can solve Two Sum in O(n) with O(1) space and NO map. Sketch how.

<details><summary>Show answer</summary>

Use two pointers, one at each end. If their sum is too small, move the left pointer up (bigger value); if too big, move the right pointer down. That's the Two Pointers pattern (Pattern 2) -- the sorted order replaces the map.

</details>

## Common mistakes

- Inserting the current element *before* the lookup, so an element equal to `target / 2` matches
  itself and returns the same index twice.
- Storing indices as values but then forgetting which is earlier -- always return
  `[stored, current]` because the stored one was seen first.
- Using `seen.get(complement)` without `containsKey` first; if the key is absent it returns
  `null`, and unboxing `null` into an `int` throws `NullPointerException`.
- A nested `for j` loop scanning left of every `i`. Correct but O(n^2); the map is the whole point.
- Comparing `Integer` boxed keys with `==`. `containsKey` / `get` use `.equals`, so this is safe
  here -- but never roll your own boxed-`Integer` comparisons.

## Related problems

- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the same "seen it before?" habit
  with a set instead of a map.
- [0049 - Group Anagrams](../0049-group-anagrams/) - another map-as-lookup-table, keyed by a
  computed signature instead of by value.
- [0001 - Two Sum (you are here)](./) is the foundation; the Two Pointers section revisits the
  same problem on a *sorted* array without a map.
