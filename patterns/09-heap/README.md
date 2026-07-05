# Pattern 9 - Heap / Priority Queue

A heap is the data structure you reach for the moment a problem mentions "the K-th largest", "the
K most/least", "merge K sorted things", or "schedule with cooldown". This section teaches the one
trick that solves four of the five problems here: **keep a heap of size K and let it evict the
worst**. Get that idea and the rest is bookkeeping.

---

## What a heap is

A **heap** is a binary tree packed into an array, kept "half-sorted":

- In a **min-heap**, every parent is smaller than its children, so the **smallest** element sits at
  the root.
- In a **max-heap**, every parent is larger than its children, so the **largest** element sits at
  the root.

You never get a fully sorted order -- you only get fast access to the one extreme element at the
root. That is the whole point: O(1) peek at the min/max, and O(log n) to push or remove it.

| Operation | Cost |
|---|---|
| peek at root (min or max) | O(1) |
| insert an element (`offer`) | O(log n) |
| remove the root (`poll`)   | O(log n) |
| build a heap from n items  | O(n) |

A **priority queue** is the abstract type; a **heap** is the usual implementation. In Java the two
words mean the same class: `PriorityQueue`.

---

## When it applies -- trigger signals

Reach for a heap when the problem statement contains any of these:

| Trigger signal | Example problem | Heap flavour |
|---|---|---|
| "K-th largest", "K-th smallest" | Kth Largest (215, 703) | size-K min-heap / max-heap |
| "top K most frequent / closest / largest" | Top K Frequent (347), K Closest (973) | size-K heap keyed on the score |
| "merge K sorted lists / streams" | Design Twitter feed (355) | K-way merge with a heap |
| "schedule tasks with cooldown", "rearrange with spacing" | Task Scheduler | max-heap of remaining counts |
| "median of a data stream" | Find Median from Data Stream | two heaps (min + max) |
| "process the next most-urgent job" | any simulation | priority queue by urgency |

If the word **K** appears and the answer is "the K extreme items" or "the K-th item", a size-K heap
is almost always the cleanest answer.

---

## The size-K heap trick (the core idea)

Suppose you want the **K largest** numbers from a stream of N. Two ways:

1. **Naive:** push all N into a max-heap, then pop K times. Cost: O(N log N) push + O(K log N) pop.
2. **Size-K trick:** keep a **min-heap of size K**. For each incoming number, push it, then if the
   heap has more than K elements, **evict the smallest**. After all N numbers, the heap holds the K
   largest, and the **root is the K-th largest**.

Why the trick wins: the heap never grows past K, so every push/pop is O(log K), not O(log N). Total
cost is **O(N log K)** -- and since K is usually tiny compared to N, this is nearly linear.

> **Mental model:** a size-K heap is a velvet rope. Only K items are let inside; whenever the
> building is over capacity, the *least important* person inside is thrown out. When the night ends,
> the K people still inside are the K most important -- and the doorman (the root) is the
> K-th most important, i.e. the one just barely good enough to stay.

### Min-heap or max-heap? It depends on what you are throwing away.

The element at the root is the one that gets evicted when the heap overflows. So **the root must be
the element you want to discard** -- the "worst" of the survivors.

| You want... | Heap type | Root = | Why |
|---|---|---|---|
| top **K largest** | **min-heap** | smallest of the K largest | evict the smallest when overfull |
| top **K smallest** | **max-heap** | largest of the K smallest | evict the largest when overfull |
| **K-th largest** | **min-heap of size K** | the K-th largest itself | peek the root |
| **K-th smallest** | **max-heap of size K** | the K-th smallest itself | peek the root |

Get this backwards and you return the wrong element. It is the #1 heap bug.

---

## General pseudocode template (size-K heap)

Learn this one shape; all of 703, 215, 347, 973 are minor variations of it.

```
function topK(items, k):
    create an empty heap
    for each item in items:
        push item into heap
        if heap size > k:
            remove the root          # the root is the "worst" survivor, by definition
    # the heap now holds exactly the k best items
    drain the heap to read them out  # or just peek the root for the k-th
```

To adapt it:

- Pick the heap order so that the root is the element you want to **evict** (min-heap for "K
  largest", max-heap for "K smallest").
- The "item" you push can be a (value, score) pair -- e.g. (number, frequency) for Top K Frequent,
  (distance, point) for K Closest. Order the heap by the **score**, not the value.

### K-way merge template (for "merge K sorted streams")

A second shape, used by Design Twitter's news feed. Each of K users has a tweet list sorted by time.
You want the 10 newest overall.

```
function mergeK(sortedLists):
    create an empty max-heap ordered by "most recent first"
    seed it with the newest element of each list
    while output has fewer than the wanted amount and heap is not empty:
        pop the newest element; append it to the output
        from that same list, push the element just older than the one we took
    return output
```

Each pop feeds the next candidate from the same list, so the heap always holds the current best
candidate from each of the K lists. Cost: O(amount * log K).

---

## Java's PriorityQueue API

Java's heap is `java.util.PriorityQueue`. It is a **min-heap by default** (natural ordering: small
first).

```java
import java.util.PriorityQueue;

// min-heap (default): smallest on top
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);            // push       O(log n)
minHeap.peek();              // look at root, does NOT remove    O(1)
minHeap.poll();              // remove and return the root       O(log n)
minHeap.size();

// max-heap: largest on top
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
```

### Custom ordering with a Comparator

When the heap holds pairs or objects, pass a `Comparator` to the constructor. A lambda returning a
negative/zero/positive int controls the order. **Remember: the comparator defines total order; the
root is always the element that is "smallest" under that comparator and gets evicted first.**

```java
// heap of int[]{value, freq}; min-heap ordered by frequency (index 1) ascending
PriorityQueue<int[]> byFreqAsc = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));

// heap of int[]{dist, x, y}; max-heap ordered by distance descending
PriorityQueue<int[]> byDistDesc = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0]));
```

Prefer `Integer.compare(x, y)` over `x - y` inside comparators: the subtraction form can overflow
when values are large (e.g. squared distances, big timestamps) and produce a wrong order silently.

---

## Problems in this section

Five problems, ramping Easy -> Medium. The first four are the same size-K trick wearing different
clothes; the fifth is a design problem that uses a K-way merge.

| # | Folder | Problem | Difficulty | One-line teaser |
|---|---|---|---|---|
| 1 | [0703-kth-largest-element-in-a-stream](./0703-kth-largest-element-in-a-stream/) | Kth Largest Element in a Stream | Easy | A min-heap of size K where the root *is* the answer after every insert. |
| 2 | [0215-kth-largest-element-in-an-array](./0215-kth-largest-element-in-an-array/) | Kth Largest Element in an Array | Medium | Same min-heap-of-size-K idea, applied once to a fixed array. |
| 3 | [0347-top-k-frequent-heap](./0347-top-k-frequent-heap/) | Top K Frequent Elements (heap) | Medium | Count frequencies, then size-K min-heap keyed on the count. |
| 4 | [0973-k-closest-points-to-origin](./0973-k-closest-points-to-origin/) | K Closest Points to Origin | Medium | Size-K **max**-heap keyed on distance -- evict the farthest survivor. |
| 5 | [0355-design-twitter](./0355-design-twitter/) | Design Twitter | Medium | A K-way max-heap merge of each followee's recent tweets. |

> Note on 0347: there is a faster bucket-sort version of this problem in
> [../01-arrays-hashing/0347-top-k-frequent/](../01-arrays-hashing/0347-top-k-frequent/). This folder
> deliberately re-solves it with a heap so you can see the size-K pattern in its purest form.

---

## Common pitfalls of the pattern

Heaps are simple but the bugs are subtle and silent. Beginners hit these repeatedly:

- **Wrong comparator direction.** The most common heap bug. To keep the K *largest* you need a
  **min**-heap (root = smallest = the one to throw away). If you build a max-heap instead, you will
  evict the very largest elements and end up with the K smallest. Always ask: "who is at the root,
  and is that who I want to discard?"

- **Forgetting to cap the heap at K.** If you never call `poll` when `size() > k`, the heap grows to
  all N elements and the O(N log K) advantage evaporates into O(N log N). Every push inside the loop
  must be followed by the size check. The size-K cap is the *whole* optimization.

- **Using a heap where a sort would be simpler.** If you need *all* N elements sorted, or K is close
  to N, just `Arrays.sort` (O(N log N)) and index -- it is shorter and usually faster in practice.
  The size-K heap only pays off when K << N. For 215 specifically, quickselect gives O(N) average
  and is the "interview-best" answer; the heap version is shown here because it teaches the
  transferable pattern.

- **Integer overflow in comparators.** Writing `(a, b) -> a[0] - b[0]` overflows when the values are
  large (squared distances up to 2*10^8 are usually safe, but timestamps or products are not). Use
  `Integer.compare(a, b)` and the bug class disappears.

- **`peek()` on an empty heap returns `null`.** Assigning that into an `int` throws a
  `NullPointerException`. Only peek when you know the heap is non-empty (e.g. size has reached K).

- **Comparing arrays/objects with `==` or relying on identity.** A `PriorityQueue<int[]>` orders
  only by the comparator you give it -- never by contents. If your comparator forgets a tiebreaker,
  equal-priority elements come out in an unspecified order, which is fine only if the problem allows
  any valid answer (Top K Frequent, K Closest do; "K-th" does not have ties in the answer).

- **Mutating a key after insertion.** A heap does **not** re-heapify when you change an element's
  field. If the score can change (task scheduler with cooldown), do not mutate in place -- remove,
  update, and re-insert, or rebuild.

With those in mind, open
[0703-kth-largest-element-in-a-stream](./0703-kth-largest-element-in-a-stream/) and start.
