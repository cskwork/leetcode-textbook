# 0212 - Word Search II

**Difficulty:** Hard
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/word-search-ii/

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

Two naive approaches both fail:

1. **For each word, DFS the board.** With W words and a board of mn cells, that is W separate
   DFS searches costing O(W * mn * 4^L) -- redoing the same board work W times.
2. **For each cell, DFS every possible string and check it against a HashSet of words.** Branching
   factor 4 and depth L gives 4^L strings per starting cell; almost all of them are useless
   prefixes no dictionary word begins with.

The right move is the second approach, but **pruned by the dictionary**. We build a trie of all
`words`, then DFS from each cell. As we walk, we follow edges in the trie: the moment the current
path is no longer a prefix of any dictionary word, we abandon it -- no wasted exploration. Every
time we land on a trie node marked end-of-word, we record the word. This is the canonical
"Trie + Backtracking" pattern: the trie prunes the search, backtracking explores the board.

Two refinements that matter:

- **Store the word at its end-of-word node** so when we hit a `isEnd` node we know *which* word we
  found, without rebuilding it from the path.
- **Unmark `isEnd` after collecting.** A word can be reached from many board paths (or the
  dictionary may contain duplicates); clearing the flag on collection guarantees each word is
  reported exactly once, and also prunes the search a little.

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
