# 0128 - Longest Consecutive Sequence

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/longest-consecutive-sequence/

## Problem

Given an unsorted array of integers `nums`, return the length of the longest sequence of
consecutive integers that can be formed from its elements. The algorithm must run in **O(n)**
time. Duplicates in the input count only once (a sequence is a set of consecutive values).

Signature:

    int longestConsecutive(int[] nums)

Examples:

    Input:  nums = [100,4,200,1,3,2]
    Output: 4         # the sequence is [1, 2, 3, 4]

    Input:  nums = [0,3,7,2,5,8,4,6,0,1]
    Output: 9         # the sequence is [0,1,2,3,4,5,6,7,8]

## Intuition

Sorting would give O(n log n) -- close, but the problem demands O(n). The hashing trick is: dump
every value into a HashSet, which gives O(1) "is x present?" forever after. Now, a consecutive
sequence is just a chain `x, x+1, x+2, ...` all present in the set. The crucial optimization: we
must not walk the chain from every value -- that would revisit numbers and blow up to O(n^2).
Instead, walk the chain **only from its start**. A value `x` is a start exactly when `x - 1` is
*not* in the set (nothing comes before it). For every start we count how long the chain runs; for
non-starts we do nothing. Each value is touched by a chain-walk at most once across the whole run,
so the total work is O(n).

## Pseudocode

    function longestConsecutive(nums):
        put every value of nums into a set (duplicates collapse)
        best <- 0
        for each value x in the set:
            if (x - 1) is NOT in the set:          # x begins a sequence
                length <- 1
                next <- x + 1
                while next is in the set:
                    length <- length + 1
                    next <- next + 1
                best <- max(best, length)
        return best

## Java Solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) {
            set.add(x);
        }

        int best = 0;
        for (int x : set) {
            if (!set.contains(x - 1)) {
                int length = 1;
                int next = x + 1;
                while (set.contains(next)) {
                    length++;
                    next++;
                }
                best = Math.max(best, length);
            }
        }
        return best;
    }
}
```

We iterate over the *set* (not the original array) so duplicates are not re-examined. The guard
`!set.contains(x - 1)` is the whole algorithm: it ensures the inner `while` only fires from a
sequence's leftmost element. Although the `while` looks like it could be O(n) per element, every
number is part of at most one chain and is consumed once across the entire outer loop, so the
amortized cost is O(n). An empty array yields an empty set, the loop never runs, and `best` stays
`0` -- the correct answer.

## Complexity

    Time:  O(n)  -- building the set is O(n); each value is walked by a chain at most once
    Space: O(n)  -- the set holds up to every distinct value

## Dry-Run

Input `nums = [100, 4, 200, 1, 3, 2]`. Set = `{1, 2, 3, 4, 100, 200}`.

For each `x`, check whether `x - 1` is present (i.e., is `x` a start?):

| x   | x-1 in set? | is start? | chain walked            | length | best |
|----:|-------------|-----------|-------------------------|-------:|-----:|
| 100 | 99? no      | yes       | 100, 101? no            | 1      | 1    |
| 4   | 3? yes      | no        | (skip)                  | -      | 1    |
| 200 | 199? no     | yes       | 200, 201? no            | 1      | 1    |
| 1   | 0? no       | yes       | 1, 2, 3, 4, 5? no       | 4      | 4    |
| 3   | 2? yes      | no        | (skip)                  | -      | 4    |
| 2   | 1? yes      | no        | (skip)                  | -      | 4    |

Output: `4` (the chain `1, 2, 3, 4`).

Notice only the three starts (100, 200, 1) did any chain-walking; 4, 3, 2 were skipped because
each has a predecessor in the set.

## Common mistakes

- Starting a chain from *every* element and walking up. It returns the right answer but is O(n^2)
  in the worst case (e.g. `nums = [1,2,3,...,n]`); the start-guard is what keeps it O(n).
- Sorting first. Correct and simpler to reason about, but O(n log n), violating the constraint.
- Re-counting duplicates by iterating over `nums` instead of the set. Slower and redundant.
- Using `set.contains(next)` inside `while` but forgetting to advance `next` -- infinite loop.
- Returning `best + 1` or initializing `length` to 0. The start value itself already counts as 1,
  so the length must begin at 1.

## Related problems

- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the same "load everything into a set,
  then query" shape.
- [0347 - Top K Frequent Elements](../0347-top-k-frequent/) - the other O(n) standout in this
  section that refuses the obvious sort.
- [0001 - Two Sum](../0001-two-sum/) - existence checks against a set, this time chaining upward.
