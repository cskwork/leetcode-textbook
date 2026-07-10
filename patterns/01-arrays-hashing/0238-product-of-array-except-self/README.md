# 0238 - Product of Array Except Self

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/product-of-array-except-self/

## Concepts used

- **Array** -- a row of numbered slots; the answer is built directly inside one.
  [glossary](../../../docs/10-glossary.md#array)
- **Linear scan** -- walking the array once in each direction (two passes total).
  [glossary](../../../docs/10-glossary.md#linear-scan)
- **In-place** -- computing the answer using only a fixed amount of extra memory beyond the
  output. [glossary](../../../docs/10-glossary.md#in-place)

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

Picture a row of four switches. For each switch you must report "the combined setting of all the
OTHER switches." The slow way: for switch `i`, walk the whole row again, skipping `i`. The fast
way: notice that the answer for position `i` is simply (everything to its left) times (everything
to its right). If you knew the running product of all left neighbors and all right neighbors, each
answer would be one multiplication.

Check the smallest case, `nums = [1, 2, 3, 4]`. The answer for slot 2 (the value `3`) should be
`1 * 2 * 4 = 8`. That splits as (product of the left, `1 * 2 = 2`) times (product of the right,
`4`): `2 * 4 = 8`. Correct.

The general rule: for position `i`, the answer equals (product of everything strictly left of `i`)
times (product of everything strictly right of `i`). Make two passes over a single output
[array](../../../docs/10-glossary.md#array). Pass one runs left-to-right carrying a running "left
product": at each slot, write the running product in, then multiply the current element into it.
Pass two runs right-to-left carrying a running "right product", multiplying it into each slot.
After both passes every slot holds left times right. Division is never used, so zeros cause no
trouble.

Why write the running product BEFORE multiplying in the current element? Because the left product
for slot `i` must exclude `nums[i]`. Writing `prefix` into `result[i]` first and only then doing
`prefix *= nums[i]` keeps `nums[i]` out of slot `i` -- it joins the running product for the next
slot instead. The right pass mirrors this exactly, and because we reuse the one output array, only
two integer accumulators are needed beyond the answer itself -- an
[in-place](../../../docs/10-glossary.md#in-place) result.

### Checkpoint A -- Prefix and postfix

Pause before expanding.

**Q1 (recall).** The answer for position `i` equals (product of everything strictly ___ of i) times (product of everything strictly ___ of i).
- a) left of / right of
- b) equal to / less than
- c) before sort / after sort

<details><summary>Show answer</summary>

**(a)** -- "left of" times "right of". Each slot's answer excludes exactly its own element, so it's everything on the left paired with everything on the right.

</details>

**Q2 (comprehend).** In the left-to-right pass, why write `prefix` into `result[i]` BEFORE doing `prefix *= nums[i]`?
- a) Order doesn't matter
- b) So that `nums[i]` is excluded from its own slot; it only joins the running product for the NEXT slot
- c) To avoid overflow

<details><summary>Show answer</summary>

**(b)** -- writing first keeps slot i as the product strictly to its left. Multiplying afterward rolls `nums[i]` into `prefix` for the benefit of slot i+1.

</details>

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

### Checkpoint B -- Trace, zeros, and a variant

**Q1 (apply).** Left pass on `nums = [a, b]`: what does `result` become after the LEFT pass only?
- a) `[a, b]`
- b) `[1, a]` (prefix at each slot, before its own element)
- c) `[a*b, a*b]`

<details><summary>Show answer</summary>

**(b)** -- slot 0 gets prefix=1, then prefix*=a -> a; slot 1 gets prefix=a. So `result = [1, a]`. The right pass then yields `[b, a]`, the product-except-self.

</details>

**Q2 (analyze).** The problem forbids division. Why does division also BREAK on input like `[0, 1, 2, 0]`?
- a) Division is slow
- b) Dividing a total product by 0 is undefined, and multiple zeros make every "except self" product zero, which naive division can't distinguish
- c) It doesn't break, it's just banned

<details><summary>Show answer</summary>

**(b)** -- you can't divide by a zero element, and with two or more zeros the nonzero cases are themselves zero, which division-based code mishandles. The prefix/postfix method never divides, so zeros are a non-issue.

</details>

**Q3 (transfer).** Could you solve this with explicit `left[]` and `right[]` arrays? What do you gain and lose versus the fused one-array version?

<details><summary>Show answer</summary>

Yes -- compute left and right products separately, then multiply pointwise. It's easier to read but uses O(n) EXTRA space; the fused version reuses the output array for O(1) extra space, which is the problem's intended point.

</details>

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
