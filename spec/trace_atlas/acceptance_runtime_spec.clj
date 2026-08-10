(ns trace-atlas.acceptance-runtime-spec
  (:require [speclj.core :refer :all]
            [trace-atlas.acceptance-runtime :as runtime]))

(describe "acceptance runtime"
  (it "resolves named example values"
    (should= "the ratio is 4.5"
             (runtime/resolve-step-text
              {:text "the ratio is <minimum>" :parameters ["minimum"]}
              {:minimum "4.5"})))

  (it "fails before dispatch when an example value is missing"
    (should= :rejected
             (try
               (runtime/resolve-step-text
                {:text "the ratio is <minimum>" :parameters ["minimum"]}
                {})
               :accepted
               (catch Exception _ :rejected))))

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
