# 0017 - Letter Combinations of a Phone Number

**Difficulty:** Medium
**Pattern:** Backtracking
**LeetCode:** https://leetcode.com/problems/letter-combinations-of-a-phone-number/

## Concepts used

- **Recursion** -- a function that calls itself on a smaller version of the problem; here "smaller" means "one fewer digit left to fill". [glossary](../../../docs/10-glossary.md#recursion)
- **Backtracking** -- try a choice (pick a letter), recurse, then UNDO it before trying the next letter. [glossary](../../../docs/10-glossary.md#backtracking)
- **Decision tree** -- a branching picture of all the choices, one level per digit. [glossary](../../../docs/10-glossary.md#decision-tree)

## Problem

Given a string `digits` containing only digits `2-9` inclusive, return *all
possible letter combinations* that the number could represent, in any order.
A mapping of digits to letters (like on telephone buttons) is given below.
Return `0` combinations if the input is empty.

```
2 -> "abc"      3 -> "def"
4 -> "ghi"      5 -> "jkl"     6 -> "mno"
7 -> "pqrs"     8 -> "tuv"     9 -> "wxyz"
```

Signature:

    List<String> letterCombinations(String digits)

Example (verbatim from LeetCode):

    Input:  digits = "23"
    Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]

    Input:  digits = ""
    Output: []

    Input:  digits = "2"
    Output: ["a","b","c"]

## Intuition

This is backtracking stripped to its barest form, which is why it is a great
first or second problem in the pattern. The decision at each step is "pick one
of the letters mapped to the current digit"; the path is the prefix built so
far; the "done" check is "we have placed one letter per digit".

The shape is a **cartesian product** -- pick one option from group 1, cross
it with every option from group 2, and so on. That is exactly the recursion
tree backtracking produces when "the choices at step `i`" depends only on `i`
and not on what was picked before. There is no `used[]` mask, no
start-index juggling, no duplicate-skipping -- the cleanest possible instance
of choose / explore / un-choose.

The one fiddly bit is the digit->letters map. We keep it in a fixed-size
`String[]` indexed by the digit character: `map['2' - '2'] = "abc"`, and so
on. Indexing by `digits.charAt(i)` lets us look up the choices for slot `i`
in constant time.

The base case fires when the prefix length equals the number of digits -- at
that point we have placed one letter per digit, exactly as required.

### Checkpoint A -- One letter per digit

Pause and answer before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** At each recursion level, what is the "choice" the loop branches on?
- a) Which digit to place next
- b) Which letter (mapped to the current digit) to append
- c) Which digit to skip

<details><summary>Show answer</summary>

**(b)** -- `index` walks forward through the digits automatically (one per level); the loop inside iterates over the letters of `digits[index]`, appending one and recursing.

</details>

**Q2 (comprehend).** Why is there no `used[]` mask and no start index here (unlike Permutations and Subsets)?
- a) Because the output must be sorted
- b) Because each digit is an independent slot -- a cartesian product -- and no letter constrains the choices for another digit
- c) Because the input has no duplicates

<details><summary>Show answer</summary>

**(b)** -- every digit contributes exactly one independent slot, and the letters for one digit do not depend on what was picked for another. So there is nothing to "mark used" or "look forward" past; the skeleton is choose/explore/un-choose in its barest form.

</details>

## Pseudocode

    function letterCombinations(digits):
        if digits is empty: return empty list
        map = ["abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"]   # for 2..9
        results = empty list
        backtrack(prefix = empty, index = 0, digits, map, results)
        return results

    function backtrack(prefix, index, digits, map, results):
        if index == length(digits):               # one letter placed per digit
            append a copy of prefix to results
            return
        letters = map for digit digits[index]
        for each ch in letters:
            append ch to prefix                   # CHOOSE
            backtrack(prefix, index + 1, digits, map, results)   # EXPLORE next digit
            remove the last char of prefix        # UN-CHOOSE

Structural notes:

- `index` walks *forward* through `digits`, one position per recursion level.
  We do not loop back over earlier digits -- each digit contributes exactly
  one slot.
- The loop body iterates over the **letters** of the current digit, not over
  the digits themselves. The dimension we branch on is "which letter for this
  digit".
- Because we mutate a shared `StringBuilder` prefix, the un-choose is a
  `deleteCharAt` (mirroring Generate Parentheses from Pattern 4). Use
  `StringBuilder` rather than `String + String` so each append is `O(1)`
  rather than `O(prefix length)`.
- The empty-input case is special-cased up front -- without it, the recursion
  would record one empty string instead of zero strings.

## Java Solution

```java
import java.util.*;

class Solution {
    public List<String> letterCombinations(String digits) {
        List<String> results = new ArrayList<>();
        if (digits.isEmpty()) return results;

        String[] map = {"abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        backtrack(results, new StringBuilder(), 0, digits, map);
        return results;
    }

    private void backtrack(List<String> results, StringBuilder prefix,
                           int index, String digits, String[] map) {
        if (index == digits.length()) {
            results.add(prefix.toString());             // snapshot the builder
            return;
        }
        String letters = map[digits.charAt(index) - '2'];  // choices for this digit
        for (int i = 0; i < letters.length(); i++) {
            prefix.append(letters.charAt(i));             // CHOOSE
            backtrack(results, prefix, index + 1, digits, map);  // EXPLORE next digit
            prefix.deleteCharAt(prefix.length() - 1);     // UN-CHOOSE
        }
    }
}
```

The early `if (digits.isEmpty()) return results` is what makes the empty-input
case return `[]` rather than `[""]` -- without it, the base case fires
immediately at `index == 0 == digits.length()` and records one empty string.
`map[digits.charAt(index) - '2']` is the constant-time lookup that turns the
character `'2'..'9'` into the array index `0..7`; this is why the array holds
exactly eight entries in digit order. We pass a `StringBuilder` (not a
`String`) so each `append` is `O(1)` amortised -- if we used `prefix + ch` we
would allocate a fresh string at every frame and pay `O(prefix length)` per
append, bloating the constant factor. `prefix.deleteCharAt(prefix.length() -
1)` is the un-choose step, paired with the `append` exactly as in Generate
Parentheses; without it the prefix would leak across sibling branches and
every recorded string would be far too long.

## Complexity

    Time:  O(4^n * n) where n = |digits|. Each output string has length n, and
           there are at most 4^n of them (digit 7 and 9 map to 4 letters, the
           rest to 3). Building each one is O(n).
    Space: O(n) recursion + StringBuilder depth. Output list not counted.

## Dry-Run

Tree on `digits = "23"`. `map['2'-'2'] = "abc"`, `map['3'-'2'] = "def"`. We
write state as `prefix | index`.

```
backtrack("" | 0)
  letters for '2' = "abc"
  i=0 ch='a': append a -> backtrack("a" | 1)
                letters for '3' = "def"
                i=0 ch='d': append d -> backtrack("ad" | 2)
                              index == 2 == length -> RECORD "ad", return
                              delete d -> "a"
                i=1 ch='e': append e -> backtrack("ae" | 2) -> RECORD "ae"
                              delete e -> "a"
                i=2 ch='f': append f -> backtrack("af" | 2) -> RECORD "af"
                              delete f -> "a"
                loop ends, return
              delete a -> ""
  i=1 ch='b': append b -> backtrack("b" | 1)
                ... RECORD "bd", "be", "bf"
              delete b -> ""
  i=2 ch='c': append c -> backtrack("c" | 1)
                ... RECORD "cd", "ce", "cf"
              delete c -> ""
```

Recordings in order: `ad, ae, af, bd, be, bf, cd, ce, cf` -- nine strings
(`3 * 3`), exactly the cartesian product of `"abc"` and `"def"`. Notice that
each frame's `prefix` is back to its pre-choice value by the time its sibling
begins; that is the un-choose doing its job.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `digits = "34"`. How many combinations are produced, and which is first?
- a) 6, starting with `"dg"`
- b) 9, starting with `"dg"`
- c) 9, starting with `"ad"`

<details><summary>Show answer</summary>

**(b)** -- `'3'` maps to `"def"` (3 letters) and `'4'` to `"ghi"` (3 letters), so `3 * 3 = 9` combinations. The first picks `'d'` then `'g'`, giving `"dg"`.

</details>

**Q2 (analyze).** What does the code return for `digits = ""` (empty), and why is the early guard needed?
- a) `[""]` -- one empty combination
- b) `[]` -- zero combinations; without the guard the base case fires at once and records one empty string
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- empty input should yield zero combinations. Without the `if (digits.isEmpty()) return results` guard, the base case `index == digits.length()` (0 == 0) fires immediately and records one empty string `""`, which is wrong.

</details>

**Q3 (transfer).** Suppose a digit could map to a variable number of letters and you wanted only combinations whose total length equals a given `L`. What would change?

<details><summary>Show answer</summary>

Only the DONE check: record (and return) when `prefix.length() == L` instead of `index == digits.length()`. The loop over the current digit's letters and the append/deleteCharAt un-choose stay the same; you simply stop at the target length.

</details>

## Common mistakes

- **Returning `[""]` instead of `[]` for empty input.** Without the early
  guard, the base case `index == digits.length()` fires immediately and
  records one empty string. The problem asks for zero combinations in that
  case, so guard explicitly.
- **Using `String + ch` instead of a `StringBuilder`.** Works correctly but is
  `O(n)` per append instead of `O(1)` -- noticeable on long inputs. Always
  mutate one builder and undo with `deleteCharAt`.
- **Forgetting the un-choose.** Then the prefix keeps growing across siblings;
  every recorded string after the first is too long.
- **Indexing the map incorrectly.** `digits.charAt(index) - '2'`, not
  `digits.charAt(index) - '0'`, because the map array starts at digit `'2'`
  (digits `0` and `1` map to no letters).
- **Using `index` to walk the letters of one digit instead of across digits.**
  The outer recursion walks digits (`index + 1` per call); the inner loop
  walks the letters of the current digit. Mixing the two either produces one
  giant string or one letter per output.

## Related problems

- [0078 - Subsets](../0078-subsets/) -- the parent skeleton; this problem is
  the same choose/explore/un-choose shape with the simplest possible "what
  are my choices" rule.
- [0046 - Permutations](../0046-permutations/) -- another fixed-length
  backtracker; contrast how Permutations gates the loop with `used[]` while
  this one has no such constraint (every letter is independent).
- [0022 - Generate Parentheses](../../04-stack/0022-generate-parentheses/)
  (Pattern 4) -- the bridge problem into this pattern; same StringBuilder +
  `deleteCharAt` un-choose idiom used here.
