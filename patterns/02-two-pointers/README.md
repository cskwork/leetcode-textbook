# Pattern 2 - Two Pointers

## What the pattern is

A **pointer** is just a cursor — an index (or object reference) that marks a
position in a structure. The Two Pointers pattern uses **two** cursors and moves
them through the input. Because each pointer only travels in one direction, the
two together visit at most ~2n positions total. That collapses a naive nested
loop from O(n^2) down to O(n), usually with O(1) extra space.

The pattern earns its own section because an enormous family of array / string /
linked-list problems become trivial once you ask: *"where do I put the two
pointers, and what comparison decides which one moves?"*

## When it applies (trigger signals)

Reach for Two Pointers when the problem statement or input shows any of these:

| Signal | Example phrasing |
|---|---|
| **Sorted array** | "given a sorted array", "non-decreasing order" |
| **Find a pair / triplet** | "two numbers that sum to target", "three numbers that sum to 0" |
| **In-place modify** | "remove duplicates", "move zeroes to the end", "do not allocate extra space" |
| **Palindrome** | "is this string a palindrome", "valid after deleting at most one char" |
| **Range / area** | "container with most water", "two lines that hold the most water" |
| **Linked list** | "middle of list", "cycle", "nth from end" — slow/fast variant (see Pattern 6) |

A single unsorted array does **not** rule the pattern out — you can often sort
first (O(n log n)) and then run two pointers, which still beats O(n^2).

## The two variants

### Variant A - Pointers at both ends ("opposite ends")

Both pointers start at opposite boundaries and walk **toward each other**. Use
this when the input is sorted (or can be sorted), or when you are comparing
symmetric positions (palindrome, squares of a sorted array).

```text
function twoPointersOppositeEnds(array):
    set left to first index
    set right to last index
    while left < right:
        examine the pair (array[left], array[right])
        if they meet the goal:
            record or return the answer
        decide which pointer to move:
            if we need a "bigger" pair:  move left forward
            if we need a "smaller" pair: move right back
            otherwise move toward the more promising side
    return whatever was recorded
```

The key decision is *which pointer moves*. The rule of thumb: **change the
pointer whose change could improve the answer**. In Two Sum II on a sorted
array, if the sum is too small, only advancing `left` (the bigger values live
ahead) can help. In Container With Most Water, you move the shorter line,
because moving the taller one can only shrink the area.

### Variant B - Fast and slow ("read and write")

Both pointers start at the **same** end. One (`fast` / `read`) scans every
element; the other (`slow` / `write`) only advances when a condition holds. Use
this for in-place transforms: partitioning, removing duplicates, moving zeroes.

```text
function fastSlow(array):
    set write to first index
    for read from first index to last index:
        if array[read] satisfies the keep-condition:
            copy array[read] into array[write]
            advance write
    fill the remainder (e.g. with zeroes) if the problem requires it
```

`read` always moves forward; `write` only catches up when we keep an element.
After the loop, the prefix `[0 .. write)` holds exactly the kept elements.

## Problems in this section

| # | LC | Problem | Difficulty | One-line teaser |
|---|----|---------|-----------|-----------------|
| 9 | 125 | [Valid Palindrome](./0125-valid-palindrome/) | Easy | Opposite-end pointers skip non-letters and compare. |
| 10 | 167 | [Two Sum II - Sorted Array](./0167-two-sum-ii/) | Medium | Opposite ends on a sorted array; sum too small -> move left. |
| 11 | 15 | [3Sum](./0015-3sum/) | Medium | Fix one element, then two-pointer the rest; skip duplicate triplets. |
| 12 | 11 | [Container With Most Water](./0011-container-with-most-water/) | Medium | Opposite ends; always move the shorter line. |
| 13 | 977 | [Squares of a Sorted Array](./0977-squares-of-a-sorted-array/) | Easy | Opposite ends; write largest square into the result from the back. |
| 14 | 283 | [Move Zeroes](./0283-move-zeroes/) | Easy | Fast/slow: write non-zeroes, then pad the tail with zeroes. |

Work them in that order. The first five are all Variant A (opposite ends); the
last introduces Variant B (fast/slow).

## Common pitfalls

- **Off-by-one on the meet point.** Writing `while left <= right` when the
  pointers should never point at the same element (Two Sum, Container) double-
  counts the middle. Use `while left < right` for pair problems; use
  `while left <= right` only when a single center element is legitimate
  (Squares of a Sorted Array, where the middle element is a real value to
  place).
- **Skipping the dedup step in 3Sum.** After recording a triplet you must
  advance both pointers **past equal neighbours** before continuing, otherwise
  you emit the same triplet multiple times. This is the #1 reason 3Sum "almost
  works" but fails on inputs like `[-2,0,0,2,2]`.
- **Moving the wrong pointer.** On a sorted array the comparison must map to the
  *correct* direction: too-small sum -> move `left` (need bigger); too-large ->
  move `right`. In Container With Most Water, move the **shorter** line — moving
  the taller one can never increase area.
- **Forgetting the 1-indexed return (LC 167).** LeetCode asks for 1-based
  indices; returning the raw 0-based pointers fails every test.
- **Mutating while iterating / not staying in-place (LC 283).** A beginner
  often builds a second array. The fast/slow form keeps O(1) extra space and is
  required by the problem.
- **Char-class confusion (LC 125).** "Alphanumeric" means letters **and**
  digits; use the standard `isLetterOrDigit` check, and compare case-insensitively.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what is the core idea of the Two Pointers pattern?

<details><summary>Show answer</summary>

Use two cursors that each only move in one direction, so together they visit at most about 2n positions. That collapses a nested O(n^2) loop down to O(n), usually with O(1) extra space.

</details>

**Q2 (pattern recognition).** New problem: "given a sorted array, remove the duplicates in-place so each value appears once, returning the new length." Which variant fits?
- a) Variant B (fast/slow): `read` scans, `write` advances only when the value differs from the last kept one
- b) Variant A (opposite ends): `left` and `right` walk toward each other
- c) Neither -- this needs a hash set

<details><summary>Show answer</summary>

**(a)** -- this is the Move-Zeroes skeleton with "!= 0" swapped for "differs from the previous kept value". Opposite-end pointers do not help because the duplicates sit next to each other, not at the boundaries.

</details>

**Q3 (pattern recognition).** New problem: "return true if the string can be a palindrome after deleting at most one character." Which approach?
- a) Opposite-end pointers; on the first mismatch, try skipping the left char OR the right char and check whether either side is a plain palindrome
- b) Fast/slow read/write pointers
- c) Sort the string, then compare ends

<details><summary>Show answer</summary>

**(a)** -- it is a direct extension of the Valid Palindrome opposite-end compare, with one "budget" deletion spent at the first mismatch. Sorting destroys the character order a palindrome depends on, so (c) is wrong.

</details>

**Q4 (apply).** Trace Two Sum II on `numbers = [1,3,4,5,7]`, `target = 9`. What is returned?
- a) `[3,4]`
- b) `[2,3]`
- c) `[1,5]`

<details><summary>Show answer</summary>

**(a)** -- step 1: 1+7=8 < 9 -> `left++`. step 2: 3+7=10 > 9 -> `right--`. step 3: 3+5=8 < 9 -> `left++`. step 4: 4+5=9 == 9 -> return `[left+1, right+1] = [3,4]`. Option (b) is the 0-based trap; (c) lists values, not indices.

</details>

**Q5 (design).** Sketch (in words, not code) how to find the pair in a sorted array whose sum is CLOSEST to a target -- not necessarily equal -- and return that closest sum.

<details><summary>Show answer</summary>

Reuse the Two Sum II opposite-ends skeleton unchanged. Track the best (closest) sum seen. At each step, if the current sum is closer to the target than the best, update the best; then move exactly as in Two Sum II -- sum below target moves `left` (toward bigger), sum above moves `right` (toward smaller) -- because stepping toward the target is also stepping toward the closest. When the pointers meet, return the best.

</details>

---

With those in mind, open [0125-valid-palindrome](./0125-valid-palindrome/) and start.
