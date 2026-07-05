# 0039 - Combination Sum

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/combination-sum/

## Problem

You are given an array of **distinct** integers `candidates` and a target
integer `target`. Return a list of all *unique combinations* of `candidates`
where the chosen numbers sum to `target`. The same number may be chosen from
`candidates` an **unlimited number of times**. Two combinations are unique if
the frequency of at least one chosen number differs. Order of output does not
matter.

Signature:

    List<List<Integer>> combinationSum(int[] candidates, int target)

Example (verbatim from LeetCode):

    Input:  candidates = [2,3,6,7], target = 7
    Output: [[2,2,3],[7]]

    Input:  candidates = [2,3,5], target = 8
    Output: [[2,2,2,2],[2,3,3],[3,5]]

    Input:  candidates = [2], target = 1
    Output: []

## Intuition

This is Subsets wearing a different hat. The path is still a list of chosen
values; the loop still walks indices `start..n-1`; the only differences are
the **done condition** (sum equals target) and the **reuse rule**.

The reuse rule is the trick the problem is named for. Each candidate may be
used again, so when we recurse we pass `i`, *not* `i + 1`:

- Subsets passed `i + 1` -> "never reuse the same index" -> combinations without
  repetition.
- Combination Sum passes `i` -> "the same index may be picked again next time"
  -> combinations with unlimited repetition.

Passing `i` instead of `i + 1` is the *only* character that changes the
semantics of the recursion. Everything else -- the path, the un-choose, the
snapshot -- is identical to Subsets.

The "done" condition is `sum == target`. We also gain a **pruning** opportunity
that Subsets did not have: if at any point `sum > target`, no amount of further
non-negative additions can reduce it, so the whole subtree is dead and we can
return early. Sorting the candidates first lets us prune even more cheaply --
once `candidates[i]` alone exceeds the remaining budget, every later candidate
does too, so we `break` instead of just skipping.

## Pseudocode

    function combinationSum(candidates, target):
        sort candidates                            # enables clean pruning
        results = empty list
        backtrack(path = empty, start = 0, remaining = target,
                  candidates, results)
        return results

    function backtrack(path, start, remaining, candidates, results):
        if remaining == 0:                         # path sums to target
            append a COPY of path to results
            return                                 # further additions only overshoot
        for i from start to length(candidates) - 1:
            if candidates[i] > remaining:          # sorted -> no later one fits either
                break
            add candidates[i] to path              # CHOOSE
            backtrack(path, i, remaining - candidates[i],   # EXPLORE
                      candidates, results)
                # note: child start is i, NOT i + 1  -> unlimited reuse
            remove the last element of path        # UN-CHOOSE

Two non-obvious bits to internalise:

1. The recursive call's third argument is `remaining - candidates[i]`, *not*
   `remaining`. We track the budget left, never the running sum. This avoids
   an extra addition on every call and reads more naturally.
2. The `break` on `candidates[i] > remaining` is correct **only because we
   sorted first**. Without sorting, a too-big candidate at index `i` says
   nothing about candidates `i + 1..n-1`.

## Java Solution

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> results = new ArrayList<>();
        Arrays.sort(candidates);                                   // enables clean pruning
        backtrack(results, new ArrayList<>(), 0, target, candidates);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           int start, int remaining, int[] candidates) {
        if (remaining == 0) {
            results.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;                  // sorted -> prune rest
            path.add(candidates[i]);                               // CHOOSE
            backtrack(results, path, i, remaining - candidates[i], candidates); // EXPLORE, reuse i
            path.remove(path.size() - 1);                          // UN-CHOOSE
        }
    }
}
```

`remaining` is the budget, not the running sum -- decrementing it on the way
down reads as "I just spent `candidates[i]`" and makes the `== 0` test
readable. The recursive call passes `i`, the single character that *enables
unlimited reuse* of the same candidate; change it to `i + 1` and you get
Combination Sum III (each candidate usable once) -- a different problem with a
different answer set. Sorting up front lets the loop `break` rather than merely
`continue` when a candidate is too large; because the array is sorted, every
later candidate is at least as big and would also overshoot, so the whole tail
of the loop is dead. The defensive `new ArrayList<>(path)` snapshot is the same
discipline as Subsets: the live `path` is mutated across frames, so each
recorded answer must be a copy.

## Complexity

    Time:  O(n^(T/m + 1)) where n = |candidates|, T = target, m = min(candidates).
           The recursion tree's depth is bounded by T/m (you can pick the smallest
           candidate at most T/m times), and at each level you branch at most n ways.
           The standard loose bound taught is O(2^T) but the branching/depth model
           above is tighter. The sort is O(n log n), negligible.
    Space: O(T/m) recursion + path depth. Output list not counted.

## Dry-Run

Backtracking tree on `candidates = [2, 3, 6, 7]`, `target = 7` (already sorted).
We write state as `path | remaining`.

```
backtrack([], 7)
  i=0 c=2 (2<=7):
      add 2 -> backtrack([2], 5)
        i=0 c=2 (2<=5):
            add 2 -> backtrack([2,2], 3)
              i=0 c=2 (2<=3):
                  add 2 -> backtrack([2,2,2], 1)
                    i=0 c=2: 2 > 1 -> break       (pruned; no path through [2,2,2])
                  remove 2 -> [2,2]
              i=1 c=3 (3<=3):
                  add 3 -> backtrack([2,2,3], 0)
                             remaining == 0 -> RECORD [2,2,3], return
                  remove 3 -> [2,2]
              i=2 c=6: 6 > 3 -> break             (pruned)
            remove 2 -> [2]
        i=1 c=3 (3<=5):
            add 3 -> backtrack([2,3], 2)
              i=0 c=2: but start=1, so we begin at i=1 -> c=3 > 2 -> break
            remove 3 -> [2]
        i=2 c=6 (6<=5? no) -> break               (pruned; 6 > 5)
      remove 2 -> []
  i=1 c=3 (3<=7):
      add 3 -> backtrack([3], 4)
        i=1 c=3 (3<=4):
            add 3 -> backtrack([3,3], 1)
              i=1 c=3 > 1 -> break
            remove 3 -> [3]
        i=2 c=6 > 4 -> break
      remove 3 -> []
  i=2 c=6 (6<=7):
      add 6 -> backtrack([6], 1)
        i=2 c=6 > 1 -> break
      remove 6 -> []
  i=3 c=7 (7<=7):
      add 7 -> backtrack([7], 0)
                 remaining == 0 -> RECORD [7], return
      remove 7 -> []
```

Two recordings: `[2, 2, 3]` and `[7]`. That matches the expected output
`[[2,2,3],[7]]`. Notice how many branches the `break` killed (e.g. exploring
`[2,2,2,2]` would have summed to 8 -- pruned the moment `2 > 1`).

## Common mistakes

- **Passing `i + 1` on the recursive call.** That forbids reuse and you get
  each-candidate-at-most-once behaviour, missing answers like `[2, 2, 3]`.
  This is *the* single most common bug; the difference from Subsets is one
  character.
- **No pruning.** Without the `break` (or at least `continue`) when a candidate
  exceeds `remaining`, the search explores dead branches and may TLE on large
  targets. Sorting first lets you `break` instead of just skipping.
- **Not sorting.** Then `candidates[i] > remaining` says nothing about later
  indices, the `break` is unsafe, and duplicate combinations like `[3, 2, 2]`
  and `[2, 2, 3]` can both appear because "look forward only" relies on the
  sorted order.
- **Using `path` instead of a copy in `results.add`.** Same trap as Subsets --
  every entry then aliases the same live list.
- **Continuing to recurse after recording.** Once `remaining == 0` you must
  `return`; continuing would add positive numbers and overshoot forever (or,
  with the pruning, just waste iterations).
- **Assuming candidates are sorted or non-negative without checking.** The
  problem guarantees distinct positive integers, but if a candidate were
  negative the `break` pruning would be wrong (a later smaller one might still
  fit). State your assumptions.

## Related problems

- [0078 - Subsets](../0078-subsets/) -- the parent skeleton; Combination Sum is
  Subsets with a target-sum "done" check and the reuse knob turned on.
- [0090 - Subsets II](../0090-subsets-ii/) -- another Subsets variant; the
  dedup rule there is the *other* common modification to this skeleton.
- [0046 - Permutations](../0046-permutations/) -- contrast: order matters, so
  no start index is used at all.
