# 0021 - Merge Two Sorted Lists

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/merge-two-sorted-lists/

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

Both inputs are already sorted, so the smallest remaining node overall is always whichever
head is smaller. Walk both lists with one pointer each, compare the two heads, splice the
smaller one onto a growing result, and advance only that pointer. Repeat until one list is
exhausted, then attach whatever is left of the other (it is already sorted and all its values
are at least as large as the last node we appended).

The tricky part is the *very first* append: before any node is attached there is no result
list, so you need a special case for "first node". The clean fix is the **dummy head idiom**
from the pattern intro -- a throwaway node in front of the real head. Append everything to a
`tail` pointer that starts at the dummy, and at the end return `dummy.next`. No first-node
branch, no "did the head change?" bookkeeping.

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
