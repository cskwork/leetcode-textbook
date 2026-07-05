# 0234 - Palindrome Linked List

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/palindrome-linked-list/

## Problem

Given the `head` of a singly linked list, return `true` if it is a palindrome (reads the same
forwards and backwards). Do it in O(n) time and O(1) extra space.

Signature:

    boolean isPalindrome(ListNode head)

Examples:

    Input:  head = [1,2,2,1]
    Output: true

    Input:  head = [1,2,3,2,1]
    Output: true

    Input:  head = [1,2]
    Output: false

## Intuition

A palindrome mirrors around its center. So the first half of the list must equal the *reverse*
of the second half. We cannot walk a singly linked list backwards -- but we can reverse a
chunk of it. The plan is three steps, each a technique we have already practiced:

1. **Find the middle** with slow / fast pointers (same as Floyd, but we stop slow at the
   first middle node of an even-length list).
2. **Reverse the second half** in place with the pointer-flip from Reverse Linked List.
3. **Compare** the first half and the reversed second half node by node.

We never copy values into an array (that would be O(n) space, which the problem forbids) and
we never build a second list. The trick is that reversing the back half *is* the
"walk it backwards" we needed -- after reversing, a pointer starting at the new head of that
half moves through the original tail values in reverse order, which is exactly what we compare
against the front.

## Pseudocode

    function isPalindrome(head):
        if head is null or head.next is null:
            return true                 # empty or single node is trivially a palindrome

        # 1. find the middle (slow stops on the first-middle for even length)
        slow <- head
        fast <- head
        while fast.next is not null and fast.next.next is not null:
            slow <- slow.next
            fast <- fast.next.next

        # 2. reverse the second half in place
        second <- reverse(slow.next)

        # 3. compare first half vs reversed second half
        p <- head
        q <- second
        result <- true
        while q is not null:            # q is at most as long as p
            if p.value is not q.value:
                result <- false
                break
            p <- p.next
            q <- q.next

        # (optional: reverse the second half back to restore the original list)
        return result

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
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }

        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode second = reverse(slow.next);
        ListNode p = head;
        ListNode q = second;
        boolean result = true;
        while (q != null) {
            if (p.val != q.val) {
                result = false;
                break;
            }
            p = p.next;
            q = q.next;
        }
        return result;
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

The slow/fast loop condition is `fast.next != null && fast.next.next != null` (note: both
`.next`, *not* `fast` itself) so that on an even-length list `slow` stops on the *first*
middle node, leaving the back half exactly half the list. We reverse `slow.next` -- the back
half -- which detaches it from the front only in the sense that its arrows now run the other
way; the front half is untouched and still readable from `head`. The compare loop iterates
`q` (the reversed half) to its end, which is at most as long as `p`'s half, so `p` never runs
off the front. For an odd-length list the lone middle node sits on the `p` side and is simply
never compared -- correct, because a palindrome's center can be anything. The restore step is
commented out for simplicity; LeetCode does not require it, but in an interview you would
reverse `second` again to leave the input list unmutated.

## Complexity

    Time:  O(n)  -- find-middle walks half, reverse walks half, compare walks half
    Space: O(1)  -- a handful of pointers; no array copy, no recursion stack

## Dry-Run

Input `head = [1, 2, 2, 1]` (even length, 4 nodes: A=1, B=2, C=2, D=1).

**Step 1 -- find middle:**

| iteration | slow after | fast after | note |
|----------:|------------|------------|------|
| start     | A          | A          | -    |
| 1         | B          | C          | fast.next=D, fast.next.next=null -> stop |

`slow` stops on B (first middle). Back half to reverse = `slow.next` = C->D.

**Step 2 -- reverse C->D:** result `second` = D->C.

**Step 3 -- compare:**

| p (front) | q (reversed back) | p.val | q.val | match? |
|-----------|-------------------|-------|-------|--------|
| A         | D                 | 1     | 1     | yes    |
| B         | C                 | 2     | 2     | yes    |
| C         | null              | -     | -     | stop   |

`q` ran out, all matched. Return `true`.

Odd-length check `[1, 2, 3, 2, 1]`: slow stops on the center (value 3), back half = `2->1`
reverses to `1->2`, compares `1==1, 2==2` against the front `1, 2` (the center 3 sits on the
p side and is ignored). Return `true`. Non-palindrome `[1, 2]`: slow stops on A, back half =
B reverses to B, compare `1 vs 2` -> mismatch -> false.

## Common mistakes

- Copying values into an `ArrayList` or `int[]` and two-pointering it. Correct and easy, but
  costs O(n) space, which the problem explicitly forbids.
- Reversing the *whole* list. Then you have no front half to compare against -- you destroyed
  it. Only the back half is reversed.
- Wrong slow/fast condition. Using `fast != null && fast.next != null` lets `slow` overshoot
  to the second-middle on even lists, and the halves compare off by one.
- Forgetting the single-node / empty base case, then `slow.next` on a one-node list is null
  and `reverse(null)` is fine but the rest must still return true cleanly.
- Comparing values with `==` on boxed `Integer`. Here values are primitive `int` so `==` is
  correct -- but never generalize this to boxed types.

## Related problems

- [0143 - Reorder List](../0143-reorder-list/) - identical three-step skeleton (middle,
  reverse, splice) used for a different goal.
- [0141 - Linked List Cycle](../0141-linked-list-cycle/) - the slow/fast setup you reuse
  here for finding the middle.
- [0206 - Reverse Linked List](../0206-reverse-linked-list/) - the reverse helper here is a
  verbatim copy of that solution.
