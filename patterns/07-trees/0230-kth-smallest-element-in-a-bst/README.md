# 0230 - Kth Smallest Element in a BST

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/kth-smallest-element-in-a-bst/

## Concepts used

- **Binary search tree (BST)** -- a binary tree where all left-subtree values are smaller and all right-subtree values are larger than the node. [glossary](../../../docs/10-glossary.md#binary-search-tree-bst)
- **Inorder traversal** -- a depth-first order that visits the left subtree, then the node, then the right subtree; on a BST it visits values in sorted order. [glossary](../../../docs/10-glossary.md#preorder--inorder--postorder)
- **Stack** -- a last-in-first-out container: the most recent item added is the first removed. [glossary](../../../docs/10-glossary.md#stack)

## Problem

Given the `root` of a binary search tree and an integer `k`, return the `k`-th smallest value among
all node values (1-indexed).

Signature:

    int kthSmallest(TreeNode root, int k)

Examples:

    Input:  root = [3,1,4,null,2], k = 1
    Output: 1

    Input:  root = [5,3,6,2,4,null,null,1], k = 3
    Output: 3

## Intuition

A [binary search tree](../../../docs/10-glossary.md#binary-search-tree-bst) (BST) is like a
sorted filing cabinet arranged as a tree: everything in a node's left subtree is smaller,
everything in its right subtree is larger. If you visit the files in a special order -- *left
subtree first, then the node itself, then the right subtree* -- you read them out in ascending
order. That order is called an **inorder traversal** (one of the three depth-first orderings
on a tree; see [Preorder / Inorder / Postorder](../../../docs/10-glossary.md#preorder--inorder--postorder)).
So "the kth smallest value" is simply "the kth node an inorder walk visits."

Trace `root = [5,3,6,2,4,null,null,1]`, `k = 3`:

```
        5
       / \
      3   6
     / \
    2   4
   /
  1
```

Inorder always goes leftmost first. From `5`, go left to `3`, left to `2`, left to `1` -- no
left child, so visit `1`. Back up to `2` (visit), back to `3` (visit), then right to `4`
(visit). Back up to `5` (visit), then right to `6` (visit). The visit order is
`1, 2, 3, 4, 5, 6`. The 3rd node visited is `3` -- the answer.

We could collect the whole visit sequence into a list and read index `k-1`, but that does more
work than needed. Instead we run the inorder walk *iteratively* with an explicit
[stack](../../../docs/10-glossary.md#stack) (last-in-first-out), so we can **stop the instant
we have visited k nodes**. Here is the mechanic, which simulates exactly what the recursive
version would do:

- From the current node, walk left as far as possible, pushing each node onto the stack. The
  stack now holds the chain of suspended ancestors, deepest on top.
- When you cannot go left any further, pop the top -- that is the next node in sorted order.
  Visit it (decrement `k`); if `k` hits `0`, return its value immediately.
- Then move to its *right* child and repeat the left-walk from there. If there is no right
  child, the next pop simply resumes from where the previous descent was suspended.

The reason this yields sorted order: the leftmost unvisited node is always the smallest one
remaining, and popping it before any of its ancestors (which hold larger values) keeps the
visit sequence ascending. The early return at `k == 0` means that on average we touch far
fewer than `n` nodes.

### Checkpoint A -- Spot the traversal

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** Which traversal visits a BST's values in ascending sorted order?
- a) Preorder
- b) Inorder
- c) Postorder
- d) Level-order

<details><summary>Show answer</summary>

**(b)** -- inorder (left, node, right) on a BST yields values from smallest to largest. So "the kth smallest" is simply "the kth node an inorder walk visits."

</details>

**Q2 (comprehend).** Why does the iterative version stop the moment `k` hits `0`, instead of collecting the whole inorder sequence?
- a) To save stack space
- b) Because the kth node visited in sorted order IS the kth smallest; visiting any more nodes is wasted work
- c) Because the stack would overflow otherwise

<details><summary>Show answer</summary>

**(b)** -- the moment we have popped k nodes we have the answer. Stopping early means we usually touch far fewer than n nodes.

</details>

## Pseudocode

    function kthSmallest(root, k):
        stack = empty stack
        node = root
        while stack is not empty or node is not null:
            while node is not null:           # walk all the way left, pushing each
                push node onto stack
                node = node.left
            node = stack.pop()                # next node in sorted order
            decrement k
            if k == 0:
                return node.value             # early exit
            node = node.right                 # then go right, and resume the left-walk
        # k was larger than the node count

The inner `while` dives left as far as possible; each pop yields the next value in sorted order;
moving to the right child after a pop is what makes the traversal resume correctly.

## Java Solution

```java
class Solution {
    public int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode node = root;
        while (!stack.isEmpty() || node != null) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
            node = stack.pop();
            k--;
            if (k == 0) {
                return node.val;
            }
            node = node.right;
        }
        throw new IllegalArgumentException("k is larger than the number of nodes");
    }
}
```

`ArrayDeque` serves as the stack (the book's preferred LIFO, `03-java-crash-course.md` section 4).
The outer loop condition `!stack.isEmpty() || node != null` is the standard iterative-inorder
guard: we keep going while there is either a suspended ancestor on the stack or a node to descend
into. Decrementing `k` on each pop and returning immediately at `0` is the early-exit -- on
average we touch far fewer than `n` nodes. The trailing `throw` is unreachable on valid input but
satisfies the compiler and documents the precondition.

## Complexity

    Time:  O(h + k)   -- O(h) to reach the first (leftmost) node, then k pops to find the answer;
                          O(n) worst case when k is the largest element
    Space: O(h)       -- stack holds at most one root-to-leaf path (log n balanced, n skewed)

## Dry-Run

Tree `[5,3,6,2,4,null,null,1]`, `k = 3`:

```
        5
       / \
      3   6
     / \
    2   4
   /
  1
```

Inorder order: 1, 2, 3, 4, 5, 6.

| Step | action                    | stack (top right)   | node | k after |
|-----:|---------------------------|---------------------|------|--------:|
| 1    | dive left from 5          | [5, 3, 2, 1]        | null | 3       |
| 2    | pop 1                     | [5, 3, 2]           | 1    | 2       |
| 3    | 1.right is null; pop 2    | [5, 3]              | 2    | 1       |
| 4    | 2.right is null; pop 3    | [5]                 | 3    | **0**   |

`k == 0` after popping node `3` -> return `3`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace this BST with `k = 4`:

```
        4
       / \
      2   5
     / \
    1   3
```

Inorder visits `1, 2, 3, 4, 5`. What is returned?
- a) `3`
- b) `4`
- c) `5`

<details><summary>Show answer</summary>

**(b)** -- the 4th value in sorted order is `4`. The walk pops `1`, `2`, `3`, then `4`, at which point `k` reaches `0` and `4` is returned.

</details>

**Q2 (analyze).** What happens if you forget the `node = node.right` step after a pop?
- a) Nothing changes; the traversal still works
- b) The traversal collapses to a left-spine walk and misses every right subtree, returning the wrong value
- c) It throws an exception

<details><summary>Show answer</summary>

**(b)** -- after visiting a node, the next value in sorted order lives in its right subtree (or is the next suspended ancestor). Skipping the right move discards whole subtrees and the sequence becomes wrong.

</details>

**Q3 (transfer).** How would you find the KTH LARGEST element instead of the kth smallest, reusing the same idea? Outline it in words.

<details><summary>Show answer</summary>

Reverse the visit order: a REVERSED inorder (right subtree, then node, then left subtree) yields values in descending order on a BST. Run that reversed inorder and stop at the kth node visited -- that is the kth largest. Everything else (the stack, the early exit) stays the same.

</details>

## Common mistakes

- Doing a *full* inorder traversal into a list, then indexing `[k-1]`. Correct but `O(n)` time and
  `O(n)` space always; the iterative early-exit is faster on average and uses only `O(h)` space.
- Using preorder or BFS -- neither yields sorted order, so "kth visited" is meaningless.
- Forgetting the `node = node.right` step after a pop. Without it the traversal collapses to a
  left-spine walk and misses every right subtree.
- Trying recursion with a counter: works, but the counter must be a field or a single-element
  array (ints are pass-by-value), and the early return must propagate up -- the iterative form is
  less error-prone.

## Related problems

- [0098 - Validate Binary Search Tree](../0098-validate-binary-search-tree/) - another inorder-
  flavoured BST problem (an inorder sweep is also how you can verify sortedness).
- [0235 - Lowest Common Ancestor of a BST](../0235-lowest-common-ancestor-of-a-bst/) - the third
  problem in this pattern that depends on the BST ordering property.
- [0102 - Binary Tree Level Order Traversal](../0102-binary-tree-level-order-traversal/) - the
  other fundamental traversal (BFS) in this pattern, for contrast.
