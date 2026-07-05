# 0219 - Contains Duplicate II

**Difficulty:** Easy
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/contains-duplicate-ii/

## Problem

Given an integer array `nums` and an integer `k`, return `true` if there exist two
distinct indices `i` and `j` such that `nums[i] == nums[j]` and `abs(i - j) <= k`.
Otherwise return `false`.

Signature:

    boolean containsNearbyDuplicate(int[] nums, int k)

Example 1:

    Input:  nums = [1,2,3,1], k = 3
    Output: true
    (nums[0] == nums[3] and abs(0 - 3) = 3 <= 3.)

Example 2:

    Input:  nums = [1,0,1,1], k = 1
    Output: true
    (nums[2] == nums[3] and abs(2 - 3) = 1 <= 1.)

Example 3:

    Input:  nums = [1,2,3,1,2,3], k = 2
    Output: false

## Intuition

The constraint `abs(i - j) <= k` says: the two equal values must sit within a window of
width `k`. So slide a window of the last `k` elements across the array; the window is a
`HashSet` of values. At each new element, ask "is this value already in the window?" --
a hit means a duplicate within distance `k`. This is a fixed-distance window: the set
always holds exactly the most recent `k` values seen.

## Pseudocode

    function containsNearbyDuplicate(nums, k):
        window = empty set
        for right from 0 to len(nums)-1:
            if nums[right] in window:
                return true                     # duplicate within the last k indices
            add nums[right] to window
            if size of window > k:              # window would reach back further than k
                remove nums[right - k] from window
        return false

After processing index `right`, the set contains the last `k` values (indices
`right-k+1 .. right`), which is exactly what the next iteration needs to check.

## Java Solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();
        for (int right = 0; right < nums.length; right++) {
            if (window.contains(nums[right])) {
                return true;
            }
            window.add(nums[right]);
            if (window.size() > k) {
                window.remove(nums[right - k]);
            }
        }
        return false;
    }
}
```

A `HashSet` is enough: we only ever ask "is this value within range?", not "where was
it?". The set holds the most recent `k` values, so `contains` is the duplicate check.
The eviction line `remove(nums[right - k])` fires exactly when the window has grown past
`k`, keeping the look-back range at `<= k`. Note the guard `size > k`, not `>= k`: the
window is allowed to hold `k` values, evict only the `k+1`-th. The edge `k = 0` works
for free -- `size > 0` is always true after an add, so each value is removed the moment
it is inserted, and the answer is always `false` (two distinct indices cannot satisfy
`abs(i - j) <= 0`).

## Complexity

    Time:  O(n)   -- one pass; each element is added once and removed at most once.
    Space: O(k)   -- the set never holds more than k+1 elements.

## Dry-Run

Step-by-step on `nums = [1,2,3,1], k = 3`:

| right | nums[right] | window (before) | in window? | window (after add) | size > 3? | evict | window (end) |
|-------|-------------|-----------------|------------|--------------------|-----------|-------|--------------|
| 0     | 1           | {}              | no         | {1}                | no        | -     | {1}          |
| 1     | 2           | {1}             | no         | {1,2}              | no        | -     | {1,2}        |
| 2     | 3           | {1,2}           | no         | {1,2,3}            | no        | -     | {1,2,3}      |
| 3     | 1           | {1,2,3}         | **YES**    | return true        | -         | -     | -            |

Return `true`.

## Common mistakes

- Storing a `Map<Integer, Integer>` of every index instead of just the last `k` values --
  works but wastes memory and obscures the sliding-window idea.
- Forgetting to evict the element that falls out of range -> false positives for pairs
  more than `k` apart.
- Off-by-one on the eviction: `remove(nums[right - k - 1])` or evicting *before* the size
  check breaks the "window holds last k" invariant.
- Treating the condition as `abs(i - j) < k` (strict) instead of `<= k`.
- Crashing on `k = 0`. The guard `size > k` handles it; `right - k` is never negative
  because eviction only runs after at least `k + 1` adds.

## Related problems

- [0003 - Longest Substring Without Repeating Characters](../0003-longest-substring-without-repeating-characters/) --
  the same "hash structure tracks the recent window" reflex, applied to a variable-size
  window.
- [0209 - Minimum Size Subarray Sum](../0209-minimum-size-subarray-sum/) -- a
  variable-size window whose state is a running sum rather than a set.
