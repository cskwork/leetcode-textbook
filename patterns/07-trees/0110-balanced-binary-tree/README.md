# 0110 - Balanced Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/balanced-binary-tree/

## Problem

A binary tree is *height-balanced* if, for every node, the heights of its left and right subtrees
differ by at most 1. Given `root`, return `true` if the tree is height-balanced.

Signature:

    boolean isBalanced(TreeNode root)

Examples:

    Input:  root = [3,9,20,null,null,15,7]
    Output: true

    Input:  root = [1,2,2,3,3,null,null,4,4]
    Output: false

## Intuition

The naive approach -- call `maxDepth` on both children of every node -- is `O(n^2)` because each
depth call re-walks the subtree. The trigger "is balanced" wants a single bottom-up DFS that
returns *both* the height and the balance verdict in one pass. The trick: make the helper return
the height when the subtree is balanced, and a sentinel (`-1`) when it is not. Any caller that
receives `-1` immediately propagates `-1` upward, so the recursion aborts the instant an imbalance
is found.

## Pseudocode

    function isBalanced(root):
        return checkHeight(root) != -1

    function checkHeight(node):
        if node is null:
            return 0
        left = checkHeight(node.left)
        if left == -1:  return -1          # left already bad, bail out
        right = checkHeight(node.right)
        if right == -1: return -1          # right already bad, bail out
        if |left - right| > 1:
            return -1                       # this node is unbalanced
        return 1 + max(left, right)

The sentinel `-1` is safe because real heights are always non-negative.

## Java Solution

```java
class Solution {
    public boolean isBalanced(TreeNode root) {
        return checkHeight(root) != -1;
    }

    // Returns the height of the subtree, or -1 if it is unbalanced (sentinel for "bad").
    private int checkHeight(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int left = checkHeight(node.left);
        if (left == -1) {
            return -1;
        }
        int right = checkHeight(node.right);
        if (right == -1) {
            return -1;
        }
        if (Math.abs(left - right) > 1) {
            return -1;
        }
        return 1 + Math.max(left, right);
    }
}
```

The private helper is the real algorithm; the public method is a one-line boolean view of its
result. Each `-1` check after a recursive call is the early-exit: we never finish exploring a bad
subtree's sibling if the first child already failed. `Math.abs(left - right) > 1` is the textbook
balance definition (heights differ by at most one).

## Complexity

    Time:  O(n)   -- every node is visited at most once; the -1 sentinel prunes bad subtrees early
    Space: O(h)   -- recursion stack depth equals tree height (log n balanced, n skewed)

## Dry-Run

Unbalanced tree `[1,2,3,4,null,null,null,5]`:

```
        1
       / \
      2   3
     /
    4
   /
  5
```

| Call         | left | right | \|l-r\| | return |
|--------------|-----:|------:|--------:|-------:|
| checkHeight(5) | 0 | 0 | 0 | 1 |
| checkHeight(4) | 1 | 0 | 1 | 2 |
| checkHeight(2) | 2 | 0 | 2 | **-1** (imbalance) |
| checkHeight(1) | -1 (left) | - | - | **-1** (propagate) |

`isBalanced` sees `-1` and returns `false`.

## Common mistakes

- Computing height with one helper and balance with a *second* pass per node (`O(n^2)`). The
  sentinel trick folds both into one postorder traversal.
- Forgetting the early-exit checks (`if (left == -1) return -1`) -- then an imbalanced left child
  is still compared against a balanced right child, and the verdict can flip.
- Returning `true`/`false` from the helper instead of the height -- the parent then has no number
  to compare, and the design collapses.
- Treating `null` as unbalanced. An empty subtree is a *balanced* subtree of height `0`.

## Related problems

- [0104 - Maximum Depth of Binary Tree](../0104-maximum-depth-of-binary-tree/) - this helper is
  literally Maximum Depth with a sentinel bolted on.
- [0543 - Diameter of Binary Tree](../0543-diameter-of-binary-tree/) - the same "return height to
  parent, track answer separately" structure.
- [0098 - Validate Binary Search Tree](../0098-validate-binary-search-tree/) - another single-pass
  DFS that bails out early on a bad node.
