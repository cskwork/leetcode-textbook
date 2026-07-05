# 00 - How To Use This Book

Read this once before solving anything. Ten minutes.

## The one rule

> **Never read the Java before you understand the pseudocode.**

Java is dense. A beginner reading Java sees syntax noise -- generics, `int[]` declarations,
`HashMap<Integer,Integer>` boilerplate -- and misses the algorithm underneath. Pseudocode strips
that noise away so the *idea* is visible. Once the idea is clear, the Java writes itself.

Every problem in this book is laid out in this fixed order:

1. **Problem** -- what is being asked, in plain English.
2. **Intuition** -- the *first thought* a strong solver has. Why does this pattern fit?
3. **Pseudocode** -- language-neutral steps. Read this slowly.
4. **Java solution** -- the pseudocode, translated.
5. **Complexity** -- time and memory, with reasoning.
6. **Dry-run** -- the algorithm executed on a concrete input, line by line.

## The 20-minute rule

For each problem, spend up to 20 minutes trying it yourself **before** reading the solution.
If you solve it, compare with the book. If you don't, read the **Intuition** section only, then
try again for another 15 minutes. Only then read pseudocode + Java.

Why 20 minutes? Shorter and you train yourself to give up. Longer and you waste time. Twenty
minutes is the sweet spot where struggle produces learning.

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

These are heuristics, not laws. After ~30 problems the recognition becomes automatic.

## How to use the tests

Every problem folder has `SolutionTest.java`. Run it:

```bash
cd patterns/01-arrays-hashing/217-contains-duplicate
javac Solution.java SolutionTest.java && java SolutionTest
```

You should see:

```
All tests passed.
```

If a test fails, **read the failing case**. The test file tells you exactly which input broke.
LeetCode itself uses hidden tests; the tests here are the same public cases LeetCode shows you,
plus a couple of edge cases per problem.

## Spaced repetition

Do not solve 100 problems in a week. You will forget them. Recommended pace:

- **Week 1-2:** Patterns 1-4 (Arrays/Hashing, Two Pointers, Sliding Window, Stack). ~25 problems.
- **Week 3-4:** Patterns 5-9 (Binary Search, Linked List, Trees, Tries, Heap). ~32 problems.
- **Week 5-6:** Patterns 10-13 (Backtracking, Graphs, Greedy, Intervals). ~25 problems.
- **Week 7-8:** Patterns 14-16 (1-D DP, 2-D DP, Bit Manipulation). ~18 problems.

After finishing a pattern, **re-solve 2 of its problems from memory 3 days later.** That is
what makes patterns stick.

## When you get stuck

1. Re-read the **pattern README** (the `patterns/XX-name/README.md` file). It lists the
   pattern's trigger signals and a template.
2. Read only the **Intuition** section of the problem, then try again.
3. Only as a last resort, read pseudocode + Java together.
4. After seeing the solution, **close the book and re-type the solution from memory.** Typing it
   from understanding (not copying) is what cements it.

## What this book does NOT cover

- Hard problems (by design -- see the main README).
- Concurrency / multithreading LeetCode problems.
- SQL problems.
- System design.

This is a *patterns and algorithms* book for the coding interview's algorithmic portion.

---

Next: [01-patterns-overview.md](./01-patterns-overview.md)
