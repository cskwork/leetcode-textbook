# 0206 - Reverse Linked List

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/reverse-linked-list/

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

Every node currently points forward to the next one. To reverse the list we must flip every
one of those arrows so each node instead points back at its predecessor. The catch: the
moment we overwrite `curr.next`, we lose the pointer to the rest of the list. So on each
node we do three things in order -- remember where `next` used to point, redirect the arrow
backward, then step forward using the value we saved. This is Template A from the pattern
intro, and it is the single most reused snippet in the whole section: Reorder, Palindrome,
and Add-Two-Numbers' reversals all lean on it.

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
