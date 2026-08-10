(ns trace-atlas.acceptance-runtime-spec
  (:require [speclj.core :refer :all]
            [trace-atlas.acceptance-runtime :as runtime]))

(describe "acceptance runtime values"
  (it "accepts positive finite thresholds"
    (should= 4.5 (runtime/parse-positive-number "4.5")))

  (it "rejects NaN thresholds instead of passing comparisons accidentally"
    (should= :rejected
             (try
               (runtime/parse-positive-number "NaN")
               :accepted
               (catch Exception _ :rejected)))))
