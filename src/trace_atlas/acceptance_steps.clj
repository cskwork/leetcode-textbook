(ns trace-atlas.acceptance-steps
  (:require [clojure.string :as str]
            [trace-atlas.theme-contract :as contract]))

(defn require-fragments! [label source fragments]
  (doseq [fragment fragments]
    (when-not (str/includes? source fragment)
      (throw (ex-info (str label " is missing: " fragment)
                      {:label label :fragment fragment})))))

(defn rule-body [stylesheet selector]
  (contract/block-body
   stylesheet
   (re-pattern (str "(?s)" (java.util.regex.Pattern/quote selector) "\\s*\\{(.*?)\\}"))
   selector))

(defn parse-positive-number [value]
  (let [number (Double/parseDouble value)]
    (when (or (not (Double/isFinite number)) (not (pos? number)))
      (throw (ex-info (str "Expected a positive finite number, got: " value) {:value value})))
    number))

(def three-to-one-pairs
  #{["--ta-control-rule" "--ta-paper-raised"]
    ["--ta-search-glyph-ink" "--search-input-background-color"]
    ["--search-clear-icon-color1" "--search-clear-icon-color2"]})

(defn required-contrast [foreground background]
  (if (contains? three-to-one-pairs [foreground background]) 3.0 4.5))

(defn step-handler [text]
  (or
   (when (= text "the TRACE ATLAS theme stylesheet and learning shell are loaded")
     (fn [world]
       (assoc world
              :stylesheet (slurp "assets/atlas-theme.css")
              :shell (slurp "assets/atlas-shell.js"))))

   (when-let [[_ foreground background theme]
              (re-matches #"I resolve \"([^\"]+)\" against \"([^\"]+)\" in the \"([^\"]+)\" theme" text)]
     (fn [{:keys [stylesheet] :as world}]
       (assoc world
              :contrast (contract/contrast-for stylesheet theme foreground background)
              :foreground foreground
              :background background)))

   (when-let [[_ minimum]
              (re-matches #"the source-level contrast ratio is at least \"([^\"]+)\"" text)]
     (fn [{:keys [contrast foreground background] :as world}]
       (let [threshold (parse-positive-number minimum)
             required (required-contrast foreground background)]
         (when-not (= required threshold)
           (throw (ex-info (format "Contrast contract for %s on %s must be %.1f, got %.2f"
                                   foreground background required threshold)
                           {:foreground foreground
                            :background background
                            :minimum threshold
                            :required required})))
         (when (or (nil? contrast) (< contrast threshold))
           (throw (ex-info (format "Contrast %.2f is below %.2f" (or contrast 0.0) threshold)
                           {:contrast contrast :minimum threshold})))
         world)))

   (when (= text "the search contract owns input, placeholder, glyph, clear, result, and keyword colors in both themes")
     (fn [{:keys [stylesheet] :as world}]
       (let [root (contract/block-body stylesheet #"(?s):root\s*\{(.*?)\}" ":root")
             dark (contract/block-body stylesheet
                                       #"(?s)html\[data-ta-theme=\"dark\"\],\s*html\.dark-mode\s*\{(.*?)\}"
                                       "dark theme")]
         (require-fragments!
          "root search contract" root
          ["--search-input-background-color:"
           "--search-input-background-image:"
           "--search-input-color:"
           "--search-input-placeholder-color:"
           "--search-clear-icon-color1:"
           "--search-clear-icon-color2:"
           "--search-result-item-color:"
           "--search-result-heading-color:"
           "--search-result-keyword-background:"
           "--search-result-keyword-color:"])
         (require-fragments! "dark search contract" dark ["--search-input-background-image:"])
         (require-fragments! "search input binding"
                             (rule-body stylesheet ".sidebar .search input")
                             ["padding: 0.65rem 0.78rem 0.65rem 2.75rem"
                              "background-image: var(--search-input-background-image)"
                              "background-repeat: no-repeat"
                              "background-position: 0.78rem center"
                              "background-size: 1.25rem 1.25rem"])
         (doseq [theme ["light" "dark"]]
           (let [encoded-glyph (str/replace (contract/resolve-property stylesheet theme "--ta-search-glyph-ink")
                                            "#" "%23")
                 theme-block (if (= theme "light") root dark)]
             (require-fragments! (str theme " search glyph") theme-block [encoded-glyph])))
         world)))

   (when (= text "both table row surfaces are owned")
     (fn [{:keys [stylesheet] :as world}]
       (require-fragments! "odd table row"
                           (rule-body stylesheet ".markdown-section tr:nth-child(odd) td")
                           ["color: var(--ta-ink)"
                            "background: var(--table-row-odd-background)"])
       (require-fragments! "even table row"
                           (rule-body stylesheet ".markdown-section tr:nth-child(even) td")
                           ["color: var(--ta-ink)"
                            "background: var(--table-row-even-background)"])
       world))

   (when (= text "code-surface fallback and focus treatments use code-surface ink")
     (fn [{:keys [stylesheet] :as world}]
       (require-fragments! "dialog fallback mark"
                           (rule-body stylesheet ".ta-dialog__fallback-mark")
                           ["color: var(--ta-code-control-ink)"])
       (require-fragments! "copy-button focus"
                           (rule-body stylesheet ".docsify-copy-code-button:focus-visible")
                           ["outline-color: var(--ta-code-control-ink)"])
       (require-fragments! "dialog code surface"
                           (rule-body stylesheet ".ta-dialog__body")
                           ["background: var(--ta-code)"])
       world))

   (when (= text "dashboard action boundaries use the control rule in every state")
     (fn [{:keys [stylesheet] :as world}]
       (require-fragments! "dashboard continue boundary"
                           (rule-body stylesheet ".ta-dashboard__actions .ta-dashboard__continue")
                           ["border-color: var(--ta-control-rule)"])
       world))

   (when (= text "the theme storage key is separate from learner state")
     (fn [{:keys [shell] :as world}]
       (require-fragments! "storage contract" shell
                           ["var STORAGE_KEY = 'trace-atlas:v1:learner-state'"
                            "var THEME_KEY = 'trace-atlas:v1:theme'"])
       world))

   (when (= text "theme switching suppresses mixed transition frames")
     (fn [{:keys [stylesheet shell] :as world}]
       (require-fragments! "theme switch behavior" shell
                           ["root.classList.add('ta-theme-switching')"
                            "window.requestAnimationFrame"
                            "root.classList.remove('ta-theme-switching')"])
       (require-fragments! "theme switch styling" stylesheet
                           ["html.ta-theme-switching"
                            "transition-property: none !important;"])
       world))

   (when (= text "a persisted checkpoint reveal restores the revealed treatment without forcing its disclosure open")
     (fn [{:keys [shell] :as world}]
       (require-fragments! "checkpoint persistence" shell
                           ["if (revealed[id]) detail.classList.add('ta-checkpoint--revealed')"
                            "detail.classList.add('ta-checkpoint--revealed')"
                            "markCheckpointRevealed(route, id)"])
       (when (or (re-find #"detail\.open\s*=" shell)
                 (re-find #"detail\.setAttribute\(['\"]open" shell))
         (throw (ex-info "Checkpoint restoration must not force the disclosure open" {})))
       world))))

;; clj-mutate-manifest-begin
;; {:version 1, :tested-at "2026-08-11T01:02:16.918658+09:00", :module-hash "152406198", :forms [{:id "form/0/ns", :kind "ns", :line 1, :end-line 3, :hash "-900386403"} {:id "defn/require-fragments!", :kind "defn", :line 5, :end-line 9, :hash "1883487050"} {:id "defn/rule-body", :kind "defn", :line 11, :end-line 15, :hash "-376294777"} {:id "defn/parse-positive-number", :kind "defn", :line 17, :end-line 21, :hash "1063838340"} {:id "def/three-to-one-pairs", :kind "def", :line 23, :end-line 26, :hash "1025725968"} {:id "defn/required-contrast", :kind "defn", :line 28, :end-line 29, :hash "-75388106"} {:id "defn/step-handler", :kind "defn", :line 31, :end-line 156, :hash "-771570408"}]}
;; clj-mutate-manifest-end
