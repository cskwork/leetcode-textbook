# 0125 - Valid Palindrome

**Difficulty:** Easy
**Pattern:** Two Pointers
**LeetCode:** https://leetcode.com/problems/valid-palindrome/

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

A palindrome reads the same forwards and backwards, so the natural move is the
Two-Pointer **opposite-ends** variant: one pointer at the left, one at the right,
walk them toward the middle, and at each step check that the characters match.

The only wrinkle is that the string is littered with spaces and punctuation that
must be ignored. So before each comparison we *skip* any character that is not a
letter or digit, and we compare case-insensitively. The pattern's trigger signals
match exactly: a palindrome question is a textbook opposite-ends job.

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
