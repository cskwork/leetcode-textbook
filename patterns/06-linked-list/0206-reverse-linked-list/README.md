# 0206 - Reverse Linked List

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/reverse-linked-list/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores a value and an arrow (pointer) to the next node; you can only walk forward. [glossary](../../../docs/10-glossary.md#linked-list)
- **Two pointers** -- two variables that walk the structure together; here `prev` and `curr` move in lockstep so each arrow can be flipped safely. [glossary](../../../docs/10-glossary.md#two-pointers)
- **In-place** -- modifying the existing nodes directly using only O(1) extra memory, instead of building a second list. [glossary](../../../docs/10-glossary.md#in-place)

## Problem

Given the `head` of a singly linked list, reverse the list and return the new head.

Signature:

    ListNode reverseList(ListNode head)

Examples:

    Input:  head = [1,2,3,4,5]
    Output: [5,4,3,2,1]

    Input:  head = [1,2]
    Output: [2,1]

    Input:  head = []
    Output: []

## Intuition

Think of a linked list as a treasure hunt: each clue (a [node](../../../docs/10-glossary.md#linked-list))
holds a value and a *pointer* -- a reference to the next node, like an arrow
drawn on paper pointing from one clue to the next. The last clue's arrow points
to "nothing" (`null`), which ends the hunt. To reverse the list, picture a
one-way street where every arrow points forward; we want to flip every single
arrow so the street runs the other way.

Let us watch the smallest meaningful case, `1 -> 2 -> 3 -> null`:

- Before: `1 -> 2 -> 3 -> null`
- After:  `3 -> 2 -> 1 -> null`

Node `1` used to point at `2`; now it must point at `null` (it is the new tail).
Node `3` used to point at `null`; now it must point at `2` (it is the new head).
Every node's arrow gets flipped to point at the node that used to come *before*
it.

Here is the catch that freezes beginners. The moment we overwrite a node's arrow
(say, point `1` at `null`), the old arrow to `2` is gone -- and we still need to
reach `2` to flip it. So before changing anything we must *save* where the arrow
currently points. That gives a four-step dance, repeated for every node, using
two fingers called `prev` (the node we just came from) and `curr` (the node we
are fixing right now):

1. **Save** the next node: `next = curr.next` (remember where the arrow points).
2. **Flip**: `curr.next = prev` (point this node's arrow backward, at `prev`).
3. **Advance `prev`**: `prev = curr` (the current node becomes "behind" next
   round).
4. **Advance `curr`**: `curr = next` (step forward using the arrow we saved).

This is [two pointers](../../../docs/10-glossary.md#two-pointers) walking the
list in lockstep, and because we only restring existing arrows and never allocate
new nodes, the work is [in-place](../../../docs/10-glossary.md#in-place) -- O(1)
extra memory. When `curr` finally steps off the end (becomes `null`), `prev` is
sitting on the old tail, which is the new head, and that is what we return.

### Checkpoint A -- The four-step arrow flip

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** During one step of the reverse loop, what is the very first thing you must do with `curr.next`?
- a) Overwrite it with `prev`
- b) Save it into a temporary variable
- c) Set it to `null`

<details><summary>Show answer</summary>

**(b)** -- after `curr.next = prev`, the old link to the rest of the list is gone; saving `next = curr.next` first is what lets `curr` step forward.

</details>

**Q2 (comprehend).** When the `while (curr != null)` loop exits, which pointer is the new head of the reversed list?
- a) `curr`
- b) `prev`
- c) `head`

<details><summary>Show answer</summary>

**(b)** -- the loop stops because `curr` became `null`; `prev` is on the last node touched, which is the old tail and therefore the new head.

</details>

## Pseudocode

    function reverseList(head):
        prev <- null
        curr <- head
        while curr is not null:
            next <- curr.next      # save the rest of the list before breaking the link
            curr.next <- prev      # flip this node's arrow to point backwards
            prev <- curr           # advance prev
            curr <- next           # advance curr using the saved pointer
        return prev                # when curr is null, prev is the new head

## Java Solution

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
}
```

`prev` starts as `null` so the first node's arrow flips into the void -- exactly what the
new tail needs. We save `curr.next` into `next` *before* overwriting it, because after
`curr.next = prev` the original link is gone. Both `prev` and `curr` advance from the saved
`next`/old `curr`, never from the mutated list. When the loop exits, `curr` has run off the
end (it is `null`) and `prev` sits on the last node we touched -- the old tail, which is the
new head. No allocations happen; we are just restringing existing nodes, which is why space
is O(1).

### A note on the recursive form

The problem is famous for an elegant one-line recursive version. It is worth knowing but not
the default: it costs O(n) call-stack space and fails on very long lists. The iterative
version above is what the rest of this section builds on.

## Complexity

    Time:  O(n)  -- one pass, four pointer assignments per node
    Space: O(1)  -- only two local pointers; nodes are reused, not copied

## Dry-Run

Input `head = [1, 2, 3, 4, 5]`. Pointers shown after each iteration's four statements:

| Step | prev | curr (start of iter) | next (saved) | action on curr | list state (by arrow) |
|-----:|------|----------------------|--------------|----------------|------------------------|
| 0    | null | 1                    | -            | -              | 1->2->3->4->5->null    |
| 1    | 1    | 2                    | 2            | 1->null        | null<-1   2->3->4->5   |
| 2    | 2    | 3                    | 3            | 2->1           | null<-2<-1  3->4->5    |
| 3    | 3    | 4                    | 4            | 3->2           | null<-3<-2<-1  4->5    |
| 4    | 4    | 5                    | 5            | 4->3           | null<-4<-3<-2<-1  5    |
| 5    | 5    | null                 | null         | 5->4           | null<-5<-4<-3<-2<-1    |

Loop exits because `curr` is now `null`. Return `prev` = node `5`. Output: `[5, 4, 3, 2, 1]`.

Edge cases: empty list `[]` -- `curr` starts `null`, loop never runs, return `prev` = null.
Single node `[1]` -- one iteration flips `1.next` to null, `prev` becomes `1`, return `1`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `reverseList` on `head = [7, 8, 9]`. After the loop exits, which node does `prev` point at, and what is the output?
- a) `prev` = node 7, output `[7, 8, 9]`
- b) `prev` = node 9, output `[9, 8, 7]`
- c) `prev` = `null`, output `[]`

<details><summary>Show answer</summary>

**(b)** -- each iteration flips one arrow and advances; when `curr` steps off the end `prev` sits on the old tail (9), and reading from it gives `[9, 8, 7]`.

</details>

**Q2 (analyze).** What happens if you swap the order and write `curr.next = prev;` *before* `next = curr.next;` on a 3-node list?
- a) The list reverses correctly
- b) The link to the remaining nodes is destroyed, so only the first node is reversed and the loop ends early
- c) It throws a `NullPointerException`

<details><summary>Show answer</summary>

**(b)** -- overwriting `curr.next` first throws away the pointer to node 2, so `curr` has nowhere to advance to and the rest of the list is lost.

</details>

**Q3 (transfer).** Suppose you wanted to reverse only the *first k* nodes and leave the rest attached. In words, what changes?

<details><summary>Show answer</summary>

Run the same four-step dance exactly k times, then point the *original* head (now the tail of the reversed prefix) at the node `curr` is resting on (the first unreversed node). That keeps the unchanged tail spliced on after the reversed prefix.

</details>

## Common mistakes

- Overwriting `curr.next` *before* saving it into `next`. The rest of the list is lost and
  the loop ends after one node.
- Advancing `curr` by reading `curr.next` *after* the flip -- you will follow the new
  backward arrow back to `prev` and spin forever.
- Returning `head` instead of `prev`. After the flip `head` still names node `1`, which is
  now the tail, not the head.
- Returning `curr`. It is `null` at the end.
- Allocating a second list and copying values. It works but wastes O(n) space and breaks the
  spirit of the problem, which is in-place pointer surgery.

## Related problems

- [0143 - Reorder List](../0143-reorder-list/) - reverse is a sub-step: the second half gets
  flipped before interleaving.
- [0234 - Palindrome Linked List](../0234-palindrome-linked-list/) - reverse the back half,
  compare, done.
- [0021 - Merge Two Sorted Lists](../0021-merge-two-sorted-lists/) - a different pointer
  technique on the same data structure, with a dummy head.
