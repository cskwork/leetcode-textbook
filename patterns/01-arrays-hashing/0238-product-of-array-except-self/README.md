# 0238 - Product of Array Except Self

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/product-of-array-except-self/

## Problem

Given an integer array `nums`, return an array `answer` such that `answer[i]` is the product of
all elements of `nums` except `nums[i]`. The algorithm must run in O(n) time and must **not use
the division operator**. The product of any prefix or suffix fits in a 32-bit integer.

Signature:

    int[] productExceptSelf(int[] nums)

Examples:

    Input:  nums = [1,2,3,4]
    Output: [24,12,8,6]
        # 24 = 2*3*4, 12 = 1*3*4, 8 = 1*2*4, 6 = 1*2*3

    Input:  nums = [-1,1,0,-3,3]
    Output: [0,0,9,0,0]

## Intuition

The brute-force "multiply every other element for each i" is O(n^2). Division would make it O(n)
(count zeros, divide total by nums[i]) but division is banned -- and breaks on zeros anyway. The
key insight: the product-except-self at position `i` is exactly **(product of everything to the
left of i) x (product of everything to the right of i)**. So if we precompute all left-products
and all right-products, each answer is one multiplication. We can even fuse both passes into the
single output array: the left-to-right sweep writes the running left-product into each slot, and
the right-to-left sweep multiplies in the running right-product. No division, no extra array --
the "memory" lives in two running integers.

## Pseudocode

    function productExceptSelf(nums):
        n <- length of nums
        create result array of size n, all zeros
        prefix <- 1                      # product of everything strictly to the LEFT of i
        for i from 0 to n-1:
            result[i] <- prefix
            prefix <- prefix * nums[i]
        postfix <- 1                     # product of everything strictly to the RIGHT of i
        for i from n-1 down to 0:
            result[i] <- result[i] * postfix
            postfix <- postfix * nums[i]
        return result

## Java Solution

```java
class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        int prefix = 1;
        for (int i = 0; i < n; i++) {
            result[i] = prefix;
            prefix *= nums[i];
        }

        int postfix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= postfix;
            postfix *= nums[i];
        }
        return result;
    }
}
```

`prefix` carries the product of all elements before the current index; we write it into
`result[i]` *before* multiplying in `nums[i]`, so `prefix` always lags one step behind `i` --
that is what "strictly to the left" means. The second sweep does the mirror image from the right,
multiplying each slot by the running `postfix`. The output array does not count as extra space
per the problem's definition, so the only auxiliary storage is the two integer accumulators: O(1)
extra space. Zeros need no special handling because we never divide.

## Complexity

    Time:  O(n)   -- two independent linear passes
    Space: O(1)   -- only two integer accumulators beyond the output array

## Dry-Run

Input `nums = [1, 2, 3, 4]`:

First pass (write left-products):

| i | prefix (before) | result[i] set to | prefix (after = prefix * nums[i]) |
|--:|----------------:|-----------------:|----------------------------------:|
| 0 | 1               | 1                | 1 * 1 = 1                         |
| 1 | 1               | 1                | 1 * 2 = 2                         |
| 2 | 2               | 2                | 2 * 3 = 6                         |
| 3 | 6               | 6                | 6 * 4 = 24                        |

result = `[1, 1, 2, 6]`.

Second pass (multiply right-products):

| i | postfix (before) | result[i] becomes | postfix (after = postfix * nums[i]) |
|--:|-----------------:|------------------:|------------------------------------:|
| 3 | 1                | 6 * 1 = 6         | 1 * 4 = 4                           |
| 2 | 4                | 2 * 4 = 8         | 4 * 3 = 12                          |
| 1 | 12               | 1 * 12 = 12       | 12 * 2 = 24                         |
| 0 | 24               | 1 * 24 = 24       | 24 * 1 = 24                         |

result = `[24, 12, 8, 6]`. Output matches.

## Common mistakes

- Multiplying in `nums[i]` *before* writing `prefix` to `result[i]`. Then slot `i` would include
  the element it should exclude, defeating the whole algorithm.
- Running the second pass left-to-right as well. The right-products must accumulate from the end,
  or each slot multiplies the wrong suffix.
- Using division to divide a total product by `nums[i]`. Banned by the problem, and it crashes on
  zero (and mishandles multiple zeros).
- Allocating separate `left[]` and `right[]` arrays. Correct and O(n) time, but O(n) extra space;
  fusing into one output array gives O(1) extra space, which is the point.
- Returning `result` before the second pass. Half the multiplications would be missing.

## Related problems

- [0347 - Top K Frequent Elements](../0347-top-k-frequent/) - another "two passes, no extra work
  per element" structure, but over a map.
- [0128 - Longest Consecutive Sequence](../0128-longest-consecutive-sequence/) - the other O(n)
  standout in this section that forbids the obvious O(n log n) sort.
- [0001 - Two Sum](../0001-two-sum/) - the chapter's other "trade a pass for a lookup" idea.
