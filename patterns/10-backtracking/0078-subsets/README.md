# 0078 - Subsets

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/subsets/

## Concepts used

- **Array** -- a row of numbered slots holding values, accessed by position. [glossary](../../../docs/10-glossary.md#array)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Backtracking** -- a recursive search that tries a choice, recurses, then UNDOES the choice before trying the next; the undo is the step beginners forget. [glossary](../../../docs/10-glossary.md#backtracking)
- **Decision tree** -- a branching picture of all the choices, one level per decision. [glossary](../../../docs/10-glossary.md#decision-tree)

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

Imagine a restaurant menu with three dishes, and the waiter asks about each
dish in turn. For each one you say either "yes please" or "no thanks". Every
possible sequence of yes/no answers is one possible meal -- and listing all of
them is exactly this problem. A subset is just "the dishes you said yes to".
Start with the smallest interesting example, `nums = [1, 2]`. Walking the menu
gives exactly four subsets:

    []       said no to 1,  no to 2
    [1]      said yes to 1, no to 2
    [2]      said no to 1,  yes to 2
    [1, 2]   said yes to 1, yes to 2

Four subsets = 2 x 2 = 2^2. In general an array of `n` elements has `2^n`
subsets, because each element either is or is not in any given subset.

To generate all of them we use **recursion** -- a function that calls itself
on a smaller piece of the problem -- in the **backtracking** style. We keep one
shared list called `path` ("the elements we have said yes to so far") and walk
the elements in order. At each element we do three steps: (1) **choose** -- add
the element to `path`; (2) **explore** -- recurse to make the yes/no decisions
for the remaining elements; (3) **un-choose** -- remove the element from `path`
so it is clean before we try saying "no" to this element and moving on. One
extra rule keeps the subsets distinct: when we recurse we only ever look at
elements *after* the one we just picked (passing a `start` index that grows).
Because `[1, 2]` and `[2, 1]` are the same subset, never reaching backward is
what stops us printing both.

The **un-choose** step is the one beginners forget, so let us make it visceral
with a trace. Suppose `path = []` and we are deciding about element `1`. We
add `1`, so `path = [1]`, then recurse to decide about `2` -- that recursion
records `[1]` and `[1, 2]`. When it returns, `path` is still `[1]`. But we are
now done with the whole "yes to 1" branch and want to start the "no to 1, yes
to 2" branch, which must begin from `path = []`. If we had forgotten to remove
the `1`, the next branch would wrongly start from `[1]` and produce a bogus
`[1, 2]` instead of the correct `[2]`. The single line `path.remove(last)`
rewinds `path` to `[]` so each sibling branch starts from a clean slate.
Picture the whole process as a decision tree: each level is one element, going
"down" means "yes", and climbing back up means "undo that yes".

The last question is when to record an answer. In Subsets, *every node of the
decision tree is itself a valid subset* -- the empty path is the empty subset,
a one-element path is a singleton, and so on. So we snapshot `path` at the very
top of every call, before the loop. There is no separate **base case** (the
simplest input, answered without recursing) to write: when the loop runs out of
elements to iterate, the call simply returns, which is the correct "do nothing"
behaviour at the bottom of every branch.

### Checkpoint A -- Choose, explore, and undo

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** At each element the solution does three steps: choose (add), explore (recurse), and a third one beginners forget. What is it?
- a) Sort the element
- b) Un-choose (remove the element from `path`)
- c) Copy the `path` before recursing

<details><summary>Show answer</summary>

**(b)** -- the un-choose (`path.remove(last)`) restores `path` to its pre-choice state so the next sibling branch starts clean. Without it the path leaks into siblings.

</details>

**Q2 (comprehend).** Why does the recursive call pass `i + 1` (not `0`) as the child's start?
- a) So the same element can be reused
- b) So we only look forward and never emit both `[1,2]` and `[2,1]`
- c) To make the recursion run faster

<details><summary>Show answer</summary>

**(b)** -- "look forward only" builds each subset in non-decreasing index order, so `[1,2]` is produced once and `[2,1]` never. Passing `0` would generate every ordering of every subset.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [5, 6]`. How many subsets are recorded, and which ones?
- a) 3: `[5], [6], [5,6]`
- b) 4: `[], [5], [5,6], [6]`
- c) 4: `[], [5], [6], [5,6,5]`

<details><summary>Show answer</summary>

**(b)** -- record `[]` at the top, then `[5]`, then `[5,6]`, then after un-choosing back to `[]` the second loop iteration records `[6]`. Four subsets = `2^2`, matching any 2-element input.

</details>

**Q2 (analyze).** What goes wrong if you delete the un-choose line (`path.remove(...)`)?
- a) Nothing -- it is optional
- b) Sibling branches inherit earlier choices, so paths grow too long and answers repeat
- c) The code throws immediately

<details><summary>Show answer</summary>

**(b)** -- the shared `path` never shrinks, so after exploring the "yes to 5" branch the "yes to 6" branch wrongly starts from `[5]` and produces bogus long subsets. The un-choose is the correctness condition.

</details>

**Q3 (transfer).** Suppose you wanted only subsets of size exactly `k` (not all subsets). What is the smallest change to the approach?

<details><summary>Show answer</summary>

Keep the skeleton; change the DONE check so you record (and return) only when `path.size() == k`, instead of recording every node. The choose/explore/un-choose loop is unchanged.

</details>

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
