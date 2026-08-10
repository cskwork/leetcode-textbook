(ns trace-atlas.acceptance-runtime
  (:require [cheshire.core :as json]
            [clojure.string :as str]))

(def placeholder-pattern #"<([^<>]+)>")
(def step-keywords #{"Given" "When" "Then" "And"})

(defn placeholder-names [text]
  (->> (re-seq placeholder-pattern text)
       (map second)
       distinct))

(defn invalid-ir! [message data]
  (throw (ex-info (str "Invalid acceptance IR: " message) data)))

(defn validate-step! [step location]
  (when-not (map? step)
    (invalid-ir! (str location " must be an object") {:location location}))
  (when-not (contains? step-keywords (:keyword step))
    (invalid-ir! (str location " has an invalid keyword")
                 {:location location :keyword (:keyword step)}))
  (when-not (and (string? (:text step)) (not (str/blank? (:text step))))
    (invalid-ir! (str location " must have non-empty text") {:location location}))
  (when (contains? step :parameters)
    (when-not (and (vector? (:parameters step)) (every? string? (:parameters step)))
      (invalid-ir! (str location " parameters must be an array of strings")
                   {:location location}))
    (when-not (= (vec (placeholder-names (:text step))) (:parameters step))
      (invalid-ir! (str location " parameters disagree with its text")
                   {:location location
                    :parameters (:parameters step)
                    :text (:text step)}))))

(defn validate-example! [example location example-index]
  (when-not (and (map? example)
                 (every? keyword? (keys example))
                 (every? string? (vals example)))
    (invalid-ir! (str location " example " example-index
                      " must map string keys to string values")
                 {:location location :example-index example-index})))

(defn validate-scenario! [scenario scenario-index]
  (let [location (str "scenario " scenario-index)
        steps (:steps scenario)
        examples (:examples scenario)]
    (when-not (map? scenario)
      (invalid-ir! (str location " must be an object") {:location location}))
    (when-not (and (string? (:name scenario)) (not (str/blank? (:name scenario))))
      (invalid-ir! (str location " name must be a non-empty string") {:location location}))
    (when-not (and (vector? steps) (seq steps))
      (invalid-ir! (str location " steps must be a non-empty array") {:location location}))
    (doseq [[step-index step] (map-indexed vector steps)]
      (validate-step! step (str location " step " step-index)))
    (when-not (vector? examples)
      (invalid-ir! (str location " examples must be an array") {:location location}))
    (doseq [[example-index example] (map-indexed vector examples)]
      (validate-example! example location example-index))))

(defn validate-feature! [feature]
  (when-not (map? feature)
    (invalid-ir! "feature must be an object" {}))
  (when-not (and (string? (:name feature)) (not (str/blank? (:name feature))))
    (invalid-ir! "feature name must be a non-empty string" {}))
  (let [background (or (:background feature) [])
        scenarios (:scenarios feature)]
    (when-not (vector? background)
      (invalid-ir! "background must be an array" {}))
    (doseq [[index step] (map-indexed vector background)]
      (validate-step! step (str "background step " index)))
    (when-not (and (vector? scenarios) (seq scenarios))
      (invalid-ir! "scenarios must be a non-empty array" {}))
    (doseq [[scenario-index scenario] (map-indexed vector scenarios)]
      (validate-scenario! scenario scenario-index))
    feature))

(defn resolve-step-text [{:keys [text]} examples]
  (reduce
   (fn [result parameter]
     (let [example-key (keyword parameter)]
       (when-not (contains? examples example-key)
         (throw (ex-info (str "Missing example value: " parameter)
                         {:parameter parameter})))
       (str/replace result (str "<" parameter ">") (str (get examples example-key)))))
   text
   (placeholder-names text)))

(defn execute-step! [step-handler world step examples]
  (let [text (resolve-step-text step examples)]
    (if-let [handler (step-handler text)]
      (handler world)
      (throw (ex-info (str "Unsupported acceptance step: " text) {:step text})))))

(defn executions [{:keys [background scenarios]}]
  (for [scenario scenarios
        examples (if (seq (:examples scenario)) (:examples scenario) [{}])]
    {:name (:name scenario)
     :examples examples
     :steps (concat background (:steps scenario))}))

(defn run-execution! [step-handler {:keys [name examples steps]}]
  (reduce #(execute-step! step-handler %1 %2 examples) {} steps)
  (println "PASS" name (when (seq examples) (pr-str examples))))

(defn run-feature! [ir-path step-handler]
  (let [feature (validate-feature! (json/parse-string (slurp ir-path) true))
        cases (vec (executions feature))]
    (doseq [execution cases]
      (run-execution! step-handler execution))
    (println (format "Acceptance passed: %d executions" (count cases)))))

;; clj-mutate-manifest-begin
;; {:version 1, :tested-at "2026-08-11T01:05:48.373949+09:00", :module-hash "942575639", :forms [{:id "form/0/ns", :kind "ns", :line 1, :end-line 3, :hash "1765333079"} {:id "def/placeholder-pattern", :kind "def", :line 5, :end-line 5, :hash "914323254"} {:id "def/step-keywords", :kind "def", :line 6, :end-line 6, :hash "-780512500"} {:id "defn/placeholder-names", :kind "defn", :line 8, :end-line 11, :hash "-2082701950"} {:id "defn/invalid-ir!", :kind "defn", :line 13, :end-line 14, :hash "-2086944948"} {:id "defn/validate-step!", :kind "defn", :line 16, :end-line 32, :hash "1053405577"} {:id "defn/validate-example!", :kind "defn", :line 34, :end-line 40, :hash "-1781790011"} {:id "defn/validate-scenario!", :kind "defn", :line 42, :end-line 57, :hash "1190995746"} {:id "defn/validate-feature!", :kind "defn", :line 59, :end-line 74, :hash "-957966915"} {:id "defn/resolve-step-text", :kind "defn", :line 76, :end-line 85, :hash "1122635855"} {:id "defn/execute-step!", :kind "defn", :line 87, :end-line 91, :hash "501860190"} {:id "defn/executions", :kind "defn", :line 93, :end-line 98, :hash "-1189599160"} {:id "defn/run-execution!", :kind "defn", :line 100, :end-line 102, :hash "-1342276075"} {:id "defn/run-feature!", :kind "defn", :line 104, :end-line 109, :hash "-950070875"}]}
;; clj-mutate-manifest-end
