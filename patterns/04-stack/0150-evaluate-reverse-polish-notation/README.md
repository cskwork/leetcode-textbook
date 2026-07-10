# 0150 - Evaluate Reverse Polish Notation

**Difficulty:** Medium
**Pattern:** Stack
**LeetCode:** https://leetcode.com/problems/evaluate-reverse-polish-notation/

## Concepts used

- **Stack** — a last-in-first-out (LIFO) container: the last item in is the first one out, like a stack of plates. [glossary](../../../docs/10-glossary.md#stack)
- Beyond the stack, this problem needs only basic arithmetic and reading the token list left to right.

## Problem

Evaluate the value of an arithmetic expression in **Reverse Polish Notation**
(RPN, also called postfix notation).

Valid operators are `+`, `-`, `*`, and `/`. Each operand is an integer. Division
between two integers **truncates toward zero**. The input is guaranteed to be a
valid RPN expression.

Signature:

    int evalRPN(String[] tokens)

Examples (verbatim from LeetCode):

    Input:  tokens = ["2","1","+","3","*"]
    Output: 9
    Explanation: ((2 + 1) * 3) = 9

    Input:  tokens = ["4","13","5","/","+"]
    Output: 6
    Explanation: 4 + (13 / 5) = 4 + 2 = 6

    Input:  tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
    Output: 22

## Intuition

Think of RPN as an assembly line. Numbers are parts piling up; an operator is a
worker who grabs the **two most recent** parts, combines them, and puts the
result back on the pile. For `a - b` the worker takes the top part as `b` (the
right operand) and the part just below as `a` (the left). RPN was literally
designed so a machine needs no parentheses and no precedence rules — it just
reads left to right.

Why does this fit a [stack](../../../docs/10-glossary.md#stack)? A stack is
last-in-first-out, so its top is always "the most recent number" — exactly what
an operator must grab. So: push every number; when you hit an operator, pop two,
compute, push the result back.

Trace the smallest case, `tokens = ["2", "1", "+", "3", "*"]` (this is
`((2 + 1) * 3) = 9`):

- `2` → number, push. Stack: `2`.
- `1` → number, push. Stack: `2, 1`.
- `+` → operator. Pop `b = 1`, pop `a = 2`, push `2 + 1 = 3`. Stack: `3`.
- `3` → number, push. Stack: `3, 3`.
- `*` → operator. Pop `b = 3`, pop `a = 3`, push `3 * 3 = 9`. Stack: `9`.

The single value left, `9`, is the answer. One detail to lock in: **always pop
into `b` first, then `a`**, and compute `a op b`. Reverse them and `-` and `/`
come out with the wrong sign.

### Checkpoint A -- Operand order

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** When the next token is an operator, how many values do you pop from the stack?
- a) One
- b) Two
- c) All of them

<details><summary>Show answer</summary>

**(b)** -- every operator in RPN combines exactly two operands: pop two, compute, push the result back.

</details>

**Q2 (comprehend).** The code pops into `b` first, then `a`, and computes `a op b`. Why this order and not the reverse?
- a) It runs faster
- b) The top of the stack is the RIGHT operand (pushed last); reversing it flips the sign of `-` and `/`
- c) Java requires it

<details><summary>Show answer</summary>

**(b)** -- for non-commutative operators the right operand sits on top, so you must pop it first; computing `b op a` would turn `a - b` into `b - a`.

</details>

## Pseudocode

    function evalRPN(tokens):
        create an empty stack of numbers

        for each token in tokens:
            if token is one of "+" , "-" , "*" , "/":
                b = pop the stack          # right operand (popped first)
                a = pop the stack          # left operand
                result = apply operator to (a, b)
                    # for "/" : truncate toward zero
                push result onto the stack
            else:
                push (parse token as integer) onto the stack

        return the single value left on the stack

## Java Solution

```java
import java.util.*;

class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String t : tokens) {
            switch (t) {
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                    break;
                }
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a / b); // Java int division already truncates toward zero
                    break;
                }
                default:
                    stack.push(Integer.parseInt(t));
            }
        }
        return stack.pop();
    }
}
```

A `switch` on the token cleanly separates the four operators from the
number-parsing default branch. For the commutative operators `+` and `*` the pop
order is irrelevant, so a one-liner reads well. For `-` and `/` we deliberately
pop into `b` first, then `a`, and write `a - b` / `a / b` — getting this backwards
is the classic bug. Java's integer division already truncates toward zero (it
rounds toward negative infinity only for `%`, not `/`), so `a / b` directly
satisfies the requirement with no `Math.abs` / sign-juggling. We use
`Integer.parseInt` to turn numeric tokens (including negatives like `"-11"`)
into values.

## Complexity

    Time:  O(n)  -- each token is processed once; every push and pop is O(1).
    Space: O(n)  -- the stack can hold up to ~n/2 operands for an all-numbers prefix.

## Dry-Run

Step-by-step on `tokens = ["4","13","5","/","+"]` (expected `6`):

| Step | token | action | stack (bottom -> top) |
|-----:|:-----:|--------|-----------------------|
| 1 | `4`  | push 4 | `4` |
| 2 | `13` | push 13 | `4, 13` |
| 3 | `5`  | push 5 | `4, 13, 5` |
| 4 | `/`  | b=5, a=13, push 13/5=2 | `4, 2` |
| 5 | `+`  | b=2, a=4, push 4+2=6 | `6` |

End: return top = **6**.

Dry-run on `tokens = ["2","1","+","3","*"]` (expected `9`):

| Step | token | action | stack |
|-----:|:-----:|--------|-------|
| 1 | `2` | push 2 | `2` |
| 2 | `1` | push 1 | `2, 1` |
| 3 | `+` | push 2+1=3 | `3` |
| 4 | `3` | push 3 | `3, 3` |
| 5 | `*` | push 3*3=9 | `9` |

Return **9**.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `tokens = ["3", "5", "-"]`. What is returned?
- a) `2` (that is, `5 - 3`)
- b) `-2` (that is, `3 - 5`)
- c) `0`

<details><summary>Show answer</summary>

**(b)** -- push 3, push 5; on `-` pop `b = 5`, pop `a = 3`, push `3 - 5 = -2`; return -2.

</details>

**Q2 (analyze).** What does `tokens = ["-7", "3", "/"]` return, given Java truncates integer division toward zero?
- a) `-3` (rounds toward negative infinity)
- b) `-2` (truncates toward zero)
- c) `-2.33`

<details><summary>Show answer</summary>

**(b)** -- `-7 / 3 = -2.33...`; Java `int` division truncates toward zero, giving -2, which is exactly what the problem requires (no `Math.floor` needed).

</details>

**Q3 (transfer).** How would you add a unary operator, say `~` meaning "negate the top operand"?

<details><summary>Show answer</summary>

Add a `case "~"` branch that pops ONE value, negates it, and pushes the result back. Unary operators pop one operand instead of two; everything else stays identical.

</details>

## Common mistakes

- **Swapping the operands** for `-` and `/`. If you pop `a` first (the left
  operand) you actually got the *right* operand from the top; `a - b` then gives
  the wrong sign. Always pop into `b` first.
- Reimplementing truncating division. Java's `/` on `int` already truncates toward
  zero — adding `Math.floor` or sign checks is both wrong and unnecessary.
- Forgetting that tokens are **Strings**, not `char`. Comparing `t == "+"` is a
  reference comparison; the `switch` on a `String` does the right `.equals`
  internally, but an `if (t == "+")` chain would silently fail.
- Not handling negative number tokens. `Integer.parseInt("-11")` works fine; a
  hand-rolled parser that only reads digits would break.

## Related problems

- [0020 - Valid Parentheses](../0020-valid-parentheses/) — the same LIFO
  discipline, but for matching rather than computing.
- [0155 - Min Stack](../0155-min-stack/) — another stack design where popping
  order is the central concern.
