# 0021 - Merge Two Sorted Lists

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/merge-two-sorted-lists/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores a value and an arrow (pointer) to the next node. [glossary](../../../docs/10-glossary.md#linked-list)
- **Two pointers** -- one finger on each input list; advance only the list whose head was just taken. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Sentinel (dummy head)** -- a throwaway node placed in front of the real head, so the first append needs no special case. [glossary](../../../docs/10-glossary.md#sentinel)

## Problem

You are given the heads of two sorted linked lists `list1` and `list2`. Merge them into one
sorted list and return its head. The merged list is made by splicing together the nodes of
the two inputs (no new nodes).

Signature:

    ListNode mergeTwoLists(ListNode list1, ListNode list2)

Examples:

    Input:  list1 = [1,2,4], list2 = [1,3,4]
    Output: [1,1,2,3,4,4]

    Input:  list1 = [], list2 = []
    Output: []

    Input:  list1 = [], list2 = [0]
    Output: [0]

## Intuition

Picture two face-up piles of number cards, each pile already sorted with the
smallest card on top. To merge them into one sorted pile, you only ever look at
the two top cards, take the smaller one, lay it down, and repeat -- you never
need to look deeper, because both piles are already sorted. A
[linked list](../../../docs/10-glossary.md#linked-list) works exactly like those
piles: each node holds a value and a *pointer* (an arrow, drawn on paper, to the
next node), and the *head* is the "top card".

Smallest meaningful case: `list1 = 1 -> 2 -> 4` and `list2 = 1 -> 3 -> 4`.

- Compare the two heads: `1` and `1` (a tie -- take list1's). Result so far: `1`.
- Now compare `2` and `1`: take list2's `1`. Result: `1 -> 1`.
- Compare `2` and `3`: take `2`. Result: `1 -> 1 -> 2`.
- Keep going until one pile is empty, then drop the *entire rest* of the other
  pile onto the end (it is already sorted, and every value left is at least as
  big as the last one we placed).

The general rule: keep one finger on each head; compare; splice the smaller head
onto a growing answer; advance *only* that finger. This is
[two pointers](../../../docs/10-glossary.md#two-pointers), one per input list.

One detail trips up beginners: the *very first* append. Before any node is
attached there is no answer list yet, so a naive version needs a special "is this
the first node?" branch. The clean fix is a **dummy head** -- a throwaway node
placed in front of the real head (a [sentinel](../../../docs/10-glossary.md#sentinel)).
We append every node to a `tail` finger that starts at the dummy, and at the end
we return `dummy.next`. Now the first real node is attached exactly the same way
as every other node -- no special case, no "did the head change?" bookkeeping. We
reuse the input nodes themselves rather than copying their values, so beyond the
single dummy node no extra memory is spent.

### Checkpoint A -- The dummy head trick

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why does this solution create a `dummy` node before doing anything else?
- a) To store the final sorted value
- b) So the first real node is attached the same way as every other, with no special case
- c) To sort the two input lists

<details><summary>Show answer</summary>

**(b)** -- the dummy sits in front of the real head so every append (including the very first) is just `tail.next = ...`; we then return `dummy.next`.

</details>

**Q2 (comprehend).** After splicing the smaller head onto `tail`, which list pointer(s) should advance?
- a) Both `list1` and `list2`
- b) Only the list you took the node from (and `tail`)
- c) Neither

<details><summary>Show answer</summary>

**(b)** -- the other list's head is still the next candidate to compare, so only the finger you drew from moves forward.

</details>

## Pseudocode

    function mergeTwoLists(list1, list2):
        dummy <- new Node(0)        # throwaway node before the real head
        tail  <- dummy

        while list1 is not null and list2 is not null:
            if list1.value <= list2.value:
                tail.next <- list1
                list1     <- list1.next
            else:
                tail.next <- list2
                list2     <- list2.next
            tail <- tail.next

        # one list is exhausted; attach the rest of the other (already sorted)
        tail.next <- whichever of list1, list2 is not null
        return dummy.next

## Java Solution

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }
        tail.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }
}
```

`dummy` is the throwaway front node; we never read its value, it just exists so that the
first real append is no different from any other. After the comparison, `tail.next = ...`
splices the chosen node in place (we reuse the input nodes, we do not copy them), and we
advance *only* the list we took from -- the other list's head must stay put for the next
comparison. The single line after the loop attaches the entire remainder of the surviving
list in one pointer assignment: because that remainder is sorted and every value in it is at
least as large as the last node appended, no further merging is needed. `<=` (not `<`) keeps
the merge stable -- on ties list1's node goes first, which is harmless and deterministic.

## Complexity

    Time:  O(n + m)  -- each node is visited and spliced exactly once
    Space: O(1)      -- only the dummy node is allocated; all other nodes are reused

## Dry-Run

Input `list1 = [1, 2, 4]`, `list2 = [1, 3, 4]`:

| Step | list1 | list2 | compare      | append | tail walks to | remaining list1 | remaining list2 |
|-----:|-------|-------|--------------|--------|---------------|-----------------|------------------|
| init | 1     | 1     | -            | -      | dummy         | 1->2->4         | 1->3->4          |
| 1    | 1     | 1     | 1 <= 1 (tie) | 1 (l1) | node 1        | 2->4            | 1->3->4          |
| 2    | 2     | 1     | 2 > 1        | 1 (l2) | node 1        | 2->4            | 3->4             |
| 3    | 2     | 3     | 2 <= 3       | 2 (l1) | node 2        | 4               | 3->4             |
| 4    | 4     | 3     | 4 > 3        | 3 (l2) | node 3        | 4               | 4                |
| 5    | 4     | 4     | 4 <= 4 (tie) | 4 (l1) | node 4        | null            | 4                |

Loop exits (list1 is null). Attach remainder of list2: `tail.next = 4`. Result chain under
dummy: `0 -> 1 -> 1 -> 2 -> 3 -> 4 -> 4`. Return `dummy.next` = first `1`.

Output: `[1, 1, 2, 3, 4, 4]`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `mergeTwoLists(list1 = [2], list2 = [1, 3])`. What is returned?
- a) `[1, 2, 3]`
- b) `[2, 1, 3]`
- c) `[1, 3, 2]`

<details><summary>Show answer</summary>

**(a)** -- take 1 (list2), then 2 (list1), the loop ends when list1 is null, and the remaining `3` is attached in one step; reading gives `1 -> 2 -> 3`.

</details>

**Q2 (analyze).** What goes wrong if you delete the final "attach the remainder" line (`tail.next = ...`)?
- a) The merged list drops the tail of whichever list was longer
- b) It throws a `NullPointerException`
- c) Nothing -- the loop already handled it

<details><summary>Show answer</summary>

**(a)** -- the loop runs only while *both* lists have nodes, so any nodes left in the longer list are never attached and are silently lost.

</details>

**Q3 (transfer).** How would you change the code to merge the two lists in *descending* order instead?

<details><summary>Show answer</summary>

Swap the comparison so the *larger* head is taken each step (e.g. take `list1` when `list1.val >= list2.val`). The dummy, the tail, and the final remainder-attach all stay identical -- only the winner of each comparison flips.

</details>

## Common mistakes

- No dummy node: then the first append needs a separate `if (head == null)` branch and you
  must remember whether `head` has been set yet. The dummy removes that whole class of bug.
- Advancing *both* `list1` and `list2` after each append. Only the list you took a node from
  should move; the other head is still the next candidate.
- Copying node values into a new list instead of splicing. Slower and misses the point.
- Forgetting the final "attach the remainder" step, which drops the tail of the longer list.
- Using `<` instead of `<=` and getting an unstable but still-sorted merge -- usually fine,
  but `<=` is the idiomatic, predictable choice.

## Related problems

- [0143 - Reorder List](../0143-reorder-list/) - another "build a result by splicing" problem
  that benefits from thinking in pointers rather than copies.
- [0021 - Merge Two Sorted Lists (you are here)](./) is the warm-up; Pattern 9's
  Merge K Sorted Lists generalizes it with a heap.
- [0206 - Reverse Linked List](../0206-reverse-linked-list/) - the other foundational
  pointer-rewriting drill to do alongside this one.
