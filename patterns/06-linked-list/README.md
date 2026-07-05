# Pattern 6 - Linked List

A linked list is a chain of nodes where each node holds a value and a single pointer
(`next`) to the next node. Unlike an array, you cannot jump to index `i` in O(1); you must
walk from the head. The trade-off: inserting or deleting a node is O(1) once you are
standing on its predecessor -- you just rewrite two pointers instead of shifting every
element.

This pattern is about **pointer rewriting**. Almost every linked-list problem is solved by
carefully reassigning `.next` fields as you walk the list. There are no fancy data
structures here -- just two or three pointers moving at the right speed and the right time.

---

## What the pattern is

Every solution in this section reduces to one of three mechanical techniques:

1. **Pointer rewriting (reverse / reorder / interleave).** Walk the list with a
   `previous / current` pair. On each step, point the current node's `.next` *backwards*,
   then slide both pointers forward. This is how you reverse a list, and reversing is a
   sub-step of half the problems here (Reorder, Palindrome).

2. **Slow / fast pointers (Floyd's technique).** Launch two pointers from the head: `slow`
   advances one node per step, `fast` advances two. They are the key to two questions you
   cannot answer by walking once:
   - **"Is there a cycle?"** -- if `fast` ever lands on `slow`, the list loops back on
     itself. (Linked List Cycle)
   - **"Where is the middle?"** -- when `fast` reaches the end, `slow` is at the midpoint.
     (Reorder List, Palindrome Linked List)

3. **The dummy head idiom.** When you *build* or *delete* at the head of a list, the head
   itself is a special case. A dummy node placed *before* the head lets you treat the head
   exactly like every other node, then return `dummy.next` as the real head. (Merge Two
   Sorted, Remove Nth, Add Two Numbers.)

All three cost O(n) time and O(1) extra space -- the whole point is to avoid allocating a
second list.

---

## When it applies -- trigger signals

Reach for this pattern when the input is explicitly a linked list, or when the words below
appear:

| Trigger signal | Example problem | Which technique |
|---|---|---|
| "reverse the list" | Reverse Linked List (206) | pointer rewriting |
| "merge two sorted lists" | Merge Two Sorted Lists (21) | dummy head |
| "reorder", "interleave first and last" | Reorder List (143) | find middle + reverse + interleave |
| "remove nth from end" | Remove Nth Node (19) | two pointers with n-gap + dummy head |
| "cycle", "loop", "does it ever repeat?" | Linked List Cycle (141) | Floyd slow/fast |
| "add two numbers stored in reverse" | Add Two Numbers (2) | digit-by-digit with carry + dummy head |
| "is it a palindrome?" | Palindrome Linked List (234) | find middle + reverse + compare |

If you see an array input, do **not** reach here -- use Arrays & Hashing (1), Two Pointers
(2), or Sliding Window (3) instead. Linked List techniques assume you cannot index; you
can only follow `next`.

---

## General pseudocode templates

### Template A -- pointer rewriting (reverse)

This four-line body is the heart of reversing. Memorize the order of the four statements.

```
function reverse(head):
    prev <- null
    curr <- head
    while curr is not null:
        next <- curr.next        # 1. remember the rest of the list
        curr.next <- prev        # 2. flip the current node's arrow backwards
        prev <- curr             # 3. slide prev forward
        curr <- next             # 4. slide curr forward
    return prev                  # prev is the new head; curr is null
```

The order matters: you must save `curr.next` *before* overwriting it, and you must advance
`prev` and `curr` from the *saved* values, not from the (now-broken) list.

### Template B -- slow / fast pointers (Floyd)

Two questions, same setup. Launch both at the head, move them at different speeds.

**Cycle detection** -- the moment `fast` catches `slow` from behind, a loop exists:

```
function hasCycle(head):
    slow <- head
    fast <- head
    while fast is not null and fast.next is not null:   # fast runs out first on a flat list
        slow <- slow.next
        fast <- fast.next.next
        if slow is fast:            # they met -> a cycle pulled fast back onto slow
            return true
    return false                    # fast hit the end -> no cycle
```

**Finding the middle** -- let fast run to the end; slow stops at the midpoint. The loop
condition changes so that on an even-length list `slow` lands on the *first* middle node,
which makes splitting clean:

```
function middle(head):
    slow <- head
    fast <- head
    while fast.next is not null and fast.next.next is not null:
        slow <- slow.next
        fast <- fast.next.next
    return slow                     # first-middle for even length; exact middle for odd
```

### The dummy head idiom

When the answer list is being *built* node-by-node, or when the original *head* might be the
node you delete, a dummy node in front removes every special case:

```
function buildOrDelete(head):
    dummy  <- new Node(any value)   # throwaway node before the real head
    tail   <- dummy                 # for building; OR prev <- dummy for deleting

    ... walk the list, always attaching to tail.next / unlinking via prev ...

    return dummy.next               # the real head (possibly different from the input head)
```

Why it works: the real head is now just "the node after dummy", which is the same operation
as "the node after any other predecessor". No `if (head == target)` branch, no
"did the head change?" bookkeeping. You will see this in Merge, Remove Nth, and Add Two
Numbers.

---

## Problems in this section

Seven problems, Easy first. Do them in order -- each one layers a new technique on top of
reverse or slow/fast.

| # | Folder | Problem | Difficulty | One-line teaser |
|---|---|---|---|---|
| 1 | [0206-reverse-linked-list](./0206-reverse-linked-list/) | Reverse Linked List | Easy | The four-line pointer flip every other problem depends on. |
| 2 | [0021-merge-two-sorted-lists](./0021-merge-two-sorted-lists/) | Merge Two Sorted Lists | Easy | A dummy head lets you splice without special-casing the first node. |
| 3 | [0141-linked-list-cycle](./0141-linked-list-cycle/) | Linked List Cycle | Easy | Slow and fast on the same list -- if they meet, it loops. |
| 4 | [0234-palindrome-linked-list](./0234-palindrome-linked-list/) | Palindrome Linked List | Easy | Find the middle, reverse the back half, compare. |
| 5 | [0019-remove-nth-node-from-end-of-list](./0019-remove-nth-node-from-end-of-list/) | Remove Nth Node From End | Medium | Give fast an n-node head start so slow lands on the deletion target's predecessor. |
| 6 | [0143-reorder-list](./0143-reorder-list/) | Reorder List | Medium | Three steps in one: middle, reverse, interleave. |
| 7 | [0002-add-two-numbers](./0002-add-two-numbers/) | Add Two Numbers | Medium | Digit-by-digit addition with a carry flag, stitched onto a dummy head. |

---

## Common pitfalls of the pattern

Linked-list code is short but ruthlessly unforgiving about order. Beginners hit these
again and again:

- **Losing the head.** Once you start moving `head` or reassigning `.next` fields, the
  variable that *was* the head may now point into the middle of a rewritten list. Always
  keep a stable reference (the input `head`, or a `dummy`) and return from *that*, not from
  a pointer you have been mutating.

- **Breaking links in the wrong order.** Reversing requires saving `curr.next` *before*
  overwriting it. If you write `curr.next = prev` first, you have just thrown away the rest
  of the list and the loop ends immediately. The four statements in Template A are ordered
  for exactly this reason.

- **Forgetting to advance a pointer.** A loop that reassigns links but never moves `curr`
  (or `slow`/`fast`) forward runs forever. After every mutation, ask: "did I move every
  pointer I need to move?"

- **Creating a cycle by not nulling the tail.** When you split or reverse, the node that
  used to be in the middle still has a `.next` pointing into the other half. If you do not
  set `slow.next = null` after splitting, the "two halves" silently stay connected and your
  compare/interleave loop walks in circles. (Bites in Reorder and Palindrome.)

- **NPE on `fast.next.next`.** The Floyd loop conditions are ordered on purpose: check
  `fast != null` *then* `fast.next != null` *then* dereference `fast.next.next`. Java
  short-circuits `&&`, so reversing the order crashes on a one-node list.

- **Off-by-one in "nth from end".** To remove the nth-from-last node you need a pointer on
  the node *before* it, which means fast should get an `(n+1)`-node head start, not `n`.
  This off-by-one is why Remove Nth uses a dummy -- so that removing the head is not
  special.

- **Ignoring the final carry.** In Add Two Numbers the carry can survive past the last
  digit (e.g. 5 + 5 = 10). If your loop condition is `while l1 != null || l2 != null` you
  drop the leading `1`. Add `|| carry != 0` to the condition.

With these in mind, open [0206-reverse-linked-list](./0206-reverse-linked-list/) and start.
