(ns trace-atlas.acceptance-runtime
  (:require [cheshire.core :as json]
            [clojure.string :as str]
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

(defn substitute-examples [text examples]
  (reduce-kv (fn [result name value]
               (str/replace result (str "<" (clojure.core/name name) ">") (str value)))
             text
             examples))

(defn parse-positive-number [value]
  (let [number (Double/parseDouble value)]
    (when (or (not (Double/isFinite number)) (not (pos? number)))
      (throw (ex-info (str "Expected a positive finite number, got: " value) {:value value})))
    number))

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
       (assoc world :contrast (contract/contrast-for stylesheet theme foreground background))))

   (when-let [[_ minimum]
              (re-matches #"the source-level contrast ratio is at least \"([^\"]+)\"" text)]
     (fn [{:keys [contrast] :as world}]
       (let [threshold (parse-positive-number minimum)]
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

(defn execute-step! [world step examples]
  (let [text (substitute-examples (:text step) examples)]
    (if-let [handler (step-handler text)]
      (handler world)
      (throw (ex-info (str "Unsupported acceptance step: " text) {:step text})))))

(defn executions [{:keys [background scenarios]}]
  (for [scenario scenarios
        examples (if (seq (:examples scenario)) (:examples scenario) [{}])]
    {:name (:name scenario)
     :examples examples
     :steps (concat background (:steps scenario))}))

(defn run-execution! [{:keys [name examples steps]}]
  (reduce #(execute-step! %1 %2 examples) {} steps)
  (println "PASS" name (when (seq examples) (pr-str examples))))

(defn run-feature! [ir-path]
  (let [feature (json/parse-string (slurp ir-path) true)
        cases (vec (executions feature))]
    (doseq [execution cases]
      (run-execution! execution))
    (println (format "Acceptance passed: %d executions" (count cases)))))
