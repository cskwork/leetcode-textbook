# Pattern 12 - Greedy

## What the pattern is

A **greedy** algorithm builds a solution one step at a time, and at each step
it commits to the choice that looks best *right now*, without ever revisiting
or undoing a previous choice. There is no backtracking, no "try every option
and compare" — just a single forward pass that keeps a small amount of state
(a running best, a farthest reach, a count).

The pattern earns its own section because an enormous family of "minimum
steps", "maximum number of X", "can you reach the end", and "is this
arrangement possible" problems become a 10-line loop once you identify the
right local rule. The hard part is not the code; it is **proving the local
rule is safe**.

## The core question

Before writing any greedy code, ask yourself:

> *Can I prove that the locally-best choice at this step is part of SOME
> optimal solution?*

- If **yes** → greedy is correct, and it will beat DP / backtracking by a
  mile (often O(n) or O(n log n) instead of O(n^2) or worse).
- If **no** (you can find a counterexample where committing early traps you
  in a suboptimal answer) → drop greedy and use DP, where every choice is
  kept open until the end.

Most "obvious" greedy rules fail this test. For example, "to make change,
always pick the largest coin that fits" works for US coins (1, 5, 10, 25)
but fails for denominations like `{1, 3, 4}` with target `6` (greedy picks
`4+1+1 = 3 coins`; optimal is `3+3 = 2 coins`). That problem (LC 322, Coin
Change) belongs to the 1-D DP chapter precisely because no local rule is
safe for arbitrary denominations.

## When it applies (trigger signals)

Reach for Greedy when the problem statement or input shows any of these:

| Signal | Example phrasing |
|---|---|
| **Max number of X you can do** | "maximum number of meetings you can attend", "assign cookies" |
| **Minimum steps / cost** | "minimum number of jumps to reach the end", "minimum refuels" |
| **Reachability** | "can you reach the last index", "gas station circuit" |
| **Subarray sum / max** | "maximum sum of a contiguous subarray" (Kadane) |
| **Locally-best choice works** | "form straights from a hand of cards", "container with most water" |
| **Sorting + one pass** | "merge intervals", "non-overlapping intervals" — see also Pattern 13 |

The tell-tale sign: the brute force is exponential (try every subset /
sequence), the DP formulation exists but feels heavy, and you suspect a
single pass with one rule might work. Confirm the rule with an exchange
argument or an invariant, then code it.

## A general pseudocode template

Almost every greedy solution in this section has this shape:

```text
function greedy(input):
    (optional) sort input by the right key
    initialise a running best (running sum, farthest reach, count, ...)
    for each element x in input:
        update the running best using x
        if a local choice must be made now:
            commit to it (do NOT keep alternatives)
        if the running state becomes invalid:
            reset it (or restart from x)
    return the accumulated answer
```

Two details matter more than the rest:

1. **The sort key.** If you sort, you must sort by the key that makes the
   greedy rule safe. Hand of Straights sorts ascending so each group starts
   at the smallest unused card; Gas Station does *not* sort (the circuit
   order is fixed); Merge Intervals (Pattern 13) sorts by start time.
2. **The running best.** This is the only state the algorithm remembers. In
   Kadane it is the "best sum ending here"; in Jump Game it is the
   "farthest index reachable so far"; in Gas Station it is the "tank". If
   you find yourself tracking more than two or three running values, you are
   probably sliding into DP territory.

## When greedy fails (contrast with DP)

A few classic counterexamples to keep you honest:

- **Coin Change (LC 322).** Greedy-by-largest-coin works for "canonical"
  coin systems (US, Euro) but fails on arbitrary denominations. DP is the
  general answer.
- **0/1 Knapsack (LC 416 variant).** Greedy by best value/weight ratio can
   pick an item that blocks a better combination. DP again.
- **Task scheduling with weights.** Most weighted scheduling problems need
  DP or a heap; only the unweighted "max count" version is cleanly greedy.

The diagnostic test: try to construct a small input where the obvious local
rule gives a worse answer than a different first move. If you can, greedy is
out (or you have the wrong rule).

## Problems in this section

| # | LC | Problem | Difficulty | One-line teaser |
|---|----|---------|-----------|-----------------|
| 73 | 53 | [Maximum Subarray](./0053-maximum-subarray/) | Medium | Kadane: a negative prefix never helps, so reset the running sum to 0. |
| 74 | 55 | [Jump Game](./0055-jump-game/) | Medium | Track the farthest reachable index; if you reach index n-1, win. |
| 75 | 45 | [Jump Game II](./0045-jump-game-ii/) | Medium | Greedy BFS-like level expansion: count jumps, not paths. |
| 76 | 134 | [Gas Station](./0134-gas-station/) | Medium | Total surplus >= 0 ⇒ a circuit exists; one-pass restart finds the start. |
| 77 | 846 | [Hand of Straights](./0846-hand-of-straights/) | Medium | Sort, count, then greedily consume each group from its smallest card. |
| 78 | 11 | [Container Greedy](./0011-container-greedy/) | Medium | Same two-pointer move, but the win is the exchange argument that the shorter line is safe to drop. |

Work them in that order. The first three warm you up on the "running
invariant" flavour of greedy. Gas Station is the classic "total + one-pass
restart" insight. Hand of Straights adds sorting and a frequency map.
Container Greedy closes the section by reframing a Two-Pointer problem as a
greedy choice with a formal proof.

## Common pitfalls

- **Assuming greedy without proof.** This is the #1 sin. Always ask "can I
  find a counterexample?" before committing. If you cannot prove the rule,
  the judge will find the counterexample for you.
- **Sorting by the wrong key.** Hand of Straights *must* be sorted ascending
  by card value; sorting by frequency or by suit breaks the consume-in-order
  argument. Always articulate *why* your key makes the rule safe.
- **Integer overflow on a running sum.** Kadane and Gas Station accumulate
  values across the whole array. If the input range permits sums beyond
  ~2.1 * 10^9, use `long` for the running variable and cast back at the end.
- **Off-by-one on the last element.** Jump Game must consider the jump that
  *lands on* the last index, not one that overshoots; Kadane must include
  the final element in the running max. Check the loop bound and the early
  exit carefully.
- **Resetting state at the wrong moment.** Kadane resets the running sum to
  0 *after* recording the best, not before — otherwise a single negative
  element wipes out a valid answer. Gas Station restarts the start index
  *and* zeroes the tank together; doing only one leaves the state
  inconsistent.
- **Forgetting the global feasibility check.** Gas Station only has an
  answer when `totalGas >= totalCost`. Skip that check and you will return a
  bogus start index on impossible inputs.
