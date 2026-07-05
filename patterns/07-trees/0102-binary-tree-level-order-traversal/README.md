# 0102 - Binary Tree Level Order Traversal

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/binary-tree-level-order-traversal/

## Problem

Given the `root` of a binary tree, return the *level-order* traversal of its nodes' values --
grouped level by level, from left to right.

Signature:

    List<List<Integer>> levelOrder(TreeNode root)

Examples:

    Input:  root = [3,9,20,null,null,15,7]
    Output: [[3],[9,20],[15,7]]

    Input:  root = [1]
    Output: [[1]]

    Input:  root = []
    Output: []

## Intuition

The trigger "level / by level / row" demands breadth-first search -- recursion cannot easily keep
levels separate because a recursive stack mixes depths. The standard BFS skeleton (push root,
poll a node, push its children) visits nodes in level order but flattens them into a single list.
To keep the grouping, *snapshot the queue size at the top of each pass*: that snapshot is exactly
how many nodes belong to the current level, so drain that many of them (collecting values and
enqueuing their children) before starting the next level.

## Pseudocode

    function levelOrder(root):
        result = empty list
        if root is null:
            return result
        queue = new queue; queue.push(root)
        while queue is not empty:
            levelSize = current size of queue       # snapshot before we add children
            level = empty list
            repeat levelSize times:
                node = queue.poll()
                add node.value to level
                if node.left  is not null: queue.push(node.left)
                if node.right is not null: queue.push(node.right)
            add level to result
        return result

The `levelSize` snapshot is the whole trick -- it separates "nodes already queued from previous
levels" from "children queued during this level".

## Java Solution

```java
class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>(levelSize);
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            result.add(level);
        }
        return result;
    }
}
```

`ArrayDeque` is the book's preferred queue (`03-java-crash-course.md` section 4) -- faster than
`LinkedList` and refusing `null` is fine because we only ever enqueue real children. The
`for (int i = 0; i < levelSize; i++)` loop captures `queue.size()` exactly once at the top, so the
children we enqueue during the loop do not bleed into the current level. Sizing each inner
`ArrayList` with `levelSize` avoids a resize.

## Complexity

    Time:  O(n)   -- every node is enqueued and dequeued exactly once
    Space: O(w)   -- queue holds at most one level; w = max row width (n/2 in the worst case)

## Dry-Run

Tree `[3,9,20,null,null,15,7]`:

```
        3
       / \
      9   20
         /  \
        15   7
```

| Outer iteration | queue (start)    | levelSize | polled values | queue (end)        | result so far        |
|-----------------|------------------|----------:|---------------|--------------------|----------------------|
| 1               | [3]              | 1         | 3             | [9, 20]            | [[3]]                |
| 2               | [9, 20]          | 2         | 9, 20         | [15, 7]            | [[3],[9,20]]         |
| 3               | [15, 7]          | 2         | 15, 7         | []                 | [[3],[9,20],[15,7]]  |

Output: `[[3],[9,20],[15,7]]`.

## Common mistakes

- Polling until the queue is empty without snapshotting `levelSize` -- all nodes collapse into one
  flat list and the level grouping is lost.
- Capturing `queue.size()` inside the loop (it grows as children are enqueued), turning the
  `for` into an infinite loop. Read size once, before the loop.
- Enqueueing `null` children and crashing inside `ArrayDeque.offer(null)` -- guard with
  `if (node.left != null)` first.
- Forgetting the empty-root case -- the guard at the top returns an empty result instead of
  dereferencing `null`.

## Related problems

- [0104 - Maximum Depth of Binary Tree](../0104-maximum-depth-of-binary-tree/) - level-order is
  an alternative DFS-vs-BFS solution: count the rows.
- [0226 - Invert Binary Tree](../0226-invert-binary-tree/) - the BFS counterpart to the DFS
  warm-up; same queue scaffolding, mutation instead of grouping.
- [0230 - Kth Smallest Element in a BST](../0230-kth-smallest-element-in-a-bst/) - the other
  fundamental traversal (inorder DFS) in this pattern.
