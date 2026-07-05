# 0215 - Kth Largest Element in an Array

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/kth-largest-element-in-an-array/

## Problem

Given an integer array `nums` and an integer `k`, return the **k-th largest** element in the array
(1-indexed), counting duplicates separately. You must not sort the array.

Signature:

    int findKthLargest(int[] nums, int k)

Examples:

    Input:  nums = [3,2,1,5,6,4], k = 2
    Output: 5          # sorted desc: 6,5,4,3,2,1 ; 2nd largest is 5

    Input:  nums = [3,2,3,1,2,4,5,5,6], k = 4
    Output: 4          # sorted desc: 6,5,5,4,3,3,2,2,1 ; 4th largest is 4

## Intuition

This is the same "k-th largest" trigger as LC 703, just applied to a fixed array in one pass instead
of to a stream. The size-K heap trick applies directly: keep a **min-heap of size k** holding the k
largest values seen so far. After scanning the whole array, the heap's root is the smallest among
those k largest -- exactly the k-th largest. Each push/poll is O(log k), so the whole scan is
O(N log k).

## Pseudocode

    function findKthLargest(nums, k):
        create an empty min-heap
        for each value x in nums:
            push x into the heap
            if heap size > k:
                remove the smallest from the heap
        return the smallest element now in the heap     # the k-th largest

## Java Solution

```java
import java.util.PriorityQueue;

class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int x : nums) {
            heap.offer(x);
            if (heap.size() > k) {
                heap.poll();
            }
        }
        return heap.peek();
    }
}
```

`PriorityQueue<Integer>` defaults to a min-heap, so `poll()` always removes the smallest value --
the one we do not want to keep among the top k. The heap never grows past k+1, so each `offer`/`poll`
pair is O(log k). After the loop the heap holds exactly the k largest values, and `peek()` returns
their minimum, i.e. the k-th largest overall. This is byte-for-byte the body of `add` from LC 703
run inside a loop.

## Complexity

    Time:  O(N log k)  -- N inserts, each costing at most log(k+1) because the heap is capped at k+1
    Space: O(k)        -- the heap holds at most k elements

## Dry-Run

Trace `nums = [3,2,1,5,6,4]`, `k = 2`. The heap is shown as a sorted view (root on the left).

| Step | x | heap after push | size > 2? | heap after poll | survivors (sorted) |
|-----:|--:|-----------------|----------:|-----------------|--------------------|
| 1    | 3 | {3}             | no        | {3}             | [3]                |
| 2    | 2 | {2,3}           | no        | {2,3}           | [2,3]              |
| 3    | 1 | {1,3,2}         | yes       | {2,3}           | [2,3]              |  (evict 1)
| 4    | 5 | {2,3,5}         | yes       | {3,5}           | [3,5]              |  (evict 2)
| 5    | 6 | {3,5,6}         | yes       | {5,6}           | [5,6]              |  (evict 3)
| 6    | 4 | {4,6,5}         | yes       | {5,6}           | [5,6]              |  (evict 4)

Final heap = {5,6}, root = 5. Return **5**. (The two largest values are 6 and 5; 5 is the 2nd
largest.)

## Common mistakes

- Using a **max-heap** of all N elements and polling k times. It works (O(N + k log N) to build then
  pop) but discards the O(N log k) advantage and is slower when k is small. Worse, a max-heap of
  size k for "k-th largest" is wrong: its root is the largest survivor, so `poll` would throw away
  the very element you need.
- Forgetting the `size > k` cap, letting the heap grow to N and silently degrading to O(N log N).
- Misreading "k-th largest" as "k-th smallest" or "element at index k after ascending sort".
  `[3,2,1,5,6,4]` with k=2 answers 5, not 2.
- Trying to use the array's k-th index directly. After `Arrays.sort` the k-th largest lives at index
  `N - k`, not `k - 1`. (Sorting is forbidden here anyway, and is O(N log N).)

> Note on alternatives: the **quickselect** algorithm solves this in O(N) average time and O(1)
> space by partially partitioning the array around a pivot until the k-th largest lands in place. It
> is the interview-best answer when k varies and N is large. The heap version is shown here because
> it teaches the transferable size-K pattern that also solves 703, 347, and 973.

## Related problems

- [0703 - Kth Largest Element in a Stream](../0703-kth-largest-element-in-a-stream/) - identical
  heap idea, but the array is revealed one `add` at a time.
- [0347 - Top K Frequent (heap)](../0347-top-k-frequent-heap/) - the heap now stores (value,
  frequency) pairs, ordered by frequency.
- [0973 - K Closest Points to Origin](../0973-k-closest-points-to-origin/) - the mirrored version:
  a size-k **max**-heap because the farthest survivor must be evicted.
