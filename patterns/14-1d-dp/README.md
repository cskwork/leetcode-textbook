# Pattern 14 - 1-D Dynamic Programming

## What the pattern is

**Dynamic Programming (DP)** is the technique of solving a problem by
breaking it into overlapping subproblems and combining their answers,
**caching each subproblem's answer so it is solved only once**.

Two ingredients must be present for DP to apply:

1. **Overlapping subproblems** — the same smaller question is asked many
   times. Climbing stair 5 requires stair 4 and stair 3; both of those
   require stair 2. A naive recursion recomputes stair 2 twice, stair 1
   three times, and so on — exponential blow-up.
2. **Optimal substructure** — the optimal answer for the big problem can
   be built from the optimal answers of its subproblems. If the best way
   to rob houses 1..n is known, the best way to rob houses 1..n+1 only
   depends on those, not on any choice we abandoned earlier.

When both hold, the recipe is: define a small lookup table `dp[]`,
fill it from smallest subproblem to largest, and return the entry that
answers the original question.

In *1-D* DP the state has a single index — `dp[i]` is the answer for
"the first i elements" or "the position i". (Two indices would be
Pattern 15, 2-D DP.)

## When it applies (trigger signals)

| Signal | Example phrasing |
|---|---|
| **"How many ways"** | "number of ways to climb n stairs", "number of decodings" |
| **"Minimum / maximum to reach"** | "minimum cost to reach the top", "minimum coins to make amount" |
| **"Can you reach / partition"** | "can you break the string into dictionary words" |
| **"Longest subsequence with property X"** | "longest strictly increasing subsequence" |
| **Recurrence on one index** | "answer for n depends on answer for n-1 and n-2" |

The tell-tale sign: a brute-force recursion exists, it would recompute
the same subproblem repeatedly, and the answer for `i` only needs
answers for indices *smaller* than `i`. If you can write a recurrence
on one index, you are in 1-D DP territory.

## The 5-step DP recipe

Every problem in this section is solved with the same five moves. Memorise them:

1. **Define the state.** Decide what `dp[i]` *means* in plain English.
   This is the most important step — a wrong definition dooms everything
   after it. For Climbing Stairs, `dp[i]` = "number of distinct ways to
   reach step i". For House Robber, `dp[i]` = "maximum money robbed from
   the first i houses". For Coin Change, `dp[a]` = "fewest coins to make
   amount a".
2. **Identify the recurrence.** Express `dp[i]` in terms of earlier
   entries. Ask: "what was the *last choice* made to arrive at i?" Each
   legal last choice contributes one term; combine with `max` / `min` /
   `+` as the problem demands.
3. **Write the base cases.** The smallest subproblems have no
   predecessors. Get them exactly right — a wrong base case propagates
   through every later entry. (`dp[0]` is usually 0, 1, or empty; the
   first real element is often a special case.)
4. **Choose the iteration order.** Compute entries in an order that
   guarantees every dependency is already known. For 1-D DP that is
   almost always left-to-right (`i` from 1 to `n`). For problems with a
   "last choice" over a set (Coin Change, Word Break) the *outer* loop
   is the index and the *inner* loop scans the choices.
5. **Return the answer.** It is usually `dp[n]`, but read the problem:
   sometimes the answer lives at `dp[n-1]`, sometimes at the max over
   all entries (Longest Palindromic Substring), sometimes at `dp[0]`
   when you build right-to-left.

## A general pseudocode template

```text
function solve(input of size n):
    create dp array of size n+1, filled with a neutral value (0, +inf, ...)
    set the base cases: dp[0] = <base>, dp[1] = <base>, ...
    for i from <first computed index> to n:
        dp[i] = recurrence applied to dp[i-1], dp[i-2], ... (or a scan over choices)
    return dp[n]   (or whatever index the problem asks for)
```

Every problem below is a concrete instance of this template. When you
read each one, identify the five steps explicitly.

## Top-down (memoization) vs bottom-up (tabulation)

There are two ways to fill the `dp[]` table, and both are correct:

| | Top-down (memoization) | Bottom-up (tabulation) |
|---|---|---|
| **Direction** | Start from the big problem, recurse into subproblems. | Start from the base cases, iterate up to the answer. |
| **Storage** | A hash map or array, filled lazily on demand. | An array, filled in a fixed loop order. |
| **Time** | Same asymptotic O(…) as bottom-up. | Same. |
| **Space** | O(n) table + O(n) recursion stack. | O(n) table, no recursion stack. |
| **Wins** | Mirrors the recurrence naturally; only computes states you actually need. | No recursion overhead; easier to space-optimise; no stack-overflow risk. |
| **When to prefer** | The recurrence is clearer as recursion; many states are never reached. | The iteration order is obvious; you want the tightest constant factor. |

In this book we use **bottom-up** for every problem because the
iteration order is explicit and the dry-runs are easy to follow. On a
real interview, write whichever form you find less error-prone; just
*pick one* and commit.

## Space optimisation (rolling variables)

When the recurrence for `dp[i]` only looks back a fixed, small number
of steps (typically `dp[i-1]` and `dp[i-2]`), you do not need the
whole array — a couple of variables are enough.

```text
prev2 = base case for i-2
prev1 = base case for i-1
for i from 2 to n:
    current = recurrence(prev1, prev2)
    prev2 = prev1
    prev1 = current
return prev1
```

This drops the space from O(n) to O(1) without changing the time. We
do this in Climbing Stairs, Min Cost Climbing Stairs, and House
Robber. For problems whose recurrence scans an arbitrary set
(Coin Change, Word Break) or stores per-element maxima (LIS), the full
array is needed.

## Problems in this section

| # | LC | Problem | Difficulty | One-line teaser |
|---|----|---------|-----------|-----------------|
| 79 | 70 | [Climbing Stairs](./0070-climbing-stairs/) | Easy | The Fibonacci gateway: `dp[i] = dp[i-1] + dp[i-2]`. |
| 80 | 746 | [Min Cost Climbing Stairs](./0746-min-cost-climbing-stairs/) | Easy | Same shape as Climbing Stairs, but you *pay* the step you stand on and minimise. |
| 81 | 198 | [House Robber](./0198-house-robber/) | Medium | At each house: rob it (and skip the previous) or skip it. `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`. |
| 82 | 213 | [House Robber II](./0213-house-robber-ii/) | Medium | Circular street — never rob both first and last, so run House Robber I on two linear slices. |
| 83 | 322 | [Coin Change](./0322-coin-change/) | Medium | `dp[a] = min over coins c of dp[a-c] + 1`; the classic "min coins to make amount". |
| 84 | 300 | [Longest Increasing Subsequence](./0300-longest-increasing-subsequence/) | Medium | `dp[i]` = LIS ending at i; scan all earlier indices for a smaller value. (O(n log n) variant exists.) |
| 85 | 139 | [Word Break](./0139-word-break/) | Medium | `dp[i] = true` if any dictionary word matches the slice ending at i AND the prefix before it was breakable. |
| 86 | 5 | [Longest Palindromic Substring](./0005-longest-palindromic-substring/) | Medium | Expand around each centre — a 1-D-style scan with the practical O(1) space solution; DP form mentioned. |

Work them in that order. The first two teach the `dp[i-1] + dp[i-2]`
shape. House Robber introduces a *choice* (`max` over two terms).
House Robber II shows how to reduce a constrained problem to a simpler
one. Coin Change and Word Break show the "scan over a choice set"
flavour. LIS is the canonical O(n^2) DP. Longest Palindromic Substring
closes the section by showing that the *practical* solution to a
problem with a DP formulation is sometimes a different algorithm
entirely.

## Common pitfalls

- **Wrong `dp[i]` definition.** This is the most expensive mistake. If
  you define `dp[i]` as "max money from house i" instead of "max money
  from the first i houses", the recurrence you write will be subtly
  wrong. Always write the definition down in a comment before the
  recurrence.
- **Missing or wrong base cases.** `dp[0]` often means "the empty
  prefix" and must be a neutral value (0 cost, 1 way, true, …). For
 getting `dp[0]` wrong you get every later entry wrong. For strings,
  `dp[0]` usually represents the empty string.
- **Off-by-one on the iteration order / array size.** Decide once
  whether `dp` has size `n` or `n+1`, and whether index `i` means "up
  to and including element i" or "the first i elements". Mixing the
  two conventions in the same solution is the most common source of
  fence-post bugs.
- **Integer overflow on counts.** "How many ways" problems grow like
  Fibonacci — for `n = 45`, `dp[45]` is 1.8 billion, right at the edge
  of `int`. LeetCode usually constrains the answer to fit in `int`,
  but if the constraint is loose, switch to `long`.
- **Using recursion without memoisation.** A naive recursive Fibonacci
  is `O(2^n)`. If you go top-down you MUST cache results in a map or
  array keyed by the index. Otherwise the "DP" is just brute force.
- **Choosing the wrong loop nesting for "scan over choices" problems.**
  Coin Change and Word Break need the *index* on the outside and the
  *choice* (coin / word) on the inside. Swapping them changes the
  semantics and silently breaks the answer.
- **Forgetting the "impossible" sentinel.** Coin Change must return
  `-1` when no combination exists. Use `+infinity` (or `amount+1`) as
  the "unreachable" marker and convert it to `-1` at the return.
