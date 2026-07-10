# 0155 - Min Stack

**Difficulty:** Medium
**Pattern:** Stack
**LeetCode:** https://leetcode.com/problems/min-stack/

## Concepts used

- **Stack** — a last-in-first-out (LIFO) container: the last item in is the first one out, like a stack of plates. [glossary](../../../docs/10-glossary.md#stack)
- **Time complexity** — how the runtime grows as the input grows; O(1) means "constant time, regardless of input size." [glossary](../../../docs/10-glossary.md#time-complexity)

## Problem

Design a stack that supports `push`, `pop`, `top`, and retrieving the **minimum**
element — all in **O(1)** time.

Signature (the class is named `MinStack`):

    MinStack()                    // constructor
    void push(int val)
    void pop()
    int top()
    int getMin()

Methods `pop`, `top`, and `getMin` will always be called on a **non-empty** stack.

Example (verbatim from LeetCode):

    Input:
      ["MinStack","push","push","push","getMin","pop","top","getMin"]
      [[],[-2],[0],[-3],[],[],[],[]]
    Output: [null,null,null,null,-3,null,0,-2]

## Intuition

Picture a stack of index cards, each with a number, where you can only see or
take the top card. You also want to always know the **smallest** number in the
pile without flipping through every card. The trick: every time you add a card,
jot down "the smallest value seen so far" on a sticky note and place that note
on a second pile. The second pile's top always reports the current minimum, and
when you remove a card you remove its sticky note too — the two piles move in
sync.

Walk through the smallest case, pushing `-2`, then `0`, then `-3`:

- push(-2): values `-2`, mins `-2`. Min = -2.
- push(0): values `-2, 0`; `0` is not below -2, so the min is unchanged — mins
  becomes `-2, -2`. Min still -2.
- push(-3): values `-2, 0, -3`; -3 is a new low — mins becomes `-2, -2, -3`.
  Min = -3.
- pop(): remove `-3` from values **and** `-3` from mins. values `-2, 0`, mins
  `-2, -2`. Min = -2 instantly — no scanning.

The general rule: keep a second [stack](../../../docs/10-glossary.md#stack) that,
at every depth, holds the minimum among all values at that depth or below. The
two stacks always have the same size, so `getMin` just reads the top of the min
stack. Why a second stack at all, instead of scanning for the min on demand?
Scanning is [time complexity](../../../docs/10-glossary.md#time-complexity) O(n)
per `getMin`, but the problem demands O(1); the second stack buys O(1) speed by
spending O(n) extra memory.

### Checkpoint A -- Two stacks

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** In this design, what does the second stack (`mins`) hold at every depth?
- a) The maximum value seen so far
- b) The minimum among all live values at that depth or below
- c) The number of elements

<details><summary>Show answer</summary>

**(b)** -- `mins` mirrors `values` one-for-one, so its top is always the minimum of everything currently in the stack.

</details>

**Q2 (comprehend).** On `push`, when `val` is NOT a new minimum the code pushes a COPY of the current `mins` top instead of `val`. Why?
- a) To keep the two stacks the same size, so a later `pop` drops one from each and `getMin` stays a single O(1) peek
- b) To use less memory
- c) To sort the values

<details><summary>Show answer</summary>

**(a)** -- carrying the running min forward makes every push add exactly one entry to `mins`, so `pop` can remove exactly the right partner and `getMin` never scans.

</details>

## Pseudocode

    structure MinStack:
        values = new stack      # the real elements
        mins   = new stack      # parallel: minimum for each prefix

    function push(val):
        push val onto values
        if mins is empty or val < top of mins:
            push val onto mins
        else:
            push (top of mins) onto mins      # carry the current min forward

    function pop():
        pop from values
        pop from mins

    function top():
        return top of values

    function getMin():
        return top of mins

## Java Solution

```java
import java.util.*;

class MinStack {
    private final Deque<Integer> values = new ArrayDeque<>();
    private final Deque<Integer> mins = new ArrayDeque<>();

    public void push(int val) {
        values.push(val);
        mins.push(mins.isEmpty() || val < mins.peek() ? val : mins.peek());
    }

    public void pop() {
        values.pop();
        mins.pop();
    }

    public int top() {
        return values.peek();
    }

    public int getMin() {
        return mins.peek();
    }
}
```

Two `ArrayDeque`s back the design: `values` holds the real elements and `mins`
holds the running minimum, one entry per element so the two stacks always have
the same size. On `push` we write `val` to `values`, and to `mins` we write
`val` when it is a new minimum, otherwise we copy the current top — that copy is
the key invariant: "the top of `mins` is always the minimum of all live values."
`pop` removes one entry from each, so `getMin` is a single O(1) peek. This
mirroring costs O(n) extra space but keeps every operation constant time, which
is the explicit requirement.

## Complexity

    Time:  O(1) per operation  -- push/pop/top/getMin each do a constant number of stack ops.
    Space: O(n)                 -- the min stack mirrors the main stack, one entry per element.

## Dry-Run

Replaying the LeetCode example: push -2, push 0, push -3, getMin, pop, top,
getMin.

| Operation | values (bottom -> top) | mins (bottom -> top) | return |
|-----------|------------------------|----------------------|--------|
| push(-2) | `-2` | `-2` | — |
| push(0)  | `-2, 0` | `-2, -2` | — |
| push(-3) | `-2, 0, -3` | `-2, -2, -3` | — |
| getMin   | `-2, 0, -3` | `-2, -2, -3` | **-3** |
| pop      | `-2, 0` | `-2, -2` | — |
| top      | `-2, 0` | `-2, -2` | **0** |
| getMin   | `-2, 0` | `-2, -2` | **-2** |

After popping `-3` the top of `mins` drops back to `-2` automatically — no scan
required.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace: `push(5)`, `push(2)`, `push(7)`, then `getMin`. What is returned?
- a) 2
- b) 5
- c) 7

<details><summary>Show answer</summary>

**(a)** -- values = `[5, 2, 7]`, mins = `[5, 2, 2]` (7 is not below 2, so 2 is copied); `getMin` peeks the mins top = 2.

</details>

**Q2 (analyze).** What breaks if you forget to call `mins.pop()` inside `pop()`?
- a) Nothing -- `mins` is independent of `values`
- b) `mins` falls out of sync; after popping the minimum, `getMin` still returns that now-removed value
- c) It throws immediately

<details><summary>Show answer</summary>

**(b)** -- the two stacks must move in lockstep; skipping `mins.pop` leaves a stale entry on top, so `getMin` reports a value no longer in the stack.

</details>

**Q3 (transfer).** How would you extend the design to also support `getMax()` in O(1)?

<details><summary>Show answer</summary>

Add a third parallel stack `maxs` with the same mirroring logic, but push `val` when it is a new MAXIMUM, otherwise copy the current top. All operations stay O(1); the cost is more memory.

</details>

## Common mistakes

- Recomputing the minimum on `getMin` with a loop. That is O(n), which breaks the
  requirement.
- Pushing to `mins` only when the value is a new minimum, then popping from it
  only when it equals the top. This can work but is bug-prone; mirroring (one
  entry per push) is simpler and always correct.
- Forgetting to `pop` the min stack in `pop`, leaving `mins` out of sync — then
  `getMin` returns a value that is no longer in the stack.
- Peeking an empty stack. The problem guarantees calls are on non-empty stacks,
  but if you relax that, guard with `isEmpty()`.

## Related problems

- [0020 - Valid Parentheses](../0020-valid-parentheses/) — the other plain-LIFO
  problem in this pattern.
- [0739 - Daily Temperatures](../0739-daily-temperatures/) — an auxiliary stack
  is again the key, but for "next greater" instead of "minimum so far".
