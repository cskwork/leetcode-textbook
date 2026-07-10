# 0055 - Jump Game

**Difficulty:** Medium
**Pattern:** Greedy
**LeetCode:** https://leetcode.com/problems/jump-game/

## Concepts used

- **Greedy** -- make the locally-best choice at each step and never revisit it; works only when you can prove that choice is part of some optimal solution. [glossary](../../../docs/10-glossary.md#greedy)
- **Array** -- a row of numbered slots holding values, accessed by position in O(1). [glossary](../../../docs/10-glossary.md#array)
- **Invariant** -- a condition that is always true at the start of every loop iteration; stating it clearly is how you prove a loop correct. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

You are given an integer array `nums` of length `n`. You start at index `0`,
and each element `nums[i]` is the **maximum** jump length from index `i`.
Return `true` if you can reach the last index, otherwise `false`.

Signature:

    boolean canJump(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [2,3,1,1,4]
    Output: true
    Explanation: jump 1 step from index 0 to 1, then 3 steps to the last index.

    Input:  nums = [3,2,1,0,4]
    Output: false
    Explanation: you always land on index 3, whose jump length is 0; you are stuck.

## Intuition

The trigger: "can you reach the end?" — a pure reachability question on a
1-D board. Brute force tries every jump sequence (exponential). DP asks
"can I reach index i?" for every i (O(n^2)). The greedy insight collapses
that to a single pass.

The invariant is the **farthest index reachable so far**. Walk left to
right; at each index `i`, if `i` is itself reachable (i.e. `i <= farthest`),
then from `i` you can extend the frontier to `i + nums[i]`. So the new
`farthest` is `max(farthest, i + nums[i])`. If at any point the current
index `i` is *beyond* `farthest`, you cannot even stand on `i`, so the end
is unreachable — return false. If `farthest` ever reaches or passes the last
index, return true.

**Proof sketch of the greedy rule.** The "farthest reachable" frontier is
monotonic: it only grows. If some index `j` is reachable, every index
between the start and `j` is also reachable (because the jump that lands on
`j` could have been shortened to land on any earlier index). Therefore
tracking only the single frontier variable is complete — there is no need
to remember *how* we got there. This monotonic invariant is exactly what
makes the local choice (always extend the frontier) globally safe.

### Checkpoint A -- The reachable frontier

Pause and answer before expanding.

**Q1 (recall).** What single value does the algorithm track as its only state?
- a) The exact index you are currently standing on
- b) The farthest index reachable so far
- c) The number of jumps taken

<details><summary>Show answer</summary>

**(b)** -- `farthest` is the running frontier; everything up to and including it is reachable, everything beyond is not.

</details>

**Q2 (comprehend).** The algorithm returns false only when:
- a) Some `nums[i]` equals 0
- b) The cursor `i` is greater than `farthest` -- we cannot even stand on index `i`
- c) `farthest` overshoots the array

<details><summary>Show answer</summary>

**(b)** -- a zero is only a trap if every later index depends on passing through it; the `i > farthest` test captures that precisely, so zeros need no special case.

</details>

## Pseudocode

```text
function canJump(nums):
    farthest = 0                      # farthest index reachable so far
    last = length of nums - 1
    for i from 0 to last:
        if i is greater than farthest:
            return false              # we cannot even stand on i
        reach = i + nums[i]
        if reach is greater than farthest:
            farthest = reach
        if farthest is at least last:
            return true
    return true                       # only reached when the loop finishes on the last index
```

The early returns make this typically O(n) and let us stop as soon as we
know the answer.

## Java Solution

```java
class Solution {
    public boolean canJump(int[] nums) {
        int farthest = 0;
        int last = nums.length - 1;
        for (int i = 0; i <= last; i++) {
            // If we cannot even reach index i, the goal is unreachable.
            if (i > farthest) {
                return false;
            }
            int reach = i + nums[i];
            if (reach > farthest) {
                farthest = reach;
            }
            if (farthest >= last) {
                return true;
            }
        }
        return true;
    }
}
```

`farthest` is the only state. The check `i > farthest` is the failure
detector: the loop walks forward one index at a time, and the moment the
frontier falls behind the cursor, no amount of future jumps can save us
(we cannot jump from an index we cannot stand on). The success detector
`farthest >= last` fires the instant the last index enters the reachable
set, often long before the loop would naturally end. Because the loop
condition is `i <= last`, the final iteration handles the case where the
answer is determined only at the last index.

## Complexity

    Time:  O(n)   -- one pass; each index is examined at most once.
    Space: O(1)   -- only two integer variables.

## Dry-Run

Step-by-step on `nums = [3,2,1,0,4]` (last index = 4):

| Step | i | nums[i] | i > farthest? | reach = i + nums[i] | farthest (after) | farthest >= 4? |
|------|---|---------|---------------|---------------------|------------------|----------------|
| 1    | 0 | 3       | no            | 3                   | 3                | no             |
| 2    | 1 | 2       | no            | 3                   | 3                | no             |
| 3    | 2 | 1       | no            | 3                   | 3                | no             |
| 4    | 3 | 0       | no            | 3                   | 3                | no             |
| 5    | 4 | 4       | **yes (4 > 3)** | —                | —                | —              |

At step 5 the cursor `i = 4` is beyond `farthest = 3`, so index 4 is
unreachable. Return **false**. The trap at index 3 (`nums[3] = 0`) froze
the frontier at 3, and the goal sat one step beyond it.

For contrast, on `nums = [2,3,1,1,4]`:

| Step | i | nums[i] | i > farthest? | reach | farthest | >= 4? |
|------|---|---------|---------------|-------|----------|-------|
| 1    | 0 | 2       | no            | 2     | 2        | no    |
| 2    | 1 | 3       | no            | 4     | 4        | yes   |

Return **true** at step 2 — the goal is reached before we even visit later
indices.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [2, 0, 2, 0, 1]` (last index 4). What is returned, and at which index is it decided?
- a) true, decided at index 2
- b) false, stuck at index 1
- c) false, never reaches index 4

<details><summary>Show answer</summary>

**(a)** -- index 0 sets farthest to 2; index 2 (reachable, since 2 <= 2) sets farthest to 4, which is >= last, so `return true` fires there.

</details>

**Q2 (analyze).** On a single-element input `nums = [0]`, what is returned and why?
- a) true -- already on the last index, no jump needed
- b) false -- `nums[0]` is 0 so you can never move
- c) it throws an exception

<details><summary>Show answer</summary>

**(a)** -- the loop's first iteration finds `farthest >= last` (both are 0) and returns true before any movement matters.

</details>

**Q3 (transfer).** If the problem asked for the farthest index reachable (not just yes/no), what would you change?

<details><summary>Show answer</summary>

Return `farthest` instead of a boolean -- the algorithm already computes it. Stop the loop when `i > farthest` and return the last value of `farthest`.

</details>

## Common mistakes

- **Summing the jumps instead of tracking the frontier.** A beginner
  sometimes writes `reach = i + nums[i]` but then checks `reach == last`
  instead of `reach >= last`. Jumps can overshoot the last index, which is
  legal, so the test must be `>=`.
- **Forgetting the failure check `i > farthest`.** Without it the loop
  happily walks past an unreachable region and counts `nums[i]` from an
  index you could never stand on, returning a false `true` on inputs like
  `[3,2,1,0,4]`.
- **Using `i < last` as the loop bound.** This skips the last index and
  can misreport on single-element inputs or when the last element is the
  trap. Use `i <= last` and let the early returns do their job.
- **Treating `nums[i]` as the *exact* jump length.** The problem allows any
  length up to `nums[i]`; the greedy rule works precisely because
  "at most nums[i]" is captured by taking the max extension.
- **Returning false too eagerly when `nums[i] == 0`.** A zero at index `i`
  only blocks progress if every later index is reachable *only* through
  `i`. The frontier variable already encodes that; do not special-case
  zeros.

## Related problems

- [0045 - Jump Game II](../0045-jump-game-ii/) - the same frontier
  invariant, but now you must *count* the minimum number of jumps.
- [0053 - Maximum Subarray](../0053-maximum-subarray/) - another single
  running-variable greedy (the running sum).
- [0134 - Gas Station](../0134-gas-station/) - reachability on a circular
  track, solved with a running-tank reset rule.
