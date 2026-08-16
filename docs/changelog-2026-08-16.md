# Changelog — 2026-08-16 — Expedition redesign

## Why

The TRACE ATLAS shell rendered every Problem Lesson as one long page. Beginners
reported the wall-of-text feel as the main source of overwhelm. This release
redesigns the learning experience around teaching little by little, in the
spirit of Duolingo-style bite-size progression, while keeping Markdown as the
source of truth and keeping every existing source-level contract green.

## What changed

1. **Guided Lesson Player (steps mode).** A Problem Lesson now plays one Step at
   a time: Warm-up, The problem, Intuition, Pseudocode, Java, Complexity,
   Dry-run, Traps, Next up. A sticky quest bar shows progress ("3 / 9"), step
   pills allow free jumps, and a large Continue button advances. Advancing marks
   the lesson Studying; Finish lesson marks it Completed, fires confetti, and
   shows a completion card with the next-lesson link.
2. **Reading Mode toggle.** "One page" restores the classic scroll view with the
   Learning Station rail; "Step mode" returns to the player. The choice is
   persisted per device. Printing always renders every step.
3. **XP and streaks.** Finishing a lesson awards 20 XP (once per lesson);
   first-time Checkpoint reveals award 2 XP. Consecutive study days build a
   streak. Both are device-local fields inside the existing versioned learner
   state (`trace-atlas:v1:learner-state`) — no accounts, no network.
4. **Expedition dashboard.** The home cover now renders a "Jump back in" card
   plus sixteen unit cards, each with a progress meter and a path of lesson
   nodes (open / studying / completed / up-next-pulsing). The sidebar shows
   status dots per lesson.
5. **Canvas UI cover effect.** The cover mounts Canvas UI's GlyphRain
   (`assets/glyph-rain.js`, vanilla build) as the page's single dominant canvas
   effect, in overlay-fallback-first wiring. `assets/atlas-fx.js` also provides
   the completion confetti. Both honor `prefers-reduced-motion` and degrade to
   the plain themed page when WebGL2 is unavailable.
6. **Visual redesign.** Same warm-paper palette (all contrast contracts
   unchanged and passing), new friendlier component language: larger radii,
   pressed-down chunky buttons, pill progress meters, quiz-card checkpoints,
   difficulty/pattern chips in a lesson brief, and a fixed dark cover that no
   longer inherits docsify-themeable's cover mask (that mask painted the cover
   cream in dark mode). docsify-themeable's `overflow` on `<main>` silently
   disabled `position: sticky` against window scroll; `main` now clips only
   the x axis so the quest bar actually sticks (the pre-redesign sticky
   toolbar had the same latent defect).
7. **Content.** `00-how-to-use-this-book.md` was rewritten around the new
   guided flow; the README lead now explains steps, XP, and streaks; the
   coverpage copy was rewritten; `CONTEXT.md` gained Lesson Player, Step,
   Reading Mode, XP, and Streak terms.

## What stayed untouched

- All 100 lesson Markdown files, solutions, and tests.
- The acceptance pipeline, speclj specs, and their contracts (verified below).
- Learner-state storage key, version, statuses, checkpoint semantics, station
  persistence keys (old saved stations still resolve to player steps).
- The walkthrough dialog, its focus containment, and the walkthrough manifest.

## Verification

- `node scripts/verify-site.mjs` — all PASS (100 lessons, 25 walkthroughs).
- `bb spec` — 43 examples, 0 failures.
- `bash scripts/run-acceptance.sh` — Acceptance passed: 30 executions.
- `bash scripts/run-trace-atlas-theme-legibility-ui-qa.sh` — pass: true,
  browserErrors: 0 (harness updated for the redesign: steps-mode geometry,
  one-page geometry, 16 units / 100 nodes, Reading Mode pinning, and a
  transition force-finish guard for the ego environment's frozen CSS
  transition clock).
- Playwright browser QA at 1440x900 and 390x844, light and dark: cover with
  GlyphRain, dashboard path, step flow Continue/Back, finish + confetti + XP,
  checkpoint XP, deep link into a hidden step, One page toggle with station
  rail, quest bar stickiness.
