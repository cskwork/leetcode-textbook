# 0875 - Koko Eating Bananas

**Difficulty:** Medium
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/koko-eating-bananas/

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

This is the canonical **binary-search-on-the-answer-space** problem, and the
payoff of Template B from the pattern README. The trigger phrase is the giveaway:
*"minimum speed such that ... works"*. Notice the search target is not an index
into `piles` -- it is a *speed*, a number living in a range. That is the
meta-pattern beginners miss.

Two questions turn this into a binary search:

1. **What is the answer space?** The smallest meaningful speed is `1` (one
   banana per hour). The largest speed worth considering is `max(piles)`,
   because at that speed every pile takes exactly one hour, and Koko cannot do
   better than `n` hours anyway (one pile per hour). So the candidate speed `k`
   lives in the closed range `[1, max(piles)]`.

2. **Can I check a candidate in one pass, and is the check monotonic?** Yes and
   yes. Given a candidate speed `k`, pile `p` takes `ceil(p / k)` hours, so the
   total hours is `sum of ceil(p / k)` over all piles -- an O(n) check. And the
   check is monotonic: a *faster* speed never takes *more* hours, so once some
   `k` is fast enough, every larger `k` is also fast enough. The answer sequence
   over `k = 1, 2, 3, ...` looks like `false false ... false true true ... true`,
   and we want the first `true`.

That is exactly the shape Template B handles: a monotonic predicate over a
range, looking for the first true. So we binary-search `k`, and each probe calls
the O(n) `canFinish` helper. Total cost: O(n log max).

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
