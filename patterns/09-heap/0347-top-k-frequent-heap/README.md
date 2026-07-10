# 0347 - Top K Frequent Elements (heap version)

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/top-k-frequent-elements/

## Concepts used

- **Hash map** -- a key-to-value lookup table with O(1) average lookup; perfect for counting
  occurrences. [glossary](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary)
- **Heap** -- a structure that always gives back the smallest or largest item it holds in O(1),
  with O(log n) cost to add or remove one. [glossary](../../../docs/10-glossary.md#heap--priority-queue)
- **Min-heap** -- a heap whose top item is the *smallest* of everything in it (Java's
  `PriorityQueue` default).
- **Time complexity** -- how runtime grows as input grows; here O(N log k) means we touch all N
  numbers but only do log-k work per distinct value.
  [glossary](../../../docs/10-glossary.md#time-complexity-big-o)

## Problem

Given an integer array `nums` and an integer `k`, return the `k` elements that appear **most
frequently**. The answer may be in any order, and is guaranteed to be unique.

Signature:

    int[] topKFrequent(int[] nums, int k)

Examples:

    Input:  nums = [1,1,1,2,2,3], k = 2
    Output: [1,2]        # 1 appears 3x, 2 appears 2x, 3 appears 1x -> top two are 1 and 2

    Input:  nums = [1], k = 1
    Output: [1]

> This folder re-solves the problem with a **heap** so you can see the size-K pattern in its purest
> form. A faster O(N) bucket-sort version lives in
> [../../01-arrays-hashing/0347-top-k-frequent/](../../01-arrays-hashing/0347-top-k-frequent/).

## Intuition

Two jobs. (1) Count how often each value appears -- a classic
[hash map](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary) job (key = value,
value = how many times it shows up). (2) Pick the K values with the highest counts. Job 2 is the
resume-shortlist story from [0215 - Kth Largest Element in an Array](../0215-kth-largest-element-in-an-array/):
keep a desk of size K, and whenever a new candidate arrives that beats the weakest on the desk,
swap them. The only twist is that here "better" means "higher count", not "larger number".

To make "drop the weakest of the current K" cheap we use a
[**min-heap**](../../../docs/10-glossary.md#heap--priority-queue) -- a container whose top is always
the **smallest** item inside it -- but we order it by **count**, not by value. So the heap holds
(value, count) pairs with the *least frequent* of the K survivors on top. When a new value's count
beats the top, we pop the top (the weakest survivor) and push the new pair. This is the **size-K
heap trick** applied to counts: keep only K items, evict the worst when full. It costs
O(D log k) where D is the number of distinct values, plus O(N) up front for counting.

The reversal to notice: for "top K most **frequent**" we use a **min**-heap on frequency (min on
top), not a max-heap. The reason is the same as in 0215 -- the survivor we want to throw away is the
*least frequent* of the K we kept, so that one must sit on top ready to pop. A max-heap on frequency
would put the most-frequent survivor on top, and polling it would discard exactly the value we want
to keep.

**Smallest trace.** Take `nums = [1,1,1,2,2,3]`, `k = 2`.

1. Count: `freq = {1: 3, 2: 2, 3: 1}` (value 1 appears 3 times, 2 appears twice, 3 appears once).
2. Walk the distinct values, keeping a size-2 min-heap ordered by count:

   | Step | (value, count) | heap after push (least-freq on top) | size > 2? | heap after poll |
   |-----:|-----------------|--------------------------------------|----------:|-----------------|
   | 1    | (1, 3)          | {(1,3)}                              | no        | {(1,3)}         |
   | 2    | (2, 2)          | {(2,2), (1,3)}                       | no        | {(2,2),(1,3)}   |
   | 3    | (3, 1)          | {(3,1), (1,3), (2,2)}                | yes       | {(2,2),(1,3)}   |

3. Survivors: values `{1, 2}`. Output `[1, 2]` (any order accepted).

Value 3 (count 1) entered, but on entering the heap grew past K, so the smallest-count survivor was
evicted -- and that was value 3 itself. The K most frequent survived.

This is the same size-K heap trick used by [0215](../0215-kth-largest-element-in-an-array/), just
keyed on frequency instead of on the raw value. A faster O(N) bucket-sort version of this problem
(no heap) lives in
[../../01-arrays-hashing/0347-top-k-frequent/](../../01-arrays-hashing/0347-top-k-frequent/).

### Checkpoint A -- Count, then cap by frequency

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** After counting frequencies, the heap stores (value, count) pairs ordered by which field?
- a) By the value (the number itself)
- b) By the frequency (the count), ascending
- c) By insertion order

<details><summary>Show answer</summary>

**(b)** -- the comparator compares index 1 (the count). The value is just carried along so we can report it; the heap's order comes only from the frequency.

</details>

**Q2 (comprehend).** Why a min-heap on frequency (not a max-heap) for "top k most frequent"?
- a) So the least-frequent survivor sits on top and gets evicted when the heap overflows
- b) Because min-heaps are faster than max-heaps
- c) Because the most frequent value should be evicted first

<details><summary>Show answer</summary>

**(a)** -- the root is the element you discard when size exceeds k, and that must be the least-frequent of the k you kept. A max-heap would put the most frequent on top and poll would throw away exactly the value you want to keep.

</details>

## Pseudocode

    function topKFrequent(nums, k):
        build a frequency map: value -> how many times it appears
        create an empty min-heap, ordered by frequency ascending
        for each (value, frequency) in the frequency map:
            push (value, frequency) into the heap
            if heap size > k:
                remove the root                 # the entry with the smallest frequency
        drain the heap and collect the values   # these k values are the most frequent
        return them in any order

## Java Solution

```java
import java.util.*;

class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.merge(x, 1, Integer::sum);
        }

        // min-heap of size k keyed on frequency: root = least frequent, evicted when overfull
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            heap.offer(new int[]{e.getKey(), e.getValue()});
            if (heap.size() > k) {
                heap.poll();
            }
        }

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = heap.poll()[0];
        }
        return result;
    }
}
```

`freq.merge(x, 1, Integer::sum)` is the counting idiom: insert `x` with value 1, or add 1 to its
current value if it already exists. The heap stores `int[]{value, frequency}` and its comparator
compares only index 1 (the frequency) ascending -- so this is a min-heap on frequency, exactly what
"keep the k largest frequencies" needs. We use `Integer.compare` instead of `a[1] - b[1]` to be
immune to overflow (harmless here, but the habit matters). After the loop, `poll` drains the
remaining k entries; their value half goes into the result array in any order, which the problem
allows.

## Complexity

    Time:  O(N + D log k) ~= O(N log k)  -- N to count, then D inserts into a size-k heap (D = distinct values)
    Space: O(D)            -- the frequency map; the heap is bounded by k

## Dry-Run

Trace `nums = [1,1,1,2,2,3]`, `k = 2`.

**Phase 1 -- count:** `freq = {1:3, 2:2, 3:1}`.

**Phase 2 -- size-2 min-heap keyed on frequency** (heap shown sorted by frequency; root leftmost):

| Step | entry (value, freq) | heap after push            | size > 2? | heap after poll | survivors          |
|-----:|---------------------|----------------------------|----------:|-----------------|--------------------|
| 1    | (1, 3)              | {(1,3)}                    | no        | {(1,3)}         | [(1,3)]            |
| 2    | (2, 2)              | {(2,2), (1,3)}             | no        | {(2,2),(1,3)}   | [(2,2),(1,3)]      |
| 3    | (3, 1)              | {(3,1), (1,3), (2,2)}      | yes       | {(2,2),(1,3)}   | [(2,2),(1,3)]      |  (evict the freq-1 entry)

Final survivors: values `{1, 2}`. Drain -> result `[1, 2]` (or `[2, 1]`; both accepted). Return
**[1, 2]**.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [4,4,5,5,5,6]`, `k = 2`. Which two values are returned (in any order)?
- a) 5 and 4 (counts 3 and 2)
- b) 6 and 5
- c) 4 and 6

<details><summary>Show answer</summary>

**(a)** -- counts are 4->2, 5->3, 6->1. The size-2 min-heap on frequency keeps the two highest counts: 5 (freq 3) and 4 (freq 2). Value 6 (freq 1) is the least frequent and gets evicted when the heap overflows.

</details>

**Q2 (analyze).** What goes wrong if the comparator compares `a[0]` (the value) instead of `a[1]` (the frequency)?
- a) The heap picks by value, ignoring frequency entirely -- it returns the wrong elements
- b) Nothing, both slots hold numbers
- c) It throws an exception

<details><summary>Show answer</summary>

**(a)** -- ordering by the value turns this into "k smallest/largest values", not "k most frequent". The frequency column is then dead data, so the answer is unrelated to what the problem asks.

</details>

**Q3 (transfer).** The README points to a bucket-sort version that runs in O(N). In one sentence, what does it replace the heap with?

<details><summary>Show answer</summary>

An array of lists indexed by frequency: `bucket[i]` holds every value that appears exactly i times. After one counting pass fills the buckets, a top-down scan collects k values -- no comparisons, so the whole thing is O(N).

</details>

## Common mistakes

- Ordering the heap by **value** instead of by frequency. The comparator must compare the count
  (`a[1]`), or the heap keeps the wrong elements entirely.
- Using a **max-heap** of size k. A max-heap on frequency makes the root the most frequent survivor,
  so `poll` would evict exactly the values you want to keep. For "top k most frequent" you need a
  **min-heap** so the least-frequent survivor is the one thrown away.
- Forgetting the `size > k` cap, letting all D distinct values sit in the heap -- still correct but
  O(D log D) instead of O(D log k).
- Pushing every raw array element into the heap (one per occurrence) instead of distinct (value,
  count) pairs. That miscounts and wastes memory.
- Assuming a fixed result order. The problem allows any order, so a test must compare as a *set*, not
  a list -- otherwise it is flaky.

## Related problems

- [0215 - Kth Largest Element in an Array](../0215-kth-largest-element-in-an-array/) - same size-k
  cap, but the heap holds bare integers and the answer is the single root.
- [0973 - K Closest Points to Origin](../0973-k-closest-points-to-origin/) - the heap holds
  (distance, point) pairs, and the order is **max**-heap because you evict the farthest.
- [../../01-arrays-hashing/0347-top-k-frequent/](../../01-arrays-hashing/0347-top-k-frequent/) - the
  O(N) bucket-sort solution to the same problem (no heap).
