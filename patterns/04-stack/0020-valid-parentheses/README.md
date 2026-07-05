# 0020 - Valid Parentheses

**Difficulty:** Easy
**Pattern:** Stack
**LeetCode:** https://leetcode.com/problems/valid-parentheses/

## Problem

Given a string `s` containing just the characters `'('`, `')'`, `'{'`, `'}'`,
`'['` and `']'`, determine if the input string is **valid**.

A string is valid when:

1. Open brackets must be closed by the **same type** of bracket.
2. Open brackets must be closed in the **correct order** (the most recent open
   must close first).
3. Every close bracket has a corresponding open bracket of the same type.

Signature:

    boolean isValid(String s)

Examples (verbatim from LeetCode):

    Input:  s = "()"
    Output: true

    Input:  s = "()[]{}"
    Output: true

    Input:  s = "(]"
    Output: false

## Intuition

This is the *hello world* of the Stack pattern. The defining rule — *"the most
recent unclosed opener must close first"* — is exactly LIFO order. So every
opener is pushed onto a stack while it waits for its partner; on a closer, the
opener it must match is the one on top of the stack.

If a closer's top doesn't match (wrong type) or the stack is empty (nothing to
close), the string is invalid. After scanning the whole string the stack must be
empty — any leftover opener never got closed.

## Pseudocode

    function isValid(s):
        create an empty stack

        for each character c in s:
            if c is an opener ('(' , '[' , '{'):
                push c onto the stack
            else:                                   # c is a closer
                if stack is empty:
                    return false                    # nothing to match against
                open = pop the top of the stack
                if open and c are not a matching pair:
                    return false

        return true if the stack is empty, else false

## Java Solution

```java
import java.util.*;

class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }
                char open = stack.pop();
                if (!matches(open, c)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    private boolean matches(char open, char close) {
        return (open == '(' && close == ')')
            || (open == '[' && close == ']')
            || (open == '{' && close == '}');
    }
}
```

We use `Deque<Character>` backed by `ArrayDeque` because it is the modern, faster
LIFO container (the legacy `Stack` class is synchronized and discouraged). Openers
are pushed immediately with no checks. Closers must check two failure modes: an
empty stack (closer with no opener) and a type mismatch. The `matches` helper
pairs each opener with exactly one closer so the logic reads as a table rather
than nested `if`s. Finally we return `stack.isEmpty()` rather than `true`, because
a leftover opener like `"("` otherwise passes every closer check yet is still
invalid.

## Complexity

    Time:  O(n)  -- each character is pushed and popped at most once.
    Space: O(n)  -- in the worst case (all openers) the stack holds every character.

## Dry-Run

Step-by-step on `s = "([)]"` (expected `false`):

| Step | char | action | stack (bottom -> top) | result so far |
|-----:|:----:|--------|------------------------|---------------|
| 1 | `(` | opener -> push | `[(` | — |
| 2 | `[` | opener -> push | `[([` | — |
| 3 | `)` | closer, pop top = `[` | `[(` | `[` vs `)` mismatch -> return **false** |

The algorithm stops at step 3 and returns `false`. Notice the top of the stack
`[` is not the partner of `)`, which is exactly the violation the rules describe.

Dry-run on `s = "{[]}"` (expected `true`) for contrast:

| Step | char | action | stack (bottom -> top) |
|-----:|:----:|--------|------------------------|
| 1 | `{` | push | `{` |
| 2 | `[` | push | `{ [` |
| 3 | `]` | pop `[`, matches -> ok | `{` |
| 4 | `}` | pop `{`, matches -> ok | (empty) |

End: stack empty -> return **true**.

## Common mistakes

- Returning `true` at the end unconditionally. You must return `stack.isEmpty()`
  — a single `"("` matches no closer but never triggers a mismatch either.
- Calling `pop` / `peek` on an empty stack before the guard. Always check
  `isEmpty()` first; otherwise the closer of `")"` on an empty input throws.
- Comparing `char` values with `==` is fine, but checking String equality on
  single-character substrings needs `.equals(...)`. Prefer `charAt` so you stay
  in `char` territory.
- Treating brackets as interchangeable. Each closer matches exactly one opener;
  a single `matches` table avoids tangled `if/else` chains.

## Related problems

- [0022 - Generate Parentheses](../0022-generate-parentheses/) — build valid
  strings instead of checking them; open/close counters mirror this stack.
- [0150 - Evaluate Reverse Polish Notation](../0150-evaluate-reverse-polish-notation/) —
  same LIFO discipline, applied to operands and operators.
- [0739 - Daily Temperatures](../0739-daily-temperatures/) — the monotonic-stack
  cousin: the stack again holds elements "waiting for a partner".
