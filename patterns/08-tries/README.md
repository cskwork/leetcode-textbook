# Pattern 8 - Tries

A trie (pronounced "try", from re**trie**val) is a tree whose **paths spell words**. If you have
ever used autocomplete, spell-check, or a phone contact search that filters as you type, you have
used a trie. This is the pattern that makes prefix queries fast.

---

## What a trie is

A trie stores strings character by character. Every edge is labeled with one character, and every
path from the root to a node spells out the **prefix** you would type to reach that node. Some
nodes are marked as **end-of-word**: the path from root to such a node is a complete word that was
inserted.

```
            root
           /    \
          a      b
          |      |
          p      a
          |      |
          p      t(*)     <- "bat"
          |
          l
          |
          e(*)    <- "apple"

          p(*)    <- "app" (also a prefix of "apple")
```

Nodes marked `(*)` are end-of-word markers. Notice that "app" is a prefix of "apple" and shares
the same first three nodes. **Shared prefixes share nodes** -- that is the entire reason a trie
exists.

The node structure:

```
class TrieNode:
    children : array or map of (character -> child node)
    isEnd    : boolean, true if a word ends at this node
```

For lowercase English letters (`'a'..'z'`) we use a fixed `children[26]` array, indexing with
`c - 'a'`. For arbitrary alphabets (Unicode, mixed case), swap the array for a `HashMap`.

---

## When it applies -- trigger signals

Reach for a trie when the problem statement contains any of these signals:

| Trigger signal                                | Example problem                          |
|---|---|
| "word starts with prefix", "startsWith"       | Implement Trie (208)                     |
| "autocomplete", "all words with prefix"       | Word Search II (212), suggestions systems |
| "word dictionary", "design a data structure"  | Implement Trie (208), Add/Search (211)   |
| "shared-prefix detection", "longest common"   | Longest Common Prefix, Longest Word (720)|
| "wildcard match on a dictionary"              | Design Add and Search Words (211)        |
| "find dictionary words in a grid"             | Word Search II (212)                     |

If the question is "is this exact string in a set?", a `HashSet` is enough and a trie is overkill.
Tries earn their keep when you also ask about **prefixes** or need to walk **character by
character** (for wildcards, for backtracking on a board, for "is every prefix a word?").

---

## Why a trie beats a HashSet for prefix queries

A `HashSet<String>` answers "is `s` in the dictionary?" in O(L) (it must hash the whole string).
But it answers **"does any word start with `app`?"** only by scanning every word -- O(n * L).

A trie answers both questions by walking L edges from the root, never touching unrelated words:

| Question                       | HashSet         | Trie |
|---|---|---|
| Is `"apple"` a word?           | O(L)            | O(L) |
| Does any word start `"app"`?   | O(n * L)        | O(L) |
| All words with prefix `"app"`? | O(n * L)        | O(L + output size) |

The trie's cost does not depend on the *number* of words -- it depends only on the length of the
query. That is the win.

---

## General pseudocode template

The three core operations share one shape: **walk down the tree, one character at a time**. Only
what you do at the end differs.

```
# A node has:
#   children : a map from character -> child node
#   isEnd    : boolean flag, true when a word terminates here

function insert(word):
    node <- root
    for each character c in word:
        if c is not a key in node.children:
            create a new empty child under c
        node <- node.children[c]
    node.isEnd <- true        # mark END-OF-WORD on the LAST character's node

function search(word):
    node <- walk(word)         # descend following each character; stop if any is missing
    return node is not null AND node.isEnd is true

function startsWith(prefix):
    node <- walk(prefix)
    return node is not null    # any node reachable means some word extends this prefix

# shared helper used by search and startsWith
function walk(text):
    node <- root
    for each character c in text:
        if c is not a key in node.children:
            return null        # path breaks -- text cannot be a word or a prefix
        node <- node.children[c]
    return node                # the node where the path ends
```

Three rules to internalize:

1. **The end-of-word marker lives on the LAST character's node, not on the next one.** Inserting
   "app" and "apple" creates one shared path `a -> p -> p`; the second `p` gets `isEnd = true`
   (for "app"), and the path continues to `l -> e` whose `e` node also gets `isEnd = true` (for
   "apple").
2. **`search` requires the end-of-word flag; `startsWith` does not.** A prefix is valid as soon as
   the walk succeeds.
3. **Every node starts empty.** Inserting "apple" creates five new nodes, one per character.

---

## Problems in this section

Four problems, building from the raw data structure up to a classic Hard capstone. Do them in
order -- each one layers one new idea on top of the previous.

| # | Folder                                                              | Problem                                | Difficulty | One-line teaser |
|---|---|---|---|---|
| 1 | [0208-implement-trie-prefix-tree](./0208-implement-trie-prefix-tree/) | Implement Trie (Prefix Tree)         | Medium     | The raw data structure: `insert`, `search`, `startsWith`. Foundation for the section. |
| 2 | [0211-design-add-and-search-words-data-structure](./0211-design-add-and-search-words-data-structure/) | Design Add and Search Words Data Structure | Medium | Same trie, but `.` matches any letter -- recurse on every child. |
| 3 | [0212-word-search-ii](./0212-word-search-ii/)                       | Word Search II                         | Hard       | Build a trie from the word list, then backtrack across the board walking the trie. |
| 4 | [0720-longest-word-in-dictionary](./0720-longest-word-in-dictionary/) | Longest Word in Dictionary           | Easy       | Build a trie, then DFS for the longest word whose every prefix is itself a word. |

---

## Common pitfalls of the pattern

Tries look simple in pseudocode but have many places to slip:

- **Forgetting `isEnd` on the last character.** The most common bug. Insert "app" and then call
  `search("ap")` -- it should return `false`, but if you set `isEnd` on the *next* node after
  the last char (or never set it at all), it returns `true`. Set the flag on the node you land on
  after consuming the final character.
- **Mixing up children indexing.** For lowercase letters the index is `c - 'a'`, not `c` itself.
  Using `c` as an array index throws `ArrayIndexOutOfBoundsException` because `'a'` is 97, not 0.
  For mixed-case or Unicode input, switch to `HashMap<Character, TrieNode>`.
- **Confusing `search` with `startsWith`.** `startsWith` only needs the walk to succeed; `search`
  additionally needs `isEnd == true` at the destination. Many submissions swap these and fail on
  the "prefix but not a word" test case.
- **Not handling the wildcard `.` in LC 211.** When the character is `.`, you must try **every**
  non-null child and return true if **any** branch reaches an end-of-word. Forgetting to recurse
  and just `break`-ing out is the usual mistake.
- **Memory blow-up for sparse alphabets.** A fixed `children[26]` wastes 26 pointers per node
  even when most are null. For a dictionary of long, prefix-poor words this is fine; for huge
  alphabets (Unicode) it is not -- use a map, and consider DAWG / radix-tree compression for
  production systems.
- **Reporting duplicate words in LC 212.** If the dictionary contains the same word twice, or if
  two board paths reach the same word, a naive DFS appends it twice. After collecting a word,
  clear its `isEnd` flag (or store results in a `Set`) so it is reported only once.
- **Walking off the board / revisiting cells in LC 212.** Backtracking DFS needs a `visited`
  marker (usually a temporary on-board sentinel or a separate set), undone on the way out.

## Pattern Mastery Quiz

Five questions ramping from recall to design. Try each before revealing.

**Q1 (recall).** In one sentence, what is the core advantage a trie holds over a HashSet for prefix queries?

<details><summary>Show answer</summary>

A trie answers "does any word start with this prefix?" in O(L) by walking L edges, whereas a HashSet must scan every stored word -- O(n * L) -- because it cannot tell that a string is merely the start of a word.

</details>

**Q2 (pattern recognition).** New problem: "a search box that, after every keystroke, lists all dictionary words beginning with the typed prefix." Which tool fits best?
- a) A HashSet of words, rescanned on each keystroke
- b) A trie; walk to the prefix node, then collect every end-of-word descendant
- c) A sorted list, binary-searched on each keystroke and then expanded

<details><summary>Show answer</summary>

**(b)** -- the trie makes each prefix lookup O(L + number of suggestions), independent of the dictionary size. A HashSet rescan is O(n * L) per keystroke, and the sorted-list option still has to walk the sorted tail to gather suggestions.

</details>

**Q3 (pattern recognition).** New problem: "given a board of letters and a list of words, count how many of the words appear somewhere on the board." Which approach?
- a) Build a trie of the words and DFS the board guided by the trie (the Word Search II skeleton)
- b) For each word, scan the whole board independently with no shared structure
- c) Throw every board substring into a HashSet and look each word up

<details><summary>Show answer</summary>

**(a)** -- the trie prunes dead prefixes so the search does not redo work per word; (b) repeats the board walk for every word, and (c) enumerates an exponential number of board strings.

</details>

**Q4 (apply).** A trie is built from `["to", "tea", "ted"]`. What do `search("te")` and `startsWith("te")` return?
- a) `search("te")` -> `false`; `startsWith("te")` -> `true`
- b) both -> `true`
- c) `search("te")` -> `true`; `startsWith("te")` -> `false`

<details><summary>Show answer</summary>

**(a)** -- the path `t -> e` exists (under it sit "tea" and "ted"), so the walk lands on the `e` node. But "te" was never inserted, so that node's `isEnd` is false: not a word for `search`, yet a perfectly good prefix for `startsWith`.

</details>

**Q5 (design).** Sketch (in words, not code) how to add a `delete(word)` operation that removes a word without breaking other words that share its prefix.

<details><summary>Show answer</summary>

Walk to the word's end node and clear its `isEnd` flag -- that alone makes `search` stop returning the word. Then walk back up and remove any node that is now a leaf and not end-of-word, stopping at the first node that still has children or its own `isEnd`. That upward prune reclaims dead nodes while leaving shared prefixes intact.

</details>

---

Next: [0208 - Implement Trie (Prefix Tree)](./0208-implement-trie-prefix-tree/) -- start here,
everything else in this section depends on this data structure.
