# Pattern 1 - Arrays & Hashing

This is the first and most-used pattern in the book. Master it before moving on. Almost every
interview set opens with one of these problems because the techniques here (hash maps and hash
sets) underpin solutions in every later pattern.

---

## What the pattern is

**Trade memory for time.** A brute-force check "does X already exist?" or "how many times has X
appeared?" costs O(n) per question if you scan the array. A hash structure answers the same
question in O(1) average time, at the cost of O(n) extra space.

Concretely, the pattern gives you two tools:

- A **HashSet** when you only need membership ("have I seen this value?").
- A **HashMap** when you need to attach data to each value ("what index did I first see this
  at?", "how many times did it appear?", "which group does it belong to?").

One pass through the input, one O(1) lookup per element, one O(1) insert. Total: O(n) time.

---

## When it applies -- trigger signals

Reach for this pattern when the problem statement contains any of these words:

| Trigger signal | Example problem | Which tool |
|---|---|---|
| "duplicate", "appears twice", "any value ... more than once" | Contains Duplicate (217) | HashSet |
| "two numbers that sum to", "pair", "complement" | Two Sum (1) | HashMap (value -> index) |
| "anagram", "same characters", "rearrange" | Valid Anagram (242), Group Anagrams (49) | HashMap / int[26] |
| "frequency", "how many times", "count" | Top K Frequent (347) | HashMap (value -> count) |
| "group ... by", "cluster" | Group Anagrams (49) | HashMap (key -> list) |
| "every other element", "product of all except self" | Product Except Self (238) | prefix/postfix pass |
| "valid", "no repeats in row/col/box" | Valid Sudoku (36) | HashSet per group |
| "consecutive", "sequence" (must be O(n)) | Longest Consecutive (128) | HashSet of all values |

If the problem gives you an **unsorted** array and asks a question about **existence, counting,
or grouping**, this is almost always the right pattern. If the array is **sorted**, consider
Two Pointers (Pattern 2) or Binary Search (Pattern 5) first -- they avoid the extra space.

---

## General pseudocode template

This single shape solves six of the eight problems in this section. Learn it, then adapt the
lookup key and the stored value to each problem.

```
function solve(input):
    create an empty hash structure

    for each element x in input:
        key  <- transform(x)            # the value, count, signature, or complement to look up
        if key is already in the hash structure:
            use the stored info to answer        # found a pair, a duplicate, a group, ...
            (optionally return early)
        otherwise:
            store info about x under key         # index, count, list, ...
            (or store x under transform(x))

    return the accumulated answer
```

Variants you will see:

- **Check-then-store** (Two Sum, Contains Duplicate): look up *before* inserting so you do not
  match an element against itself.
- **Store-then-group** (Group Anagrams): every element lands in a bucket keyed by its signature.
- **Count-then-bucket** (Top K Frequent): two passes -- count into a map, then bucket by count.
- **Prefix/postfix pass** (Product Except Self): two linear sweeps replace a hash entirely; the
  "memory" lives in a running product instead of a map.
- **Set-of-all + sequence-start test** (Longest Consecutive): load everything into a set, then
  only expand from values that begin a sequence.

---

## Problems in this section

Eight problems, ramping Easy -> Medium. Do them in order -- each one introduces one new idea.

| # | Folder | Problem | Difficulty | One-line teaser |
|---|---|---|---|---|
| 1 | [0217-contains-duplicate](./0217-contains-duplicate/) | Contains Duplicate | Easy | Is any value repeated? A HashSet beeps on the second sighting. |
| 2 | [0242-valid-anagram](./0242-valid-anagram/) | Valid Anagram | Easy | Do two strings have the same letter counts? Add for one string, subtract for the other. |
| 3 | [0001-two-sum](./0001-two-sum/) | Two Sum | Easy | Find two indices summing to target; store each value's index as you go. |
| 4 | [0049-group-anagrams](./0049-group-anagrams/) | Group Anagrams | Medium | Sort each word to get a key; group all words that share a key. |
| 5 | [0347-top-k-frequent](./0347-top-k-frequent/) | Top K Frequent Elements | Medium | Count with a map, then bucket values by frequency for an O(n) top-K. |
| 6 | [0238-product-of-array-except-self](./0238-product-of-array-except-self/) | Product of Array Except Self | Medium | Multiply prefix products by postfix products -- no division allowed. |
| 7 | [0036-valid-sudoku](./0036-valid-sudoku/) | Valid Sudoku | Medium | One HashSet per row, column, and 3x3 box catches every duplicate. |
| 8 | [0128-longest-consecutive-sequence](./0128-longest-consecutive-sequence/) | Longest Consecutive Sequence | Medium | Drop all values into a set; only expand from sequence starts. |

---

## Common pitfalls of the pattern

Hashing is powerful but has sharp edges. Beginners hit these repeatedly:

- **Hash collisions and `equals`/`hashCode` correctness.** For built-in types (`Integer`,
  `String`) Java handles this. For your own keys (rare here), you must override both methods or
  the map/set will misbehave. In this section every key is a built-in, so you are safe -- but
  remember it for later patterns.

- **Autoboxing overhead.** `Map<Integer, Integer>` silently boxes every `int` key and value into
  an `Integer` object. For the tiny inputs in this book it is irrelevant, but on huge inputs it
  costs memory and time. When performance matters, prefer an `int[]` indexed by value (see the
  `int[26]` trick in Valid Anagram, or bucket arrays in Top K Frequent).

- **Mutating a collection while iterating it.** Calling `map.put(...)` or `set.remove(...)`
  inside a `for-each` over the same collection throws
  `ConcurrentModificationException`. Build a *new* collection, or collect changes and apply
  them after the loop. (You will not hit this in the eight problems below, but it bites in
  Patterns 3, 11, and 14.)

- **Check-then-store ordering.** In Two Sum and Contains Duplicate you must look up *before*
  inserting the current element, otherwise the element matches itself. Get this backwards and
  every self-pair looks like a hit.

- **Returning `null` from `map.get`.** Assigning `map.get(k)` straight into an `int` throws a
  `NullPointerException` if the key is absent, because unboxing `null` fails. Use
  `containsKey(k)` first, or `getOrDefault(k, default)`.

- **Assuming order.** `HashMap` has no iteration order. If a problem allows any valid ordering of
  the answer (Top K Frequent, Group Anagrams), your test must compare as a *set*, not as an
  ordered list, or it will be flaky.

- **`==` on `String`.** Always compare strings with `.equals(...)`. Two equal strings from
  different sources are usually different object references, so `==` returns `false`. This ruins
  anagram keying.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what is the core trade this pattern makes?

<details><summary>Show answer</summary>

Trade memory for time: spend O(n) extra space on a hash structure so each "have I seen / how many / where" question is answered in O(1) instead of O(n).

</details>

**Q2 (pattern recognition).** A new problem: "given a list of words, find all words that appear more than once." Which tool fits best?
- a) A hash set of the words
- b) A hash map word -> count, then scan for counts > 1
- c) Sorting then binary search

<details><summary>Show answer</summary>

**(b)** -- you need the COUNT, not just existence, so a map word -> count is the right shape. (a) can only say "seen / not seen", losing the count.

</details>

**Q3 (pattern recognition).** Problem: "is every value in this array distinct?" Which is the most direct tool?
- a) A hash set; if any `add` returns false, there's a duplicate
- b) Nested loops comparing all pairs
- c) A heap

<details><summary>Show answer</summary>

**(a)** -- a set answers existence in O(1); the boolean return of `add` detects the first repeat. This is literally Contains Duplicate.

</details>

**Q4 (apply).** You're solving Top K Frequent with the bucket method on `nums = [5,5,5,5,6,7]`, `k = 1`. Which slot holds the answer value, and what is returned?
- a) Slot 4 holds 5; returns `[5]`
- b) Slot 1 holds 5; returns `[5]`
- c) Slot 2 holds 5; returns `[5]`

<details><summary>Show answer</summary>

**(a)** -- 5 appears 4 times so it lands in `bucket[4]`; the top-down scan hits slot 4 first and returns `[5]`.

</details>

**Q5 (design).** Sketch (in words, not code) how to solve "first non-repeating character in a string" using ideas from this pattern.

<details><summary>Show answer</summary>

Two passes: pass 1 builds a char -> count map; pass 2 walks the string again and returns the first character whose count is 1. The map is the hashing habit; the second pass preserves original order to find the FIRST unique.

</details>

---

With those in mind, open [0217-contains-duplicate](./0217-contains-duplicate/) and start.
