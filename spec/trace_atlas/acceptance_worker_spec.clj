(ns trace-atlas.acceptance-worker-spec
  (:require [babashka.fs :as fs]
            [cheshire.core :as json]
            [speclj.core :refer :all]
            [trace-atlas.acceptance-generator :as generator]
            [trace-atlas.acceptance-worker :as worker]))

(def worker-root "tmp/spec-worker")

(defn write-job-fixture! [name feature]
  (let [work-dir (str worker-root "/" name "/work")
        generated-dir (str worker-root "/" name "/generated")
        base-ir (str worker-root "/" name "/base.json")
        feature-json (str work-dir "/feature.json")]
    (fs/create-dirs work-dir)
    (fs/create-dirs generated-dir)
    (spit base-ir
          (json/generate-string
           {:name "base"
            :background []
            :scenarios [{:name "base scenario"
                         :steps [{:keyword "Given"
                                  :text "the TRACE ATLAS theme stylesheet and learning shell are loaded"}]
                         :examples []}]}))
    (spit feature-json (json/generate-string feature))
    (spit (str generated-dir "/fixture.acceptance.clj")
          (generator/entrypoint-source base-ir))
    {:id name
     :feature_json feature-json
     :generated_dir generated-dir
     :work_dir work-dir}))

(describe "persistent APS acceptance worker"
  (it "executes the request's mutated IR instead of the generation-time IR"
    (let [job (write-job-fixture!
               "mutated-ir"
               {:name "mutant"
                :background []
                :scenarios [{:name "unsupported mutant"
                             :steps [{:keyword "Then" :text "unsupported"}]
                             :examples []}]})
          result (worker/run-job job)]
      (should= "test_failure" (:outcome result))
      (should (< (:duration result) 10000000000))))

  (it "returns test_success for a passing mutated IR"
    (let [job (write-job-fixture!
               "passing-ir"
               {:name "passing"
                :background []
                :scenarios [{:name "passing scenario"
                             :steps [{:keyword "Given"
                                      :text "the TRACE ATLAS theme stylesheet and learning shell are loaded"}]
                             :examples []}]})
          result (worker/run-job job)]
      (should= "test_success" (:outcome result))
      (should (< (:duration result) 10000000000))
      (should-contain "Acceptance passed: 1 executions" (:output result))))

  (it "rejects feature paths outside the mutation work directory"
    (let [job (assoc (write-job-fixture!
                      "escaped-feature"
                      {:name "passing"
                       :background []
                       :scenarios [{:name "passing scenario"
                                    :steps [{:keyword "Given"
                                             :text "the TRACE ATLAS theme stylesheet and learning shell are loaded"}]
                                    :examples []}]})
                     :feature_json "bb.edn")
          result (worker/run-job job)]
      (should= "infrastructure_error" (:outcome result))
      (should-contain "feature_json must be inside work_dir" (:error result))))

  (it "keeps malformed protocol input on the infrastructure-error path"
    (let [result (worker/process-line "not-json")]
      (should= "infrastructure_error" (:outcome result))
      (should-be-nil (:id result))
      (should= 0 (:duration result)))))
