# 0001 - Two Sum

**Difficulty:** Easy
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/two-sum/

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

For each element `x` we know exactly what its partner must be: the `complement = target - x`. The
question "is the complement already somewhere to my left?" is an existence lookup -- the core
trigger signal of this pattern. A `HashMap` answers it in O(1), and as a bonus it can store the
complement's *index* alongside it. So instead of asking "does the complement exist?", we ask
"have I already recorded the complement, and if so, at what index?". One pass, one lookup, one
insert per element. This collapses the brute-force O(n^2) double loop into O(n).

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
