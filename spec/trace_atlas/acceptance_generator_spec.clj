(ns trace-atlas.acceptance-generator-spec
  (:require [babashka.fs :as fs]
            [cheshire.core :as json]
            [speclj.core :refer :all]
            [trace-atlas.acceptance-generator :as generator]))

(describe "acceptance entrypoint generator"
  (it "normalizes feature paths for deterministic metadata names"
    (should= "acceptance-trace-atlas-source-contracts-feature.json"
             (generator/metadata-name "acceptance/trace-atlas-source-contracts.feature")))

  (it "records the real feature path through the checked project naming convention"
    (let [feature "acceptance/trace-atlas-source-contracts.feature"
          ir "tmp/spec-generator/trace-atlas-source-contracts.json"
          output "tmp/spec-generator/generated"
          metadata (str output "/metadata/acceptance-trace-atlas-source-contracts-feature.json")]
      (fs/create-dirs (fs/parent ir))
      (spit ir "{\"name\":\"Generator contract\",\"scenarios\":[],\"background\":[]}")
      (generator/generate! ir output)
      (should= feature (:feature_path (json/parse-string (slurp metadata) true)))))

  (it "rejects an IR basename that would produce false feature metadata"
    (should= :rejected
             (try
               (generator/inferred-feature-path "tmp/renamed-ir.json")
               :accepted
               (catch Exception _ :rejected))))

  (it "generates a thin Babashka executable that delegates to the shared runtime"
    (let [entrypoint (generator/entrypoint-source "build/acceptance/theme.json")]
      (should-contain "trace-atlas.acceptance-runtime" entrypoint)
      (should-contain "runtime/run-feature!" entrypoint)
      (should-contain "build/acceptance/theme.json" entrypoint)
      (should-not-contain "gherkin" entrypoint))))
