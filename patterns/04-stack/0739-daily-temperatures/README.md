# 0739 - Daily Temperatures

**Difficulty:** Medium
**Pattern:** Stack (monotonic decreasing)
**LeetCode:** https://leetcode.com/problems/daily-temperatures/

## Concepts used

- **Array** — a row of numbered slots holding values, each reached in O(1) by its index. [glossary](../../../docs/10-glossary.md#array)
- **Monotonic stack** — a stack kept sorted in one direction; used to answer "next greater element" in one pass. [glossary](../../../docs/10-glossary.md#monotonic-stack)

## Problem

Given an array `temperatures` of daily temperatures, return an array `answer`
such that `answer[i]` is the **number of days you have to wait** after the `i`th
day to get a warmer temperature. If there is no future day with a warmer
temperature, `answer[i] == 0`.

Signature:

    int[] dailyTemperatures(int[] temperatures)

Examples (verbatim from LeetCode):

    Input:  temperatures = [73,74,75,71,69,72,76,73]
    Output: [1,1,4,2,1,1,0,0]

    Input:  temperatures = [30,40,50,60]
    Output: [1,1,1,0]

    Input:  temperatures = [30,60,90]
    Output: [1,1,0]

## Intuition

For each day we want the **next** day that is warmer — a "next greater element"
question. The brute force is to scan everyone after each day, but that checks
pairs over and over. We can do it in one pass with a waiting list.

Walk down the days left to right. Some days have not yet met anyone warmer; keep
those waiting days on a [stack](../../../docs/10-glossary.md#stack) that we keep
**sorted in one direction** — specifically, strictly *decreasing* in temperature
from bottom to top (hottest at the bottom, coldest at the top). A stack kept
sorted like this is called a [monotonic stack](../../../docs/10-glossary.md#monotonic-stack);
"monotonic" just means "always moving one way, never reversing."

Why **decreasing**, and why does it find the answer? The top of the stack is the
coldest day still waiting. The moment a warmer day arrives, that warmer day must
be the answer for the top — because every day between the top and today was even
colder (otherwise the top would have been popped already). So we pop the top,
record the gap "today minus that day," and repeat, because today might also be
the answer for the next colder day waiting below. When the top is no longer
colder than today, today itself joins the stack to wait for its own warmer
future.

Trace the smallest case, `T = [73, 74, 75]` (expected `[1, 1, 0]`). The stack
holds **indices**, and we compare temperatures through them:

- i=0, T=73: stack empty, push 0. Stack: `[0]`.
- i=1, T=74: 74 > T[0]=73, so day 0 is answered — pop 0, answer[0] = 1 - 0 = 1.
  Stack empty, push 1. Stack: `[1]`.
- i=2, T=75: 75 > T[1]=74, pop 1, answer[1] = 2 - 1 = 1. Stack empty, push 2.
  Stack: `[2]`.
- End. Day 2 (75) never found a warmer day, so answer[2] stays 0.

Final: `[1, 1, 0]`. Although a `while` sits inside the `for`, each index is
pushed once and popped at most once across the whole run, so the total work is
O(n), not O(n²) — this "cheap on average over many operations" property is called
[amortized](../../../docs/10-glossary.md#amortized) cost.

### Checkpoint A -- Indices and monotonicity

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** What does the stack store -- the temperatures, or the indices of the days?
- a) The temperatures
- b) The indices
- c) Both, as index-temperature pairs

<details><summary>Show answer</summary>

**(b)** -- indices, so we can write `answer[j]` and compute the day gap `i - j`; the temperature is looked up through the index.

</details>

**Q2 (comprehend).** The `for` loop contains a `while` loop. Why is the whole algorithm still O(n), not O(n^2)?
- a) Because the input array is small
- b) Because each index is pushed once and popped at most once across the entire run, so the inner loop's total work is O(n)
- c) Because the stack is kept sorted

<details><summary>Show answer</summary>

**(b)** -- across all n iterations the `while` body fires at most n times in total (amortized O(1) per element), so the total is O(n).

</details>

## Pseudocode

    function dailyTemperatures(temperatures):
        n = length(temperatures)
        answer = new array of size n, filled with 0
        stack = new empty stack          # stores INDICES; temperatures stay decreasing

        for i from 0 to n - 1:
            while stack is not empty AND temperatures[i] > temperatures[top of stack]:
                j = pop the stack        # day j has finally found a warmer day: i
                answer[j] = i - j
            push i onto the stack

        return answer
        # indices left on the stack never found a warmer day -> stay 0 (the default)

## Java Solution

```java
import java.util.*;

class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n]; // default 0 = "no warmer day found"
        Deque<Integer> stack = new ArrayDeque<>(); // indices, temps strictly decreasing
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int j = stack.pop();
                answer[j] = i - j;
            }
            stack.push(i);
        }
        return answer;
    }
}
```

The stack holds indices whose warmer partner hasn't been seen yet, and it is kept
strictly decreasing by temperature, which is what makes the `while` loop correct:
as soon as today is warmer than the top, today is the *first* such day (every
earlier, colder day was already popped by someone else). The `answer` array
starts as all zeros, which is exactly the "no warmer day" default, so any index
left on the stack at the end needs no extra handling. Although the `for` contains
a `while`, every index is pushed once and popped at most once, so the total work
is O(n), not O(n^2).

## Complexity

    Time:  O(n)  -- each index is pushed once and popped at most once across the whole loop.
    Space: O(n)  -- the stack can hold up to n indices (e.g. a strictly decreasing input).

## Dry-Run

Step-by-step on `temperatures = [73,74,75,71,69,72,76,73]` (expected
`[1,1,4,2,1,1,0,0]`). The stack stores indices; `T[i]` is the temperature.

| Step | i | T[i] | pops (idx -> ans) | stack after | answer so far |
|-----:|--:|-----:|-------------------|-------------|---------------|
| 1 | 0 | 73 | — (stack empty) | `[0]` | `[0,0,0,0,0,0,0,0]` |
| 2 | 1 | 74 | 0 -> ans[0]=1 | `[1]` | `[1,0,0,0,0,0,0,0]` |
| 3 | 2 | 75 | 1 -> ans[1]=1 | `[2]` | `[1,1,0,0,0,0,0,0]` |
| 4 | 3 | 71 | — (71<75) | `[2,3]` | `[1,1,0,0,0,0,0,0]` |
| 5 | 4 | 69 | — (69<71) | `[2,3,4]` | `[1,1,0,0,0,0,0,0]` |
| 6 | 5 | 72 | 4 -> ans[4]=1; 3 -> ans[3]=2 | `[2,5]` | `[1,1,0,2,1,0,0,0]` |
| 7 | 6 | 76 | 5 -> ans[5]=1; 2 -> ans[2]=4 | `[6]` | `[1,1,4,2,1,1,0,0]` |
| 8 | 7 | 73 | — (73<76) | `[6,7]` | `[1,1,4,2,1,1,0,0]` |

Indices `6` and `7` remain on the stack -> their answers stay `0`. Final result:
`[1,1,4,2,1,1,0,0]`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `temperatures = [50, 40, 30, 60]`. What is `answer`?
- a) `[1, 1, 1, 0]`
- b) `[3, 2, 1, 0]`
- c) `[3, 2, 0, 0]`

<details><summary>Show answer</summary>

**(b)** -- days 0, 1, 2 all wait until day 3 (60); gaps are 3, 2, 1; day 3 finds nothing warmer, so it stays 0.

</details>

**Q2 (analyze).** What goes wrong if the `while` condition uses `>=` instead of `>`?
- a) Nothing changes
- b) Equal temperatures get reported as "warmer" -- for `[70, 70]` it would wrongly set `answer[0] = 1`
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- `>=` pops on a tie, treating an equal day as warmer; `[70, 70]` would set `answer[0] = 1`, when the correct answer is `0` (no STRICTLY warmer day).

</details>

**Q3 (transfer).** How would you adapt the algorithm to find the "next SMALLER day" (next day with a LOWER temperature)?

<details><summary>Show answer</summary>

Flip the monotonicity: keep an INCREASING stack and pop while the current temperature is smaller than the top. Storing indices, the distance formula `i - j`, and the zero default all stay the same.

</details>

## Common mistakes

- Storing temperatures instead of indices. Then you cannot fill `answer[j]` or
  compute the day gap `i - j`. Always store the index; look up the temperature
  through it.
- Using `>=` instead of `>` in the `while`. That would pop on equal temperatures,
  reporting a same-temperature day as "warmer" — wrong, and it also breaks the
  strictly-decreasing invariant. "Warmer" means strictly greater.
- Forgetting to default `answer` to 0 and instead assuming every day finds a
  partner. Indices never popped (the global maximum and anything after it) must
  end up `0`.
- Worrying that the nested `while` makes it O(n^2). It does not: across the entire
  run each index is pushed once and popped at most once, so the amortized cost per
  element is O(1).
- Keeping an *increasing* stack by mistake. "Next greater" needs a *decreasing*
  stack — the stack waits in the opposite sense of what you seek.

## Related problems

- [0020 - Valid Parentheses](../0020-valid-parentheses/) — same "waiting for a
  partner" mental model, simpler matching rule.
- [0155 - Min Stack](../0155-min-stack/) — another auxiliary stack, but tracking
  the running minimum instead of the next greater.
- (Outside this pattern, for later reading:) "Next Greater Element I/II" (LC 496,
  503) and "Largest Rectangle in Histogram" (LC 84) reuse this exact monotonic
  template.
