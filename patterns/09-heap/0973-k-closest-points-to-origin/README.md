# 0973 - K Closest Points to Origin

**Difficulty:** Medium
**Pattern:** Heap / Priority Queue
**LeetCode:** https://leetcode.com/problems/k-closest-points-to-origin/

## Concepts used

- **Heap** -- a structure that always gives back the smallest or largest item it holds in O(1),
  with O(log n) cost to add or remove one. [glossary](../../../docs/10-glossary.md#heap--priority-queue)
- **Min-heap / max-heap** -- a heap whose top is the *smallest* (min-heap) or *largest* (max-heap)
  item inside it. Java's `PriorityQueue` is a min-heap by default.
- **Array** -- a row of numbered slots holding values, accessed by position in O(1).
  [glossary](../../../docs/10-glossary.md#array)

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

Imagine a dart-throwing contest where you must report the **K closest** darts to the bullseye, out
of many thrown. You keep a board that fits exactly K darts. Once the board is full, every new dart
only stays if it is **closer** than the *farthest* dart currently on the board -- and if it is, you
remove that farthest one to make room. When all darts are in, the K on the board are your answer.

This is the mirror image of [0215 - Kth Largest Element in an Array](../0215-kth-largest-element-in-an-array/).
There we wanted the **K largest** values, so we kept the *smallest* of the survivors on top ready to
evict -- a [**min-heap**](../../../docs/10-glossary.md#heap--priority-queue) (top = smallest). Here
we want the **K smallest** distances, so the survivor we want to throw away is the *farthest* of the
K we kept -- the **largest** distance. That means we need the *largest* on top ready to pop, which
is a [**max-heap**](../../../docs/10-glossary.md#heap--priority-queue) (top = largest). Everything
else is the **size-K heap trick**: keep only K items, evict the worst when full; each step is
O(log k).

This "which one is on top" decision is the conceptual hurdle, so let's walk through it slowly.
"K closest" = "K smallest distances". The heap holds the K smallest distances seen so far. When a
new point arrives and the heap is full, we have to drop one -- and the right one to drop is the
farthest of the K we kept, because it is the least "closest". For that to be cheap, that farthest
one has to be sitting on top. A max-heap puts the **largest** distance on top, which is exactly the
farthest survivor -- so a max-heap is correct. A min-heap here would put the *closest* of the
survivors on top and polling it would discard the very points we want to keep.

We compare **squared** distances (`x*x + y*y`) instead of the real Euclidean distance. Squaring is
fine because for non-negative numbers, larger distance always means larger squared distance -- the
ordering is preserved -- and we avoid floating-point `sqrt` entirely.

**Smallest trace.** Take `points = [[3,3],[5,-1],[-2,4]]`, `k = 2`. Squared distances:
`[3,3] -> 18`, `[5,-1] -> 26`, `[-2,4] -> 20`. Walk the array, keeping a size-2 max-heap ordered by
distance. The **farthest** survivor is on top (that is the one we evict):

| Step | point  | dist^2 | heap after push (farthest on top) | size > 2? | heap after poll | why               |
|-----:|--------|-------:|-----------------------------------|----------:|-----------------|-------------------|
| 1    | [3,3]  | 18     | {18}                              | no        | {18}            | not full yet      |
| 2    | [5,-1] | 26     | {26, 18}                          | no        | {26, 18}        | size = 2 = K      |
| 3    | [-2,4] | 20     | {26, 18, 20}                      | yes       | {20, 18}        | evict 26 = [5,-1] |

Survivors: `[[3,3], [-2,4]]`. The point `[5,-1]` (distance 26) entered, but the heap overflowed, so
the farthest survivor -- 26 -- was evicted, and that was `[5,-1]` itself. Output
**`[[3,3], [-2,4]]`** in any order.

This is the same size-K heap trick as 0215 and [0347 - Top K Frequent](../0347-top-k-frequent-heap/)
-- only the heap order is flipped because we want the smallest distances instead of the largest
values.

### Checkpoint A -- Why a max-heap this time

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** For "k closest" (= k smallest distances), which heap of size k do you use?
- a) A min-heap
- b) A max-heap, so the farthest survivor is on top to evict
- c) Either works

<details><summary>Show answer</summary>

**(b)** -- the root must be the survivor you discard, and for "k closest" that is the farthest of the k kept (the largest distance). A max-heap puts the largest distance on top, ready to poll.

</details>

**Q2 (comprehend).** Why compare squared distance (`x*x + y*y`) instead of the real Euclidean distance?
- a) Because squaring preserves order for non-negative numbers and avoids sqrt and floating-point error
- b) Because squaring is more accurate
- c) Because the heap only accepts integers

<details><summary>Show answer</summary>

**(a)** -- distances are non-negative, and squaring is strictly increasing there, so the closest points stay closest. Dropping the sqrt keeps everything in integers and skips a slow, lossy operation.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `points = [[0,1],[1,0],[2,2]]`, `k = 2`. Which two points are returned (any order)?
- a) [[0,1],[1,0]] (both squared distance 1)
- b) [[2,2],[0,1]]
- c) [[2,2]]

<details><summary>Show answer</summary>

**(a)** -- squared distances are 1, 1, and 8. The size-2 max-heap keeps the two smallest (both 1); when 8 enters the heap overflows and evicts the farthest survivor, which is the 8 itself.

</details>

**Q2 (analyze).** What goes wrong if you used a min-heap here instead of a max-heap?
- a) The closest points get evicted as they arrive, leaving the farthest k -- the opposite of "closest"
- b) Nothing changes, the result is the same
- c) It throws an exception

<details><summary>Show answer</summary>

**(a)** -- a min-heap puts the smallest distance on top, so every overflow polls a close point. After the whole pass the heap holds the k farthest points, the mirror of what was asked.

</details>

**Q3 (transfer).** Suppose ties in distance had to be broken by smaller x-coordinate first, then smaller y. What is the only change needed?

<details><summary>Show answer</summary>

Extend the comparator: compare distance first (max-heap direction), and on equal distance compare x ascending, then y ascending. The heap mechanics (push, size-k trim, drain) are unchanged -- only the ordering gains tiebreakers.

</details>

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
