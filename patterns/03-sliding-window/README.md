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

---

Next: start with [0121-best-time-to-buy-and-sell-stock](./0121-best-time-to-buy-and-sell-stock/).
