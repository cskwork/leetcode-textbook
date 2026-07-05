# 02 - Big-O Complexity Cheatsheet (for Java solvers)

Big-O describes how runtime or memory grows as the input size `n` grows. We drop constants and
lower-order terms. This page gives the practical version -- enough to analyze any problem in
this book.

## The growth hierarchy (memorize this)

From fastest (best) to slowest (worst):

| Class | Name | n=10 | n=1000 | n=1,000,000 | Example |
|-------|------|------|--------|-------------|---------|
| O(1) | constant | 1 | 1 | 1 | Array index, hash get |
| O(log n) | logarithmic | 3 | 10 | 20 | Binary search |
| O(n) | linear | 10 | 1,000 | 1,000,000 | Single loop |
| O(n log n) | linearithmic | 33 | 10,000 | 20,000,000 | Sort, merge sort |
| O(n^2) | quadratic | 100 | 1,000,000 | too big | Nested loops |
| O(2^n) | exponential | 1,024 | too big | too big | All subsets |
| O(n!) | factorial | 3,628,800 | too big | too big | All permutations |

Rule of thumb for LeetCode: with n up to ~10^5, you need O(n log n) or faster. With n up to
~20, O(2^n) is acceptable. With n up to ~10, O(n!) is acceptable.

## Counting loops

To analyze Java code, count nested loops and recursion depth:

```java
for (int i = 0; i < n; i++) { ... }              // O(n)
for (int i = 0; i < n; i++)
  for (int j = i; j < n; j++) { ... }            // O(n^2)
for (int i = 1; i < n; i *= 2) { ... }           // O(log n)
while (n > 0) { n /= 2; }                        // O(log n)
```

Independent loops add: `O(n) + O(n) = O(n)`. Nested loops multiply: `O(n) * O(n) = O(n^2)`.

## Java data-structure costs (the table you'll re-read constantly)

| Structure | get / contains | add / insert | remove | iterate | Space |
|-----------|----------------|--------------|--------|---------|-------|
| `int[]` / array | O(1) | O(n) | O(n) | O(n) | O(n) |
| `ArrayList<E>` | O(1) index | O(1) amortized append | O(n) | O(n) | O(n) |
| `LinkedList<E>` | O(n) | O(1) at ends | O(1) at ends | O(n) | O(n) |
| `HashMap<K,V>` | O(1) avg, O(n) worst | O(1) avg | O(1) avg | O(n) | O(n) |
| `HashSet<E>` | O(1) avg | O(1) avg | O(1) avg | O(n) | O(n) |
| `TreeMap<K,V>` | O(log n) | O(log n) | O(log n) | O(n) | O(n) |
| `TreeSet<E>` | O(log n) | O(log n) | O(log n) | O(n) | O(n) |
| `PriorityQueue<E>` | O(1) peek | O(log n) | O(log n) | O(n) | O(n) |
| `ArrayDeque<E>` (stack/queue) | O(1) ends | O(1) ends | O(1) ends | O(n) | O(n) |
| `String` | O(1) `charAt` | O(n) concat | n/a | O(n) | O(n) |

Key gotchas:
- `HashMap` is O(1) *average*, but degrades to O(n) if many hash collisions. For interview
  analysis we treat it as O(1).
- `String.substring` in Java 7+ is O(n) (it copies), not O(1). For repeated slicing use
  `StringBuilder` or char arrays.
- `ArrayList.contains` is O(n). If you need O(1) lookup, use a `HashSet`.

## Common algorithm costs at a glance

| Algorithm | Time | Space | Where in this book |
|-----------|------|-------|--------------------|
| Hash-based scan | O(n) | O(n) | Arrays & Hashing |
| Two pointers | O(n) | O(1) | Two Pointers |
| Sliding window | O(n) | O(k) | Sliding Window |
| Monotonic stack | O(n) | O(n) | Stack |
| Binary search | O(log n) | O(1) | Binary Search |
| Linked-list pass | O(n) | O(1) | Linked List |
| Tree DFS / BFS | O(n) | O(h) / O(w) | Trees |
| Trie insert/search | O(L) | O(total chars) | Tries |
| Heap top-K | O(n log k) | O(k) | Heap |
| Backtracking subsets | O(2^n * n) | O(n) | Backtracking |
| Backtracking perms | O(n! * n) | O(n) | Backtracking |
| Graph BFS / DFS | O(V + E) | O(V) | Graphs |
| Sort | O(n log n) | O(n) | many |
| 1-D DP | O(n) | O(n) | 1-D DP |
| 2-D DP | O(m * n) | O(m * n) | 2-D DP |

## Space complexity: count the structures

Space = the largest auxiliary structure you allocate.

```java
int[] dp = new int[n];           // O(n) space
HashMap<Integer,Integer> m;      // O(k) where k = distinct keys
List<Integer> result;            // O(n) if you keep every input
```

Recursion also costs space: each call frame is O(1), and the deepest recursion path is O(h)
where h is tree height or recursion depth. A balanced tree DFS is O(log n) space; a degenerate
one is O(n).

## Amortized cost (the tricky one)

`ArrayList.add` is O(1) *amortized* even though occasionally it copies the whole array (O(n))
when capacity doubles. Over n inserts, the total work is O(n), so the average is O(1) per
insert. LeetCode accepts amortized analysis.

## How to write complexity in your solution notes

Format every problem's complexity like this:

```
Time:  O(n)    -- single pass through the array
Space: O(n)    -- the HashMap stores up to n entries
```

Always include the *why* in a one-line comment. "O(n)" without justification is not analysis;
it's a guess.

## Worked example: Two Sum

```java
Map<Integer,Integer> seen = new HashMap<>();
for (int i = 0; i < nums.length; i++) {
    if (seen.containsKey(target - nums[i])) return ...;
    seen.put(nums[i], i);
}
```

- One loop over `n` elements: O(n) iterations.
- Inside: `containsKey` and `put` on a HashMap: O(1) each.
- Total time: O(n).
- The map holds up to n entries: O(n) space.

```
Time:  O(n)
Space: O(n)
```

---

Next: [03-java-crash-course.md](./03-java-crash-course.md)
