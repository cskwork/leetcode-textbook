# 0100 - Same Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/same-tree/

## Problem

Given the roots of two binary trees `p` and `q`, return `true` if they are *structurally
identical* and every corresponding node has the same value.

Signature:

    boolean isSameTree(TreeNode p, TreeNode q)

Examples:

    Input:  p = [1,2,3], q = [1,2,3]
    Output: true

    Input:  p = [1,2], q = [1,null,2]
    Output: false

## Intuition

This is a *twin* DFS -- walk both trees in lockstep and ask one question at every matched pair.
The trigger is "same / identical / mirror two trees". The reasoning is three ordered checks that
fully cover every pairing of the two nodes: both empty (structurally equal so far), exactly one
empty (shape mismatch), or both present (values must match, then recurse into both child pairs).
`&&` short-circuits, so the moment any subtree pair differs the whole call unwinds to `false`.

## Pseudocode

    function isSame(nodeA, nodeB):
        if nodeA is null and nodeB is null:
            return true
        if nodeA is null or nodeB is null:
            return false
        if nodeA.value != nodeB.value:
            return false
        return isSame(nodeA.left, nodeB.left)
           and isSame(nodeA.right, nodeB.right)

The two null checks together form a complete partition: "both null" / "exactly one null" /
"neither null". There is no fourth case.

## Java Solution

```java
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if (p == null || q == null) {
            return false;
        }
        if (p.val != q.val) {
            return false;
        }
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}
```

The two null guards must come before the `p.val` read -- dereferencing a `null` reference throws.
Order matters: the `&&` form (both null) is checked first so an all-empty subtree returns `true`
and never reaches the value comparison. Using primitive `int` from `TreeNode.val` means `!=`
compares true integer values with no boxing surprises.

## Complexity

    Time:  O(min(n, m)) -- we stop at the first mismatch; worst case visits every node of the
                           smaller tree
    Space: O(min(h1, h2)) -- twin recursion, bounded by the shallower tree's height

## Dry-Run

`p = [1,2,3]`, `q = [1,2,3]`:

```
      p              q
      1              1
     / \            / \
    2   3          2   3
```

| Call              | p.val | q.val | result                     |
|-------------------|------:|------:|----------------------------|
| isSame(1p, 1q)    | 1     | 1     | depends on children        |
| isSame(2p, 2q)    | 2     | 2     | both children null -> true |
| isSame(3p, 3q)    | 3     | 3     | both children null -> true |
| back at 1         | -     | -     | true && true -> true       |

Output: `true`.

For the mismatch case `p=[1,2]`, `q=[1,null,2]`: at the root both are `1`, but
`isSameTree(p.left=2, q.left=null)` hits the "exactly one null" branch and returns `false`, which
short-circuits the whole answer.

## Common mistakes

- Writing a single `if (p == q) return true;` -- reference equality compares pointers, not tree
  contents; two separately-built identical trees would wrongly report `false`.
- Comparing `p.val != q.val` *before* the null guards, then dereferencing `null.val` and throwing.
- Returning `false` inside the child calls but forgetting the `&&` -- a single `return` of either
  child's result ignores the other subtree.
- Treating the two null checks as one: `if (p == null || q == null) return false` wrongly rejects
  the valid "both leaves empty" case.

## Related problems

- [0104 - Maximum Depth of Binary Tree](../0104-maximum-depth-of-binary-tree/) - the same recursive
  scaffolding, asking a different question at each node.
- [0226 - Invert Binary Tree](../0226-invert-binary-tree/) - the same null-and-value guards,
  applied to mutation instead of comparison.
- [0098 - Validate Binary Search Tree](../0098-validate-binary-search-tree/) - another "ask one
  question per node" DFS, but with bounds passed down instead of a sibling node.
