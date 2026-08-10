(ns trace-atlas.theme-contract
  (:require [clojure.string :as str]))

(defn declarations [block]
  (into {}
        (map (fn [[_ name value]] [name (str/trim value)]))
        (re-seq #"(?m)(--[a-z0-9-]+)\s*:\s*([^;]+);" block)))

(defn block-body [stylesheet pattern label]
  (or (second (re-find pattern stylesheet))
      (throw (ex-info (str "Missing CSS block: " label) {:label label}))))

(defn theme-properties [stylesheet theme]
  (let [root (declarations (block-body stylesheet #"(?s):root\s*\{(.*?)\}" ":root"))]
    (case theme
      "light" root
      "dark" (merge root
                    (declarations
                     (block-body
                      stylesheet
                      #"(?s)html\[data-ta-theme=\"dark\"\],\s*html\.dark-mode\s*\{(.*?)\}"
                      "dark theme")))
      (throw (ex-info (str "Unsupported theme: " theme) {:theme theme})))))

(defn resolve-value [properties value seen]
  (cond
    (re-matches #"#[0-9a-fA-F]{6}" value) (str/lower-case value)
    (re-matches #"var\((--[a-z0-9-]+)\)" value)
    (let [[_ property] (re-matches #"var\((--[a-z0-9-]+)\)" value)]
      (when (contains? seen property)
        (throw (ex-info (str "Circular CSS property: " property) {:property property})))
      (if-let [nested (get properties property)]
        (resolve-value properties nested (conj seen property))
        (throw (ex-info (str "Missing CSS property: " property) {:property property}))))
    :else (throw (ex-info (str "Expected a six-digit color, got: " value) {:value value}))))

(defn resolve-property [stylesheet theme property]
  (let [properties (theme-properties stylesheet theme)]
    (if-let [value (get properties property)]
      (resolve-value properties value #{property})
      (throw (ex-info (str "Missing CSS property: " property) {:property property})))))

(defn hex-channel [hex offset]
  (Integer/parseInt (subs hex offset (+ offset 2)) 16))

(defn linear-channel [channel]
  (let [value (/ channel 255.0)]
    (if (<= value 0.04045)
      (/ value 12.92)
      (Math/pow (/ (+ value 0.055) 1.055) 2.4))))

(defn luminance [hex]
  (let [red (linear-channel (hex-channel hex 1))
        green (linear-channel (hex-channel hex 3))
        blue (linear-channel (hex-channel hex 5))]
    (+ (* 0.2126 red) (* 0.7152 green) (* 0.0722 blue))))

(defn contrast [foreground background]
  (let [a (luminance foreground)
        b (luminance background)]
    (/ (+ (max a b) 0.05) (+ (min a b) 0.05))))

(defn contrast-for [stylesheet theme foreground background]
  (contrast (resolve-property stylesheet theme foreground)
            (resolve-property stylesheet theme background)))

(defn contains-all? [source fragments]
  (every? #(str/includes? source %) fragments))
