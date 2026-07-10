# 0104 - Maximum Depth of Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/maximum-depth-of-binary-tree/

## Concepts used

- **Binary tree** -- a tree where each node has at most two children. [glossary](../../../docs/10-glossary.md#tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Base case** -- the simplest input a recursive function answers without recursing further. [glossary](../../../docs/10-glossary.md#base-case)

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

Think of a family tree drawn top-down. The **maximum depth** is "how many generations deep is
the deepest person" -- count yourself, then keep going down through children until you reach
someone with no children; the depth is the number of people on the *longest* such chain,
counting both ends. In tree terms: the maximum depth is the number of
[nodes](../../../docs/10-glossary.md#tree) on the longest path from the **root** (top) down to
a **leaf** (a node with no children). A single node has depth 1; an empty tree has depth 0.

Trace `[3,9,20,null,null,15,7]`:

```
        3
       / \
      9   20
         /  \
        15   7
```

The left branch from `3` stops at `9` -- that chain is `3 -> 9`, depth 2. The right branch
goes `3 -> 20 -> 15` (or `-> 7`), depth 3. The longest is 3, so the answer is 3.

The depth of any node is `1` (the node itself) plus the *larger* of its two children's depths.
We compute it with [recursion](../../../docs/10-glossary.md#recursion) -- a function that calls
itself on a smaller input. Reason it out explicitly (no leaps of faith): **assume** `maxDepth`
already returns the correct depth of any smaller subtree handed to it. Then the depth at the
root is `1 + max(maxDepth(left subtree), maxDepth(right subtree))`. That assumption is valid
because the *same* `1 + max(left, right)` rule applies inside each subtree, all the way down to
a leaf, whose children are both empty. An empty subtree is the
[base case](../../../docs/10-glossary.md#base-case): it returns depth `0`, which is exactly
what makes a leaf's own depth resolve to `1 + max(0, 0) = 1`. This shape -- recurse both
children, then combine at the node -- is a postorder
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search), and it is the template for most of
this pattern: [Invert Binary Tree](../0226-invert-binary-tree/) swaps instead of taking the
`max`, and [Balanced Binary Tree](../0110-balanced-binary-tree/) builds directly on it.

### Checkpoint A -- Spot the combine step

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** By this book's count, a tree with a single node and no children has depth...?
- a) 0
- b) 1
- c) undefined

<details><summary>Show answer</summary>

**(b)** -- LeetCode counts *nodes* on the root-to-leaf path, so one node is depth 1. Depth 0 is reserved for the empty tree.

</details>

**Q2 (comprehend).** The depth at any node is `1 + max(leftDepth, rightDepth)`. What is the `1` for?
- a) Counting the node itself
- b) Converting edges into nodes
- c) Handling the empty-tree base case
- d) It is required by `Math.max`

<details><summary>Show answer</summary>

**(a)** -- the node itself sits on top of whichever child chain is deeper, so it adds 1 to the deeper of its two subtrees' depths. The empty-tree case is handled by the `null` branch returning 0.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace this tree (root `5`; left child `4` is a leaf; right child `6` has children `3` and `7`):

```
        5
       / \
      4   6
         / \
        3   7
```

What does `maxDepth` return?
- a) 2
- b) 3
- c) 4

<details><summary>Show answer</summary>

**(b)** -- the right branch `5 -> 6 -> 3` (or `-> 7`) is three nodes deep; the left branch `5 -> 4` is only two. `1 + max(1, 2) = 3`.

</details>

**Q2 (analyze).** What does `maxDepth(null)` return, and why does that matter for a leaf?
- a) `0` -- the base case; a leaf's children both return 0, so the leaf's own depth resolves to `1 + max(0,0) = 1`
- b) `1` -- counting the missing root
- c) It throws `NullPointerException`

<details><summary>Show answer</summary>

**(a)** -- `null` is the base case returning 0. That is exactly what makes a leaf compute to depth 1 rather than depth 0.

</details>

**Q3 (transfer).** How would you find the *minimum* depth (shortest root-to-leaf path) instead of the maximum? Sketch the idea in words.

<details><summary>Show answer</summary>

The cleanest way is BFS level-order: descend row by row, and the first leaf you reach is at the minimum depth (return its depth). Doing it with DFS needs care, because a node with one `null` child is NOT a leaf -- you must only take `min` over children that actually exist.

</details>

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
