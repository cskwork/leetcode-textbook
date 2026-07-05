# 01 - Patterns Overview (Cheat Sheet)

This is the single most important page in the book. Print it. Bookmark it. When a new problem
appears, scan the "Trigger signals" column first.

Each pattern below has its own folder under `patterns/` with a full `README.md` template.

---

## Quick decision table

| If the problem involves... | Use pattern | Section |
|---|---|---|
| Counting, existence, grouping, frequency | Arrays & Hashing | 1 |
| Sorted array, "find pair / triplet" | Two Pointers | 2 |
| "Longest / shortest subarray with property X" | Sliding Window | 3 |
| "Next greater", "valid parentheses", "recent" | Stack | 4 |
| Sorted data, "find in O(log n)" | Binary Search | 5 |
| Linked list, "reverse", "cycle", "merge" | Linked List | 6 |
| Trees, "depth", "level order", "validate" | Trees | 7 |
| "Word starts with", "autocomplete" | Tries | 8 |
| "Top K", "Kth largest", scheduling | Heap / PQ | 9 |
| "All permutations / combinations / subsets" | Backtracking | 10 |
| Grid, network, "connected", "shortest path" | Graphs | 11 |
| "Minimum steps / cost", locally-best choice | Greedy | 12 |
| Ranges, meetings, "merge intervals" | Intervals | 13 |
| "How many ways", "max/min over choices" | 1-D DP | 14 |
| 2-D grid paths, two-string edit distance | 2-D DP | 15 |
| XOR, "without extra space", power of 2 | Bit Manipulation | 16 |

---

## The 16 patterns in depth

### 1. Arrays & Hashing
- **Trigger:** "count", "find duplicate", "two-sum", "group anagrams", "frequency".
- **Core idea:** Trade memory for time. A `HashSet` makes existence-checks O(1); a `HashMap`
  makes count/group O(1) per operation.
- **Template:** Iterate once, put each element into a hash structure, look up as you go.
- **Cost:** O(n) time, O(n) space.
- **Flagship problems:** Two Sum, Contains Duplicate, Product of Array Except Self.

### 2. Two Pointers
- **Trigger:** Sorted array, "find pair/triplet", "in-place remove", palindromes.
- **Core idea:** Place one pointer at each end (or both at the start) and move them inward /
  forward based on a comparison. Avoids nested loops.
- **Template:** `left=0, right=n-1; while left<right: compare, move one pointer`.
- **Cost:** O(n) time, O(1) space.
- **Flagship problems:** Two Sum II, 3Sum, Container With Most Water.

### 3. Sliding Window
- **Trigger:** "Longest/shortest subarray/substring with property X", "max sum of size-k window",
  "at most K distinct".
- **Core idea:** Maintain a window `[left..right]`. Expand `right`, and when the window violates
  the constraint, shrink `left` until it's valid again. Each element enters and leaves once.
- **Template:** Two pointers + a state map/counter.
- **Cost:** O(n) time, O(k) space.
- **Flagship problems:** Best Time to Buy/Sell Stock, Longest Substring Without Repeating,
  Minimum Window Substring.

### 4. Stack
- **Trigger:** "Next greater element", "valid parentheses", "evaluate expression", "recent X".
- **Core idea:** LIFO. A monotonic stack tracks elements waiting for a "partner". Parentheses
  match because the most-recent open must close first.
- **Template:** Iterate; while stack non-empty and top satisfies condition, pop and process; push.
- **Cost:** O(n) time, O(n) space.
- **Flagship problems:** Valid Parentheses, Min Stack, Daily Temperatures, Largest Rectangle.

### 5. Binary Search
- **Trigger:** Sorted array, "find in O(log n)", "minimum capacity", "first/last position".
- **Core idea:** Halve the search space each step by comparing to the middle.
- **Template:** `lo=0, hi=n-1; while lo<=hi: mid=(lo+hi)/2; narrow`.
- **Cost:** O(log n) time, O(1) space.
- **Flagship problems:** Binary Search, Search Insert Position, Koko Eating Bananas.

### 6. Linked List
- **Trigger:** Linked list input, "reverse", "detect cycle", "merge", "nth from end".
- **Core idea:** Pointer rewriting. Slow/fast pointers detect cycles; dummy head simplifies
  insertion/deletion.
- **Template:** `dummy.next = head; prev, curr pointers; rewrite .next fields`.
- **Cost:** O(n) time, O(1) space.
- **Flagship problems:** Reverse Linked List, Merge Two Sorted Lists, Linked List Cycle.

### 7. Trees
- **Trigger:** Binary tree, "depth", "invert", "level order", "validate BST", "lowest ancestor".
- **Core idea:** Recursion. Most tree problems: solve for left subtree, solve for right subtree,
  combine at root. BFS uses a queue, DFS uses recursion or an explicit stack.
- **Template:** `fn(node): if node==null return base; L=fn(node.left); R=fn(node.right); combine`.
- **Cost:** O(n) time, O(h) recursion space.
- **Flagship problems:** Invert Binary Tree, Maximum Depth, Level Order, Validate BST.

### 8. Tries
- **Trigger:** "Word starts with prefix", "autocomplete", "word dictionary".
- **Core idea:** A tree where each path from root spells a word. Shared prefixes share nodes.
- **Template:** Node has `children[26]` and `isEnd` flag; insert/search walk char by char.
- **Cost:** O(L) per operation where L = word length; O(total chars) space.
- **Flagship problems:** Implement Trie, Word Search II, Design Add/Search Words.

### 9. Heap / Priority Queue
- **Trigger:** "Top K", "Kth largest", "merge K sorted", "scheduling / task cooldown".
- **Core idea:** A heap gives O(1) peek-min/max and O(log n) insert/remove. Min-heap of size K
  tracks the top-K largest.
- **Template:** Java `PriorityQueue<>`, customize comparator, push then maybe pop.
- **Cost:** O(n log k) for top-K.
- **Flagship problems:** Kth Largest Element, Top K Frequent, Task Scheduler.

### 10. Backtracking
- **Trigger:** "All permutations / combinations / subsets / arrangements", "generate",
  "word search".
- **Core idea:** Recursion with a choice at each step; make a choice, recurse, then **undo**
  the choice (backtrack) before trying the next.
- **Template:** `backtrack(path, choices): if done: record; for c in choices: add c; recurse;
  remove c`.
- **Cost:** O(2^n) or O(n!) depending on problem.
- **Flagship problems:** Subsets, Permutations, Combination Sum, Word Search.

### 11. Graphs
- **Trigger:** Grid or adjacency list, "connected", "shortest path", "number of islands",
  "course schedule".
- **Core idea:** BFS for shortest path / level order, DFS for connectivity / cycle detection.
  Union-Find for dynamic connectivity.
- **Template:** Queue (BFS) or recursion/stack (DFS) + visited set.
- **Cost:** O(V+E) time and space.
- **Flagship problems:** Number of Islands, Clone Graph, Course Schedule, Rotting Oranges.

### 12. Greedy
- **Trigger:** "Maximum number of X you can do", "minimum steps", "jump game",
  locally-best choice works.
- **Core idea:** At each step make the choice that looks best *now*. Works when the problem has
  optimal substructure and no need to revisit choices.
- **Template:** Sort or iterate; maintain a running best (current end, farthest reach, count).
- **Cost:** Usually O(n log n) for the sort, then O(n).
- **Flagship problems:** Maximum Subarray, Jump Game, Gas Station, Assign Cookies.

### 13. Intervals
- **Trigger:** List of [start, end] pairs, "merge", "insert", "meeting rooms", "non-overlapping".
- **Core idea:** Sort by start. Then one pass merging or counting overlaps.
- **Template:** `sort by start; for each interval: if overlaps previous: merge, else push`.
- **Cost:** O(n log n).
- **Flagship problems:** Merge Intervals, Insert Interval, Meeting Rooms, Non-overlapping.

### 14. 1-D Dynamic Programming
- **Trigger:** "How many ways", "minimum cost to reach", "can you reach",
  recurrence on one index.
- **Core idea:** Define `dp[i]` = answer for first i elements. Recurrence: `dp[i] = f(dp[i-1],
  dp[i-2], ...)`. Solve bottom-up.
- **Template:** Initialize base cases; loop filling `dp[]`; return `dp[n]`.
- **Cost:** O(n) time and space (often O(1) space with rolling vars).
- **Flagship problems:** Climbing Stairs, House Robber, Coin Change, Longest Palindromic
  Substring.

### 15. 2-D Dynamic Programming
- **Trigger:** Grid paths, "edit distance", "longest common subsequence", 2-D state.
- **Core idea:** `dp[i][j]` = answer combining row i and column j. Recurrence uses
  `dp[i-1][j]`, `dp[i][j-1]`, `dp[i-1][j-1]`.
- **Template:** Initialize top row + left column; nested loops; return `dp[m][n]`.
- **Cost:** O(m*n) time and space.
- **Flagship problems:** Unique Paths, Longest Common Subsequence, Edit Distance.

### 16. Bit Manipulation
- **Trigger:** "Without extra space", XOR, "single number", "power of two", "reverse bits".
- **Core idea:** Use the bits of an integer as a compact data structure. XOR is its own inverse;
  `x & (x-1)` clears the lowest set bit.
- **Template:** Iterate over bits, or use XOR / AND tricks.
- **Cost:** O(1) space, O(1) or O(32) time.
- **Flagship problems:** Single Number, Number of 1 Bits, Missing Number.

---

## How patterns combine

Real interview problems often blend patterns. Examples:

- *Word Search* = Backtracking + Grid (Graph-style DFS).
- *Course Schedule* = Graph DFS + cycle detection.
- *Top K Frequent Words* = Hashing + Heap.
- *Longest Increasing Subsequence* = Binary Search + DP.

Once the 16 individual patterns are automatic, combinations become easy to recognize.

---

Next: [02-complexity-cheatsheet.md](./02-complexity-cheatsheet.md)
