# 0347 - Top K Frequent Elements

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/top-k-frequent-elements/

## Problem

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements. The
answer may be in any order. It is guaranteed that the answer is unique.

Signature:

    int[] topKFrequent(int[] nums, int k)

Examples:

    Input:  nums = [1,1,1,2,2,3], k = 2
    Output: [1,2]      # 1 appears 3 times, 2 appears 2 times -> the top 2

    Input:  nums = [1], k = 1
    Output: [1]

## Intuition

Two-step pipeline: first **count** frequencies (a HashMap, the classic counting trigger), then
**select** the top `k`. The selection step is where the choice matters. Sorting all distinct
values by frequency is O(n log n). A heap of size `k` is O(n log k) -- that solution lives in the
Heap chapter. Here we use **bucket sort**: because a frequency is an integer between 1 and `n`,
we can index an array of length `n+1` by frequency, dropping each value into the slot matching its
count. Then we walk the array from the high end downward, collecting values until we have `k`.
That walk touches each slot once, so the whole selection is O(n) -- beating both sort and heap.

## Pseudocode

    function topKFrequent(nums, k):
        count <- frequency map of nums                    # value -> how many times
        create an array "bucket" of size n+1, each slot an empty list
        for each (value, freq) in count:
            append value to bucket[freq]                  # values with the same freq share a slot
        create an empty result list
        for freq from n down to 1:
            for each value in bucket[freq]:
                append value to result
                if result has k values:
                    return result

## Java Solution

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {
    @SuppressWarnings("unchecked")
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            freq.merge(x, 1, Integer::sum);
        }

        List<Integer>[] bucket = new List[nums.length + 1];
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            int f = e.getValue();
            if (bucket[f] == null) {
                bucket[f] = new ArrayList<>();
            }
            bucket[f].add(e.getKey());
        }

        int[] result = new int[k];
        int pos = 0;
        for (int f = bucket.length - 1; f >= 0 && pos < k; f--) {
            if (bucket[f] != null) {
                for (int v : bucket[f]) {
                    if (pos < k) {
                        result[pos++] = v;
                    }
                }
            }
        }
        return result;
    }
}
```

`freq.merge(x, 1, Integer::sum)` is the counting idiom -- it reads "set x to its current value
plus 1, treating a missing key as 0". `bucket` is indexed by frequency, so a value appearing 3
times lands in `bucket[3]`; the array size is `n+1` because the maximum possible frequency is `n`
(all elements equal). We scan from the top frequency downward and stop exactly at `k` results,
so values with equal frequency are collected in arbitrary order (acceptable here because the
problem guarantees a unique answer). This bucket approach is O(n); the heap variant is taught in
the Heap chapter.

## Complexity

    Time:  O(n)  -- one count pass + one bucket-fill pass + one top-down scan; all linear
    Space: O(n)  -- the frequency map and the bucket array each hold at most n entries

## Dry-Run

Input `nums = [1,1,1,2,2,3]`, `k = 2`:

Frequency map after counting: `{1: 3, 2: 2, 3: 1}`.

Bucket array (size 7, index = frequency):

| freq | 0 | 1   | 2   | 3   | 4 | 5 | 6 |
|-----:|---|-----|-----|-----|---|---|---|
| vals | - | [3] | [2] | [1] | - | - | - |

Top-down scan collecting 2 values:

| freq scanned | bucket[freq] | values taken | result so far |
|-------------:|--------------|--------------|---------------|
| 6, 5, 4      | empty        | -            | []            |
| 3            | [1]          | 1            | [1]           |
| 2            | [2]          | 2            | [1, 2]        |  # pos == k, stop

Output: `[1, 2]`.

## Common mistakes

- Sorting values by frequency with a `Comparator` -- correct but O(n log n); bucket sort is the
  intended O(n) solution for this chapter.
- Making the bucket array size `n` instead of `n+1`. A value that appears `n` times would index
  out of bounds.
- Forgetting `bucket[f]` may be `null` (some frequencies never occur) and calling `.add` on null,
  causing a `NullPointerException`.
- Stopping the top-down scan too early or too late: collect until `pos == k`, not until the loop
  ends -- extra equal-frequency values beyond `k` must be ignored.
- Comparing result order in tests. The problem allows any order, so a test must compare as a set
  or sort both sides first (this book's tests sort).

## Related problems

- [0049 - Group Anagrams](../0049-group-anagrams/) - same "map then collect" shape, different key.
- [0242 - Valid Anagram](../0242-valid-anagram/) - the counting idiom that feeds the frequency map.
- [0217 - Contains Duplicate](../0217-contains-duplicate/) - the simplest existence check that
  starts the whole hashing habit.
