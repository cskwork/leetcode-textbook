# Pattern 15 - 2-D Dynamic Programming

## What this pattern is

2-D DP extends 1-D DP: instead of a single index `dp[i]`, the state is a whole
**table** `dp[i][j]` indexed by two things at once -- typically two positions in
two strings, or a row and column in a grid. The answer for cell `(i, j)` is built
from already-computed neighbours: the cell **above** `dp[i-1][j]`, the cell to the
**left** `dp[i][j-1]`, and very often the **diagonal** `dp[i-1][j-1]`. You fill
the table row by row (or length by length) until the bottom-right cell holds the
final answer.

The hallmark: the problem's state needs **two** "pointers" to describe. The moment
you catch yourself saying *"the answer depends on where I am in X **and** where I
am in Y"*, you are in 2-D DP territory.

## When to apply it (trigger signals)

Scan the statement for any of these:

| Trigger signal                                              | Likely sub-pattern          |
|-------------------------------------------------------------|-----------------------------|
| "robot moves only right/down", "number of ways to reach"    | Grid-path DP                |
| "two strings", "longest common subsequence", "common to both" | LCS-style DP              |
| "convert word1 to word2", "edit distance", "insert/delete/replace" | Edit-distance DP       |
| "subset that sums to target", "0/1 knapsack", "partition into equal" | Knapsack DP (0/1)     |
| "number of combinations to make amount", "unlimited coins"  | Knapsack DP (unbounded)     |
| "is s[i..j] a palindrome", "count palindromic substrings"   | Interval DP (2-D on i, j)   |

## The dp[i][j] mental model

Three things define every 2-D DP. Decide them in this order and the code almost
writes itself.

1. **State.** What do `i` and `j` represent? Row/column of a grid; a prefix of
   string A / a prefix of string B; the start and end of a substring; an item
   index / a running sum. Write the definition as a sentence before any code.
2. **Recurrence.** How is `dp[i][j]` computed from its neighbours? Almost always a
   combination of `dp[i-1][j]` (above), `dp[i][j-1]` (left), and `dp[i-1][j-1]`
   (diagonal). This is the heart of the problem.
3. **Base case.** The top row `dp[0][*]` and the left column `dp[*][0]` are filled
   **first**, before the main loops, because they have no "above" or "left" to
   read from. Getting these wrong is the number-one source of bugs.

## General pseudocode template

    function solveTwoD(input):
        let m = number of rows, n = number of columns
        create a dp table of size (m+1) by (n+1)      # +1 reserves a "zero" row and column
        fill the base row dp[0][*] and base column dp[*][0]

        for i from 1 to m:
            for j from 1 to n:
                dp[i][j] = combine(
                    dp[i-1][j],    # answer without the current row's element
                    dp[i][j-1],    # answer without the current column's element
                    dp[i-1][j-1]   # answer without either
                )

        return dp[m][n]

The `(m+1) x (n+1)` sizing with a sentinel row/column of zeros is the single most
useful convention: it removes every special case for the first real row and
column, because they always have a valid "above" (the sentinel row) and "left"
(the sentinel column) to read. Edit Distance, LCS, and the knapsack family all
benefit.

For pure grid problems where cell `(0,0)` has its own value rather than
representing an "empty prefix", you size the table `m x n` and fill row 0 and
column 0 explicitly before the loops -- see Unique Paths and Minimum Path Sum.

## How to recognise a 2-D DP

Ask one question: *how many independent positions do I need to describe a
sub-problem?*

- One position (an index into one array, or a single running value) -- that is
  1-D DP (Pattern 14).
- Two positions (two strings walked in lock-step; a grid cell; a substring's
  start **and** end) -- that is 2-D DP.

If you can phrase the sub-problem as *"the answer for the first `i` of X **and**
the first `j` of Y"*, it is 2-D.

## The four classic sub-patterns

### A. Grid-path DP -- Unique Paths, Minimum Path Sum

State = a grid cell. Movement is restricted to right and down, so each cell's
answer combines only the cell **above** and the cell to the **left**. Base = the
top row and the left column, each of which has exactly one way to be reached.

### B. LCS-style DP -- Longest Common Subsequence

State = a prefix of string A paired with a prefix of string B. When the two
current characters **match**, extend the diagonal: `dp[i-1][j-1] + 1`. When they
do not, carry forward the best of the two neighbours: `max(dp[i-1][j],
dp[i][j-1])`. Base = a row and column of zeros (the empty prefix matches nothing).

### C. Edit-distance DP -- Edit Distance

State = prefixes of two strings. Three operations map to three neighbours:

- **Insert** a character into word1 to match word2's current char -> `dp[i][j-1] + 1`
  (we consumed a char of word2 but not word1).
- **Delete** a character from word1 -> `dp[i-1][j] + 1` (we consumed a char of
  word1 but not word2).
- **Replace** a character -> `dp[i-1][j-1] + 1` (we consumed one char of each).

When the two current characters already match, the cost is `dp[i-1][j-1]` -- the
operation is free. Base: row 0 = `j` (insert `j` chars into an empty string),
column 0 = `i` (delete `i` chars to reach an empty string).

### D. Knapsack DP -- Partition Equal Subset Sum, Coin Change II

State = (item index, remaining capacity). For each item you choose: include it or
skip it. The two flavours differ only in **loop direction**:

- **0/1 knapsack** (Partition Equal Subset Sum): each item is used at most once,
  so iterate the capacity loop **descending** -- this stops a just-included item
  from being picked again in the same pass.
- **Unbounded knapsack / combination count** (Coin Change II): each item is
  reusable, so iterate the capacity loop **ascending**. Crucially the **items**
  must be the **outer** loop and the **amount** the **inner** loop; swapping them
  counts *permutations* instead of *combinations* (a different problem, LC 377).

## The 7 problems in this pattern

Read them in this order -- grid first (simplest recurrence), then two-string DP,
then interval DP, then the two knapsack variants.

| #    | Problem                                              | Difficulty | Teaser                                                                  |
|-----:|------------------------------------------------------|------------|-------------------------------------------------------------------------|
| 0062 | [Unique Paths](./0062-unique-paths/)                 | Medium     | The simplest grid DP: `dp[i][j] = dp[i-1][j] + dp[i][j-1]`.             |
| 0064 | [Minimum Path Sum](./0064-minimum-path-sum/)         | Medium     | Grid DP with cell costs: add the cheaper of above / left.               |
| 1143 | [Longest Common Subsequence](./1143-longest-common-subsequence/) | Medium | Textbook LCS: diagonal `+1` on a match, else max of the two neighbours. |
| 0072 | [Edit Distance](./0072-edit-distance/)               | Medium     | The classic: insert / delete / replace map to three neighbours.         |
| 0647 | [Palindromic Substrings](./0647-palindromic-substrings/) | Medium | Interval DP: `dp[i][j]` depends on `dp[i+1][j-1]` -- fill by length.    |
| 0518 | [Coin Change II](./0518-coin-change-ii/)             | Medium     | Combination count: loop order decides combinations vs permutations.     |
| 0416 | [Partition Equal Subset Sum](./0416-partition-equal-subset-sum/) | Medium | 0/1 knapsack in disguise: can a subset hit `sum / 2`?                   |

## Common pitfalls

- **Wrong base on row 0 / column 0.** The first real row and column have no
  "above" or "left" neighbour, so they must be filled **before** the main loops.
  Forgetting this reads uninitialized cells and produces garbage. Under the
  `(m+1) x (n+1)` convention the sentinel row and column are zeros; under the
  grid convention they are running sums (Minimum Path Sum) or all-1s (Unique
  Paths).
- **Off-by-one on the dp dimensions.** A `(len1+1) x (len2+1)` table uses indices
  `0..len1` and `0..len2`, so the recurrence reads `text.charAt(i-1)` and
  `text.charAt(j-1)`. Mixing up `i` with `i-1` when indexing the original string
  is the single most common bug in this pattern.
- **Swapping `i` and `j`.** By convention `i` indexes rows (outer loop) and `j`
  indexes columns (inner loop). Writing `dp[j][i]` or `text2.charAt(i-1)`
  compiles and runs but yields wrong answers. Pick a convention and keep it for
  the whole solution.
- **Knapsack loop order.** In the 1-D (space-optimised) knapsack the inner loop's
  direction decides correctness: **descending** for 0/1 (each item once),
  **ascending** for unbounded (items reusable). And for combination-count
  (Coin Change II) the **items** must be the outer loop -- swapping the loops
  counts permutations instead.
- **Wrong fill order for interval DP.** Problems whose recurrence reads
  `dp[i+1][j-1]` (Palindromic Substrings) cannot be filled top-down row by row,
  because `i+1` sits **below** `i`. Either fill by substring length, or iterate
  `i` **descending** and `j` **ascending**.
- **Clobbering the diagonal when space-optimising.** When you collapse a 2-D
  table into one or two rows, the diagonal value `dp[i-1][j-1]` is overwritten
  before it is read in the next column. Carry a `prev` variable that snapshots
  the old `dp[j-1]` before the update.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what makes a problem 2-D DP rather than 1-D DP?

<details><summary>Show answer</summary>

The state needs two independent indices to describe a sub-problem -- e.g. a position in string A AND a position in string B, or a grid row AND column -- so the table is `dp[i][j]` instead of a single `dp[i]`.

</details>

**Q2 (pattern recognition).** A new problem: "a robot walks a grid of coins moving only right/down and wants to MAXIMISE the coins collected." Which sub-pattern fits?
- a) Grid-path DP (like Minimum Path Sum, but `max` instead of `min`)
- b) Interval DP
- c) Edit distance

<details><summary>Show answer</summary>

**(a)** -- same restricted moves (right/down) mean each cell depends only on the cell above and to the left; swapping `min` for `max` turns it into a maximum-cost grid path.

</details>

**Q3 (pattern recognition).** A new problem: "choose items, each usable at most once, so their weights sum exactly to W." Which sub-pattern and, in 1-D form, which inner-loop direction?
- a) 0/1 knapsack, inner loop DESCENDING (Partition Equal Subset Sum style)
- b) Unbounded knapsack, inner loop ASCENDING
- c) LCS-style, diagonal dependency

<details><summary>Show answer</summary>

**(a)** -- "at most once" is 0/1 knapsack; descending prevents an item from being reused within the same pass. (b) is the unbounded (Coin Change II) flavour.

</details>

**Q4 (apply).** For Edit Distance with `word1 = "ab"`, `word2 = "ab"`, what is `dp[2][2]`?
- a) 0
- b) 1
- c) 2

<details><summary>Show answer</summary>

**(a)** -- the strings are identical: both `a` and `b` are matches, so the cost carries the diagonal for free all the way through; `dp[2][2] = dp[1][1] = dp[0][0] = 0`.

</details>

**Q5 (design).** Sketch (in words, not code) a 2-D DP for "count the number of subsequences of string `s` that equal string `t`" -- how would you define `dp[i][j]` and the recurrence?

<details><summary>Show answer</summary>

Let `dp[i][j]` = number of ways the first `j` chars of `t` appear as a subsequence within the first `i` chars of `s`. If `s[i-1] == t[j-1]`, then `dp[i][j] = dp[i-1][j-1]` (match these two) `+ dp[i-1][j]` (skip `s`'s char); otherwise `dp[i][j] = dp[i-1][j]` (must skip). Base: `dp[i][0] = 1` (empty `t` matches once), `dp[0][j>0] = 0`.

</details>

---

Next problem: [0062 - Unique Paths](./0062-unique-paths/).
