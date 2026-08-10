(ns trace-atlas.acceptance-steps-spec
  (:require [speclj.core :refer :all]
            [trace-atlas.acceptance-steps :as steps]))

(describe "TRACE ATLAS acceptance step values"
  (it "accepts positive finite thresholds"
    (should= 4.5 (steps/parse-positive-number "4.5")))

  (it "rejects NaN thresholds instead of passing comparisons accidentally"
    (should= :rejected
             (try
               (steps/parse-positive-number "NaN")
               :accepted
               (catch Exception _ :rejected))))

  (it "maps semantic pairs to their WCAG contract threshold"
    (should= 3.0 (steps/required-contrast "--ta-control-rule" "--ta-paper-raised"))
    (should= 3.0 (steps/required-contrast "--ta-search-glyph-ink" "--search-input-background-color"))
    (should= 4.5 (steps/required-contrast "--ta-ink" "--table-row-even-background")))

  (it "fails when a required source fragment is absent"
    (should= :rejected
             (try
               (steps/require-fragments! "fixture" "present" ["missing"])
               :accepted
               (catch Exception _ :rejected))))

  (it "enforces both the semantic threshold and the exact contrast boundary"
    (let [handler (steps/step-handler "the source-level contrast ratio is at least \"4.5\"")
          valid-world {:contrast 4.5
                       :foreground "--ta-ink"
                       :background "--table-row-even-background"}]
      (should= valid-world (handler valid-world))
      (should= :rejected
               (try
                 (handler (assoc valid-world :contrast 4.49))
                 :accepted
                 (catch Exception _ :rejected)))
      (should= :rejected
               (try
                 ((steps/step-handler "the source-level contrast ratio is at least \"3.0\"")
                  valid-world)
                 :accepted
                 (catch Exception _ :rejected)))))

  (it "checks both light and dark search glyph bindings"
    (let [stylesheet (slurp "assets/atlas-theme.css")
          handler (steps/step-handler
                   "the search contract owns input, placeholder, glyph, clear, result, and keyword colors in both themes")]
      (should= stylesheet (:stylesheet (handler {:stylesheet stylesheet})))))

  (it "executes every production source-contract handler against the shipped assets"
    (let [world ((steps/step-handler
                  "the TRACE ATLAS theme stylesheet and learning shell are loaded")
                 {})
          texts ["both table row surfaces are owned"
                 "code-surface fallback and focus treatments use code-surface ink"
                 "dashboard action boundaries use the control rule in every state"
                 "the theme storage key is separate from learner state"
                 "theme switching suppresses mixed transition frames"
                 "a persisted checkpoint reveal restores the revealed treatment without forcing its disclosure open"]]
      (doseq [text texts]
        (should= world ((steps/step-handler text) world)))
      (should-be-nil (steps/step-handler "not a project step"))))

  (it "rejects checkpoint restoration that forces the disclosure open"
    (let [shell (str "if (revealed[id]) detail.classList.add('ta-checkpoint--revealed')\n"
                     "detail.classList.add('ta-checkpoint--revealed')\n"
                     "markCheckpointRevealed(route, id)\n"
                     "detail.open = true")
          handler (steps/step-handler
                   "a persisted checkpoint reveal restores the revealed treatment without forcing its disclosure open")]
      (should= :rejected
               (try
                 (handler {:shell shell})
                 :accepted
                 (catch Exception _ :rejected))))))
