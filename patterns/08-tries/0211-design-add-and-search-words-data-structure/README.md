# 0211 - Design Add and Search Words Data Structure

**Difficulty:** Medium
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/design-add-and-search-words-data-structure/

## Problem

Design a data structure that supports two operations:

- `addWord(word)` -- add the word to the dictionary. Word is lowercase letters.
- `search(word)` -- return `true` if any dictionary word matches `word`. The query may contain
  dots `.` which match **any single letter**.

Signature:

    WordDictionary()
    void addWord(String word)
    boolean search(String word)

Example:

    Input:
      addWord("bad")
      addWord("dad")
      addWord("mad")
      search("pad")   // false   -- "pad" not inserted
      search("bad")   // true
      search(".ad")   // true    -- ".ad" matches "bad", "dad", "mad"
      search("b..")   // true    -- "b.." matches "bad"

## Intuition

This is LC 208's trie with one twist: the search query may contain `.` (wildcard). A normal
letter forces us down one specific child, but a dot asks us to try **every** child and succeed if
**any** branch reaches an end-of-word. That branching is exactly recursion (DFS) -- at a dot, we
fan out to all non-null children; at a real letter, we follow just one child. The trigger signal
is "word dictionary" plus "match any letter" -- the moment you see a wildcard over a structured
alphabet, recursion on every branch is the natural move. The trie keeps the *dictionary* small
(shared prefixes share nodes); recursion does the *query*.

## Pseudocode

    structure Node:
        children : fixed array of 26 child slots
        isEnd    : boolean

    operation addWord(word):
        node <- root
        for each character c in word:
            index <- c minus 'a'
            if node.children[index] is empty:
                create a new Node there
            node <- node.children[index]
        node.isEnd <- true

    operation search(word):
        return matchFrom(root, word, position 0)

    # recursive: does node's subtree match word[position..]?
    function matchFrom(node, word, position):
        if position equals word length:
            return node.isEnd is true

        c <- word[position]
        if c is a dot '.':
            for each non-empty child of node:
                if matchFrom(child, word, position + 1) is true:
                    return true          # any branch succeeding is enough
            return false                 # no child matched
        else:
            index <- c minus 'a'
            if node.children[index] is empty:
                return false             # no such path
            return matchFrom(node.children[index], word, position + 1)

## Java Solution

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
}

class WordDictionary {
    private TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) {
                node.children[i] = new TrieNode();
            }
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return match(root, word, 0);
    }

    private boolean match(TrieNode node, String word, int pos) {
        if (pos == word.length()) {
            return node.isEnd;
        }
        char c = word.charAt(pos);
        if (c == '.') {
            for (TrieNode child : node.children) {
                if (child != null && match(child, word, pos + 1)) {
                    return true;
                }
            }
            return false;
        }
        TrieNode child = node.children[c - 'a'];
        return child != null && match(child, word, pos + 1);
    }
}
```

`addWord` is identical to LC 208's `insert`. The new work is in `match`: when the current query
character is `.`, we loop over **every** non-null child and recurse with the rest of the word;
returning `true` on the first hit short-circuits the search. For a normal letter we descend into
exactly one child. The base case `pos == word.length()` checks the end-of-word flag -- this is
what distinguishes "the query path exists" from "a real word ends here". Recursion depth equals
query length, so the call stack is bounded by `word.length()`.

## Complexity

    Time (addWord):  O(L)
    Time (search):   O(L) for a query with no dots; O(26^L) worst case for a query of all dots,
                     because each dot fans out over up to 26 children. In practice the trie is
                     sparse and the dictionary constrains the branching hard.
    Space: O(total chars inserted) for the trie, plus O(L) recursion stack per search.

## Dry-Run

Dictionary after `addWord("bad")`, `addWord("dad")`, `addWord("mad")`:

```
        root
       / | \
      b  d  m
      |  |  |
      a  a  a
      |  |  |
      d(*) d(*) d(*)
```

All three words share the `a -> d` suffix because the trie merges common paths.

Step-by-step for `search("b..")`:

| pos | char | node at start of call | action                              | result |
|----:|------|-----------------------|-------------------------------------|--------|
| 0   | 'b'  | root                  | follow child 'b'                    | recurse|
| 1   | '.'  | the 'b' node          | only child is 'a' -> recurse on it  | recurse|
| 2   | '.'  | the 'a' under 'b'     | only child is 'd' -> recurse on it  | recurse|
| 3   | (end)| the 'd' under 'b...a' | pos == length, check isEnd -> true  | true   |

For `search(".ad")` the first call sees `.` at the root and recurses on each of `b`, `d`, `m`. The
`b` branch (then `a`, then `d`) reaches an end-of-word first, so the whole search returns `true`
without exploring the others.

For `search("pad")`: at pos 0 we look for child `'p'` of root, which is null, so `match` returns
`false` immediately.

## Common mistakes

- Treating `.` as a normal character and indexing `children['.' - 'a']` -- that index is garbage
  and either throws or always misses. Always branch on the dot before indexing.
- Forgetting to recurse when handling `.` -- instead just picking the first non-null child. That
  misses words reachable only through other children.
- Returning `true` as soon as a child exists when handling `.`. Existence of a child is not
  enough; the rest of the query must still match down that subtree. Always recurse.
- Using the end-of-word check only at `pos == word.length()` and not at all -- some beginners
  return `true` whenever they reach any node after walking all characters, which wrongly accepts
  prefixes that are not full words.
- Not skipping null children when looping on `.` -- recursing into `null` causes a
  `NullPointerException` at the next level.

## Related problems

- [0208 - Implement Trie (Prefix Tree)](../0208-implement-trie-prefix-tree/) -- the same data
  structure without the wildcard; do this one first.
- [0212 - Word Search II](../0212-word-search-ii/) -- the wildcard's bigger cousin: instead of a
  dot, every cell on the board is a "try any letter" branch.
- [0720 - Longest Word in Dictionary](../0720-longest-word-in-dictionary/) -- another DFS over a
  trie, this time looking for the longest word with all-valid prefixes.
