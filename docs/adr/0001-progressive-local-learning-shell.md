---
status: accepted
---

# Keep the learning shell static, progressive, and local-first

The production redesign keeps Markdown and Docsify as the source of truth, adds learner progress only in versioned browser storage, and lazy-loads an Interactive Walkthrough only when that Problem Lesson has a real visualization. This preserves all existing lesson routes and independent Java exercises while delivering an app-like guided curriculum without introducing accounts, a backend, or mandatory WebGL.

## Consequences

The textbook remains readable when optional enhancement code or visualization loading fails. A checked-in walkthrough manifest is the capability contract and verification must keep it aligned with real Problem Lesson routes and files; lesson entry never probes optional HTML. The iframe is created only after explicit launch, with a readable fallback when WebGL is unavailable. Progress is device-local and must be described that way; cross-device sync is outside this decision.
