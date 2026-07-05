# 0150 - Evaluate Reverse Polish Notation

**Difficulty:** Medium
**Pattern:** Stack
**LeetCode:** https://leetcode.com/problems/evaluate-reverse-polish-notation/

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

RPN was *invented* to be evaluated by a stack — there are no parentheses and no
precedence rules, so a single left-to-right pass suffices. Numbers go onto a
stack; when an operator appears, it consumes its two most recent operands — which
are exactly the top two of the stack — and pushes the result back.

The subtlety is operand order: the top of the stack is the **second** operand
(the right-hand one). For `a - b` and `a / b` this matters, so always pop into a
temporary `b` first, then `a`, then compute `a op b`.

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
