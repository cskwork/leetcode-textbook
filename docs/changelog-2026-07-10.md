# Changelog - 2026-07-10

## Decision: add progressive, beginner-friendly quizzes throughout the book

**Context.** The user asked to make the course more beginner-friendly by adding quizzes "in
between" concepts, ramping from the simplest question to harder ones, and to use subagents.

**Goal.** Turn passive reading into active retrieval. Every concept a learner meets should be
immediately testable, with the difficulty of each block ramping so a beginner can always answer
the first question and is stretched by the last.

## What was added

- **Two "Checkpoint" quiz blocks in every problem README** (all 100 problems):
  - **Checkpoint A** -- inserted after *Intuition*, before *Pseudocode*. Two questions: a
    **recall** question (Bloom 1) and a **comprehend/apply** question (Bloom 2).
  - **Checkpoint B** -- inserted after *Dry-Run*, before *Common mistakes*. Three questions: an
    **apply** trace on a *new* input (Bloom 2-3), an **analyze** edge-case question (Bloom 3-4),
    and a **transfer**/"how would you adapt this" question (Bloom 4, open-ended).
- **One "Pattern Mastery Quiz" at the end of every pattern README** (all 16 patterns): 4-5
  questions ramping recall -> pattern-recognition (given a brand-new problem statement, which
  tool/variant fits?) -> apply -> open-ended design. This is the gate to move to the next pattern.
- **All answers are collapsible** via `<details><summary>Show answer</summary>` so self-study works
  on GitHub (and stays readable in a plain text editor).

Total: **100 Checkpoint A + 100 Checkpoint B + 16 Pattern Mastery Quiz = 580 question/answer
blocks.**

## Why this design (pedagogy)

- **Retrieval practice beats re-reading.** Forcing a guess before revealing the answer is the
  single most effective study technique; the collapsible answers enforce a genuine attempt.
- **Placement checks the right thing at the right time.** Checkpoint A checks *concept* (did you
  understand why the pattern fits?); Checkpoint B checks *execution* (can you trace a fresh input
  and spot an edge case?).
- **The difficulty ramp is the core feature.** Q1 of every block is answerable by a beginner who
  just read the preceding section; each later question is harder. This keeps beginners unstuck
  while still stretching them.
- **Apply questions always use a NEW input**, never the dry-run's input, so the learner must
  actually reason rather than pattern-match to the worked example.
- **Every answer is verified against the actual pseudocode/Java** in its README (subagents traced
  each correct option before writing).

## Architecture decisions

### Spec-first, reference-first, then parallel subagents

1. **Wrote the binding spec into `TEMPLATE.md`** -- two new sections ("Checkpoint quizzes" and
   "Pattern-README mastery quiz") define exact placement, format, the recall->transfer ramp, the
   `<details>` answer wrapper, and the <=25-line cap. This is the contract every subagent followed.
2. **Built Pattern 1 (Arrays & Hashing) by hand as the canonical reference** -- all 8 problem
   READMEs plus the pattern mastery quiz -- so subagents had a concrete, byte-exact model to copy
   rather than only a prose spec.
3. **Dispatched 15 `general` subagents (one per remaining pattern) in three parallel waves of 5**,
   each given the spec pointer, the Pattern 1 reference, its exact folder list, the hard rules
   (edit only `.md`; preserve all existing prose; ramp simplest-first; verify the correct answer
   against the code), and a mandatory verification + report step.
4. **The orchestrator (not subagents) wrote** `TEMPLATE.md` (spec), `00-how-to-use-this-book.md`
   ("How to use the quizzes" section), the Pattern 1 reference, and this changelog.

### Format contract (mandatory across all 100 problems)

- Headers: `### Checkpoint A -- <title>` and `### Checkpoint B -- <title>`.
- Question labels carry the Bloom level visibly: `**Q1 (recall).**`, `**Q2 (comprehend).**`,
  `**Q1 (apply).**`, `**Q2 (analyze).**`, `**Q3 (transfer).**`.
- Multiple-choice options are `- a) ...` bullets.
- Every answer wrapped exactly as:
  `<details><summary>Show answer</summary>` + blank line + `**(x)** -- one-sentence why.` + blank
  line + `</details>`.
- No new jargon introduced inside a quiz; plain-English voice; no emojis.

## Alternatives rejected

- **A single separate `quizzes/` directory.** Rejected: quizzes would be divorced from the concept
  they test, breaking the "check immediately after reading" rhythm. Inline checkpoints keep the
  retrieval moment next to the teaching moment.
- **No answers / a separate answer key file.** Rejected: collapsible inline answers are better for
  self-study and avoid answer-key drift.
- **Free-text questions only (no multiple choice).** Rejected: MCQ lets a beginner attempt quickly
  and self-grade; the ramp ends in open-ended transfer/design questions for depth.
- **Re-implementing Pattern 1 via subagent too.** Rejected: hand-authoring the reference gave the
  subagents an exact format target and let the orchestrator tune the voice once.

## Verification evidence

Independent of subagent self-reports, the orchestrator ran from a clean state:

- **All 100 problem test suites compiled and passed**:
  `javac Solution.java SolutionTest.java && java SolutionTest` in every folder -> `100 passed,
  0 failed`. This matches the original 100/100 baseline, confirming the markdown-only edits broke
  no solution.
- **Coverage is exact**:
  - `### Checkpoint A --` appears in 100 problem READMEs (1 each).
  - `### Checkpoint B --` appears in 100 problem READMEs (1 each).
  - `## Pattern Mastery Quiz` appears in 16 pattern READMEs (1 each).
  - 580 `<details><summary>Show answer</summary>` blocks total = 100 problems x 5 questions + 16
    mastery quizzes x ~5 questions. Matches.
- **No `.java` file modified** vs HEAD (`git diff --name-only HEAD -- '*.java'` is empty).
- **No malformed markers**: 0 occurrences of `****` or escaped `**\(` across `patterns/`.
- Spot-checked apply/analyze/transfer traces (e.g. LC 200 grid `[1 0; 0 1]` -> 2 islands; LC 167
  `[1,3,4,5,7]` target 9 -> `[3,4]`; LC 70 recurrence) are accurate against their solutions.

## Files written/edited by the orchestrator

- `TEMPLATE.md` -- added the two binding quiz-spec sections (the subagent contract).
- `00-how-to-use-this-book.md` -- added "The checkpoint quizzes" section (how/when to use them).
- `patterns/01-arrays-hashing/README.md` -- Pattern Mastery Quiz.
- `patterns/01-arrays-hashing/*/README.md` (8 problems) -- Checkpoint A + B (the reference).
- `docs/changelog-2026-07-10.md` -- this file.

## Files edited by subagents (15 patterns)

- `patterns/02-two-pointers/` through `patterns/16-bit-manipulation/` -- every problem README got
  Checkpoint A + B; every pattern README got a Pattern Mastery Quiz. Each subagent verified its own
  Java tests passed before reporting; the orchestrator re-verified all 100 globally.
