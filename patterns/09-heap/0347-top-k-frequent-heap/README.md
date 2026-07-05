# 0347 - Top K Frequent Elements (heap version)

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/top-k-frequent-elements/

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

Two phases. First, count how often each value appears -- a classic HashMap job. Second, fish out the
k highest-count values. That second phase is exactly the size-K heap trick: walk the distinct values,
and in a **min-heap of size k keyed on the count**, the root is the least-frequent of the k survivors
-- so when the heap overflows we evict the smallest count, keeping only the k most frequent. The heap
holds (value, count) pairs ordered by count ascending.

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
