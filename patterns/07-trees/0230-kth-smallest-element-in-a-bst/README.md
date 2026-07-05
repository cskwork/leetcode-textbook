# 0230 - Kth Smallest Element in a BST

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/kth-smallest-element-in-a-bst/

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

The key insight is BST-specific: an *inorder* traversal (left, node, right) visits the values in
strictly ascending order. So "the kth smallest" is just "the kth node visited by an inorder
walk." The trigger "kth smallest / sorted order in a BST" pins the traversal flavour. The trick
is to do that walk *iteratively* with an explicit stack, so we can stop the instant we have popped
`k` nodes -- no need to materialise the full sorted list.

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
