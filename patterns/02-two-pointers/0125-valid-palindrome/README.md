# 0125 - Valid Palindrome

**Difficulty:** Easy
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/valid-palindrome/

## Concepts used

- **Two pointers** -- placing two indices into a string (or array) and moving them toward each other to compare characters, so we never need a nested loop. [glossary](../../../docs/10-glossary.md#two-pointers)
- **Array** -- a row of numbered slots holding values, each read in O(1) time by its position (index); a string works the same way, with `s.charAt(i)` reading slot `i`. [glossary](../../../docs/10-glossary.md#array)
- **Linear scan** -- walking through every element once, in order; here the two pointers together sweep the whole string exactly once. [glossary](../../../docs/10-glossary.md#linear-scan)

## Problem

Given a string `s`, return `true` if it is a palindrome considering only
alphanumeric characters and ignoring case, otherwise `false`. An empty string
counts as a palindrome.

Signature:

    boolean isPalindrome(String s)

Examples (verbatim from LeetCode):

    Input:  s = "A man, a plan, a canal: Panama"
    Output: true

    Input:  s = "race a car"
    Output: false

    Input:  s = " "
    Output: true

## Intuition

A palindrome is a word that reads the same forwards and backwards -- "racecar",
"mom", "noon". How would you check by eye? Put one finger on the first letter and
one on the last; if they match, slide both fingers one step inward; keep going.
If every finger-pair agrees before the fingers meet in the middle, it's a
palindrome. This finger trick is [two pointers](../../../docs/10-glossary.md#two-pointers):
one index (`left`) at the start, one (`right`) at the end, moving toward each other.

Let's watch it on the tiniest real case, `s = "mom"`:

- `left=0` points at `'m'`, `right=2` points at `'m'` -> match, move both inward.
- `left=1`, `right=1` -> the fingers have met in the middle, so stop. No mismatch
  was ever found, so it's a palindrome.

This problem adds two twists. First, the string is full of spaces and punctuation
(`','`, `':'`, `' '`) that we must **ignore** -- a comma should never count as a
mismatch. Second, uppercase and lowercase count as the same (`'A'` equals `'a'`).
The word for "a letter or a digit" is **alphanumeric** -- so `'a'` and `'7'` count,
but `' '` and `','` do not.

Here is the general rule with the skipping built in. At each step, if the
character under a finger is not alphanumeric, slide just that finger past it and
do not compare yet. Once both fingers sit on real letters or digits, lowercase
them and compare. If they differ, return `false` immediately. If they match, move
both inward. When the fingers meet or cross with no mismatch, every relevant pair
has agreed, so return `true`. Sweeping through the string once this way is a
[linear scan](../../../docs/10-glossary.md#linear-scan); because both fingers only
ever move forward, the whole check finishes in O(n) time.

### Checkpoint A -- The two-finger idea

Pause and pick before expanding. A wrong first guess teaches more than a fast right one.

**Q1 (recall).** This problem must ignore some characters before comparing. Which set does it keep (the "alphanumeric" ones)?
- a) Letters and digits only -- spaces and punctuation are skipped
- b) Only lowercase letters
- c) Everything except digits

<details><summary>Show answer</summary>

**(a)** -- "alphanumeric" means a letter or a digit. Spaces, commas, and colons are skipped; the two `continue` branches advance past them without comparing.

</details>

**Q2 (comprehend).** Trace `s = "Aa"` (length 2). Both characters are alphanumeric. What is the result, and why?
- a) `false` -- 'A' and 'a' are different characters
- b) `true` -- after lowercasing both are 'a', they match, then the pointers cross and the loop exits
- c) The loop never runs

<details><summary>Show answer</summary>

**(b)** -- `left=0`, `right=1`. After `toLowerCase`, 'A' becomes 'a' and equals 'a'. Both pointers step inward (`left=1`, `right=0`), `left < right` is now false, and control reaches the final `return true`.

</details>

## Pseudocode

```text
function isPalindrome(s):
    set left to first index of s
    set right to last index of s
    while left < right:
        if character at left is not alphanumeric:
            advance left
            continue
        if character at right is not alphanumeric:
            move right back one
            continue
        if lowercase(left char) is not equal to lowercase(right char):
            return false
        advance left
        move right back one
    return true
```

## Java Solution

```java
class Solution {
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            char l = s.charAt(left);
            char r = s.charAt(right);
            if (!Character.isLetterOrDigit(l)) {
                left++;
                continue;
            }
            if (!Character.isLetterOrDigit(r)) {
                right--;
                continue;
            }
            if (Character.toLowerCase(l) != Character.toLowerCase(r)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
```

We snapshot the two characters into `l` and `r` first so the two skip-checks
read cleanly. `Character.isLetterOrDigit` matches the problem's "alphanumeric"
exactly (it accepts Unicode letters, which LeetCode accepts too). Comparing
after `toLowerCase` handles the case-insensitive rule. The `continue` after
advancing one pointer restarts the loop so we re-validate the *new* character
before comparing — this is what keeps the skip logic simple.

## Complexity

    Time:  O(n)  -- each character is visited at most once as left or right advance.
    Space: O(1)  -- only two index variables are used; no copy of the string.

## Dry-Run

Step-by-step on `s = "A man, a plan, a canal: Panama"` (length 25, indices 0..24).
We show left, right, the characters considered, and the action. `M(0)` means
"match at this step", `SKIP` means a character was ignored.

| Step | left | right | left char | right char | Action |
|------|------|-------|-----------|------------|--------|
| 1 | 0 | 24 | 'A' | 'a' (last) | match -> left++, right-- |
| 2 | 1 | 23 | ' ' | 'm' | SKIP left (space) -> left++ |
| 3 | 2 | 23 | 'm' | 'm' | match -> left++, right-- |
| 4 | 3 | 22 | 'a' | 'a' | match -> left++, right-- |
| 5 | 4 | 21 | 'n' | 'm' | match? 'n' vs 'a'... |

(A full trace is 13 matches; the key point is that every compared pair is equal
and the pointers cross without finding a mismatch, so the function returns
`true`.) The loop exits when `left >= right`, at which point every relevant pair
has been verified.

### Checkpoint B -- Trace and stress it

**Q1 (apply).** Trace `s = "ab"`. What is returned?
- a) `true`
- b) `false`, because 'a' does not equal 'b'
- c) The loop never compares them

<details><summary>Show answer</summary>

**(b)** -- both characters are alphanumeric, no skipping happens, and `'a' != 'b'` after lowercasing, so the code hits `return false` on the first comparison.

</details>

**Q2 (analyze).** What happens if a skip branch advances its pointer but you forget the `continue` (or forget the `left++` / `right--`)?
- a) The pointer never moves past that character, so the same non-alphanumeric is re-read forever -- an infinite loop
- b) The character is wrongly compared -- a one-off wrong answer
- c) Nothing; the loop still progresses

<details><summary>Show answer</summary>

**(a)** -- without advancing (or without restarting the loop so the new character is re-checked), progress stalls. This is the exact "infinite loop" mistake called out in Common mistakes below.

</details>

**Q3 (transfer).** Suppose the task changed to: "return true if the string can be a palindrome after deleting at most one character." How would you adapt the approach?

<details><summary>Show answer</summary>

Keep the opposite-end two-pointer compare. On the FIRST mismatch, you get one deletion to spend, so try both options -- skip the left character, or skip the right character -- and check whether either remaining substring is a plain palindrome. The skeleton stays; you add a helper that checks a clean palindrome on a substring, and call it twice from the mismatch point.

</details>

## Common mistakes

- **Using `==` on `Character` objects.** Comparing boxed `Character`s with `==`
  breaks for values outside the cached range; always compare `char` primitives
  (as above) or use `.equals`.
- **Comparing without lowercasing.** `'A' != 'a'`, so a mixed-case palindrome
  like `"Aa"` would wrongly return `false`.
- **Treating only letters, not digits.** The problem keeps digits; a check like
  `Character.isLetter` alone would drop them and break inputs like `"0P"`.
- **Infinite loop from forgetting to advance.** If a skip branch does not move
  its pointer (or uses `continue` without `left++`), the loop never progresses.

## Related problems

- [0167 - Two Sum II](../0167-two-sum-ii/) - same opposite-ends shape, different
  goal (find a pair summing to a target).
- [0977 - Squares of a Sorted Array](../0977-squares-of-a-sorted-array/) -
  opposite ends again, this time writing output from the back.
- [0283 - Move Zeroes](../0283-move-zeroes/) - introduces the fast/slow variant.
