# 0226 - Invert Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/invert-binary-tree/

## Concepts used

- **Binary tree** -- a tree where each node has at most two children, called `left` and `right`. [glossary](../../../docs/10-glossary.md#tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Base case** -- the simplest input a recursive function answers without recursing further. [glossary](../../../docs/10-glossary.md#base-case)

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

Picture a family tree drawn on a whiteboard. To make a mirror image of it, you do one
small action at every person: swap whoever is drawn on their left with whoever is on their
right. A [binary tree](../../../docs/10-glossary.md#tree) is exactly that picture -- each
**node** holds a value and has at most two children, `left` and `right`. The top node is the
**root**; a node with no children is a **leaf**. "Invert the tree" means: at every node, swap
its left and right children.

Trace the smallest interesting case, `[1,2,3]`:

```
      1                1
     / \     --->      / \
    2   3             3   2
```

At the root `1`, the two children are `2` (left) and `3` (right). One swap turns them into
`3` (left) and `2` (right). Nodes `2` and `3` are leaves with nothing to swap, so the answer
is `[1,3,2]`.

To invert the whole tree we use [recursion](../../../docs/10-glossary.md#recursion) -- a
function that calls itself on a smaller input. Reason it through step by step (no leaps of
faith): **assume** `invertTree` already correctly inverts any smaller subtree you hand it.
Then to invert the whole tree you (a) hand the left subtree to `invertTree`, (b) hand the
right subtree to `invertTree`, and (c) swap the two now-inverted subtrees at the root. That
assumption is valid because the *same* three-step logic applies to each subtree you handed
off -- it inverts its own left, its own right, then swaps -- and this keeps going until you
reach a leaf, whose children are both empty. An empty subtree is the
[base case](../../../docs/10-glossary.md#base-case): inverting "nothing" gives back nothing,
so the recursion stops there. Every node is reached exactly once, so the whole tree gets
inverted.

One implementation detail: when you swap, save the original left child in a temporary
variable *before* overwriting it -- otherwise you lose it and accidentally move the wrong
subtree into the right slot. The "recurse both children, then act at the node" shape is a
postorder [DFS](../../../docs/10-glossary.md#dfs-depth-first-search) (depth-first: go as deep
as possible before backtracking); nearly every problem in this pattern reuses it -- for
example [Maximum Depth of Binary Tree](../0104-maximum-depth-of-binary-tree/) combines the
two children with `max` instead of a swap.

### Checkpoint A -- Spot the move at each node

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** To invert a tree, what single action do you perform at every node?
- a) Add together the two children's values
- b) Swap its left and right children
- c) Delete whichever child has the smaller value

<details><summary>Show answer</summary>

**(b)** -- "Invert" means mirror the tree, which is exactly swapping every node's left and right child. Nothing is added or deleted.

</details>

**Q2 (comprehend).** The pseudocode saves `node.left` into `savedLeft` *before* overwriting it. Why?
- a) To avoid creating a new node
- b) Because once `node.left` is overwritten, the original left child is lost, and the right slot would wrongly receive an already-inverted subtree
- c) To make the call tail-recursive
- d) It is just a style preference; removing it changes nothing

<details><summary>Show answer</summary>

**(b)** -- Java reads `invert(root.left)` only when the assignment runs. If `root.left` was already reassigned to the inverted right subtree, the right slot would re-invert the wrong tree. Saving the original pointer first prevents that.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `root = [9,4,15]` (root `9`, left child `4`, right child `15`). After `invertTree` returns, what does the root's left child point to?
- a) node `4`
- b) node `15`
- c) `null`

<details><summary>Show answer</summary>

**(b)** -- the root swaps its two children, so the old right child `15` becomes the new left child. Output level-order is `[9,15,4]`.

</details>

**Q2 (analyze).** Suppose you delete the `if (root == null) return null;` line. What happens when the recursion reaches a leaf and recurses on its `null` children?
- a) It still works; the swap handles leaves fine
- b) It throws `NullPointerException` when it dereferences `null.left`
- c) It silently returns the leaf unchanged by coincidence

<details><summary>Show answer</summary>

**(b)** -- with no null guard, calling the function on a `null` child tries to read `.left`/`.right` of `null`. The null base case exists precisely to stop this.

</details>

**Q3 (transfer).** Instead of *inverting* a tree, suppose you must *check whether a tree is symmetric* (a mirror of itself). How does the approach change, in words?

<details><summary>Show answer</summary>

No mutation. Compare the root's two subtrees as mirror images: recursively check that the left child of one matches the right child of the other (and vice versa), with equal values at each pair. It becomes a twin DFS like Same Tree, but with the children crossed.

</details>

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
