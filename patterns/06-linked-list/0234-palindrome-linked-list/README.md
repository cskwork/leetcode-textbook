# 0234 - Palindrome Linked List

**Difficulty:** Easy
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/palindrome-linked-list/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores a value and an arrow (pointer) to the next node; only forward traversal is possible. [glossary](../../../docs/10-glossary.md#linked-list)
- **Two pointers** -- a slow pointer (one step) and a fast pointer (two steps) that find the middle in one pass. [glossary](../../../docs/10-glossary.md#two-pointers)
- **In-place** -- reversing a chunk of the existing list and comparing with pointers, using O(1) extra memory instead of copying values into an array. [glossary](../../../docs/10-glossary.md#in-place)

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

A palindrome reads the same forwards and backwards, like "racecar" or
`1 2 2 1`. It mirrors around its center: the first half equals the *reverse* of
the second half. For an array we would simply compare slot 0 with the last slot,
slot 1 with the second-last, and so on. But a singly
[linked list](../../../docs/10-glossary.md#linked-list) only points forward (each
node's *pointer* is an arrow to the next node), so we cannot walk it backwards to
do that mirror comparison. The trick: we can reverse a *chunk* of it.

Smallest meaningful case, `1 -> 2 -> 2 -> 1` (a palindrome). The plan, in
pictures:

- Front half: `1 -> 2`. Back half: `2 -> 1`.
- Reverse the back half: `1 -> 2`.
- Compare the two halves node by node: `1 == 1`, then `2 == 2`. All match, so it
  is a palindrome.

So the solution is three steps, each a technique we have already practiced:

1. **Find the middle**, using the slow/fast trick from
   [Linked List Cycle (LC 141)](../0141-linked-list-cycle/) -- but here we stop
   `slow` at the middle instead of hunting for a collision.
2. **Reverse the second half**, using the arrow-flipping dance from
   [Reverse Linked List (LC 206)](../0206-reverse-linked-list/): for each node,
   save its next arrow, point the node backward at the previous one, advance.
3. **Compare**: walk one finger down the front half and one down the reversed
   back half; if every pair of values matches, it is a palindrome.

Why reverse only the back half? After reversing it, a finger starting at its new
head moves through the original tail values in reverse order -- which is exactly
the "walk it backwards" we needed. The front half is untouched and still
readable from `head`. We never copy values into an array (that would be O(n)
extra memory, which the problem forbids), so the whole thing is
[in-place](../../../docs/10-glossary.md#in-place) apart from a handful of
pointers. For an odd-length list the lone middle node sits on the front side and
is simply never compared -- correct, because a palindrome's center can be
anything.

### Checkpoint A -- Three steps, one palindrome

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** In this solution, which part of the list gets reversed?
- a) The whole list
- b) Only the second (back) half
- c) The first (front) half

<details><summary>Show answer</summary>

**(b)** -- the front half is left untouched (still readable from `head`); only the back half is flipped so a finger can walk it backwards.

</details>

**Q2 (comprehend).** On an odd-length list like `[1, 2, 3, 2, 1]`, what happens to the center node (value 3) during the compare?
- a) It is compared and must match something
- b) It sits on the front-half side and is simply never compared
- c) It is deleted

<details><summary>Show answer</summary>

**(b)** -- the reversed back half is shorter, so the compare loop ends before reaching the center; a palindrome's middle can be anything, so ignoring it is correct.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `isPalindrome` on `head = [1, 2, 3, 1]`. What is returned?
- a) `true`
- b) `false`, because the values 2 and 3 mismatch
- c) `false`, because an even-length list can never be a palindrome

<details><summary>Show answer</summary>

**(b)** -- `slow` stops on node 2, the back half `[3, 1]` reverses to `[1, 3]`, and comparing the fronts gives `1==1` then `2 != 3`, a mismatch.

</details>

**Q2 (analyze).** What goes wrong if you find the middle with the condition `fast != null && fast.next != null` instead?
- a) On even-length lists `slow` stops one node too late, so the two halves compare off by one
- b) It throws a `NullPointerException`
- c) Nothing changes

<details><summary>Show answer</summary>

**(a)** -- that condition lets `slow` overshoot to the second-middle on even lists, unbalancing the halves and misaligning the compare.

</details>

**Q3 (transfer).** LeetCode does not require restoring the list. In words, how would you restore the original list after deciding the answer?

<details><summary>Show answer</summary>

Reverse the `second` half a *second* time -- it flips back to its original order -- then reattach it to the tail of the front half. The answer is already safe in a boolean, so this only tidies the structure for the caller.

</details>

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
