# Pattern 16 - Bit Manipulation

## What the pattern is

A **bit-manipulation** algorithm treats the bits of an integer as a compact
data structure and uses bitwise operators (`&`, `|`, `^`, `~`, `<<`, `>>`)
to answer the question in one or two passes, with **no extra memory**.

Where Pattern 1 (Arrays & Hashing) buys O(n) time by spending O(n) space on
a hash set, this pattern refuses to spend the space. It exploits the fact
that an `int` is already a 32-cell array of bits, and a single machine
instruction can inspect or transform all 32 cells at once. The result is
often a 5-line solution that looks like a magic trick.

The pattern earns the closing slot of this book because it is the one place
where knowing a **single algebraic identity** (`x ^ x = 0`) turns an
O(n)-space hash problem into an O(1)-space bit problem. Once you internalise
two identities -- the XOR identity and `n & (n-1)` -- a whole family of
"without extra space" problems collapse.

## When it applies (trigger signals)

Reach for Bit Manipulation when the problem statement shows any of these:

| Signal | Example phrasing |
|---|---|
| **"Without extra space"** | "solve in O(1) extra memory" / "constant space" |
| **XOR mentioned or implied** | "every element appears twice except one" |
| **Single number / odd one out** | "find the unique value", "the element that appears once" |
| **Power of two** | "is n a power of two?", "has exactly one set bit" |
| **Reverse / count bits** | "number of 1 bits", "reverse bits", "binary gap" |
| **Parity** | "does it have odd or even number of set bits" |
| **Two values differ in exactly one bit** | "all numbers twice except one", "find the difference of two strings" |

The tell-tale sign: brute force is O(n^2) or O(n) space, a hash solution is
O(n) time and O(n) space, *and* the problem either demands O(1) space or
the values have a clean "pair-cancelling" structure. That structure is the
green light for bits.

## The essential bit tricks table

These are the nine moves in the bit-manipulation playbook. Memorise them;
every problem in this chapter is a combination of these.

| Expression | Name | What it does |
|---|---|---|
| `a ^ b` | XOR | result bit is 1 where `a` and `b` differ |
| `a & b` | AND | result bit is 1 only where **both** are 1 |
| `a \| b` | OR | result bit is 1 where **either** is 1 |
| `~a` | NOT | flips every bit (inverts all 32) |
| `a << k` | left shift | appends `k` zero bits on the right; equals `a * 2^k` |
| `a >> k` | arithmetic right shift | shifts right, **copies the sign bit** into the vacated positions |
| `a >>> k` | logical right shift | shifts right, **fills with zeros** regardless of sign |
| `a & (a - 1)` | clear lowest set bit | turns the rightmost 1-bit into a 0 (Brian Kernighan's trick) |
| `a & (-a)` | isolate lowest set bit | keeps **only** the rightmost 1-bit (two's complement: `-a == ~a + 1`) |

Two tricks from this table carry the whole chapter: `a ^ b` (XOR) and
`a & (a - 1)` (clear lowest set bit). The shifts matter for bit-counting
and reversing; the isolate trick appears in problems like Single Number III
and power-of-two checks.

## The XOR identity (the foundation)

XOR (`^`) is the "cancellation" operator. Two facts make it magical:

1. **`x ^ x == 0`** -- any value XOR itself is zero. Identical values cancel.
2. **`x ^ 0 == x`** -- XOR with zero leaves a value unchanged.

Add two more properties:

- **Commutative:** `a ^ b == b ^ a` (order does not matter).
- **Associative:** `(a ^ b) ^ c == a ^ (b ^ c)` (grouping does not matter).

Consequence: if you XOR together every value in a list where each value
appears an **even** number of times except one value that appears an **odd**
number of times, all the even-counted values cancel in pairs and the
running XOR collapses to that single odd-counted value. This is exactly
LeetCode 136 (Single Number) and, with a twist, LeetCode 268 (Missing
Number).

> In words: *"a value XOR itself is nothing; a value XOR nothing is itself.
> So XOR-ing a list where every duplicate pair cancels leaves behind the
> unpaired value."*

## A general pseudocode template

Almost every "XOR scan" problem in this chapter has this shape:

```text
function xorScan(values):
    running <- 0                       # XOR identity: x XOR 0 == x
    for each value v in values:
        running <- running XOR v       # pairs cancel, odd one survives
    return running
```

The whole trick is choosing *what* goes into the `values` list. For Single
Number it is the array itself. For Missing Number it is the array values
XOR-ed against the index range `0..n`, so that every present value cancels
with its index and the missing index is what survives.

For the second flavour of problem (counting set bits), the template is
different -- use the Brian Kernighan trick:

```text
function popcount(n):
    count <- 0
    while n is not zero:
        n <- n AND (n - 1)             # clear the lowest set bit
        count <- count + 1
    return count
```

This loop runs **exactly once per set bit**, never per bit-position. A number
with 3 set bits takes 3 iterations, not 32.

## When NOT to use bits

Bit tricks are fast and memory-cheap, but they trade readability for
cleverness. Use them only when one of these holds:

- The problem **explicitly** demands O(1) space.
- The "pair-cancelling" structure is obvious and the XOR solution is shorter
  than the hash solution.
- You are asked about bits directly (counting, reversing, power-of-two).

Otherwise, **prefer the readable hash solution.** In an interview, a
`HashMap` counting solution that the interviewer understands in 10 seconds
beats a 3-line XOR that takes them 5 minutes to verify. State the hash
solution first, *then* offer the bit trick as an optimisation and explain
the identity that makes it correct. Most interviewers reward the clear
communicator over the clever hacker.

Avoid bits when the problem needs to count *how many times* a value appears
(more than once vs. zero/once), when it needs the actual duplicate value
rather than its XOR signature, or when values can appear an arbitrary
odd/even mix that does not cancel cleanly (e.g. "every element appears
*three* times except one" -- LC 137 -- needs bit-position counting, not a
plain XOR scan).

## Problems in this section

| # | LC | Problem | Difficulty | One-line teaser |
|---|----|---------|-----------|-----------------|
| 96 | 136 | [Single Number](./0136-single-number/) | Easy | XOR all numbers; the duplicate pairs cancel to 0 and the lone value survives. |
| 97 | 191 | [Number of 1 Bits](./0191-number-of-1-bits/) | Easy | Repeatedly clear the lowest set bit with `n & (n-1)`; count how many times until 0. |
| 98 | 268 | [Missing Number](./0268-missing-number/) | Easy | XOR all indices with all values; everything pairs up except the missing one. |

Work them in that order. Single Number installs the XOR identity. Number of
1 Bits introduces the `n & (n-1)` trick and the signed-vs-unsigned subtlety.
Missing Number shows the same XOR identity applied to a different list
(indices XOR values), with a cleaner arithmetic alternative (the sum
formula) discussed alongside it.

## Common pitfalls

- **Confusing `>>` and `>>>`.** Java's `>>` is *arithmetic*: it copies the
  sign bit into the vacated positions, so a negative number stays negative
  and a shift-based bit-counter loops forever. `>>>` is *logical*: it
  always fills with zeros, treating the `int` as an unsigned 32-bit value.
  For any bit-counting that walks the bits by shifting, you almost always
  want `>>>`.
- **Integer overflow on `<<`.** `1 << 31` does **not** give a large positive
  number -- it sets the sign bit and yields `Integer.MIN_VALUE`. When you
  need a set bit beyond position 30, write `1L << 31` (a `long` literal) so
  the shift happens in 64-bit space.
- **Assuming the width.** Java `int` is **always** 32-bit two's complement,
  signed. But `long` is 64-bit and `char` is 16-bit unsigned; mixing them
  changes how much a shift moves and what "the sign bit" even means. Decide
  the width up front and stay in it.
- **Thinking XOR cancels odd counts.** XOR isolates the value that appears
  an *odd* number of times **only when every other value appears an even
  number of times**. "Twice except one" works; "three times except one"
  (LC 137) does **not** collapse to a plain XOR scan -- the counts no
  longer pair off. That problem needs counting set bits per position.
- **Forgetting XOR is order-independent.** Because XOR is commutative and
  associative, the order of the array does not matter. This is *why* the
  scan works on a shuffled input -- but it also means you cannot recover
  *which* element was unpaired, only its value.
- **Ignoring the empty / zero cases.** An XOR scan initialised to `0`
  correctly handles an empty input (returns `0`) and an input containing
  literal zeros (they XOR away harmlessly). Do not add a special case that
  breaks this.
