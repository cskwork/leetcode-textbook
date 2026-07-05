# 0019 - Remove Nth Node From End of List

**Difficulty:** Medium
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/remove-nth-node-from-end-of-list/

## Problem

Given the `head` of a linked list, remove the nth node from the end of the list and return
the new head. The input is guaranteed valid: the list has at least `n` nodes.

Signature:

    ListNode removeNthFromEnd(ListNode head, int n)

Examples:

    Input:  head = [1,2,3,4,5], n = 2
    Output: [1,2,3,5]          # 4 is the 2nd from the end

    Input:  head = [1], n = 1
    Output: []

    Input:  head = [1,2], n = 1
    Output: [1]

## Intuition

To delete a node from a singly linked list you must stand on its *predecessor* and skip past
it: `prev.next = prev.next.next`. So the real question is "how do I get a pointer onto the
node *before* the nth-from-last, in a single pass, without knowing the length?"

The trick: give `fast` an n-node head start. Then advance both `slow` and `fast` together
until `fast` falls off the end. Because `fast` started exactly n nodes ahead, when `fast` is
null, `slow` is exactly n nodes behind -- which puts it on the predecessor of the
nth-from-last node. One skip and we are done.

Two details make this bullet-proof:

- **Off-by-one.** "nth from end" means there are n nodes after... actually, the nth-from-last
  node has n-1 nodes trailing it. To land `slow` on its *predecessor*, `fast` must start n+1
  positions ahead of `slow`. We get the "+1" for free by starting both at a **dummy** node
  in front of the head.
- **Removing the head.** If the nth-from-last is the head itself (e.g. remove the last node
  of a length-n list), there is no predecessor among the real nodes. The dummy node *becomes*
  that predecessor, so deleting the head is no longer a special case.

## Pseudocode

    function removeNthFromEnd(head, n):
        dummy <- new Node(0)
        dummy.next <- head
        fast <- dummy
        slow <- dummy

        # advance fast n+1 steps ahead (n real nodes + the dummy itself is the +1)
        repeat n+1 times:
            fast <- fast.next

        # now slide both until fast runs off the end
        while fast is not null:
            fast <- fast.next
            slow <- slow.next

        # slow is on the predecessor of the target; skip the target
        slow.next <- slow.next.next
        return dummy.next

## Java Solution

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode fast = dummy;
        ListNode slow = dummy;

        for (int i = 0; i <= n; i++) {     // advance fast n+1 steps: n real nodes + 1 for the predecessor gap
            fast = fast.next;
        }
        while (fast != null) {
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;
        return dummy.next;
    }
}
```

`dummy` is the throwaway front node that absorbs the "removing the head" special case and
supplies the `+1` in the gap. `i <= n` runs the loop `n + 1` times (0 through n inclusive),
sending `fast` exactly n nodes ahead of the *real* head plus one more for the predecessor
offset. When `fast` is null, `slow` is on the predecessor of the nth-from-last real node, so
`slow.next.next` safely skips the target -- `slow.next` is guaranteed non-null because the
problem promises at least n nodes. Returning `dummy.next` instead of `head` is essential: if
we removed the original head, `head` now dangles and `dummy.next` points to the new one.

## Complexity

    Time:  O(L)  -- one pass of L+1 nodes; each node is visited once
    Space: O(1)  -- dummy plus two pointers

## Dry-Run

Input `head = [1, 2, 3, 4, 5]`, `n = 2`. Nodes: A=1, B=2, C=3, D=4, E=5; target = D (2nd from
end). `dummy` -> A.

**Phase 1 -- advance fast n+1 = 3 steps:**

| i | fast before | fast after |
|--:|-------------|------------|
| 0 | dummy       | A          |
| 1 | A           | B          |
| 2 | B           | C          |

`fast` is now on C.

**Phase 2 -- slide together until fast runs off:**

| iteration | fast after | slow after |
|----------:|------------|------------|
| start     | C          | dummy      |
| 1         | D          | A          |
| 2         | E          | B          |
| 3         | null       | C          |

Loop exits. `slow` is on C -- the predecessor of D. Execute `slow.next = slow.next.next`:
C.next jumps from D to E. Chain under dummy: `0 -> 1 -> 2 -> 3 -> 5`. Return `dummy.next`.

Output: `[1, 2, 3, 5]`.

Removing-the-head case `[1, 2, 3, 4, 5]`, `n = 5`: phase 1 sends fast `null` after 5 steps
(A,B,C,D,E then null at i... let's count: i=0->A, i=1->B, i=2->C, i=3->D, i=4->E, i=5->null).
Phase 2 never runs. `slow` is still on dummy. `slow.next = slow.next.next` makes
dummy.next = B. Return B -> `[2, 3, 4, 5]`. No special case needed.

## Common mistakes

- Advancing `fast` only n steps instead of n+1. Then `slow` lands *on* the target, not its
  predecessor, and `slow.next.next` skips the wrong node.
- Not using a dummy. When the target is the head you have no predecessor and need a special
  branch (`if (target is head) return head.next`). The dummy unifies both cases.
- Returning `head` instead of `dummy.next`. If the head was removed, the caller gets a node
  that is no longer in the list.
- Computing the length in a first pass, then a second pass to delete. Correct but two passes;
  the two-pointer version is one pass and is what interviewers want.
- Forgetting the `n+1` comes from the dummy offset. Beginners often write `for (int i = 0; i <
  n; i++)` and then patch with a separate `fast = fast.next` -- work the +1 into the loop.

## Related problems

- [0143 - Reorder List](../0143-reorder-list/) - another "two pointers + careful link
  surgery" problem on the same structure.
- [0021 - Merge Two Sorted Lists](../0021-merge-two-sorted-lists/) - the other canonical use
  of the dummy head idiom.
- [0206 - Reverse Linked List](../0206-reverse-linked-list/) - the basic pointer-rewriting
  drill this problem assumes you can already do.
