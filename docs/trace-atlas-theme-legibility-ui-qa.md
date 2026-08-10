# TRACE ATLAS theme legibility — UI-only QA suite

This suite exercises only browser-visible controls and pages. It does not call a project API or mutate storage directly.

## Test setup

- Use an isolated real Chromium context against `http://127.0.0.1:8765`.
- Run `scripts/run-trace-atlas-theme-legibility-ui-qa.sh` against the dedicated server to execute the repeatable Ego Browser checks and worktree-to-HTTP hash guard.
- Run every applicable case at 1440×900 and 390×844 CSS pixels.
- Test dark and light themes using the visible theme control. Start each theme run with a reload to verify persistence.
- Record uncaught exceptions, unhandled rejections, `console.error`, and failed resources from first navigation through the final dialog close.
- For each visible text sample, resolve transparent backgrounds through its painted ancestors and calculate WCAG relative-luminance contrast from computed foreground and effective background colors. Require 4.5:1 for normal text and 3:1 for large text. Require 3:1 for visible focus indicators and control boundaries needed to identify a control.
- At every checkpoint, require `document.documentElement.scrollWidth <= window.innerWidth`. Internal horizontal scrolling is acceptable only in the execution rail, table wrapper, code block, sidebar, or dialog body.

## QA-001 — Cover and dashboard

1. Open `#/`, activate dark theme, and reload.
2. Check cover text and every visible action in default, hover, keyboard-focus, active, and disabled states.
3. Check dashboard title, summary, progress, pattern cards, Continue, and Random unseen lesson in the same applicable states.
4. Verify that the action labels never inherit a light foreground over `#fff8e8` or the saffron hover surface.
5. Repeat in light theme.

Expected:

- All text and required control boundaries meet the setup thresholds at both viewports.
- Controls stay inside their containers without clipped labels, overlap, or root overflow.
- The cover and dashboard retain their existing content, links, and destinations.

## QA-002 — Representative lesson shell and content

1. Open `#/patterns/01-arrays-hashing/0001-two-sum/README` in dark theme.
2. Check the breadcrumb, lesson heading and prose, inline code, toolbar, all three status controls, lesson actions, five-station execution rail, Java block, copy control, checkpoints, dry-run table, adjacent navigation, sidebar, and fixed theme control.
3. Inspect every table header and cell, including both alternating row backgrounds.
4. Inspect every visible syntax-token class and the copy control in default, hover, focus, Copied, and Copy failed states.
5. Confirm that no rendered code character lies under the copy control; long code must remain reachable through the code block's own scrolling.
6. Expand a checkpoint, verify its answer and revealed treatment, collapse it, and reload.
7. Repeat in light theme and at both viewports.

Expected:

- Alternating table cells never show light theme row backgrounds behind light text.
- Copy-control text meets 4.5:1 and does not overlap code.
- The rail may scroll internally on mobile; the page itself never scrolls horizontally.
- Checkpoint reveal persists after reload and does not change the lesson to Completed.

## QA-003 — Sidebar and search

1. On the Two Sum lesson, open the sidebar with its visible Menu control at mobile width; leave it visible at desktop width.
2. Keyboard-focus the search field and enter `Two Sum`.
3. Check the field text, placeholder, search icon, clear control, result titles, snippets, empty state if shown, and every highlighted query fragment.
4. Scroll the result list, open the exact `1 - Two Sum` result, return, clear the query, and close the mobile sidebar.
5. Repeat in light theme.

Expected:

- Search text, placeholder, results, and highlighted fragments meet text thresholds; the search and clear icons meet 3:1.
- Results remain contained and operable inside the sidebar with no clipped text, overlap, or root overflow.
- The selected result returns to the canonical Two Sum lesson route.

## QA-004 — Walkthrough dialog

1. From the Two Sum toolbar, open the interactive walkthrough with a mouse and then with the keyboard.
2. Check the backdrop, panel, header, title, close control, dialog body, and embedded walkthrough at both viewports and themes.
3. Tab forward and backward through all dialog controls, then press Escape.
4. Reopen the dialog, resize between the two viewports, and close it with the visible Close control.

Expected:

- Focus begins inside the dialog, cannot leave it while open, and returns to the launch control after close.
- The title and Close control do not collide; the panel and embedded content remain within the viewport.
- Walkthrough labels and controls are readable, and no fixed walkthrough control is clipped or overlaps another control.
- The background is inert while open, body scrolling is restored after close, and there is no root overflow.

## QA-005 — Theme transition, state, accessibility, and errors

1. On both `#/` and the Two Sum route, switch light→dark and dark→light using the visible theme control.
2. At 0, 70, 160, and 300 ms after each click, measure all currently visible samples from QA-001 through QA-004.
3. Set Two Sum to Studying, reveal one checkpoint, and change one pattern expansion through the UI. Toggle theme and reload.
4. Expand every pattern group through the sidebar and count the user-visible lesson links and distinct destinations.
5. Repeat the navigation with keyboard only, beginning with the skip link.
6. Enable reduced motion before reload and repeat theme switching, sidebar opening, checkpoint reveal, and dialog open/close.
7. Review the recorded browser-error stream.

Expected:

- Every sampled transition frame meets the contrast thresholds; no old-theme flash or mixed foreground/background pair appears.
- Theme selection, Studying status, checkpoint reveal, sidebar expansion, and the current route survive reload independently.
- The sidebar exposes 16 ordered pattern groups and exactly 100 unique Problem Lesson destinations.
- Focus is always visible, the skip link reaches lesson content, and reduced-motion transitions finish within one animation frame.
- There are no browser exceptions, console errors, unhandled rejections, or failed resources.
