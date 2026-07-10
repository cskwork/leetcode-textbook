# 0019 - Remove Nth Node From End of List

**Difficulty:** Medium
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/remove-nth-node-from-end-of-list/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores a value and an arrow (pointer) to the next node. [glossary](../../../docs/10-glossary.md#linked-list)
- **Two pointers** -- a fast pointer and a slow pointer kept a fixed distance apart, so the slow one lands on the deletion target's predecessor. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Sentinel (dummy head)** -- a throwaway node in front of the real head that supplies the predecessor gap and absorbs the "deleting the head" special case. [glossary](../../../docs/10-glossary.md#sentinel)

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

To delete a node from a singly [linked list](../../../docs/10-glossary.md#linked-list)
you cannot just "remove" it -- you must stand on the node *before* it and make
that node's arrow (its *pointer*, the reference to the next node) skip over the
target: `prev.next = prev.next.next`. So the real task is: "land a finger on the
node just before the nth-from-last, in a single pass, without knowing the list's
length."

Analogy: two friends walk the same path, but one starts exactly `n` paces ahead
of the other. They always walk at the same speed, so the gap between them stays
exactly `n`. When the friend in front steps off the end of the path, the friend
behind is exactly `n` paces from the end -- standing right where we want.

Smallest meaningful case, `1 -> 2 -> 3 -> 4 -> 5`, remove `n = 2` from the end
(the target node is `4`):

- Give the front friend a head start, then march both together until the front
  friend falls off the end.
- When the front friend is `null`, the behind friend lands on node `3` -- the
  *predecessor* of `4`. Make `3` skip `4` (`3.next = 5`), and the list becomes
  `1 -> 2 -> 3 -> 5`.

Two off-by-one traps hide in "n paces ahead", and a single trick fixes both:
start both fingers on a **dummy head** -- a throwaway node placed in front of the
real head (a [sentinel](../../../docs/10-glossary.md#sentinel)).

- *Land on the predecessor, not the target.* The nth-from-last node has `n - 1`
  nodes trailing it, so to stop on its *predecessor* the front finger must start
  `n + 1` ahead. Starting both fingers on the dummy contributes that extra `+1`
  for free.
- *Deleting the head.* If the target *is* the head (e.g. remove the 5th-from-last
  in a 5-node list), there is no real predecessor -- but the dummy *becomes* that
  predecessor, so deleting the head needs no special branch.

This is [two pointers](../../../docs/10-glossary.md#two-pointers) held a fixed
distance apart, and we return `dummy.next` (not `head`): if the original head was
deleted, `head` now dangles, while `dummy.next` correctly points to the new one.

### Checkpoint A -- Land on the predecessor

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** To remove the nth-from-last node, the slow pointer must land on which node?
- a) The target node itself
- b) The node just before the target (its predecessor)
- c) The head of the list

<details><summary>Show answer</summary>

**(b)** -- deletion works by making the predecessor's arrow skip the target (`prev.next = prev.next.next`), so you must be standing one node *before* it.

</details>

**Q2 (comprehend).** Why does `fast` get an n+1 step head start instead of just n?
- a) To move faster through the list
- b) So that when `fast` falls off the end, `slow` is one node short of the target -- on its predecessor
- c) To count the length of the list

<details><summary>Show answer</summary>

**(b)** -- the extra step accounts for the "one before" offset; with only n steps `slow` would land *on* the target, too late to unlink it.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `removeNthFromEnd(head = [1, 2, 3], n = 1)`. What is returned?
- a) `[1, 2]` -- the last node is removed
- b) `[2, 3]` -- the first node is removed
- c) `[1, 3]` -- the middle node is removed

<details><summary>Show answer</summary>

**(a)** -- fast gets a 2-step head start (lands on 2), then both slide until fast is `null`, leaving slow on node 2 (the predecessor of 3); unlinking 3 yields `1 -> 2`.

</details>

**Q2 (analyze).** If you advanced `fast` only n steps (a loop `for i < n`), where would slow end up?
- a) On the target node, so unlinking skips the wrong node
- b) On the head, which is fine
- c) On `null`, causing a crash

<details><summary>Show answer</summary>

**(a)** -- short by one, `slow` lands *on* the nth-from-last node; `slow.next.next` then skips the node *after* the target, corrupting the list.

</details>

**Q3 (transfer).** Suppose you were also told the list's length up front. In words, how else could you solve this?

<details><summary>Show answer</summary>

The target sits at index `length - n` from the front, so walk to its predecessor at index `length - n - 1` (or to a dummy if that is -1, i.e. the head case) and skip the target. Same unlink, but the two-pointer version avoids a separate length-counting pass.

</details>

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
