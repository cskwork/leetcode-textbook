# 0973 - K Closest Points to Origin

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/k-closest-points-to-origin/

## Problem

Given an array of `points` where `points[i] = [xi, yi]` represents a point on the X-Y plane, and an
integer `k`, return the `k` closest points to the origin `(0, 0)`. Distance is the usual Euclidean
distance but you may compare squared distances (no square root needed). The answer may be in any
order.

Signature:

    int[][] kClosest(int[][] points, int k)

Examples:

    Input:  points = [[1,3],[-2,2]], k = 1
    Output: [[-2,2]]     # dist^2 of [1,3] = 10, of [-2,2] = 8 -> closest is [-2,2]

    Input:  points = [[3,3],[5,-1],[-2,4]], k = 2
    Output: [[3,3],[-2,4]]   # dist^2 = 18, 26, 20 -> two smallest are 18 and 20

## Intuition

"K closest" means "k smallest distances", which is the mirror image of "k largest". Recall the size-K
heap rule: the root must be the survivor you want to **evict**. For the k smallest distances, the
element to throw away when the heap overflows is the **farthest** of the k closest -- so we need a
**max-heap of size k keyed on distance**. Each incoming point is pushed; if the heap then holds more
than k, we poll the largest distance, which removes the farthest survivor. After the scan, the heap
contains exactly the k closest points.

We compare **squared** distances (`x*x + y*y`) to avoid the floating-point `sqrt`; squaring
preserves order for non-negative numbers.

## Pseudocode

    function kClosest(points, k):
        create an empty max-heap, ordered by distance descending   # farthest survivor on top
        for each point p in points:
            d = p.x * p.x + p.y * p.y                              # squared distance to origin
            push (d, p) into the heap
            if heap size > k:
                remove the root                                    # evict the farthest of the k closest
        drain the heap and collect the points                      # these k points are the closest
        return them in any order

## Java Solution

```java
import java.util.*;

class Solution {
    public int[][] kClosest(int[][] points, int k) {
        // max-heap of size k keyed on squared distance: root = farthest survivor, evicted when overfull
        PriorityQueue<int[]> heap = new PriorityQueue<>(
            (a, b) -> Integer.compare(b[0], a[0]));

        for (int[] p : points) {
            int d = p[0] * p[0] + p[1] * p[1];
            heap.offer(new int[]{d, p[0], p[1]});
            if (heap.size() > k) {
                heap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            int[] e = heap.poll();
            result[i][0] = e[1];
            result[i][1] = e[2];
        }
        return result;
    }
}
```

Each heap entry is `{squaredDistance, x, y}`. The comparator `Integer.compare(b[0], a[0])` reverses
the natural order on the distance field, making this a **max-heap** -- so the root is the farthest
of the k survivors and is the one removed when the heap grows past k. `Integer.compare` (rather than
`b[0] - a[0]`) keeps the comparator safe from overflow; here the squared distance is at most
`2 * 10^8` for coordinates up to `10^4`, which fits in an `int`, but the habit pays off on problems
with bigger values. After the loop the surviving k entries are drained into the result; their order
does not matter per the problem statement.

## Complexity

    Time:  O(N log k)  -- N points, each push/poll bounded by log(k+1) because the heap is capped
    Space: O(k)        -- the heap holds at most k entries

## Dry-Run

Trace `points = [[3,3],[5,-1],[-2,4]]`, `k = 2`. Squared distances: `[3,3] -> 18`, `[5,-1] -> 26`,
`[-2,4] -> 20`.

The heap is a max-heap on distance, shown sorted with the **farthest** survivor on the left (the
root that gets evicted):

| Step | point    | dist^2 | heap after push (dist shown) | size > 2? | heap after poll | survivors (point) |
|-----:|----------|-------:|------------------------------|----------:|-----------------|--------------------|
| 1    | [3,3]    | 18     | {18}                         | no        | {18}            | [[3,3]]            |
| 2    | [5,-1]   | 26     | {26, 18}                     | no        | {26,18}         | [[3,3],[5,-1]]     |
| 3    | [-2,4]   | 20     | {26, 18, 20}                 | yes       | {20,18}         | [[3,3],[-2,4]]     |  (evict dist 26 = [5,-1])

Final survivors: `[[3,3], [-2,4]]`. Return (in any order) **`[[3,3],[-2,4]]`**.

## Common mistakes

- Using a **min-heap** here. "K closest" = "k smallest distances", and for that you need a max-heap
  of size k so the farthest survivor (the largest distance) is the one evicted. A min-heap would
  instead discard the closest points as they arrive. This is the exact mirror of the 215/347 pitfall
  -- read the "min vs max" table in the pattern intro.
- Comparing the wrong field. If the comparator ignores the distance and falls back to object
  identity, the heap returns essentially random points. Always compare the stored score explicitly.
- Forgetting the `size > k` cap, letting all N points accumulate -- correct but O(N log N) instead
  of O(N log k), losing the whole point of the pattern.
- Calling `Math.sqrt`. It is unnecessary (squared distance preserves order for non-negative values),
  introduces floating-point error, and is slower. Compare `x*x + y*y` directly.
- Using `a[0] - b[0]` in the comparator. With small bounded coordinates it happens to be safe here,
  but for larger inputs the subtraction can overflow and return a wrong sign. Prefer
  `Integer.compare`.

## Related problems

- [0215 - Kth Largest Element in an Array](../0215-kth-largest-element-in-an-array/) - the same
  size-k cap but a min-heap, because there you evict the smallest survivor.
- [0347 - Top K Frequent (heap)](../0347-top-k-frequent-heap/) - min-heap of size k keyed on a
  computed score (frequency), versus this problem's max-heap keyed on distance.
- [0703 - Kth Largest Element in a Stream](../0703-kth-largest-element-in-a-stream/) - the streaming
  cousin of the size-k min-heap.
