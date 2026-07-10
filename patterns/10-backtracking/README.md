# Pattern 10 - Backtracking

## What this pattern is

Backtracking is **exhaustive recursion with an undo button**. You are asked to
*generate every* valid arrangement -- every subset, every permutation, every
combination that sums to a target, every word spelled out on a grid -- and the
search space is a tree of decisions. At each node you **choose** one option,
**explore** the subtree that choice opens, then **un-choose** it before trying
the next option. That third step is what gives the pattern its name: you
back-track to the state you were in, so the next sibling branch starts from an
identical, clean slate.

The whole pattern fits on three lines:

    function backtrack(path, choices):
        if path is a complete answer: record a copy; return
        for each choice c in choices:
            add c to path                 # CHOOSE
            backtrack(path, next choices) # EXPLORE
            remove c from path            # UN-CHOOSE

Every problem in this folder is one of three things: (a) what counts as a
"choice", (b) what "next choices" looks like, or (c) when the path is "done".
Once you can answer those three questions for a problem, the rest is mechanical.

Backtracking is sometimes called "DFS over the decision tree". The two are the
same idea viewed from different angles: DFS walks a graph one branch at a time,
backtracking walks a *choice tree* one branch at a time and undoes the choice on
the way back up.

## When to apply it (trigger signals)

| Trigger signal                                            | Likely variant                  |
|-----------------------------------------------------------|---------------------------------|
| "all subsets / power set"                                 | Decision-at-each-index          |
| "all combinations that sum to target"                      | Start-index recursion           |
| "all permutations"                                        | Swap technique or `used[]`      |
| "all permutations/combinations with duplicates in input"  | Sort + skip-equal-neighbour     |
| "all arrangements / letter combinations / phone digits"   | Cartesian-product backtrack     |
| "find a word in a grid", "is the path possible"           | Grid backtrack, mark in-place   |
| "all valid placements" (N-Queens, Sudoku)                 | Constraint-pruned backtrack     |

The dead-giveaway verb is **"all"** or **"every"** followed by a noun like
*permutation, combination, subset, arrangement, partition, way*. If you have to
*list every* answer (not just count it), reach for backtracking first.

## The "choose / explore / un-choose" mental model

Beginners freeze on backtracking because they try to track what every recursive
call does to the shared `path`. Use this contract instead:

> **One path is mutated in place.** Each recursive frame adds exactly one
> element on the way down and removes that same element on the way back up.
> When control returns to a frame, `path` is *byte-for-byte identical* to what
> it was before that frame made its choice. So you never need to "pass a copy"
> or "reset between siblings" -- the un-choose step already did that for you.

Concrete example (Subsets, `nums = [1, 2]`):

- Frame 0 starts with `path = []`. It is about to try choice `1`.
- Frame 0 *chooses* `1`: `path = [1]`. It recurses into Frame 1.
- Frame 1 explores the whole subtree under `[1]`, then returns.
- Frame 0 *un-chooses* `1`: `path = []` again, byte-for-byte clean.
- Frame 0 now tries choice `2` from the exact same starting state.

The un-choose is not a courtesy; it is the correctness condition. Without it,
later siblings inherit earlier siblings' choices and the output is garbage.

## The general pseudocode template

This is the shape every solution in this folder compiles to:

    function solve(input):
        results = empty list of completed paths
        backtrack(starting path, starting choices, results)
        return results

    function backtrack(path, choices, results):
        if path satisfies the DONE condition:        # base case
            append a COPY of path to results         # snapshot, never the live ref
            return                                   # (sometimes optional, see below)

        for each c in choices:
            if c is pruned by a duplicate / bound rule: skip c
            add c to path                            # CHOOSE
            backtrack(path, restricted choices, results)   # EXPLORE
            remove the last element from path        # UN-CHOOSE

Three knobs turn this skeleton into any specific problem:

1. **DONE** -- when do we record? For Subsets, *every* node is a valid answer,
   so we record at the top of every call. For Permutations and Combination
   Sum, only *full-length* or *exactly-sums-to-target* paths qualify.
2. **CHOICES** -- the loop body. For combinations we iterate `start..n-1`; for
   permutations we iterate over elements not yet used; for Word Search we
   iterate over four directions.
3. **RESTRICTED CHOICES** -- how the loop shrinks for the child. Combinations
   pass `i + 1` as the new start (no reuse, order doesn't matter);
   Combination Sum passes `i` (unlimited reuse); Permutations passes a
   `used[]` mask (no reuse, order matters).

The optional `return` after recording matters: in problems where the path only
*grows*, recording at length `k` and then continuing to recurse is wasteful but
harmless. In problems like Combination Sum where the path's sum can keep
growing past the target, you **must** `return` the moment you hit the target
(or exceed it) to prune the search.

## Combinations vs permutations vs subsets -- the one table

This is the single most useful distinction in the pattern. Memorise it.

| Shape          | Order matters? | Reuse allowed? | "Done" check         | Restrict child choices to |
|----------------|----------------|----------------|----------------------|---------------------------|
| **Subset**     | no             | no             | every node           | index `i + 1` onward      |
| **Combination**| no             | no             | path hits a target   | index `i + 1` onward      |
| **Combination w/ reuse** | no    | **yes**        | path hits a target   | index `i` onward (same)   |
| **Permutation**| **yes**        | no             | path length == n     | any element not yet used  |
| **Permutation w/ reuse** | yes    | yes            | path length == n     | any element               |

Read the third column twice. "Order matters" is why permutations loop over
*every* index (with a `used[]` mask) instead of using a start index -- each
slot in the answer can hold any element. "Reuse" is why Combination Sum
recurses with `i` instead of `i + 1` -- the same candidate can be picked again.

A second, mechanical way to do Permutations (no `used[]`) is the **swap
technique**: swap element `i` into the "current" slot, recurse on `i + 1`, swap
back. It enumerates permutations in place but is harder to combine with
duplicate-skipping, so this book uses `used[]` for LC 46.

## Handling duplicates -- sort, then skip the equal neighbour

When the input has duplicates (LC 90 Subsets II), the naive backtrack produces
duplicate answers: `[1a, 2]` and `[1b, 2]` are the same subset but appear
twice. The fix is two steps and it must be **both**:

1. **Sort the input first.** This puts equal values side by side so a single
   neighbour check can detect them.
2. **Inside the loop, skip a candidate when it equals its predecessor and the
   predecessor was *not* picked at this level.**

The precise guard, in language-neutral form:

    sort input
    for i from start to n-1:
        if i > start and input[i] == input[i-1]: skip i      # skip the duplicate
        ... choose / explore / un-choose ...

Why "and `i > start`"? The first candidate at any recursion level (`i == start`)
is always taken -- it represents "include this value for the first time at this
position". Only the *subsequent* equal values at the same level are skipped,
because they would start a subtree the first one already covered. Skipping on
`i > start` preserves `[1, 1, 2]` -> `[1a, 1b, 2]` (both 1s *in the same path*,
which is legal) while killing the duplicate `[1b]` (a sibling of `[1a]`, which
is not).

## The 6 problems in this pattern

| #    | Problem                                                  | Difficulty | Teaser                                                        |
|-----:|----------------------------------------------------------|------------|---------------------------------------------------------------|
| 0078 | [Subsets](./0078-subsets/)                               | Medium     | Record every node of the decision tree.                       |
| 0039 | [Combination Sum](./0039-combination-sum/)               | Medium     | Subsets where the candidates may be reused.                   |
| 0046 | [Permutations](./0046-permutations/)                     | Medium     | Order matters, no reuse: track a `used[]` mask.               |
| 0090 | [Subsets II](./0090-subsets-ii/)                         | Medium     | Subsets with duplicate values: sort then skip-equal-neighbour.|
| 0079 | [Word Search](./0079-word-search/)                       | Medium     | Backtracking on a grid; mark the visited cell in place.       |
| 0017 | [Letter Combinations of a Phone Number](./0017-letter-combinations-of-a-phone-number/) | Medium | Cartesian product over digit->letters maps.        |

Read them in that order. Subsets is the canonical choose/explore/un-choose
skeleton; Combination Sum introduces the "reuse" knob; Permutations introduces
the "order matters" knob; Subsets II bolts on duplicate-skipping; Word Search
takes backtracking off a list and onto a grid; Letter Combinations shows the
cleanest possible case (fixed mapping, fixed length), which is the gentlest
introduction to cartesian-product backtracking.

## Common pitfalls

- **Forgetting to un-choose.** The single most common bug. If you `add` to a
  shared `path` before recursing, you must `remove` after. Miss it and the
  path leaks into sibling branches, producing answers that are too long or
  repeated. Symptom: output has the right *number* of entries but the entries
  themselves are wrong.
- **Recording the live reference instead of a copy.** `results.add(path)` adds
  a pointer to the *same* list you keep mutating; by the time the function
  returns, every entry in `results` points at the final empty path. Always
  snapshot: `results.add(new ArrayList<>(path))`. Same trap for `StringBuilder`
  -> call `.toString()` to freeze it.
- **Wrong start index for combinations vs permutations.** For combinations you
  must pass `i + 1` so each answer is built in non-decreasing index order and
  `[2,7]` is not produced alongside `[7,2]`. For permutations you must *not*
  use a start index at all -- loop over all indices, gated by `used[]`. Mix
  these up and you either get duplicates or miss answers.
- **Passing `i + 1` when reuse is allowed (LC 39).** The single character
  difference between Combination Sum (`i`) and Combination Sum II (`i + 1`)
  changes the answer completely. Read the reuse rule twice before writing the
  recursive call.
- **Not sorting before dedup (LC 90).** The skip-equal-neighbour trick
  *requires* equal values to be adjacent. Sort first, always.
- **Off-by-one on the base case.** "Done" is often `path.size() == k` or
  `sum == target`, not `== n` or `== target - 1`. Write the equality, then
  check it against the smallest example by hand.
- **Mutating shared state across frames (Word Search).** When you mark a cell
  visited by overwriting it (e.g. `board[r][c] = '#'`), you must restore the
  original character on the way back up. Forgetting the restore strands cells
  as permanently visited and the search misses valid paths.
- **Iterating the loop with the wrong bounds.** `for (int i = start; i < n; ...)`
  not `i <= n`. `start` is the parameter, not a constant `0` -- if you hardcode
  `0` in a combinations problem you generate every ordering of every subset.
- **Pruning too aggressively.** In Combination Sum you can prune when
  `sum > target`, but you cannot prune when `sum == target` and *also* continue
  to look for other answers -- record then `return`, do not stop the whole
  search.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what three steps does every branch of a backtracking solution perform?

<details><summary>Show answer</summary>

Choose (add the option to the path), explore (recurse into the subtree that choice opens), un-choose (remove the option so the next sibling starts from a clean state). The un-choose is the step beginners forget.

</details>

**Q2 (pattern recognition).** New problem: "list every way to choose exactly `k` numbers from `1..n` (order does not matter)." Which variant fits?
- a) Permutations-style: loop over all indices gated by a `used[]` mask
- b) Combinations-style: loop from a moving start index, pass `i + 1` to the child
- c) Cartesian-product: loop over a fixed letter map

<details><summary>Show answer</summary>

**(b)** -- order does not matter and there is no reuse, so the "look forward only" rule applies: a start index that advances to `i + 1`. A `used[]` mask is for when order matters (Permutations).

</details>

**Q3 (pattern recognition).** New problem: "list every word that can be typed by pressing a given sequence of phone digits." Which variant?
- a) Cartesian-product backtrack (one slot per digit, loop over that digit's letters)
- b) Subsets with duplicate-skipping
- c) Grid backtrack with in-place marking

<details><summary>Show answer</summary>

**(a)** -- each digit is an independent slot and the letters for one digit do not constrain another, so the recursion is a plain cartesian product -- the cleanest choose/explore/un-choose case.

</details>

**Q4 (apply).** You run the Subsets solution on `nums = [1, 2]`. How many subsets are recorded, and is the empty subset among them?
- a) 2, and `[]` is not recorded
- b) 4, and `[]` is recorded
- c) 4, and `[]` is not recorded

<details><summary>Show answer</summary>

**(b)** -- `2^2 = 4` subsets, and because recording happens at the top of every call before the loop, the very first call records `[]`.

</details>

**Q5 (design).** Sketch (in words, not code) how to solve Combination Sum if each candidate may be used at most once instead of unlimited times.

<details><summary>Show answer</summary>

Change one character: pass `i + 1` as the child's start (forbid reuse) instead of `i`. Keep the same done condition (`remaining == 0`), the sort-first step, the `break` when a candidate exceeds `remaining`, and the defensive snapshot. That single change turns LC 39 into LC 40.

</details>

---

Next problem: [0078 - Subsets](./0078-subsets/).
