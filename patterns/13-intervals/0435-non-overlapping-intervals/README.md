# 0435 - Non-overlapping Intervals

**Difficulty:** Medium
**Pattern:** Intervals (greedy)
**LeetCode:** https://leetcode.com/problems/non-overlapping-intervals/

## Problem

Given an array of intervals `intervals[i] = [start_i, end_i]`, return the
**minimum number of intervals you must remove** so that the remaining
intervals are non-overlapping. Note: intervals that only touch at a point
(e.g. `[1,2]` and `[2,3]`) count as non-overlapping.

Signature:

    int eraseOverlapIntervals(int[][] intervals)

Examples (verbatim from LeetCode):

    Input:  intervals = [[1,2],[2,3],[3,4],[1,3]]
    Output: 1
    Explanation: [1,3] can be removed, leaving three non-overlapping intervals.

    Input:  intervals = [[1,2],[1,2],[1,2]]
    Output: 2
    Explanation: two of the three identical intervals must be removed.

    Input:  intervals = [[1,2],[2,3]]
    Output: 0

## Intuition

The trigger signals point at Intervals, and the question "minimum number
to remove" is a classic **greedy maximization** in disguise: removing as
few as possible is the same as *keeping as many as possible* that do not
overlap. So the real problem is "find the maximum set of mutually
non-overlapping intervals", and the answer is `n - (max kept)`.

The greedy rule: **always keep the interval that ends earliest** (among
those that do not conflict with what we have already kept). The reason is
an exchange argument. Suppose an optimal solution picks some interval `s`
as its first kept interval, but a different interval `e` ends even
earlier. Because `e.end <= s.end`, anything that comes after `e` and does
not conflict with `s` also does not conflict with `e` (it starts at or
after `s.end >= e.end`). So we can swap `s` for `e` without breaking the
solution or shrinking it. Repeating this argument, there is always an
optimal solution that includes the earliest-ending interval — and then,
recursively, the earliest-ending interval that does not conflict with it,
and so on.

That exchange argument tells us the **sort key**: sort by END, not by
start. Sorting by start would greedily keep a long interval that starts
early but ends late (e.g. `[1,10]`), wrongly blocking several shorter
intervals that fit inside it. Sorting by end guarantees we always commit
to the choice the argument proved safe.

After sorting by end, the algorithm is one walk: keep each interval whose
start is at or past the previous kept interval's end; otherwise remove it.

## Pseudocode

```text
function eraseOverlapIntervals(intervals):
    if intervals is empty:
        return 0
    sort intervals by end (ascending)

    removed = 0
    prevEnd = negative infinity      # end of the last KEPT interval

    for each interval cur in sorted intervals:
        if cur.start >= prevEnd:
            # compatible -> keep it, extend the frontier
            prevEnd = cur.end
        else:
            # overlaps a kept interval -> remove this one
            removed = removed + 1
    return removed
```

The `>=` (not `>`) encodes the touching rule: `[1,2]` and `[2,3]` are
compatible. Note we never update `prevEnd` on a removal — that is the
whole point of sorting by end. The kept interval ends as early as
possible, so a conflicting later interval is the correct one to drop.

## Java Solution

```java
import java.util.*;

class Solution {
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;
        // Sort by END: the exchange argument requires the earliest-ending
        // interval be considered first, so it is the safe one to keep.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int removed = 0;
        int prevEnd = Integer.MIN_VALUE;
        for (int[] cur : intervals) {
            if (cur[0] >= prevEnd) {
                prevEnd = cur[1];          // keep: extend the frontier
            } else {
                removed++;                 // drop: conflicts with a kept interval
            }
        }
        return removed;
    }
}
```

`Arrays.sort` here compares `a[1]` with `b[1]` — the **end**, the line
that distinguishes this problem from Merge Intervals. `prevEnd` is the
only state; it tracks the end of the most recently kept interval, which
(after the sort) is always the earliest end compatible with everything
kept so far. We update `prevEnd` only on a "keep" decision, because a
removed interval could only push the frontier later and worsen the
result. Initializing `prevEnd` to `Integer.MIN_VALUE` makes the first
interval always kept, matching the exchange-argument base case.

## Complexity

    Time:  O(n log n)  -- the sort dominates; the single pass is O(n).
    Space: O(log n)    -- in-place sort's recursion stack. No extra output list.

## Dry-Run

Step-by-step on `intervals = [[1,2],[2,3],[3,4],[1,3]]`:

First sort by end: `[[1,2],[2,3],[1,3],[3,4]]` (note `[1,3]` slides to
third because its end `3` ties with `[2,3]` but it sorts after; ties do
not affect the answer).

| Step | cur      | cur.start >= prevEnd? | action   | prevEnd | removed |
|------|----------|-----------------------|----------|---------|---------|
| 1    | `[1,2]`  | 1 >= -inf yes         | keep     | 2       | 0       |
| 2    | `[2,3]`  | 2 >= 2 yes            | keep     | 3       | 0       |
| 3    | `[1,3]`  | 1 >= 3 no             | remove   | 3       | 1       |
| 4    | `[3,4]`  | 3 >= 3 yes            | keep     | 4       | 1       |

Return `removed = 1`. The kept set is `[[1,2],[2,3],[3,4]]`, three
non-overlapping intervals — maximum possible, so one removal is optimal.

### Why start-sort would fail

Consider `[[1,10],[2,3],[4,5]]`, whose correct answer is `1` (keep the two
short intervals, drop the long one). Sort by end gives `[[2,3],[4,5],[1,10]]`:

| Step | cur       | cur.start >= prevEnd? | action | prevEnd | removed |
|------|-----------|-----------------------|--------|---------|---------|
| 1    | `[2,3]`   | 2 >= -inf yes         | keep   | 3       | 0       |
| 2    | `[4,5]`   | 4 >= 3 yes            | keep   | 5       | 0       |
| 3    | `[1,10]`  | 1 >= 5 no             | remove | 5       | 1       |

Return `1` — correct.

Had we sorted by start instead (`[[1,10],[2,3],[4,5]]`), the walk would
keep `[1,10]` (prevEnd = 10), then be forced to drop both `[2,3]` and
`[4,5]`, returning `2` — the wrong answer. This is exactly why the
sort key is END for the min-removal flavour.

## Common mistakes

- **Sorting by start instead of end.** The single most common error. It
  passes LeetCode's tiny samples and fails on any case where a long
  early-starting interval blocks several short ones. The exchange argument
  is what licenses the end-sort; if you cannot state it, do not deviate.
- **Updating `prevEnd` on a removal.** After dropping an interval, the
  frontier must stay where the last *kept* interval ended. Moving
  `prevEnd` forward on a drop defeats the "keep earliest end" rule and
  over-counts removals.
- **Using `>` instead of `>=`.** Touching intervals are compatible in this
  problem, so the test is `cur.start >= prevEnd`. A strict `>` would flag
  `[1,2]` followed by `[2,3]` as conflicting and remove one needlessly.
- **Returning `kept` instead of `removed`.** The problem asks for the
  count to *remove*, which is `n - kept`. Counting removals directly (as
  above) sidesteps this, but if you count keeps, remember to subtract.
- **Forgetting the empty case.** On `intervals.length == 0` the loop never
  runs and `removed` is 0; the early return is a guard, not strictly
  required, but it documents intent and avoids sorting an empty array.

## Related problems

- [0056 - Merge Intervals](../0056-merge-intervals/) - same skeleton, but
  sort by START and merge instead of count.
- [0252 - Meeting Rooms](../0252-meeting-rooms/) - the related detection
  problem; touching intervals are also compatible there.
- [0057 - Insert Interval](../0057-insert-interval/) - building a clean
  non-overlapping list, the inverse of pruning one down.
