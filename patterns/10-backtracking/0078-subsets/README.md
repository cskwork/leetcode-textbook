# 0078 - Subsets

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/subsets/

## Problem

Given an integer array `nums` of **unique** elements, return *all possible
subsets* (the power set). The solution set must not contain duplicate subsets.
Order of output does not matter.

Signature:

    List<List<Integer>> subsets(int[] nums)

Example (verbatim from LeetCode):

    Input:  nums = [1,2,3]
    Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]

    Input:  nums = [0]
    Output: [[],[0]]

## Intuition

A subset is just "for each element, decide in or out". That yields `2^n`
answers, which is exactly the size of the power set. The natural way to walk
that decision tree is **backtracking**: maintain a `path` of the elements we
have said "in" to so far, and at each step decide which element to include
*next*.

The crucial constraint that makes this Subsets rather than Permutations is that
**order does not matter**. `[1, 2]` and `[2, 1]` are the same subset. To avoid
generating both, we never let the recursion reach *backwards*: each frame only
considers elements at indices **strictly after** the last one it picked. We
enforce that with a `start` index that is passed down and increases on every
recursive call.

The other key decision is *when to record*. Unlike permutations, where only
full-length paths count, **every node in the decision tree is a valid subset**.
The empty path is the empty subset; a path of length 1 is a singleton subset;
and so on. So we snapshot the path at the *top* of every call, before the loop.

## Pseudocode

    function subsets(nums):
        results = empty list
        backtrack(path = empty, start = 0, nums, results)
        return results

    function backtrack(path, start, nums, results):
        append a COPY of path to results        # every node is a valid subset
        for i from start to length(nums) - 1:
            add nums[i] to path                 # CHOOSE
            backtrack(path, i + 1, nums, results)   # EXPLORE only later indices
            remove the last element of path     # UN-CHOOSE

Note three things about the template:

1. Recording happens *before* the loop, so the empty subset is captured.
2. The recursive call passes `i + 1`, never `start + 1` and never `0`. That
   "only look forward" rule is what kills `[2, 1]` and `[1, 2]` duplicates.
3. The un-choose is a single line -- `remove the last element of path` -- and
   it runs *after* the recursive call returns, restoring `path` for the next
   sibling.

## Java Solution

```java
import java.util.*;

class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        backtrack(results, new ArrayList<>(), 0, nums);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           int start, int[] nums) {
        results.add(new ArrayList<>(path));        // snapshot every node
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);                      // CHOOSE
            backtrack(results, path, i + 1, nums);  // EXPLORE (look forward only)
            path.remove(path.size() - 1);           // UN-CHOOSE
        }
    }
}
```

`results.add(new ArrayList<>(path))` makes a defensive copy of the live `path`.
If we wrote `results.add(path)` instead, every entry would point at the *same*
list we keep mutating, and they would all collapse to the final empty path by
the time the function returned. The `start` parameter is the order-does-not-
matter lever: combined with `i + 1` in the recursive call it guarantees every
recorded subset is in non-decreasing index order, so `[1, 2]` is produced once
and `[2, 1]` never. The un-choose `path.remove(path.size() - 1)` is the whole
reason one shared `path` works at all -- it restores the path to its
pre-choice state for the next sibling. The loop naturally terminates when
`start == nums.length`, recording only the snapshot at that leaf; no separate
base case is needed because "do nothing" is the correct behaviour at the
bottom of every branch.

## Complexity

    Time:  O(n * 2^n)  -- there are 2^n subsets and we spend O(n) copying each
                           one into the result list.
    Space: O(n) recursion + path depth. Output list holds 2^n subsets of total
           size n * 2^n, not counted toward auxiliary space.

## Dry-Run

Decision tree on `nums = [1, 2, 3]`. Each line shows the state at the *top* of
a `backtrack` call: the `path` we are about to record, then the `start` we
loop from.

```
backtrack([], 0)         -> record []
  i=0: add 1 -> backtrack([1], 1)
                   record [1]
                   i=1: add 2 -> backtrack([1,2], 2)
                                    record [1,2]
                                    i=2: add 3 -> backtrack([1,2,3], 3)
                                                     record [1,2,3]
                                                     loop empty, return
                                    remove 3 -> [1,2]
                                    loop ends, return
                   remove 2 -> [1]
                   i=2: add 3 -> backtrack([1,3], 3)
                                    record [1,3]
                                    loop empty, return
                   remove 3 -> [1]
                   loop ends, return
  remove 1 -> []
  i=1: add 2 -> backtrack([2], 2)
                 record [2]
                 i=2: add 3 -> backtrack([2,3], 3)
                                  record [2,3]
                                  return
                 remove 3 -> [2]
                 return
  remove 2 -> []
  i=2: add 3 -> backtrack([3], 3)
                 record [3]
                 return
  remove 3 -> []
```

Recordings in order: `[], [1], [1,2], [1,2,3], [1,3], [2], [2,3], [3]` -- eight
subsets, exactly `2^3`, and no permutation is duplicated.

## Common mistakes

- **`results.add(path)` instead of a copy.** Every entry then aliases the same
  live list, and the output collapses to a list of empty lists. Always
  `new ArrayList<>(path)`.
- **Recursing with `start + 1` instead of `i + 1`.** This skips indices
  between `start` and `i`, dropping valid subsets like `[1, 3]` when started
  from `i = 1`. The child's new start is the *current* index plus one.
- **Recursing with `0` or no start at all.** You then generate every
  permutation of every subset -- `[2, 1]` appears alongside `[1, 2]` -- and
  blow up to `n!`-ish output instead of `2^n`.
- **Putting the record inside the loop, or skipping it for the empty path.**
  The empty subset is a valid answer and must be recorded. Recording at the
  top of the function captures it for free.
- **`for (int i = start; i <= nums.length; ...)`.** Off-by-one: `i` reaches
  `nums.length`, indexes out of bounds. Use strict `<`.

## Related problems

- [0090 - Subsets II](../0090-subsets-ii/) -- same skeleton, adds
  duplicate-skipping on a sorted array.
- [0039 - Combination Sum](../0039-combination-sum/) -- same skeleton, but the
  child start index is `i` (unlimited reuse) and only target-sum paths record.
- [0046 - Permutations](../0046-permutations/) -- contrast: order matters, so
  no start index; gate the loop with a `used[]` mask instead.
