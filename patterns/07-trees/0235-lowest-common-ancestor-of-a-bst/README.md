# 0235 - Lowest Common Ancestor of a Binary Search Tree

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/

## Concepts used

- **Binary search tree (BST)** -- a binary tree where, for every node, all values in its left subtree are smaller and all values in its right subtree are larger. [glossary](../../../docs/10-glossary.md#binary-search-tree-bst)
- **Tree** -- a hierarchy of nodes with one root at the top and no cycles. [glossary](../../../docs/10-glossary.md#tree)

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

Imagine a company where every manager's left-hand team has lower employee IDs and right-hand
team has higher IDs. To find the lowest-ranking manager who oversees two specific employees
`p` and `q`, you walk down from the CEO: if both IDs are smaller than the current manager's,
go left; if both are bigger, go right; the moment they lie on opposite sides -- or you step
onto one of them -- that manager is the answer. This works because the input is a
[binary search tree](../../../docs/10-glossary.md#binary-search-tree-bst) (BST): at every
[node](../../../docs/10-glossary.md#tree), all values in its left subtree are smaller and all
values in its right subtree are larger, so one comparison tells you which side any value lives
on.

Trace `root = [6,2,8,0,4,7,9,null,null,3,5]`, `p = 2`, `q = 8`:

```
        6
       / \
      2   8
```

At the root `6`: `p = 2 < 6` (lives on the left), `q = 8 > 6` (lives on the right). They have
split. Going left would abandon `q`; going right would abandon `p`. So `6` is the deepest node
that is still an ancestor of both -- the **lowest common ancestor** (LCA). Answer: `6`. For
`p = 2, q = 4`: both are `< 6`, so go left to node `2`. Now `p == 2`, and a node is its own
ancestor, so `2` is the LCA.

Here is the reasoning made explicit (no handwaving). The LCA is, by definition, the *deepest*
node that is an ancestor of both `p` and `q`. As long as `p` and `q` sit on the *same* side of
the current node, the current node is not the deepest common ancestor -- there is a deeper one
(the next node down on that side) -- so we must keep descending. The instant they sit on
*opposite* sides (or one of them equals the current node), descending further would mean
abandoning one of them, so no deeper node can still be an ancestor of both. That fork is
therefore the LCA. The whole thing is a single descending walk -- no recursion, no
backtracking -- which is why it uses `O(1)` extra space, unlike the ordinary-tree version
(LC 236) that has no ordering to exploit and must recurse through the whole tree.

### Checkpoint A -- Spot the BST shortcut

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** The BST property says: all values in a node's left subtree are ____ the node, and all in the right subtree are ____.
- a) equal to; greater than
- b) smaller than; larger than
- c) larger than; smaller than

<details><summary>Show answer</summary>

**(b)** -- left subtree values are smaller, right subtree values are larger. One comparison against the current node tells you which side any value lives on.

</details>

**Q2 (comprehend).** The loop descends while `p` and `q` are on the SAME side, and returns the node the moment they split. Why is that split point the LOWEST common ancestor, not just any ancestor?
- a) Because it is the root of the whole tree
- b) Because descending any further would mean walking toward only one of `p`/`q`, abandoning the other, so no deeper node can still be an ancestor of both
- c) Because BST nodes are sorted by depth

<details><summary>Show answer</summary>

**(b)** -- once the targets lie on opposite sides, going either direction leaves one target behind. So this node is the deepest one that still has both targets underneath it.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** BST `root = [10,5,15]` (root `10`, left `5`, right `15`), with `p = 5`, `q = 15`. What is the LCA?
- a) node `5`
- b) node `10`
- c) node `15`

<details><summary>Show answer</summary>

**(b)** -- at the root `10`, `5 < 10` and `15 > 10`: they split, so the loop returns `10` immediately without descending.

</details>

**Q2 (analyze).** This solution uses `O(1)` extra space. Why can the ordinary-binary-tree LCA (no ordering) NOT match that?
- a) Because ordinary trees simply have more nodes
- b) Because with no ordering, you cannot know which side a value lives on, so you must explore both subtrees via recursion, which needs an `O(h)` stack
- c) Because Java forbids iteration on ordinary trees

<details><summary>Show answer</summary>

**(b)** -- without the BST property there is no single "go left or right" decision, so the search must fan out into both subtrees and combine results on the way back. That recursion costs `O(h)` space.

</details>

**Q3 (transfer).** How would you find the LCA of two nodes in an ordinary (non-search) binary tree, where no ordering is available? Outline the idea in words.

<details><summary>Show answer</summary>

Recurse from the root: a node is the LCA when `p` and `q` are found in different subtrees of it (one in each), or when the node itself is `p` or `q` and the other lies somewhere beneath it. Each call reports whether its subtree contains `p` and/or `q`; the node where both sides report "found" is the LCA. This is `O(n)` time and `O(h)` space.

</details>

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
