# 0268 - Missing Number

**Difficulty:** Easy
**Pattern:** Bit Manipulation
**LeetCode:** https://leetcode.com/problems/missing-number/

## Problem

Given an array `nums` containing `n` **distinct** numbers taken from the
range `[0, n]` (inclusive), return the one number from that range that is
**missing** from the array.

Signature:

    int missingNumber(int[] nums)

Examples:

    Input:  nums = [3,0,1]
    Output: 2              # range [0,1,2,3]; 2 is absent

    Input:  nums = [0,1]
    Output: 2              # range [0,1,2]; 2 is absent

    Input:  nums = [9,6,4,2,3,5,7,0,1]
    Output: 8              # range [0..9]; 8 is absent

## Intuition

The trigger signal is "without extra space" combined with the "every value
appears ... except one" structure -- the same shape as Single Number (LC
136), but now the duplicate is supplied by the **index range itself**.

The full set of values that *should* be present is `{0, 1, 2, ..., n}` --
exactly `n + 1` values. The array holds `n` of them, so one is missing. If
we XOR together the desired set `{0, 1, ..., n}` and the actual array
contents, every value that appears in both cancels (`x ^ x == 0`), and the
one value that appears in the desired set but not the array survives
(`0 ^ x == x`). That survivor is the missing number.

The desired set `{0, 1, ..., n}` is conveniently produced by XOR-ing the
loop index `i` (which runs `0..n-1`) together with `n` itself. So the scan
is: accumulate `i ^ nums[i]` for each index, seed the accumulator with `n`,
and whatever is left at the end is the answer. This is the exact same XOR
identity that solved Single Number, applied to a different list.

### Checkpoint A -- Why seed with n?

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why is the XOR accumulator seeded with `n` (the array length) rather than `0`?
- a) Because XOR with `n` runs faster
- b) Because the loop index only reaches `n - 1`, so seeding `n` makes the full range `{0..n}` appear exactly once
- c) Because seeding with `0` would throw an error

<details><summary>Show answer</summary>

**(b)** -- the index `i` runs `0..n-1`, so the value `n` itself would never be XOR-ed in. Seeding with `n` puts it into the mix, covering every value the range `[0, n]` should contain.

</details>

**Q2 (comprehend).** In the dry-run, the combined list is `{0,1,2,3}` XOR `{3,0,1}`. Why does `2` survive while 0, 1, and 3 cancel?
- a) Because 2 is the largest value
- b) Because 2 appears in the range but never in the array, so it has nothing to cancel with; 0, 1, and 3 each appear in both lists and pair off
- c) Because 2 is a power of two

<details><summary>Show answer</summary>

**(b)** -- XOR only cancels a value that appears in BOTH the range and the array. Since 2 is in the range but missing from the array, it is left unpaired and survives in `running`.

</details>

## Pseudocode

    function missingNumber(nums):
        n <- length of nums
        running <- n                             # seed with n so 0..n is fully covered
        for each index i from 0 to n - 1:
            running <- running XOR i XOR nums[i] # present values cancel their index
        return running

In words: a value XOR itself is nothing; a value XOR nothing is itself. The
range 0..n and the array values are XOR-ed together, so every value present
in both cancels, and the one value present only in the range -- the missing
number -- is what the running XOR holds at the end.

## Java Solution

```java
class Solution {
    public int missingNumber(int[] nums) {
        int n = nums.length;
        int running = n;
        for (int i = 0; i < n; i++) {
            running ^= i ^ nums[i];
        }
        return running;
    }
}
```

The accumulator is seeded with `n` rather than `0` because the index `i`
only reaches `n - 1`; seeding with `n` ensures the full target range
`{0, ..., n}` is represented exactly once, so every value that *is* in the
array pairs with its copy in the range and cancels. Each iteration XORs the
index and the value together into `running`, and because XOR is
commutative/associative the order of the array is irrelevant. No extra
storage is allocated -- a single `int` -- so the space is genuinely O(1),
satisfying the constant-space goal that a `HashSet` solution would not.

### Alternative: the sum formula (Gauss)

The same problem has a beautiful arithmetic solution: the sum of
`{0, 1, ..., n}` is `n * (n + 1) / 2`. Subtract the actual sum of the array
and what remains is the missing number.

```java
class Solution {
    public int missingNumber(int[] nums) {
        int n = nums.length;
        long expected = (long) n * (n + 1) / 2;   // Gauss; cast to long to avoid overflow on the product
        long actual = 0;
        for (int v : nums) actual += v;
        return (int) (expected - actual);
    }
}
```

Both are O(n) time and O(1) space. The sum formula is usually easier to
explain in an interview; the XOR version is the bit-manipulation answer and
sidesteps overflow entirely (XOR never produces a value wider than its
inputs). For LeetCode's constraints (`n <= 10^4`, so `n*(n+1)/2 <= ~5*10^7`,
well within `int`), either is safe; the `long` cast above is just defensive
habit for larger ranges.

## Complexity

    Time:  O(n)   -- one pass; XOR (or addition) is O(1) per element
    Space: O(1)   -- a single accumulator integer

## Dry-Run

Input `nums = [3, 0, 1]`, so `n = 3` and the target range is `{0, 1, 2, 3}`.
Seed `running = n = 3`.

| Step | i | nums[i] | i ^ nums[i] (binary)       | running (before) | running (after) |
|-----:|--:|--------:|----------------------------|-----------------:|----------------:|
| seed | - | -       | -                          | -                | 3  = `011`      |
| 1    | 0 | 3       | `000` ^ `011` = `011` (3)  | 3  = `011`       | 0  = `000`      |
| 2    | 1 | 0       | `001` ^ `000` = `001` (1)  | 0  = `000`       | 1  = `001`      |
| 3    | 2 | 1       | `010` ^ `001` = `011` (3)  | 1  = `001`       | 2  = `010`      |

Output: `2`.

Why it lands on 2: think of the combined list XOR-ed together as
`{0, 1, 2, 3}` (the range) XOR `{3, 0, 1}` (the array). The `0`s cancel
(range has 0, array has 0), the `1`s cancel, the `3`s cancel -- but `2`
appears only in the range, never in the array, so it survives in `running`.

Sanity check with the sum formula: expected `= 3*4/2 = 6`; actual
`= 3+0+1 = 4`; `6 - 4 = 2`. Same answer.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `missingNumber([2, 0, 1])` (so `n = 3`, range `{0,1,2,3}`). What is returned?
- a) `3`
- b) `0`
- c) `2`

<details><summary>Show answer</summary>

**(a)** -- seed `running = 3`. Step 0: `3 ^ 0 ^ 2 = 1`; step 1: `1 ^ 1 ^ 0 = 0`; step 2: `0 ^ 2 ^ 1 = 3`. The value `3` is missing from the array, so it survives and is returned.

</details>

**Q2 (analyze).** Suppose you seeded the accumulator with `0` instead of `n`, on input `nums = [0, 1, 2]` (`n = 3`, so the true missing value is `3`). What would the code return?
- a) `3` -- still correct
- b) `0` -- wrong, because `3` is never XOR-ed into the accumulator
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- without the seed, `3` never enters the XOR. The values present (`0, 1, 2`) all cancel with their indices, leaving `0` -- incorrect, since the true missing number is `3`. The seed is what catches a missing `n`.

</details>

**Q3 (transfer).** The sum formula (Gauss) solves this too: `expected = n*(n+1)/2`, answer = `expected - actual_sum`. Give one advantage and one risk of the sum approach versus the XOR approach.

<details><summary>Show answer</summary>

Advantage: it is easier to explain and remember in an interview. Risk: `n * (n + 1)` can overflow a 32-bit `int` for large `n`, so one operand must be cast to `long` first. XOR never overflows, because it never produces a value wider than its inputs.

</details>

## Common mistakes

- Seeding the accumulator with `0` instead of `n`. The loop only feeds in
  indices `0..n-1`, so without seeding `n`, a missing `n` could never be
  produced. Seeding with `n` covers the full range `{0..n}`.
- XOR-ing only the values (forgetting the indices). That leaves `3 ^ 0 ^ 1`
  for the example, which is `2` here only by luck of this input -- it does
  not generalise. You must XOR the indices (and `n`) *against* the values.
- Integer overflow in the sum-formula variant. `n * (n + 1)` can exceed
  `Integer.MAX_VALUE` for large `n`; cast one operand to `long` before the
  multiply. LeetCode's `n <= 10^4` is safe, but the habit matters.
- Sorting first then scanning for the gap. O(n log n) time; both the XOR and
  the sum approaches are O(n) and simpler.
- Returning the index instead of the value, or off-by-one on the range
  boundary. The range is `[0, n]` inclusive on **both** ends -- `n + 1`
  values total, one of which is `n` itself.

## Related problems

- [0136 - Single Number](../0136-single-number/) - the same XOR identity in
  its purest form: pairs cancel, the lone value survives.
- [0191 - Number of 1 Bits](../0191-number-of-1-bits/) - the other great bit
  trick in this chapter: `n & (n-1)` clears the lowest set bit.
