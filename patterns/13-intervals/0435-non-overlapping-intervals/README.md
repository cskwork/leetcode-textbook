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

### Checkpoint A -- Greedy by end

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** To minimize the number of intervals removed, the algorithm sorts by:
- a) Start, ascending
- b) End, ascending
- c) Interval length

<details><summary>Show answer</summary>

**(b)** -- the exchange argument licenses committing to the earliest-ending interval first, which leaves the most room for everything after it. This is the line that separates this problem from Merge Intervals.

</details>

**Q2 (comprehend).** Why sort by end and not by start? Think about `[[1,10],[2,3],[4,5]]`:
- a) End-sort keeps the two short intervals and removes only the long one (answer 1); start-sort would wrongly keep the long one and remove two
- b) Both sort keys give the same answer on this input
- c) Start-sort is correct and end-sort is wrong

<details><summary>Show answer</summary>

**(a)** -- a long interval that starts early but ends late blocks several short ones if you sort by start. Sorting by end greedily commits to the choice the exchange argument proved safe.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `intervals = [[1,2],[1,2],[1,2]]` after sorting by end. What is returned?
- a) 0
- b) 1
- c) 2

<details><summary>Show answer</summary>

**(c)** -- sorted by end the order is unchanged (all tie). Step 1 keeps `[1,2]` (`prevEnd = 2`); steps 2 and 3 each have `start 1 >= 2`? no, so each is removed. `removed = 2`.

</details>

**Q2 (analyze).** On `[[1,2],[2,3]]` the keep test is `cur.start >= prevEnd`. What would happen if you wrote `cur.start > prevEnd` (strict) instead?
- a) Correct: returns 0
- b) Wrong: returns 1, because the touching pair `[1,2]`/`[2,3]` would be flagged as conflicting
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- touching intervals are compatible in this problem. With `>`, `[2,3]` (`start 2`) versus `prevEnd 2` gives `2 > 2` false, so it is removed and the answer becomes 1 instead of 0.

</details>

**Q3 (transfer).** Suppose in a variant touching intervals ARE considered overlapping (the opposite rule from this problem). What single change adapts the algorithm?

<details><summary>Show answer</summary>

Change the keep test from `cur.start >= prevEnd` to `cur.start > prevEnd`. The sort-by-end key and the greedy rule stay the same; only the compatibility operator flips to match the new touching rule.

</details>

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
