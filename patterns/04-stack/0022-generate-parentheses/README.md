# 0022 - Generate Parentheses

**Difficulty:** Medium
**Pattern:** Stack (bridge to Backtracking)
**LeetCode:** https://leetcode.com/problems/generate-parentheses/

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

A well-formed string of `n` pairs is built one character at a time under two
rules, and those rules are the *logical* equivalent of a stack of unmatched
openers:

- You may add `'('` as long as you haven't used all `n` of them.
- You may add `')'` only when there is an **unmatched** `'('` to close — i.e. when
  the number of `')'` used so far is less than the number of `'('` used so far.

The difference `(open - close)` is exactly "how many openers are currently
unmatched" — a virtual stack depth. When that depth is positive you may close one;
when it is zero you may not. This turns "generate all valid strings" into a
decision tree we explore with **backtracking**: try `'('`, recurse, undo; then try
`')'` if allowed, recurse, undo.

This problem is the deliberate **bridge to Pattern 10 (Backtracking)** — same
"make a choice, recurse, undo the choice" shape — but its validity rule is the
stack discipline from this pattern.

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
