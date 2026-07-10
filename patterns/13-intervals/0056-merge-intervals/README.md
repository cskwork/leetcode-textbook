# 0056 - Merge Intervals

**Difficulty:** Medium
**Pattern:** Intervals
**LeetCode:** https://leetcode.com/problems/merge-intervals/

## Problem

You are given an array of intervals where `intervals[i] = [start_i, end_i]`.
Merge all overlapping intervals, and return an array of the non-overlapping
intervals that cover all the intervals in the input. Note: intervals that
only touch at a single point (e.g. `[1,2]` and `[2,3]`) count as overlapping
and must be merged.

Signature:

    int[][] merge(int[][] intervals)

Examples (verbatim from LeetCode):

    Input:  intervals = [[1,3],[2,6],[8,10],[15,18]]
    Output: [[1,6],[8,10],[15,18]]
    Explanation: [1,3] and [2,6] overlap -> merged into [1,6].

    Input:  intervals = [[1,4],[4,5]]
    Output: [[1,5]]
    Explanation: touching intervals are considered overlapping.

## Intuition

The trigger signals are unmistakable: a list of `[start, end]` pairs and
the word "merge". The brute force compares every pair of intervals
(O(n^2)) and keeps merging until no pair overlaps — correct but clumsy,
and it has to be re-run after every merge.

The intervals pattern collapses this to **sort by start, then one pass**.
Once sorted, an interval can only overlap its immediate predecessor: any
interval that overlaps the current one must start no later than the
current end, and because the list is start-sorted, the candidate
predecessors are exactly those already placed at the back of the result.
So we walk left to right, keeping a running "last merged interval" at the
tail of the output; each new interval either extends that tail (overlap)
or starts a fresh tail (no overlap).

The overlap test under start-sort is `next.start <= last.end` — note the
`<=`, because touching intervals merge in this problem. The merge itself
is `last.end = max(last.end, next.end)`; the `max` is mandatory because a
later interval can be fully nested inside an earlier one (e.g. `[1,10]`
then `[2,5]`), and a plain assignment would shrink the end and lose
coverage.

### Checkpoint A -- Sort then walk

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Once the intervals are sorted by start, any interval that overlaps something can only overlap which one?
- a) Any interval anywhere in the list
- b) The interval at the tail of the result so far (its immediate predecessor)
- c) Only the interval immediately after it

<details><summary>Show answer</summary>

**(b)** -- after start-sorting, every interval that starts before the current one has already been placed, and the only one whose end could still reach forward is the tail of the result. That is what makes the overlap test local.

</details>

**Q2 (comprehend).** On input `[[1,10],[2,5]]` the merge step uses `last.end = max(last.end, cur.end)`. What goes wrong if you wrote `last.end = cur.end` instead?
- a) Nothing -- both give the same merged range
- b) The merged interval shrinks to `[1,5]`, losing coverage of points 5..10
- c) The merged interval grows to `[1,10]` correctly

<details><summary>Show answer</summary>

**(b)** -- `[2,5]` is fully nested inside `[1,10]`, so assigning `cur.end` (5) overwrites the larger end (10). The `max` is mandatory precisely for this nested case.

</details>

## Pseudocode

```text
function merge(intervals):
    sort intervals by start (ascending)
    result = empty list

    for each interval cur in sorted intervals:
        if result is not empty and cur.start <= last interval's end:
            overlap -> merge: last.end = max(last.end, cur.end)
        else:
            no overlap -> append cur to result as a new interval
    return result
```

The whole algorithm is the loop body. The sort makes the overlap test
local (only the tail matters); the `max` makes the merge correct under
nesting.

## Java Solution

```java
import java.util.*;

class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] cur : intervals) {
            if (!merged.isEmpty() && cur[0] <= merged.get(merged.size() - 1)[1]) {
                // Overlap (incl. touching): extend the end, never shrink it.
                merged.get(merged.size() - 1)[1] =
                        Math.max(merged.get(merged.size() - 1)[1], cur[1]);
            } else {
                merged.add(cur);
            }
        }
        return merged.toArray(new int[0][]);
    }
}
```

`Arrays.sort` with a lambda comparing `a[0]` does the start-sort. We keep
the merged intervals in an `ArrayList<int[]>` and mutate the tail's `end`
on overlap — the tail element is the same array reference we appended, so
updating index `[1]` is in place. The `max` guard prevents a nested
interval from shrinking the tail. `merged.toArray(new int[0][])`
converts the list back to the `int[][]` LeetCode expects; `new int[0][]`
lets the JVM size the result array itself. The non-overlap branch appends
`cur` directly because after the merge pass the original arrays in the
non-overlapping case are exactly what we want to return.

## Complexity

    Time:  O(n log n)  -- the sort dominates; the single pass is O(n).
    Space: O(n)        -- the merged list holds up to n intervals (output).
                          Sorting is in place, so no extra work-space beyond output.

## Dry-Run

Step-by-step on `intervals = [[1,3],[2,6],[8,10],[15,18]]`
(already start-sorted for readability):

| Step | cur       | last in result | cur.start <= last.end? | action                | result after                |
|------|-----------|----------------|------------------------|-----------------------|-----------------------------|
| 1    | `[1,3]`   | (empty)        | —                      | append                | `[[1,3]]`                   |
| 2    | `[2,6]`   | `[1,3]`        | 2 <= 3 yes             | merge -> end=max(3,6) | `[[1,6]]`                   |
| 3    | `[8,10]`  | `[1,6]`        | 8 <= 6 no              | append                | `[[1,6],[8,10]]`            |
| 4    | `[15,18]` | `[8,10]`       | 15 <= 10 no            | append                | `[[1,6],[8,10],[15,18]]`    |

Final result: `[[1,6],[8,10],[15,18]]`.

A nested case to show why `max` matters, `[[1,10],[2,5],[6,8]]`:

| Step | cur      | last       | overlap? | action            | result after             |
|------|----------|------------|----------|-------------------|--------------------------|
| 1    | `[1,10]` | (empty)    | —        | append            | `[[1,10]]`               |
| 2    | `[2,5]`  | `[1,10]`   | 2<=10 yes| merge end=max(10,5)=10 | `[[1,10]]`           |
| 3    | `[6,8]`  | `[1,10]`   | 6<=10 yes| merge end=max(10,8)=10 | `[[1,10]]`           |

Without `max`, step 2 would set end to 5 and lose coverage of `[6,8]`.
With `max`, the result stays `[[1,10]]` — correct.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `intervals = [[1,4],[2,3]]` (already start-sorted). What does `merge` return?
- a) `[[1,3]]`
- b) `[[1,4]]`
- c) `[[1,4],[2,3]]`

<details><summary>Show answer</summary>

**(b)** -- step 1 appends `[1,4]`; step 2 reads `[2,3]`, `2 <= 4` overlaps, and `end = max(4, 3) = 4`, so the tail stays `[1,4]`. The nested `[2,3]` is swallowed without shrinking the end.

</details>

**Q2 (analyze).** On `[[1,4],[4,5]]` the overlap test is `cur.start <= last.end`. What would change if you used `<` instead?
- a) Correct: returns `[[1,5]]`
- b) Wrong: returns `[[1,4],[4,5]]`, because the touching pair would not merge
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- this problem treats touching intervals as overlapping, so the test must be `<=`. A strict `<` leaves `[1,4]` and `[4,5]` separate, failing the problem's own example.

</details>

**Q3 (transfer).** Instead of returning the merged list, suppose you must return the total LENGTH of the number line covered by at least one interval (e.g. `[[1,3],[8,10]]` covers `2 + 2 = 4`). How would you adapt the merge pass?

<details><summary>Show answer</summary>

Build the merged list exactly as before, then sum `(end - start)` over each interval in it. The merged list is precisely the union of coverage, so its total length is the answer. No other change is needed.

</details>

## Common mistakes

- **Forgetting to sort.** On unsorted input, non-adjacent overlaps are
  missed. Always sort by start as the first line, even when the sample
  input is already sorted.
- **Comparing the wrong endpoints.** The test is `cur[0] <= last[1]`
  (next start vs previous end). Beginners sometimes write `cur[0] <=
  last[0]` (start vs start), which never detects overlap.
- **Shrinking the end on merge.** `last[1] = cur[1]` instead of `max`
  loses coverage whenever a later interval is nested inside the current
  merged range. Use `Math.max`.
- **Using `<` instead of `<=`.** Touching intervals `[1,4]` and `[4,5]`
  must merge in this problem; a strict `<` leaves them separate and fails
  the example.
- **Mutating the input when you should not.** Appending `cur` directly
  reuses the caller's array; that is fine here because we only ever
  rewrite the `end` of a tail we have taken ownership of, but copying
  (`new int[]{cur[0], cur[1]}`) is safer if downstream code mutates the
  original.

## Related problems

- [0057 - Insert Interval](../0057-insert-interval/) - the same merge
  pass, but preceded by inserting one new interval into a sorted list.
- [0435 - Non-overlapping Intervals](../0435-non-overlapping-intervals/) -
  the inverse: count how many to delete so the rest look like a merged set.
- [0252 - Meeting Rooms](../0252-meeting-rooms/) - overlap *detection*
  only, no merging; the sort-then-walk skeleton is identical.
