# 0219 - Contains Duplicate II

**Difficulty:** Easy
**Pattern:** Sliding Window
**LeetCode:** https://leetcode.com/problems/contains-duplicate-ii/

## Concepts used

- **Hash set** -- a container that stores only keys and answers "have I seen this value before?" in O(1) average; here it holds the values in the recent window. [glossary](../../../docs/10-glossary.md#hash-set)
- **Sliding window** -- a fixed-width window of the last `k` elements sliding across the array. [glossary](../../../docs/10-glossary.md#sliding-window)

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

You are checking tickets at a gate, and the rule is "no two identical tickets within the
last `k` people". You keep a tray holding the tickets of the most recent `k` people. For
each new person, you glance at the tray -- if their ticket is already there, that is a
violation (return true). Then you drop their ticket onto the tray, and if the tray now
holds more than `k` tickets, you remove the one that has fallen out of range.

The condition `abs(i - j) <= k` is just another way of saying "the two equal values must
sit within `k` positions of each other". So we only need to remember the last `k` values
seen -- nothing older can ever be part of a valid pair.

Smallest example, `nums = [1, 2, 3, 1], k = 3`:

- right=0, value 1: tray {} does not contain 1. Add -> {1}.
- right=1, value 2: tray {1} does not contain 2. Add -> {1, 2}.
- right=2, value 3: tray {1, 2} does not contain 3. Add -> {1, 2, 3}.
- right=3, value 1: tray {1, 2, 3} DOES contain 1! Return true. (Distance 3 - 0 = 3 <= 3.)

General rule: keep a hash set of the last `k` values. At each new element, check membership
-- a hit means a nearby duplicate. Then add the element, and if the set now holds more than
`k` values, remove the one that fell out of range (`nums[right - k]`).

The **invariant** is: *after processing index `right`, the set holds exactly the values at
indices `right-k+1 .. right`*. So the next iteration's membership check covers precisely the
allowed look-back range. Note the guard is `size > k`, not `>= k`: the window is allowed to
hold `k` values, and we evict only the `(k+1)`-th. The edge case `k = 0` works for free --
each value is removed the instant it is added, so the answer is always false (two *distinct*
indices can never be 0 apart).

This is a fixed-distance sliding window. [0003 Longest Substring Without Repeating Characters](../0003-longest-substring-without-repeating-characters/)
reuses the "hash structure tracks the recent window" reflex with a window that grows and
shrinks, and [0209 Minimum Size Subarray Sum](../0209-minimum-size-subarray-sum/) uses a
window whose state is a running sum rather than a set.

### Checkpoint A -- The last-k tray

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** After processing index `right`, what does the hash set contain?
- a) Every value seen so far
- b) Exactly the values in the most recent `k` positions
- c) The values at even indices only

<details><summary>Show answer</summary>

**(b)** -- the set holds the last `k` values (indices `right-k+1 .. right`), which is exactly the range the next check needs to scan.

</details>

**Q2 (comprehend).** Why is the eviction guard `size > k` and not `>= k`?
- a) Because the window is allowed to hold up to `k` values, so we evict only the `(k+1)`-th
- b) Because `>= k` would skip a value
- c) Because sets cannot hold exactly `k` items

<details><summary>Show answer</summary>

**(a)** -- a look-back of `<= k` permits `k` values in the tray. Eviction must wait until the tray overflows to `k+1`, hence the strict `>`.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [1, 2, 1], k = 1`. What is returned?
- a) `true`
- b) `false`

<details><summary>Show answer</summary>

**(b)** -- right=0 adds {1}; right=1 adds {1,2} then evicts nums[0]=1 -> {2}; right=2 sees 1 not in {2}, adds {1,2}, evicts nums[1]=2 -> {1}. The two 1's sit 2 apart (> k=1), so the loop finishes and returns `false`.

</details>

**Q2 (analyze).** What happens when `k = 0`, and why is no special case needed?
- a) The answer is always `false`; each value is evicted the instant it is added, so no two distinct indices can ever be 0 apart
- b) It throws `IndexOutOfBoundsException`
- c) It returns `true` for any array

<details><summary>Show answer</summary>

**(a)** -- after every add `size > 0` is true, so the just-added value is immediately removed; the set is empty at the next check. Two distinct indices satisfy `abs(i-j) <= 0` is impossible, so `false` is correct.

</details>

**Q3 (transfer).** Suppose instead of a boolean you had to return the *smallest* index distance between any equal-valued pair within `k`. What minimal change captures that?

<details><summary>Show answer</summary>

Swap the set for a map value -> most-recent-index: on each hit, compute `right - lastIndex` and keep the minimum over the whole scan; never jump backward. The eviction idea is replaced by simply overwriting the stored index each time.

</details>

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
