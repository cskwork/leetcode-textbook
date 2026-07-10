# 10 - Glossary (Plain-English Definitions)

Every technical term used anywhere in this book, defined in one sentence with a concrete example.
When a problem README uses one of these terms for the first time, it links here.

If a term is missing, open the problem's README and look at the **Concepts used** callout at the
top -- that lists the prerequisite ideas in plain English.

---

## A - The data structures

### Array
A row of numbered slots holding values, accessed by position (index) in O(1) time.
Example: `[2, 7, 11, 15]` -- slot 0 holds 2, slot 1 holds 7, etc.

### Hash map (a.k.a. hash table, dictionary)
A data structure that stores **key -> value** pairs and can look up a key in O(1) average time.
Like a coat-check room: you hand in a coat (key), get a ticket, and retrieve it instantly later.
In Java: `HashMap<K,V>`.

### Hash set
A hash map with only keys (no values) -- used to answer "have I seen X before?" in O(1).
In Java: `HashSet<E>`.

### Linked list
A chain of nodes; each node holds one value and a pointer to the next node. You can only walk
forward (or backward in a *doubly* linked list). Inserting/removing at a known position is O(1);
finding position k is O(k).

### Stack
A "last-in-first-out" (LIFO) container: the most recent item added is the first one removed.
Think of a stack of plates -- you add and remove from the top.
In Java: `Deque<E> stack = new ArrayDeque<>();` (use `push` / `pop`).

### Queue
A "first-in-first-out" (FIFO) container: the earliest item added is the first one removed.
Think of a line at a store -- first come, first served.
In Java: `Queue<E> q = new ArrayDeque<>();` (use `offer` / `poll`).

### Heap / Priority Queue
A tree-like structure that always gives back the smallest (min-heap) or largest (max-heap) item
in O(1), with O(log n) insert and remove. Think of a hospital triage: the most urgent patient is
always seen next, regardless of arrival order.
In Java: `PriorityQueue<E>` (min-heap by default).

### Tree
A hierarchy of nodes: one **root** at the top, every node has zero or more **children**, and no
node is its own ancestor (no cycles). A *binary tree* allows at most 2 children per node.

### Binary search tree (BST)
A binary tree where, for every node, all values in its **left** subtree are smaller and all values
in its **right** subtree are larger. This ordering lets you find any value in O(log n) on average.

### Trie (prefix tree)
A tree where each edge is labeled with a character, and any path from the root spells a word.
Shared prefixes share nodes, so "look up all words starting with 'ap'" is fast.

### Graph
A set of **nodes** (also called vertices) connected by **edges**. A grid is an implicit graph:
each cell is a node, its neighbors are the cells up/down/left/right. A *directed* graph has
one-way edges; an *undirected* graph has two-way edges.

---

## B - The algorithmic ideas

### Algorithm
A step-by-step recipe a computer follows to solve a problem. "Sort this list" is the problem;
quicksort is one algorithm for it.

### Time complexity (Big-O)
How the runtime grows as the input size `n` grows. O(n) means "doubling the input roughly doubles
the work"; O(1) means "constant time, regardless of input size". See
[02-complexity-cheatsheet.md](../02-complexity-cheatsheet.md).

### Space complexity
How much extra memory an algorithm uses, beyond the input. Same Big-O notation.

### Recursion
A function that calls itself to solve a smaller version of the same problem. Every recursion
needs a **base case** (the smallest version, answered directly) and a **recursive case** (break
the problem into a smaller one and recurse).
Example: to sum a list, take the first element + sum of the rest.

### Base case
The simplest input a recursive function answers without recursing further. Without one,
recursion never stops (stack overflow).

### Iteration vs recursion
Two ways to repeat work. Iteration uses loops (`for`, `while`); recursion uses self-calls.
Any recursion can be rewritten as iteration and vice versa, but one is usually cleaner.

---

## C - Searching and sorting

### Linear scan
Walk through every element once, in order. O(n). The simplest possible search.

### Binary search
A search that works on **sorted** data by repeatedly halving the search space: look at the middle;
if the target is smaller, search the left half; if larger, the right half. O(log n).
Example: finding a word in a paper dictionary -- you open the middle, then narrow to the half
that could contain the word.

### Sorting
Putting elements in order (ascending or descending). Standard sorts run in O(n log n).

### In-place
An algorithm that modifies the input directly, using only O(1) extra memory.

---

## D - Traversal techniques

### Traversal
Visiting every element of a data structure exactly once.

### DFS (Depth-First Search)
A traversal that goes **as deep as possible** before backtracking. On a tree: go down the left
branch to the bottom, then back up and try the next branch. Implemented with recursion or a stack.
Analogy: exploring a maze by always taking the next passage until you hit a dead end, then
backtracking.

### BFS (Breadth-First Search)
A traversal that visits **all neighbors at the current depth** before moving deeper. Implemented
with a queue. Analogy: rippling outward from a stone dropped in water.
BFS finds the **shortest path** in an unweighted graph; DFS does not.

### Level-order traversal
BFS on a tree: visit the root, then all depth-1 nodes, then all depth-2 nodes, etc.

### Preorder / Inorder / Postorder
Three DFS orderings on a tree:
- **Preorder**: visit node, then left subtree, then right subtree.
- **Inorder**: left subtree, then node, then right subtree. On a BST this visits values in sorted order.
- **Postorder**: left subtree, then right subtree, then node.

---

## E - Patterns and their core ideas

### Two pointers
Placing two indices into an array and moving them based on a comparison, to avoid a nested loop.
Often one at each end moving inward, or one "fast" and one "slow".

### Sliding window
Maintaining a **window** `[left..right]` over an array/string. Expand `right` to grow the window;
when the window breaks a constraint, shrink `left` until it's valid again. Each element enters
and leaves the window once, so total work is O(n).

### Monotonic stack
A stack that is always sorted (either increasing or decreasing). When a new element would break
the order, you pop elements first. Used to answer "next greater element" in O(n).

### Backtracking
A recursive search that tries choices and **undoes** them: pick option A, recurse, then undo A
and try option B. Used to generate all combinations / permutations / subsets.
Analogy: trying keys on a lock, returning each to the ring before trying the next.

### Greedy
Making the locally-best choice at each step and never revisiting it. Works only when you can
**prove** the locally-best choice is part of some optimal solution.
Counter-example: standard coin-change denominations don't always give fewest coins greedily.

### Dynamic programming (DP)
Solving a problem by combining solutions to **overlapping subproblems** -- the same small
question is needed many times, so we solve it once and reuse the answer.
Two flavors:
- **Top-down (memoization)**: recurse, but cache each answer.
- **Bottom-up (tabulation)**: solve small cases first, iteratively build to the full answer.

### Recurrence
A formula that defines an answer in terms of smaller answers to the same question.
Example: `fib(n) = fib(n-1) + fib(n-2)`.

### DP state
The set of variables that fully describe one sub-problem. For "min cost to reach stair i", the
state is just `i`. For "edit distance between first i chars of A and first j chars of B", the
state is the pair `(i, j)`.

### Subproblem
The same problem on a smaller piece of the input. The whole craft of DP is *identifying* the
subproblem -- once you have it, the recurrence usually writes itself.

### Optimal substructure
The property that "the optimal answer to the full problem can be built from optimal answers to
subproblems". DP and greedy both require this.

### Memoization
Caching the result of a function call keyed by its arguments, so the work is done only once.
In Java: usually a `HashMap` or an `int[]` filled with sentinel values.

### Tabulation
Filling a table (array or 2-D array) bottom-up so that when you need a smaller answer, it is
already computed and stored.

### Knapsack problem
A family of problems: choose items (each with weight and value) to maximize total value without
exceeding a weight capacity. **0/1 knapsack** = take each item at most once. **Unbounded
knapsack** = take each item as many times as you want. Many DP problems reduce to one of these.

---

## F - Graph-specific terms

### Connected component
A maximal group of nodes where you can reach any one from any other by following edges.
"Number of islands" = number of connected components in the grid-graph.

### Cycle
A path that returns to its starting node. Some algorithms break on cyclic graphs (e.g. naive
topological sort).

### Topological sort
An ordering of a directed graph's nodes such that every edge points forward. Only possible when
the graph has no cycle. Used for "in what order should I take these prerequisite courses?".

### Union-Find (Disjoint Set Union)
A data structure that tracks which elements are in the same group, supporting two operations in
near-O(1): `union(a, b)` (merge the groups containing a and b) and `find(a)` (which group is a in?).
Used for connectivity questions on a dynamically-changing graph.

---

## G - Misc terms you'll see

### Amortized
When an operation is sometimes expensive but cheap on average over many operations.
Example: `ArrayList.add` occasionally copies the whole array but averages O(1) per add.

### Integer overflow
When a computation's true result exceeds the maximum value an `int` can hold (about 2.1 billion),
so it wraps around to a wrong (often negative) value. Fix: use `long` for intermediate results.

### Sentinel
A special value placed at the boundary of data to simplify code (e.g. a dummy node before a
linked list's head, or a +infinity initial value in a min-finding loop).

### Two's complement
How computers represent signed integers. The leading bit is the sign; negative numbers are
stored by flipping all bits and adding 1. Matters for bit-manipulation problems because `>>` is
sign-extending (preserves the sign) while `>>>` is logical (always shifts in zeros).

### XOR (exclusive-or)
A bit operation: `a ^ b` is 1 where the bits differ, 0 where they agree. Key identity:
`x ^ x = 0` and `x ^ 0 = x`, so XOR-ing all elements of an array cancels out pairs and leaves
any unpaired element.

### Predicate
A function returning true/false, used as the decision in a search. Binary-search-on-answer-space
works by binary-searching for the smallest input where a monotonic predicate flips from false to true.

---

## H - Mental models this book uses

### "Trust the recursion"
A phrase meaning: *assume your recursive call already returns the correct answer for smaller
inputs; now figure out how to combine it with the current step.* You do not trace every recursive
call -- you trust the contract and use the result.

### "Leap of faith"
Same idea as trust-the-recursion. Beginners often try to trace the full call stack mentally;
experienced solvers write the recursive function as if the smaller calls already work.

### "Decision tree"
A branching tree of choices. Backtracking is a walk through a decision tree where each level
represents one decision (include element X or not; pick digit D next; etc.).

### "Window"
A contiguous slice `[left..right]` of an array or string. Sliding-window algorithms grow and
shrink this slice to find the answer.

### "Invariant"
A condition that is **always true** at the start of every loop iteration. Stating the invariant
clearly is how you prove a loop correct. Example: in binary search, "the target, if present, is
always in [lo, hi]".

---

## When this glossary is not enough

If a term in a problem README still feels unclear after checking here, the problem's **Concepts
used** callout (at the top of its README) restates the specific ideas that problem needs, in the
context of that problem. Read that first.
