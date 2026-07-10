# 0128 - Longest Consecutive Sequence

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/longest-consecutive-sequence/

## Concepts used

- **Hash set** -- a container that answers "is x in here?" instantly (O(1)). We pour every value
  in first, then query. [glossary](../../../docs/10-glossary.md#hash-set)
- **Linear scan** -- walking the array (or the set) one value at a time.
  [glossary](../../../docs/10-glossary.md#linear-scan)
- **Amortized** -- occasionally expensive but cheap on average over many operations; this is why
  the total work is O(n) despite an inner counting loop.
  [glossary](../../../docs/10-glossary.md#amortized)

## Problem

Given an unsorted array of integers `nums`, return the length of the longest sequence of
consecutive integers that can be formed from its elements. The algorithm must run in **O(n)**
time. Duplicates in the input count only once (a sequence is a set of consecutive values).

Signature:

    int longestConsecutive(int[] nums)

Examples:

    Input:  nums = [100,4,200,1,3,2]
    Output: 4         # the sequence is [1, 2, 3, 4]

    Input:  nums = [0,3,7,2,5,8,4,6,0,1]
    Output: 9         # the sequence is [0,1,2,3,4,5,6,7,8]

## Intuition

You hold a handful of numbered raffle tickets and want the longest unbroken run -- something like
5, 6, 7, 8. You could sort the pile first, but that is slower than the problem allows. Instead,
dump every ticket into a hat. Then for each ticket, ask the hat "do you hold the next numbers up?"
and count how far the run extends. The trick is to start counting only from a ticket whose
predecessor is NOT in the hat -- otherwise you recount the same run over and over.

Walk the smallest case, `nums = [100, 4, 200, 1, 3, 2]`. The hat holds
`{1, 2, 3, 4, 100, 200}`. Pick `1`: is `0` in the hat? No -- so `1` begins a run. `1, 2, 3, 4`
are all in the hat; `5` is not. Run length `4`. Pick `2`: is `1` in the hat? Yes -- skip, this is
not a start. The longest run found is `4`.

The general rule: first pour every value into a [hash set](../../../docs/10-glossary.md#hash-set),
which makes "is `x` present?" an O(1) question forever after. A consecutive sequence is simply a
chain `x, x+1, x+2, ...` whose every member is in the set. The key move: walk a chain only from
its leftmost value. A value `x` is the leftmost exactly when `x - 1` is NOT in the set (nothing
comes before it). For every such start, count upward how long the chain runs, and track the
longest.

Why does the start-guard keep this O(n)? Without it, you would walk the chain from every element
and revisit the same numbers many times -- O(n^2) in the worst case (think
`nums = [1, 2, 3, ..., n]`). With the guard, each value belongs to at most one chain and is
visited at most once across the whole scan, so the total chain-walking is O(n)
([amortized](../../../docs/10-glossary.md#amortized): a single chain may be long, but summed over
all starts the work is bounded by `n`).

### Checkpoint A -- The start-guard

Pause before expanding.

**Q1 (recall).** A value `x` is treated as the START of a sequence only when...
- a) `x` is the smallest in the array
- b) `x - 1` is NOT in the set
- c) `x + 1` is in the set

<details><summary>Show answer</summary>

**(b)** -- if `x - 1` is absent, nothing comes before `x`, so `x` is the leftmost of its run and we count upward from here. This guard is what keeps the whole algorithm O(n).

</details>

**Q2 (comprehend).** Without the start-guard, why would the solution become O(n^2)?
- a) The set would be too large
- b) Every element would re-walk its chain; on input like [1,2,...,n] the chain is walked n times for ~n^2 total steps
- c) Sorting would be required

<details><summary>Show answer</summary>

**(b)** -- the guard ensures each value is walked by a chain at most once. Without it, every element of a long consecutive run re-traverses the same run, blowing up to quadratic.

</details>

## Pseudocode

    function longestConsecutive(nums):
        put every value of nums into a set (duplicates collapse)
        best <- 0
        for each value x in the set:
            if (x - 1) is NOT in the set:          # x begins a sequence
                length <- 1
                next <- x + 1
                while next is in the set:
                    length <- length + 1
                    next <- next + 1
                best <- max(best, length)
        return best

## Java Solution

```java
import java.util.HashSet;
import java.util.Set;

class Solution {
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) {
            set.add(x);
        }

        int best = 0;
        for (int x : set) {
            if (!set.contains(x - 1)) {
                int length = 1;
                int next = x + 1;
                while (set.contains(next)) {
                    length++;
                    next++;
                }
                best = Math.max(best, length);
            }
        }
        return best;
    }
}
```

We iterate over the *set* (not the original array) so duplicates are not re-examined. The guard
`!set.contains(x - 1)` is the whole algorithm: it ensures the inner `while` only fires from a
sequence's leftmost element. Although the `while` looks like it could be O(n) per element, every
number is part of at most one chain and is consumed once across the entire outer loop, so the
amortized cost is O(n). An empty array yields an empty set, the loop never runs, and `best` stays
`0` -- the correct answer.

## Complexity

    Time:  O(n)  -- building the set is O(n); each value is walked by a chain at most once
    Space: O(n)  -- the set holds up to every distinct value

## Dry-Run

Input `nums = [100, 4, 200, 1, 3, 2]`. Set = `{1, 2, 3, 4, 100, 200}`.

For each `x`, check whether `x - 1` is present (i.e., is `x` a start?):

| x   | x-1 in set? | is start? | chain walked            | length | best |
|----:|-------------|-----------|-------------------------|-------:|-----:|
| 100 | 99? no      | yes       | 100, 101? no            | 1      | 1    |
| 4   | 3? yes      | no        | (skip)                  | -      | 1    |
| 200 | 199? no     | yes       | 200, 201? no            | 1      | 1    |
| 1   | 0? no       | yes       | 1, 2, 3, 4, 5? no       | 4      | 4    |
| 3   | 2? yes      | no        | (skip)                  | -      | 4    |
| 2   | 1? yes      | no        | (skip)                  | -      | 4    |

Output: `4` (the chain `1, 2, 3, 4`).

Notice only the three starts (100, 200, 1) did any chain-walking; 4, 3, 2 were skipped because
each has a predecessor in the set.

### Checkpoint B -- Trace and edge cases

**Q1 (apply).** Set = `{1, 2, 3, 4, 100, 200}`. Which values trigger chain-walking, and what is `best`?
- a) Every value walks; best = 4
- b) Only 1, 100, 200 walk (their predecessors are absent); best = 4
- c) Only 4 walks; best = 4

<details><summary>Show answer</summary>

**(b)** -- 1, 100, 200 have no predecessor in the set, so each starts a chain. 1's chain runs 1,2,3,4 (length 4); the others are length 1. `best = 4`.

</details>

**Q2 (analyze).** What does `longestConsecutive([])` return, and which line makes it work?
- a) Throws; the set is empty
- b) `0` -- the set is empty so the loop never runs and `best` stays at its initial 0
- c) `1`

<details><summary>Show answer</summary>

**(b)** -- an empty array yields an empty set, the for-each body executes zero times, and `best` initialized to 0 is returned. No special case needed.

</details>

**Q3 (transfer).** If the O(n) constraint were dropped, what simpler approach would you use, and what would it cost?

<details><summary>Show answer</summary>

Sort the array, then one pass counting adjacent differences of exactly 1. Simpler to write, but O(n log n) due to the sort -- which is exactly why this problem forbids it.

</details>

## Common mistakes

- Starting a chain from *every* element and walking up. It returns the right answer but is O(n^2)
  in the worst case (e.g. `nums = [1,2,3,...,n]`); the start-guard is what keeps it O(n).
- Sorting first. Correct and simpler to reason about, but O(n log n), violating the constraint.
- Re-counting duplicates by iterating over `nums` instead of the set. Slower and redundant.
- Using `set.contains(next)` inside `while` but forgetting to advance `next` -- infinite loop.
- Returning `best + 1` or initializing `length` to 0. The start value itself already counts as 1,
  so the length must begin at 1.

## Related problems

- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the same "load everything into a set,
  then query" shape.
- [0347 - Top K Frequent Elements](../0347-top-k-frequent/) - the other O(n) standout in this
  section that refuses the obvious sort.
- [0001 - Two Sum](../0001-two-sum/) - existence checks against a set, this time chaining upward.
