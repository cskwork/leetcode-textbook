# 0278 - First Bad Version

**Difficulty:** Easy
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/first-bad-version/

## Concepts used

- **Binary search** -- narrowing a range by halves; here we hunt the point where a yes/no answer flips. [glossary](../../../docs/10-glossary.md#binary-search)
- **Predicate** -- a yes/no question used as the decision in a search. [glossary](../../../docs/10-glossary.md#predicate)
- **Invariant** -- a condition that is always true at the start of every loop iteration. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

You are a product manager leading a team to develop a new product. Suppose all
versions of your product are numbered from `1` to `n`. You are given an API
`boolean isBadVersion(int version)` that returns whether a version is bad. Find
the **first bad version**, the smallest version number that is bad. You want to
minimize the number of API calls.

Signature:

    int firstBadVersion(int n)

On LeetCode the `isBadVersion` API is provided for you; locally we model it as a
method on `Solution` that subclasses can override (see `SolutionTest.java`).

Example (verbatim from LeetCode), with `n = 5` and `bad = 4`:

    call isBadVersion(3) -> false
    call isBadVersion(5) -> true
    call isBadVersion(4) -> true
    Output: 4

## Intuition

Picture a long line of products coming off a conveyor belt, labelled 1, 2, 3,
.... Everything is fine until some product, and from that point on *every*
product is defective -- a bad part got into the machine and stayed. Given a
tester that tells you whether product `k` is defective, find the first defective
one. You could test 1, then 2, then 3..., but that is slow. The line goes *good
good good ... good | bad bad bad ... bad* -- one clean flip from good to bad --
and that single flip is exactly what binary search is built to find.

Trace `n = 5` with the first bad version at `4`; the tester answers `1:good,
2:good, 3:good, 4:bad, 5:bad`. We keep markers `lo, hi` on the range that still
contains the first bad version (`lo = 1, hi = 5`) and halve it:

1. Middle is `3`, tester says **good**. So `1, 2, 3` are all good -- the first
   bad version is strictly later. Jump `lo = 4`.
2. Middle is `4`, tester says **bad**. So `4` is bad, and the first bad version
   is `4` *or earlier*. We can't throw `4` away (it might *be* the first), so
   pull the top down: `hi = 4`.
3. `lo = 4, hi = 4` -- they meet. The first bad version is `4`.

The shape that makes this searchable is a **predicate** -- a yes/no question
(`is version k bad?`) whose answer flips at most once as `k` grows, and once it
flips to "yes" it stays "yes". A single-flip predicate over a range is precisely
what binary search needs.

The **invariant** is: *the first bad version is always inside `[lo, hi]`.* The
"good" branch moves `lo` past everything proven good; the "bad" branch pulls
`hi` down to `mid` while keeping `mid` (since `mid` could be the answer). The
range shrinks every step and the answer never escapes it, so when `lo == hi`
that single value is the first bad version.

### Checkpoint A -- Hunt the boundary, not a match

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** This solution uses `hi = mid` (not `mid - 1`) on the "bad" branch. Why keep `mid` in the range?
- a) Because `mid` might itself be the first bad version
- b) To make the loop run faster
- c) Because `mid - 1` causes overflow

<details><summary>Show answer</summary>

**(a)** -- a bad `mid` could be the very first bad version, so excluding it with `mid - 1` might discard the answer.

</details>

**Q2 (comprehend).** On `n = 5`, first bad `= 4`, the first probe is `mid = 3` and the tester says "good". Why does the algorithm set `lo = mid + 1 = 4` rather than `lo = mid`?
- a) `mid` is proven good, so the first bad version is strictly after it; keeping `mid` would re-check a known-good version forever
- b) To avoid integer overflow
- c) Because `mid + 1` is the ceiling midpoint

<details><summary>Show answer</summary>

**(a)** -- a good `mid` is ruled out, so the search must move strictly past it. Only the "bad" branch keeps `mid`.

</details>

## Pseudocode

    function firstBadVersion(n):
        low  <- 1
        high <- n

        while low < high:
            mid <- low + (high - low) / 2       # floor midpoint (see pitfall)
            if isBadVersion(mid) is true:
                high <- mid                     # mid might be the answer; keep it
            else:
                low  <- mid + 1                 # mid is good, first bad is after it

        return low                              # low == high == first bad version

## Java Solution

```java
import java.util.*;

class Solution {
    public int firstBadVersion(int n) {
        int lo = 1, hi = n;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (isBadVersion(mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    // Overridden by tests to model a specific "bad" threshold.
    protected boolean isBadVersion(int version) {
        return false;
    }
}
```

We use the Template B reducing loop (`while (lo < hi)`) because we want a
*boundary*, not an exact index. The `mid = lo + (hi - lo) / 2` floor is
essential here, not just for overflow: with `hi = mid` we need `mid < hi`
whenever `lo < hi`, otherwise `hi` would never move and the loop would spin
forever on a two-element range -- the floor guarantees that. The
`isBadVersion` stub returns `false` by default; in tests a subclass overrides it
to model the real threshold, mirroring how LeetCode injects the API. When the
loop ends, `lo == hi`, and that value is the first version at which the
predicate is true, so we return it with no further adjustment.

## Complexity

    Time:  O(log n)  -- each call halves the candidate range; one API call per iteration.
    Space: O(1)      -- only two integer variables.

## Dry-Run

Step-by-step on `n = 5`, first bad version `= 4` (expected `4`):

| Iter | lo | hi | mid | isBadVersion(mid) | action        |
|-----:|---:|---:|----:|-------------------|---------------|
| 1    | 1  | 5  | 3   | false (3 is good) | lo = mid+1=4  |
| 2    | 4  | 5  | 4   | true  (4 is bad)  | hi = mid=4    |
| exit | 4  | 4  | -   | -                 | return **4**  |

Two API calls, not four. Notice how the range always contains the answer: after
step 1 we learn "1..3 are good", so `lo` jumps to 4; after step 2 we learn "4 is
bad", so `hi` comes down to 4.

Dry-run on a trickier case, `n = 6`, first bad version `= 1` (every version is
bad -- expected `1`):

| Iter | lo | hi | mid | isBadVersion(mid) | action        |
|-----:|---:|---:|----:|-------------------|---------------|
| 1    | 1  | 6  | 3   | true              | hi = mid=3    |
| 2    | 1  | 3  | 2   | true              | hi = mid=2    |
| 3    | 1  | 2  | 1   | true              | hi = mid=1    |
| exit | 1  | 1  | -   | -                 | return **1**  |

The `hi = mid` branch keeps pulling the upper bound left until it pins the
answer at 1.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `n = 6`, first bad version `= 1` (every version is bad). What is returned?
- a) `1` -- the `hi = mid` branch pulls the upper bound down until it pins 1
- b) `3` -- the first midpoint
- c) It loops forever

<details><summary>Show answer</summary>

**(a)** -- every probe is bad, so `hi` walks left (`3, 2, 1`) while `lo` stays `1`; the loop ends at `lo == hi == 1`.

</details>

**Q2 (analyze).** Why MUST the midpoint be the floor `lo + (hi - lo) / 2` and not the ceiling? Think about a two-element range where `mid` is bad.
- a) With the ceiling, `mid == hi`, so `hi = mid` makes no progress and the loop never ends
- b) The ceiling gives the wrong numeric answer
- c) The ceiling overflows

<details><summary>Show answer</summary>

**(a)** -- the floor guarantees `mid < hi` whenever `lo < hi`, so `hi = mid` strictly shrinks the range. The ceiling breaks that guarantee on a two-element range.

</details>

**Q3 (transfer).** In one sentence, what single property of `isBadVersion` makes this problem binary-searchable rather than requiring a linear scan?

<details><summary>Show answer</summary>

It is a monotonic predicate -- once it returns true it stays true for every larger version, so there is exactly one good-to-bad flip to hunt.

</details>

## Common mistakes

- Writing `while (lo <= hi)`. With `hi = mid` and `lo = mid + 1` that form
  repeats the final element forever once `lo == hi`. The boundary form *must*
  use `while (lo < hi)`.
- Using `hi = mid - 1` instead of `hi = mid`. If `mid` itself is bad, `mid`
  might *be* the first bad version, so excluding it (`- 1`) can discard the
  answer. The whole point of the boundary form is to keep `mid` in the range.
- Using the ceiling midpoint `mid = lo + (hi - lo + 1) / 2`. With `hi = mid`,
  a ceiling midpoint makes `mid == hi` on a two-element range, so `hi = mid`
  does nothing and the loop never ends. Pair `hi = mid` with the floor.
- Returning `lo - 1` or `hi`. When the loop exits `lo == hi` and that value is
  the first true -- no adjustment is needed.
- Calling `isBadVersion` more than once per iteration. Cache it in a local if
  you must reference it twice; LeetCode penalises extra calls.

## Related problems

- [0035 - Search Insert Position](../0035-search-insert-position/) -- the
  Template-A cousin: "where would the flip happen" phrased as an insert.
- [0875 - Koko Eating Bananas](../0875-koko-eating-bananas/) -- the same
  Template B, but the predicate is an O(n) check and the search space is a
  range of *speeds*, not version numbers.
- [0153 - Find Minimum in Rotated Sorted Array](../0153-find-minimum-in-rotated-sorted-array/) --
  the `lo < hi` / `hi = mid` shape again, where the "predicate" is a comparison
  between two array elements.
