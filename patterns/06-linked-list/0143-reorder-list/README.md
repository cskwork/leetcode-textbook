# 0143 - Reorder List

**Difficulty:** Medium
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/reorder-list/

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

The target interleaves the *first half* of the list with the *second half reversed*. That
sentence already names the three sub-techniques, each of which we have practiced on its own:

1. **Find the middle** with slow / fast pointers.
2. **Reverse the second half** in place with the pointer flip.
3. **Interleave** the two halves by alternating links.

Why this decomposition works: after reversing the second half, a pointer walking that half
visits nodes in the order `Ln, Ln-1, Ln-2, ...` -- which is exactly the order they need to
appear between the front-half nodes. So we hold one finger on each half and stitch them
together two nodes at a time. No arrays, no recursion, no extra allocation.

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
