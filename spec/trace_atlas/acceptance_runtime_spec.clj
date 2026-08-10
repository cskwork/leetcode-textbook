(ns trace-atlas.acceptance-runtime-spec
  (:require [speclj.core :refer :all]
            [trace-atlas.acceptance-runtime :as runtime]))

(describe "acceptance runtime"
  (it "resolves named example values"
    (should= "the ratio is 4.5"
             (runtime/resolve-step-text
              {:text "the ratio is <minimum>" :parameters ["minimum"]}
              {:minimum "4.5"})))

  (it "derives placeholders from authoritative step text when parser metadata is absent"
    (should= "the ratio is 4.5 and remains 4.5"
             (runtime/resolve-step-text
              {:text "the ratio is <minimum> and remains <minimum>"}
              {:minimum "4.5"})))

  (it "ignores stale parser metadata that disagrees with authoritative step text"
    (should= "the ratio is 4.5"
             (runtime/resolve-step-text
              {:text "the ratio is <minimum>" :parameters ["wrong-name"]}
              {:minimum "4.5"})))

  (it "fails before dispatch when an example value is missing"
    (should= :rejected
             (try
               (runtime/resolve-step-text
                {:text "the ratio is <minimum>" :parameters ["minimum"]}
                {})
               :accepted
               (catch Exception _ :rejected))))

  (it "fails on a missing example value when parser metadata is absent"
    (should= :rejected
             (try
               (runtime/resolve-step-text {:text "the ratio is <minimum>"} {})
               :accepted
               (catch Exception _ :rejected))))

  (it "validates optional parser parameter metadata against authoritative text"
    (runtime/validate-step!
     {:keyword "Then" :text "the ratio is <minimum>" :parameters ["minimum"]}
     "test step")
    (doseq [parameters ["minimum" ["wrong-name"]]]
      (should= :rejected
               (try
                 (runtime/validate-step!
                  {:keyword "Then" :text "the ratio is <minimum>" :parameters parameters}
                  "test step")
                 :accepted
                 (catch Exception _ :rejected)))))

  (it "accepts string-valued example rows and rejects malformed values"
    (let [feature (fn [example]
                    {:name "outlined"
                     :background []
                     :scenarios [{:name "scenario"
                                  :steps [{:keyword "Then" :text "the value is <value>"
                                           :parameters ["value"]}]
                                  :examples [example]}]})]
      (should= "outlined" (:name (runtime/validate-feature! (feature {:value "one"}))))
      (should= :rejected
               (try
                 (runtime/validate-feature! (feature {:value 1}))
                 :accepted
                 (catch Exception _ :rejected)))))

  (it "rejects every malformed required APS feature shape"
    (let [step {:keyword "Given" :text "a valid step"}
          valid {:name "valid"
                 :background [step]
                 :scenarios [{:name "scenario" :steps [step] :examples []}]}
          malformed [nil
                     (assoc valid :name "")
                     (assoc valid :background {})
                     (assoc valid :background [{}])
                     (assoc valid :scenarios [])
                     (assoc valid :scenarios [{}])
                     (assoc-in valid [:scenarios 0 :name] "")
                     (assoc-in valid [:scenarios 0 :steps] [])
                     (assoc-in valid [:scenarios 0 :steps 0] {})
                     (assoc-in valid [:scenarios 0 :examples] {})]]
      (should= valid (runtime/validate-feature! valid))
      (doseq [feature malformed]
        (should= :rejected
                 (try
                   (runtime/validate-feature! feature)
                   :accepted
                   (catch Exception _ :rejected))))))

  (it "prepends backgrounds and expands every example row"
    (let [cases (vec (runtime/executions
                      {:background [{:text "background"}]
                       :scenarios [{:name "outlined"
                                    :steps [{:text "scenario"}]
                                    :examples [{:value "one"} {:value "two"}]}
                                   {:name "plain"
                                    :steps [{:text "plain step"}]
                                    :examples []}]}))]
      (should= 3 (count cases))
      (should= ["background" "scenario"] (mapv :text (:steps (first cases))))
      (should= {} (:examples (last cases)))))

  (it "gives every execution a fresh world shared by its ordered steps"
    (let [starts (atom [])
          handler (fn [text]
                    (case text
                      "start" (fn [world]
                                (swap! starts conj world)
                                (assoc world :started true))
                      "finish" (fn [world]
                                 (when-not (:started world)
                                   (throw (ex-info "Steps ran out of order" {})))
                                 world)
                      nil))
          execution {:name "world contract"
                     :examples {}
                     :steps [{:text "start" :parameters []}
                             {:text "finish" :parameters []}]}]
      (with-out-str
        (runtime/run-execution! handler execution)
        (runtime/run-execution! handler execution))
      (should= [{} {}] @starts)))

  (it "fails unsupported project steps"
    (should= :rejected
             (try
               (with-out-str
                 (runtime/run-execution! (constantly nil)
                                         {:name "unsupported"
                                          :examples {}
                                          :steps [{:text "unknown" :parameters []}]}))
               :accepted
               (catch Exception _ :rejected)))))
