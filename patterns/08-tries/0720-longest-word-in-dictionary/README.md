# 0720 - Longest Word in Dictionary

**Difficulty:** Easy
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/longest-word-in-dictionary/

## Problem

Given a list of strings `words`, return the longest word that can be built one character at a time
by other words in the list. Concretely, the answer `w` must satisfy: every prefix of `w`
(including `w` itself) is in `words`. If multiple words tie on length, return the one that is
lexicographically smallest. You may assume a single-character word is always valid (its only
prefix is itself).

Signature:

    String longestWord(String[] words)

Example 1:

    Input:  words = ["w","wo","wor","worl","world"]
    Output: "world"
    # Every prefix of "world" is in the list: "w","wo","wor","worl","world".

Example 2:

    Input:  words = ["a","banana","app","appl","ap","apply","apple"]
    Output: "apple"
    # Both "apple" and "apply" have length 5 and all prefixes valid; "apple" < "apply".

## Intuition

A word qualifies when **every prefix** is also a word -- and that is precisely the question a trie
answers cheaply. We insert every word into a trie, marking end-of-word nodes; then we walk the
trie from the root, descending only through `isEnd` nodes (because each step must extend a valid
prefix). The longest such walk ends at our answer. This is the Tries section, so we use the trie
approach even though a sort + HashSet would also solve it -- the trie teaches the "is every prefix
a word?" query that comes back in autocomplete and dictionary problems. The trigger signal is
"shared prefix" combined with "every prefix must be valid".

Lexicographic tie-breaking comes for free: if we visit children in alphabetical order during the
DFS, the first word of any given length that we discover is already the lex-smallest one -- so we
only need to overwrite our best answer when a strictly longer word is found.

## Pseudocode

    structure Node:
        children : array of 26 slots
        isEnd    : boolean
        word     : the full word ending here (for convenience)

    operation longestWord(words):
        root <- empty Node
        for each word w in words:
            insert w into the trie, storing w at its end node

        best <- empty string
        depthFirstSearch(root, best)
        return best

    function depthFirstSearch(node, best):
        # the root itself is not a word; skip it, but explore its children
        if node is not the root AND node.word is not empty:
            if length(node.word) > length(best):
                best <- node.word
            else if length(node.word) == length(best) AND node.word < best lexicographically:
                best <- node.word

        # visit children in alphabetical order so ties break lexicographically
        for index from 0 to 25:
            child <- node.children[index]
            if child is not empty AND child.isEnd is true:
                depthFirstSearch(child, best)

    # insert is the standard trie insert with the word stashed at the end node
    function insert(root, w):
        node <- root
        for each character c in w:
            index <- c minus 'a'
            if node.children[index] is empty:
                create new Node there
            node <- node.children[index]
        node.isEnd <- true
        node.word <- w

`best` must be passed and returned by reference (or be a mutable holder) because every recursive
call updates the same answer.

## Java Solution

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
    String word;
}

class Solution {
    public String longestWord(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                int i = c - 'a';
                if (node.children[i] == null) {
                    node.children[i] = new TrieNode();
                }
                node = node.children[i];
            }
            node.isEnd = true;
            node.word = w;
        }

        String[] best = {""};
        dfs(root, best);
        return best[0];
    }

    private void dfs(TrieNode node, String[] best) {
        if (node.word != null) {
            if (node.word.length() > best[0].length()
                    || (node.word.length() == best[0].length()
                        && node.word.compareTo(best[0]) < 0)) {
                best[0] = node.word;
            }
        }
        for (int i = 0; i < 26; i++) {
            TrieNode child = node.children[i];
            // Only descend through nodes that are themselves complete words --
            // every prefix of the answer must be a word in the dictionary.
            if (child != null && child.isEnd) {
                dfs(child, best);
            }
        }
    }
}
```

The trie stores the full word at each end-of-word node so the DFS never has to rebuild strings
from the path. The DFS only descends into a child if that child's `isEnd` is true -- this is the
"every prefix must be a word" rule made structural: a missing `isEnd` means that prefix was never
inserted, so no longer word extending it can qualify. The `best` answer is held in a one-element
`String[]` so recursive calls can mutate it without a return value (Java strings are immutable and
references are passed by value). Children are visited in index order 0..25, which is alphabetical,
so the first time we encounter any given length the word is already lex-smallest than any later
one of the same length -- the explicit `compareTo` check is a safety net that also makes the rule
visible.

## Complexity

    Time:  O(sum of word lengths)  -- building the trie touches each character once; the DFS
                                      visits each end-of-word node at most once.
    Space: O(sum of word lengths)  -- the trie stores one node per character; recursion stack is
                                      bounded by the longest word.

## Dry-Run

`words = ["w","wo","wor","worl","world"]`. Trie (only end-of-word nodes marked `(*)`):

```
root
 |
 w(*)
 |
 o(*)
 |
 r(*)
 |
 l(*)
 |
 d(*)
```

DFS from root:

| visit | node.word | best before | best after | reason                  |
|-------|-----------|-------------|------------|-------------------------|
| root  | null      | ""          | ""         | skip (root is not word) |
| w     | "w"       | ""          | "w"        | longer than ""          |
| wo    | "wo"      | "w"         | "wo"       | longer                  |
| wor   | "wor"     | "wo"        | "wor"      | longer                  |
| worl  | "worl"    | "wor"       | "worl"     | longer                  |
| world | "world"   | "worl"      | "world"    | longer                  |

No branches exist (each node has exactly one end-of-word child), so the DFS walks straight down
and `best` ends as `"world"`.

Tie-break example, `words = ["a","banana","app","appl","ap","apply","apple"]`:

```
root
 |
 a(*)
 |
 p(*)
 |
 p(*)
 |\
 | \
 ..(continue for appl -> apple, apply)
```

The relevant subtrie under `a -> p -> p`:

```
        p(*)
        / \
       l   l
       |   |
       y   e
      (*) (*)
   "apply" "apple"
```

Both "apple" and "apply" have length 5. The DFS visits child `'e'` (index 4) before `'y'` (index
24), so it finds "apple" first and sets `best = "apple"`. When it later reaches "apply" the
lengths tie and `compareTo` keeps the earlier (lex-smaller) "apple". Output: `"apple"`.

## Common mistakes

- Descending into a child whose `isEnd` is false. That prefix was never inserted as a word, so it
  cannot be part of the answer. Always check `child.isEnd` before recursing.
- Starting the answer as `null` and forgetting to handle the empty-result case, causing a
  `NullPointerException` on the `compareTo` call. Initialise `best` to `""`.
- Visiting children out of alphabetical order and then forgetting the `compareTo` tie-break. Both
  must be consistent; doing either one alone is enough, doing both is safe.
- Comparing lengths with `>` but using `>=` on the tie, which overwrites a valid lex-smaller word
  with a later one of the same length.
- Skipping single-character words. They are always valid (their only prefix is themselves), so
  they must be eligible answers -- the DFS naturally includes them because the root's child is
  visited and has `isEnd == true`.

## Related problems

- [0208 - Implement Trie (Prefix Tree)](../0208-implement-trie-prefix-tree/) -- the underlying
  data structure; the `isEnd` check that drives this solution is exactly `Trie.search`.
- [0211 - Design Add and Search Words Data Structure](../0211-design-add-and-search-words-data-structure/)
  -- another DFS over a trie, with a wildcard instead of a prefix constraint.
- [0212 - Word Search II](../0212-word-search-ii/) -- the harder "trie + DFS" capstone; same
  skeleton, bigger search space.
