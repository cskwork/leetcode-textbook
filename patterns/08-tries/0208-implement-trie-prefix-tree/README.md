# 0208 - Implement Trie (Prefix Tree)

**Difficulty:** Medium
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/implement-trie-prefix-tree/

## Concepts used

- **Trie (prefix tree)** -- a [tree](../../../docs/10-glossary.md#tree) whose edges are letters,
  so any path from the root spells out a word or the start of one.
  [glossary](../../../docs/10-glossary.md#trie-prefix-tree)
- **Tree** -- a hierarchy of nodes with one root at the top and zero or more children under each
  node. [glossary](../../../docs/10-glossary.md#tree)
- **Hash set** -- a container that only remembers which values it has seen, answering "have I seen
  X?" in O(1); the alternative this problem improves on.
  [glossary](../../../docs/10-glossary.md#hash-set)

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

Think of the autocomplete dropdown on your phone's keyboard. Type `ap` and it instantly suggests
`apple`, `apply`, `app`. The phone is not flipping through its whole dictionary word by word --
once you have typed `a` then `p`, the phone is sitting at one spot in memory where every word
hanging off that spot begins with `ap`. That spot is a node in a **trie** (say "try"): a
[tree](../../../docs/10-glossary.md#tree) whose **edges** (the links between
[nodes](../../../docs/10-glossary.md#tree)) are each labeled with one letter, so that any path
from the root spells out a word or the start of one. Some nodes are flagged "a complete word ends
here". The first few letters of a word are its **prefix** -- `ap` is a prefix of `apple`.

Walk the smallest case. Start with an empty trie and `insert("app")`. Create one node per letter,
each hanging off the previous one: `root -> a -> p -> p`. Mark that last `p` as end-of-word. Now
`insert("apple")`. The first three letters `a`, `p`, `p` already exist as a path, so you reuse
those three nodes and only add `l -> e` underneath, then mark `e` as end-of-word too. That sharing
is the whole reason a trie exists: words that begin the same way share the same nodes.

The three operations this problem asks for all reduce to the same walk -- follow one edge per
character starting at the root -- and then a tiny decision at the end:

- `insert(word)`: walk, creating any missing nodes; mark the final node end-of-word.
- `search(word)`: walk without creating; succeed only if you land on an end-of-word node.
- `startsWith(prefix)`: walk without creating; succeed as soon as the walk completes.

Why not just dump the words into a [hash set](../../../docs/10-glossary.md#hash-set)? A hash set
answers "is `apple` here?" in O(L) by hashing the whole string -- fine. But "does *any* word start
with `ap`?" forces it to scan the entire dictionary, comparing prefixes one word at a time:
O(n * L) where n is the number of words. A trie answers both questions by walking the same
`a -> p` path once, O(L), never touching words that don't start with `a`. The trie's cost depends
only on the length of your query, not on how many words are stored.

This is the foundation of the whole Tries section. LC 211 reuses this exact data structure but
lets the search query contain a `.` wildcard (any letter); LC 720 walks the same trie to find the
longest word whose every prefix is also a word. Master the one-edge-per-letter walk here and the
other three problems build on top of it.

### Checkpoint A -- The flag and the walk

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** On a trie node, what does `isEnd == true` mean?
- a) This node has children below it
- b) A complete inserted word ends exactly at this node
- c) This node is the root of the trie

<details><summary>Show answer</summary>

**(b)** -- the flag marks the last character of a word. A node can have children (a longer word continues) and still be end-of-word, like the second `p` of "app" inside "apple".

</details>

**Q2 (comprehend).** After ONLY `insert("apple")`, why does `search("app")` return `false` while `startsWith("app")` returns `true`?
- a) The walk breaks for both, so both fail
- b) Both walks land on the same node; `startsWith` ignores `isEnd`, but `search` needs it true, and it is false
- c) `search` and `startsWith` walk completely different paths

<details><summary>Show answer</summary>

**(b)** -- the `a -> p -> p` path exists, so the walk lands on the second `p`. Only "apple" was inserted, so that node's `isEnd` is false: fine for a prefix, not enough for a full word.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Start with an empty trie. Run `insert("cat")`, then `search("ca")`. What happens?
- a) The walk breaks and returns `null`, so `false`
- b) The walk lands on the `a` node, which is not end-of-word, so `false`
- c) Returns `true`, because "ca" is a prefix of "cat"

<details><summary>Show answer</summary>

**(b)** -- after inserting "cat" the path is `c -> a -> t(*)`. Searching "ca" walks `c -> a`, lands on `a` whose `isEnd` is false, so the answer is `false`. The walk does not break -- the path exists, it just is not a full word.

</details>

**Q2 (analyze).** What breaks if you index the children array with the raw character `c` instead of `c - 'a'`?
- a) Nothing; it behaves identically
- b) `children['a']` means index 97 on a size-26 array, so it throws an out-of-bounds error
- c) It silently stores words in the wrong slots but never errors

<details><summary>Show answer</summary>

**(b)** -- `'a'` is 97 and the array has only 26 slots, so any real letter index overflows immediately with an `ArrayIndexOutOfBoundsException`.

</details>

**Q3 (transfer).** Suppose you want a `countWordsWithPrefix(prefix)` method: how many inserted words start with the given prefix. What small addition to the trie would support it?

<details><summary>Show answer</summary>

Store at each node a count of how many inserted words pass through it; bump that count on every node along the path during `insert`. Then walk to the prefix's node and return its count -- no subtree scan needed.

</details>

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
