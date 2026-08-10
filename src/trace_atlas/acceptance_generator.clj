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

(defn entrypoint-source [ir-path]
  (str "#!/usr/bin/env bb\n"
       "(require '[trace-atlas.acceptance-runtime :as runtime])\n\n"
       "(runtime/run-feature! " (pr-str ir-path) ")\n"))

(defn sha256 [value]
  (let [digest (java.security.MessageDigest/getInstance "SHA-256")]
    (.update digest (.getBytes value "UTF-8"))
    (str "sha256:" (format "%064x" (java.math.BigInteger. 1 (.digest digest))))))

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

(defn -main [& args]
  (when (not= 2 (count args))
    (binding [*out* *err*]
      (println "Usage: acceptance-entrypoint-generator <json-ir> <generated-test-output>"))
    (System/exit 2))
  (try
    (println (apply generate! args))
    (catch Exception error
      (binding [*out* *err*]
        (println "Acceptance entrypoint generation failed:" (.getMessage error)))
      (System/exit 1))))
