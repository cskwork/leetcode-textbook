# 0045 - Jump Game II

**Difficulty:** Medium
**Pattern:** Greedy
**LeetCode:** https://leetcode.com/problems/jump-game-ii/

## Problem

Same setup as Jump Game: integer array `nums`, you start at index `0`, and
`nums[i]` is the **maximum** jump length from index `i`. The input is
guaranteed to be reachable. Return the **minimum number of jumps** to reach
the last index.

Signature:

    int jump(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [2,3,1,1,4]
    Output: 2
    Explanation: jump from 0 to 1 (1 jump), then from 1 to the last index (2 jumps total).

    Input:  nums = [2,3,0,1,4]
    Output: 2

## Intuition

The trigger: "minimum number of jumps" — a minimum-cost reachability
problem. DP (count jumps to each index) is O(n^2) worst case. Greedy
shrinks it to O(n) by thinking of the array as **levels**, like BFS layers
in a graph.

Imagine the indices as nodes, and the set of indices reachable in `j` jumps
as "level `j`". You do not care *which* index within a level you stand on —
you only care how far that whole level can reach in one more jump. So you
scan left to right, maintaining two boundaries:

- `currentEnd` — the farthest index reachable using the current jump count.
- `farthest` — the farthest index reachable using one *more* jump from any
  index seen so far in this level.

When the cursor `i` hits `currentEnd`, you have finished exploring the
current level: every index in it has contributed to `farthest`. Commit to
one more jump (increment the counter) and extend `currentEnd` to
`farthest`. Repeat until `currentEnd` reaches the last index.

**Proof sketch.** This is greedy because once you have committed to the
indices reachable in `j` jumps, you never reconsider that set — you simply
expand the frontier to the union of everything one hop further. BFS is
itself a greedy algorithm (it always settles the next-closest layer), and
this is one-dimensional BFS where the "neighbours" are implicit in
`nums[i]`. The local choice ("take the whole next level") is safe because
any minimum-jump solution must pass through some index in each level, and
the level boundary is the same regardless of which index you picked.

## Pseudocode

```text
function jump(nums):
    last = length of nums - 1
    if last is 0:
        return 0                       # already at the goal
    jumps = 0                          # number of jumps committed so far
    currentEnd = 0                     # farthest index reachable with `jumps` jumps
    farthest = 0                       # farthest index reachable with one more jump
    for i from 0 to last:
        reach = i + nums[i]
        if reach is greater than farthest:
            farthest = reach
        if farthest is at least last:
            return jumps + 1           # one more jump lands on (or past) the goal
        if i equals currentEnd:        # level fully explored
            jumps = jumps + 1          # commit to the next level
            currentEnd = farthest
    return jumps
```

The early return on `farthest >= last` saves the final useless level
expansion; it fires the moment the goal enters the next level.

## Java Solution

```java
class Solution {
    public int jump(int[] nums) {
        int last = nums.length - 1;
        if (last == 0) {
            return 0;
        }
        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;
        for (int i = 0; i <= last; i++) {
            int reach = i + nums[i];
            if (reach > farthest) {
                farthest = reach;
            }
            // If the next level already covers the goal, take it and stop.
            if (farthest >= last) {
                return jumps + 1;
            }
            // Finished scanning the current level: commit one jump and
            // advance the level boundary to everything we can now reach.
            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
            }
        }
        return jumps;
    }
}
```

`currentEnd` and `farthest` describe two consecutive BFS levels. The
condition `i == currentEnd` is the "level boundary" trigger; it fires once
per jump, which is why `jumps` ends up as the minimum count. The early
return `farthest >= last` is placed *before* the boundary check so that the
final jump that reaches the goal is counted exactly once and we never
accidentally expand an extra level. The single-element guard at the top
avoids returning 1 when no jump is needed.

## Complexity

    Time:  O(n)   -- each index is scanned exactly once; jumps increment at most n times.
    Space: O(1)   -- three integer variables.

## Dry-Run

Step-by-step on `nums = [2,3,1,1,4]` (last index = 4):

| Step | i | nums[i] | reach | farthest | farthest >= 4? | i == currentEnd? | jumps | currentEnd |
|------|---|---------|-------|----------|----------------|------------------|-------|------------|
| init |   |         |       | 0        |                |                  | 0     | 0          |
| 1    | 0 | 2       | 2     | 2        | no             | yes (i == 0)     | 1     | 2          |
| 2    | 1 | 3       | 4     | 4        | **yes**        | —                | —     | —          |

Return `jumps + 1 = 2`. The first level (index 0 alone) extends the
frontier to index 2; within that level, index 1 already shows the goal is
reachable in one more jump.

Contrast with `nums = [1,1,1,1]` (linear chain, last = 3):

| Step | i | nums[i] | reach | farthest | >= 3? | i == currentEnd? | jumps | currentEnd |
|------|---|---------|-------|----------|-------|------------------|-------|------------|
| init |   |         |       | 0        |       |                  | 0     | 0          |
| 1    | 0 | 1       | 1     | 1        | no    | yes              | 1     | 1          |
| 2    | 1 | 1       | 2     | 2        | no    | yes              | 2     | 2          |
| 3    | 2 | 1       | 3     | 3        | yes   | —                | —     | —          |

Return `jumps + 1 = 3` — three hops of length 1 each.

## Common mistakes

- **Counting a jump for every index.** A naive `jumps++` inside the loop
  yields the maximum, not the minimum, jump count. Jumps must increment
  only at the level boundary (`i == currentEnd`).
- **Confusing `currentEnd` with `farthest`.** They are different on purpose:
  `currentEnd` is the boundary of the level you are scanning, `farthest` is
  what the next level will be. Updating them together collapses the levels
  and breaks the count.
- **Missing the single-element guard.** With `n == 1` you are already at
  the goal, so 0 jumps; without the guard the loop returns 1.
- **Placing the goal check after the boundary check.** Doing `if (i ==
  currentEnd) jumps++` first, then `if (farthest >= last) return jumps`,
  works on most inputs but on a tight case can return one extra jump.
  Always test the goal against `farthest` (the next level) *before*
  committing.
- **Iterating only to `last - 1`.** Some correct formulations stop at
  `last - 1` because the last index never needs to issue a jump, but if you
  also use `i == currentEnd` to commit, the boundary must be allowed to
  reach `last`. Safer to iterate through `last` and rely on the early
  return.

## Related problems

- [0055 - Jump Game](../0055-jump-game/) - the same frontier invariant,
  used only for a yes/no reachability test.
- [1024 - Video Stitching] - the same "level expansion" greedy on
  intervals.
- [0134 - Gas Station](../0134-gas-station/) - another "running state +
  reset" greedy on a one-dimensional loop.
