# The LeetCode Textbook (Java)

Master LeetCode in tiny steps. This is a pattern-first, beginner-friendly guide to
100 problems from Easy to Medium, and every one of them is taught the same
gentle way: **Intuition -> Pseudocode -> Java -> Complexity -> Dry-run**, one
short step at a time.

The goal is not to memorize 100 answers. It is to learn ~16 reusable patterns so
any new problem becomes recognizable — the way experienced solvers see "oh,
that's a Sliding Window" before they write a line of code.

> **Verified:** 100/100 problems compile and pass all tests (679 test cases total).
> Run `javac Solution.java SolutionTest.java && java SolutionTest` inside any problem folder.

Full curriculum table: [PROBLEM-MAP.md](./PROBLEM-MAP.md).

---

## Why this book exists

Most beginners fail LeetCode for one reason: they treat every problem as a
brand-new puzzle. Experienced solvers do the opposite — they map a problem onto
a known **pattern** in seconds, then write code. This book teaches that mapping
explicitly, in pieces small enough that you never feel buried.

The structure is borrowed from the proven *NeetCode 150* / *Blind 75* roadmap,
expanded to exactly 100 problems organized into 16 patterns, ordered
easiest-first.

---

## How to read this book

On the website, every Problem Lesson plays step by step: a progress bar, one
idea per screen, a **Continue** button, and a small celebration when you finish.
Finishing lessons earns XP and daily study builds a streak — both stored only in
your browser. Prefer a single page? One tap switches any lesson to a classic
scroll view. Reading the Markdown on GitHub works exactly the same, just without
the interface.

1. Start with [00-how-to-use-this-book.md](./00-how-to-use-this-book.md) (5 minutes).
2. Skim [01-patterns-overview.md](./01-patterns-overview.md) — the pattern-recognition cheat sheet.
   Bookmark it. You will return constantly.
3. If your Java is rusty, read [03-java-crash-course.md](./03-java-crash-course.md).
4. Open [patterns/01-arrays-hashing/](./patterns/01-arrays-hashing/) and read the pattern's
   `README.md` first (it explains *when* the pattern applies and gives a template).
5. Then do the problems in order inside that folder. For each problem:
    - Try to solve it yourself for 20 minutes first.
    - Read the **Intuition** step, then answer **Checkpoint A** before reading on.
    - Read the **Pseudocode**. Make sure you understand the algorithm language-agnostically.
    - Read the **Java** solution. Compile and run the test.
    - Read the **Dry-run** to see the algorithm executed on a concrete input.
    - Answer **Checkpoint B** (trace + edge-case + adapt questions) before moving on.

The rule: **never read the Java before you understand the pseudocode.** Pseudocode is the bridge
between human thinking and machine code. Skipping it is why beginners get stuck memorizing syntax
instead of learning patterns. Each pattern's README also ends with a **Pattern Mastery Quiz** --
treat it as the gate before moving to the next pattern. Quiz answers are collapsible: attempt
first, then reveal.

---

## Pattern Map (the entire book on one page)

| # | Pattern | Problems | Difficulty ramp | What you learn |
|---|---------|----------|-----------------|----------------|
| 1 | [Arrays & Hashing](./patterns/01-arrays-hashing/) | 8 | Easy | Sets, maps, counting |
| 2 | [Two Pointers](./patterns/02-two-pointers/) | 6 | Easy->Med | In-place array ops |
| 3 | [Sliding Window](./patterns/03-sliding-window/) | 6 | Easy->Med | Range queries |
| 4 | [Stack](./patterns/04-stack/) | 5 | Easy->Med | LIFO, monotonic stack |
| 5 | [Binary Search](./patterns/05-binary-search/) | 7 | Easy->Med | O(log n) on sorted data |
| 6 | [Linked List](./patterns/06-linked-list/) | 7 | Easy->Med | Pointer manipulation |
| 7 | [Trees](./patterns/07-trees/) | 9 | Easy->Med | Recursion, BFS/DFS |
| 8 | [Tries](./patterns/08-tries/) | 4 | Med | Prefix trees |
| 9 | [Heap / Priority Queue](./patterns/09-heap/) | 5 | Easy->Med | Top-K, scheduling |
| 10 | [Backtracking](./patterns/10-backtracking/) | 6 | Med | Permutations, subsets |
| 11 | [Graphs](./patterns/11-graphs/) | 9 | Easy->Med | BFS, DFS, union-find |
| 12 | [Greedy](./patterns/12-greedy/) | 6 | Easy->Med | Local optimal -> global |
| 13 | [Intervals](./patterns/13-intervals/) | 4 | Med | Merge, sweep |
| 14 | [1-D Dynamic Programming](./patterns/14-1d-dp/) | 8 | Easy->Med | Memoization, tabulation |
| 15 | [2-D Dynamic Programming](./patterns/15-2d-dp/) | 7 | Med | Grid + string DP |
| 16 | [Bit Manipulation](./patterns/16-bit-manipulation/) | 3 | Easy->Med | XOR, masks |

**Total: 100 problems across 16 patterns.**

---

## Folder layout

```
leetcode-textbook/
+- README.md                          <- you are here
+- 00-how-to-use-this-book.md
+- 01-patterns-overview.md
+- 02-complexity-cheatsheet.md
+- 03-java-crash-course.md
+- patterns/
|   +- 01-arrays-hashing/
|   |   +- README.md                  <- pattern intro + template
|   |   +- 217-contains-duplicate/
|   |   |   +- README.md              <- problem + pseudocode + walkthrough
|   |   |   +- Solution.java
|   |   |   +- SolutionTest.java
|   |   +- 242-valid-anagram/
|   |   +- ...
|   +- 02-two-pointers/
|   +- ...
+- docs/
    +- changelog-2026-07-05.md
```

Every problem folder is self-contained: read `README.md`, open `Solution.java`,
run `SolutionTest.java` to verify.

---

## How to run a problem's tests

Each `SolutionTest.java` uses a plain `main` method (no JUnit dependency needed).
From inside any problem folder:

```bash
javac Solution.java SolutionTest.java && java SolutionTest
```

If you see `All tests passed.` the solution is correct on the included cases.
Java 11+ is required (`javac -version` to check).

---

## Difficulty philosophy

This book goes **Easy -> Medium**. It deliberately excludes Hard problems because:
- 90% of interview questions are Easy/Medium.
- Patterns transfer upward: a Hard problem is usually a Medium pattern with an extra constraint.
- Mastering 100 Easy/Medium problems beats skimming 30 Hard ones.

Once you finish this book, the standard next step is the *NeetCode 150* Hard problems or
*LeetCode 75*.

---

## Credits

Curriculum structure adapted from NeetCode (neetcode.io) and the Blind 75 list.
All solutions, pseudocode, and teaching notes written fresh for this book.

---

*Built July 2026.*
