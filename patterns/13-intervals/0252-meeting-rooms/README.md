# 0252 - Meeting Rooms

**Difficulty:** Easy
**Pattern:** Intervals
**LeetCode:** https://leetcode.com/problems/meeting-rooms/

## Problem

Given an array of meeting time intervals where `intervals[i] = [start_i,
end_i]`, determine whether a person could attend **all** meetings. The
person cannot attend two meetings that overlap. Meetings that only touch
at a point (one ends exactly when the next starts) are fine — back-to-back
is allowed.

Signature:

    boolean canAttendMeetings(int[][] intervals)

Examples (verbatim from LeetCode):

    Input:  intervals = [[0,30],[5,10],[15,20]]
    Output: false
    Explanation: [0,30] overlaps both [5,10] and [15,20].

    Input:  intervals = [[7,10],[2,4]]
    Output: true

## Intuition

The trigger signals are pure Intervals: a list of `[start, end]` pairs and
the word "overlap". We do not need to merge or count anything — we only
need to say yes or no to "does any pair overlap?".

The brute force compares every pair, O(n^2). The intervals pattern
collapses it to **sort by start, then check each consecutive pair**. Why
is checking only neighbours enough? Once the list is start-sorted, any
interval that overlaps another must overlap the interval immediately
before it: an overlap with a later interval `j` requires `intervals[j].start <
intervals[i].end` for some earlier `i`, and the start-sorted order means
the largest `end` among intervals before `j` is held by `j-1` (or one even
closer). So if no consecutive pair overlaps, no pair at all overlaps.

One subtlety defines this problem: the comparison is **strict** `<`. A
meeting ending at 2 and one starting at 2 are compatible, so
`intervals[i].start < intervals[i-1].end` flags an overlap only when the
next meeting starts *strictly before* the previous one ends. This is the
opposite of Merge Intervals, where touching ranges merge. Read the
problem's overlap definition before picking `<` versus `<=`.

### Checkpoint A -- Neighbours only

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** After sorting the meetings by start, which pairs do we need to compare to detect any overlap?
- a) Every pair (all combinations)
- b) Only consecutive (adjacent) pairs
- c) Only the first and the last

<details><summary>Show answer</summary>

**(b)** -- once sorted, an interval that overlaps anything must overlap the one immediately before it, so checking neighbours is enough. This is what turns O(n^2) into O(n log n).

</details>

**Q2 (comprehend).** The overlap test is strict: `intervals[i][0] < intervals[i-1][1]`. Why strict `<` instead of `<=`?
- a) Because a meeting ending exactly when the next starts (back-to-back) is attendable; `<=` would wrongly flag it as a conflict
- b) Because strict comparison runs faster
- c) Because `<=` would miss real overlaps

<details><summary>Show answer</summary>

**(a)** -- touching meetings like `[1,2]` then `[2,3]` are legal, so the next start must be STRICTLY before the previous end to count as a clash. This single operator is the problem-defining detail.

</details>

## Pseudocode

```text
function canAttendMeetings(intervals):
    sort intervals by start (ascending)

    for i from 1 to length - 1:
        if intervals[i].start < intervals[i-1].end:
            return false              # this pair overlaps -> cannot attend both
    return true
```

The loop does not even need an early-exit variable: the first overlap
short-circuits to `false`; reaching the end means every adjacent pair is
compatible, which (by the sort) means every pair is.

## Java Solution

```java
import java.util.*;

class Solution {
    public boolean canAttendMeetings(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 1; i < intervals.length; i++) {
            // Strict <: a meeting starting exactly when the previous ends is OK.
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false;
            }
        }
        return true;
    }
}
```

`Arrays.sort` orders by start, which makes the overlap check local to
consecutive pairs. We start the loop at `i = 1` and compare `intervals[i]`
against `intervals[i - 1]`, so every adjacent pair is examined exactly
once. The strict `<` (rather than `<=`) is what makes back-to-back
meetings legal — the single most important detail in this problem. With
zero or one meetings the loop body never runs and we return `true`, which
is correct: nothing to conflict with.

## Complexity

    Time:  O(n log n)  -- the sort dominates; the single pass is O(n).
    Space: O(log n)    -- in-place sort's recursion stack. No extra structures.

## Dry-Run

Step-by-step on `intervals = [[0,30],[5,10],[15,20]]` (already start-sorted):

| i | intervals[i] | intervals[i-1] | intervals[i][0] < intervals[i-1][1]? | result |
|---|--------------|----------------|--------------------------------------|--------|
| 1 | `[5,10]`     | `[0,30]`       | 5 < 30 yes                           | false  |

Return `false` at `i = 1`. The meeting `[5,10]` starts at 5, which is
strictly before `[0,30]` ends at 30 — a clear overlap. We never even look
at `[15,20]`, because one conflict is enough to answer.

For contrast, on `intervals = [[7,10],[2,4]]`:

First sort by start: `[[2,4],[7,10]]`.

| i | intervals[i] | intervals[i-1] | 7 < ... no wait, 7 < 4? | result |
|---|--------------|----------------|-------------------------|--------|
| 1 | `[7,10]`     | `[2,4]`        | 7 < 4 no                | —      |

Loop completes with no early return, so the final `return true` fires.
The two meetings are compatible: `[2,4]` ends at 4, `[7,10]` starts at 7,
plenty of gap.

Touching case `[[1,2],[2,3]]`:

| i | intervals[i] | intervals[i-1] | 2 < 2? | result |
|---|--------------|----------------|--------|--------|
| 1 | `[2,3]`      | `[1,2]`        | no     | —      |

Loop completes, return `true` — the back-to-back meetings are attendable.
A non-strict `<=` here would wrongly flag an overlap.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `intervals = [[1,5],[3,4],[6,7]]` after sorting by start. What is returned?
- a) `true`
- b) `false`
- c) Throws an exception

<details><summary>Show answer</summary>

**(b)** -- already start-sorted. At `i = 1`, `[3,4]` has `start 3 < [1,5].end 5`, a clear overlap, so we `return false` immediately. We never even look at `[6,7]`.

</details>

**Q2 (analyze).** What does `canAttendMeetings([])` (empty input) return, and why does the code handle it with no special case?
- a) `true` -- the loop never runs, so control falls through to `return true`; with zero meetings nothing can conflict
- b) `false` -- the empty input is special
- c) It throws an exception

<details><summary>Show answer</summary>

**(a)** -- the loop starts at `i = 1` and runs while `i < length`, so for length 0 (and 1) the body never executes, and the trailing `return true` fires. The default is "attendable".

</details>

**Q3 (transfer).** Suppose the question changed to "what is the MINIMUM number of meetings you must cancel so the rest do not overlap?" How does the approach change?

<details><summary>Show answer</summary>

Switch to the Non-overlapping Intervals algorithm: sort by END (not start), greedily keep the earliest-ending compatible meeting, and count how many are removed. The "detect any overlap" walk is replaced by a greedy count that tracks the end of the last KEPT interval.

</details>

## Common mistakes

- **Using `<=` instead of `<`.** Touching meetings are legal, so
  `intervals[i][0] <= intervals[i-1][1]` reports a false conflict on
  back-to-back meetings like `[1,2]` then `[2,3]`. This is the
  problem-defining detail.
- **Forgetting to sort.** On unsorted input, two overlapping meetings that
  are not adjacent (e.g. `[[2,4],[7,10],[0,3]]`, where `[0,3]` overlaps
  `[2,4]`) are never compared, and the function wrongly returns `true`.
- **Comparing the wrong endpoints.** The check is next start vs previous
  end (`intervals[i][0]` vs `intervals[i-1][1]`). Mixing up indices —
  comparing start to start, or end to end — never detects overlap.
- **Nested-loop fallback.** Reaching for the O(n^2) all-pairs comparison
  "to be safe" works but throws away the whole point of the pattern and
  times out on large inputs.
- **Returning the wrong default.** If no overlap is found the answer is
  `true` (all meetings attendable), not `false`. The early return is the
  `false` case; the loop falling through is the `true` case.

## Related problems

- [0056 - Merge Intervals](../0056-merge-intervals/) - same sort, but the
  loop merges instead of just detecting, and the touching rule flips.
- [0435 - Non-overlapping Intervals](../0435-non-overlapping-intervals/) -
  the follow-up: now count the minimum meetings to cancel.
- [0057 - Insert Interval](../0057-insert-interval/) - same family, with
  one new interval to splice into an already-clean list.
