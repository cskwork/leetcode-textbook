# 0015 - 3Sum

**Difficulty:** Medium
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/3sum/

## Concepts used

- **Two pointers** -- placing two indices into an array and moving them based on a comparison; used here on the *inner* pair once one element is fixed. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Sorting** -- putting elements in non-decreasing order up front; it enables both the two-pointer move rule and the duplicate-skipping. [glossary](../../../docs/10-glossary.md#sorting)
- **Array** -- a row of numbered slots holding values, each read in O(1) time by its index. [glossary](../../../docs/10-glossary.md#array)

## Problem

Given an integer array `nums`, return all the **unique** triplets
`[nums[i], nums[j], nums[k]]` such that `i != j != k != i` and
`nums[i] + nums[j] + nums[k] == 0`. The solution set must not contain duplicate
triplets, but the input may contain duplicate values.

Signature:

    List<List<Integer>> threeSum(int[] nums)

Examples (verbatim from LeetCode):

    Input:  nums = [-1,0,1,2,-1,-4]
    Output: [[-1,-1,2],[-1,0,1]]

    Input:  nums = [0,1,1]
    Output: []

    Input:  nums = [0,0,0]
    Output: [[0,0,0]]

## Intuition

Picking three numbers that sum to zero is one level harder than picking two. The
naive approach -- three nested loops trying every group of three -- is correct
but slow: with 1000 numbers it checks roughly a billion triples. We need a
shortcut.

The shortcut turns the three-number problem into many two-number problems.
Suppose you must form triples whose ages sum to 90. Pick one person as the
"anchor"; now the other two only need to sum to (90 - that person's age) -- a
two-person job. Solve it, then pick the next anchor and repeat. Anchoring one
element and using [two pointers](../../../docs/10-glossary.md#two-pointers) for
the remaining pair turns the triple search into many two-sum searches: n anchors,
each with an O(n) two-finger scan, for O(n^2) total instead of the O(n^3) brute
force.

Let's watch the core on the tiniest case, `nums = [-1, 0, 1]`. First we
[sort](../../../docs/10-glossary.md#sorting) it (already sorted here). Anchor the
first element, `-1`; the pair we still need must now sum to `-(-1) = 1`. Put
`left` on the element right after the anchor (`0`) and `right` on the last element
(`1`): `0 + 1 = 1` -- exactly the target, so `[-1, 0, 1]` is a triplet. This inner
scan is the same two-finger move used in [Two Sum II](../0167-two-sum-ii/) (find
two numbers hitting a target in a sorted array); we just rerun it for each anchor.

One last trap: duplicate triplets. An input like `[-2,0,0,2,2]` would happily
report `[-2,0,2]` twice unless we stop it. Sorting is what saves us -- equal
values end up sitting side by side, so right after we use a value (as an anchor or
as half of a pair) we skip past any neighbour equal to it. That way each distinct
triplet is recorded exactly once.

### Checkpoint A -- Anchor plus a pair

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** After fixing one element as the anchor `nums[i]`, what must the remaining two elements sum to?
- a) 0
- b) the negative of the anchor, `-nums[i]`
- c) the anchor value itself, `nums[i]`

<details><summary>Show answer</summary>

**(b)** -- the whole triplet must sum to 0, so the pair must sum to `0 - nums[i]`, which is `-nums[i]`. That value becomes the two-pointer target for the inner scan.

</details>

**Q2 (comprehend).** Why does the algorithm sort the array first?
- a) Sorting puts equal values side by side, which makes BOTH the two-pointer move rule and the duplicate-skipping work
- b) So the output triplets look tidy
- c) Because a list can only hold sorted values

<details><summary>Show answer</summary>

**(a)** -- the two-pointer direction logic needs sorted order (same reason as Two Sum II), and skipping equal neighbours to dedup triplets only works when equal values sit next to each other. Sorting buys both at once.

</details>

## Pseudocode

```text
function threeSum(nums):
    sort nums in non-decreasing order
    result = empty list
    for i from 0 to length - 3:
        if nums[i] is the same as nums[i-1]:   # skip duplicate first elements
            continue
        set left to i + 1
        set right to last index
        target to -nums[i]
        while left < right:
            pairSum = nums[left] + nums[right]
            if pairSum equals target:
                add [nums[i], nums[left], nums[right]] to result
                advance left past any value equal to nums[left]
                move right back past any value equal to nums[right]
                advance left by one
                move right back by one
            else if pairSum is less than target:
                advance left
            else:
                move right back by one
    return result
```

## Java Solution

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int left = i + 1, right = n - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }
}
```

The outer loop stops at `n - 2` because we need two more elements after `i`. The
first skip (`i > 0 && nums[i] == nums[i-1]`) prevents fixing the same first
value twice. Inside, after a hit, the two inner `while` loops slide `left` and
`right` to the *last* occurrence of the just-used value, and the `left++` /
`right--` then step onto a fresh value — together they guarantee the next pair
is genuinely different. `Arrays.asList` builds the immutable triplet; we do not
need to mutate it afterwards.

## Complexity

    Time:  O(n^2)  -- outer loop O(n); inner two-pointer scan O(n); sort is O(n log n), dominated.
    Space: O(1)    -- ignoring the output list; no auxiliary structure beyond a few indices.
                      (Java's sort on primitives uses O(log n) stack for quicksort.)

## Dry-Run

Step-by-step on `nums = [-1,0,1,2,-1,-4]`. After sorting: `[-4,-1,-1,0,1,2]`.

Outer loop, `i = 0` (`nums[i] = -4`, target `= 4`):

| left | right | nums[left] | nums[right] | sum | action |
|------|-------|------------|-------------|-----|--------|
| 1 | 5 | -1 | 2 | -3 | -3 < 0 -> left++ |
| 2 | 5 | -1 | 2 | -3 | < 0 -> left++ |
| 3 | 5 | 0 | 2 | -2 | < 0 -> left++ |
| 4 | 5 | 1 | 2 | -1 | < 0 -> left++ (left==right, exit) |

No triplet for `-4`.

`i = 1` (`nums[i] = -1`, target `= 1`):

| left | right | nums[left] | nums[right] | sum | action |
|------|-------|------------|-------------|-----|--------|
| 2 | 5 | -1 | 2 | 0 | == 0 -> record [-1,-1,2]; dedup (no equal neighbours); left=3, right=4 |
| 3 | 4 | 0 | 1 | 0 | == 0 -> record [-1,0,1]; dedup; left=4, right=3 (exit) |

`i = 2`: `nums[2] == nums[1]` (-1 == -1) -> **skip** (this is the dedup that
prevents re-recording `[-1,-1,2]` and `[-1,0,1]`).

`i = 3, 4`: only one or two elements remain, no full triplet possible.

Result: `[[-1,-1,2],[-1,0,1]]`. The sort plus dedup is what makes the output
exactly the unique triplets.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `nums = [-1,0,1]`. What triplets are returned?
- a) `[[-1,0,1]]`
- b) `[]`
- c) `[[-1,1,0]]`

<details><summary>Show answer</summary>

**(a)** -- after sorting (already sorted), anchor `i=0` is `-1`, target `1`. Inner: `left=1` (0), `right=2` (1), pair sum `0+1 = 1 == target`, so `[-1,0,1]` is recorded. Option (c) is the same values in a non-canonical order, which sorting prevents.

</details>

**Q2 (analyze).** What breaks if you delete the outer-loop duplicate check `if (i > 0 && nums[i] == nums[i-1]) continue;`?
- a) The same anchor value gets fixed more than once, so identical triplets are recorded multiple times
- b) The array fails to sort
- c) Nothing -- the inner dedup handles every case

<details><summary>Show answer</summary>

**(a)** -- on an input like `[-1,-1,0,1]`, both `-1`s would become anchors, each rediscovering the same triplets. The outer skip exists precisely so each distinct first value is anchored once.

</details>

**Q3 (transfer).** How would you extend this to 4Sum -- find all unique quadruplets summing to a target?

<details><summary>Show answer</summary>

Add a second outer loop: fix `i`, then fix `j = i+1 .. n`, then run the same two-pointer scan on the rest for `target - nums[i] - nums[j]`. That is O(n^3). Keep the sort-first discipline and the skip-equal-neighbours rule at EACH level so no quadruplet is recorded twice.

</details>

## Common mistakes

- **Forgetting to skip duplicates.** The two `while` loops inside the hit
  branch, plus the `nums[i] == nums[i-1]` check, are the whole point. Miss one
  and you get duplicate triplets.
- **Dedup then forgetting the final `left++; right--`.** The `while` loops only
  reach the *last* equal value; you still must step past it.
- **Not sorting first.** Without sorting, the two-pointer direction logic and
  the neighbour-equality dedup both break.
- **Returning the same triplet in different orders.** Sorting up front fixes a
  canonical order, so equal triplets look byte-identical and dedup is trivial.
- **Using a `Set` to dedup instead of skipping.** Works but costs O(n) extra
  space and is slower; the in-place skips are the intended solution.

## Related problems

- [0167 - Two Sum II](../0167-two-sum-ii/) - the inner loop is literally this
  problem with a fixed first element.
- [0011 - Container With Most Water](../0011-container-with-most-water/) - same
  opposite-ends skeleton, different decision rule.
- [0018 - 4Sum] - the natural extension: fix two elements, then two pointers.
