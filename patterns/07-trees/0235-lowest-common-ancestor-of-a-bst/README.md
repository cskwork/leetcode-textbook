# 0235 - Lowest Common Ancestor of a Binary Search Tree

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/

## Problem

Given a binary *search* tree `root` and two nodes `p` and `q`, return their *lowest common
ancestor* (LCA) -- the deepest node that has both `p` and `q` as descendants (a node is its own
descendant). The values are guaranteed unique.

Signature:

    TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q)

Examples:

    Input:  root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
    Output: 6

    Input:  root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
    Output: 2

## Intuition

This is a BST problem, not an ordinary-tree problem -- the trigger is "lowest common ancestor" +
"BST". The ordering property (`left < node < right`) makes the LCA trivial to spot: walk down from
the root, and the instant `p` and `q` stop being on the *same* side of the current node, that node
is the split point -- which is exactly the LCA. If both are smaller, go left; if both are larger,
go right; otherwise we have found the fork. No recursion state, no backtracking: a single
descending walk.

## Pseudocode

    function lca(root, p, q):
        node = root
        while node is not null:
            if p.value < node.value and q.value < node.value:
                node = node.left                 # both on the left, descend
            else if p.value > node.value and q.value > node.value:
                node = node.right                # both on the right, descend
            else:
                return node                      # split point (or p/q is the node itself)

The `else` covers three cases at once: `p` is the node, `q` is the node, or `p` and `q` lie on
opposite sides. All three mean "this node is the LCA".

## Java Solution

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode node = root;
        while (node != null) {
            if (p.val < node.val && q.val < node.val) {
                node = node.left;
            } else if (p.val > node.val && q.val > node.val) {
                node = node.right;
            } else {
                return node;
            }
        }
        return null;
    }
}
```

The iterative form is `O(1)` space -- no recursion stack -- which is the main win over the
recursive version. Comparing `p.val` against `node.val` (not object identity) is what makes the
BST property usable; a node is its own ancestor, so the moment either `p` or `q` *equals* the
current value, the `else` fires and returns it. The trailing `return null` only triggers if `p`
or `q` is not actually present in the tree, which LeetCode guarantees does not happen here.

## Complexity

    Time:  O(h)   -- we descend one path from root to the LCA (log n balanced, n skewed)
    Space: O(1)   -- constant extra memory; iterative, no recursion stack

## Dry-Run

Tree `[6,2,8,0,4,7,9,null,null,3,5]`, `p = 2`, `q = 8`:

```
        6
       / \
      2   8
     /\   /\
    0  4 7  9
       /\
      3  5
```

| Iteration | node.val | p, q vs node            | action       |
|-----------|---------:|-------------------------|--------------|
| 1         | 6        | 2 < 6, 8 > 6 (split)    | return 6     |

Output: node `6`.

For `p = 2`, `q = 4`: at node `6`, both `2, 4 < 6`, go left to `2`; at node `2`, `p.val == node`,
so the `else` fires and returns `2` (a node is its own ancestor).

## Common mistakes

- Treating this like the *ordinary binary tree* LCA (LC 236), which needs full recursion because
  there is no ordering to exploit. On a BST that recursion is `O(n)` time and `O(h)` space -- far
  slower than the descending walk.
- Comparing node *references* instead of values -- works only when `p`/`q` are the exact same
  objects LeetCode hands you, which is fragile. Compare `val`.
- Going left only when *strictly* smaller, but forgetting the symmetric "go right only when
  strictly larger" -- a missing branch silently returns the wrong ancestor.
- Stopping the descent too early: you must keep going while both targets are on the same side;
  only stop at the genuine split.

## Related problems

- [0098 - Validate Binary Search Tree](../0098-validate-binary-search-tree/) - the other problem
  that lives or dies on the BST ordering property.
- [0230 - Kth Smallest Element in a BST](../0230-kth-smallest-element-in-a-bst/) - uses the
  ordering in a different way: inorder yields sorted values.
- [0235 in the Graphs sense] - the ordinary-tree variant (LC 236) is the recursive sibling worth
  contrasting against: same goal, no ordering shortcut.
