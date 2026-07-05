# 0015 - 3Sum

**Difficulty:** Medium
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/3sum/

## Problem

Given an integer array `nums`, return all the **unique** triplets
`[nums[i], nums[j], nums[k]]` such that `i != j != k != i` and
`nums[i] + nums[j] + nums[k] == 0`. The solution set must not contain duplicate
triplets, but the input may contain duplicate values.

Signature:

    List<List<Integer>> threeSum(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [-1,0,1,2,-1,-4]
    Output: [[-1,-1,2],[-1,0,1]]

    Input:  nums = [0,1,1]
    Output: []

    Input:  nums = [0,0,0]
    Output: [[0,0,0]]

## Intuition

A naive three-nested-loop solution is O(n^3). The breakthrough is to **sort**
first (O(n log n)) and then reduce the problem to repeated Two Sum II: fix the
first element `nums[i]`, and use opposite-end pointers on the rest of the array
to find pairs that sum to `-nums[i]`. That drops the inner cost to O(n), for
O(n^2) overall.

The only remaining trap is **duplicate triplets**. After sorting, equal values
sit next to each other, so we skip ahead past equal neighbours whenever we fix
`i` or find a matching pair. Without these skips, an input like
`[-2,0,0,2,2]` would emit `[-2,0,2]` twice.

## Pseudocode

```text
function threeSum(nums):
    sort nums in non-decreasing order
    result = empty list
    for i from 0 to length - 3:
        if nums[i] is the same as nums[i-1]:   # skip duplicate first elements
            continue
        set left to i + 1
        set right to last index
        target to -nums[i]
        while left < right:
            pairSum = nums[left] + nums[right]
            if pairSum equals target:
                add [nums[i], nums[left], nums[right]] to result
                advance left past any value equal to nums[left]
                move right back past any value equal to nums[right]
                advance left by one
                move right back by one
            else if pairSum is less than target:
                advance left
            else:
                move right back by one
    return result
```

## Java Solution

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int left = i + 1, right = n - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }
}
```

The outer loop stops at `n - 2` because we need two more elements after `i`. The
first skip (`i > 0 && nums[i] == nums[i-1]`) prevents fixing the same first
value twice. Inside, after a hit, the two inner `while` loops slide `left` and
`right` to the *last* occurrence of the just-used value, and the `left++` /
`right--` then step onto a fresh value — together they guarantee the next pair
is genuinely different. `Arrays.asList` builds the immutable triplet; we do not
need to mutate it afterwards.

## Complexity

    Time:  O(n^2)  -- outer loop O(n); inner two-pointer scan O(n); sort is O(n log n), dominated.
    Space: O(1)    -- ignoring the output list; no auxiliary structure beyond a few indices.
                      (Java's sort on primitives uses O(log n) stack for quicksort.)

## Dry-Run

Step-by-step on `nums = [-1,0,1,2,-1,-4]`. After sorting: `[-4,-1,-1,0,1,2]`.

Outer loop, `i = 0` (`nums[i] = -4`, target `= 4`):

| left | right | nums[left] | nums[right] | sum | action |
|------|-------|------------|-------------|-----|--------|
| 1 | 5 | -1 | 2 | -3 | -3 < 0 -> left++ |
| 2 | 5 | -1 | 2 | -3 | < 0 -> left++ |
| 3 | 5 | 0 | 2 | -2 | < 0 -> left++ |
| 4 | 5 | 1 | 2 | -1 | < 0 -> left++ (left==right, exit) |

No triplet for `-4`.

`i = 1` (`nums[i] = -1`, target `= 1`):

| left | right | nums[left] | nums[right] | sum | action |
|------|-------|------------|-------------|-----|--------|
| 2 | 5 | -1 | 2 | 0 | == 0 -> record [-1,-1,2]; dedup (no equal neighbours); left=3, right=4 |
| 3 | 4 | 0 | 1 | 0 | == 0 -> record [-1,0,1]; dedup; left=4, right=3 (exit) |

`i = 2`: `nums[2] == nums[1]` (-1 == -1) -> **skip** (this is the dedup that
prevents re-recording `[-1,-1,2]` and `[-1,0,1]`).

`i = 3, 4`: only one or two elements remain, no full triplet possible.

Result: `[[-1,-1,2],[-1,0,1]]`. The sort plus dedup is what makes the output
exactly the unique triplets.

## Common mistakes

- **Forgetting to skip duplicates.** The two `while` loops inside the hit
  branch, plus the `nums[i] == nums[i-1]` check, are the whole point. Miss one
  and you get duplicate triplets.
- **Dedup then forgetting the final `left++; right--`.** The `while` loops only
  reach the *last* equal value; you still must step past it.
- **Not sorting first.** Without sorting, the two-pointer direction logic and
  the neighbour-equality dedup both break.
- **Returning the same triplet in different orders.** Sorting up front fixes a
  canonical order, so equal triplets look byte-identical and dedup is trivial.
- **Using a `Set` to dedup instead of skipping.** Works but costs O(n) extra
  space and is slower; the in-place skips are the intended solution.

## Related problems

- [0167 - Two Sum II](../0167-two-sum-ii/) - the inner loop is literally this
  problem with a fixed first element.
- [0011 - Container With Most Water](../0011-container-with-most-water/) - same
  opposite-ends skeleton, different decision rule.
- [0018 - 4Sum] - the natural extension: fix two elements, then two pointers.
