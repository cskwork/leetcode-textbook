# 0141 - Linked List Cycle

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/linked-list-cycle/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores a value and an arrow (pointer) to the next node. [glossary](../../../docs/10-glossary.md#linked-list)
- **Two pointers** -- a slow pointer (one step) and a fast pointer (two steps) that collide if a loop exists. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Cycle** -- a path that returns to a node already visited; here a node's arrow points back to an earlier node instead of to `null`. [glossary](../../../docs/10-glossary.md#cycle)

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

The simplest detection idea is "remember every node I visit in a set; if I ever
see one twice, there is a loop." That works but costs O(n) memory. We can detect
a loop with **zero** extra memory using a footrace.

Analogy: two runners share a track that may or may not loop back on itself. They
start together; the slow runner takes one step per tick, the fast runner takes
two. On a straight track the fast runner simply reaches the end first, and we
report "no loop". But if the track bends into a
[cycle](../../../docs/10-glossary.md#cycle), the fast runner enters the loop and
circles forever -- and because he gains exactly one step on the slow runner every
tick, he *must* eventually lap her. The moment they share the same spot, a loop
is proven. (This footrace is widely called Floyd's algorithm, but the
"fast runner laps the slow runner" picture is all you need; we never invoke the
name as if it were magic.)

A [linked list](../../../docs/10-glossary.md#linked-list) is exactly such a
track: each node holds a value and a *pointer* -- an arrow to the next node. A
cycle means some node's arrow points backward to an earlier node instead of
forward to `null`.

Smallest meaningful case, `1 -> 2 -> 3 -> 2` (node `3` points back to node `2`,
so the loop is `2 -> 3 -> 2`). Track the two runners (each tick: slow moves one
arrow, fast moves two):

| tick | slow | fast                                   |
|------|------|----------------------------------------|
| 0 (start) | 1 | 1                                  |
| 1    | 2    | 3 (fast: 1->2->3)                      |
| 2    | 3    | 3 (fast: 3->2->3) -- collide on node 3|

At tick 2 both runners stand on node `3` -- collision, return `true`.

Why must the fast runner always catch up, never skip past forever? Once both are
inside the loop, the gap between them (measured around the loop) shrinks by
exactly 1 each tick: the fast runner gains one node, so a gap of `g` reaches 0 in
at most `g` ticks. Because the gap changes by exactly 1, it can never *jump over*
the slow runner -- it lands on her precisely. This is
[two pointers](../../../docs/10-glossary.md#two-pointers) with different step
sizes; the loop condition `fast != null && fast.next != null` guarantees that a
flat tail (no cycle) is detected without a null-pointer crash.

### Checkpoint A -- The runner picture

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** On each tick, how far do the two pointers move?
- a) slow: 1 node, fast: 2 nodes
- b) slow: 2 nodes, fast: 1 node
- c) both: 1 node

<details><summary>Show answer</summary>

**(a)** -- slow advances one `.next`, fast advances two (`.next.next`); that one-step-per-tick gain is what lets fast lap slow inside a loop.

</details>

**Q2 (comprehend).** Why is the loop condition `fast != null && fast.next != null` rather than just `while fast != null`?
- a) Because the next line dereferences `fast.next.next`, so both `fast` and `fast.next` must exist or it crashes
- b) To make the loop finish sooner
- c) Because `slow` might be `null`

<details><summary>Show answer</summary>

**(a)** -- reading `fast.next.next` needs `fast` and `fast.next` to be non-null; Java's `&&` short-circuits so checking them first prevents an NPE on a flat tail.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `hasCycle` on `head = [1, 2, 3]` with no cycle (`pos = -1`). What is returned, and why does the loop stop?
- a) `true`, at step 1
- b) `false`, because `fast.next` is `null` on the last node so the condition fails
- c) `false`, because `slow` reaches the end first

<details><summary>Show answer</summary>

**(b)** -- after one tick slow is on 2 and fast is on 3; the next check finds `fast.next` is `null`, the loop exits, and control reaches the final `return false`.

</details>

**Q2 (analyze).** What goes wrong if you compare `slow == fast` *before* either pointer has moved?
- a) Nothing -- it is a harmless extra check
- b) Both start at the head, so even a flat list falsely reports a cycle
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- at the starting line both pointers are `head`, so the comparison is instantly true; the check must come *after* the moves.

</details>

**Q3 (transfer).** If you were allowed O(n) memory, how would you detect a cycle in a simpler way?

<details><summary>Show answer</summary>

Walk the list and drop each visited node into a `HashSet`; the first node already in the set is where the cycle begins. It uses more memory than the runner trick, but it is simpler to reason about and also finds the cycle's entry point directly.

</details>

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
