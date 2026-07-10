# 0100 - Same Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/same-tree/

## Concepts used

- **Binary tree** -- a tree where each node has at most two children. [glossary](../../../docs/10-glossary.md#tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Base case** -- the simplest input a recursive function answers without recursing further. [glossary](../../../docs/10-glossary.md#base-case)

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

To check whether two printed family trees are identical, you would put a finger on each root
and walk both at the same time: at every pair of people you meet, they must have the same
name, the same number of children, and the children must match pairwise. Two
[binary trees](../../../docs/10-glossary.md#tree) are *the same* when this holds at every
corresponding pair of **nodes** -- structurally identical, with equal values.

Trace `p = [1,2,3]`, `q = [1,2,3]`:

```
      p              q
      1              1
     / \            / \
    2   3          2   3
```

Both roots are `1`. Compare the left pair `(2, 2)` -- both are leaves, match. Compare the
right pair `(3, 3)` -- both leaves, match. Every pair matched, so the answer is `true`. Now a
mismatch: `p = [1,2]`, `q = [1,null,2]`. At the root both are `1`, but the left pair is
`(2, null)` -- one exists, one does not -- a shape mismatch, so the answer is `false`.

We compare pair by pair using [recursion](../../../docs/10-glossary.md#recursion) -- a
function that calls itself on a smaller input. Reason it out explicitly (no leaps of faith):
**assume** `isSameTree` already correctly decides whether any smaller pair of subtrees is the
same. At the current pair, three ordered checks cover every possible situation: (1) **both
empty** -- equal so far, return `true`; (2) **exactly one empty** -- shapes differ, return
`false`; (3) **both present** -- the values must be equal, *and* the left subtrees must be the
same, *and* the right subtrees must be the same. That assumption is valid because those same
three checks apply to each child pair all the way down, until a pair is `(null, null)` -- the
[base case](../../../docs/10-glossary.md#base-case), which returns `true`. Walking both trees
in lockstep this way is a twin [DFS](../../../docs/10-glossary.md#dfs-depth-first-search)
(depth-first: go as deep as possible before backtracking); `&&` short-circuits, so the first
mismatch anywhere unwinds the whole answer to `false`.

### Checkpoint A -- Spot the three-case check

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** When both `p` and `q` are `null`, what should `isSameTree` return?
- a) `false`
- b) `true`
- c) throw an error

<details><summary>Show answer</summary>

**(b)** -- two empty subtrees are structurally identical, so the pair matches and we return `true`. This is the base case.

</details>

**Q2 (comprehend).** Why must the value comparison (`p.val != q.val`) come *after* the two null checks?
- a) It runs faster that way
- b) Reading `.val` on a `null` reference throws `NullPointerException`
- c) Order does not matter; both orders give the same result

<details><summary>Show answer</summary>

**(b)** -- once we know neither pointer is `null`, dereferencing `.val` is safe. Doing it before the guards would crash on any one-sided pair.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Let `p = [1,2,3]` (root `1`, children `2`,`3`) and `q = [1,3,2]` (root `1`, children `3`,`2`). What does `isSameTree` return?
- a) `true` -- both have the same values
- b) `false` -- the left children differ (`2` vs `3`)

<details><summary>Show answer</summary>

**(b)** -- at the root both are `1`, but `isSameTree(p.left=2, q.left=3)` sees `2 != 3` and returns `false`, which short-circuits the whole answer. Order matters, not just the set of values.

</details>

**Q2 (analyze).** Suppose you wrote only ONE null check: `if (p == null || q == null) return false;`. Which previously-valid case now wrongly fails?
- a) Two identical non-empty trees
- b) Two empty subtrees (`p == null && q == null`), which should return `true`
- c) A value mismatch at the root

<details><summary>Show answer</summary>

**(b)** -- the single `||` check can't tell "both empty" from "only one empty", so it returns `false` for two `null` subtrees, breaking every leaf's children.

</details>

**Q3 (transfer).** How would you adapt this to check whether tree `s` *contains* tree `t` as an exact subtree (some node of `s` roots a subtree identical to `t`)? In words.

<details><summary>Show answer</summary>

Walk `s`; at each node run the same `isSameTree` twin DFS comparing that node's subtree against `t`. If any check returns `true`, the answer is `true`. The only new idea is applying the existing pair-check at every node of `s` rather than just once at the roots.

</details>

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
