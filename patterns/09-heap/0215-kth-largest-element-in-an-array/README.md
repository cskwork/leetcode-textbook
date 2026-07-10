# 0215 - Kth Largest Element in an Array

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/kth-largest-element-in-an-array/

## Concepts used

- **Heap** -- a structure that always gives back the smallest or largest item it holds in O(1),
  with O(log n) cost to add or remove one. [glossary](../../../docs/10-glossary.md#heap--priority-queue)
- **Min-heap** -- a heap whose top item is the *smallest* of everything in it (Java's
  `PriorityQueue` default).
- **Array** -- a row of numbered slots holding values, accessed by position in O(1).
  [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- putting elements in order; standard sorts run in O(n log n).
  [glossary](../../../docs/10-glossary.md#sorting)

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

Picture a recruiter with a desk that only fits **K** resumes, reading from a tall pile. Once the
desk is full, every new resume only stays if it beats the **weakest** one on the desk -- and if it
does, the weakest goes in the trash to make room. When the pile is done, the weakest survivor left
on the desk is the **K-th best** overall. This problem is that story with "best = largest number";
the "pile" is the whole array, all read in one pass.

To make "drop the weakest of the current K" cheap we use a
[**min-heap**](../../../docs/10-glossary.md#heap--priority-queue) -- a container whose top is always
the **smallest** item inside it. We keep its size at exactly K and store the K largest values seen
so far. Because the top is the smallest of those K, the top *is* the K-th largest overall; when a
new value arrives that beats it, we pop the top (the weakest survivor) and push the new value. This
is the **size-K heap trick**: keep only K items, evict the worst when full. It costs O(n log k),
which beats [sorting](../../../docs/10-glossary.md#sorting) the whole array (O(n log n)) whenever K
is small -- exactly why the problem tells you not to sort.

The one idea that takes a moment: for "K-th **largest**" we use a **min**-heap (min on top), not a
max-heap. The reason is the desk analogy -- the value we want to throw away is the *smallest* of the
K survivors, so that is the value that must sit on top, ready to pop. A max-heap would put the
*largest* survivor on top and polling it would discard the very value we want to keep.

**Smallest trace.** Take `nums = [3,2,1,5,6,4]`, `k = 2`. Walk the array, keeping a min-heap of size
2. The top of the heap is shown on the left.

| Step | x | heap after push (top first) | size > 2? | heap after poll | why                |
|-----:|--:|-----------------------------|----------:|-----------------|--------------------|
| 1    | 3 | {3}                         | no        | {3}             | not full yet       |
| 2    | 2 | {2,3}                       | no        | {2,3}           | size = 2 = K, full |
| 3    | 1 | {1,2,3}                     | yes       | {2,3}           | evict 1 (smallest) |
| 4    | 5 | {2,3,5}                     | yes       | {3,5}           | evict 2            |
| 5    | 6 | {3,5,6}                     | yes       | {5,6}           | evict 3            |
| 6    | 4 | {4,5,6}                     | yes       | {5,6}           | evict 4            |

After the scan, the heap holds the two largest values `{5, 6}`; the top (5) is the smallest of them,
so it is the **2nd largest**. This is byte-for-byte the body of `add` from
[0703 - Kth Largest Element in a Stream](../0703-kth-largest-element-in-a-stream/) -- the streaming
cousin of this problem -- run inside a loop. The same size-K heap trick also drives
[0347 - Top K Frequent](../0347-top-k-frequent-heap/), where the heap stores value-frequency pairs.

### Checkpoint A -- Why a min-heap and the size cap

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why does "k-th largest" use a min-heap of size k (not a max-heap)?
- a) The root is the smallest of the k largest, which is both the k-th largest and the one to evict
- b) A min-heap sorts faster than a max-heap
- c) A max-heap would overflow on large arrays

<details><summary>Show answer</summary>

**(a)** -- the root must be the survivor you throw away when overfull. For "k-th largest" that is the smallest of the k kept, so it has to sit on top -- which is a min-heap. That same root is also the answer you peek at the end.

</details>

**Q2 (comprehend).** Why is this solution O(N log k) and not O(N log N)?
- a) Because the heap never grows past k, each push/poll is O(log k)
- b) Because the array is already sorted
- c) Because peek at the end is O(1)

<details><summary>Show answer</summary>

**(a)** -- the `size > k` poll caps the heap at k elements, so its tree height is log k. N inserts at log k each gives O(N log k). Without the cap the heap would reach all N and degrade to O(N log N).

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [7, 1, 9]`, `k = 2`. What is returned?
- a) 9
- b) 7
- c) 1

<details><summary>Show answer</summary>

**(b)** -- push 7 -> {7}; push 1 -> {1,7} (full); push 9 -> {1,7,9}, size 3 > 2 evicts the smallest (1) -> {7,9}. The root 7 is the smaller survivor, i.e. the 2nd largest. (Sorted desc: 9,7,1.)

</details>

**Q2 (analyze).** Suppose you deleted the `if (heap.size() > k) poll()` line. On `nums = [3,2,1,5,6,4]`, `k = 2`, what would `peek()` return?
- a) 5 -- still correct
- b) 1 -- the heap now holds all six, so the root is the overall smallest, a wrong answer
- c) It throws

<details><summary>Show answer</summary>

**(b)** -- without the cap every value stays, so the heap is a min-heap of all six and the root is 1 (the overall min). That is the 6th largest, not the 2nd. The cap is not just an optimization; it is what makes the root equal the k-th largest.

</details>

**Q3 (transfer).** The note mentions quickselect as an O(N) average alternative. In one sentence, what idea does quickselect use that a heap does not?

<details><summary>Show answer</summary>

Quickselect partially partitions the array around a pivot and recurses only into the side that contains the k-th largest, never building a separate survivor set. It trades the heap's bounded memory (O(k)) for in-place partitioning (O(1) extra) and average O(N) time.

</details>

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
