# How To Use This Book

Five minutes here saves you hours later. Promise.

This book teaches 100 LeetCode problems, but its real product is the ~16 reusable
**patterns** behind them. Finish the book and a brand-new problem should feel like
"oh, this is a Sliding Window with one twist" — not a blank page.

## How a lesson works

Open any Problem Lesson and it plays **one small step at a time**: Warm-up, then
the problem, then intuition, pseudocode, Java, complexity, a dry-run, common traps,
and where to go next. A progress bar at the top shows how far you are; the big
**Continue** button takes you forward. Most steps take a few minutes.

A few things happen on their own:

- Opening a lesson and moving forward marks it **Studying**. Pressing
  **Finish lesson** on the last step marks it **Completed** — and yes, there is
  confetti.
- Finishing a lesson earns **XP**, and studying on consecutive days builds a
  **streak**. Both live only in your browser (device-local, no accounts, no
  tracking) — they exist to make small daily sessions feel as rewarding as they
  actually are.
- Your place is saved. Leave mid-lesson, come back tomorrow, and you resume on
  the same step.

Prefer everything on one page? Press **One page** in the lesson's top bar. The
whole lesson renders as a single scroll with a station rail. Your choice is
remembered.

## The one rule

> **Never read the Java before you understand the pseudocode.**

Java is dense. A beginner reading Java sees syntax noise — generics, `int[]`
declarations, `HashMap<Integer,Integer>` boilerplate — and misses the algorithm
underneath. Pseudocode strips that noise away so the *idea* is visible. Once the
idea is clear, the Java writes itself. This is why the steps come in the order
they do; resist the urge to skip ahead.

## The 20-minute rule

Before reading a lesson's solution steps, spend up to 20 minutes trying the
problem yourself. If you solve it, compare with the book. If you don't, read
only the **Intuition** step, then try again for another 15 minutes. Only then
continue to pseudocode and Java.

Why 20 minutes? Shorter and you train yourself to give up. Longer and you waste
time. Twenty minutes is the sweet spot where struggle produces learning.

## How to recognize a pattern (the meta-skill)

When you see a new problem, ask these diagnostic questions **in order**:

1. **Is the data sorted, or can I sort it?**
   - Yes -> *Two Pointers* or *Binary Search*.
2. **Am I looking for a contiguous subarray / substring with some property?**
   - Yes -> *Sliding Window*.
3. **Do I need to count, check existence, or group items?**
   - Yes -> *Arrays & Hashing*.
4. **Does the problem involve pairs/triples, or reversing order?**
   - "recent / next greater / valid pair" -> *Stack*.
5. **Is it a tree or graph traversal?**
   - Tree -> *Trees* pattern. Graph -> *Graphs* pattern.
6. **Does the problem ask "how many ways" or "best/worst over all possibilities"?**
   - Yes -> *Dynamic Programming*.
7. **Does it ask for all combinations / permutations / subsets?**
   - Yes -> *Backtracking*.
8. **"Top K", "Kth largest", scheduling, merging sorted streams?**
   - Yes -> *Heap / Priority Queue*.
9. **Word prefix lookups?**
   - Yes -> *Tries*.
10. **Start/end pairs, meetings, ranges?**
    - Yes -> *Intervals*.
11. **Can I make the locally-best choice at each step and trust it globally?**
    - Yes -> *Greedy*.
12. **Flags, parity, power-of-two, "without extra space"?**
    - Yes -> *Bit Manipulation*.

These are heuristics, not laws. After ~30 problems the recognition becomes
automatic.

## The checkpoint quizzes

Inside each lesson you will meet two short **Checkpoint** quizzes — one after
Intuition, one after the Dry-run. Each pattern's overview page ends with a longer
**Pattern Mastery Quiz**. Questions ramp from easy recall to "adapt the idea", so
you can always answer the first one and are stretched by the last.

Use them well:

1. **Commit to a guess before pressing "Show answer".** A wrong guess you correct
   yourself sticks far better than a right answer you merely read.
2. **Checkpoint A checks the concept** (why does this pattern fit?).
   **Checkpoint B checks execution** (can you trace a new input, spot an edge
   case, adapt the idea?). Trip on A: re-read Intuition. Trip on B: redo the
   Dry-run with your own numbers.
3. **The Pattern Mastery Quiz is your gate to the next pattern.** If you can
   answer its pattern-recognition questions, move on. If not, re-solve two
   problems in the section from memory first.

Retrieval practice — forcing yourself to recall before checking — is the single
most effective way to make a pattern stick. The quizzes are part of the lesson,
not a test; revealing an answer is always your call.

## How to use the tests

Every problem folder has `SolutionTest.java`. Run it:

```bash
cd patterns/01-arrays-hashing/0217-contains-duplicate
javac Solution.java SolutionTest.java && java SolutionTest
```

You should see:

```
All tests passed.
```

If a test fails, **read the failing case**. The test file tells you exactly which
input broke. LeetCode itself uses hidden tests; the tests here are the same
public cases LeetCode shows you, plus a couple of edge cases per problem.

## Pace yourself (spaced repetition)

Do not solve 100 problems in a week. You will forget them. A streak of small
daily sessions beats a weekend binge every time. Recommended pace:

- **Week 1-2:** Patterns 1-4 (Arrays/Hashing, Two Pointers, Sliding Window, Stack). ~25 problems.
- **Week 3-4:** Patterns 5-9 (Binary Search, Linked List, Trees, Tries, Heap). ~32 problems.
- **Week 5-6:** Patterns 10-13 (Backtracking, Graphs, Greedy, Intervals). ~25 problems.
- **Week 7-8:** Patterns 14-16 (1-D DP, 2-D DP, Bit Manipulation). ~18 problems.

After finishing a pattern, **re-solve 2 of its problems from memory 3 days
later.** That is what makes patterns stick.

## When you get stuck

1. **Read the "Warm-up" step first.** It lists every prerequisite idea in plain
   English with a link to the glossary. If a concept is unfamiliar, read its
   glossary entry before continuing.
2. Re-read the **pattern overview** (the `patterns/XX-name/README.md` page). It
   lists the pattern's trigger signals and a template.
3. Read only the **Intuition** step of the problem, then try again.
4. Only as a last resort, read pseudocode + Java together.
5. After seeing the solution, **close the book and re-type the solution from
   memory.** Typing it from understanding (not copying) is what cements it.

## Where to look up unfamiliar terms

Every technical term in this book is defined in plain English in
[docs/10-glossary.md](./docs/10-glossary.md). When a lesson uses a term for the
first time, it links to the glossary entry. Bookmark it — you will visit often
in the first few patterns.

## What this book does NOT cover

- Hard problems (by design — see the main README).
- Concurrency / multithreading LeetCode problems.
- SQL problems.
- System design.

This is a *patterns and algorithms* book for the coding interview's algorithmic
portion.

---

Next: [01-patterns-overview.md](./01-patterns-overview.md)
