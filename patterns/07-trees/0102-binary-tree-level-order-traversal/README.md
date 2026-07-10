# 0102 - Binary Tree Level Order Traversal

**Difficulty:** Medium
**Pattern:** Trees
**LeetCode:** https://leetcode.com/problems/binary-tree-level-order-traversal/

## Concepts used

- **Binary tree** -- a tree where each node has at most two children. [glossary](../../../docs/10-glossary.md#tree)
- **Queue** -- a first-in-first-out container: the earliest item added is the first removed. [glossary](../../../docs/10-glossary.md#queue)
- **BFS (breadth-first)** -- a traversal that visits all nodes at the current depth before going deeper. [glossary](../../../docs/10-glossary.md#bfs-breadth-first-search)
- **Level-order traversal** -- BFS on a tree: visit the root, then all depth-1 nodes, then all depth-2 nodes, and so on. [glossary](../../../docs/10-glossary.md#level-order-traversal)

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

Think of a company org chart. To list everyone "by rank" you would start with the CEO, then
list all the VPs (one rung down), then all the directors, and so on -- finishing each rung
before starting the next. That is **level-order** on a [binary tree](../../../docs/10-glossary.md#tree):
visit the **root**, then every node at depth 1, then every node at depth 2, and so on. It is
[BFS](../../../docs/10-glossary.md#bfs-breadth-first-search) (breadth-first: visit all nodes at
the current depth before going deeper) -- the natural counterpart to the depth-first walks in
the rest of this pattern.

BFS needs a [queue](../../../docs/10-glossary.md#queue) -- a first-in-first-out line, like
shoppers at a checkout. Put the root at the back; then repeatedly take the front node, record
it, and put its children at the back. Because each node's children are added *after* every node
already waiting, the queue naturally drains one rung before it starts on the next. Trace
`[3,9,20,null,null,15,7]`:

```
        3
       / \
      9   20
         /  \
        15   7
```

Start: queue = `[3]`. Take `3` out, record it, put its children `9` and `20` at the back ->
queue = `[9, 20]`. Take `9` out (no children), take `20` out, put `15` and `7` at the back ->
queue = `[15, 7]`. Take `15` out, take `7` out -> queue empty. The plain visit order is
`3, 9, 20, 15, 7` -- but the problem wants the nodes *grouped by level*:
`[[3], [9,20], [15,7]]`.

The flat walk above loses the level boundaries. To keep them, **snapshot the queue size at the
start of each level**: that snapshot is exactly how many nodes belong to the current rung.
Drain exactly that many (recording each one and putting its children at the back), collect them
into one list, append that list to the answer, then repeat with whatever is now in the queue.
The children added during a level wait *behind* the current level's remaining nodes, so they
never bleed into the current group. An empty root is the base case: return an empty list
immediately, before touching the queue.

### Checkpoint A -- Spot the queue trick

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** Which container does BFS level-order use to hold the nodes waiting to be processed?
- a) A stack (last-in-first-out)
- b) A queue (first-in-first-out)
- c) A hash set

<details><summary>Show answer</summary>

**(b)** -- a queue drains the oldest item first, so a whole level is processed before any of its children (added later) are reached. That ordering is what produces level-by-level output.

</details>

**Q2 (comprehend).** Why snapshot `levelSize = queue.size()` at the *start* of each level, rather than just polling until the queue is empty?
- a) It is faster
- b) Children enqueued during the level would otherwise bleed into the current level's group, destroying the level boundaries
- c) Because calling `queue.size()` inside the loop is forbidden

<details><summary>Show answer</summary>

**(b)** -- as we poll a node we push its children to the back. If we kept polling until empty, those children would be processed in the same pass and every level would collapse into one big list.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace this tree:

```
        1
       / \
      2   3
     / \
    4   5
```

What is the level-order output?
- a) `[[1,2,3,4,5]]`
- b) `[[1],[2,3],[4,5]]`
- c) `[[1],[2],[3],[4],[5]]`

<details><summary>Show answer</summary>

**(b)** -- level 0 is `[1]`, level 1 is `[2,3]`, level 2 is `[4,5]`. The queue drains each level before its children are processed.

</details>

**Q2 (analyze).** What happens if you enqueue a `null` child (no null guard) into the `ArrayDeque`?
- a) Nothing -- nulls are silently ignored
- b) `ArrayDeque.offer(null)` throws `NullPointerException`
- c) The traversal produces an extra empty level

<details><summary>Show answer</summary>

**(b)** -- unlike `LinkedList`, `ArrayDeque` refuses `null` elements. Guarding each child with `if (node.left != null)` before offering prevents the crash.

</details>

**Q3 (transfer).** How would you produce a BOTTOM-UP level order (last level first, root last)? Sketch the change in words.

<details><summary>Show answer</summary>

Run exactly the same BFS, but prepend each level's list to the front of the result (or collect normally and reverse the result list at the end). The traversal logic is unchanged; only the insertion order of the finished level-lists differs.

</details>

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
