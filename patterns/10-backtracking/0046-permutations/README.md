# 0046 - Permutations

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/permutations/

## Concepts used

- **Recursion** -- a function that calls itself on a smaller version of the problem; here "smaller" means "one fewer empty slot to fill". [glossary](../../../docs/10-glossary.md#recursion)
- **Backtracking** -- try a choice, recurse, then UNDO the choice before trying the next. [glossary](../../../docs/10-glossary.md#backtracking)
- **Base case** -- the simplest input a recursive function answers directly; here, "all slots filled". [glossary](../../../docs/10-glossary.md#base-case)

## Problem

Given an array of **distinct** integers `nums`, return *all possible
permutations*. Order of the output does not matter.

Signature:

    List<List<Integer>> permute(int[] nums)

Example (verbatim from LeetCode):

    Input:  nums = [1,2,3]
    Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

    Input:  nums = [0,1]
    Output: [[0,1],[1,0]]

    Input:  nums = [1]
    Output: [[1]]

## Intuition

A permutation reorders the input. Every permutation has length `n`, and at
each of its `n` slots we may place **any** element we have not placed yet --
*order matters*, so `[1, 2]` and `[2, 1]` are both valid and distinct answers.

Contrast this with Subsets, where order did not matter and we enforced a
"look forward only" rule via a `start` index. Permutations throws that rule
away: each slot can hold any element. The new mechanism for "what can I still
pick?" is a `used[]` boolean array -- mark an element used when you place it,
recurse, mark it unused on the way back. Same choose/explore/un-choose
skeleton, a different "what are my choices" rule.

The base case is the cleanest of any problem in this pattern: a permutation
is complete exactly when the path length equals `n`. So we record only when
`path.size() == nums.length` and return.

> **Alternative technique -- swap-in-place.** You can also generate
> permutations without `used[]` or `path` by swapping element `i` into the
> current slot, recursing on `i + 1`, and swapping back. It uses less memory
> but is awkward to combine with duplicate-skipping (LC 47). This book uses
> the `used[]` form because it is the same skeleton as the rest of the pattern
> and extends cleanly to Subsets II and Combination Sum II later.

### Checkpoint A -- Order matters, so mark what's used

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Subsets used a moving `start` index to avoid reuse. What does Permutations use instead?
- a) A `used[]` boolean array
- b) Sorting the input
- c) A hash set of indices, rebuilt each call

<details><summary>Show answer</summary>

**(a)** -- a `used[]` mask records which elements are already in the path. It is set true on choose and false on un-choose.

</details>

**Q2 (comprehend).** Why does the loop run from `0` every call (no start index)?
- a) To let the same element be reused
- b) Because order matters, so every index is eligible for every slot; `used[]` prevents reuse, not the loop bounds
- c) Because the array is sorted

<details><summary>Show answer</summary>

**(b)** -- each slot can hold any not-yet-used element, so the loop scans all indices and `if (used[i]) continue` skips the spent ones. A start index would forbid later elements appearing before earlier ones, producing subsets, not permutations.

</details>

## Pseudocode

    function permute(nums):
        results = empty list
        used = array of false with length(nums)
        backtrack(path = empty, used, nums, results)
        return results

    function backtrack(path, used, nums, results):
        if length(path) == length(nums):          # all slots filled
            append a COPY of path to results
            return
        for i from 0 to length(nums) - 1:         # every index is a candidate
            if used[i]: skip i                    # but only if not yet used
            add nums[i] to path                   # CHOOSE
            set used[i] = true
            backtrack(path, used, nums, results)  # EXPLORE
            set used[i] = false                   # UN-CHOOSE
            remove the last element of path

Two structural facts to internalise, both contrasts with Subsets / Combination
Sum:

1. **No `start` index.** The loop runs from `0`, not from a moving `start`.
   Order matters, so every index is eligible for every slot; the `used[]`
   mask is what prevents reuse, not the loop bounds.
2. **The un-choose is two lines, not one.** A choice has *two* side effects
   (it appended to `path` *and* set a flag), so the undo has two as well
   (clear the flag *and* remove from path). Miss either and the next sibling
   inherits a corrupted state.

## Java Solution

```java
import java.util.*;

class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> results = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(results, new ArrayList<>(), used, nums);
        return results;
    }

    private void backtrack(List<List<Integer>> results, List<Integer> path,
                           boolean[] used, int[] nums) {
        if (path.size() == nums.length) {
            results.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            path.add(nums[i]);                                  // CHOOSE
            used[i] = true;
            backtrack(results, path, used, nums);               // EXPLORE
            used[i] = false;                                    // UN-CHOOSE
            path.remove(path.size() - 1);
        }
    }
}
```

The loop runs the full `0..n-1` range, not `start..n-1` -- this is the
defining difference from combinations. Gating each iteration with
`if (used[i]) continue` is what prevents reusing an element; without that
guard you would place the same element in two slots and the same element in
two slots means the output is not a permutation. The un-choose is a *pair* of
statements: `used[i] = false` and `path.remove(...)`. Forgetting either half
breaks a different sibling -- leaving `used[i]` true makes later siblings
wrongly skip element `i`, while leaving it in `path` makes the next
permutation too long. `new ArrayList<>(path)` is the same defensive snapshot
discipline as the rest of the pattern; the live `path` is shared across all
frames and would otherwise collapse every recorded answer to the final empty
list.

## Complexity

    Time:  O(n * n!)  -- there are n! permutations and we spend O(n) copying
                          each one into the result list; the loop also does
                          O(n) work per call to scan over already-used indices.
    Space: O(n) recursion + path + used[]. Output list not counted.

## Dry-Run

Permutation tree on `nums = [1, 2]`. We write state as
`path | used[0], used[1]`. Expected output: `[1, 2]` and `[2, 1]`.

```
backtrack([], F,F)
  i=0 not used:
      add 1 -> path=[1], used=[T,F]
      backtrack([1], T,F)
        length 1 != 2, keep looping
        i=0 used -> skip
        i=1 not used:
            add 2 -> path=[1,2], used=[T,T]
            backtrack([1,2], T,T)
                length 2 == 2 -> RECORD [1,2], return
            remove 2 -> path=[1], used=[T,F]
        loop ends, return
      used[0]=false, remove 1 -> path=[], used=[F,F]
  i=1 not used:
      add 2 -> path=[2], used=[F,T]
      backtrack([2], F,T)
        i=0 not used:
            add 1 -> path=[2,1], used=[T,T]
            backtrack([2,1], T,T) -> RECORD [2,1], return
            remove 1 -> path=[2], used=[F,T]
        i=1 used -> skip
        loop ends, return
      used[1]=false, remove 2 -> path=[], used=[F,F]
```

Two recordings: `[1, 2]` and `[2, 1]` -- the two permutations of a 2-element
array. Note that the un-choose runs *twice per choice* (the flag and the
list), and that `used[]` is fully reset to `[F, F]` by the time the function
returns -- the invariant the next call relies on.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [1, 2, 3]`. How many permutations are recorded, and which one is recorded first?
- a) 3, and `[1,2,3]` is first
- b) 6, and `[1,2,3]` is first
- c) 6, and `[3,2,1]` is first

<details><summary>Show answer</summary>

**(b)** -- there are `3! = 6` permutations. The first path always takes the lowest available index at each slot (`1, then 2, then 3`), so `[1,2,3]` is recorded first.

</details>

**Q2 (analyze).** The un-choose is two lines: `used[i] = false` and `path.remove(...)`. What breaks if you forget ONLY the `used[i] = false` line?
- a) Nothing -- `path.remove` is enough
- b) Later siblings wrongly skip element `i`, because they think it is still spent
- c) The paths become too long

<details><summary>Show answer</summary>

**(b)** -- `used[i]` stays `true`, so the next sibling (and frames above) skip element `i` forever; permutations come out missing that element. (Forgetting only `path.remove` instead would make the paths too long.)

</details>

**Q3 (transfer).** How would you generate all permutations of length exactly `k` (partial permutations) instead of full length `n`?

<details><summary>Show answer</summary>

Change the DONE check from `path.size() == nums.length` to `path.size() == k` (record and return at length `k`). The loop, the `used[]` mask, and the two-line un-choose stay identical.

</details>

## Common mistakes

- **Using a `start` index from combinations.** That forbids any later element
  from appearing before an earlier one -- you produce subsets, not
  permutations. The loop must start at `0` each time.
- **Forgetting to flip `used[i]` back to false.** After the recursive call the
  flag must be cleared; otherwise the next sibling (and every frame above it)
  thinks element `i` is permanently spent and skips it incorrectly.
- **Forgetting to remove from `path`.** Then the path keeps growing across
  siblings and the recorded permutations are all far too long.
- **Checking `path.size() == nums.length - 1` as the base case.** Off-by-one:
  you record one slot early and miss the last element of every permutation.
- **`results.add(path)` without a copy.** Same aliasing trap as Subsets and
  Combination Sum -- every entry collapses to the final empty path.
- **Allocating a new `used[]` per recursive call.** Wasteful and easy to get
  wrong. Allocate once in the public method, mutate in place, undo on the way
  back -- exactly what the skeleton shows.

## Related problems

- [0078 - Subsets](../0078-subsets/) -- the parent skeleton; Permutations is
  the "order matters" variant of it.
- [0090 - Subsets II](../0090-subsets-ii/) -- applies the duplicate-skipping
  trick on top of the Subsets skeleton, the same trick you would use for
  Permutations II (LC 47).
- [0039 - Combination Sum](../0039-combination-sum/) -- contrast: order does
  not matter, so the start index is used and `used[]` is not.
