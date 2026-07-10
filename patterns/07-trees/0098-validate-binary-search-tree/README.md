# 0098 - Validate Binary Search Tree

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/validate-binary-search-tree/

## Concepts used

- **Binary search tree (BST)** -- a binary tree where all left-subtree values are smaller and all right-subtree values are larger than the node. [glossary](../../../docs/10-glossary.md#binary-search-tree-bst)
- **Binary tree** -- a tree where each node has at most two children. [glossary](../../../docs/10-glossary.md#tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Base case** -- the simplest input a recursive function answers without recursing further. [glossary](../../../docs/10-glossary.md#base-case)

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

Think of each node as having an "allowed range" of values, like a bouncer checking IDs: "yours
must be strictly between `low` and `high`." The root starts with the widest possible range
(every integer). As you walk *down*, the range shrinks: going into the **left** subtree, the
parent's value becomes the new *upper* limit (everything on the left must be smaller than the
parent); going into the **right** subtree, the parent's value becomes the new *lower* limit. A
[binary search tree](../../../docs/10-glossary.md#binary-search-tree-bst) (BST) demands this
not just against the immediate parent but against *every* ancestor -- so the range a node must
satisfy is built from the whole chain of [nodes](../../../docs/10-glossary.md#tree) above it.

The classic trap is checking only the immediate parent:
`node.left.val < node.val < node.right.val`. That is *necessary* but not *enough*. Take
`[5,1,4,null,null,3,6]`:

```
      5
     / \
    1   4
       / \
      3   6
```

At node `4` the parent check passes (`3 < 4 < 6`). But node `3` lives in the *root's right*
subtree, so the BST rule demands `3 > 5` -- which fails. The parent-only check misses this;
only carrying the root's bound down catches it.

We check every node against its allowed range using
[recursion](../../../docs/10-glossary.md#recursion). Reason it out explicitly (no leaps of
faith): **assume** `validate(node, low, high)` already correctly decides whether any smaller
subtree is a valid BST *within its allowed range*. At the current node: if `node.val` is not
strictly inside `(low, high)`, return `false`; otherwise recurse into the left child with the
upper bound tightened to `node.val`, and into the right child with the lower bound tightened to
`node.val`, combining the two child results with `and`. That assumption is valid because the
same bound-tightening applies to each subtree all the way down to an empty subtree -- the
[base case](../../../docs/10-glossary.md#base-case), which is trivially valid. This depth-first
walk is a [DFS](../../../docs/10-glossary.md#dfs-depth-first-search).

One Java detail: the bounds are `long`, not `int`. A node's legal value can equal
`Integer.MIN_VALUE` or `Integer.MAX_VALUE`; if you started from those as the initial bounds,
the strict `<= low` check would wrongly reject a perfectly legal node. `long` bounds are wider
than any `int`, so every real node value has strict room inside the initial open interval.

### Checkpoint A -- Spot the bounds method

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** Why is checking only `node.left.val < node.val < node.right.val` NOT enough to validate a BST?
- a) It is enough; every node is checked locally
- b) A node deep in the right subtree must still be larger than the ROOT, not just its immediate parent -- the local check misses that
- c) Because values are allowed to be equal

<details><summary>Show answer</summary>

**(b)** -- every ancestor's bound must hold, not only the parent's. The example `[5,1,4,null,null,3,6]` passes every parent check yet is invalid because `3` sits in the root's right subtree but is smaller than `5`.

</details>

**Q2 (comprehend).** The bounds are declared `long`, not `int`. What bug does this prevent?
- a) Stack overflow on deep trees
- b) A node whose value equals `Integer.MIN_VALUE` or `Integer.MAX_VALUE` (both legal inputs) being wrongly rejected by the strict bound check
- c) `NullPointerException` on missing nodes

<details><summary>Show answer</summary>

**(b)** -- `long` bounds are wider than any `int`, so every real node value sits strictly inside the initial open interval. With `int` bounds, a legal extreme value would trip the `<= low` / `>= high` test.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace this tree:

```
      10
       \
        15
       /  \
      6    20
```

(`10`'s right child is `15`; `15`'s children are `6` and `20`.) Is it a valid BST?
- a) Valid -- every parent-child pair is locally ordered
- b) Invalid -- node `6` sits in `10`'s right subtree but `6 < 10`

<details><summary>Show answer</summary>

**(b)** -- descending into `10`'s right subtree tightens the lower bound to `10`, so everything there must exceed `10`. Node `6` fails `6 <= 10` and the whole tree is rejected.

</details>

**Q2 (analyze).** Suppose the tree contains two nodes with the SAME value (a duplicate). What does this solution return?
- a) Valid -- duplicates are fine in a BST
- b) Invalid -- the bounds check uses `<=` / `>=` (strict inequality), so any duplicate is rejected
- c) It depends on which subtree the duplicate is in

<details><summary>Show answer</summary>

**(b)** -- the problem requires strict inequality, enforced by `<= low || >= high`. The second copy of a value equals the bound set by the first, so it fails.

</details>

**Q3 (transfer).** An inorder walk of a valid BST visits values in sorted order. How could you validate a BST using only an inorder traversal and one extra variable? Sketch it in words.

<details><summary>Show answer</summary>

Traverse inorder while remembering the previously visited value. A valid BST yields a strictly increasing sequence, so if any visited value is `<=` the previous one, reject it. No bounds are passed down -- the sorted-order check alone is sufficient.

</details>

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
