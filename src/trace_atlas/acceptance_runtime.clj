(ns trace-atlas.acceptance-runtime
  (:require [cheshire.core :as json]
            [clojure.string :as str]))

(defn resolve-step-text [{:keys [text parameters]} examples]
  (reduce
   (fn [result parameter]
     (let [example-key (keyword parameter)]
       (when-not (contains? examples example-key)
         (throw (ex-info (str "Missing example value: " parameter)
                         {:parameter parameter})))
       (str/replace result (str "<" parameter ">") (str (get examples example-key)))))
   text
   parameters))

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
  (let [feature (json/parse-string (slurp ir-path) true)
        cases (vec (executions feature))]
    (doseq [execution cases]
      (run-execution! step-handler execution))
    (println (format "Acceptance passed: %d executions" (count cases)))))
