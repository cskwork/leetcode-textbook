(ns trace-atlas.acceptance-generator
  (:require [babashka.fs :as fs]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn slug [value]
  (-> value
      str/lower-case
      (str/replace #"[^a-z0-9]+" "-")
      (str/replace #"(^-|-$)" "")))

(defn metadata-name [feature-path]
  (str (slug feature-path) ".json"))

(def acceptance-dependency-paths
  ["src/trace_atlas/acceptance_runtime.clj"
   "src/trace_atlas/acceptance_steps.clj"
   "src/trace_atlas/theme_contract.clj"])

(defn sha256 [value]
  (let [digest (java.security.MessageDigest/getInstance "SHA-256")]
    (.update digest (.getBytes value "UTF-8"))
    (str "sha256:" (format "%064x" (java.math.BigInteger. 1 (.digest digest))))))

(defn dependency-fingerprint []
  (sha256
   (str/join "\n"
             (map #(str % "\n" (slurp %)) acceptance-dependency-paths))))

(defn entrypoint-source [ir-path]
  (str "#!/usr/bin/env bb\n"
       ";; acceptance-dependencies " (dependency-fingerprint) "\n"
       "(require '[trace-atlas.acceptance-runtime :as runtime]\n"
       "         '[trace-atlas.acceptance-steps :as steps])\n\n"
       "(runtime/run-feature! (or (first *command-line-args*) " (pr-str ir-path) ")\n"
       "                      steps/step-handler)\n"))

(defn inferred-feature-path [ir-path]
  (let [feature-path (str "acceptance/"
                          (-> ir-path fs/file-name str (str/replace #"\.json$" ".feature")))]
    (if (fs/regular-file? feature-path)
      feature-path
      (throw (ex-info (str "IR basename does not map to an acceptance feature: " feature-path)
                      {:ir-path ir-path :feature-path feature-path})))))

(defn generate! [ir-path output-dir]
  (let [ir (json/parse-string (slurp ir-path) true)
        feature-slug (slug (:name ir))
        generated-file (fs/path output-dir (str feature-slug ".acceptance.clj"))
        source (entrypoint-source ir-path)
        feature-path (inferred-feature-path ir-path)
        metadata-dir (fs/path output-dir "metadata")
        metadata-file (fs/path metadata-dir (metadata-name feature-path))
        generated-path (str generated-file)
        metadata {:schema_version 1
                  :feature_path feature-path
                  :ir_path ir-path
                  :implementation_hash (sha256 source)
                  :hash_scope "generated_files"
                  :generated_files [generated-path]}]
    (fs/create-dirs output-dir)
    (fs/create-dirs metadata-dir)
    (spit (str generated-file) source)
    (spit (str metadata-file) (str (json/generate-string metadata {:pretty true}) "\n"))
    (fs/set-posix-file-permissions generated-file "rwxr-xr-x")
    generated-path))

(def usage "Usage: acceptance-entrypoint-generator <json-ir> <generated-test-output>")

(defn validated-args [args]
  (when-not (= 2 (count args))
    (throw (ex-info usage {:exit-code 2})))
  args)

(defn error-message [error]
  (or (ex-message error) "Unknown acceptance entrypoint generation error"))

(defn error-exit-code [error]
  (or (:exit-code (ex-data error)) 1))

(defn -main [& args]
  (try
    (println (apply generate! (validated-args args)))
    (catch Exception error
      (binding [*out* *err*]
        (println "Acceptance entrypoint generation failed:" (error-message error)))
      (System/exit (error-exit-code error)))))

;; clj-mutate-manifest-begin
;; {:version 1, :tested-at "2026-08-11T01:03:58.980431+09:00", :module-hash "-759738826", :forms [{:id "form/0/ns", :kind "ns", :line 1, :end-line 4, :hash "738287613"} {:id "defn/slug", :kind "defn", :line 6, :end-line 10, :hash "658527170"} {:id "defn/metadata-name", :kind "defn", :line 12, :end-line 13, :hash "-1790165770"} {:id "def/acceptance-dependency-paths", :kind "def", :line 15, :end-line 18, :hash "-287232482"} {:id "defn/sha256", :kind "defn", :line 20, :end-line 23, :hash "397570139"} {:id "defn/dependency-fingerprint", :kind "defn", :line 25, :end-line 28, :hash "-997775715"} {:id "defn/entrypoint-source", :kind "defn", :line 30, :end-line 36, :hash "853224221"} {:id "defn/inferred-feature-path", :kind "defn", :line 38, :end-line 44, :hash "-648151150"} {:id "defn/generate!", :kind "defn", :line 46, :end-line 66, :hash "-2038048693"} {:id "def/usage", :kind "def", :line 68, :end-line 68, :hash "1755288047"} {:id "defn/validated-args", :kind "defn", :line 70, :end-line 73, :hash "492727457"} {:id "defn/error-message", :kind "defn", :line 75, :end-line 76, :hash "-387797523"} {:id "defn/error-exit-code", :kind "defn", :line 78, :end-line 79, :hash "-1215658122"} {:id "defn/-main", :kind "defn", :line 81, :end-line 87, :hash "320756072"}]}
;; clj-mutate-manifest-end
