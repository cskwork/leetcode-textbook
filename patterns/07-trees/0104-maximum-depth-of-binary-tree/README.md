# 0104 - Maximum Depth of Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/maximum-depth-of-binary-tree/

## Problem

Given the `root` of a binary tree, return its *maximum depth* -- the number of nodes along the
longest path from the root down to a leaf.

Signature:

    int maxDepth(TreeNode root)

Examples:

    Input:  root = [3,9,20,null,null,15,7]
    Output: 3

    Input:  root = [1,null,2]
    Output: 2

## Intuition

"Depth / height" is the canonical postorder trigger: the depth of a node is one (itself) plus the
greater of its two children's depths. Apply the leap of faith -- assume `maxDepth` already returns
the correct depth of any smaller subtree -- and the whole function is three lines. The base case:
an empty subtree has depth `0`, which is what makes the `1 +` for a leaf resolve to `1`.

## Pseudocode

    function maxDepth(node):
        if node is null:
            return 0
        leftDepth  = maxDepth(node.left)
        rightDepth = maxDepth(node.right)
        return 1 + max(leftDepth, rightDepth)

This is textbook postorder: recurse both children, then combine at the node.

## Java Solution

```java
class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);
        return 1 + Math.max(leftDepth, rightDepth);
    }
}
```

`null` returns `0` so a leaf's children both contribute `0` and the leaf's own depth becomes
`1 + max(0, 0) = 1`. Storing the two depths in named locals (rather than inlining into the return)
makes the postorder shape visible and simplifies debugging. `Math.max` is `O(1)`; the recursion
itself does all the work.

## Complexity

    Time:  O(n)   -- every node is visited once
    Space: O(h)   -- recursion stack depth equals tree height (log n balanced, n skewed)

## Dry-Run

Tree `[3,9,20,null,null,15,7]`:

```
        3
       / \
      9   20
         /  \
        15   7
```

| Call         | leftDepth | rightDepth | return |
|--------------|----------:|-----------:|-------:|
| maxDepth(null) | -       | -          | 0      |
| maxDepth(9)  | 0         | 0          | 1      |
| maxDepth(15) | 0         | 0          | 1      |
| maxDepth(7)  | 0         | 0          | 1      |
| maxDepth(20) | 1         | 1          | 2      |
| maxDepth(3)  | 1         | 2          | 3      |

Output: `3`.

## Common mistakes

- Returning `0` for a leaf instead of `1` -- happens when the base case fires on a non-null leaf
  (i.e. checking `left == null && right == null` and returning `0`). Use a *single* null base case
  and always add `1` for the node itself.
- Writing `max(maxDepth(left), maxDepth(right)) + 1` -- correct but calls the function twice per
  node, doubling the work. Cache into locals.
- Confusing depth (root-to-leaf count) with height-of-edges. LeetCode counts *nodes*, so a single
  node has depth `1` and an empty tree has depth `0`.
- Iterative BFS without a level counter. Level-order also solves this (count rows), but the DFS
  form is shorter and is the recursive reflex this problem trains.

## Related problems

- [0110 - Balanced Binary Tree](../0110-balanced-binary-tree/) - the same postorder, but the
  helper returns height *and* detects imbalance on the way up.
- [0543 - Diameter of Binary Tree](../0543-diameter-of-binary-tree/) - reuses these two depths to
  measure the widest path through each node.
- [0226 - Invert Binary Tree](../0226-invert-binary-tree/) - the other postorder warm-up.
