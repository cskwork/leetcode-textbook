(ns trace-atlas.acceptance-worker
  (:require [babashka.fs :as fs]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [trace-atlas.acceptance-runtime :as runtime]))

(defn real-path [path]
  (let [nio-path (if (instance? java.nio.file.Path path)
                   path
                   (.toPath (io/file path)))]
    (.toRealPath nio-path (make-array java.nio.file.LinkOption 0))))

(defn elapsed-nanos [started]
  (- (System/nanoTime) started))

(defn require-contained-path [root path label]
  (when-not (and (string? path) (not (str/blank? path)))
    (throw (ex-info (str label " must be a non-empty path") {:label label})))
  (let [resolved (real-path path)]
    (when-not (.startsWith resolved root)
      (throw (ex-info (str label " escapes the repository: " path)
                      {:label label :path path})))
    resolved))

(defn generated-entrypoint [generated-dir]
  (let [matches (->> (fs/glob (str generated-dir) "*.acceptance.clj")
                     (filter fs/regular-file?)
                     vec)]
    (when-not (= 1 (count matches))
      (throw (ex-info (format "Expected one generated acceptance entrypoint in %s, found %d"
                              generated-dir (count matches))
                      {:generated-dir (str generated-dir) :matches (mapv str matches)})))
    (let [entrypoint (real-path (first matches))]
      (when-not (.startsWith entrypoint generated-dir)
        (throw (ex-info "generated acceptance entrypoint escapes generated_dir"
                        {:entrypoint (str entrypoint) :generated-dir (str generated-dir)})))
      entrypoint)))

(defn validated-job [{:keys [id feature_json generated_dir work_dir]}]
  (when-not (and (string? id) (not (str/blank? id)))
    (throw (ex-info "worker job id must be a non-empty string" {:id id})))
  (let [root (real-path ".")
        feature (require-contained-path root feature_json "feature_json")
        generated (require-contained-path root generated_dir "generated_dir")
        work (require-contained-path root work_dir "work_dir")]
    (when-not (.startsWith feature work)
      (throw (ex-info "feature_json must be inside work_dir"
                      {:feature-json (str feature) :work-dir (str work)})))
    (when-not (fs/regular-file? feature)
      (throw (ex-info "feature_json must be a regular file" {:feature-json (str feature)})))
    (when-not (fs/directory? generated)
      (throw (ex-info "generated_dir must be a directory" {:generated-dir (str generated)})))
    (when-not (fs/directory? work)
      (throw (ex-info "work_dir must be a directory" {:work-dir (str work)})))
    {:id id
     :feature-json (str feature)
     :entrypoint (str (generated-entrypoint generated))}))

(defn run-job [job]
  (let [started (System/nanoTime)
        id (:id job)
        output (java.io.StringWriter.)]
    (try
      (let [{:keys [feature-json entrypoint]} (validated-job job)]
        ;; Parse before loading generated code so malformed input is classified as
        ;; an adapter failure rather than a killed acceptance mutation.
        (runtime/validate-feature! (json/parse-string (slurp feature-json) true))
        (try
          (binding [*command-line-args* [feature-json]
                    *out* output]
            (load-file entrypoint))
          {:id id
           :outcome "test_success"
           :output (str output)
           :error ""
           :duration (elapsed-nanos started)}
          (catch Throwable error
            {:id id
             :outcome "test_failure"
             :output (str output (.getMessage error) "\n")
             :error ""
             :duration (elapsed-nanos started)})))
      (catch Throwable error
        {:id id
         :outcome "infrastructure_error"
         :output (str output)
         :error (or (.getMessage error) (.getName (class error)))
         :duration (elapsed-nanos started)}))))

(defn process-line [line]
  (try
    (run-job (json/parse-string line true))
    (catch Throwable error
      {:id nil
       :outcome "infrastructure_error"
       :output ""
       :error (or (.getMessage error) (.getName (class error)))
       :duration 0})))

(defn -main [& _]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]
    (println (json/generate-string (process-line line)))
    (flush)))

;; clj-mutate-manifest-begin
;; {:version 1, :tested-at "2026-08-11T01:03:22.777088+09:00", :module-hash "342571201", :forms [{:id "form/0/ns", :kind "ns", :line 1, :end-line 6, :hash "-136412819"} {:id "defn/real-path", :kind "defn", :line 8, :end-line 12, :hash "-579075823"} {:id "defn/elapsed-nanos", :kind "defn", :line 14, :end-line 15, :hash "-1320586420"} {:id "defn/require-contained-path", :kind "defn", :line 17, :end-line 24, :hash "-373982260"} {:id "defn/generated-entrypoint", :kind "defn", :line 26, :end-line 38, :hash "-97617211"} {:id "defn/validated-job", :kind "defn", :line 40, :end-line 58, :hash "38150950"} {:id "defn/run-job", :kind "defn", :line 60, :end-line 89, :hash "-301196822"} {:id "defn/process-line", :kind "defn", :line 91, :end-line 99, :hash "-1071999313"} {:id "defn/-main", :kind "defn", :line 101, :end-line 104, :hash "200204553"}]}
;; clj-mutate-manifest-end
