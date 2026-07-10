# 0212 - Word Search II

**Difficulty:** Hard
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/word-search-ii/

## Concepts used

- **Trie (prefix tree)** -- a tree whose edges are letters; built from the word list so dead
  prefixes can be pruned during the search.
  [glossary](../../../docs/10-glossary.md#trie-prefix-tree)
- **Backtracking** -- a search that tries a choice, recurses, then undoes the choice before trying
  the next; here, stepping onto a board cell and restoring it on the way out.
  [glossary](../../../docs/10-glossary.md#backtracking)
- **DFS (Depth-First Search)** -- going as deep as possible down one branch of a structure before
  backing up. [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)
- **Graph** -- a set of nodes joined by edges; the board is a graph where each cell is connected
  to its up/down/left/right neighbors.
  [glossary](../../../docs/10-glossary.md#graph)

## Problem

Given an `m x n` grid of characters `board` and a list of strings `words`, return every word from
`words` that can be formed by a path on the board. A path:

- moves to horizontally or vertically adjacent cells (no diagonals),
- never reuses the same cell twice in one word,
- spells the word by concatenating the letters on the cells it visits, in order.

Signature:

    List<String> findWords(char[][] board, String[] words)

Example 1:

    Input:  board = [["o","a","a","n"],
                     ["e","t","a","e"],
                     ["i","h","k","r"],
                     ["i","f","l","v"]],
            words = ["oath","pea","eat","rain"]
    Output: ["eat","oath"]        # order does not matter

Example 2:

    Input:  board = [["a","b"],
                     ["c","d"]],
            words = ["abcb"]
    Output: []                    # "abcb" needs to reuse the 'b'

## Intuition

You have played Boggle: lettered dice in a grid, and you score by connecting neighboring dice
(up, down, left, right -- no diagonals, no reusing a die in one word) to spell a word from a list.
The board here is exactly a Boggle grid, and the question is "which words from the list can you
spell?" The trap is that the list may have hundreds of words and the grid dozens of cells, so
checking each word one by one, or exploring every string the board can form, both blow up.

The key idea is to let the **dictionary guide the exploration**. Build a
[trie](../../../docs/10-glossary.md#trie-prefix-tree) (a [tree](../../../docs/10-glossary.md#tree)
whose edges are letters) holding every word in the list. Then start a
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search) (depth-first search -- go as deep as
possible down one branch before backing up) from each grid cell. As you walk from cell to cell
spelling out a path, you walk the *same* path down the trie. The moment the next cell's letter is
not a child of your current trie node, you stop: no dictionary word continues this way, so there
is nothing to find down this branch. That prune is the entire reason this is fast. Whenever you
step onto a trie node flagged end-of-word, you record the word.

Trace a tiny board and list. Board:

    o a
    e t

List: `["oat", "eat"]`. The trie has two paths: `o -> a -> t` and `e -> a -> t`. Start DFS at the
top-left cell `o`. From the root, is `o` a child? Yes -- descend, and mark this cell used. (The
mark is the [backtracking](../../../docs/10-glossary.md#backtracking) trick: temporarily overwrite
the cell so you can't step on it again, then restore it when you leave. Backtracking is a search
that tries a choice, recurses, and undoes the choice before trying the next.) Move to a neighbor:
right to `a`. Is `a` a child of the `o` node? Yes -- descend; `a` is not end-of-word, keep going.
From `a` the only unused neighbor whose letter is a trie child is down to `t`; `t` is end-of-word,
so record `"oat"`. No children remain under `t`, so backtrack, restoring each cell on the way out.
Now try the other start cells: starting at `e` traces `e -> a -> t` and records `"eat"`; starting
at `a` dies immediately because no word begins with `a`. Result: `["oat", "eat"]`.

Two refinements matter. First, store the full word *on* its end-of-word node, so when you hit one
you know exactly which word you found without rebuilding it from the path. Second, after collecting
a word, clear its end-of-word marker: a word can be reachable along several board paths (or the
list may contain it twice), and clearing guarantees you report it once while also trimming future
search.

Why a trie and not a `HashSet` of words? A hash set answers only "is this exact string a word?" --
it cannot tell you "is this string the *start* of any word?", so it cannot prune. With a hash set
you would explore the full 4-way board to depth L and only then look up each formed string; the
trie kills dead prefixes after one or two letters. The board is naturally a
[graph](../../../docs/10-glossary.md#graph) (each cell is a node joined to its up/down/left/right
neighbors by edges), which is why DFS with a visited marker -- i.e. backtracking -- is the right
traversal. This is the same "every child is a branch" idea as LC 211's `.` wildcard, but here the
board supplies the branches and the trie supplies the prune.

### Checkpoint A -- Why the trie guides the search

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** Why build a trie of the word list instead of a HashSet of words?
- a) A HashSet cannot store strings
- b) The trie lets the DFS stop the moment the current path is not the start of any word
- c) A trie always uses less memory than a set

<details><summary>Show answer</summary>

**(b)** -- a HashSet answers only "is this exact string a word?" and cannot prune a half-built path. The trie prunes dead prefixes after one or two letters, which is the whole reason this is fast.

</details>

**Q2 (comprehend).** Why is the visited cell restored (backtracked) before trying the next direction?
- a) To keep memory usage low
- b) So sibling paths and later starting cells can use that cell again
- c) To sort the results alphabetically

<details><summary>Show answer</summary>

**(b)** -- the `'#'` marker only blocks reuse within the current word. Restoring the original letter leaves the board clean for every other path that passes through the cell.

</details>

## Pseudocode

    structure Node:
        children : map from character -> child Node
        word     : the full word, or empty if no word ends here

    operation findWords(board, words):
        build a trie by inserting every word, storing the full word at its end node
        results <- empty list

        for each cell (r, c) on the board:
            depthFirstSearch(board, r, c, trie.root, results)

        return results

    function depthFirstSearch(board, r, c, node, results):
        # bounds + visited check
        if (r, c) is off the board OR board[r][c] is already visited:
            return

        letter <- board[r][c]
        if letter is not a key in node.children:
            return                          # dead end: this path is not a dictionary prefix

        next <- node.children[letter]

        # collect any word that ends here
        if next.word is not empty:
            add next.word to results
            set next.word to empty          # avoid reporting the same word again

        mark board[r][c] as visited         # e.g. temporarily overwrite with '#'
        for each of the 4 neighbours (nr, nc):
            depthFirstSearch(board, nr, nc, next, results)
        restore board[r][c]                 # BACKTRACK: undo the visited marker

The visited marker is a single in-place character swap (e.g. to `#`) restored on the way out --
no separate `visited` matrix is needed.

## Java Solution

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    String word;
}

class Solution {
    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
            }
            node.word = w;
        }

        List<String> found = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                dfs(board, r, c, root, found);
            }
        }
        return found;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node, List<String> found) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) {
            return;
        }
        char letter = board[r][c];
        if (letter == '#' || !node.children.containsKey(letter)) {
            return;
        }

        TrieNode next = node.children.get(letter);
        if (next.word != null) {
            found.add(next.word);
            next.word = null; // report each dictionary word once
        }

        board[r][c] = '#'; // mark visited in place
        dfs(board, r - 1, c, next, found);
        dfs(board, r + 1, c, next, found);
        dfs(board, r, c - 1, next, found);
        dfs(board, r, c + 1, next, found);
        board[r][c] = letter; // backtrack
    }
}
```

The trie is built once with `computeIfAbsent` so each character either descends into an existing
child or creates a new node lazily; the full word is stashed on the end node. The DFS uses an
in-place `'#'` sentinel to mark the current cell visited, then restores the original character on
the way out -- this is the backtracking undo step that lets each starting cell explore freely
without a separate visited matrix. The line `next.word = null` after collecting is what prevents
duplicate reports: once a word has been emitted, clearing the marker means future paths reaching
the same node find nothing to record. We use `Map<Character, TrieNode>` (not `children[26]`)
because the board can contain any lowercase letter, the wildcard sentinel `'#'` is not in `'a'..'z'`,
and a map keeps the per-node footprint proportional to actual branching.

## Complexity

    Time:  O(m * n * 4 * 3^(L-1)) worst case, where L is the length of the longest word.
           From each cell we have 4 first moves, then at most 3 (we never go back onto the cell we
           came from). The trie prunes aggressively: most branches die early because the path stops
           being a dictionary prefix. Building the trie is O(sum of word lengths).
    Space: O(sum of word lengths) for the trie, plus O(L) recursion stack per DFS path.

## Dry-Run

Board and words from Example 1:

```
board:  o a a n
        e t a e
        i h k r
        i f l v

words:  oath, pea, eat, rain
```

Trie (showing only paths that exist):

```
root
 |-- o -> a -> t -> h(*)   "oath"
 |-- p -> e -> a(*)        "pea"
 |-- e -> a -> t(*)        "eat"
 |-- r -> a -> i -> n(*)   "rain"
```

Start DFS at cell (0,0) = 'o':

| step | (r,c) | letter | next trie node | action                              |
|-----:|-------|--------|----------------|-------------------------------------|
| 1    | (0,0) | 'o'    | root.o         | descend; not a word; mark (0,0) '#' |
| 2    | (1,0) | 'e'    | root.o.e?      | no such child; return               |
| 3    | (0,1) | 'a'    | root.o.a       | descend; not a word; mark (0,1) '#' |
| 4    | (1,1) | 't'    | root.o.a.t     | descend; not a word; mark (1,1) '#' |
| 5    | (2,1) | 'h'    | root.o.a.t.h   | IS a word -> record "oath", clear   |

Path traced: `(0,0) o -> (0,1) a -> (1,1) t -> (2,1) h` spells `"oath"`. After recording, the DFS
keeps exploring the neighbourhood but `o.a.t.h` has no children, so it returns. Backtracking
restores each cell's letter.

Continuing the outer scan, cell (1,3) = 'e' eventually traces `e -> a -> t` via (1,3) -> (2,2) ->
(2,3)? Let's see: (1,3)='e' lands at `root.e`; the DFS tries neighbours. The path that finds
"eat" is `(1,3) e -> (2,3) ... ` -- no, (2,3)='r'. Actually "eat" is found by another starting
'e' or by walking e->a->t elsewhere on the board. Whatever the exact path, the trie guarantees
that as soon as a cell's letter is not a prefix of any word, the DFS returns immediately -- e.g.
starting at (0,1)='a' dies instantly because no word starts with 'a'.

Final output: `["oath", "eat"]` (order may vary).

For Example 2 ("abcb" on a 2x2 board): the DFS finds 'a','b','c','b' but the second 'b' requires
revisiting a cell already on the path -- the `'#'` sentinel blocks it, so "abcb" is never recorded.
Output: `[]`.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Board `[["a","b"],["c","a"]]`, words `["aba"]`. What does `findWords` return?
- a) `["aba"]`
- b) `[]`
- c) `["aba", "aba"]` (duplicated)

<details><summary>Show answer</summary>

**(a)** -- the path `(0,0) a -> (0,1) b -> (1,1) a` spells "aba" without reusing a cell. The marker is restored on the way out, but no second path reaches the word, so it is reported once.

</details>

**Q2 (analyze).** After recording a found word, the code sets `next.word = null`. Why?
- a) To free memory
- b) So the same word is never reported twice, even if a second board path reaches it
- c) To mark the node as a leaf with no children

<details><summary>Show answer</summary>

**(b)** -- clearing the marker both de-duplicates the output and prunes future search; a later path that arrives at this node finds nothing left to record.

</details>

**Q3 (transfer).** If a word could appear twice in `words` and you had to report it once per occurrence, how would you change the approach?

<details><summary>Show answer</summary>

Stop clearing `word` after collecting it, and instead count how many times each word appears in the input list; emit it up to that count when its node is reached. The trie skeleton is unchanged -- only the bookkeeping of multiplicities differs.

</details>

## Common mistakes

- Revisiting cells. Without a visited marker the DFS walks in circles and may even loop forever.
  Use the in-place `'#'` trick or a separate `boolean[][] visited`, and always undo it on
  backtrack.
- Forgetting to restore the board cell after the recursive calls. The backtrack step
  (`board[r][c] = letter`) is mandatory; without it later starting points see a corrupted board.
- Reporting the same word twice. Either clear `node.word` after collecting, or use a `Set` for
  results -- but clearing is cheaper and also prunes future search.
- Recursing before checking that the cell's letter is a child of the current trie node. Without
  the trie check the search explodes into all 4^L paths.
- Re-creating the trie per starting cell, or scanning the word list per cell -- both erase the
  win. The trie is built once.
- Not handling empty `words` or empty `board`. Both should return an empty list with no work.

## Related problems

- [0208 - Implement Trie (Prefix Tree)](../0208-implement-trie-prefix-tree/) -- the trie itself;
  master insert/search before tackling this.
- [0211 - Design Add and Search Words Data Structure](../0211-design-add-and-search-words-data-structure/)
  -- another trie + DFS; the wildcard there is the same idea as "try any neighbour" here.
- [0720 - Longest Word in Dictionary](../0720-longest-word-in-dictionary/) -- same "trie + DFS"
  skeleton, but the search space is the trie itself rather than a board.
