# 0875 - Koko Eating Bananas

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/koko-eating-bananas/

## Concepts used

- **Binary search** -- narrowing a range by halves; here the range is a range of *answers*, not array indices. [glossary](../../../docs/10-glossary.md#binary-search)
- **Predicate** -- a yes/no question used as the decision in a search. [glossary](../../../docs/10-glossary.md#predicate)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

Koko loves bananas. There are `n` piles of bananas; the `i`-th pile has
`piles[i]` bananas. The guards will be away for `h` hours, and within those
`h` hours Koko must eat every banana.

Each hour Koko picks one pile and eats `k` bananas from it (if the pile has
fewer than `k` bananas, she eats all of them instead and does not start another
pile that hour). Koko wants to eat *as slowly as possible* but still finish in
time.

Return the **minimum integer eating speed** `k` such that she can finish all
bananas within `h` hours.

Signature:

    int minEatingSpeed(int[] piles, int h)

Example (verbatim from LeetCode):

    Input:  piles = [3,6,7,11], h = 8
    Output: 4

## Intuition

You've played "guess a number between 1 and 100": each time you guess, I reply
*higher* or *lower*, and you pick the middle of the remaining range -- about 7
guesses instead of 100. Notice you are *not* searching a list; you are searching
a *range of numbers* (1 to 100), and each reply tells you which half of the
range to keep. Koko is that game in disguise.

Here the hidden number is the **eating speed** `k` (bananas per hour), and we
want the *slowest* speed that still finishes all piles within `h` hours. The
smallest speed worth trying is `1`; the largest worth trying is `max(piles)`
(at that speed every pile is gone in one hour). So the "range of numbers" is
`1, 2, 3, ..., max(piles)` -- for `piles = [3, 6, 7, 11]`, `h = 8`, the range
is `1..11`. Each guess is a speed `mid`, and "too slow / fast enough" is decided
by a helper that asks: *can Koko finish all piles within `h` hours at this
speed?*

The crucial insight beginners miss is *why a range of speeds is searchable at
all*. It works because the helper's answer is **monotonic** -- it flips at most
once and never flips back. If speed `4` finishes in time, then speeds `5, 6, 7,
...` also finish in time (eating faster can never cost you *more* hours). So the
answers for speeds `1, 2, 3, 4, 5, ...` read `no, no, no, YES, YES, ...`, one
clean flip, and that flip point is the minimum speed we want. A yes/no question
that flips at most once like this is called a **predicate**, and a single-flip
predicate over a range is exactly what binary search is built to find.

Let's confirm `speed = 4` is that flip for `piles = [3, 6, 7, 11]`, `h = 8`.
Each pile takes `ceil(pile / 4)` hours (round *up*, because even one leftover
banana costs a whole hour): `ceil(3/4)=1`, `ceil(6/4)=2`, `ceil(7/4)=2`,
`ceil(11/4)=3`, total `1+2+2+3 = 8 <= 8` -- just fits. Speed `3` gives
`1+2+3+4 = 10 > 8` -- too slow. So `4` is the first speed that works, i.e. the
minimum.

We now binary-search the range `1..11` holding the **invariant** *the minimum
feasible speed is always in `[lo, hi]`*: guess the middle speed; if it can
finish, the answer is that speed or slower (`hi = mid`); if it can't, the
answer is strictly faster (`lo = mid + 1`). When `lo == hi`, that speed is the
answer.

### Checkpoint A -- Search a range of answers

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** In this problem the binary search is NOT over array indices. What is the search space?
- a) A range of candidate speeds `[1, max(piles)]`
- b) The pile indices `0..n-1`
- c) The number of hours `1..h`

<details><summary>Show answer</summary>

**(a)** -- the hidden number is a speed (bananas per hour), so the search space is the range of speeds from 1 up to the largest pile.

</details>

**Q2 (comprehend).** Why is the `canFinish(speed)` answer monotonic -- why does "true at speed s" guarantee "true at every speed `> s`"?
- a) Eating faster can never cost MORE hours, so a fast-enough speed stays fast enough
- b) Because the speeds are sorted in the array
- c) Because `h` is always large

<details><summary>Show answer</summary>

**(a)** -- raising the speed only shrinks each `ceil(pile / speed)`, so total hours never increase. That single-flip shape is what binary search needs.

</details>

## Pseudocode

    function minEatingSpeed(piles, h):
        low  <- 1
        high <- maximum value in piles          # the answer-space bounds

        while low < high:
            mid <- low + (high - low) / 2        # candidate speed
            if canFinish(piles, h, mid):
                high <- mid                      # mid works -> try slower
            else:
                low  <- mid + 1                  # mid too slow -> go faster

        return low                               # low == high == minimum speed

    function canFinish(piles, h, speed):
        hours <- 0
        for each pile in piles:
            hours <- hours + ceil(pile / speed)  # (pile + speed - 1) / speed
            if hours > h:                        # early exit
                return false
        return hours <= h

## Java Solution

```java
import java.util.*;

class Solution {
    public int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = 1;
        for (int p : piles) {
            hi = Math.max(hi, p);
        }
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canFinish(piles, h, mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    private boolean canFinish(int[] piles, int h, int speed) {
        long hours = 0;
        for (int p : piles) {
            hours += (p + speed - 1) / speed;     // ceil(p / speed)
            if (hours > h) {
                return false;
            }
        }
        return hours <= h;
    }
}
```

The outer loop is Template B verbatim, but its search space is *speeds* in
`[1, max(piles)]`, not array indices -- that conceptual shift is the whole
lesson. `hi` is seeded with the maximum pile, because eating at that speed each
pile finishes in one hour and the constraint `piles.length <= h` guarantees
feasibility. The `canFinish` helper computes `ceil(p / speed)` with the integer
trick `(p + speed - 1) / speed` rather than floating-point math, and bails early
the moment the running total exceeds `h`. The running total uses `long` because
at speed `1` the hours can be the sum of all piles, which is up to `10^4 *
10^9 = 10^13` and would overflow `int`. The midpoint uses the safe form to
avoid overflow even when `hi` is one billion. When the loop ends, `lo == hi`
and that value is the minimum feasible speed.

## Complexity

    Time:  O(n log M)  -- where n = piles.length and M = max(piles). Each of the
                          log M iterations runs the O(n) canFinish check.
    Space: O(1)        -- only a few scalar variables.

## Dry-Run

Step-by-step on `piles = [3, 6, 7, 11]`, `h = 8` (expected `4`).

First compute the answer-space bounds: `lo = 1`, `hi = max(piles) = 11`.

For reference, the hours needed at each candidate speed:

| speed k | ceil(3/k) | ceil(6/k) | ceil(7/k) | ceil(11/k) | total | <= 8? |
|--------:|----------:|----------:|----------:|-----------:|------:|:------|
| 1       | 3         | 6         | 7         | 11         | 27    | false |
| 2       | 2         | 3         | 4         | 6          | 15    | false |
| 3       | 1         | 2         | 3         | 4          | 10    | false |
| 4       | 1         | 2         | 2         | 3          | 8     | true  |
| 5       | 1         | 2         | 2         | 3          | 8     | true  |
| ...     | ...       | ...       | ...       | ...        | ...   | true  |
| 11      | 1         | 1         | 1         | 1          | 4     | true  |

The first true is at `k = 4`. Now the binary search:

| Iter | lo | hi | mid | canFinish(mid)? | action        |
|-----:|---:|---:|----:|-----------------|---------------|
| 1    | 1  | 11 | 6   | true (total 6)  | hi = mid=6    |
| 2    | 1  | 6  | 3   | false (total 10)| lo = mid+1=4  |
| 3    | 4  | 6  | 5   | true (total 8)  | hi = mid=5    |
| 4    | 4  | 5  | 4   | true (total 8)  | hi = mid=4    |
| exit | 4  | 4  | -   | -               | return **4**  |

Five probes of an 11-wide range found the boundary. A linear scan from speed 1
would have done four full `canFinish` calls (27, 15, 10, 8) plus the array
summations -- and on a `max(piles) = 10^9` range it would be a billion calls.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** `piles = [3, 6, 7, 11]`, `h = 8`. What is the total hours at speed `3`, and is speed `3` feasible?
- a) Total `10`, not feasible (`10 > 8`)
- b) Total `8`, feasible
- c) Total `6`, feasible

<details><summary>Show answer</summary>

**(a)** -- `ceil(3/3)=1`, `ceil(6/3)=2`, `ceil(7/3)=3`, `ceil(11/3)=4`, sum `10 > 8`. So speed `3` is too slow, which is why the answer is `4`.

</details>

**Q2 (analyze).** Why does `canFinish` accumulate hours in a `long` rather than an `int`?
- a) At speed 1 the sum of piles can reach ~`10^13`, which overflows `int`
- b) `long` arithmetic is faster in Java
- c) To match LeetCode's return type

<details><summary>Show answer</summary>

**(a)** -- `int` tops out near `2.1 * 10^9`, but the worst-case hour sum is `10^4` piles each of size `10^9`, i.e. `10^13`, so `long` is mandatory.

</details>

**Q3 (transfer).** What goes wrong if you set `hi = piles.length` instead of `hi = max(piles)`? One sentence.

<details><summary>Show answer</summary>

The answer space would be capped at the number of piles, but the minimum feasible speed can be far larger than `piles.length`; you would search a range that may not contain the answer and return too small a value.

</details>

## Common mistakes

- Setting `hi = piles.length` or `hi = piles.length - 1`. The search space is
  *speeds*, not indices; a speed can be much larger than the array length. The
  upper bound is the maximum pile.
- Using `int` for the running hours. At speed 1 the sum of piles can overflow
  `int` (up to ~`10^13`). Always accumulate in `long`.
- Computing `ceil(p / k)` with floating point (`Math.ceil((double) p / k)`).
  It works but is slower and risks rounding surprises; the integer identity
  `(p + k - 1) / k` is exact and faster.
- Writing `while (lo <= hi)` with `hi = mid - 1`. That is the exact-match form;
  for a boundary / answer-space search it produces off-by-one answers or misses
  the minimum. Use Template B (`while (lo < hi)`, `hi = mid`).
- Forgetting to bail early in `canFinish`. Without the `if (hours > h) return
  false` short-circuit, every check walks the whole array even when the answer
  is obviously "too slow". It does not change the asymptotic bound but roughly
  halves constant factors on slow candidates.
- Setting `lo = 0` instead of `lo = 1`. Speed 0 causes division by zero in the
  first probe. The minimum meaningful speed is 1.

## Related problems

- [0278 - First Bad Version](../0278-first-bad-version/) -- the same Template B
  with a trivial predicate; Koko is the "real predicate" version.
- [0035 - Search Insert Position](../0035-search-insert-position/) -- the
  Template-A boundary, for contrast.
- [0153 - Find Minimum in Rotated Sorted Array](../0153-find-minimum-in-rotated-sorted-array/) --
  another `lo < hi` boundary search, but the predicate is an array comparison.

Siblings outside this section (same answer-space idea): Split Array Largest
Sum (LC 410), Capacity To Ship Packages Within D Days (LC 1011), Minimum Size
Subarray Sum's binary-search variant, and the "minimum time to complete trips"
family.
