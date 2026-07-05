# Pattern 4 - Stack

A stack is a **Last-In, First-Out (LIFO)** container: the last element you put in
is the first one to come out. Think of a stack of plates — you add to the top and
take from the top. That single rule solves a surprising range of problems because
it models the natural idea of **"deal with the most recent thing first."**

---

## When the Stack pattern applies

Scan the problem statement for these **trigger signals** (from
`01-patterns-overview.md`):

| Signal in the problem | Stack flavor to reach for |
|---|---|
| "valid parentheses", "balanced brackets", "matching pairs" | Plain LIFO stack |
| "next greater / next smaller element", "days until warmer" | **Monotonic stack** (decreasing or increasing) |
| "evaluate expression", "reverse Polish notation", "postfix" | Operand stack |
| "recent X", "min/max so far in O(1)", "design a ..." | Auxiliary stack / pair-stack |
| "remove adjacent duplicates", "backspace compare" | Builder stack |

If you hear *any* of those phrases, a stack is almost always the cleanest answer.

---

## The LIFO mental model

```
push(a)  push(b)  push(c)      pop() -> c      pop() -> b
   |        |        |              |              |
   a        b        c              b              a
            a        b              a
                     a
top ->  c            b              a
```

The element on **top** is always the *most recent* one still waiting. Two
consequences make the pattern powerful:

1. **Reversal for free.** Pushing a sequence and popping it yields the reverse.
   This is why a stack parses expressions and undoes operations naturally.
2. **"Waiting for a partner."** Anything you push is *paused*, holding its place
   until something later in the input releases it.

### Why parentheses matching works

A string of brackets is valid exactly when **the most recent unclosed opener must
close first**. That is *precisely* LIFO order:

- An opener `(` `[` `{` goes on the stack — it is "waiting" for its partner.
- On a closer `)` `]` `}`, the opener it must match is the one on **top** of the
  stack (the most recent unclosed one). If the top doesn't match, or the stack is
  empty, the string is invalid.
- At the end the stack must be **empty** — every opener found a partner.

This is the canonical "most-recent-first" stack use case, and the same idea
powers expression evaluation and undo/backspace problems.

---

## The reusable template: monotonic stack

The trickiest and most reusable variant is the **monotonic stack**, used for
"next greater / next smaller" problems (LC 739, 496, 503, 84...). The whole
pattern is: *every element gets pushed once and popped at most once, so the total
work is O(n)* despite the nested-looking `while` loop.

```
function monotonic_stack(values):
    initialize an empty stack          # stores indices, kept in monotonic order
    initialize an answer array of zeros

    for i from 0 to length(values) - 1:
        # While the current element is the "partner" the stacked elements wait for:
        while stack is not empty AND values[i] relates to values[top of stack]:
            j = pop top of stack        # j is released by i
            answer[j] = i - j           # (or values[i], or whatever the problem asks)
        push i onto the stack           # i now waits for its own future partner

    # anything left on the stack at the end never found a partner -> default answer (0)
    return answer
```

**Two knobs to set per problem:**

- **What the stack stores.** Usually *indices* (so you can fill `answer[j]` and
  compute distance `i - j`), sometimes the values themselves.
- **The monotonicity direction.**
  - "Next *greater*" -> keep a **decreasing** stack; the new element pops
    everything smaller than itself.
  - "Next *smaller*" -> keep an **increasing** stack.

Memory aid: *the stack is always monotonic in the opposite sense of what you are
looking for.* Looking for a bigger partner? The stack shrinks downward, waiting
for something bigger.

---

## Problems in this pattern

| # | LC | Problem | Difficulty | Teaser |
|---|----|---------|-----------|--------|
| 21 | 20 | [Valid Parentheses](./0020-valid-parentheses/) | Easy | The "hello world" of stacks — does every bracket find its partner? |
| 22 | 155 | [Min Stack](./0155-min-stack/) | Medium | Design a stack that returns the minimum in O(1). |
| 23 | 150 | [Evaluate Reverse Polish Notation](./0150-evaluate-reverse-polish-notation/) | Medium | Postfix expressions: push operands, pop on operators. |
| 24 | 739 | [Daily Temperatures](./0739-daily-temperatures/) | Medium | "Days until warmer" — your first monotonic decreasing stack. |
| 25 | 22 | [Generate Parentheses](./0022-generate-parentheses/) | Medium | Build all valid bracket strings; open/close counters act like a stack. (Bridge to the Backtracking pattern.) |

The list ramps from the plain LIFO idea (20) through a design problem (155) and
expression evaluation (150), into the monotonic variant (739), and finally a
generative problem (22) whose open/close counters are the *logical* equivalent of
a stack of unmatched openers — a deliberate bridge to Pattern 10 (Backtracking).

---

## Common pitfalls

- **Popping in the wrong order for non-commutative operators.** In RPN, `a - b`
  is *not* `b - a`. Always pop into a temp `b` first, then `a`, then compute
  `a op b`. Same trap for division.
- **Forgetting to flush / check the stack at the end.** In Valid Parentheses you
  must return `stack.isEmpty()` at the end — extra openers mean invalid. In a
  monotonic stack, leftover elements mean "no partner found" and must keep the
  default answer (usually 0), so initialise the answer array to the right
  default rather than leaving garbage.
- **Peeking an empty stack.** Before `top`/`peek` or `pop`, guard with
  `isEmpty()`. Popping empty throws (or returns null) and is the #1 source of
  crashes.
- **Storing values when you need indices.** For "next greater" problems you need
  the *index* so you can write `answer[j]` and compute distance. Storing the
  value loses position.
- **Wrong monotonicity direction.** "Next greater" needs a *decreasing* stack;
  if you keep an increasing one you pop nothing and get all-zero answers. Think:
  *the stack waits in the opposite sense of the target.*
- **Using the legacy `Stack` class.** In Java prefer `ArrayDeque` — it is faster
  and not synchronized. Declare `Deque<T> stack = new ArrayDeque<>();` and use
  `push` / `pop` / `peek`.

---

## Java note: use `ArrayDeque`, not `Stack`

This book always uses

```java
Deque<Integer> stack = new ArrayDeque<>();   // LIFO
stack.push(x);                               // add to top
int top = stack.peek();                      // look at top
stack.pop();                                 // remove top
```

`java.util.Stack` is a legacy, synchronized class kept only for backward
compatibility; `ArrayDeque` is the modern, faster LIFO/FIFO container (see the
Java crash course, section 4).

---

Next: [0020 - Valid Parentheses](./0020-valid-parentheses/) — start here.
