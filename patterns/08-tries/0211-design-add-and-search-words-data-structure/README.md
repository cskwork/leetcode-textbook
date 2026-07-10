# 0211 - Design Add and Search Words Data Structure

**Difficulty:** Medium
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/design-add-and-search-words-data-structure/

## Concepts used

- **Trie (prefix tree)** -- a tree whose edges are letters, so any path from the root spells a
  word; the dictionary we store words in.
  [glossary](../../../docs/10-glossary.md#trie-prefix-tree)
- **Recursion** -- a function that calls itself on a smaller version of the same problem; we use
  it to try every child at a `.` wildcard.
  [glossary](../../../docs/10-glossary.md#recursion)
- **DFS (Depth-First Search)** -- a traversal that dives down one branch fully before backing up
  to try the next. [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)

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

Picture an old card catalog in a library: each drawer leads you letter by letter to the book you
want. Now ask the librarian "give me any three-letter word shaped `_ad`" -- the first letter could
be anything, but the next two must be `a` and `d`. The librarian opens every drawer, and for each
opening letter walks the `a -> d` path to see whether a complete word sits there. That is this
problem: a [trie](../../../docs/10-glossary.md#trie-prefix-tree) (a
[tree](../../../docs/10-glossary.md#tree) whose edges are letters) storing the dictionary, plus
queries in which `.` means "any letter in this slot".

Start from the trie built in LC 208. Inserting `"bad"`, `"dad"`, `"mad"` gives three branches
under the root: `b -> a -> d`, `d -> a -> d`, `m -> a -> d`. A normal query like `"bad"` is easy --
follow one child per letter, the same walk as before. The new twist is `.`. When the query
character is a real letter, go down exactly one edge. When it is `.`, you don't know which edge to
take, so try **every** child that exists and succeed if **any** branch reaches an end-of-word.
"Try every child, then combine the answers" is
[recursion](../../../docs/10-glossary.md#recursion) -- a function that calls itself on a smaller
piece of the same problem, here the rest of the query -- and it is the same idea as
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search) (depth-first search): dive down one
branch fully, and if it fails, back up and try the next.

Trace the smallest wildcard query, `search("b..")`, on the `{bad, dad, mad}` dictionary. Position
0 is `b`: take the single `b` child. Position 1 is `.`: from the `b` node the only child is `a`,
so recurse into `a`. Position 2 is `.`: from `b -> a` the only child is `d`, recurse into `d`. The
query is now exhausted, and `d` is an end-of-word node, so return `true`. For `search(".ad")` the
first `.` fans out to `b`, `d`, and `m`; the `b` branch lands on an end-of-word first, so the whole
search returns `true` without exploring the others.

The cost depends sharply on the query shape. A query with no dots is the same O(L) walk as LC 208.
A query of *all* dots is the worst case: each dot may branch into up to 26 children, giving as
many as 26^L paths. In practice the trie is sparse -- most of those 26 children don't exist -- so
the real dictionary prunes the branching hard.

### Checkpoint A -- Real letter vs wildcard

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** In a search query, what does a `.` match?
- a) Only the letter 'o'
- b) Any single letter in that position
- c) The end of a word

<details><summary>Show answer</summary>

**(b)** -- a dot is a wildcard standing in for one arbitrary letter. It does not match more than one letter or mark a word boundary.

</details>

**Q2 (comprehend).** When the current query character is a real letter (not `.`), how many children do you follow?
- a) Every non-null child, trying them all
- b) Exactly one -- the child for that specific letter
- c) Two -- the matching child plus a fallback

<details><summary>Show answer</summary>

**(b)** -- a real letter names a single edge, so you descend into just that child. Branching over many children only happens at a `.`.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** The dictionary contains only `addWord("bad")`. What do `search(".ad")` and `search("b.d")` each return?
- a) `.ad` -> `true`; `b.d` -> `true`
- b) `.ad` -> `false`; `b.d` -> `true`
- c) `.ad` -> `true`; `b.d` -> `false`

<details><summary>Show answer</summary>

**(a)** -- both queries describe "any letter, then a, then d". The trie has exactly one `? -> a -> d(*)` branch (under `b`), so each wildcard resolves to `b` and lands on the end-of-word `d`.

</details>

**Q2 (analyze).** When handling a `.`, what goes wrong if you recurse into null children instead of skipping them?
- a) Nothing; null children are ignored automatically
- b) The next call dereferences null and throws a `NullPointerException`
- c) It silently returns the wrong boolean

<details><summary>Show answer</summary>

**(b)** -- a null slot means "no child here", and the recursive call would read fields off a null node. Always guard with `child != null` before recursing.

</details>

**Q3 (transfer).** Suppose the query also allowed `*` meaning "match zero or more letters" (a variable-length wildcard). Conceptually, how must the search change?

<details><summary>Show answer</summary>

At a `*`, the matcher must try two moves at once: consume nothing and continue with the rest of the query at the same node, OR consume one letter down a child and keep the `*`. The trie walk is unchanged, but the match function now branches into two recursive cases per star.

</details>

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
