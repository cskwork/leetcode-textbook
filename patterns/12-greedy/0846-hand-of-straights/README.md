# 0846 - Hand of Straights

**Difficulty:** Medium
**Pattern:** Greedy
**LeetCode:** https://leetcode.com/problems/hand-of-straights/

## Concepts used

- **Greedy** -- make the locally-best choice at each step and never revisit it; works only when you can prove that choice is part of some optimal solution. [glossary](../../../docs/10-glossary.md#greedy)
- **Sorting** -- putting elements in order (ascending or descending); standard sorts run in O(n log n). [glossary](../../../docs/10-glossary.md#sorting)
- **Hash map** -- a key->value lookup table, O(1) average; here we use a sorted one (`TreeMap`) to count cards. [glossary](../../../docs/10-glossary.md#hash-map)
- **Invariant** -- a condition that is always true at the start of every loop iteration; stating it clearly is how you prove a loop correct. [glossary](../../../docs/10-glossary.md#invariant)

## Problem

Alice holds `n` cards, given as an integer array `hand` where `hand[i]` is
the value of the `i`-th card. She wants to rearrange them into groups of
size `groupSize`, where each group consists of `groupSize` **consecutive**
cards (e.g. `[2, 3, 4, 5]` is valid, `[2, 4, 5]` is not). Return `true` if
she can partition all cards this way, otherwise `false`.

Signature:

    boolean isNStraightHand(int[] hand, int groupSize)

Examples (verbatim from LeetCode):

    Input:  hand = [1,2,3,6,2,3,4,7,8], groupSize = 3
    Output: true
    Explanation: groups [1,2,3], [2,3,4], [6,7,8].

    Input:  hand = [1,2,3,4,5], groupSize = 4
    Output: false
    Explanation: cannot split 5 cards into groups of 4.

## Intuition

The trigger: "partition into groups of consecutive values" — a classic
"can I commit greedily to the next group?" problem. The key observation is
that the **smallest unused card** has no freedom: it must be the start of
some group, and that group must be `[card, card+1, card+2, ...,
card+groupSize-1]`. There is no alternative placement for the smallest
card, because no smaller card exists to extend a group "from below".

So the locally-best choice is forced: at every step, take the smallest
remaining card and greedily consume its required consecutive run. If at
any point the run is incomplete (a needed value is missing), return false.
If we consume every card this way, return true.

To make "smallest unused" fast, sort the cards and keep a frequency map.
Walk the sorted keys; for each card that still has positive count, try to
peel off a group of `groupSize` consecutive cards starting there. A
`TreeMap` (red-black tree) gives sorted iteration and O(log n) updates, so
the whole algorithm is O(n log n).

**Proof sketch (why the smallest-card rule is safe).** Suppose an optimal
partition exists that does *not* put the globally smallest card `c` at the
start of its group. Then `c` is somewhere in the middle of some group
`[..., c, c+1, ...]`, which requires a `c-1` — impossible, since `c` is the
smallest overall. Contradiction. So in *every* valid partition, the
smallest card starts a group. After we commit that group and remove its
cards, the same argument applies recursively to the new smallest card. By
the exchange argument, greedy is optimal.

### Checkpoint A -- The smallest card has no choice

Pause and answer before expanding.

**Q1 (recall).** At each step, which card is chosen to start the next group?
- a) The most frequent card
- b) The smallest remaining card
- c) Any card that still has a positive count

<details><summary>Show answer</summary>

**(b)** -- the smallest remaining card cannot be placed in the middle of a group, so it is forced to start one.

</details>

**Q2 (comprehend).** Why MUST the smallest remaining card start a group, rather than sit in the middle of one?
- a) Because smaller cards are worth more
- b) Because a middle slot would need a `card - 1` to come before it, and no such card exists
- c) Because the cards are sorted alphabetically

<details><summary>Show answer</summary>

**(b)** -- sitting in the middle requires a predecessor one less, which cannot exist when the card is the global minimum. So it must open a group; this is the exchange argument.

</details>

## Pseudocode

```text
function isNStraightHand(hand, groupSize):
    if length of hand is not divisible by groupSize:
        return false
    counts = map of card value -> frequency      # a sorted map
    for each card in hand:
        counts[card] += 1

    while counts is not empty:
        first = the smallest key in counts
        for offset from 0 to groupSize - 1:
            need = first + offset
            if need is not in counts or counts[need] is 0:
                return false
            counts[need] -= 1
            if counts[need] is 0:
                remove need from counts          # keeps "smallest key" correct
    return true
```

Removing zero-count entries from the map is what makes "smallest key" mean
"smallest key with a remaining card".

## Java Solution

```java
import java.util.TreeMap;

class Solution {
    public boolean isNStraightHand(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) {
            return false;
        }
        TreeMap<Integer, Integer> counts = new TreeMap<>();
        for (int card : hand) {
            counts.merge(card, 1, Integer::sum);
        }
        while (!counts.isEmpty()) {
            int first = counts.firstKey();
            // The smallest remaining card must start a group; consume its run.
            for (int offset = 0; offset < groupSize; offset++) {
                int need = first + offset;
                Integer have = counts.get(need);
                if (have == null || have == 0) {
                    return false;
                }
                if (have == 1) {
                    counts.remove(need);
                } else {
                    counts.put(need, have - 1);
                }
            }
        }
        return true;
    }
}
```

`TreeMap` is chosen over `HashMap` because we need ordered keys (so
`firstKey()` is the smallest remaining card in O(1)). `counts.merge(card,
1, Integer::sum)` is the counting idiom: insert-or-add-one in one call. The
`if (have == 1) remove; else put` dance keeps zero counts out of the map,
which is essential — otherwise `firstKey()` could return a card that has
already been fully consumed, and the algorithm would loop forever or
misfire. The size pre-check at the top short-circuits the obvious
impossible case before any work.

## Complexity

    Time:  O(n log n)  -- each card is inserted once and removed once, each O(log n); n cards total.
    Space: O(n)        -- the frequency map holds every distinct card value.

## Dry-Run

Step-by-step on `hand = [1,2,3,6,2,3,4,7,8]`, `groupSize = 3`:

Initial counts (sorted): `{1:1, 2:2, 3:2, 4:1, 6:1, 7:1, 8:1}`.

| Iter | first | need run   | counts after consuming the run              | result |
|------|-------|------------|---------------------------------------------|--------|
| 1    | 1     | [1, 2, 3]  | {2:1, 3:1, 4:1, 6:1, 7:1, 8:1}              | ok     |
| 2    | 2     | [2, 3, 4]  | {6:1, 7:1, 8:1}                             | ok     |
| 3    | 6     | [6, 7, 8]  | {}                                          | ok     |

Map empty -> return **true**. The groups are `[1,2,3]`, `[2,3,4]`, `[6,7,8]`.

For the impossible case `hand = [1,2,3,4,5]`, `groupSize = 4`:

`5 % 4 = 1 != 0` -> return **false** immediately at the size pre-check.

For `hand = [1,2,3,4,5]`, `groupSize = 5`:

| Iter | first | need run       | counts after              | result |
|------|-------|----------------|---------------------------|--------|
| 1    | 1     | [1,2,3,4,5]    | {}                        | ok     |

Return **true**: one group of all five cards.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `hand = [1, 2, 2, 3, 3, 4]`, `groupSize = 3`. What is returned?
- a) true (groups [1,2,3] and [2,3,4])
- b) false
- c) true only after re-sorting

<details><summary>Show answer</summary>

**(a)** -- counts start {1:1, 2:2, 3:2, 4:1}. First run [1,2,3] leaves {2:1,3:1,4:1}; second run [2,3,4] empties the map, so return true.

</details>

**Q2 (analyze).** On `hand = [1, 2, 3, 4]`, `groupSize = 2`, the answer is true. What goes wrong if zero-count entries are left in the map instead of being removed?
- a) `firstKey()` would keep returning an already-used card, eventually hitting a count of 0 and wrongly returning false
- b) Nothing -- the algorithm still works
- c) It throws a null-pointer error immediately

<details><summary>Show answer</summary>

**(a)** -- after consuming [1,2], if those keys stay at 0, `firstKey()` returns 1 again; `counts.get(1)` is 0, triggering a false "false". The remove-when-zero rule keeps "smallest key" meaning "smallest key with a remaining card".

</details>

**Q3 (transfer).** Suppose each group only needs to be 3-or-more consecutive cards, of any length (not a fixed size). Does "consume the smallest card's run greedily" still decide everything? What extra information is needed?

<details><summary>Show answer</summary>

No -- with variable lengths you cannot fix the run. The smallest card must still start a run, but you must also decide whether to EXTEND an existing run ending at `card-1` or start a new one, typically tracked with a second map of open runs (this is LC 659).

</details>

## Common mistakes

- **Forgetting the size pre-check.** Without `hand.length % groupSize !=
  0` the loop may consume most of the cards and then fail only at the
  very end with a confusing "missing card" return.
- **Leaving zero-count entries in the map.** If you decrement but never
  `remove`, `firstKey()` keeps returning fully-used cards and the loop
  spins forever or returns a wrong `false`. Always pair a decrement with a
  remove-when-zero.
- **Iterating over a `HashMap` and trusting iteration order.** The
  smallest-card argument requires sorted order; a plain `HashMap` gives no
  ordering guarantee. Use `TreeMap` or sort the keys separately.
- **Starting each group from a card that is not the smallest remaining.**
  A common wrong rule is "start from any card with positive count". That
  breaks the proof and can paint you into a corner where a smaller card
  has nowhere to go.
- **Using `Integer == ` to compare counts.** After `counts.get(need)`,
  auto-unboxing two `Integer`s and comparing with `==` can fail for
  values outside the cached range `[-128, 127]`. Compare against `int`
  locals or use `.intValue()`.

## Related problems

- [0128 - Longest Consecutive Sequence] - also about consecutive runs, but
  asks for the longest one rather than a partition (Pattern 1, hashing).
- [0659 - Split Array into Consecutive Subsequences] - the same greedy
  consume-in-sorted-order idea, but with variable-length subsequences.
- [0350 - Intersection of Two Arrays II] - another counting-with-TreeMap
  pattern, though not greedy.
