# 0226 - Invert Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/invert-binary-tree/

## Problem

Given the `root` of a binary tree, *invert* it -- for every node, swap its left and right subtrees
-- and return the new root.

Signature:

    TreeNode invertTree(TreeNode root)

Examples:

    Input:  root = [4,2,7,1,3,6,9]
    Output: [4,7,2,9,6,3,1]

    Input:  root = [2,1,3]
    Output: [2,3,1]

    Input:  root = []
    Output: []

## Intuition

This is the gentlest possible recursion problem -- the textbook "trust the recursion" warm-up.
The trigger is "binary tree" plus "mirror / swap children". The leap of faith: assume
`invertTree` already correctly inverts any *smaller* subtree handed to it. Then inverting a single
node takes three moves -- swap the two (already inverted) children, return the node. The base case
is an empty subtree: an inverted `null` is still `null`.

## Pseudocode

    function invert(node):
        if node is null:
            return null
        savedLeft = node.left
        node.left  = invert(node.right)
        node.right = invert(savedLeft)
        return node

The local variable `savedLeft` is essential: once we overwrite `node.left` we would lose the
original left child before we can invert it for the new right slot.

## Java Solution

```java
class Solution {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode savedLeft = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(savedLeft);
        return root;
    }
}
```

The null check at the top is the universal base case for every tree problem in this pattern.
Stashing `root.left` in `savedLeft` before reassigning is the only non-obvious line: Java
evaluates both sides of an assignment, so writing `root.right = invertTree(root.left)` *after*
mutating `root.left` would pass the new (already-inverted) right subtree into the left side by
mistake. We mutate in place and return the same `root`, so the caller's reference stays valid.

## Complexity

    Time:  O(n)   -- every node is visited exactly once
    Space: O(h)   -- recursion depth equals tree height (log n balanced, n skewed)

## Dry-Run

Tree `[4,2,7,1,3,6,9]`:

```
        4                  4
       / \                / \
      2   7    --->      7   2
     /\   /\            /\   /\
    1  3 6  9          9  6 3  1
```

Recursion tree (postorder swap):

| Call                | left child | right child | After swap     |
|---------------------|------------|-------------|----------------|
| invert(4)           | 2          | 7           | left=7, right=2 |
| invert(2)           | 1          | 3           | left=3, right=1 |
| invert(7)           | 6          | 9           | left=9, right=6 |
| invert(1)           | null       | null        | unchanged      |
| invert(3)           | null       | null        | unchanged      |
| invert(6)           | null       | null        | unchanged      |
| invert(9)           | null       | null        | unchanged      |

Output root is still node `4`, but its children now read `7, 2, 9, 6, 3, 1` in level order.

## Common mistakes

- Reassigning `root.left` first, then calling `invertTree(root.left)` for the right side -- this
  re-inverts the subtree you just installed, producing a double-inverted (i.e. original) right
  side. Always save the old pointer first.
- Forgetting the `root == null` base case -- any leaf's `null` child is then dereferenced and the
  program throws `NullPointerException`.
- Allocating a brand-new tree instead of mutating in place. Both are correct, but the mutating
  version uses `O(h)` stack only and is the answer LeetCode expects.
- Trying to invert iteratively with a stack on the first attempt. That works, but the recursive
  form is the canonical "learn recursion" exercise -- get this one right first.

## Related problems

- [0104 - Maximum Depth of Binary Tree](../0104-maximum-depth-of-binary-tree/) - same postorder
  recursion, but combines children with `max` instead of swapping.
- [0100 - Same Tree](../0100-same-tree/) - twin DFS that reuses the null/value checks learned here.
- [0102 - Binary Tree Level Order Traversal](../0102-binary-tree-level-order-traversal/) - the BFS
  counterpart to this DFS warm-up.
