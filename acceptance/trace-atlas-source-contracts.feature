# mutation-stamp: sha256=bc546520d11493217a60982a440ee1e1847e679783db0a1326938f30889120db
# acceptance-mutation-manifest-begin
# {"version":1,"tested_at":"2026-08-10T16:05:52.805013Z","feature_name":"TRACE ATLAS source contracts","feature_path":"acceptance/trace-atlas-source-contracts.feature","background_hash":"3c1f5a90c2306c8ba3e30cb9f451d440f3e99bc0cdbd30dace2079d838ff5ffa","implementation_hash":"sha256:1846daf1feddcb2b1b7a180a1fb858f8fc8cd80f0cc2d15b7497e5834929dc5d","scenarios":[{"index":0,"name":"Semantic theme pairs meet their source-level contrast contract","scenario_hash":"7a9362902e48aced23c371eab43f8b568636edeebcb66d25c06768a3ed120c7d","mutation_count":112,"result":{"Total":112,"Killed":112,"Survived":0,"Errors":0},"tested_at":"2026-08-10T15:56:22.390427Z"}]}
# acceptance-mutation-manifest-end

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
