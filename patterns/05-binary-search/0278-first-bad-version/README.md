# 0278 - First Bad Version

**Difficulty:** Easy
**Pattern:** Binary Search
**LeetCode:** https://leetcode.com/problems/first-bad-version/

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

This is your first **boundary** search, and the gateway to Template B from the
pattern README. The trigger signals are "first version that is bad" and "first
[thing] in a range" -- classic wording for *find the boundary where a predicate
flips*.

The crucial observation is that the predicate `isBadVersion(k)` is
**monotonic**: once a version is bad, every later version is also bad (the bug
shipped and stayed). So the sequence of answers from `1` to `n` looks like

    false  false  false  ...  false  true  true  ...  true

and we are hunting the single index where `false` flips to `true`. That
monotonic shape is exactly what binary search needs.

Now reason about the midpoint of any candidate range `[lo, hi]`:

- If `isBadVersion(mid)` is **true**, the first bad version is `mid` *or
  earlier* -- so we can safely pull the upper bound down: `hi = mid`.
- If `isBadVersion(mid)` is **false**, the first bad version is *strictly later*
  than `mid` -- so we push the lower bound up past `mid`: `lo = mid + 1`.

Every iteration keeps the answer inside `[lo, hi]`, and the range strictly
shrinks, so the loop ends with `lo == hi` -- and that single point is the first
bad version. This is Template B verbatim.

Why not Template A (`lo <= hi`) here? Because we are not looking for an *exact
match*; we are looking for a *flip point* that no single comparison hands us.
The reducing form `lo < hi` with `hi = mid` is built precisely for that.

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
