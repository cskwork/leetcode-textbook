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

## Concepts used

<A 2-4 line callout listing the prerequisite ideas this problem relies on, each defined in plain
English in one sentence, with a link to docs/10-glossary.md#anchor on first mention. Example:

- **Hash map** -- a key->value lookup table, O(1) average. [glossary](../../../docs/10-glossary.md#hash-map)
- **Single pass** -- walking the array once, doing constant work per element.

If the problem needs no advanced concept (e.g. plain array iteration), say so explicitly:
"This problem needs only basic array iteration and an `if` check." This callout exists so a
beginner can see, before reading anything else, what ideas they must already understand.>

## Problem

<Restate the problem in plain English. Include the exact signature and 1-2 examples verbatim
from LeetCode. Beginners must be able to understand the task without leaving the page.>

Example:
    Input: nums = [2,7,11,15], target = 9
    Output: [0,1]

## Intuition

<BEGINNER-FIRST CONTRACT -- this is the most important section and must follow these rules:

1. **Lead with a concrete analogy or real-world scenario** BEFORE any algorithmic framing.
   Example for DP: "Imagine climbing a staircase -- to reach step n, your last move was from
   step n-1 (a single step) or step n-2 (a double step)."
2. **Define every technical term on first use** in plain English, and link it to
   docs/10-glossary.md. Never use words like "recurrence", "state", "DFS", "monotonic",
   "subproblem" without a one-sentence definition the first time they appear.
3. **Walk through the smallest meaningful example FIRST** (e.g. n=3 for Fibonacci, an array of
   3 elements for Two Sum). Show the reader the actual values and the actual answer before
   generalizing.
4. **Only then state the general rule.** Move from concrete (specific numbers) to abstract
   (the pattern/formula).
5. **Replace handwaves with reasoning.** "Trust the recursion" / "by definition" / "leap of
   faith" are forbidden on their own -- if you rely on recursive reasoning, EXPLICITLY walk
   through why the assumption is valid for this problem.
6. **Reference other problems/patterns by name only with a one-sentence explanation** of what
   they are. Never say "this mirrors LCS" without saying what LCS is.
7. **No jargon dumps.** If a paragraph has 3+ technical terms, break it up and define each.

Target length: 2-4 short paragraphs. The goal is that a reader who has read the glossary and
the orientation docs can follow this section without ever feeling lost.>

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

## Checkpoint quizzes (mandatory, beginner-friendly retrieval practice)

Every problem README MUST contain two short "Checkpoint" quiz blocks inserted at natural break
points. The goal is **active retrieval**: the reader pauses, recalls, then self-checks. Questions
ramp simplest-first (recall -> apply -> analyze/transfer) so a beginner can always answer Q1 and
is stretched by the last question.

### Placement (exactly two blocks, at these spots)

1. **Checkpoint A** -- insert AFTER the "Intuition" section and BEFORE "Pseudocode". Two questions:
   - Q1: **Recall** (Bloom 1) -- "what/which" about a concept just explained. Multiple choice, 3 options.
   - Q2: **Comprehend/apply** (Bloom 2) -- a tiny trace or "why does this work" on the smallest example. Multiple choice, 3-4 options.
2. **Checkpoint B** -- insert AFTER the "Dry-Run" section and BEFORE "Common mistakes". Three questions:
   - Q1: **Apply** (Bloom 2-3) -- trace a NEW small input (not the dry-run input) and predict state/output.
   - Q2: **Analyze** (Bloom 3-4) -- an edge case, off-by-one, or "what breaks if..." question.
   - Q3: **Transfer** (Bloom 4) -- a variant / "how would you solve <related twist>" -- open ended, short.

### Format rules (mandatory, for consistency across all 100 problems)

- Header is `### Checkpoint A -- <short title>` and `### Checkpoint B -- <short title>`.
- A one-line lead-in telling the reader to attempt before revealing, e.g.
  "Pause and answer before expanding. Wrong guesses teach more than fast right ones."
- Every question has a label like `**Q1 (recall).**` / `**Q2 (apply).**` / `**Q3 (transfer).**`
  so the difficulty ramp is visible.
- Multiple-choice options are a bulleted list `- a) ...`, `- b) ...`, etc.
- Each answer is wrapped in a collapsible block so self-study works:

  ```markdown
  <details><summary>Show answer</summary>

  **(b)** -- one-sentence why.

  </details>
  ```

- Keep the whole block under ~25 lines. Quizzes are retrieval nudges, not exams. If a problem is
  genuinely tiny (e.g. a 5-line solution), Checkpoint B may drop Q3, but never drop Checkpoint A.
- Write answers in the same plain-English voice as the rest of the README. Define no new jargon in
  a quiz; if you need a term it was already defined in Intuition/Concepts.
- Never ask the reader to write more than ~3 lines of code. Transfer questions are conceptual
  ("what would change in the approach"), not full re-implementations.

### Example (the shape to copy)

```markdown
### Checkpoint A -- Spot the data structure

Pause and pick before expanding.

**Q1 (recall).** For this problem the fastest "have I seen X?" check needs:
- a) a sorted array + binary search
- b) a hash set
- c) a second nested loop

<details><summary>Show answer</summary>

**(b)** -- a hash set answers membership in O(1); the nested loop is the O(n^2) brute force.

</details>

**Q2 (apply).** With input `[2,5,1,5]`, what is in the set just before the loop reads the last `5`?
- a) {}
- b) {2,5}
- c) {1,2,5}

<details><summary>Show answer</summary>

**(c)** -- after steps 1-3 we have added 2, 5, and 1. The duplicate is detected on reading the last 5.

</details>
```

## Pattern-README mastery quiz (mandatory)

Every `patterns/XX-name/README.md` (the pattern intro file) MUST end with a
`## Pattern Mastery Quiz` block containing 4-5 questions that ramp across the whole pattern:

- 1x **recall** -- restate the pattern's core idea or trigger signal.
- 1-2x **pattern recognition** -- given a NEW short problem statement (not one already in the
  folder), which variant/tool of this pattern fits? Multiple choice.
- 1x **apply** -- predict output/state on a small input for a representative problem.
- 1x **design** -- open ended: "sketch the approach (not code) for <twist on the pattern>".

Same `<details>` answer format and plain-English voice as the per-problem checkpoints. This block
goes AFTER the existing "Common pitfalls" section and BEFORE the closing "start the first problem"
link.

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
