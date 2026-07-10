# 0347 - Top K Frequent Elements

**Difficulty:** Medium
**Pattern:** Arrays & Hashing
**LeetCode:** https://leetcode.com/problems/top-k-frequent-elements/

## Concepts used

- **Hash map** -- a key->value lookup table; here the key is a number and the value is how many
  times it appears. [glossary](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary)
- **Array** -- numbered slots reached by index; here we use the index itself as data (the
  frequency). [glossary](../../../docs/10-glossary.md#array)
- **Sorting** -- ordering items; sorting values by frequency would solve this in O(n log n), but
  we do better. [glossary](../../../docs/10-glossary.md#sorting)

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

A teacher wants the 3 most-read books in her class. She could line up every student's book and
sort the whole pile by title -- wasteful, since she only needs the top 3. Instead she first counts
how many students read each book (a tally), then asks "which books were read exactly N times?"
starting from the highest possible N and working down. The first few answers fill her top 3.

Walk the smallest case, `nums = [1, 1, 1, 2, 2, 3]`, `k = 2`. Count first: `1` appears three
times, `2` appears twice, `3` appears once. The most frequent is `1` (three times), the next is
`2` (twice). So the top 2 are `[1, 2]`.

The general rule has two stages. First, count how many times each value appears -- a
[hash map](../../../docs/10-glossary.md#hash-map-aka-hash-table-dictionary) does this in one pass
(key = value, value = count). Second, pick the `k` most frequent. The clever part is stage two. A
value's frequency is always a whole number between 1 and `n` (where `n` is the array length, since
nothing can appear more than `n` times). So build an [array](../../../docs/10-glossary.md#array)
of size `n + 1` whose index IS the frequency -- slot `3` holds every value that appeared exactly
three times. Then walk that array from the top index downward, scooping values into the answer
until you have `k`.

Why does walking from the top collect the most frequent? Because a higher array index means a
higher frequency, so the first non-empty slots we hit are the most-frequent values. Each value
lives in exactly one slot and we touch each slot once, so the whole selection is O(n) -- faster
than sorting the values by frequency, which is O(n log n).

### Checkpoint A -- Why bucket sort

Pause before expanding.

**Q1 (recall).** In the bucket array, what does the INDEX of a slot represent?
- a) A value from `nums`
- b) A frequency (how many times some value appeared)
- c) A position in the original array

<details><summary>Show answer</summary>

**(b)** -- slot `f` holds every value that appeared exactly `f` times. So scanning from the top index collects the most frequent values first.

</details>

**Q2 (comprehend).** Why is this O(n) while "sort values by frequency" is O(n log n)?
- a) Hash maps are always faster than sorting
- b) Frequencies are whole numbers in 1..n, so an array indexed by frequency buckets everything in one linear pass -- no comparisons, no log factor
- c) Because k is small

<details><summary>Show answer</summary>

**(b)** -- bucket sort exploits that frequency is a small integer range (1..n), so we place each value in O(1) and read off the top in O(n). Comparison sorting can't avoid the log factor.

</details>

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

### Checkpoint B -- Trace and tune

**Q1 (apply).** For `nums = [1,1,2,2,2,3]`, `k = 1`: which slot is `2` placed into, and what is returned?
- a) Slot 2, returns `[1]`
- b) Slot 3 (frequency 3), returns `[2]`
- c) Slot 1, returns `[3]`

<details><summary>Show answer</summary>

**(b)** -- `2` appears three times, so it goes in `bucket[3]`. The top-down scan hits slot 3 first and returns `[2]`.

</details>

**Q2 (analyze).** Why must the bucket array be size `n + 1`, not `n`?
- a) Java requires odd sizes
- b) A single value repeated n times has frequency n, which must index `bucket[n]`; size n would throw out-of-bounds
- c) To leave a slot for frequency 0

<details><summary>Show answer</summary>

**(b)** -- the maximum frequency is n (all elements equal), so the valid indices are 0..n, requiring n+1 slots.

</details>

**Q3 (transfer).** The Heap chapter solves this same problem with a min-heap of size k. When would the heap be preferable to the bucket approach?

<details><summary>Show answer</summary>

When you only need the top k and k is tiny relative to n, a size-k heap uses O(k) space and O(n log k) time -- less memory than the O(n) bucket array, and it streams well if `nums` arrives element-by-element.

</details>

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
