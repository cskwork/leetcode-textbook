# 0739 - Daily Temperatures

**Difficulty:** Medium
**Pattern:** Stack (monotonic decreasing)
**LeetCode:** https://leetcode.com/problems/daily-temperatures/

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

For each day we want the **next** day with a higher temperature — a classic
"next greater element" question, which is the flagship use of a **monotonic
stack**.

The idea: scan left to right, keeping a stack of days whose warmer partner has
**not yet appeared**. To make finding that partner cheap, we keep the stack
strictly *decreasing* in temperature. When today's temperature is higher than the
temperature of the day on top of the stack, today *is* that day's partner — so we
pop it, record the distance, and keep popping until the stack is again decreasing
(or empty). Then today itself goes on the stack to wait.

We store **indices** (not temperatures) so we can write `answer[idx]` and compute
the day difference `i - idx`.

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
