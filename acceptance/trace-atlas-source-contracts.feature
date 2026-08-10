Feature: TRACE ATLAS source contracts
  Background:
    Given the TRACE ATLAS theme stylesheet and learning shell are loaded

  Scenario Outline: Semantic theme pairs meet their source-level contrast contract
    When I resolve "<foreground>" against "<background>" in the "<theme>" theme
    Then the source-level contrast ratio is at least "<minimum>"

    Examples:
      | theme | foreground                 | background                   | minimum |
      | light | --ta-fixed-action-ink       | --ta-fixed-action-cream      | 4.5     |
      | dark  | --ta-fixed-action-ink       | --ta-fixed-action-cream      | 4.5     |
      | light | --ta-fixed-action-ink       | --ta-fixed-action-saffron    | 4.5     |
      | dark  | --ta-fixed-action-ink       | --ta-fixed-action-saffron    | 4.5     |
      | light | --ta-code-control-ink       | --ta-code-control-surface    | 4.5     |
      | dark  | --ta-code-control-ink       | --ta-code-control-surface    | 4.5     |
      | light | --ta-control-rule           | --ta-paper-raised            | 3.0     |
      | dark  | --ta-control-rule           | --ta-paper-raised            | 3.0     |
      | light | --search-input-color        | --search-input-background-color | 4.5  |
      | dark  | --search-input-color        | --search-input-background-color | 4.5  |
      | light | --search-input-placeholder-color | --search-input-background-color | 4.5 |
      | dark  | --search-input-placeholder-color | --search-input-background-color | 4.5 |
      | light | --search-result-item-color  | --ta-paper-raised            | 4.5     |
      | dark  | --search-result-item-color  | --ta-paper-raised            | 4.5     |
      | light | --search-result-heading-color | --ta-paper-raised           | 4.5     |
      | dark  | --search-result-heading-color | --ta-paper-raised           | 4.5     |
      | light | --search-result-keyword-color | --search-result-keyword-background | 4.5 |
      | dark  | --search-result-keyword-color | --search-result-keyword-background | 4.5 |
      | light | --ta-search-glyph-ink       | --search-input-background-color | 3.0  |
      | dark  | --ta-search-glyph-ink       | --search-input-background-color | 3.0  |
      | light | --search-clear-icon-color1  | --search-clear-icon-color2     | 3.0  |
      | dark  | --search-clear-icon-color1  | --search-clear-icon-color2     | 3.0  |
      | light | --ta-ink                    | --table-row-odd-background   | 4.5     |
      | dark  | --ta-ink                    | --table-row-odd-background   | 4.5     |
      | light | --ta-ink                    | --table-row-even-background  | 4.5     |
      | dark  | --ta-ink                    | --table-row-even-background  | 4.5     |
      | light | --ta-code-control-ink       | --ta-code                    | 4.5     |
      | dark  | --ta-code-control-ink       | --ta-code                    | 4.5     |

  Scenario: Themeable integrations use complete semantic color contracts
    Then the search contract owns input, placeholder, glyph, clear, result, and keyword colors in both themes
    And both table row surfaces are owned
    And code-surface fallback and focus treatments use code-surface ink
    And dashboard action boundaries use the control rule in every state

  Scenario: Theme state remains independent from learner progress
    Then the theme storage key is separate from learner state
    And theme switching suppresses mixed transition frames
    And a persisted checkpoint reveal restores the revealed treatment without forcing its disclosure open
