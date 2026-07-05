# 0208 - Implement Trie (Prefix Tree)

**Difficulty:** Medium
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/implement-trie-prefix-tree/

## Problem

Design a data structure that stores strings and supports three operations:

- `insert(word)` -- add the word to the trie.
- `search(word)` -- return `true` if the exact word was inserted before.
- `startsWith(prefix)` -- return `true` if any previously inserted word starts with the given
  prefix.

Signature:

    Trie()                         // constructor
    void insert(String word)
    boolean search(String word)
    boolean startsWith(String prefix)

Example:

    Input:
      insert("apple")
      search("apple")      // returns true
      search("app")        // returns false  -- "app" was never inserted as a word
      startsWith("app")    // returns true   -- "apple" starts with "app"
      insert("app")
      search("app")        // returns true

Words consist only of lowercase English letters.

## Intuition

This is the foundational data structure of the entire Tries pattern. The trigger signal is
literal: the method names `startsWith` and the phrase "word dictionary". The core idea is a tree
where each path from the root spells a word, and shared prefixes share nodes. Three operations
reduce to one shared move -- walk character by character from the root -- and then a tiny decision
at the end:

- `insert`: walk, creating missing nodes along the way; mark the final node `isEnd = true`.
- `search`: walk without creating; success iff the walk finishes on an `isEnd` node.
- `startsWith`: walk without creating; success iff the walk finishes at all.

Because every operation walks exactly `L = word.length()` edges, each is O(L) time regardless of
how many words are stored.

## Pseudocode

    structure Node:
        children : fixed array of 26 child slots (one per lowercase letter)
        isEnd    : boolean, true if a word ends at this node

    operation insert(word):
        node <- root
        for each character c in word:
            index <- c minus 'a'                      # 'a' -> 0, 'b' -> 1, ..., 'z' -> 25
            if node.children[index] is empty:
                create a new empty Node at node.children[index]
            node <- node.children[index]
        node.isEnd <- true                            # mark the LAST character's node

    operation search(word):
        node <- walkDown(word)
        return node is not null AND node.isEnd is true

    operation startsWith(prefix):
        node <- walkDown(prefix)
        return node is not null

    # shared helper: descend following each char; null if the path breaks
    helper walkDown(text):
        node <- root
        for each character c in text:
            index <- c minus 'a'
            if node.children[index] is empty:
                return null
            node <- node.children[index]
        return node

## Java Solution

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd;
}

class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
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
        TrieNode node = walk(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return walk(prefix) != null;
    }

    private TrieNode walk(String text) {
        TrieNode node = root;
        for (char c : text.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) {
                return null;
            }
            node = node.children[i];
        }
        return node;
    }
}
```

`TrieNode` is a top-level class (not an inner class) so the test file can use it directly without
redefinition. The `children` array is fixed-size 26 because the alphabet is lowercase letters --
this gives O(1) indexing with `c - 'a'` and avoids the per-node overhead of a `HashMap`. The
private `walk` helper factors out the shared descent so that `search` and `startsWith` differ only
in their final check. The `isEnd` flag is set on the node we land on **after** consuming the final
character -- not on a new node below it -- so a prefix that is also a full word (e.g. "app" inside
"apple") is correctly recognized.

## Complexity

    Time:  O(L) per operation  -- walk exactly L = word.length() edges; array indexing is O(1)
    Space: O(total chars inserted)  -- in the worst case every char creates a new node; shared
                                      prefixes share nodes, so it is usually much less

Constructor is O(1); it just creates the empty root.

## Dry-Run

Operations in order: `insert("apple")`, `insert("app")`, then queries.

After `insert("apple")`:

```
root
 |
 a
 |
 p
 |
 p
 |
 l
 |
 e(*)        isEnd = true   ("apple")
```

After `insert("app")` -- the first three nodes are reused, only `isEnd` is set on the second `p`:

```
root
 |
 a
 |
 p
 |
 p(*)        isEnd = true   ("app")
 |
 l
 |
 e(*)        isEnd = true   ("apple")
```

Step-by-step for each query:

| Call                  | walk lands on | isEnd? | Result  | Why                                          |
|---|---|---|---|---|
| `search("apple")`     | the `e` node  | true   | `true`  | full word was inserted                       |
| `search("app")`       | the 2nd `p`   | true   | `true`  | "app" was inserted after "apple"             |
| `search("ap")`        | the 1st `p`   | false  | `false` | walk succeeds but "ap" was never inserted    |
| `search("applx")`     | null          | -      | `false` | walk breaks at 'x'; no such path             |
| `startsWith("app")`   | the 2nd `p`   | -      | `true`  | any reachable node counts as a prefix        |
| `startsWith("apz")`   | null          | -      | `false` | walk breaks at 'z'                           |

The key contrast is `search("ap")` vs `startsWith("ap")`: same destination node, different result,
because only `search` requires the end-of-word flag.

## Common mistakes

- Setting `isEnd = true` on the wrong node -- for instance, before advancing `node` to the child,
  or on a freshly created "next" node. Always set it on the node you arrive at **after** the last
  character.
- Indexing `children` with `c` directly instead of `c - 'a'`. `'a'` is 97, so the array overflows.
- Forgetting that `search` and `startsWith` differ by exactly the `isEnd` check -- swapping their
  bodies passes the wrong half of the tests.
- Not creating the root in the constructor; `root == null` causes a `NullPointerException` on the
  first insert.
- Treating `search` of a prefix as `true`. The walk succeeds but the flag is false, so the answer
  must be false -- beginners often return `true` whenever the walk lands on a node.

## Related problems

- [0211 - Design Add and Search Words Data Structure](../0211-design-add-and-search-words-data-structure/)
  -- same trie, but `search` accepts `.` as a wildcard that matches any letter.
- [0212 - Word Search II](../0212-word-search-ii/) -- builds a trie of dictionary words and walks
  it while backtracking across a board.
- [0720 - Longest Word in Dictionary](../0720-longest-word-in-dictionary/) -- builds a trie, then
  walks it looking for the longest word whose every prefix is also a word.
