# 0720 - Longest Word in Dictionary

**Difficulty:** Easy
**Pattern:** Tries
**LeetCode:** https://leetcode.com/problems/longest-word-in-dictionary/

## Concepts used

- **Trie (prefix tree)** -- a tree whose edges are letters, so any path from the root spells a
  word; we store the dictionary in it.
  [glossary](../../../docs/10-glossary.md#trie-prefix-tree)
- **DFS (Depth-First Search)** -- a traversal that dives deep down one branch before backing up;
  we walk the trie this way to find the longest valid word.
  [glossary](../../../docs/10-glossary.md#dfs-depth-first-search)
- **Hash set** -- a container that answers "have I seen X?" in O(1); the alternative approach we
  compare against. [glossary](../../../docs/10-glossary.md#hash-set)

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

Think of unlocking doors in sequence: you can only walk through doorway 4 if you have already
passed through doorways 1, 2, and 3. This problem is the word version. A word qualifies only if
**every shorter version of it, chopping one letter off the end at a time, is also in the list**.
For `"world"` to be the answer, the list must also contain `"worl"`, `"wor"`, `"wo"`, and `"w"`.
Each of those shorter strings is a **prefix** -- the first few letters of the word -- and the
question "is this prefix itself a word?" is exactly what a
[trie](../../../docs/10-glossary.md#trie-prefix-tree) answers cheaply.

Walk the smallest case: `words = ["w", "wo", "wor", "worl", "world"]`. Build a trie (a
[tree](../../../docs/10-glossary.md#tree) whose edges are letters) by inserting all five words;
each inserted word marks its final node end-of-word. The result is a single chain
`w -> o -> r -> l -> d`, and every node along it is flagged end-of-word, because every prefix is
itself a word in the list. So you can walk the whole chain, and `"world"` is the longest reachable
word.

The general rule follows the same shape. Walk the trie from the root, but only ever step into a
child whose node is end-of-word: a missing flag means that prefix was never inserted, so no longer
word built on it can qualify, and you stop descending that way. Keep track of the longest word you
land on. For the tie-break ("if two words have the same length, return the alphabetically first"),
visit children in alphabetical order during the
[DFS](../../../docs/10-glossary.md#dfs-depth-first-search) (depth-first search -- dive deep down
one branch, then back up). Then the first word of any given length you meet is already the
alphabetically-first one, so you overwrite your best only when you find a *strictly longer* word.

Why a trie and not a sorted list plus a [hash set](../../../docs/10-glossary.md#hash-set)? That
combo works (sort the words, then for each word check that every prefix is in the set), but the
trie makes "are all prefixes valid?" structural: the moment a node lacks the end-of-word flag you
do not descend, so the answer prunes itself as you walk. This is the "is every prefix a word?"
query that also powers autocomplete systems. LC 208 is the underlying data structure -- its
`search` is exactly the per-node end-of-word check used here -- and LC 211 walks the same trie
with a `.` wildcard instead of a prefix rule.

### Checkpoint A -- The prefix rule

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** For a word to be a valid answer, what must be true of every one of its prefixes?
- a) Each prefix must be longer than the word itself
- b) Each prefix must itself be a word in the list
- c) Each prefix must contain only one letter

<details><summary>Show answer</summary>

**(b)** -- chopping one letter off the end, repeatedly, must always land on another word in the list. The answer is built one valid prefix at a time.

</details>

**Q2 (comprehend).** During the DFS, why does the code only step into a child whose `isEnd` is true?
- a) To save memory
- b) Because if a prefix is not a word, no longer word built on top of it can qualify
- c) So that children are visited in alphabetical order

<details><summary>Show answer</summary>

**(b)** -- a missing `isEnd` means that prefix was never inserted, so it breaks the "every prefix is a word" rule. Refusing to descend there prunes the whole invalid branch.

</details>

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

### Checkpoint B -- Trace and stress it

**Q1 (apply).** `words = ["a", "ab", "abc"]`. What does `longestWord` return?
- a) `"a"`
- b) `"ab"`
- c) `"abc"`

<details><summary>Show answer</summary>

**(c)** -- every node in the single chain `a(*) -> b(*) -> c(*)` is end-of-word, so the DFS descends all the way and "abc" (length 3) is the longest valid word.

</details>

**Q2 (analyze).** `words = ["ab", "abc"]` (note: the single letter "a" is NOT in the list). What is returned, and why?
- a) `"abc"`, because it is the longest word present
- b) `""`, because the `a` node is not end-of-word, so the DFS never descends into it
- c) `"ab"`, because at least it is a word

<details><summary>Show answer</summary>

**(b)** -- the DFS checks `child.isEnd` before descending; the `a` node lacks the flag, so neither "ab" nor "abc" is ever reached. With no valid prefix chain, `best` stays `""`.

</details>

**Q3 (transfer).** If the tie-break were reversed ("on equal length, return the lexicographically LARGEST"), what is the smallest change to the DFS?

<details><summary>Show answer</summary>

Visit children in reverse alphabetical order -- index 25 down to 0 -- so the lex-largest word of any given length is found first and wins. (Equivalently, flip the `compareTo` direction; doing just the visit order is enough.)

</details>

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
