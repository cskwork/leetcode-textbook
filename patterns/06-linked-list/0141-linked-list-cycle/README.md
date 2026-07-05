# 0141 - Linked List Cycle

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/linked-list-cycle/

## Problem

Given `head`, the head of a linked list, determine whether the list has a cycle in it. A
cycle exists if some node can be reached again by following `next` continuously. Internally
the input is described by a `pos` field: the index (0-based) of the node that the tail's
`next` points back to, or `-1` if there is no cycle. You do not receive `pos`; you must
detect the cycle from the nodes alone.

Signature:

    boolean hasCycle(ListNode head)

Examples:

    Input:  head = [3,2,0,-4], pos = 1   # tail (-4) points back to node index 1 (value 2)
    Output: true

    Input:  head = [1,2], pos = 0
    Output: true

    Input:  head = [3,2,0,-4], pos = -1
    Output: false

## Intuition

The brute-force idea is "remember every node I have visited in a Set; if I ever see a node
twice, there is a cycle". That is O(n) time but O(n) space. We can do it in O(1) space with
**Floyd's slow / fast technique**. Send two pointers out from the head: `slow` takes one
step per turn, `fast` takes two. If there is no cycle, `fast` simply reaches the end first
and we report false. If there *is* a cycle, `fast` enters the loop and runs forever -- but
because it moves one extra node per turn relative to `slow`, it gains on `slow` by one node
each step, and on a closed loop it is mathematically guaranteed to land on top of `slow`.
The moment they point to the *same node*, a cycle is proven.

Why the gain must close the gap: once both are inside the cycle, the distance (gap) between
them shrinks by 1 each step (fast gains 1, modulo the cycle length). A gap of `g` reaches 0
in at most `g` steps -- it never skips over `slow` because the gap changes by exactly 1.

## Pseudocode

    function hasCycle(head):
        slow <- head
        fast <- head
        while fast is not null and fast.next is not null:
            slow <- slow.next              # one step
            fast <- fast.next.next         # two steps
            if slow is fast:               # they collided -> a loop pulled fast back onto slow
                return true
        return false                       # fast ran off the end -> flat list, no cycle

## Java Solution

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }
}
```

Both pointers start at the head; the only difference is step size. The loop condition guards
the *dereference* `fast.next.next`: we need `fast` itself to be non-null, and `fast.next` to
be non-null, before we may read `.next` off it. Java's `&&` short-circuits, so the order --
`fast != null` first, then `fast.next != null` -- is what stops a one-node list or a flat
tail from throwing `NullPointerException`. The comparison `slow == fast` is *reference*
equality (the same node object, not just equal values), which is exactly right: a cycle means
the same node is visited twice. The check sits after both pointers have moved so they do not
trivially match at the starting line.

## Complexity

    Time:  O(n)  -- in the no-cycle case fast visits each node once; in the cycle case slow
                    traverses at most n nodes before fast closes the gap (gap <= cycle length)
    Space: O(1)  -- two pointers, nothing else

## Dry-Run

Input `head = [3, 2, 0, -4]` with `pos = 1` (so `-4` points back to `2`). Call the nodes
A(3), B(2), C(0), D(-4), and D.next = B, B.next = C, C.next = D.

| Step | slow before | fast before | slow after | fast after | same? |
|-----:|-------------|-------------|------------|------------|-------|
| 1    | A           | A           | B          | C          | no    |
| 2    | B           | C           | C          | B (C->D->B)| no    |
| 3    | C           | B           | D          | D (B->C->D)| yes   |

At step 3 `slow` and `fast` both land on node D. Return `true`.

Flat-list case `[3, 2, 0, -4]` with `pos = -1`: step 1 slow=B, fast=C; step 2 slow=C,
fast=null (fast tried `C.next.next` = `-4.next` = null). Loop condition `fast != null` is
false, exit, return `false`.

Edge cases: empty list -- `fast` starts null, loop skipped, return false. Single node with a
self-cycle (`pos = 0`) -- step 1 slow=head, fast=head.next.next=head, both on head, true.

## Common mistakes

- Checking `slow == fast` at the *start* (before either has moved). They are both `head`, so
  you falsely report a cycle on a flat list. Always move first, then compare.
- A loop condition of `while fast != null` only, then dereferencing `fast.next.next`. On a
  list whose length is odd, `fast` lands on the last node and the dereference throws NPE.
- Comparing `slow.val == fast.val` instead of the node references. Two unrelated nodes can
  have equal values; you would mis-detect cycles.
- Initializing only one pointer at the head. Both must start at the head; the relative speed
  is what closes the gap.
- Using a `HashSet` of visited nodes. Correct, but O(n) space -- the whole elegance of
  Floyd's method is the O(1) space it achieves.

## Related problems

- [0234 - Palindrome Linked List](../0234-palindrome-linked-list/) - same slow/fast setup,
  but you stop `slow` at the middle instead of hunting for a collision.
- [0143 - Reorder List](../0143-reorder-list/) - uses slow/fast to split the list in half.
- [0206 - Reverse Linked List](../0206-reverse-linked-list/) - the other foundational
  pointer drill; combine it with slow/fast and you have Reorder and Palindrome.
