# Pattern 7 - Trees

## What this pattern is

A tree is a recursive data structure: every node is the root of a smaller tree. That self-similar
shape is the whole secret of this pattern -- almost every tree problem reduces to "solve the same
problem on the left subtree, solve it on the right subtree, then combine the two answers at the
root." Once you internalise that single move, the 9 problems in this folder become variations on
a theme rather than 9 separate algorithms.

The node convention used throughout this book (see `03-java-crash-course.md` section 8):

    class TreeNode {
        int val;
        TreeNode left, right;
    }

A `null` reference represents an empty subtree. The single most important base case in the entire
pattern is `if (node == null) return <base>;` -- forgetting it is the number-one source of
`NullPointerException` crashes in tree problems.

## When to apply it (trigger signals)

Scan the problem statement for any of these phrases and reach for the Trees pattern:

| Trigger signal                                | Likely technique           |
|-----------------------------------------------|----------------------------|
| "binary tree", "given root", "subtree"        | DFS recursion              |
| "depth", "height", "maximum depth"            | postorder DFS              |
| "is balanced", "diameter"                     | postorder DFS + accumulator |
| "invert", "mirror", "symmetric"               | preorder / postorder swap  |
| "same tree", "subtree of another"             | twin DFS over two trees    |
| "validate BST", "is it a BST"                 | DFS with min/max bounds    |
| "lowest common ancestor"                      | DFS, return found node up  |
| "level order", "by level", "zig-zag", "right view" | BFS with a queue      |
| "kth smallest", "inorder", "sorted in a BST"  | inorder traversal          |

Two words in particular are dead giveaways: **BST** (binary *search* tree -- use the ordering
property) and **level** (use BFS, never DFS).

## The two core techniques

Every tree problem is solved with one of two traversal strategies. Pick the strategy first, then
worry about what to do at each node.

### Technique A -- DFS recursion (the workhorse)

Walk to the bottom first, combine answers on the way back up. Three flavours differ only in *when*
you visit the current node:

- **Preorder** -- visit node, then left, then right. Use when the parent must be processed before
  its children (invert, copy, root-first answers).
- **Inorder** -- left, node, right. On a BST this visits values in sorted order; use for kth
  smallest, validate-bst, "next in sorted order".
- **Postorder** -- left, right, node. Use when you need full information from both children before
  answering for the parent (depth, diameter, balanced).

Pseudocode template (postorder shown; swap the order of the three lines for the other flavours):

    function dfs(node):
        if node is null:
            return BASE                 # depth 0, true, etc.
        leftAnswer  = dfs(node.left)    # trust the recursion
        rightAnswer = dfs(node.right)
        return COMBINE(node, leftAnswer, rightAnswer)

The recursion space is the height of the tree: `O(h)`. On a balanced tree `h = log n`; on a
degenerate (linked-list-shaped) tree `h = n`.

### Technique B -- BFS level-order (the queue)

Visit nodes row by row, top to bottom, left to right. Push the root into a queue; repeatedly pull
a node out, process it, and push its children in. To capture "one level at a time", snapshot the
queue size at the start of each level and drain exactly that many nodes before moving on.

Pseudocode template:

    function levelOrder(root):
        if root is null: return empty result
        queue = new queue, push root
        result = empty list
        while queue is not empty:
            levelSize = current size of queue
            levelValues = empty list
            repeat levelSize times:
                node = queue.poll()
                add node.val to levelValues
                if node.left  is not null: push node.left
                if node.right is not null: push node.right
            add levelValues to result
        return result

Each node enters and leaves the queue exactly once, so time is `O(n)` and queue space is `O(w)`
where `w` is the maximum row width.

## The "trust the recursion" mental model

Beginners freeze on tree recursion because they try to trace every level down to the leaves and
back up. Don't. Use this contract instead:

> Assume the recursive call already returns the correct answer for any *smaller* subtree. Now
> write the one line that combines those two correct answers into the answer for the current node.

Concretely, for Maximum Depth:

- I trust that `dfs(left)` returns the true depth of the left subtree.
- I trust that `dfs(right)` returns the true depth of the right subtree.
- Therefore the depth at *this* node is `1 + max(leftDepth, rightDepth)`. Done.

You do not need to know *how* it got the answer -- only that it did, because the same function is
applied to a strictly smaller input. This is also called the **leap of faith**, and it is the only
way to stay sane on problems like Diameter where the recursive return value and the global answer
are different things.

## The 9 problems in this pattern

| #    | Problem                                   | Difficulty | Teaser                                              |
|-----:|-------------------------------------------|------------|-----------------------------------------------------|
| 0226 | [Invert Binary Tree](./0226-invert-binary-tree/)                 | Easy   | Swap every node's children, recursively.            |
| 0104 | [Maximum Depth of Binary Tree](./0104-maximum-depth-of-binary-tree/) | Easy   | Deepest path length: `1 + max(left, right)`.       |
| 0100 | [Same Tree](./0100-same-tree/)                                   | Easy   | Twin DFS: both null, or same value + same subtrees. |
| 0110 | [Balanced Binary Tree](./0110-balanced-binary-tree/)             | Easy   | Return height, or a `-1` sentinel when unbalanced.  |
| 0543 | [Diameter of Binary Tree](./0543-diameter-of-binary-tree/)       | Easy   | Track the widest left+right path during one DFS.    |
| 0102 | [Binary Tree Level Order Traversal](./0102-binary-tree-level-order-traversal/) | Medium | BFS row by row, snapshot level size each pass.      |
| 0235 | [Lowest Common Ancestor of a BST](./0235-lowest-common-ancestor-of-a-bst/) | Medium | Walk down using the BST ordering property.          |
| 0098 | [Validate Binary Search Tree](./0098-validate-binary-search-tree/) | Medium | Narrow an open interval (min, max) down each branch. |
| 0230 | [Kth Smallest Element in a BST](./0230-kth-smallest-element-in-a-bst/) | Medium | Inorder traversal yields sorted order; stop at kth. |

Read them roughly in order: the first six build the recursive reflex, and the last three show how
the BST ordering property turns harder checks into simple comparisons.

## Common pitfalls

- **Confusing BST with ordinary BT.** A binary tree has *no* ordering guarantee; a binary *search*
  tree requires left subtree values < node < right subtree values. Problems 235, 230, 98 only work
  because of that ordering -- do not apply BST logic to a plain BT problem, and do not assume a BT
  is sorted.
- **Forgetting the `null` base case.** Every recursive helper must handle `node == null` first.
  Skipping it means dereferencing a `null` child and crashing with `NullPointerException`.
- **Integer overflow on Validate BST (LC 98).** The naive `node.val > min && node.val < max` with
  `min = Integer.MIN_VALUE` wrongly rejects a node whose value *equals* `Integer.MIN_VALUE` (a legal
  value) and also breaks when a node equals the bound. Use `Long` bounds initialised to
  `Long.MIN_VALUE` / `Long.MAX_VALUE` so every real `int` fits strictly inside.
- **Mutating shared/global state inside recursion.** When a problem tracks a running maximum
  (Diameter) or a counter (Kth Smallest), store it in a field or a single-element array and update
  it at exactly the right moment -- not as the return value. The recursive return value is for the
  *parent*, the global is for the *answer*.
- **Returning the wrong quantity.** In Balanced and Diameter the helper returns *height* (used by
  the parent) while the real answer is a *boolean* or *diameter* (stored separately). Beginners
  often return the answer up the chain and lose the height the parent needs.
- **Using BFS where level boundaries are not snapshotted.** If you `poll()` until the queue is empty
  without first recording the level size, all nodes collapse into one big list and "by level" is
  lost. Always grab `int levelSize = queue.size()` at the top of each outer loop iteration.
- **Trusting `==` on boxed `Integer`.** When collecting values into `List<Integer>`, comparing two
  boxed integers with `==` breaks outside the cached range `[-128, 127]`. Use `.equals` or compare
  the unboxed `int` values.
- **Deep recursion on skewed trees.** A degenerate tree of `n` nodes has height `n`; recursion then
  uses `O(n)` stack space and can `StackOverflowError` on very large inputs. Iterative traversal
  (an explicit stack or Morris threading) is the escape hatch, but is rarely required on LeetCode.

---

Next problem: [0226 - Invert Binary Tree](./0226-invert-binary-tree/).
