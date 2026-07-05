# 0543 - Diameter of Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/diameter-of-binary-tree/

## Problem

The *diameter* of a binary tree is the length (number of edges) of the longest path between any
two nodes. The path may or may not pass through the root. Given `root`, return the diameter.

Signature:

    int diameterOfBinaryTree(TreeNode root)

Examples:

    Input:  root = [1,2,3,4,5]
    Output: 3

    Input:  root = [1,2]
    Output: 1

## Intuition

The longest path through *any* node looks like an arch: down through its deepest left descendant
and down through its deepest right descendant. The length of that arch is `leftHeight +
rightHeight` (both counted in edges). So at every node we record `left + right` and keep the
maximum seen. The trigger "diameter / longest path" is a postorder DFS where the *return value*
(height) and the *answer* (diameter) are different things -- a recurring design in this pattern.

## Pseudocode

    best = 0

    function diameter(root):
        reset best to 0
        height(root)
        return best

    function height(node):
        if node is null:
            return 0
        left  = height(node.left)
        right = height(node.right)
        best  = max(best, left + right)        # arch through this node
        return 1 + max(left, right)            # height goes UP to the parent

Resetting `best` matters if the solver object is reused across calls.

## Java Solution

```java
class Solution {
    private int best = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        best = 0;
        height(root);
        return best;
    }

    // Returns height (edge count) of subtree; updates the global best diameter seen so far.
    private int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        int left = height(node.left);
        int right = height(node.right);
        best = Math.max(best, left + right);
        return 1 + Math.max(left, right);
    }
}
```

The field `best` is the running answer; it must be reset in the public method because LeetCode
reuses the same `Solution` instance across test cases. The helper returns *edge-count* height
(`null` -> `0`, leaf -> `1 + max(0,0) = 1`) so `left + right` is already in edges -- no `+1` is
needed for the arch, unlike node-count formulations.

## Complexity

    Time:  O(n)   -- every node is visited exactly once
    Space: O(h)   -- recursion stack depth equals tree height (log n balanced, n skewed)

## Dry-Run

Tree `[1,2,3,4,5]`:

```
        1
       / \
      2   3
     / \
    4   5
```

| Call    | left | right | left+right | best after | return (height) |
|---------|-----:|------:|-----------:|-----------:|----------------:|
| height(4) | 0  | 0     | 0          | 0          | 1               |
| height(5) | 0  | 0     | 0          | 0          | 1               |
| height(2) | 1  | 1     | 2          | 2          | 2               |
| height(3) | 0  | 0     | 0          | 2          | 1               |
| height(1) | 2  | 1     | 3          | **3**      | 3               |

Longest path: 4 -> 2 -> 1 -> 3 (three edges). Output: `3`.

## Common mistakes

- Returning `left + right + 1` as the diameter -- that counts nodes, but LeetCode counts *edges*.
  Keep the height as edge-count and the arch as a bare `left + right`.
- Only considering the path through the root. The widest arch may sit inside a subtree, which is
  why `best` is updated at *every* node, not just the root.
- Forgetting to reset the field before each call -- the previous test case's answer leaks in.
- Returning the diameter up the recursion instead of the height. The parent needs height to build
  its own arch; the diameter is a side effect stored separately.

## Related problems

- [0104 - Maximum Depth of Binary Tree](../0104-maximum-depth-of-binary-tree/) - this helper *is*
  Maximum Depth with one extra line that updates `best`.
- [0110 - Balanced Binary Tree](../0110-balanced-binary-tree/) - the same "return height, track
  something else" dual-purpose DFS.
- [0235 - Lowest Common Ancestor of a BST](../0235-lowest-common-ancestor-of-a-bst/) - another
  single-pass DFS that decides the answer from local information.
