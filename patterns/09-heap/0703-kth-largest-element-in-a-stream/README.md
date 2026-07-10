# 0703 - Kth Largest Element in a Stream

**Difficulty:** Easy
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/kth-largest-element-in-a-stream/

## Concepts used

- **Heap** -- a structure that always gives back the smallest or largest item it holds in O(1),
  with O(log n) cost to add or remove one. [glossary](../../../docs/10-glossary.md#heap--priority-queue)
- **Min-heap** -- a heap whose top item is the *smallest* of everything in it. In Java this is the
  default behavior of `PriorityQueue`.
- **Array** -- a row of numbered slots holding values, accessed by position in O(1).
  [glossary](../../../docs/10-glossary.md#array)
- **Time complexity** -- how runtime grows as the input grows; O(log k) means each step roughly
  doubles how much it can hold. [glossary](../../../docs/10-glossary.md#time-complexity-big-o)

## Problem

Design a class that receives an integer `k` and an integer array `nums` (the initial stream), and
has a method `add(val)` that appends `val` to the stream then returns the **k-th largest** element
in the stream. If two elements are equal they each count separately (so `[5,5,5]` with k=2 has 2nd
largest = 5).

Signature:

    class KthLargest:
        constructor(int k, int[] nums)
        int add(int val)

Examples:

    Input: k = 3, nums = [4,5,8,2]
           add(3)  -> 4
           add(5)  -> 5
           add(10) -> 5
           add(9)  -> 8
           add(4)  -> 8
    Output: [4, 5, 5, 8, 8]

    Input: k = 1, nums = []
           add(3)  -> 3
           add(5)  -> 5
    Output: [3, 5]            # k=1 means "the single largest so far"

## Intuition

Imagine you are a hiring manager building a shortlist of the **K best** applicants out of a tall
pile of resumes, and you read them one at a time. Once your shortlist has K people, every new
applicant only gets in if they are better than the **worst** person currently on the list. If they
are, you drop the worst and slot the new one in. The person you keep an eye on -- the one who would
be dropped next -- is the **weakest of your current K**. This problem is exactly that, where "best"
means "largest number" and new resumes arrive one `add` at a time.

To make "drop the worst of the current K" cheap we use a
[**min-heap**](../../../docs/10-glossary.md#heap--priority-queue) -- a container whose top is always
the **smallest** of the items inside it. We keep the heap at exactly size K and store the K largest
values seen so far. Because the heap's top is the smallest of those K, the top *is* the K-th largest
overall -- and when a new value arrives that beats it, we pop the top (the weakest survivor) and
push the new value. This is the **size-K heap trick**: keep only K items; when full, evict the
worst. Each add costs O(log k), no matter how big the stream has grown.

The reversal here is the only thing that takes a moment: for "K-th **largest**" we use a **min**-heap
(min on top), not a max-heap. The reason is exactly the resume analogy -- the value we want to throw
away is the *smallest* of the K survivors, so that value has to be the one sitting on top ready to
pop. A max-heap would put the *largest* survivor on top, and polling it would throw away the very
value we want to keep.

**Smallest trace.** Take `k = 3` and the stream `4, 5, 8, 2` as the initial values, then `add(3)`:

1. Start with an empty min-heap.
2. Push 4 -> heap `{4}`, size 1, not over 3 yet.
3. Push 5 -> heap `{4, 5}` (top is 4).
4. Push 8 -> heap `{4, 5, 8}` (top is 4). Size = 3 = K, full now.
5. Push 2 -> heap `{2, 4, 5, 8}` (size 4, one too many). Pop the top (2). Heap is back to
   `{4, 5, 8}`, top = 4. So the 3rd largest of `[4,5,8,2]` is **4**.
6. `add(3)`: push 3 -> `{3, 4, 5, 8}` (size 4), pop top (3) -> `{4, 5, 8}`, top = 4. Return **4**.

The constructor does steps 1-5 once for each starting value; `add` repeats steps 6 on every call.
This is the same size-K heap trick used by [0215 - Kth Largest Element in an
Array](../0215-kth-largest-element-in-an-array/) (the same question on a fixed array instead of a
stream) and [0347 - Top K Frequent](../0347-top-k-frequent-heap/) (the heap stores value-frequency
pairs).

### Checkpoint A -- Pick the right heap

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** For "k-th largest" using a heap of size k, which heap puts the answer at the root?
- a) A max-heap (root = largest)
- b) A min-heap (root = smallest of the k largest)
- c) Either works

<details><summary>Show answer</summary>

**(b)** -- the root must be the survivor you would evict, which for "k-th largest" is the smallest of the k kept. That smallest survivor is exactly the k-th largest overall.

</details>

**Q2 (comprehend).** Why is each `add` O(log k) and not O(log N), even after the stream has grown huge?
- a) Because the stream stays small
- b) Because the heap is capped at k, its height is log k, not log N
- c) Because we sort the heap after each add

<details><summary>Show answer</summary>

**(b)** -- the `size > k` poll keeps the heap at k elements, so every push/poll touches a tree of height log k. The total stream size N never affects a single add.

</details>

## Pseudocode

    constructor(k, nums):
        store k
        create an empty min-heap
        for each value x in nums:
            push x into the heap
            if heap size > k:
                remove the smallest from the heap

    function add(val):
        push val into the heap
        if heap size > k:
            remove the smallest from the heap
        return the smallest element now in the heap   # that is the k-th largest

## Java Solution

```java
import java.util.PriorityQueue;

class KthLargest {
    private final PriorityQueue<Integer> heap;
    private final int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.heap = new PriorityQueue<>();
        for (int x : nums) {
            heap.offer(x);
            if (heap.size() > k) {
                heap.poll();
            }
        }
    }

    public int add(int val) {
        heap.offer(val);
        if (heap.size() > k) {
            heap.poll();
        }
        return heap.peek();
    }
}
```

The heap is a `PriorityQueue<Integer>`, which is a min-heap by default -- exactly what we need,
because the root must be the smallest of the k largest (the element we would discard, and also the
answer). `offer` pushes and `poll` removes the root; both are O(log k) since the heap never exceeds
k+1 elements. The constructor and `add` share the identical "push then trim to k" step, which keeps
the invariant `heap.size() <= k` after every operation. `peek` at the end returns the k-th largest
in O(1). The guarantee "at least k elements exist when you search" means `peek` is never called on
an empty heap.

## Complexity

    Time:  O(N log k) to build, O(log k) per add  -- each push/poll is bounded by heap height log k
    Space: O(k)  -- the heap holds at most k elements

## Dry-Run

Trace `k = 3`, `nums = [4,5,8,2]`, then `add(3)` and `add(10)`.

**Constructor (build the initial heap of size 3):**

| Step | x | heap after push  | size > 3? | heap after poll | heap (sorted view) |
|-----:|--:|------------------|----------:|-----------------|--------------------|
| 1    | 4 | {4}              | no        | {4}             | [4]                |
| 2    | 5 | {4,5}            | no        | {4,5}           | [4,5]              |
| 3    | 8 | {4,5,8}          | no        | {4,5,8}         | [4,5,8]            |
| 4    | 2 | {2,5,8,4}        | yes       | {5,8,4}         | [4,5,8]            |

After construction the root = 4, which is already the 3rd largest of `[4,5,8,2]`.

**add(3):**

| Action          | heap state        | result |
|-----------------|-------------------|-------:|
| push 3          | {3,4,8,5}         | -      |
| size 4 > 3 poll | {4,5,8}           | -      |
| peek            | root = 4          | **4**  |

**add(10):**

| Action          | heap state        | result |
|-----------------|-------------------|-------:|
| push 10         | {5,10,8,5}        | -      |  (the two 5s are 4-then-5)
| size 4 > 3 poll | {5,8,10}          | -      |
| peek            | root = 5          | **5**  |

The survivors after `add(10)` are the three largest values `5,8,10`; the root 5 is the 3rd largest.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Take `k = 2`, `nums = [1,2,3]`, then call `add(0)`. What does `add(0)` return?
- a) 0
- b) 2
- c) 3
- d) 1

<details><summary>Show answer</summary>

**(b)** -- the constructor ends with heap {2,3} (pushing 3 evicts 1). `add(0)` pushes 0, then size 3 > 2 evicts the smallest (0), leaving {2,3}; peek returns 2, the 2nd largest.

</details>

**Q2 (analyze).** What goes wrong if `add` polls the root BEFORE pushing the new value?
- a) Nothing -- order is irrelevant
- b) You may evict a current survivor before learning whether the new value beats it
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- when the heap is exactly full (size k), polling first drops a survivor blindly; if the incoming value turns out smaller, that survivor was lost for nothing. Always push first, then trim.

</details>

**Q3 (transfer).** If the class had to return the k-th SMALLEST instead of the k-th largest, what is the one change that does it?

<details><summary>Show answer</summary>

Swap the min-heap for a max-heap of size k. The root becomes the largest of the k smallest survivors, which is exactly the k-th smallest. Everything else (push, size-k trim, peek) stays identical.

</details>

## Common mistakes

- Using a **max-heap** here. A max-heap's root is the largest, so polling it would discard the very
  value you are trying to keep. The heap order must make the *evicted* element the root, which for
  "k-th largest" means a **min-heap**.
- Never trimming: pushing every element without the `size > k` check grows the heap to the whole
  stream, turning O(N log k) into O(N log N) and breaking the size invariant.
- Polling *before* pushing in `add`. That would drop the wrong element on the very call that brings
  the heap from k to k+1. Always push first, then trim.
- Returning `peek()` without handling the constructor having fewer than k starting elements. The
  problem guarantees k elements exist at query time, but the heap legitimately starts smaller; the
  code is correct as long as you let `add` fill it up before peeking.
- Confusing "k-th largest" with "k-th from the front". `[1,2,3]` with k=2 answers **2** (2nd
  largest), not 1.

## Related problems

- [0215 - Kth Largest Element in an Array](../0215-kth-largest-element-in-an-array/) - the same
  min-heap-of-size-k, applied once to a fixed array instead of a stream.
- [0347 - Top K Frequent (heap)](../0347-top-k-frequent-heap/) - same size-k cap, but the heap
  stores (value, frequency) pairs.
- [0973 - K Closest Points to Origin](../0973-k-closest-points-to-origin/) - the mirror image: a
  size-k **max**-heap, because you evict the farthest.
