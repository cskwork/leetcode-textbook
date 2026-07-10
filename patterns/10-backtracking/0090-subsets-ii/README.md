# 0090 - Subsets II

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/subsets-ii/

## Concepts used

- **Array** -- a row of numbered slots holding values; here it may contain duplicates. [glossary](../../../docs/10-glossary.md#array)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Backtracking** -- try a choice, recurse, then UNDO the choice before trying the next. [glossary](../../../docs/10-glossary.md#backtracking)

## Problem

Given an integer array `nums` that **may contain duplicates**, return all
possible subsets (the power set). The solution set **must not contain
duplicate subsets**. Order of output does not matter.

Signature:

    List<List<Integer>> subsetsWithDup(int[] nums)

Example (verbatim from LeetCode):

    Input:  nums = [1,2,2]
    Output: [[],[1],[1,2],[1,2,2],[2],[2,2]]

    Input:  nums = [0]
    Output: [[],[0]]

## Intuition

If we ran the LC 78 Subsets algorithm on `[1, 2a, 2b]` we would record both
`[2a]` and `[2b]` -- the same subset twice, because the two `2`s sit at
different indices even though they have the same value. The fix is two steps
and it has to be **both**:

1. **Sort the input first.** This places equal values side by side so a single
   "is this value the same as the previous one?" check can find them.
2. **Inside the loop, skip a candidate when it equals its predecessor *and*
   that predecessor was not chosen at this recursion level.**

The precise guard, in language-neutral form:

    for i from start to length(nums) - 1:
        if i > start and nums[i] == nums[i-1]: skip i
        ... choose / explore / un-choose ...

The condition `i > start` is the subtle half. At any recursion level, `i`
starts at `start`; that first candidate is always taken, because it represents
"include this value for the first time at this position". For indices `i >
start`, if `nums[i] == nums[i-1]` then the subtree rooted at `i` is a strict
*subset* of the subtree rooted at `i - 1` that we just finished exploring --
picking `nums[i]` would lead to the same paths. So we skip it.

Crucially, `i > start` (not `i > 0`) is what lets `[1, 1, 2]` legitimately
contain `[1a, 1b, 2]`: at the recursion frame that has already picked `1a`
(start advanced past `1a`'s index), `1b` is the *first* candidate at its
level (`i == start`), so the duplicate guard does not fire and both `1`s
appear in the same path. The guard only kills *sibling* duplicates, not
*duplicate-in-path* duplicates.

### Checkpoint A -- Kill the duplicate subsets

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Two steps are required to avoid duplicate subsets on an input with repeated values. Which pair?
- a) Sort the input, then skip an element equal to its predecessor
- b) Use a hash set of subsets, then sort at the end
- c) Reverse the input, then dedupe with a map

<details><summary>Show answer</summary>

**(a)** -- sorting puts equal values side by side so one neighbour check can spot them; the skip then drops the duplicate sibling. Both steps are needed.

</details>

**Q2 (comprehend).** The skip guard is `i > start and nums[i] == nums[i-1]`. Why `i > start` and not `i > 0`?
- a) To skip the very first element of the whole array
- b) So the first candidate at each recursion level is always taken, letting both `1`s coexist in the same path like `[1,1,2]`
- c) To make the loop run faster

<details><summary>Show answer</summary>

**(b)** -- `i == start` means "first candidate at this level", which always represents a fresh inclusion of that value and is always taken. Only for `i > start` does an equal predecessor mean "we just explored this subtree", so we skip it. `i > 0` would wrongly kill the second `1` inside `[1,1,2]`.

</details>

## Pseudocode

    function subsetsWithDup(nums):
        sort nums                                    # required for the neighbour check
        results = empty list
        backtrack(path = empty, start = 0, nums, results)
        return results

    function backtrack(path, start, nums, results):
        append a COPY of path to results             # every node is a valid subset
        for i from start to length(nums) - 1:
            if i > start and nums[i] == nums[i-1]:   # sibling duplicate, skip it
                skip i
            add nums[i] to path                      # CHOOSE
            backtrack(path, i + 1, nums, results)    # EXPLORE later indices only
            remove the last element of path          # UN-CHOOSE

Three things this skeleton has that plain Subsets does not:

1. A `sort nums` line at the top. Without it the neighbour check is meaningless
   -- equal values may not be adjacent.
2. A skip guard *inside* the loop, before the choose/explore/un-choose block.
   The skip must be `i > start`, never `i > 0`; the latter would wrongly kill
   the second `1` in `[1, 1, 2] -> [1, 1]`.
3. Everything else (record-before-loop, `i + 1` child start, defensive copy,
   single-line un-choose) is identical to LC 78. Sorting + one new `if` is
   the whole delta from LC 78 to LC 90.

## Java Solution

```java
import java.util.*;

class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        Arrays.sort(nums);                                   // puts equal values adjacent
        backtrack(results, new ArrayList<>(), 0, nums);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           int start, int[] nums) {
        results.add(new ArrayList<>(path));                  // every node is a subset
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue; // skip sibling duplicate
            path.add(nums[i]);                                // CHOOSE
            backtrack(results, path, i + 1, nums);            // EXPLORE later indices
            path.remove(path.size() - 1);                     // UN-CHOOSE
        }
    }
}
```

`Arrays.sort(nums)` is non-negotiable; the duplicate guard `nums[i] ==
nums[i - 1]` only detects duplicates when equal values are adjacent, and only
a sorted array guarantees that. The guard `i > start` (not `i > 0`) is the
half that beginners most often get wrong: `i == start` means this candidate is
the first one considered at this recursion level, and we always take it
because it represents the first inclusion of its value here; only for `i >
start` does an equal predecessor mean "we just explored this exact subtree".
The `i + 1` in the recursive call is the same "look forward only" rule as LC
78 -- order does not matter, so we never reach back to an earlier index. The
`new ArrayList<>(path)` snapshot and the `path.remove(path.size() - 1)`
un-choose are identical to LC 78; only the sort and the skip are new.

## Complexity

    Time:  O(n * 2^n)  -- the power set has at most 2^n subsets and we spend
                           O(n) copying each one into the result. Sorting is
                           O(n log n), negligible.
    Space: O(n) recursion + path depth. Output list not counted.

## Dry-Run

Backtracking tree on `nums = [1, 2, 2]` (already sorted; we will annotate the
two `2`s as `2a` and `2b` for clarity, though the algorithm itself does not
distinguish them). State at the top of each call: `path | start`.

```
backtrack([], 0)        -> record []
  i=0: nums[0]=1, take
        add 1 -> backtrack([1], 1)
                   record [1]
                   i=1: nums[1]=2a, i==start, take
                        add 2a -> backtrack([1,2a], 2)
                                    record [1,2]
                                    i=2: nums[2]=2b, i==start, take
                                         add 2b -> backtrack([1,2a,2b], 3)
                                                     record [1,2,2]
                                                     loop empty, return
                                         remove 2b -> [1,2a]
                                     loop ends, return
                        remove 2a -> [1]
                   loop ends, return
  remove 1 -> []
  i=1: nums[1]=2a, i==start, take
        add 2a -> backtrack([2a], 2)
                   record [2]
                   i=2: nums[2]=2b, i==start, take
                        add 2b -> backtrack([2a,2b], 3)
                                    record [2,2]
                                    return
                        remove 2b -> [2a]
                   loop ends, return
        remove 2a -> []
  i=2: nums[2]=2b, i>start AND nums[2]==nums[1] -> SKIP      (would duplicate [2])
  loop ends, return
```

Recordings in order: `[], [1], [1,2], [1,2,2], [2], [2,2]` -- six subsets, the
expected output. The only line that does any work the LC 78 solution would not
is the final `SKIP`: that is what kills the duplicate `[2b]` that would
otherwise appear alongside `[2a]`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [1, 1]` (sorted). How many subsets are recorded, and which?
- a) 2: `[], [1]`
- b) 3: `[], [1], [1,1]`
- c) 4: `[], [1], [1], [1,1]`

<details><summary>Show answer</summary>

**(b)** -- record `[]`; pick the first `1` -> record `[1]`; at the next level the second `1` is `i == start` so it is taken -> record `[1,1]`. Back at the top, the second `1` is `i > start` and equals its predecessor, so it is skipped (would duplicate `[1]`). Three distinct subsets.

</details>

**Q2 (analyze).** What goes wrong if you keep the skip guard but forget to sort first?
- a) Nothing -- the guard works on any array
- b) Equal values may not be adjacent, so the neighbour check misses duplicates and they slip through
- c) The code throws an exception

<details><summary>Show answer</summary>

**(b)** -- `nums[i] == nums[i-1]` only detects duplicates when equal values sit next to each other, which only a sorted array guarantees. On unsorted input the guard is meaningless.

</details>

**Q3 (transfer).** How would you adapt this dedup trick to Permutations II (permutations of an array with duplicates)?

<details><summary>Show answer</summary>

Sort the input and keep the skip-equal-neighbour idea, but the loop runs over all indices gated by `used[]` (no start index, since order matters). The guard skips a duplicate whose equal predecessor was not picked at this level.

</details>

## Common mistakes

- **Skipping on `i > 0` instead of `i > start`.** `i > 0` wrongly skips the
  second `1` in `[1, 1, 2]` even when the first `1` is legitimately in the
  path, producing `[1, 2]` but never `[1, 1, 2]`. The guard must be `i >
  start` so that the first candidate at each level is always taken.
- **Forgetting to sort.** Then `nums[i] == nums[i - 1]` says nothing about
  whether `i` is a duplicate of an earlier *equal* value, and duplicates slip
  through. The neighbour check only works on a sorted array.
- **Sorting but then comparing `nums[i] == nums[i - 1]` *before* checking
  `i > start`.** Equivalent in this code (short-circuit makes the order safe),
  but if you ever rearrange the condition, `nums[i - 1]` can index out of
  bounds when `i == 0`. Always evaluate `i > start` first.
- **`results.add(path)` instead of a copy.** Same aliasing trap as the rest
  of the pattern; every entry collapses to the final empty list.
- **Treating this as a Permutations problem.** Permutations uses no start
  index; this is still combinations-style, so `i + 1` on the recursive call.
  Mixing the two produces duplicates of every ordering.

## Related problems

- [0078 - Subsets](../0078-subsets/) -- the parent skeleton; LC 90 is LC 78
  plus a sort and one skip line.
- [0046 - Permutations](../0046-permutations/) -- contrast: same duplicate-
  skipping trick applies to Permutations II (LC 47), but the loop runs over
  all indices gated by `used[]`.
- [0039 - Combination Sum](../0039-combination-sum/) -- the other Subsets
  variant; modifies the reuse rule (`i` not `i + 1`) instead of the dedup rule.
