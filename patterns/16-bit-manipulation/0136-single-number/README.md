# 0136 - Single Number

**Difficulty:** Easy
**Pattern:** Bit Manipulation
**LeetCode:** https://leetcode.com/problems/single-number/

## Problem

Given a **non-empty** array of integers `nums`, every element appears
**twice** except for one element which appears **once**. Find that single
element.

You must implement it with **linear runtime complexity** and use **only
constant extra space**.

Signature:

    int singleNumber(int[] nums)

Examples:

    Input:  nums = [2,2,1]
    Output: 1

    Input:  nums = [4,1,2,1,2]
    Output: 4

## Intuition

The phrase "every element appears twice except one" is the textbook trigger
signal for Bit Manipulation -- specifically for the **XOR identity**. The
overview's Bit Manipulation entry calls out exactly this: XOR, "single
number", "without extra space".

A frequency map would solve it in O(n) time and O(n) space, but the
"constant extra space" requirement forbids that. The XOR identity lets us
collapse the whole array into a single integer with no extra storage:

- `x ^ x == 0` -- a value XOR itself cancels to nothing.
- `x ^ 0 == x` -- XOR with zero is the identity.

Because XOR is commutative and associative, the order of the array does not
matter. If we XOR every number together, the values that appear twice pair
off and cancel (`a ^ a == 0`), and the one value that appears a single time
survives (`0 ^ that == that`). The running XOR at the end of the array *is*
the answer.

### Checkpoint A -- The XOR identity

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What is `x ^ x` for any integer `x` (where `^` is XOR)?
- a) `x`
- b) `0`
- c) `1`

<details><summary>Show answer</summary>

**(b)** -- any value XOR itself cancels to zero. This is the "pairs cancel" fact the whole solution rests on.

</details>

**Q2 (comprehend).** Why does the order of the array not matter to the final answer?
- a) Because the array is always sorted first
- b) Because XOR is commutative and associative, so `a ^ b ^ c` can be reordered and regrouped freely
- c) Because every value is stored in a hash set first

<details><summary>Show answer</summary>

**(b)** -- commutative (`a ^ b == b ^ a`) and associative means every duplicate pair meets its twin and cancels, no matter where it sits in the array.

</details>

## Pseudocode

    function singleNumber(nums):
        running <- 0                              # XOR with 0 is the identity
        for each value v in nums:
            running <- running XOR v              # pairs cancel, lone value stays
        return running

The key idea in words: a value XOR itself is nothing; a value XOR nothing is
itself. So XOR-ing the whole list leaves the one unpaired value behind.

## Java Solution

```java
class Solution {
    public int singleNumber(int[] nums) {
        int running = 0;
        for (int v : nums) {
            running = running ^ v;
        }
        return running;
    }
}
```

The accumulator starts at `0` because `x ^ 0 == x`, so the first element
flows in unchanged. Each duplicate pair meets its twin across the array and
cancels (`v ^ v == 0`), and the lone single value has no twin, so it is
what remains. We use the `^` operator directly rather than the `^=`
shorthand to mirror the pseudocode and make the running-XOR idea explicit,
though `running ^= v` is identical. No hash set is allocated, so the space
is genuinely O(1) -- just one `int`. The loop never indexes out of bounds
and handles the single-element input (a valid answer per the problem) by
returning that element unchanged after one XOR with `0`.

## Complexity

    Time:  O(n)   -- one pass; XOR is O(1) per element
    Space: O(1)   -- only a single accumulator integer, regardless of n

## Dry-Run

Input `nums = [4, 1, 2, 1, 2]`. The running XOR after each step, shown in
both decimal and 4-bit binary for readability (only the low bits matter;
sign is positive throughout):

| Step | v | running (before) | running ^ v (binary) | running (after) |
|-----:|--:|-----------------:|----------------------|----------------:|
| 1    | 4 | 0  = `0000`      | `0100`               | 4  = `0100`     |
| 2    | 1 | 4  = `0100`      | `0101`               | 5  = `0101`     |
| 3    | 2 | 5  = `0101`      | `0111`               | 7  = `0111`     |
| 4    | 1 | 7  = `0111`      | `0110`               | 6  = `0110`     |
| 5    | 2 | 6  = `0110`      | `0100`               | 4  = `0100`     |

Watch the two `1`s cancel: after step 2 the running value is `5`, and after
step 4 the second `1` flips bit 0 back off, leaving `6` (`4 ^ 2`). The two
`2`s then cancel: step 3 turns bit 1 on, step 5 turns it off again. What
survives is `4`, the lone unpaired value.

Output: `4`.

A faster way to read it: `4 ^ 1 ^ 2 ^ 1 ^ 2 == 4 ^ (1 ^ 1) ^ (2 ^ 2) == 4 ^ 0 ^ 0 == 4`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [7, 3, 7]`. What does `singleNumber` return?
- a) `7`
- b) `3`
- c) `0`

<details><summary>Show answer</summary>

**(b)** -- `0 ^ 7 = 7`; `7 ^ 3 = 4`; `4 ^ 7 = 3`. The two `7`s cancel, leaving the lone value `3`.

</details>

**Q2 (analyze).** The problem guarantees exactly ONE value appears once. What goes wrong if TWO values each appear once instead?
- a) Nothing -- the scan still returns both values
- b) The scan ends at their XOR (`a ^ b`), which is neither of the two single values
- c) The scan returns 0

<details><summary>Show answer</summary>

**(b)** -- with two unpaired values `a` and `b`, everything else cancels and the running XOR ends at `a ^ b`, not at `a` or `b`. The identity only isolates ONE unpaired value.

</details>

**Q3 (transfer).** Suppose every element appeared THREE times except one that appears once. Would the same XOR scan still work? Why or why not?

<details><summary>Show answer</summary>

No. Three copies XOR to `x ^ x ^ x == x` (not 0), so the triples do not cancel cleanly. That variant (LC 137) needs counting 1-bits at each position instead of a plain XOR scan.

</details>

## Common mistakes

- Allocating a `HashMap` to count frequencies. It works but violates the
  "constant extra space" requirement the problem explicitly demands.
- Sorting first then walking for unpaired neighbours. O(n log n) time and
  still not what the bit-manipulation constraint is fishing for.
- Initialising the accumulator to something other than `0`. Starting at `0`
  is essential -- `x ^ 0 == x` is what lets the first element enter cleanly.
- Assuming XOR finds *a* duplicate or *the count*. It only isolates the
  single unpaired value; if two values were unpaired, you would get their
  XOR, not either one.
- Worrying about sign. XOR operates bitwise on the full 32-bit two's
  complement representation, so negative numbers cancel just like positive
  ones (`-3 ^ -3 == 0`).

## Related problems

- [0268 - Missing Number](../0268-missing-number/) - same XOR identity, but
  applied to indices XOR values instead of just values.
- [0191 - Number of 1 Bits](../0191-number-of-1-bits/) - the other great
  bit trick in this chapter: `n & (n-1)` clears the lowest set bit.
