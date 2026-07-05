# 0098 - Validate Binary Search Tree

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/validate-binary-search-tree/

## Problem

Given the `root` of a binary tree, determine whether it is a *valid binary search tree*: for every
node, all values in its left subtree are strictly less than the node, and all values in its right
subtree are strictly greater.

Signature:

    boolean isValidBST(TreeNode root)

Examples:

    Input:  root = [2,1,3]
    Output: true

    Input:  root = [5,1,4,null,null,3,6]
    Output: false

## Intuition

The trap is checking only the *immediate* parent: `node.left.val < node.val < node.right.val` is
necessary but not sufficient -- a right grandchild can be smaller than the root while still being
larger than its own parent, and the naive check lets it through. The fix is to carry an open
interval `(low, high)` down each branch: every node must fall *strictly inside* its allowed range,
which shrinks as we recurse. The trigger "validate BST" is a DFS-with-bounds problem; nothing about
it needs a separate traversal pass.

## Pseudocode

    function isValid(node):
        return check(node, -infinity, +infinity)

    function check(node, low, high):
        if node is null:
            return true
        if node.value <= low or node.value >= high:
            return false
        return check(node.left,  low, node.value)        # tighten upper bound
           and check(node.right, node.value, high)       # tighten lower bound

Going left, the current value becomes the new *upper* bound; going right, it becomes the new
*lower* bound. The interval narrows monotonically as you descend.

## Java Solution

```java
class Solution {
    public boolean isValidBST(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    // Every value in this subtree must lie strictly inside the open interval (low, high).
    private boolean validate(TreeNode node, long low, long high) {
        if (node == null) {
            return true;
        }
        if (node.val <= low || node.val >= high) {
            return false;
        }
        return validate(node.left, low, node.val)
            && validate(node.right, node.val, high);
    }
}
```

The bounds are `long`, not `int`. This is the critical detail: if you start from
`Integer.MIN_VALUE` / `Integer.MAX_VALUE`, then a node whose `val` *equals* one of those extremes
is wrongly rejected by `node.val <= low` -- but `Integer.MIN_VALUE` is a perfectly legal node
value in the input. Using `long` bounds (wider than any `int`) gives every real `int` strict room
inside the interval. The `<=` / `>=` (not `<` / `>`) enforce the *strict* inequality the problem
requires, so duplicate values are rejected.

## Complexity

    Time:  O(n)   -- every node is visited at most once (short-circuits on first failure)
    Space: O(h)   -- recursion stack depth equals tree height (log n balanced, n skewed)

## Dry-Run

Valid tree `[2,1,3]`:

```
      2
     / \
    1   3
```

| Call            | low                  | high                 | node.val | result              |
|-----------------|----------------------|----------------------|---------:|---------------------|
| validate(2)     | Long.MIN_VALUE       | Long.MAX_VALUE       | 2        | 2 inside -> recurse |
| validate(1)     | Long.MIN_VALUE       | 2                    | 1        | 1 inside -> true    |
| validate(3)     | 2                    | Long.MAX_VALUE       | 3        | 3 inside -> true    |

Output: `true`.

Invalid tree `[5,1,4,null,null,3,6]` -- the right subtree's `3` is smaller than the root `5`:

| Call            | low            | high            | node.val | result                  |
|-----------------|----------------|-----------------|---------:|-------------------------|
| validate(5)     | MIN            | MAX             | 5        | recurse                 |
| validate(4)     | 5              | MAX             | 4        | 4 < 5 == `<= low` -> false |

Output: `false`.

## Common mistakes

- Using `Integer.MIN_VALUE` / `Integer.MAX_VALUE` as the initial bounds. A node whose value equals
  either extreme (a legal input!) is wrongly rejected. Always widen to `long`.
- Comparing only against the immediate parent (`node.left.val < node.val`). This misses the
  right-subtree-left-child-smaller-than-root case shown above. Bounds are mandatory.
- Allowing equality: a BST requires *strict* inequality. Use `<=` and `>=`, not `<` and `>`.
- Doing an inorder traversal and checking "sorted" -- correct (`O(n)` time, `O(n)` space) but
  needs the previous value carried and is more error-prone than the bounds approach for beginners.

## Related problems

- [0235 - Lowest Common Ancestor of a BST](../0235-lowest-common-ancestor-of-a-bst/) - the other
  problem that hinges entirely on the BST ordering property.
- [0230 - Kth Smallest Element in a BST](../0230-kth-smallest-element-in-a-bst/) - exploits the
  same ordering to produce sorted values via inorder.
- [0100 - Same Tree](../0100-same-tree/) - another "ask one question per node" DFS, here with
  bounds instead of a sibling node.
