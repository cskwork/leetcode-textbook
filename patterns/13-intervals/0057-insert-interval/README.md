# 0057 - Insert Interval

**Difficulty:** Medium
**Pattern:** Intervals
**LeetCode:** https://leetcode.com/problems/insert-interval/

## Problem

You are given an array of **non-overlapping** intervals sorted by their
start time, `intervals[i] = [start_i, end_i]`, and a new interval
`newInterval = [start, end]`. Insert `newInterval` into `intervals` so that
the list stays sorted, non-overlapping, and merged as needed. Return the
resulting list.

Signature:

    int[][] insert(int[][] intervals, int[] newInterval)

Examples (verbatim from LeetCode):

    Input:  intervals = [[1,3],[6,9]], newInterval = [2,5]
    Output: [[1,5],[6,9]]

    Input:  intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
    Output: [[1,2],[3,10],[12,16]]
    Explanation: the new interval [4,8] overlaps [3,5],[6,7],[8,10].

## Intuition

The trigger signals are the same as Merge Intervals, plus the key gift:
**the input is already sorted and non-overlapping**. That constraint turns
the problem from "merge everything" into "place one new interval and merge
only what it touches". No full sort is needed — a single left-to-right
walk does everything.

Walk the list and classify each existing interval by where it stands
relative to `newInterval`. There are exactly three phases, and they always
appear in this order:

1. **Before.** Intervals whose `end < newInterval.start`. They sit entirely
   to the left of the new interval — copy them straight through.
2. **Overlap.** Intervals that touch or intersect `newInterval`. Because
   the list is sorted, these form one contiguous block. Absorb each into
   `newInterval` by taking `min` of the starts and `max` of the ends. This
   grows `newInterval` to its final merged shape; touching intervals are
   absorbed too (`<=` in disguise).
3. **After.** Intervals whose `start > newInterval.end`. They sit entirely
   to the right — push the (now-final) `newInterval` once, then copy the
   rest straight through.

The trick is to defer inserting `newInterval` until the first "after"
interval appears (or until the loop ends). That way the merged range is
emitted exactly once, in the correct position.

## Pseudocode

```text
function insert(intervals, newInterval):
    result = empty list
    placed = false                  # has newInterval been emitted yet?

    for each interval cur in intervals:
        if cur.end < newInterval.start:
            # phase 1: entirely before
            append cur to result
        else if cur.start > newInterval.end:
            # phase 3: entirely after -> newInterval goes first (once)
            if not placed:
                append newInterval to result
                placed = true
            append cur to result
        else:
            # phase 2: overlap -> absorb cur into newInterval
            newInterval.start = min(newInterval.start, cur.start)
            newInterval.end   = max(newInterval.end,   cur.end)

    # newInterval never overlapped a later interval -> emit it at the tail
    if not placed:
        append newInterval to result

    return result
```

## Java Solution

```java
import java.util.*;

class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int start = newInterval[0];
        int end = newInterval[1];
        boolean placed = false;

        for (int[] cur : intervals) {
            if (cur[1] < start) {
                // Entirely before the new interval.
                result.add(cur);
            } else if (cur[0] > end) {
                // Entirely after the new interval: emit it once, then this one.
                if (!placed) {
                    result.add(new int[]{start, end});
                    placed = true;
                }
                result.add(cur);
            } else {
                // Overlap (incl. touching): absorb cur into the running range.
                start = Math.min(start, cur[0]);
                end = Math.max(end, cur[1]);
            }
        }

        // newInterval belongs at the tail (no later interval triggered phase 3).
        if (!placed) {
            result.add(new int[]{start, end});
        }
        return result.toArray(new int[0][]);
    }
}
```

We copy `newInterval`'s endpoints into local `start`/`end` so the merge
mutates only those locals, never the caller's array. The `placed` flag
guarantees the merged range is emitted exactly once: it fires either at the
first "after" interval (correct sorted position) or after the loop (if the
new interval ends up last). The three branches are mutually exclusive
because "before" and "after" already peeled off the disjoint cases, so the
`else` is precisely the overlap block — no extra overlap test needed.
`result.toArray(new int[0][])` converts back to the `int[][]` LeetCode
wants.

## Complexity

    Time:  O(n)   -- the input is already sorted, so one pass with no sort.
    Space: O(n)   -- the output list holds up to n + 1 intervals.

## Dry-Run

Step-by-step on `intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]]`,
`newInterval = [4,8]` (so `start = 4`, `end = 8`):

| Step | cur       | branch (start=4, end=8)              | start/end after | result after                  | placed |
|------|-----------|---------------------------------------|-----------------|-------------------------------|--------|
| 1    | `[1,2]`   | 2 < 4 -> before                       | 4 / 8           | `[[1,2]]`                     | false  |
| 2    | `[3,5]`   | 3<=4<=5<=8 -> overlap                 | min(4,3)=4 / max(8,5)=8 | `[[1,2]]`              | false  |
| 3    | `[6,7]`   | 6<=8, 6>=4 -> overlap                 | 4 / max(8,7)=8  | `[[1,2]]`                     | false  |
| 4    | `[8,10]`  | 8<=8 (touching) -> overlap            | 4 / max(8,10)=10| `[[1,2]]`                     | false  |
| 5    | `[12,16]` | 12 > 10 -> after; emit [4,10], then cur | 4 / 10        | `[[1,2],[4,10],[12,16]]`      | true   |

Loop ends with `placed = true`, so the trailing emit is skipped. Final
result: `[[1,2],[4,10],[12,16]]` — matches the expected output.

For contrast, a no-overlap case `intervals = [[5,6]]`, `newInterval = [1,2]`
(start=1, end=2):

| Step | cur      | branch            | result after        | placed |
|------|----------|-------------------|---------------------|--------|
| 1    | `[5,6]`  | 5 > 2 -> after    | `[[1,2],[5,6]]`     | true   |

Result `[[1,2],[5,6]]` — the new interval is emitted before any existing
one because every existing interval is in the "after" phase.

## Common mistakes

- **Re-sorting the input.** The problem guarantees sorted, non-overlapping
  intervals. Re-sorting wastes time and masks a misunderstanding of the
  three-phase structure; the solution is O(n), not O(n log n).
- **Mutating the caller's `newInterval`.** Writing back into
  `newInterval[0]` / `newInterval[1]` works on LeetCode but surprises
  callers in tests. Copy into local `start`/`end` and emit a fresh
  `new int[]{start, end}`.
- **Emitting `newInterval` more than once (or zero times).** Without the
  `placed` guard, an input that ends entirely in the overlap phase
  (e.g. `[[1,5]]`, `[2,3]`) never emits the merged range; an input with
  many "after" intervals would emit it once per "after". The flag fixes
  both.
- **Wrong touching rule.** Like Merge Intervals, touching intervals
  (`[1,4]` and `[4,5]`) must merge here. The `else` branch uses `<=` on
  both bounds implicitly; converting it to strict inequalities would
  leave touching intervals separate.
- **Forgetting the post-loop emit.** When `newInterval` is the largest
  (no "after" interval ever appears), the trailing `if (!placed)` is the
  only thing that emits it. Skip it and the last interval silently
  vanishes.

## Related problems

- [0056 - Merge Intervals](../0056-merge-intervals/) - the merge pass
  this problem builds on, applied to an unsorted list.
- [0435 - Non-overlapping Intervals](../0435-non-overlapping-intervals/) -
  the inverse question: how many to delete to reach a clean list.
- [0252 - Meeting Rooms](../0252-meeting-rooms/) - overlap detection on a
  sorted list, the simplest walk of the family.
