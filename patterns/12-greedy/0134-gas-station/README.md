# 0134 - Gas Station

**Difficulty:** Medium
**Pattern:** Greedy
**LeetCode:** https://leetcode.com/problems/gas-station/

## Concepts used

- **Greedy** -- make the locally-best choice at each step and never revisit it; works only when you can prove that choice is part of some optimal solution. [glossary](../../../docs/10-glossary.md#greedy)
- **Array** -- a row of numbered slots holding values, accessed by position in O(1). [glossary](../../../docs/10-glossary.md#array)
- **Running sum** -- one variable that keeps adding the next value as you walk, so it always holds the total gathered since some starting point.
- **Invariant** -- a condition that is always true at the start of every loop iteration; stating it clearly is how you prove a loop correct. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

There are `n` gas stations arranged in a **circle**. You are given two
integer arrays `gas` and `cost` of length `n`:

- `gas[i]` is the amount of fuel you get by refuelling at station `i`.
- `cost[i]` is the fuel burned driving from station `i` to station `i + 1`
  (the next station clockwise; after station `n - 1` you arrive at station
  `0`).

Your tank starts empty at one station. Pick the **unique starting station**
from which you can drive clockwise around the whole circuit once without
running out of fuel; return its index, or `-1` if no such station exists.
The problem guarantees that if a solution exists, it is unique.

Signature:

    int canCompleteCircuit(int[] gas, int[] cost)

Examples (verbatim from LeetCode):

    Input:  gas = [1,2,3,4,5], cost = [3,4,5,1,2]
    Output: 3
    Explanation: start at station 3; tank evolves 4 -> 3 -> 4 -> 5 -> 0 -> 1 (back to start).

    Input:  gas = [2,3,4], cost = [3,4,3]
    Output: -1

## Intuition

The trigger: a circular reachability problem with a single running
quantity (the tank). Two insights carry the whole solution.

**Insight 1 — global feasibility.** Sum every `gas[i] - cost[i]`. If the
total surplus is negative, *no* starting station can complete the circuit
(the total fuel burned exceeds the total fuel pumped). Conversely, if the
total surplus is non-negative, a starting station is guaranteed to exist
(we will prove via Insight 2 that it is unique and our scan will find it).
So the `-1` case is settled in O(n) up front.

**Insight 2 — the one-pass restart.** Walk the stations from index 0,
maintaining a running tank that starts at 0 and accumulates
`gas[i] - cost[i]`. As long as the tank stays non-negative, the current
candidate start is still viable. The moment the tank goes negative at
station `j`, you have learned something strong:

> If you cannot reach station `j + 1` from candidate `start`, you also
> cannot reach it from any station between `start` and `j`, because they
> all had to *pass through* the prefix that just went negative — starting
> later only drops the buffer that prefix contributed.

So discard every candidate in `[start, j]` at once: reset the tank to 0
and set the next candidate to `j + 1`. Continue. Because Insight 1 already
guaranteed a solution exists when the total surplus is non-negative, the
last candidate standing after the single pass must be the answer.

### Checkpoint A -- Total surplus and the restart

Pause and answer before expanding.

**Q1 (recall).** What does `totalSurplus` (the sum of every `gas[i] - cost[i]`) tell us?
- a) Whether any circuit is possible at all
- b) Which station is the best start
- c) How much fuel each single station gives

<details><summary>Show answer</summary>

**(a)** -- a negative total means the whole circuit burns more fuel than it pumps, so no start can succeed and we return -1.

</details>

**Q2 (comprehend).** When the running tank goes negative at station `j`, why can we discard EVERY candidate from `start` to `j`, not just `start`?
- a) Because those stations are spaced too far apart
- b) Any start in that range must pass through the same negative prefix, and starting later only loses that prefix's buffer
- c) Because `gas[j]` is zero

<details><summary>Show answer</summary>

**(b)** -- the shared prefix drove the tank negative once; a later start within the block carries even less buffer, so it fails too. We jump the candidate to `j + 1`.

</details>

## Pseudocode

```text
function canCompleteCircuit(gas, cost):
    totalSurplus = 0        # global feasibility: total gas - total cost
    tank = 0                # running tank from the current candidate start
    start = 0               # current candidate starting station
    for i from 0 to n-1:
        surplus = gas[i] - cost[i]
        totalSurplus = totalSurplus + surplus
        tank = tank + surplus
        if tank is less than 0:
            # Cannot reach station i+1 from `start`, nor from any station
            # in [start, i]. Discard them all and try starting at i+1.
            start = i + 1
            tank = 0
    if totalSurplus is less than 0:
        return -1           # no start exists
    return start
```

The scan is single-pass; the wrap-around is implicit because global
feasibility already certifies the answer.

## Java Solution

```java
class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int totalSurplus = 0;
        int tank = 0;
        int start = 0;
        for (int i = 0; i < gas.length; i++) {
            int surplus = gas[i] - cost[i];
            totalSurplus += surplus;
            tank += surplus;
            // A prefix that drives the tank negative can never help a
            // later start within it: starting later only loses this prefix's
            // (positive or negative) contribution. So skip the whole block.
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }
        if (totalSurplus < 0) {
            return -1;
        }
        return start;
    }
}
```

`totalSurplus` is the feasibility certificate; only after the loop do we
check it, because computing it in the same pass as the restart scan keeps
the code to one traversal. `start` is the only candidate we track — every
time `tank` dips below zero we discard a whole block of candidates, which
is why the algorithm is O(n) rather than O(n^2). The final return trusts
the problem's uniqueness guarantee: when a circuit exists, the surviving
candidate is it.

## Complexity

    Time:  O(n)   -- one pass over the stations; each index is visited once.
    Space: O(1)   -- three integer variables.

## Dry-Run

Step-by-step on `gas = [1,2,3,4,5]`, `cost = [3,4,5,1,2]`:

| i | surplus = gas[i] - cost[i] | totalSurplus | tank | tank < 0? | start |
|---|----------------------------|--------------|------|-----------|-------|
| - |                            | 0            | 0    |           | 0     |
| 0 | 1 - 3 = -2                 | -2           | -2   | yes       | 1     |
| 1 | 2 - 4 = -2                 | -4           | -2   | yes       | 2     |
| 2 | 3 - 5 = -2                 | -6           | -2   | yes       | 3     |
| 3 | 4 - 1 = 3                  | -3           | 3    | no        | 3     |
| 4 | 5 - 2 = 3                  | 0            | 6    | no        | 3     |

End of loop: `totalSurplus = 0`, which is non-negative, so a solution
exists. Return `start = 3`. Verify mentally: starting at station 3, the
tank goes 4 -> 3 -> 4 -> 5 -> 0 -> 1, never negative. Correct.

For the impossible case `gas = [2,3,4]`, `cost = [3,4,3]`:

| i | surplus | totalSurplus | tank | tank < 0? | start |
|---|---------|--------------|------|-----------|-------|
| 0 | -1      | -1           | -1   | yes       | 1     |
| 1 | -1      | -2           | -1   | yes       | 2     |
| 2 | 1       | -1           | 1    | no        | 2     |

`totalSurplus = -1 < 0`, so return **-1**.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `gas = [3, 1, 2]`, `cost = [2, 2, 2]`. What is returned?
- a) 0
- b) -1
- c) 2

<details><summary>Show answer</summary>

**(a)** -- surpluses are [1, -1, 0]; the tank stays non-negative throughout (1 -> 0 -> 0), `start` never moves off 0, and `totalSurplus = 0` is feasible, so return 0.

</details>

**Q2 (analyze).** If the final `totalSurplus < 0` check were removed, what would the algorithm return on `gas = [1, 1, 1]`, `cost = [2, 2, 2]`?
- a) A bogus start of 3 (out of bounds) -- a false positive
- b) -1 -- correct
- c) 0

<details><summary>Show answer</summary>

**(a)** -- every station drives the tank negative, so `start` marches to 3 (past the last index). The feasibility check is the only thing that converts this into the correct -1.

</details>

**Q3 (transfer).** The problem guarantees a unique answer. If the guarantee were dropped and you needed to return ANY valid start (or -1), would the one-pass algorithm still be correct whenever `totalSurplus >= 0`? Why?

<details><summary>Show answer</summary>

Yes. The proof only needs `totalSurplus >= 0` to guarantee that *some* valid start exists, and the restart rule converges on one. Uniqueness makes the answer deterministic but is not required for correctness.

</details>

## Common mistakes

- **Skipping the global feasibility check.** Without `totalSurplus`, the
  single pass returns the last surviving `start` even on impossible inputs,
  producing a false positive.
- **Resetting `start` without resetting `tank`.** The two go together: a
  new candidate begins with an empty buffer. Resetting one and not the
  other leaves the state inconsistent.
- **Wrapping the index with modulo and simulating the full circuit.** That
  is the brute-force O(n^2) approach. The greedy avoids the wrap by
  proving that the surviving `start` (given feasibility) is necessarily
  correct.
- **Initialising `tank` or `totalSurplus` to a non-zero value.** Both must
  start at 0; the surplus at each station is the only contribution.
- **Returning `start` when `totalSurplus < 0`.** Always check feasibility
  first; the candidate is meaningless if the circuit is impossible.

## Related problems

- [0053 - Maximum Subarray](../0053-maximum-subarray/) - the same "reset
  the running state when it goes negative" trick (Kadane).
- [0055 - Jump Game](../0055-jump-game/) - circular-reachability's linear
  cousin, also solved with a single running invariant.
- [0966 - Binary Subarrays With Sum] - another problem where a running
  count and a global invariant cooperate.
