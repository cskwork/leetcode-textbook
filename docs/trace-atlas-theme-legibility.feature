# TRACE ATLAS theme legibility 001
# TRACE ATLAS theme legibility 002
# TRACE ATLAS theme legibility 003
# TRACE ATLAS theme legibility 004
# TRACE ATLAS theme legibility 005
Feature: TRACE ATLAS theme legibility
  Learners can read and operate every shell surface in either theme without
  losing curriculum state, content, or navigation.

  Background:
    Given TRACE ATLAS is open in an isolated real browser
    And browser exceptions, console errors, and failed resources are being recorded

  # TRACE ATLAS theme legibility 001
  Scenario Outline: TRACE ATLAS theme legibility 001
    Given the viewport is "<viewport>"
    And the active theme is "<theme>"
    When I open "<route>"
    Then computed normal-text contrast is at least 4.5 to 1 on every "<surface group>" surface
    And computed large-text and visible control-boundary contrast is at least 3 to 1
    And no visible text, rendered code, label, or control is clipped or overlapped
    And only the rail, table, code block, sidebar, or dialog body may scroll horizontally within itself
    And the document does not scroll horizontally

    Examples:
      | viewport | theme | route                                                         | surface group                                                        |
      | 1440x900 | dark  | #/                                                            | cover and dashboard                                                   |
      | 1440x900 | dark  | #/patterns/01-arrays-hashing/0001-two-sum/README             | lesson, sidebar, toolbar, rail, code, checkpoints, search, and table |
      | 390x844  | dark  | #/                                                            | cover and dashboard                                                   |
      | 390x844  | dark  | #/patterns/01-arrays-hashing/0001-two-sum/README             | lesson, sidebar, toolbar, rail, code, checkpoints, search, and table |
      | 1440x900 | light | #/                                                            | cover and dashboard                                                   |
      | 1440x900 | light | #/patterns/01-arrays-hashing/0001-two-sum/README             | lesson, sidebar, toolbar, rail, code, checkpoints, search, and table |
      | 390x844  | light | #/                                                            | cover and dashboard                                                   |
      | 390x844  | light | #/patterns/01-arrays-hashing/0001-two-sum/README             | lesson, sidebar, toolbar, rail, code, checkpoints, search, and table |

  # TRACE ATLAS theme legibility 002
  Scenario Outline: TRACE ATLAS theme legibility 002
    Given the viewport is "<viewport>"
    And the active theme is "<theme>"
    When I perform "<workflow>"
    Then every default, hover, focus, active, disabled, selected, expanded, and open state that appears has compliant computed contrast
    And the state change introduces no clipping, overlap, document-level horizontal overflow, or mismatched foreground and background

    Examples:
      | viewport | theme | workflow                                                   |
      | 1440x900 | dark  | use every cover and dashboard action                      |
      | 390x844  | dark  | use every cover and dashboard action                      |
      | 1440x900 | dark  | open the sidebar and search for Two Sum                   |
      | 390x844  | dark  | open the sidebar and search for Two Sum                   |
      | 1440x900 | dark  | copy the Two Sum Java block and reveal a checkpoint       |
      | 390x844  | dark  | copy the Two Sum Java block and reveal a checkpoint       |
      | 1440x900 | dark  | open and close the Two Sum interactive walkthrough        |
      | 390x844  | dark  | open and close the Two Sum interactive walkthrough        |
      | 1440x900 | light | repeat the cover, lesson, search, copy, and dialog states |
      | 390x844  | light | repeat the cover, lesson, search, copy, and dialog states |

  # TRACE ATLAS theme legibility 003
  Scenario Outline: TRACE ATLAS theme legibility 003
    Given the viewport is "<viewport>"
    And I am viewing "<route>" in the "<starting theme>" theme
    When I toggle to the other theme
    Then the root theme, compatibility class, color scheme, and visible toggle state agree
    And computed contrast remains compliant at 0, 70, 160, and 300 milliseconds after the toggle
    And no old-theme surface, inherited text color, flash, clipping, or overlap appears
    And the selected theme remains active after reload

    Examples:
      | viewport | route                                             | starting theme |
      | 1440x900 | #/                                                | dark           |
      | 1440x900 | #/patterns/01-arrays-hashing/0001-two-sum/README | light          |
      | 390x844  | #/                                                | light          |
      | 390x844  | #/patterns/01-arrays-hashing/0001-two-sum/README | dark           |

  # TRACE ATLAS theme legibility 004
  Scenario: TRACE ATLAS theme legibility 004
    Given I set Two Sum to Studying through the lesson toolbar
    And I reveal one Two Sum checkpoint
    And I change one pattern expansion in the sidebar
    When I change the theme and reload the lesson
    Then the lesson remains Studying
    And the revealed checkpoint remains revealed without becoming mastery
    And the sidebar expansion and lesson route remain unchanged
    And the selected theme remains active after reload
    And the curriculum still exposes 16 ordered patterns and 100 unique Problem Lesson routes

  # TRACE ATLAS theme legibility 005
  Scenario Outline: TRACE ATLAS theme legibility 005
    Given the viewport is "<viewport>"
    And the active theme is "<theme>"
    And reduced motion is enabled before TRACE ATLAS loads
    When I navigate by keyboard through the skip link, sidebar, toolbar, execution rail, checkpoint, theme control, and walkthrough dialog
    Then every focused control has a visible focus indicator
    And the dialog traps focus, closes with Escape, and restores focus to its opener
    And nonessential theme and component motion completes within one animation frame
    And no browser exception, console error, or failed resource is recorded

    Examples:
      | viewport | theme |
      | 1440x900 | dark  |
      | 390x844  | dark  |
      | 1440x900 | light |
      | 390x844  | light |
