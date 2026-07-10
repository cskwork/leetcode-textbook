# Pattern 3 - Sliding Window

## What it is

A sliding window is a frame over a contiguous slice of an array or string. Instead of
recomputing the answer for every possible slice from scratch (O(n^2) or worse), you
**slide** the frame one element at a time: when the frame moves right you add one new
element; when the frame moves left you remove one old element. Each element enters the
window once and leaves once, so the whole scan costs O(n).

The window is the range between two pointers named `left` and `right`. A small piece of
**state** (a running sum, a count, a frequency map, a hash set) tracks whatever property
of the window you care about, so that "is the window valid?" is an O(1) check.

## When it applies -- trigger signals

Reach for sliding window when the problem asks for one of these:

- **"Longest / shortest subarray or substring with property X"** -- e.g. longest
  substring with no repeating characters, shortest subarray whose sum >= target.
- **"Maximum sum of a size-k window"** -- a fixed-width window slides across the data.
- **"At most K distinct / at most K replacements"** -- the window grows while a budget
  lasts and shrinks when the budget breaks.
- The input is an array or string and the answer is a **contiguous** chunk of it.

If the answer requires non-contiguous elements (subsequence, subset), sliding window is
the wrong tool -- look at Two Pointers, DP, or Backtracking instead.

## Two flavors: fixed-size vs variable-size

### Fixed-size window

The width is given (e.g. "size k"). Slide it end-to-end, update the running state on
each slide, record the best.

```
window-size = k
initialize state for the first k elements
for right from k to n-1:
    add element at right to state
    remove element at right-k from state
    update best answer from state
```

### Variable-size window (the common case)

The width is not known in advance; the window grows and shrinks to satisfy a
constraint. The recipe below is the single most important block in this pattern --
memorise it. Every Medium/Hard problem in this section is just this template with a
more elaborate `state` and `violates constraint` check.

```
function variable-window(input, constraint):
    left = 0
    state = empty            # sum, count, frequency map, etc.
    best = identity          # longest -> 0, shortest -> +infinity
    for right from 0 to len(input)-1:        # EXPAND
        add input[right] to state
        while state violates constraint:     # SHRINK until valid again
            remove input[left] from state
            left = left + 1
        update best using the current window [left..right]
    return best
```

Two crucial details:

1. **Expand first, then shrink.** The `right` pointer only moves forward; the `while`
   loop pulls `left` forward as far as needed. Never move `right` backward.
2. **Update `best` AFTER the while-loop** so you only ever measure a *valid* window.

## The 6 problems in this book

| Folder | LC | Problem | Difficulty | Teaches |
|---|---|---|---|---|
| [0121-best-time-to-buy-and-sell-stock](./0121-best-time-to-buy-and-sell-stock/) | 121 | Best Time to Buy and Sell Stock | Easy | The simplest "running best" idea: one pass, track the minimum seen so far. |
| [0003-longest-substring-without-repeating-characters](./0003-longest-substring-without-repeating-characters/) | 3 | Longest Substring Without Repeating Characters | Medium | Variable window with a hash map of last-seen indices. |
| [0424-longest-repeating-character-replacement](./0424-longest-repeating-character-replacement/) | 424 | Longest Repeating Character Replacement | Medium | Window with a constraint on replacements; the int[26] frequency trick. |
| [0076-minimum-window-substring](./0076-minimum-window-substring/) | 76 | Minimum Window Substring | Hard | Capstone: frequency map + a `formed` counter. Everything combined. |
| [0209-minimum-size-subarray-sum](./0209-minimum-size-subarray-sum/) | 209 | Minimum Size Subarray Sum | Medium | Classic variable window: grow right to add, shrink left to remove. |
| [0219-contains-duplicate-ii](./0219-contains-duplicate-ii/) | 219 | Contains Duplicate II | Easy | Fixed-distance window via a hash set that always holds the last k indices. |

The first and last are warm-ups; 0209 and 0003 drill the expand/shrink rhythm; 0424 and
0076 layer on richer state. By 0076 the template is muscle memory.

## Common pitfalls

- **Forgetting to update `state` when shrinking.** Every element removed from the window
  must decrement the sum, decrement its frequency count, or be removed from the set. A
  window whose state is out of sync gives silently wrong answers.
- **Off-by-one on the window size.** Decide once whether your window is `[left, right)`
  or `[left..right]` inclusive and compute the size accordingly (`right - left` vs
  `right - left + 1`). The wrong formula is the #1 source of "answer off by one".
- **Measuring `best` inside the shrink loop.** Always measure *after* the window is valid
  again, or you will record windows that violate the constraint.
- **Comparing the wrong way.** For "longest" initialise `best = 0` and use `max`; for
  "shortest" initialise `best = +infinity` and use `min`. A surprising number of bugs are
  just this.
- **Sorting the input.** Sliding window relies on the original sequence; sorting destroys
  "contiguous" semantics. If you find yourself wanting to sort, you probably want Two
  Pointers, not Sliding Window.
- **Confusing "at most K" with "exactly K".** "At most K distinct" grows freely under a
  budget; "exactly K" usually needs two windows (at-most-K minus at-most-(K-1)).

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In the variable-size window template, which statement about the two pointers is always true?

- a) Both `left` and `right` only ever move forward
- b) `left` moves forward but `right` can jump backward
- c) `left` can move backward to re-add dropped elements

<details><summary>Show answer</summary>

**(a)** -- `right` expands one step at a time, and the `while` loop only pulls `left` forward. Moving either pointer backward breaks the "each element enters and leaves once" guarantee that makes the whole scan O(n).

</details>

**Q2 (pattern recognition).** New problem: "longest substring containing **at most K distinct characters**." Which variant of this pattern fits?
- a) A fixed-size window of width K
- b) A variable window with a frequency map and a distinct-count; expand `right`, shrink `left` while distinct > K, then record the length
- c) Sort the string, then use two pointers

<details><summary>Show answer</summary>

**(b)** -- the constraint is on a count (distinct letters), so a frequency map plus a distinct counter drives the shrink loop. It is the 0424 shape with "at most K distinct" replacing "at most K replacements". Sorting is forbidden -- it destroys contiguity.

</details>

**Q3 (pattern recognition).** New problem: "is any value in the array repeated within `k` indices of an earlier equal value?" Which tool is the most direct?
- a) A hash set holding the last `k` values, evicting the one that falls out of range
- b) Nested loops comparing every pair
- c) A running sum of the values

<details><summary>Show answer</summary>

**(a)** -- this is a fixed-distance window: the set answers "is this value nearby?" in O(1), and eviction keeps the look-back range exactly `k`. It is the 0219 reflex.

</details>

**Q4 (apply).** Run the 0209 algorithm (shortest subarray, sum >= target) on `target = 6, nums = [1, 2, 3, 4]`. What is returned?
- a) 2
- b) 3
- c) 4

<details><summary>Show answer</summary>

**(a)** -- the running sums reach 6 at index 2 (window [1,2,3], length 3), and after shrinking at index 3 the tightest valid window is [3,4] of length 2.

</details>

**Q5 (design).** In words, not code, sketch how to solve "longest substring with at most K distinct characters" using this pattern's template.

<details><summary>Show answer</summary>

Keep a frequency map (char -> count) and a `distinct` counter. Expand `right`: add the char, bumping its count; if it was previously 0, increment `distinct`. While `distinct > K`, remove the char at `left` (decrement its count, and `distinct` when a count hits 0), then advance `left`. After the shrink, update `best = max(best, right - left + 1)`. Same expand/shrink/measure skeleton as 0424, with a different validity test.

</details>

---

Next: start with [0121-best-time-to-buy-and-sell-stock](./0121-best-time-to-buy-and-sell-stock/).
