# 0703 - Kth Largest Element in a Stream

**Difficulty:** Easy
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/kth-largest-element-in-a-stream/

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

This is the textbook "K-th largest" trigger. The insight is the size-K heap trick from the pattern
intro: keep a **min-heap of exactly size k** holding the k largest values seen so far. The root of
that min-heap is the smallest among those k -- which is, by definition, the **k-th largest** overall.
Every `add` just pushes the new value, evicts the smallest if the heap grew to k+1, and reads the
root. Each `add` is O(log k), independent of how big the stream has become.

The constructor does the same thing once for every starting element: push, then trim to size k.

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
