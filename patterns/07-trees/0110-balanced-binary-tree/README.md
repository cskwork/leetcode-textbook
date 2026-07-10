# 0110 - Balanced Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/balanced-binary-tree/

## Concepts used

- **Binary tree** -- a tree where each node has at most two children. [glossary](../../../docs/10-glossary.md#tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Base case** -- the simplest input a recursive function answers without recursing further. [glossary](../../../docs/10-glossary.md#base-case)

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

Think of a hanging mobile sculpture: it stays level only if, at *every* joint, the two arms
are roughly the same length. A [binary tree](../../../docs/10-glossary.md#tree) is
**height-balanced** the same way: at every **node**, the **height** of its left subtree and the
height of its right subtree differ by at most 1. (Height here = the number of nodes on the
longest downward path to a **leaf**; a leaf has height 1, an empty subtree has height 0.) The
rule must hold at *every* node, not just the root -- a deep imbalance hidden in one branch
makes the whole tree unbalanced.

Consider this small tree:

```
        1
       / \
      2   3
     /
    4
```

At node `4`: left = 0 (empty), right = 0 -> balanced, height 1. At node `2`: left height = 1
(subtree `4`), right height = 0 (empty). Difference `|1 - 0| = 1`, which is `<= 1` -> balanced,
height 2. At node `3`: leaf, height 1. At node `1`: left height = 2, right height = 1.
Difference `|2 - 1| = 1` -> balanced. So this tree is balanced and the answer is `true`. Now
extend the left branch one more level (add a node `5` under `4`): node `2` would then have left
height 2 and right height 0, difference 2 `> 1` -> unbalanced, so the whole tree becomes
`false`.

We solve it in a single bottom-up pass using
[recursion](../../../docs/10-glossary.md#recursion). Reason it out explicitly (no leaps of
faith): **assume** a helper `checkHeight` already returns the correct height of any smaller
subtree *if that subtree is balanced*, or a special flag `-1` *if it is unbalanced*. Then at
the current node: ask for the left height and the right height; if either came back `-1`,
propagate `-1` up immediately (a child already failed, so the whole tree fails); otherwise, if
`|left - right| > 1`, this node is the imbalance -> return `-1`; else return
`1 + max(left, right)` as this node's height. That assumption is valid because the same rule
applies to each subtree all the way down to an empty subtree -- the
[base case](../../../docs/10-glossary.md#base-case), which has height 0 and is trivially
balanced.

Why this design instead of calling the
[Maximum Depth](../0104-maximum-depth-of-binary-tree/) function on both children of every
node? Because that re-walks each subtree many times -- `O(n^2)`. The `-1` sentinel folds
"compute height" and "detect imbalance" into one `O(n)` pass, and the early `-1` propagation
stops exploring the moment any imbalance is found -- a single-pass
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search) that bails out early.

### Checkpoint A -- Spot the sentinel trick

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** The helper `checkHeight` returns what special value to signal "this subtree is unbalanced"?
- a) `0`
- b) `-1`
- c) `null`

<details><summary>Show answer</summary>

**(b)** -- a real height is always non-negative, so `-1` is reserved as the "bad" flag. The public method just checks whether the final result is `-1`.

</details>

**Q2 (comprehend).** Why is `-1` a safe sentinel that can never be confused with a real height?
- a) Because heights are always non-negative
- b) Because the tree is never deeper than 1
- c) Because Java reserves `-1` for errors

<details><summary>Show answer</summary>

**(a)** -- an empty subtree has height `0` and every other subtree has height `>= 1`, so `-1` can never be a genuine height. It is unambiguously the imbalance flag.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace this right-leaning chain on `isBalanced`:

```
    1
     \
      2
       \
        3
```

What is returned?
- a) `true`
- b) `false`

<details><summary>Show answer</summary>

**(b)** -- `checkHeight(3)` = 1, `checkHeight(2)` = 2 (`|0-1| = 1`, ok), but `checkHeight(1)` sees left=0, right=2, difference `2 > 1`, so it returns `-1`. `isBalanced` returns `false`.

</details>

**Q2 (analyze).** Suppose you remove the early-exit checks (`if (left == -1) return -1;`). On a tree whose left subtree is unbalanced but right is balanced, what can go wrong?
- a) Nothing -- the `|left - right|` comparison still catches every imbalance
- b) The `-1` from the bad child can be treated as a real height, corrupting the comparison and flipping the verdict
- c) The code runs faster but is always wrong

<details><summary>Show answer</summary>

**(b)** -- `-1` would flow into `1 + Math.max(left, right)` and into the difference check as if it were a height. A subtree that is actually unbalanced could then look balanced, so the answer is unreliable.

</details>

**Q3 (transfer).** The naive solution calls `maxDepth` on both children of every node. Why is that `O(n^2)`, and how does the sentinel trick fix it to `O(n)`? Explain in words.

<details><summary>Show answer</summary>

The naive version re-walks each subtree many times -- `maxDepth` visits every node of a subtree, and you call it for every node, so nodes are touched again and again. The sentinel folds "compute height" and "detect imbalance" into one bottom-up pass: each node is visited once and hands its height (or `-1`) to its parent, so the whole tree is traversed a single time.

</details>

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
