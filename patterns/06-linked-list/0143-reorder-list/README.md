# 0143 - Reorder List

**Difficulty:** Medium
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/reorder-list/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores a value and an arrow (pointer) to the next node; only forward traversal is possible. [glossary](../../../docs/10-glossary.md#linked-list)
- **Two pointers** -- a slow pointer (one step per turn) and a fast pointer (two steps per turn) that locate the middle in one pass. [glossary](../../../docs/10-glossary.md#two-pointers)
- **In-place** -- rewiring the existing nodes' arrows with O(1) extra memory, instead of copying values into an array. [glossary](../../../docs/10-glossary.md#in-place)

## Problem

You are given the `head` of a singly linked list `L0 -> L1 -> ... -> Ln-1 -> Ln`. Reorder it
in place to `L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 -> ...`. You may not modify the values in
the nodes -- only the links between nodes may change. Return nothing (mutate the list in
place).

Signature:

    void reorderList(ListNode head)

Examples:

    Input:  head = [1,2,3,4]      Output: [1,4,2,3]
    Input:  head = [1,2,3,4,5]    Output: [1,5,2,4,3]
    Input:  head = [1]            Output: [1]

## Intuition

The target order interleaves the front half of the list with the *back half
reversed*: `L0, L1, L2, ...` becomes `L0, Ln, L1, Ln-1, L2, ...`. Think of
shuffling a deck by taking one card from the top, one from the bottom, one from
the top, one from the bottom -- except the "bottom" cards must be read in
reverse order. A [linked list](../../../docs/10-glossary.md#linked-list) only
points forward (each node's *pointer* is an arrow to the next node), so we
cannot read the bottom half backwards directly. But we *can* flip its arrows, and
once flipped, walking that half forward visits the bottom cards in exactly the
reverse order we need.

Smallest meaningful case, `1 -> 2 -> 3 -> 4` (target `1 -> 4 -> 2 -> 3`):

- Front half: `1 -> 2`. Back half: `3 -> 4`.
- Reverse the back half: `4 -> 3`.
- Interleave: take `1` (front), then `4` (back), then `2` (front), then `3`
  (back) -- giving `1 -> 4 -> 2 -> 3`.

So the solution is three steps, each a technique practiced on its own:

1. **Find the middle**, using the trick from *Middle of the Linked List* (LC 876):
   one pointer takes one step per turn, another takes two; when the fast one runs
   off the end, the slow one is sitting on the middle.
2. **Reverse the second half**, using the arrow-flipping dance from
   [Reverse Linked List (LC 206)](../0206-reverse-linked-list/): for each node,
   save its next arrow, point the node backward at the previous one, advance.
3. **Interleave** the two halves: hold one finger on each and stitch them
   together two nodes at a time.

After step 2, a finger walking the reversed back half meets `Ln, Ln-1, Ln-2,
...` -- exactly the order those nodes must appear between the front-half nodes --
so the interleave is just "attach one from each side, repeat". Before
interleaving we split the list cleanly with `slow.next = null`; otherwise the
front half stays chained into the reversed back half and we would walk in a
circle. Everything is [in-place](../../../docs/10-glossary.md#in-place) pointer
surgery: no arrays, no recursion, no new nodes.

### Checkpoint A -- The interleave recipe

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** List the three steps of `reorderList` in order.
- a) reverse, find middle, interleave
- b) find middle, reverse the back half, interleave the two halves
- c) interleave, reverse, find middle

<details><summary>Show answer</summary>

**(b)** -- first locate the split, then flip the back half so it reads `Ln, Ln-1, ...`, then stitch one-from-each-side together.

</details>

**Q2 (comprehend).** Right after reversing the back half, the code sets `slow.next = null`. Why?
- a) To free memory
- b) To detach the front half from the reversed back half, so the interleave does not walk in a circle
- c) To reverse the front half too

<details><summary>Show answer</summary>

**(b)** -- without it the front half's tail still points into the reversed back half, and the interleave would chase its own tail forever.

</details>

## Pseudocode

    function reorderList(head):
        if head is null or head.next is null:
            return                       # nothing to reorder

        # 1. find the middle (slow stops on first-middle for even length)
        slow <- head
        fast <- head
        while fast.next is not null and fast.next.next is not null:
            slow <- slow.next
            fast <- fast.next.next

        # 2. split and reverse the back half
        second <- reverse(slow.next)
        slow.next <- null                # detach front half from the (now reversed) back half

        # 3. interleave: front = head, back = second
        first <- head
        while second is not null:
            tmpFirst <- first.next
            tmpSecond <- second.next
            first.next <- second         # front node -> back node
            second.next <- tmpFirst      # back node -> next front node
            first  <- tmpFirst
            second <- tmpSecond

    function reverse(head):
        prev <- null
        curr <- head
        while curr is not null:
            next <- curr.next
            curr.next <- prev
            prev <- curr
            curr <- next
        return prev

## Java Solution

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode second = reverse(slow.next);
        slow.next = null;                 // detach front half from back half

        ListNode first = head;
        while (second != null) {
            ListNode tmpFirst = first.next;
            ListNode tmpSecond = second.next;
            first.next = second;
            second.next = tmpFirst;
            first = tmpFirst;
            second = tmpSecond;
        }
    }

    private ListNode reverse(ListNode head) {
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

The slow/fast loop is the same first-middle variant used in Palindrome: on an even-length
list `slow` stops on the left-middle, so the back half has exactly as many nodes as the front.
`slow.next = null` is the line that actually splits the list into two -- without it the front
half's tail still points into the reversed back half and the interleave would form a cycle.
In the interleave loop we save *both* `first.next` and `second.next` before rewriting, because
both links are about to be overwritten; then we splice (`first -> second -> old first.next`)
and step both fingers forward. The loop is driven by `second` (the back half), which is never
longer than the front half, so `first` never falls off the end -- on odd total length the
leftover front-half node stays correctly as the final tail.

## Complexity

    Time:  O(n)  -- three linear passes (middle, reverse, interleave), each touching ~n/2 nodes
    Space: O(1)  -- a constant number of pointers; nodes are rewired in place

## Dry-Run

Input `head = [1, 2, 3, 4, 5]`. Nodes A=1, B=2, C=3, D=4, E=5.

**Step 1 -- find middle:**

| iteration | slow after | fast after |
|----------:|------------|------------|
| start     | A          | A          |
| 1         | B          | C          |
| 2         | C          | E          |

`fast.next` is null (E.next), loop stops. `slow` on C.

**Step 2 -- reverse back half `slow.next` = D->E:** result `second` = E->D. Then
`slow.next = null`: front half = A->B->C->null.

**Step 3 -- interleave** (`first` walks A,B,C; `second` walks E,D):

| iter | first | second | tmpFirst | tmpSecond | after splice |
|-----:|-------|--------|----------|-----------|--------------|
| 1    | A     | E      | B        | D         | A->E->B      |
| 2    | B     | D      | C        | null      | B->D->C      |

`second` is now null -> stop. `first` = C, but the loop has ended; C stays as the tail with
C.next already null from the detach step.

Final chain read from head: A -> E -> B -> D -> C = `[1, 5, 2, 4, 3]`. Correct.

Even-length `[1, 2, 3, 4]`: slow stops on B; back half C->D reverses to D->C; interleave gives
A->D->B->C = `[1, 4, 2, 3]`. Correct.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `reorderList` on `head = [1, 2, 3, 4, 5, 6]`. What is the final list?
- a) `[1, 6, 2, 5, 3, 4]`
- b) `[6, 1, 5, 2, 4, 3]`
- c) `[1, 2, 3, 4, 5, 6]`

<details><summary>Show answer</summary>

**(a)** -- slow stops on 3, the back half `4->5->6` reverses to `6->5->4`, and interleaving front `[1,2,3]` with it yields `1, 6, 2, 5, 3, 4`.

</details>

**Q2 (analyze).** What goes wrong if you omit `slow.next = null` after reversing the back half?
- a) The front half stays chained into the reversed back half, and the interleave walks in a circle
- b) The whole list gets reversed
- c) Nothing; the line is optional

<details><summary>Show answer</summary>

**(a)** -- the unbroken link turns the two halves into a loop, so the splice pointers never reach `null` and the list is corrupted.

</details>

**Q3 (transfer).** The interleave loop saves *both* `first.next` and `second.next` before splicing. Why must both be saved, and what breaks if only one is?

<details><summary>Show answer</summary>

The splice overwrites *both* links -- `first.next` becomes the back node and `second.next` becomes the next front node -- so if either successor is not saved first that finger loses its way forward and points at the wrong node, tangling the rest of the weave.

</details>

## Common mistakes

- Forgetting `slow.next = null` after reversing. The front half stays chained into the
  reversed back half, the interleave then walks in a circle, and you get an infinite loop or
  a corrupted list.
- Reversing the whole list instead of just the back half. You then have nothing to interleave
  with -- the original front is destroyed.
- Saving only one of `first.next` / `second.next`. Both links are overwritten by the splice,
  so one finger will dangle if its successor was not saved first.
- Driving the interleave with `first` instead of `second`. On odd length the front half has
  one extra node and `first` would dereference null one step too late.
- The wrong slow/fast condition (`fast != null && fast.next != null`) lets `slow` overshoot on
  even lists, making the halves unequal and the interleave off by one.

## Related problems

- [0234 - Palindrome Linked List](../0234-palindrome-linked-list/) - same three-step skeleton
  (middle, reverse, compare); this problem just splices instead of comparing.
- [0206 - Reverse Linked List](../0206-reverse-linked-list/) - the reverse helper is copied
  verbatim.
- [0019 - Remove Nth Node From End of List](../0019-remove-nth-node-from-end-of-list/) -
  another multi-step pointer-surgery problem that rewards drawing the links first.
