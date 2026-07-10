# 0191 - Number of 1 Bits

**Difficulty:** Easy
**Pattern:** Bit Manipulation
**LeetCode:** https://leetcode.com/problems/number-of-1-bits/

## Problem

Write a function that takes the **binary representation** of an unsigned
integer and returns the number of `1` bits it has (also known as the
**Hamming weight**).

In Java, the input is typed as a signed `int`, but you must treat its 32
bits as **unsigned**. For example, the input `11111111111111111111111111111101`
has 31 set bits.

Signature:

    int hammingWeight(int n)

Examples:

    Input:  n = 00000000000000000000000000001011   (decimal 11)
    Output: 3

    Input:  n = 00000000000000000000000010000000   (decimal 128)
    Output: 1

    Input:  n = 11111111111111111111111111111101   (decimal -3 as a signed int)
    Output: 31

## Intuition

The trigger is direct: this problem asks about **bits** ("number of 1
bits"), so it lives in Pattern 16. The naive approach -- loop over all 32
bit positions, mask each with `1`, count the ones -- works but always does
32 iterations even when the number has only a single set bit.

There is a famous trick that makes the loop run **once per set bit** instead
of once per bit position: Brian Kernighan's algorithm. The identity behind
it is

    n & (n - 1)

which clears the **lowest set bit** of `n`. Why? Subtracting 1 flips every
bit from the lowest set bit downward (the lowest set bit becomes 0, the
trailing zeros become 1s), and AND-ing with the original zeros those newly
flipped bits, leaving everything above the lowest set bit untouched. So
`n & (n - 1)` is `n` with exactly its rightmost 1-bit turned off.

Each iteration of `n = n & (n - 1)` removes one set bit; after `k`
iterations `n` becomes 0, where `k` is exactly the number of set bits. A
number with 3 set bits takes 3 iterations, not 32.

### Checkpoint A -- Clearing the lowest set bit

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** What does the expression `n & (n - 1)` do to `n`?
- a) Flips every bit of `n`
- b) Clears (turns off) the lowest set bit of `n`
- c) Doubles `n`

<details><summary>Show answer</summary>

**(b)** -- subtracting 1 flips the bits from the lowest set bit downward, and AND-ing with the original leaves all higher bits intact but zeroes that rightmost 1-bit.

</details>

**Q2 (comprehend).** For `n = 12` (binary `1100`), what is `n & (n - 1)`?
- a) `8`  (binary `1000`)
- b) `4`  (binary `0100`)
- c) `0`

<details><summary>Show answer</summary>

**(a)** -- `n - 1 = 11` (binary `1011`); `1100 & 1011 = 1000`, which is `8`. The lowest set bit (bit 2) was cleared.

</details>

## Pseudocode

    function hammingWeight(n):
        count <- 0
        while n is not zero:
            n <- n AND (n - 1)        # clear the lowest set bit
            count <- count + 1
        return count

In words: as long as the number is non-zero, wipe out its lowest set bit and
tick the counter. When the number reaches zero, the counter holds the number
of bits you wiped -- which is the Hamming weight.

## Java Solution

```java
class Solution {
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);   // clear the lowest set bit (Brian Kernighan)
            count++;
        }
        return count;
    }
}
```

The loop condition `n != 0` is correct even for negative inputs: Java `int`
is 32-bit two's complement, and `&` and `-` operate on the raw bit pattern,
so the sign bit is just another bit. Subtracting 1 and AND-ing clears the
lowest set bit of the *pattern*, never the "sign" as such, which is why no
`>>>` is needed here -- the loop still terminates because each iteration
removes exactly one 1-bit and a finite number of them eventually reaches 0.
This is the key advantage over the shift-and-mask alternative
(`count += n & 1; n >>>= 1;`), which would need `>>>` (logical, zero-fill)
rather than `>>` (arithmetic, sign-extending) to avoid looping forever on a
negative input. `n &= (n - 1)` sidesteps that trap entirely and is faster
on sparse inputs: 32 iterations shrink to one per set bit.

## Complexity

    Time:  O(k) where k is the number of set bits, at most 32 -- one clear per set bit
    Space: O(1) -- only a counter integer

(Worst case k = 32 for input `-1`, so this is also O(32) = O(1) in the
number of bits of an `int`.)

## Dry-Run

Input `n = 11` = `00000000 00000000 00000000 00001011` (3 set bits).
Showing the low byte only; the high bytes stay all zero.

| Step | n (decimal) | n (low byte) | n - 1 (low byte) | n & (n-1) | count |
|-----:|------------:|-------------:|-----------------:|----------:|------:|
| 1    | 11          | `00001011`   | `00001010`       | 10        | 1     |
| 2    | 10          | `00001010`   | `00001001`       | 8         | 2     |
| 3    | 8           | `00001000`   | `00000111`       | 0         | 3     |

After step 3, `n` is 0 and the loop exits. Each step wiped exactly one set
bit: bit 0, then bit 1, then bit 3.

Output: `3`.

Sanity check on the unsigned edge case `n = -3` =
`11111111 11111111 11111111 11111101` (31 set bits): the loop runs 31
times, clearing one set bit each time, and correctly returns 31 -- even
though `n` is negative throughout most of the run.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `hammingWeight(14)` (binary `1110`). What does it return, and how many loop iterations run?
- a) `3`, in 3 iterations
- b) `4`, in 4 iterations
- c) `3`, in 32 iterations

<details><summary>Show answer</summary>

**(a)** -- `14 & 13 = 12` (count 1); `12 & 11 = 8` (count 2); `8 & 7 = 0` (count 3). Three set bits, three iterations -- the loop runs once per set bit, not per bit position.

</details>

**Q2 (analyze).** Why does `n & (n - 1)` work correctly on a NEGATIVE `n`, while a shift loop using `>>` would loop forever?
- a) `n & (n - 1)` always removes exactly one set bit and reaches 0 regardless of sign; `>>` copies the sign bit in, so a negative never becomes 0
- b) Negative numbers have no set bits, so the loop is skipped
- c) `n & (n - 1)` flips the sign bit to 0 on the first step

<details><summary>Show answer</summary>

**(a)** -- each iteration clears exactly one 1-bit from the 32-bit pattern, so it must terminate; `>>` is arithmetic (sign-extending), so a negative keeps its leading 1s and never reaches 0.

</details>

**Q3 (transfer).** Using one identity from this chapter, how would you test whether a positive integer `n` is a power of two (like 1, 2, 4, 8, ...)?

<details><summary>Show answer</summary>

A power of two has exactly one set bit, so `n & (n - 1)` clears that bit and yields 0. The check is `n > 0 && (n & (n - 1)) == 0`.

</details>

## Common mistakes

- Using `>>` (arithmetic shift) in a shift-and-mask loop. A negative number
  sign-extends, so `n >>= 1` never reaches 0 and the loop runs forever. Use
  `>>>` if you walk the bits by shifting -- or, better, use
  `n & (n - 1)` and avoid shifting entirely.
- Looping exactly 32 times with `n & 1` then `n >>>= 1`. Correct, but it
  always costs 32 iterations even for `n = 1`; Brian Kernighan does 1.
- Treating the input as signed and bailing early when `n < 0`. The bits are
  what matter; "negative" just means the top bit is set. A negative `n` is
  a valid 32-bit value with a perfectly countable number of 1s.
- Writing `n = n - 1` without the `&`. That changes the value arbitrarily
  and never converges to 0 in a useful way; the `&` is what keeps all the
  higher bits intact.
- Returning the counter before the loop sets it. Initialise `count = 0` so
  the `n = 0` input correctly returns 0 without entering the loop.

## Related problems

- [0136 - Single Number](../0136-single-number/) - the other pillar of
  this chapter: the XOR identity that cancels pairs.
- [0268 - Missing Number](../0268-missing-number/) - XOR applied to a
  different list (indices XOR values).
