# 0139 - Word Break

**Difficulty:** Medium
**Pattern:** 1-D DP
**LeetCode:** https://leetcode.com/problems/word-break/

## Problem

Given a string `s` and a dictionary of strings `wordDict`, return
`true` if `s` can be **segmented** into a space-separated sequence of
one or more dictionary words. The same word may be reused multiple
times.

Signature:

    boolean wordBreak(String s, List<String> wordDict)

Examples (verbatim from LeetCode):

    Input:  s = "leetcode", wordDict = ["leet","code"]
    Output: true
    Explanation: "leet code".

    Input:  s = "applepenapple", wordDict = ["apple","pen"]
    Output: true
    Explanation: "apple pen apple"; reusing words is allowed.

    Input:  s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
    Output: false

Constraints: `1 <= s.length <= 300`, `1 <= wordDict.length <= 1000`,
`1 <= wordDict[i].length <= 20`.

## Intuition

Trigger: "can you reach the end" / "can you partition". This is a
boolean reachability DP — the analogue of Coin Change but with `OR`
instead of `min`.

Define `dp[i]` = "`true` if the prefix `s[0..i)` (the first `i`
characters) can be segmented into dictionary words". The full string
is segmentable iff `dp[s.length]` is true.

Ask: *what was the last word in a valid segmentation of `s[0..i)`?*
It is some dictionary word `w` whose characters match the tail of the
prefix — i.e. `s[i-w.length .. i)` equals `w` — AND the prefix *before*
that word was itself segmentable, i.e. `dp[i - w.length]` is true. If
any word satisfies both, `dp[i]` is true:

    dp[i] = exists word w in wordDict such that
            w.length <= i AND
            s[i - w.length .. i) == w AND
            dp[i - w.length] == true

Base case: `dp[0] = true` — the empty prefix is trivially segmentable
(zero words). Every other entry starts `false`. The answer is
`dp[s.length]`.

For fast membership checks we put `wordDict` into a `HashSet`. The
inner loop scans candidate word lengths (or the words directly); both
work. We scan words for clarity.

### Checkpoint A -- Reachability of a prefix

Pause and answer before expanding. Wrong guesses teach more than fast right ones.

**Q1 (recall).** In Word Break, `dp[i]` being true means what?
- a) The single character s[i] is in the dictionary
- b) The prefix of the first i characters, s[0..i), can be segmented into dictionary words
- c) There are i words in the dictionary

<details><summary>Show answer</summary>

**(b)** -- dp[i] records whether the first i characters are fully breakable; the answer is dp[n] for the whole string.

</details>

**Q2 (comprehend).** Why is `dp[0]` set to `true`?
- a) Because the dictionary contains the empty string
- b) Because the empty prefix is trivially segmentable (zero words), and every segmentation chains forward from it
- c) Because all strings start empty

<details><summary>Show answer</summary>

**(b)** -- dp[0]=true is the seed; without it no dp[i] could ever become true, because every word match needs the prefix before it to already be reachable.

</details>

## Pseudocode

```text
function wordBreak(s, wordDict):
    n = length of s
    build a set "words" from wordDict
    create dp array of size n+1, each entry = false
    dp[0] = true                       # empty prefix is segmentable
    for i from 1 to n:
        for each word w in wordDict:
            L = length of w
            if L <= i AND dp[i - L] AND s[i-L .. i) equals w:
                dp[i] = true
                break                  # one valid segmentation is enough
    return dp[n]
```

`break` on the first hit is a small optimisation — we only need to
know *whether* `dp[i]` is reachable, not how many ways.

## Java Solution

```java
import java.util.List;
import java.util.HashSet;
import java.util.Set;

class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            for (String w : wordDict) {
                int len = w.length();
                // A word w is a valid last segment of s[0..i) iff it fits,
                // the prefix before it was segmentable, and the tail matches w.
                if (len <= i && dp[i - len] && s.startsWith(w, i - len)) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }
}
```

`Set<String> words` is held in reserve for an O(1) `contains` if you
prefer the "scan lengths" inner loop; iterating `wordDict` directly is
clearer here and has the same complexity. `s.startsWith(w, i - len)`
is the cleanest way to ask "does `s` have `w` at offset `i-len`?" —
equivalent to `s.substring(i-len, i).equals(w)` but allocation-free.
The `break` short-circuits as soon as one segmentation is found for
position `i`.

## Complexity

    Time:  O(n * L * W)   -- n positions; for each, try W words, each compared in O(L) chars.
                             Bounded by O(n * L * total word chars); with L <= 20 and W <= 1000
                             and n <= 300 this is ~6 million char comparisons.
    Space: O(n)           -- one boolean dp array of size n+1 (plus the word set).

An alternative inner loop scans `i` over word lengths `L` from 0 to
`min(i, maxLen)` and checks `words.contains(s.substring(i-L, i))` —
useful when `wordDict` is huge but the max word length is small.

## Dry-Run

Step-by-step on `s = "leetcode"`, `wordDict = ["leet", "code"]`:

Initial `dp = [T, F, F, F, F, F, F, F, F]` (index 0..8).

| i | tail tried | len | i-len | dp[i-len] | s[i-len..i) matches? | dp[i] |
|---|------------|-----|-------|-----------|----------------------|-------|
| 1 | "leet"     | 4   | -3    | -         | len > i, skip        | F     |
|   | "code"     | 4   | -3    | -         | len > i, skip        |       |
| 2 | (both len 4 > 2, skip) | | | | | F |
| 3 | (both len 4 > 3, skip) | | | | F |
| 4 | "leet"     | 4   | 0     | T         | s[0..4)="leet" == "leet" -> yes | T |
|   | (break)    |     |       |           |                      |       |
| 5 | "leet"     | 4   | 1     | F         | skip                 | F     |
|   | "code"     | 4   | 1     | F         | skip                 |       |
| 6 | (both prefixes false) | | | | | F |
| 7 | (both prefixes false) | | | | | F |
| 8 | "leet"     | 4   | 4     | T         | s[4..8)="code" != "leet" -> no | |
|   | "code"     | 4   | 4     | T         | s[4..8)="code" == "code" -> yes | T |

Final `dp = [T, F, F, F, T, F, F, F, T]`, so `dp[8] = true`. The
segmentation is `"leet" | "code"`. Notice how `dp[4]` first becomes
true (the prefix `"leet"` is segmentable), and then `dp[8]` builds on
it by appending `"code"`.

### Checkpoint B -- Trace the boolean table

**Q1 (apply).** Trace `s = "abc"`, `wordDict = ["a", "bc"]`. What is `dp[3]`?
- a) true ("a" + "bc")
- b) false
- c) true ("abc" as one word)

<details><summary>Show answer</summary>

**(a)** -- dp[1]=true ("a"), dp[2]=false, dp[3]=true because "bc" matches s[1..3) and dp[1] is true. So "a"+"bc" segments the string.

</details>

**Q2 (analyze).** A word matches the tail only if THREE things hold. Which is NOT one of them?
- a) The word's length fits (len <= i)
- b) The prefix before the word was segmentable (dp[i-len] is true)
- c) The word appears earlier in wordDict than any other word
- d) The tail characters equal the word (s[i-len..i) == w)

<details><summary>Show answer</summary>

**(c)** -- dictionary order is irrelevant; a word is usable regardless of where it sits. The real conditions are (a) length, (b) reachable prefix, and (d) character match.

</details>

**Q3 (transfer).** If you needed to return ALL possible segmentations (not just true/false), what would dp[i] store instead of a boolean, in one sentence?

<details><summary>Show answer</summary>

dp[i] would store a list of segmentations of s[0..i); each match appends the word to every segmentation recorded at dp[i-len], which can grow exponentially.

</details>

## Common mistakes

- **Wrong base case.** `dp[0]` must be `true` (empty prefix); setting
  it `false` makes every later entry unreachable because the chain
  never starts.
- **Checking the word match before `dp[i-len]`.** Cheap booleans
  should be checked first; the substring comparison is the expensive
  part. Order matters for performance, though not for correctness.
- **Using `==` to compare strings.** Always `.equals(...)` (or
  `startsWith`/`regionMatches`); `==` compares references, not
  contents.
- **Returning `dp[i]` for the wrong `i`.** The answer is `dp[n]`
  (the full string), not `dp[n-1]`.
- **Forgetting that words can be reused.** The dictionary is a set of
  available *types*; consuming a word does not remove it. No need to
  track which words remain.

## Related problems

- [0322 - Coin Change](../0322-coin-change/) - the additive
  reachability analogue (`min` instead of `OR`); same outer-amount /
  inner-choice loop nesting.
- [0070 - Climbing Stairs](../0070-climbing-stairs/) - reachability
  with two fixed "steps" instead of a word set.
- [0300 - Longest Increasing Subsequence](../0300-longest-increasing-subsequence/)
  - another DP whose inner loop scans a set of candidate predecessors.
