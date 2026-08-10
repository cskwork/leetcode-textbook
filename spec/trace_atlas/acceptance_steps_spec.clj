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
               (catch Exception _ :rejected)))))
