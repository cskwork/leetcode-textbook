# 0002 - Add Two Numbers

**Difficulty:** Medium
**Pattern:** Linked List
**LeetCode:** https://leetcode.com/problems/add-two-numbers/

## Concepts used

- **Linked list** -- a chain of nodes where each node stores one digit and an arrow (pointer) to the next node; digits are stored least-significant first. [glossary](../../../docs/10-glossary.md#linked-list)
- **Sentinel (dummy head)** -- a throwaway node in front of the real head, so the first result digit is attached the same way as every other. [glossary](../../../docs/10-glossary.md#sentinel)

## Problem

You are given two non-empty linked lists, `l1` and `l2`, representing two non-negative
integers. The digits are stored in **reverse order**, so the head of each list is the least
significant digit. Add the two numbers and return the sum as a reversed linked list of the
same shape. Neither list has leading zeros except for the number zero itself.

Signature:

    ListNode addTwoNumbers(ListNode l1, ListNode l2)

Examples:

    Input:  l1 = [2,4,3], l2 = [5,6,4]    # 342 + 465 = 807
    Output: [7,0,8]

    Input:  l1 = [0], l2 = [0]
    Output: [0]

    Input:  l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]   # 9999999 + 9999 = 10009998
    Output: [8,9,9,9,0,0,0,1]

## Intuition

This is grade-school column addition, the way you add two numbers on paper from
right to left. Each column produces one digit of the answer plus a *carry* --
the small "1" you write on top of the next column when a column sums to 10 or
more. The only twist: here the digits live in
[linked lists](../../../docs/10-glossary.md#linked-list) -- each node holds one
digit and a *pointer* (an arrow to the next node) -- and they are stored
*reversed*, least-significant digit first. That reversal is a gift: it means we
can start at the heads and add column by column exactly as on paper, with no
reversing needed up front.

Smallest meaningful case, `l1 = 2 -> 4 -> 3` (the number 342) and
`l2 = 5 -> 6 -> 4` (465). Expected sum 807, written reversed as `7 -> 0 -> 8`.

- Column 1 (the heads): 2 + 5 = 7. Write `7`, carry 0.
- Column 2: 4 + 6 = 10. Write `0`, carry 1.
- Column 3: 3 + 4 + 1 (carry) = 8. Write `8`, carry 0.
- Result so far: `7 -> 0 -> 8`, which read as a number is 807. Correct.

The general rule: walk both lists together, add the two visible digits plus any
carry from the previous column, write `sum % 10` as the new digit, and carry
`sum / 10` into the next column. Because two single digits plus a carry of 1
total at most 19, the carry is always 0 or 1.

Two details make it bullet-proof, and both are classic beginner traps. First, the
lists may be different lengths -- when one runs out, treat its missing digit as
`0` and keep going. Second, a carry can survive *past* the last digit of both
lists (5 + 5 = 10 needs an extra node for the leading 1), so the loop must also
run one more time while a carry remains. We build the answer node by node, so
this is a **dummy head** problem: a throwaway node in front of the real head (a
[sentinel](../../../docs/10-glossary.md#sentinel)) lets us attach the first
digit exactly like every other digit, and we return `dummy.next` at the end.

### Checkpoint A -- Column addition with a carry

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Each list stores its number with which digit at the head?
- a) The most significant digit
- b) The least significant digit (the ones place)
- c) A random digit

<details><summary>Show answer</summary>

**(b)** -- digits are reversed on purpose, least-significant first, so column addition can stream straight from the head with no reversing.

</details>

**Q2 (comprehend).** Why does the loop condition include `|| carry != 0`?
- a) To skip empty lists
- b) Because a carry can survive past the last digit of both lists (e.g. 5 + 5 = 10 needs an extra node)
- c) To reverse the output

<details><summary>Show answer</summary>

**(b)** -- once both lists end, a leftover carry (0 or 1) may still need its own node; that third clause runs one extra iteration to emit it.

</details>

## Pseudocode

    function addTwoNumbers(l1, l2):
        dummy <- new Node(0)
        tail  <- dummy
        carry <- 0

        while l1 is not null OR l2 is not null OR carry is not 0:
            sum <- carry
            if l1 is not null:
                sum <- sum + l1.value
                l1  <- l1.next
            if l2 is not null:
                sum <- sum + l2.value
                l2  <- l2.next

            tail.next <- new Node(sum mod 10)     # ones digit becomes this node
            tail      <- tail.next
            carry     <- sum div 10               # tens digit carries to the next column

        return dummy.next

## Java Solution

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            tail.next = new ListNode(sum % 10);
            tail = tail.next;
            carry = sum / 10;
        }
        return dummy.next;
    }
}
```

The loop condition is the heart of the solution: `l1 != null || l2 != null || carry != 0`.
The first two clauses keep going while either input still has digits; the third clause is the
one beginners miss -- it runs one extra iteration to emit a final carry node when the sum
grows a digit longer than both inputs (5 + 5 = 10, or 999 + 1 = 1000). `sum % 10` extracts the
ones digit for the current node, `sum / 10` is the carry into the next column; because two
single digits plus a carry of 1 sum to at most 19, the carry is always 0 or 1. Each node is
allocated fresh and attached via `tail.next`, so we never mutate the input lists, and the
dummy lets us treat the first digit identically to every other digit.

## Complexity

    Time:  O(max(m, n))  -- one iteration per digit of the longer input, plus at most one for carry
    Space: O(max(m, n))  -- the output list has at most max(m, n) + 1 nodes; this is the required
                            output, not auxiliary space (auxiliary space is O(1): three locals)

## Dry-Run

Input `l1 = [2, 4, 3]` (342), `l2 = [5, 6, 4]` (465). Expected sum 807 -> `[7, 0, 8]`.

| iter | l1 | l2 | carry in | sum (carry + l1 + l2) | digit (sum%10) | carry out (sum/10) | output so far |
|-----:|----|----|---------:|----------------------:|---------------:|-------------------:|---------------|
| 1    | 2  | 5  | 0        | 0 + 2 + 5 = 7         | 7              | 0                  | 7             |
| 2    | 4  | 6  | 0        | 0 + 4 + 6 = 10        | 0              | 1                  | 7 -> 0        |
| 3    | 3  | 4  | 1        | 1 + 3 + 4 = 8         | 8              | 0                  | 7 -> 0 -> 8   |
| -    | null | null | 0    | loop ends             | -              | -                  | -             |

Loop exits (l1 null, l2 null, carry 0). Return `dummy.next` = node 7.

Output: `[7, 0, 8]` = 807. Correct.

Final-carry case `l1 = [5]`, `l2 = [5]`: iter 1 sum = 0+5+5 = 10, digit 0, carry 1; iter 2:
both inputs null but carry = 1, so the condition `carry != 0` fires, sum = 1, digit 1, carry
0. Output `[0, 1]` = 10. Correct -- without the `carry != 0` clause we would have dropped the
leading 1.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `addTwoNumbers(l1 = [9, 9], l2 = [1])` (that is, 99 + 1). What is returned?
- a) `[0, 0, 1]`
- b) `[1, 0, 0]`
- c) `[0, 1]`

<details><summary>Show answer</summary>

**(a)** -- column 1: 9+1 = 10, write 0 carry 1; column 2: 9+0+1 = 10, write 0 carry 1; then `carry != 0` fires to write the final 1, giving `0 -> 0 -> 1` (100).

</details>

**Q2 (analyze).** With a buggy condition `while l1 != null && l2 != null`, what does `addTwoNumbers([5], [5])` return?
- a) `[0, 1]` (correct)
- b) `[0]` -- the carry node is dropped because the loop stops as soon as both inputs are exhausted
- c) `[10]`

<details><summary>Show answer</summary>

**(b)** -- the single iteration writes 0 and sets carry 1, but then both lists are null so the loop ends immediately, silently dropping the leading 1.

</details>

**Q3 (transfer).** How would the approach change if the digits were stored MOST-significant-first instead?

<details><summary>Show answer</summary>

Carries flow toward the more-significant end, so you can no longer stream from the head. The clean fix is to reverse both input lists first, run this same column-addition solution, then reverse the result -- reusing Reverse Linked List as a setup step.

</details>

## Common mistakes

- Loop condition `while l1 != null && l2 != null`. Stops as soon as the shorter list ends,
  dropping the rest of the longer list and any trailing carry.
- Loop condition without `|| carry != 0`. Drops the leading carry node (5 + 5 wrongly yields
  `[0]` instead of `[0, 1]`).
- Forgetting to advance `l1` / `l2` after reading their digits -> infinite loop reading the
  same digit.
- Computing the digit as `sum - 10` only when `sum >= 10`. Works but needs an `if`; `sum % 10`
  and `sum / 10` handle both cases branch-free and are idiomatic.
- Mutating the input lists by reusing their nodes for the output. The problem allows it in
  principle, but mixing reused and freshly-allocated nodes is error-prone; allocating fresh
  nodes is clearer and still optimal.
- Treating the lists as most-significant-first. They are reversed on purpose so the addition
  can stream from the head; reversing them first is wasted work.

## Related problems

- [0021 - Merge Two Sorted Lists](../0021-merge-two-sorted-lists/) - same dummy-head +
  two-pointer walk, used to combine rather than add.
- [0206 - Reverse Linked List](../0206-reverse-linked-list/) - if a variant stored digits
  most-significant-first you would reverse both lists first, then apply this solution.
- [0143 - Reorder List](../0143-reorder-list/) - the other multi-step Medium in this section,
  combining the techniques from the Easy problems above it.
