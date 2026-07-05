# 0167 - Two Sum II - Input Array Is Sorted

**Difficulty:** Medium
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/

## Problem

Given a **1-indexed** integer array `numbers` already sorted in non-decreasing
order, find two numbers that add up to a specific `target`. Return the indices
of the two numbers, `[index1, index2]`, as an integer array of length 2 where
`index1 < index2` and both are **1-based**. The problem guarantees exactly one
solution and you may not use the same element twice.

Signature:

    int[] twoSum(int[] numbers, int target)

Examples (verbatim from LeetCode):

    Input:  numbers = [2,7,11,15], target = 9
    Output: [1,2]

    Input:  numbers = [2,3,4], target = 6
    Output: [1,3]

    Input:  numbers = [-1,0], target = -1
    Output: [1,2]

## Intuition

This is the canonical opposite-ends Two-Pointer problem, and the trigger is the
word "sorted". The hash-map trick from LC 1 (Two Sum) would also work, but it
ignores the sorting — and ignoring a given guarantee is always a hint that a
better tool exists. With sorted data we can avoid the O(n) hash map entirely and
use O(1) space.

Put one pointer at each end and look at their sum. Because the array is sorted,
advancing `left` can only increase the sum and retreating `right` can only
decrease it. So at each step we know *exactly* which pointer to move to head
toward `target`. Each element is considered once, giving O(n) time.

## Pseudocode

```text
function twoSumSorted(numbers, target):
    set left to first index
    set right to last index
    while left < right:
        set current to numbers[left] + numbers[right]
        if current equals target:
            return [left + 1, right + 1]   # 1-indexed
        if current is less than target:
            advance left                   # need a bigger sum
        else:  # current is greater than target
            move right back one            # need a smaller sum
    # problem guarantees a solution, so we never reach here
    return an empty pair
```

## Java Solution

```java
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1};
            }
            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }
}
```

The loop invariant is `left < right`: the two indices must point at distinct
elements (the problem forbids reusing one). The `+1` on the return converts from
Java's 0-based indices to the 1-based indices LeetCode wants — this is the most
common silly mistake on the problem, so it is the one line worth remembering.
The final `return new int[]{-1,-1}` is unreachable because the problem promises
exactly one solution; we include it only so the compiler is satisfied that every
path returns.

## Complexity

    Time:  O(n)  -- each iteration moves exactly one pointer, so at most n iterations.
    Space: O(1)  -- two index variables plus the 2-element result; no extra structure.

## Dry-Run

Step-by-step on `numbers = [2,7,11,15]`, `target = 9`:

| Step | left | right | numbers[left] | numbers[right] | sum | action |
|------|------|-------|---------------|----------------|-----|--------|
| 1 | 0 | 3 | 2 | 15 | 17 | 17 > 9 -> right-- |
| 2 | 0 | 2 | 2 | 11 | 13 | 13 > 9 -> right-- |
| 3 | 0 | 1 | 2 | 7 | 9 | 9 == 9 -> return [0+1, 1+1] = [1,2] |

Three iterations; the answer is `[1, 2]`. Notice `left` never moved — the sorted
property let us shrink only from the right until the pair was tight enough.

## Common mistakes

- **Returning 0-based indices.** LeetCode wants 1-based; forgetting the `+1`
  fails every test even though the logic is right.
- **Moving the wrong pointer.** If `sum < target` you must move `left` (toward
  larger values). Swapping the branches makes the pointers walk away from the
  answer and either loop forever or return garbage.
- **Using `<=` instead of `<` in the loop.** `left == right` would let an
  element pair with itself, which the problem forbids.
- **Reaching for the hash map.** It works but wastes O(n) space and ignores the
  "sorted" hint — an interviewer will ask you to remove it.

## Related problems

- [0001 - Two Sum](../../01-arrays-hashing/) (Pattern 1) - the unsorted version,
  solved with a hash map. Compare the two approaches to see when each wins.
- [0015 - 3Sum](../0015-3sum/) - the same opposite-ends inner loop, wrapped in
  an outer "fix one element" loop.
- [0011 - Container With Most Water](../0011-container-with-most-water/) - same
  shape, but the move decision is based on heights, not a sum.
