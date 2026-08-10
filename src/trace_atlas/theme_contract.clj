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

;; clj-mutate-manifest-begin
;; {:version 1, :tested-at "2026-08-11T00:59:39.400777+09:00", :module-hash "-859212648", :forms [{:id "form/0/ns", :kind "ns", :line 1, :end-line 2, :hash "-379301843"} {:id "defn/declarations", :kind "defn", :line 4, :end-line 7, :hash "681897191"} {:id "defn/block-body", :kind "defn", :line 9, :end-line 11, :hash "-1019325734"} {:id "defn/theme-properties", :kind "defn", :line 13, :end-line 23, :hash "474299566"} {:id "defn/resolve-value", :kind "defn", :line 25, :end-line 35, :hash "184019936"} {:id "defn/resolve-property", :kind "defn", :line 37, :end-line 41, :hash "-470235103"} {:id "defn/hex-channel", :kind "defn", :line 43, :end-line 44, :hash "-1964897212"} {:id "defn/linear-channel", :kind "defn", :line 46, :end-line 50, :hash "-465873462"} {:id "defn/luminance", :kind "defn", :line 52, :end-line 56, :hash "-1830221655"} {:id "defn/contrast", :kind "defn", :line 58, :end-line 61, :hash "-1998225452"} {:id "defn/contrast-for", :kind "defn", :line 63, :end-line 65, :hash "2134119718"} {:id "defn/contains-all?", :kind "defn", :line 67, :end-line 68, :hash "239036841"}]}
;; clj-mutate-manifest-end
