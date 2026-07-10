# 0022 - Generate Parentheses

**Difficulty:** Medium
**Pattern:** Stack (bridge to Backtracking)
**LeetCode:** https://leetcode.com/problems/generate-parentheses/

## Concepts used

- **Recursion** — a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Backtracking** — a recursive search that makes a choice, explores, then undoes the choice to try the next one. [glossary](../../../docs/10-glossary.md#backtracking)

## Problem

Given `n` pairs of parentheses, write a function to generate **all combinations
of well-formed (valid) parentheses**.

Signature:

    List<String> generateParenthesis(int n)

Example (verbatim from LeetCode):

    Input:  n = 3
    Output: ["((()))","(()())","(())()","()(())","()()()"]
    (any permutation of these five is accepted)

    Input:  n = 1
    Output: ["()"]

## Intuition

This problem *generates* strings instead of checking them. The validity rule is
the same one from [0020 Valid Parentheses](../0020-valid-parentheses/) (where a
stack checks that each closer matches the most recent opener), but here we never
break the rule while building — so every string we produce is already valid.

Picture filling a row of `2n` blank slots with `(` or `)`, one slot at a time,
never letting the partial string become invalid. Two rules govern what you may
write next:

- You may write `(` as long as you haven't used all `n` of them.
- You may write `)` only if there is an unmatched `(` to pair it with — that is,
  only when more `(` than `)` have been written so far.

Think of `open - close` as "how many openers are currently waiting for a
partner" — a virtual stack depth. When it is zero you've closed everything and
cannot add `)` (there's nothing to match); when it is positive you may close one.

Trace the smallest case, `n = 1`: one pair. Start at `""` with open=0, close=0.

- open < 1, so write `(` → `"("`, open=1, close=0.
- now close < open (0 < 1), so write `)` → `"()"`, open=1, close=1.
- length is 2 = 2n → record `"()"`.

That is the only valid string, so the answer is `["()"]`.

To build every valid string we explore every legal choice with
[backtracking](../../../docs/10-glossary.md#backtracking) — a
[recursive](../../../docs/10-glossary.md#recursion) search that, at each slot,
tries writing `(` (if allowed), explores everything that can follow, then
**erases** it and tries writing `)` (if allowed) instead. The erase is what lets
one builder explore many strings. When the string reaches length `2n`, the two
rules have kept it valid all along, so we save a copy. (This "make a choice,
recurse, undo the choice" skeleton is the same one you'll meet again in Pattern
10, Backtracking — Subsets, Permutations — later in the book.)

### Checkpoint A -- When may you close?

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** In the backtracking rules, when are you allowed to write a `)`?
- a) Whenever you have not yet written all `n` closers
- b) Only when `close < open` (some opener is still waiting for a partner)
- c) Only as the very last character

<details><summary>Show answer</summary>

**(b)** -- a `)` is legal only while more `(` than `)` have been placed, i.e. an opener is unmatched. This single guard keeps every partial string valid.

</details>

**Q2 (comprehend).** Start at `""` with `open=0`, `close=0`. Why is writing `)` as the first character forbidden?
- a) Because `close < open` is `0 < 0`, which is false -- no opener exists to match it
- b) Because the string would exceed length `2n`
- c) Because the recursion cannot start from `""`

<details><summary>Show answer</summary>

**(a)** -- with nothing on the "virtual stack" of openers, a leading `)` has no partner; the guard correctly blocks it.

</details>

## Pseudocode

    function generateParenthesis(n):
        results = empty list
        backtrack(current = empty string, open = 0, close = 0, n)
        return results

    function backtrack(current, open, close, n):
        if length(current) == 2 * n:
            add a copy of current to results
            return

        if open < n:                          # we can still place an opener
            append '(' to current
            backtrack(current, open + 1, close, n)
            remove the last char of current    # undo

        if close < open:                      # there is an unmatched opener to close
            append ')' to current
            backtrack(current, open, close + 1, n)
            remove the last char of current    # undo

## Java Solution

```java
import java.util.*;

class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> results = new ArrayList<>();
        backtrack(results, new StringBuilder(), 0, 0, n);
        return results;
    }

    private void backtrack(List<String> results, StringBuilder current,
                           int open, int close, int n) {
        if (current.length() == 2 * n) {
            results.add(current.toString());
            return;
        }
        if (open < n) {
            current.append('(');
            backtrack(results, current, open + 1, close, n);
            current.deleteCharAt(current.length() - 1);
        }
        if (close < open) {
            current.append(')');
            backtrack(results, current, open, close + 1, n);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
```

The state is just three counters — the builder, how many `'('` are placed
(`open`), and how many `')'` are placed (`close`). The two `if`s encode the
validity rules directly: place `'('` while any remain, place `')'` only when
something is unmatched (`close < open`). We mutate one `StringBuilder` and *undo*
each append with `deleteCharAt` instead of allocating a new string at every node —
that is the essence of backtracking and keeps the space cost to the recursion
depth. The base case fires when the string reaches length `2 * n` (all pairs
placed), at which point the two counter rules guarantee it is well-formed, so we
snapshot it into the result.

## Complexity

    Time:  O(4^n / sqrt(n))   -- the count of valid strings is the nth Catalan number,
                                 and each is built in O(n) time; this is the optimal
                                 output-sensitive bound.
    Space: O(n) for the recursion + StringBuilder depth (not counting the output list).
           The output list itself holds Catalan(n) strings of length 2n.

## Dry-Run

Backtracking tree on `n = 2` (expected output `{ "(())", "()()" }`). We write
state as `current | open, close`.

```
root:  "" | 0,0
├─ open<2: "(" | 1,0
│   ├─ open<2: "((" | 2,0
│   │   └─ close<open: "(()" | 2,1
│   │       └─ close<open: "(())" | 2,2   -> length 4, record "(())"
│   └─ close<open: "()" | 1,1
│       └─ open<2: "()(" | 2,1
│           └─ close<open: "()()" | 2,2   -> length 4, record "()()"
```

Only two complete strings of length 4 are produced: `"(())"` and `"()()"`. Every
leaf is automatically valid because the rules never allowed an extra `')'` with
no opener to close.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** For `n = 3`, the leftmost branch of the recursion (trying `(` before `)` at every node) produces which complete string first?
- a) `"()()()"`
- b) `"((()))"`
- c) `"(()())"`

<details><summary>Show answer</summary>

**(b)** -- the `open < n` branch runs first at each node, so three `(` are placed in a row, then three `)` close them: `((()))`.

</details>

**Q2 (analyze).** What goes wrong if you delete both `deleteCharAt` lines (the undo steps)?
- a) Nothing -- the output is identical
- b) The shared `StringBuilder` keeps growing across sibling branches, so later strings contain leftovers of earlier ones
- c) It throws immediately

<details><summary>Show answer</summary>

**(b)** -- the undo is what lets one builder explore many strings; without it, characters from one branch leak into its siblings and corrupt the result.

</details>

**Q3 (transfer).** How would you change the approach to COUNT the valid strings instead of listing them?

<details><summary>Show answer</summary>

Replace `results.add(...)` at the base case with a counter increment, and return the counter at the end. You never build a string; the same `open`/`close` guards guarantee only valid paths reach the base case. (The count is the nth Catalan number.)

</details>

## Common mistakes

- Letting `close` exceed `open`. That adds a `')'` with nothing to match — invalid
  string. The guard `close < open` is the entire correctness condition.
- Forgetting to undo the append. Without `deleteCharAt` (or without passing a
  fresh copy), the builder keeps growing across sibling branches and corrupts the
  output.
- Stopping only when `open == n`. That leaves unmatched openers; the real base
  case is `length == 2 * n` (equivalently `open == close == n`).
- Returning early/pruning incorrectly. There is no obvious bound to prune here
  beyond the two `if` rules; over-pruning drops valid answers.

## Related problems

- [0020 - Valid Parentheses](../0020-valid-parentheses/) — checks validity instead
  of generating; the open/close rules here are the same matching condition.
- [0155 - Min Stack](../0155-min-stack/) — another place where "current depth"
  (stack size) governs what operations are allowed.
- (Bridge, later in the book:) Pattern 10 problems — Subsets (LC 78), Permutations
  (LC 46) — share this exact make-a-choice / recurse / undo skeleton.
