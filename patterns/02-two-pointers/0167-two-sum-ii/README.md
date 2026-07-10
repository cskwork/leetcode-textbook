# 0167 - Two Sum II - Input Array Is Sorted

**Difficulty:** Medium
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/

## Concepts used

- **Two pointers** -- placing two indices into an array and moving them based on a comparison, to avoid a nested loop; here one at each end, moving inward. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Sorting** -- putting elements in non-decreasing order; the problem *gives* us a sorted array, and that ordering is exactly what tells us which finger to move. [glossary](../../../docs/10-glossary.md#sorting)
- **Array** -- a row of numbered slots holding values, each read in O(1) time by its index. [glossary](../../../docs/10-glossary.md#array)

## Problem

Given a **1-indexed** integer array `numbers` already sorted in non-decreasing
order, find two numbers that add up to a specific `target`. Return the indices
of the two numbers, `[index1, index2]`, as an integer array of length 2 where
`index1 < index2` and both are **1-based**. The problem guarantees exactly one
solution and you may not use the same element twice.

Signature:

    int[] twoSum(int[] numbers, int target)

Examples (verbatim from LeetCode):

    Input:  numbers = [2,7,11,15], target = 9
    Output: [1,2]

    Input:  numbers = [2,3,4], target = 6
    Output: [1,3]

    Input:  numbers = [-1,0], target = -1
    Output: [1,2]

## Intuition

Imagine a row of price tags sorted from cheapest to most expensive, and you have
exactly $9 to spend on two items. Put one finger on the cheapest tag and one on
the most expensive. If the two prices add to more than $9, the expensive item is
clearly too pricey -- slide the right finger to a cheaper one. If they add to less
than $9, the cheap item is too cheap -- slide the left finger to a pricier one.
Close in until the pair hits $9. This finger dance is [two pointers](../../../docs/10-glossary.md#two-pointers):
one index (`left`) at the start, one (`right`) at the end, moving inward.

Let's watch it on the smallest example, `numbers = [2,7,11,15]`, `target = 9`:

- `left=0` (value 2), `right=3` (value 15): sum = 17. Bigger than 9, so 15 is too
  big -- move `right` inward to index 2.
- `left=0` (2), `right=2` (11): sum = 13, still too big -- move `right` to 1.
- `left=0` (2), `right=1` (7): sum = 9 -- found! Return the 1-based indices `[1, 2]`.

Why is moving the correct finger *guaranteed* safe? Because the array is
[sorted](../../../docs/10-glossary.md#sorting): any index to the right holds a
value at least as large. So moving `left` forward can only increase the sum, and
moving `right` backward can only decrease it. That one-way promise means exactly
one finger can ever bring us closer to `target` -- there is never a tie where we
would have to guess. This is why sorting is the key clue: without it, moving a
finger would tell us nothing about whether the sum goes up or down.

The older problem [Two Sum (LC 1)](https://leetcode.com/problems/two-sum/) had an
*unsorted* array, so it needed a hash map (a key-to-value lookup table) to
remember values already seen. Here the sort is a free gift -- we trade that hash
map's O(n) memory for two plain index variables, dropping to O(1) space.

### Checkpoint A -- Which finger moves?

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** When the current sum is LESS than the target, which pointer moves?
- a) `left` advances (toward larger values)
- b) `right` moves back (toward smaller values)
- c) both move at once

<details><summary>Show answer</summary>

**(a)** -- the array is sorted, so advancing `left` can only increase the sum, which is what "sum too small" calls for.

</details>

**Q2 (comprehend).** Why can we be sure that moving one finger never skips over the correct pair?
- a) Because the array is sorted, so moving `left` only raises the sum and moving `right` only lowers it -- there is never ambiguity about which direction helps
- b) Because the loop checks every possible pair anyway
- c) Because the target is always in the middle of the array

<details><summary>Show answer</summary>

**(a)** -- the sorted order gives a one-way promise: each finger moves the sum in a known direction, so exactly one finger can ever bring us closer. That is why the sort is the key clue -- without it, moving a finger tells us nothing.

</details>

## Pseudocode

```text
function twoSumSorted(numbers, target):
    set left to first index
    set right to last index
    while left < right:
        set current to numbers[left] + numbers[right]
        if current equals target:
            return [left + 1, right + 1]   # 1-indexed
        if current is less than target:
            advance left                   # need a bigger sum
        else:  # current is greater than target
            move right back one            # need a smaller sum
    # problem guarantees a solution, so we never reach here
    return an empty pair
```

## Java Solution

```java
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1};
            }
            if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }
}
```

The loop invariant is `left < right`: the two indices must point at distinct
elements (the problem forbids reusing one). The `+1` on the return converts from
Java's 0-based indices to the 1-based indices LeetCode wants — this is the most
common silly mistake on the problem, so it is the one line worth remembering.
The final `return new int[]{-1,-1}` is unreachable because the problem promises
exactly one solution; we include it only so the compiler is satisfied that every
path returns.

## Complexity

    Time:  O(n)  -- each iteration moves exactly one pointer, so at most n iterations.
    Space: O(1)  -- two index variables plus the 2-element result; no extra structure.

## Dry-Run

Step-by-step on `numbers = [2,7,11,15]`, `target = 9`:

| Step | left | right | numbers[left] | numbers[right] | sum | action |
|------|------|-------|---------------|----------------|-----|--------|
| 1 | 0 | 3 | 2 | 15 | 17 | 17 > 9 -> right-- |
| 2 | 0 | 2 | 2 | 11 | 13 | 13 > 9 -> right-- |
| 3 | 0 | 1 | 2 | 7 | 9 | 9 == 9 -> return [0+1, 1+1] = [1,2] |

Three iterations; the answer is `[1, 2]`. Notice `left` never moved — the sorted
property let us shrink only from the right until the pair was tight enough.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `numbers = [1,2,3,4,6]`, `target = 5`. What is returned?
- a) `[1,4]`
- b) `[0,3]`
- c) `[1,3]`

<details><summary>Show answer</summary>

**(a)** -- step 1: `left=0` (1), `right=4` (6), sum 7 > 5 -> `right--`. step 2: `left=0` (1), `right=3` (4), sum 5 == 5 -> return `[0+1, 3+1] = [1,4]`. Option (b) is the 0-based trap; (c) sums to 7.

</details>

**Q2 (analyze).** What goes wrong if the loop condition is `while (left <= right)` instead of `while (left < right)`?
- a) When the pointers meet on one element it could pair that element with itself, which the problem forbids
- b) Nothing -- the answer is identical
- c) The loop exits too early and never finds the pair

<details><summary>Show answer</summary>

**(a)** -- the problem says you may not use the same element twice. `<` keeps the pointers on distinct elements; `<=` would let them collide and "find" a single element pairing with itself.

</details>

**Q3 (transfer).** How would you solve this if the input array were NOT sorted?

<details><summary>Show answer</summary>

You lose the guarantee that moving a finger changes the sum in a known direction, so the two-pointer move rule no longer works. Fall back to the hash-map approach from Pattern 1's Two Sum: store each value's index in a map as you scan, and for each element check whether `target - value` is already stored. Alternatively sort a copy first -- but then you must track the original indices to return them.

</details>

## Common mistakes

- **Returning 0-based indices.** LeetCode wants 1-based; forgetting the `+1`
  fails every test even though the logic is right.
- **Moving the wrong pointer.** If `sum < target` you must move `left` (toward
  larger values). Swapping the branches makes the pointers walk away from the
  answer and either loop forever or return garbage.
- **Using `<=` instead of `<` in the loop.** `left == right` would let an
  element pair with itself, which the problem forbids.
- **Reaching for the hash map.** It works but wastes O(n) space and ignores the
  "sorted" hint — an interviewer will ask you to remove it.

## Related problems

- [0001 - Two Sum](../../01-arrays-hashing/) (Pattern 1) - the unsorted version,
  solved with a hash map. Compare the two approaches to see when each wins.
- [0015 - 3Sum](../0015-3sum/) - the same opposite-ends inner loop, wrapped in
  an outer "fix one element" loop.
- [0011 - Container With Most Water](../0011-container-with-most-water/) - same
  shape, but the move decision is based on heights, not a sum.
