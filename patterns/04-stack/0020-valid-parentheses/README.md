# 0020 - Valid Parentheses

**Difficulty:** Easy
**Pattern:** Stack
**LeetCode:** https://leetcode.com/problems/valid-parentheses/

## Concepts used

- **Stack** — a last-in-first-out (LIFO) container: the last item you put in is the first one you take out, like a stack of plates. [glossary](../../../docs/10-glossary.md#stack)
- **Linear scan** — reading the string one character at a time, left to right, doing a small fixed amount of work per character. [glossary](../../../docs/10-glossary.md#linear-scan)

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

Think of nested Russian dolls, or a stack of boxes you tape shut before opening
the next: the box you opened **last** must be the first one you close. Brackets
behave the same way — if you write `(` then `[`, the matching `]` must come
before the `)`. This "last-in, first-out" rule is called **LIFO**, and a
[stack](../../../docs/10-glossary.md#stack) is the data structure built for it:
you add and remove only at the top, like a stack of plates.

Trace the smallest case that actually shows this, `s = "([])"`:

- Read `(` — opener, put it on the stack. Stack (bottom to top): `(`.
- Read `[` — opener, put it on top. Stack: `( [`.
- Read `]` — closer. It must match the most recent opener, the top `[`. They
  match, so remove the `[`. Stack: `(`.
- Read `)` — closer, must match the top `(`. Match, remove it. Stack: empty.

It worked because each closer grabbed the opener right beneath it. That is the
whole idea: **openers wait on the stack; a closer always pairs with the top
opener.** If the top is the wrong type, or the stack is empty when a closer
arrives (nothing to match), the string is invalid. And after reading every
character the stack must be empty — a leftover opener like the lone `(` in `"("`
never got closed, so that is invalid too.

### Checkpoint A -- Match the top

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** When a closer (`)`, `]`, `}`) arrives, which opener does it try to match against?
- a) The bottom of the stack (the oldest opener)
- b) The top of the stack (the most recent opener)
- c) Every opener on the stack, one at a time

<details><summary>Show answer</summary>

**(b)** -- LIFO order means a closer always pairs with the most recent unmatched opener, which is exactly the stack top.

</details>

**Q2 (comprehend).** Trace `s = "()"`. After both characters are read, what is on the stack and what is returned?
- a) Stack holds `(`; returns false
- b) Stack is empty; returns true
- c) Stack is empty; returns false

<details><summary>Show answer</summary>

**(b)** -- `(` is pushed; `)` pops it (the pair matches); the loop ends with an empty stack, so `return stack.isEmpty()` gives true.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `s = ")"` (a single closing bracket). What happens and what is returned?
- a) Returns false at the first character -- the stack is empty when the closer arrives
- b) Pushes `(` then returns true at the end
- c) Throws an exception

<details><summary>Show answer</summary>

**(a)** -- `)` is a closer; the `stack.isEmpty()` guard fires before any pop, so we return false without ever touching the stack.

</details>

**Q2 (analyze).** What does `isValid("")` (the empty string) return, and why?
- a) false -- an empty string has no brackets, so it is invalid
- b) true -- the loop runs zero times, then control falls through to `return stack.isEmpty()`, which is true
- c) It throws

<details><summary>Show answer</summary>

**(b)** -- zero iterations leave an empty stack, and `stack.isEmpty()` is true. By convention an empty string is valid.

</details>

**Q3 (transfer).** Suppose a fourth bracket pair `<` and `>` were added to the language. What is the smallest change to the solution?

<details><summary>Show answer</summary>

Add one line to the `matches` helper (`open == '<' && close == '>'`) and add `|| c == '<'` to the opener check. The LIFO structure is unchanged -- it only needs to recognise one more legal pair.

</details>

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
