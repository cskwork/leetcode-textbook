# 0543 - Diameter of Binary Tree

**Difficulty:** Easy
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/diameter-of-binary-tree/

## Concepts used

- **Binary tree** -- a tree where each node has at most two children. [glossary](../../../docs/10-glossary.md#tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem. [glossary](../../../docs/10-glossary.md#recursion)
- **Base case** -- the simplest input a recursive function answers without recursing further. [glossary](../../../docs/10-glossary.md#base-case)

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

Picture the tree as a road map: each line between a parent and child is one road segment. The
**diameter** is the longest single drive between any two houses, counted in road segments --
and that drive is a simple path with no backtracking. Any such path has exactly one
**highest** node (its topmost point), where the path stops climbing one branch and starts
descending the other. So the strategy is: *for every node*, look at the arch that passes
through it -- going down through its deepest left descendant and down through its deepest
right descendant -- and keep the longest arch seen anywhere in the tree. That longest arch is
the diameter.

Trace `[1,2,3,4,5]`:

```
        1
       / \
      2   3
     / \
    4   5
```

The arch through node `1` goes `4 -> 2 -> 1 -> 3` -- three edges. The arch through node `2`
goes `4 -> 2 -> 5` -- two edges. Arches through the leaves `3`, `4`, `5` are zero edges. The
longest is 3, so the diameter is `3`. Note the answer does *not* have to pass through the
root in general -- here it happens to, but in a left-heavy tree the widest arch may sit
entirely inside the left subtree, which is why we check every node, not just the root.

We use [recursion](../../../docs/10-glossary.md#recursion) with one twist: the helper returns
its **height** to its parent (so the parent can build its own arch), but it also **updates a
shared answer** with the best arch it sees. Reason it out explicitly (no leaps of faith):
**assume** `height` already returns the correct height of any smaller subtree. At the current
node: get `left = height(left child)` and `right = height(right child)`; the arch through this
node is `left + right`, so update the running answer with `max(answer, left + right)`; then
return `1 + max(left, right)` as this node's own height. That assumption is valid because the
same logic applies to each subtree all the way down to an empty subtree -- the
[base case](../../../docs/10-glossary.md#base-case), which has height 0.

One counting detail: the helper measures height in *nodes* (a
[leaf](../../../docs/10-glossary.md#tree) returns 1, an empty subtree returns 0). A pleasant
consequence is that `height(left child) + height(right child)` at a node comes out as the
number of *edges* in the arch through that node -- because the node-count of a child subtree
equals the number of edges from the parent down to that subtree's deepest leaf. So no extra
`+1` is needed for the arch, and the answer lands in edges as LeetCode requires. (This
"return height to the parent, track a separate answer on the side" structure is shared with
[Balanced Binary Tree](../0110-balanced-binary-tree/); both are single-pass
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search).)

### Checkpoint A -- Spot the arch measurement

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** The diameter is measured in which unit?
- a) Number of nodes
- b) Number of edges
- c) Number of leaves

<details><summary>Show answer</summary>

**(b)** -- LeetCode counts the longest path in *edges*. Because the helper measures height so that a leaf is `1` and `null` is `0`, `height(left) + height(right)` already comes out in edges with no extra `+1`.

</details>

**Q2 (comprehend).** The helper returns HEIGHT to its parent but also updates a separate `best` field. Why two different things -- why not just return the diameter up the recursion?
- a) Because Java methods can only return one value
- b) Because the parent needs the height to build its own arch; the diameter through a node cannot help build any arch above it
- c) Because the diameter is always larger than the height

<details><summary>Show answer</summary>

**(b)** -- the parent's arch is `height(left) + height(right)`, so it needs heights from its children, not diameters. The best arch seen anywhere is a side answer tracked separately.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace this left-leaning chain:

```
    1
   /
  2
 /
3
```

What diameter does the method return?
- a) `1`
- b) `2`
- c) `3`

<details><summary>Show answer</summary>

**(b)** -- the only path is `3 -> 2 -> 1`, which is two edges. At node `1` the arch is `height(left=2) + height(right=0) = 2`; that is the widest arch in the tree, so `best = 2`.

</details>

**Q2 (analyze).** The public method resets `best = 0` at the start. Why does this matter specifically on LeetCode?
- a) Because LeetCode reuses the same `Solution` object across test cases; without reset, the previous answer leaks into the next one
- b) Because `best` might start negative
- c) Because recursion corrupts the field

<details><summary>Show answer</summary>

**(a)** -- `best` is a field on the object, and LeetCode calls the method repeatedly on the same instance. Resetting guarantees each run starts from zero.

</details>

**Q3 (transfer).** Suppose you must also RETURN one example path that achieves the diameter, not just its length. How would you extend the approach, in words?

<details><summary>Show answer</summary>

Track which node produced the best arch (`left + right`). After the traversal, from that node walk down through `left` children to the deepest leaf, and walk down through `right` children to the deepest leaf; join the two walks to rebuild the actual path.

</details>

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
