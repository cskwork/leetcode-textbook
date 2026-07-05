# TEMPLATE - Per-Problem Folder Specification

Every problem folder in this book MUST follow this exact structure. Subagents use this as the
contract. Deviating from it breaks the book's consistency.

## Folder layout (mandatory)

```
patterns/XX-pattern-name/NNNN-problem-slug/
+- README.md           # teaching notes (problem + intuition + pseudocode + walkthrough)
+- Solution.java       # the LeetCode solution class, clean
+- SolutionTest.java   # runnable test with main(), no external deps
```

`NNNN` = LeetCode problem number (zero-padded to 4 digits).
`problem-slug` = lowercase-hyphenated problem name.

## README.md (mandatory sections, in this order)

```markdown
# NNNN - Problem Name

**Difficulty:** Easy | Medium
**Pattern:** Pattern Name
**LeetCode:** https://leetcode.com/problems/problem-name/

## Problem

<Restate the problem in plain English. Include the exact signature and 1-2 examples verbatim
from LeetCode. Beginners must be able to understand the task without leaving the page.>

Example:
    Input: nums = [2,7,11,15], target = 9
    Output: [0,1]

## Intuition

<Why does this pattern fit? What is the first thought a strong solver has? One or two short
paragraphs. Reference the pattern's trigger signals from 01-patterns-overview.md.>

## Pseudocode

<Language-neutral steps. NO Java syntax. Use indentation for blocks. Keep it under 20 lines.
This is the bridge from intuition to code -- a beginner who reads only this should be able to
re-implement in any language.>

    function solve(input):
        initialize hash structure
        for each element x in input:
            if complement exists in structure:
                return indices
            add x to structure

## Java Solution

<Inline the solution, then walk through each non-obvious line. Reference line numbers if
helpful. Do NOT just dump code; explain the translation from pseudocode.>

    [code block: the same content as Solution.java]

<Then 3-6 sentences explaining the Java-specific choices: why HashMap not HashSet, why we check
before put, what the early return does, etc.>

## Complexity

    Time:  O(?)  -- one-line reason
    Space: O(?)  -- one-line reason

## Dry-Run

<Execute the algorithm on one concrete input. Show the state of every variable at each step.
Format as a small table or numbered list. This is what makes the algorithm click for beginners.>

Step-by-step on input nums = [2,7,11,15], target = 9:
  1. seen = {}, i = 0, nums[0] = 2, complement = 9-2 = 7, 7 not in seen, put {2:0}
  2. i = 1, nums[1] = 7, complement = 9-7 = 2, 2 IS in seen at index 0, return [0,1]

## Common mistakes

<3-5 bullets of the mistakes beginners actually make on this problem: off-by-one, wrong
equality, forgetting the empty case, integer overflow, etc.>

## Related problems

<Link to 2-3 other problems in this book that reinforce the same pattern.>

- [0024 - Swap Nodes in Pairs](../XX-.../0024-...) - same pattern, different shape
```

## Solution.java (mandatory rules)

- Single class `Solution` with the exact LeetCode method signature.
- No `package` statement (so tests compile from any folder).
- No external imports beyond `java.util.*`.
- Comments only where the *why* is non-obvious; never restate what the code does.
- If LeetCode provides a node class (`ListNode`, `TreeNode`), define it in `Solution.java`
  guarded so it does not conflict when the test file also defines it. Use the convention below.

### Node class convention (for Linked List / Trees / Tries)

When a problem needs `ListNode`/`TreeNode`/`TrieNode`, define them as top-level classes in
`Solution.java`:

```java
// Solution.java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

class Solution {
    public ListNode reverseList(ListNode head) { ... }
}
```

Then in `SolutionTest.java` do NOT redefine them -- just compile both files together.

## SolutionTest.java (mandatory rules)

- A single class `SolutionTest` with `public static void main(String[] args)`.
- No JUnit, no TestNG, no external libraries -- only `java.util.*`.
- Test 3-6 cases covering: the LeetCode examples, at least one edge case (empty input, single
  element, all-equal, etc.), and at least one larger case.
- Print `"<passed>/<total> tests passed"` at the end and `"All tests passed."` only when all
  pass.
- Each failing case must print `"FAIL: <case name> -> <actual>"` so diagnosis is easy.

## Naming conventions

- Folder name: `NNNN-slug` (e.g. `0217-contains-duplicate`).
- Slug: lowercase, hyphens, no punctuation.
- Solution method name: exactly the LeetCode name (e.g. `twoSum`, `reverseList`).

## What subagents MUST do before reporting done

1. Write all three files following this template exactly.
2. Compile from inside the folder: `javac Solution.java SolutionTest.java`.
3. Run: `java SolutionTest`.
4. Confirm the output contains `All tests passed.`
5. If any test fails, FIX the solution and re-run. Do not weaken tests.
6. Report the exact commands run and their output in the final message.
