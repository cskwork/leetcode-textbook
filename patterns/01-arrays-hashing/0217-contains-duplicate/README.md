# 0217 - Contains Duplicate

**Difficulty:** Easy
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/contains-duplicate/

## Concepts used

- **Hash set** -- a container that only remembers which values it has seen, so "have I seen this
  value before?" is answered instantly (O(1)). [glossary](../../../docs/10-glossary.md#hash-set)
- **Linear scan** -- walking an array one element at a time, from first to last.
  [glossary](../../../docs/10-glossary.md#linear-scan)
- **Time complexity** -- how the runtime grows as the input grows; O(1) means "constant regardless
  of size", O(n) means "scales with the number of items".
  [glossary](../../../docs/10-glossary.md#time-complexity-big-o)

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

Imagine you're a teacher checking a tall stack of homework for two identical papers. You could
compare every paper against every other paper, but with 100 papers that is thousands of
comparisons. Far better: flip through the stack once, and for each paper ask "have I already seen
this exact name on my list?" The first time the answer is "yes", you've caught a duplicate and can
stop.

Walk the smallest case, `nums = [2, 5, 1, 5]`. Read `2` -- never seen it, jot it down. Read `5` --
new, jot it. Read `1` -- new, jot it. Read `5` again -- `5` is already on the list, so the answer
is `true`.

The general rule follows the same shape. Keep a notebook that answers "have I seen this?" in no
time at all. In code that notebook is a [hash set](../../../docs/10-glossary.md#hash-set) -- a
container built so that "is X in here?" is answered in O(1), as fast as recognizing a familiar
face. Walk the array once (a [linear scan](../../../docs/10-glossary.md#linear-scan)); at each
value, ask the set whether the value is already inside. The first "yes" is your duplicate. Reach
the end with every value new, and there are none.

Why O(n) and not O(n^2)? Because the set check costs O(1), not O(n). The slow alternative -- a
second inner loop comparing each element against all earlier ones -- does roughly
`n/2 + n/3 + ...` comparisons; the hash set lets each element settle its question with a single
constant-time check.

### Checkpoint A -- Spot the data structure

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** This problem keeps asking "have I seen this value before?" Which container is built exactly for that?
- a) A sorted array, binary-searched each step
- b) A hash set
- c) A second array we scan linearly each time

<details><summary>Show answer</summary>

**(b)** -- a hash set answers membership ("is X in here?") in O(1). Option (a) needs a sort first, and (c) is the O(n^2) brute force.

</details>

**Q2 (comprehend).** Why is the hash-set solution O(n) and not O(n^2)?
- a) Because the array is small
- b) Because each "have I seen X?" check is O(1), not O(n)
- c) Because we sort the array first

<details><summary>Show answer</summary>

**(b)** -- one pass of n steps, each step one O(1) lookup. The O(n^2) version comes from replacing that O(1) lookup with an inner scan of all earlier elements.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [3, 1, 3]`. What is returned, and at which step does the loop stop?
- a) `false`, after checking all three
- b) `true`, at the third element
- c) `true`, at the second element

<details><summary>Show answer</summary>

**(b)** -- step 1 adds 3 -> {3}; step 2 adds 1 -> {1,3}; step 3 reads 3, which IS in the set, so we `return true` immediately. The loop never reaches a fourth step.

</details>

**Q2 (analyze).** What should `containsDuplicate([])` return, and why does the code handle it with no special case?
- a) `true` -- the empty input is special
- b) `false` -- the loop body never runs, so control falls through to the final `return false`
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- an empty array has no pairs, so "no duplicates" is correct. The for-each loop iterates zero times and control reaches the trailing `return false`.

</details>

**Q3 (transfer).** Suppose instead of `true/false` you had to return the FIRST value that repeats. What is the smallest change to the current code?

<details><summary>Show answer</summary>

Replace `return true;` with `return x;` (change the return type to `int` and use a sentinel like `-1` for "no duplicate"). The check-before-add order already finds the first repeat at its second sighting.

</details>

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
