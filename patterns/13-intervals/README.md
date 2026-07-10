# Pattern 13 - Intervals

## What the pattern is

An **interval** is a pair `[start, end]` representing a continuous range on
the number line — a meeting from 9:00 to 10:00, a task scheduled from day 3
to day 5, a CPU job that runs from time 2 to time 7. The **intervals
pattern** is the small, sharp toolkit for solving problems that hand you a
list of these pairs and ask something about how they *overlap*: merge the
overlapping ones, insert a new one, count how many you must delete so the
rest do not overlap, decide whether a person can attend every meeting.

The pattern earns its own section because almost every intervals problem
collapses to **one sort plus one linear pass**. There is no clever data
structure, no DP table, no graph. Once the list is sorted by the right key,
the answer falls out of a single walk that compares each interval with the
one just before it. The hard part is choosing that key and writing the
overlap test correctly — those two decisions account for nearly every bug
beginners hit here.

## When it applies (trigger signals)

Reach for Intervals when the problem statement or input shows any of these:

| Signal | Example phrasing |
|---|---|
| **List of `[start, end]` pairs** | `[[1,3],[2,6],[8,10],[15,18]]` |
| **"Merge overlapping"** | "merge all overlapping intervals and return the non-overlapping set" |
| **"Insert" into non-overlapping list** | "insert newInterval into a sorted, non-overlapping list" |
| **Meeting rooms / scheduling** | "can a person attend all meetings", "minimum rooms needed" |
| **"Non-overlapping" / "minimum to remove"** | "minimum number of intervals to remove so the rest do not overlap" |
| **Coverage / union length** | "total length covered by at least one interval" |

The tell-tale sign: the brute force is O(n^2) (compare every pair) or
worse, and the input is a flat list of ranges. Sorting the list by one
endpoint turns that into O(n log n) + O(n).

## The core preprocessing step: SORT

Every solution in this section begins by sorting. Which key you sort by
decides the rest of the algorithm:

- **Sort by START** for merge / insert / overlap-detection. After this
  pass, any interval that overlaps another must overlap its *immediate
  predecessor* in the sorted order, so a single left-to-right walk sees all
  the action.
- **Sort by END** for the "minimum removals" greedy. This is subtle — see
  the dedicated callout below.

If you forget to sort, the rest of the algorithm is silently wrong: it will
miss overlaps that span non-adjacent intervals and return garbage on
unsorted input. Sort first, every time, even if the examples happen to be
pre-sorted.

## The overlap test

Two intervals `[a, b]` and `[c, d]` (with `a <= b` and `c <= d`) **overlap**
if and only if they share at least one point:

```text
overlap([a,b], [c,d])  <=>  max(a, c) <= min(b, d)
```

This single symmetric formula handles every case: nested, partial, and
touching. Memorize it; it removes all doubt about which endpoint to compare.

Once you have sorted by start (so `a <= c`), the formula simplifies. The
left side `max(a, c)` becomes just `c`, so:

```text
when sorted by start:  overlap  <=>  c <= b
                               i.e.  next.start <= prev.end
```

That is the test you will actually write in code.

### Critical subtlety: `<=` vs `<` (touching intervals)

Whether `[1,2]` and `[2,3]` count as "overlapping" is **problem-dependent**,
and getting it wrong is the most common intervals bug:

- **Merge Intervals (LC 56)** — touching intervals ARE merged. The problem
  statement explicitly treats `[1,2]` and `[2,3]` as overlapping. Use `<=`.
- **Meeting Rooms (LC 252)** — a meeting ending exactly when the next
  starts is fine; back-to-back is allowed. Use strict `<`.
- **Non-overlapping Intervals (LC 435)** — same as meeting rooms: touching
  intervals are non-overlapping. Use `<=` for "keep" (`next.start >=
  prev.end`).

Always read the problem's definition of overlap. When in doubt, the
max/min formula with the comparison operator the problem dictates is the
source of truth.

## A general pseudocode template (the merge pass)

Almost every "merge / insert / count overlap" solution has this shape:

```text
function solve(intervals):
    sort intervals by start
    result = empty list
    for each interval cur in sorted order:
        if result is not empty and cur.start <= last.end of result:
            merge cur into the last interval of result
            last.end = max(last.end, cur.end)
        else:
            append cur to result as a new interval
    return result
```

Two lines carry the whole pattern:

1. `cur.start <= last.end` — the overlap test (note the `<=`; touching
   merges here).
2. `last.end = max(last.end, cur.end)` — the merge. You must take the max
   because one interval can fully contain another (e.g. `[1, 10]` then
   `[2, 5]`); without the max, the end would shrink and silently lose
   coverage.

For the non-removal flavours (overlap detection, min removals) the body of
the loop changes — a counter, an early `return false` — but the
sort-then-walk skeleton is identical.

## When to sort by START vs by END

This is the single subtle decision in the whole pattern, and it is worth
its own callout.

**Sort by START** for: Merge Intervals, Insert Interval, Meeting Rooms,
Meeting Rooms II. The reason: you want to process intervals in the natural
order they begin, so each new interval can only interact with the one
currently "open" at the front of the result. The simplification
`overlap <=> next.start <= prev.end` only holds under start-sort.

**Sort by END** for: Non-overlapping Intervals ("minimum removals"), and
any "maximum number of non-overlapping intervals you can keep". The reason
is a greedy **exchange argument**: among all optimal selections of
non-overlapping intervals, you can always swap the first chosen interval
for the one that *ends earliest* without making the selection invalid or
smaller. The earliest-ending interval leaves the maximum remaining space
for the rest, so committing to it is safe. Sorting by start does NOT give
you this property — a long interval that starts early but ends late would
be greedily kept, wrongly blocking many shorter ones.

> Rule of thumb: **"merge / detect overlap" → sort by start.
> "minimize removals / maximize count kept" → sort by end.**

If you sort Non-overlapping Intervals by start instead of end, you will
pass the examples and fail the judge on a case like `[[1,10],[2,3],[4,5]]`
(start-sort greedily keeps `[1,10]` and removes two; end-sort keeps `[2,3]`
and `[4,5]`, removing one — the correct answer).

## Problems in this section

| # | LC | Problem | Difficulty | One-line teaser |
|---|----|---------|-----------|-----------------|
| 79 | 56 | [Merge Intervals](./0056-merge-intervals/) | Medium | The canonical problem: sort by start, one pass merging with `max` on the end. |
| 80 | 57 | [Insert Interval](./0057-insert-interval/) | Medium | Insert a non-overlapping new interval, then merge — three phases: before, overlap, after. |
| 81 | 435 | [Non-overlapping Intervals](./0435-non-overlapping-intervals/) | Medium | Greedy min-removal: sort by END, keep the earliest-ending non-overlapping interval. |
| 82 | 252 | [Meeting Rooms](./0252-meeting-rooms/) | Easy | Can a person attend all meetings? Sort by start, check each consecutive pair. |

Work them in that order. Merge Intervals establishes the sort-then-walk
skeleton and the `max`-merge. Insert Interval adds the "one new interval
plus three phases" twist on the same skeleton. Non-overlapping Intervals
introduces the sort-by-END insight and the exchange argument behind it.
Meeting Rooms is the gentle cooldown: the same sort, but the loop body is a
single consecutive-pair overlap check.

## Common pitfalls

- **Forgetting to sort.** The entire pattern assumes sorted input. An
  unsorted list silently misses overlaps between non-adjacent intervals.
  Sort first, even when the example input is already sorted.
- **Using the wrong overlap test.** The two endpoints of the same
  interval are compared, but beginners often write `cur.start <
  prev.start` or compare starts to starts. Use `next.start <= prev.end`
  (or `<` per the problem's touching rule). The `max(a,c) <= min(b,d)`
  formula is the safe fallback when confused.
- **Shrinking the end on merge.** Writing `last.end = cur.end` instead of
  `max(last.end, cur.end)` loses coverage when a later interval is fully
  nested inside the current one (`[1,10]` then `[2,5]`). Always take the
  max of the ends.
- **Modifying the input vs returning a new list.** Merge and Insert should
  build and return a fresh list; mutating the caller's array in place is
  error-prone (and LeetCode expects a new `int[][]`). Non-overlapping and
  Meeting Rooms only read the sorted array and return a count / boolean.
- **`<=` vs `<` on touching intervals.** Merge treats `[1,2]`/`[2,3]` as
  overlapping (merge them); Meeting Rooms treats them as compatible (do
  not flag). Read the problem's definition of overlap before choosing the
  operator.
- **Sorting by the wrong key for min-removal.** Non-overlapping Intervals
  must sort by END, not start. Sorting by start passes the examples but
  fails on any case where a long early interval blocks several short ones.
- **Off-by-one on the comparator.** When sorting `int[][]` with a
  `Comparator`, compare `a[0]` with `b[0]` (the start), not `a[0]` with
  `b[1]`. A typo here makes the sort silently wrong and every later step
  unreliable.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what single preprocessing step does every intervals solution in this section share?

<details><summary>Show answer</summary>

Sort the list of intervals by some key (start for merge/detect/insert, end for min-removal), then do one linear pass. Without that sort, every later step silently misses overlaps between non-adjacent intervals.

</details>

**Q2 (pattern recognition).** A new problem: "Given employees' work schedules as `[start,end]` pairs, find the total time during which AT LEAST ONE employee is working (the union length)." Which tool fits best?
- a) Sort by start, merge the overlapping intervals, then sum `(end - start)` over the merged list
- b) Sort by end, count removals
- c) Compare only consecutive pairs and stop at the first overlap

<details><summary>Show answer</summary>

**(a)** -- this is a coverage/union question, so the merge pass produces exactly the union; its total length is the answer. (b) counts removals and (c) only detects whether any overlap exists.

</details>

**Q3 (pattern recognition).** A new problem: "A conference has many talks as `[start,end]` pairs; find the MAXIMUM number of talks one person can attend with no overlaps." Which approach fits best?
- a) Sort by start, merge all overlaps
- b) Sort by end, greedily keep the earliest-ending talk compatible with the last kept one; the number kept is the answer
- c) Sort by talk length, shortest first

<details><summary>Show answer</summary>

**(b)** -- "maximize count of non-overlapping intervals" is exactly the Non-overlapping Intervals greedy. The exchange argument licenses the end-sort and the earliest-ending rule; the answer is that count (or `n - removed`).

</details>

**Q4 (apply).** You run Merge Intervals on `[[1,4],[0,2],[3,5]]`. After sorting by start the order is `[[0,2],[1,4],[3,5]]`. What is the final merged result?
- a) `[[0,2],[1,4],[3,5]]`
- b) `[[0,5]]`
- c) `[[0,4],[3,5]]`

<details><summary>Show answer</summary>

**(b)** -- step 1 appends `[0,2]`; step 2 `[1,4]` has `1 <= 2` overlap, end becomes `max(2,4)=4`, tail is `[0,4]`; step 3 `[3,5]` has `3 <= 4` overlap, end becomes `max(4,5)=5`, tail is `[0,5]`. Final: `[[0,5]]`.

</details>

**Q5 (design).** Sketch (in words, not code) how to solve "Meeting Rooms II": given meeting intervals, find the MINIMUM number of meeting rooms required so all meetings can run. Use ideas from this pattern.

<details><summary>Show answer</summary>

One approach: sort all start times and all end times into two separate sorted lists, then walk both with two pointers -- increment a "rooms in use" counter at each start, decrement at each end, and track the maximum value reached. The sort-then-walk skeleton is the same as this pattern; the twist is tracking a running count rather than merging. (Equivalently: sort by start and use a min-heap of currently-running meetings' end times; the heap's largest size is the answer.)

</details>
