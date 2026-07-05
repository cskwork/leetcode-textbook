# 0155 - Min Stack

**Difficulty:** Medium
**Pattern:** Stack
**LeetCode:** https://leetcode.com/problems/min-stack/

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

A plain stack already gives O(1) `push`/`pop`/`top`, but `getMin` would need a
linear scan. The trick is to remember the minimum **at every depth** so that
after any pop we instantly know the new minimum without recomputing.

We keep a second "min stack" that **mirrors** the main stack: every push to the
main stack also pushes the minimum *as seen at that point* onto the min stack.
Then `getMin` is just the top of the min stack, and a pop removes one entry from
each — they stay perfectly in sync.

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
