(ns trace-atlas.theme-legibility-spec
  (:require [speclj.core :refer :all]
            [trace-atlas.theme-contract :as contract]))

(def stylesheet (slurp "assets/atlas-theme.css"))
(def shell-script (slurp "assets/atlas-shell.js"))

(defn custom-property [name]
  (contract/resolve-property stylesheet "light" name))

(describe "TRACE ATLAS semantic color contracts"
  (it "computes contrast from independent WCAG worked values"
    (should= 21.0 (contract/contrast "#000000" "#ffffff")))

  (it "resolves dark theme overrides on top of root tokens"
    (should= "#f5efd9" (contract/resolve-property stylesheet "dark" "--ta-ink"))
    (should= "#173d32" (contract/resolve-property stylesheet "light" "--ta-ink")))

  (it "keeps fixed cream and saffron action surfaces readable in every theme"
    (let [ink (custom-property "--ta-fixed-action-ink")
          cream (custom-property "--ta-fixed-action-cream")
          saffron (custom-property "--ta-fixed-action-saffron")]
      (should-not-be-nil ink)
      (should-not-be-nil cream)
      (should-not-be-nil saffron)
      (should (>= (contract/contrast ink cream) 4.5))
      (should (>= (contract/contrast ink saffron) 4.5))
      (should-contain "color: var(--ta-fixed-action-ink)" stylesheet)))

  (it "keeps code controls readable on their fixed dark surface"
    (let [ink (custom-property "--ta-code-control-ink")
          surface (custom-property "--ta-code-control-surface")]
      (should-not-be-nil ink)
      (should-not-be-nil surface)
      (should (>= (contract/contrast ink surface) 4.5))))

  (it "declares a stronger boundary token for identifiable controls"
    (let [rule (custom-property "--ta-control-rule")]
      (should-not-be-nil rule)
      (should (>= (contract/contrast rule (custom-property "--ta-paper-raised")) 3.0))
      (should-contain "border-color: var(--ta-control-rule)" stylesheet)))

  (it "switches semantic theme tokens without mixed transition frames"
    (should-contain "ta-theme-switching" shell-script)
    (should-contain "html.ta-theme-switching" stylesheet)
    (should-contain "transition-property: none !important;" stylesheet))

  (it "owns both table row surfaces instead of inheriting plugin colors"
    (should-contain "--table-row-odd-background:" stylesheet)
    (should-contain "tr:nth-child(odd) td" stylesheet)
    (should-contain "tr:nth-child(even) td" stylesheet))

  (it "owns the complete search color contract"
    (doseq [property ["--search-input-background-image:"
                      "--search-input-color:"
                      "--search-input-placeholder-color:"
                      "--search-clear-icon-color1:"
                      "--search-clear-icon-color2:"
                      "--search-result-item-color:"
                      "--search-result-heading-color:"
                      "--search-result-keyword-background:"
                      "--search-result-keyword-color:"]]
      (should-contain property stylesheet)))

  (it "keeps fallback copy on the dialog's dark code surface"
    (should-contain ".ta-dialog__fallback {" stylesheet)
    (should-contain "color: var(--ta-code-ink);" stylesheet)
    (should-contain "color: var(--ta-code-control-ink);" stylesheet)
    (should-contain ".docsify-copy-code-button:focus-visible" stylesheet)
    (should-contain "outline-color: var(--ta-code-control-ink) !important;" stylesheet))

  (it "restores persisted checkpoint treatment without reopening the disclosure"
    (should-contain "if (revealed[id]) detail.classList.add('ta-checkpoint--revealed')" shell-script)
    (should-contain "markCheckpointRevealed(route, id)" shell-script)
    (should-be-nil (re-find #"detail\.open\s*=" shell-script))
    (should-be-nil (re-find #"detail\.setAttribute\(['\"]open" shell-script)))

  (it "removes both duration and delay under reduced motion"
    (should-contain "animation-delay: 0s !important;" stylesheet)
    (should-contain "transition-delay: 0s !important;" stylesheet)))
