(ns app.client
  (:require
    [clojure.walk :as walk])
  (:require-macros
    [app.macros :as m])
  (:import
    [goog.dom xml]))

(defn- children [x]
  (for [i (range (.-childElementCount x))]
    (aget (.-children x) i)))

(defn- attributes [x]
  (into {}
        (for [a (js/Array.from (or (.-attributes x) #js []))]
          [(keyword (.-name a)) (.-value a)])))

(defn xml->clj [x]
  (vec
   (list*
    (-> x .-nodeName keyword)
    (attributes x)
    (or
     (some->> x children not-empty (map xml->clj))
     (some-> x .-innerHTML .trim not-empty list)))))

(defn- conj-option [existing new]
  (cond
   (vector? existing) (conj existing new)
   existing [existing new]
   :else new))
(defn merge-children [attr children]
  (reduce
   (fn [m [k v]]
     (update m k conj-option v))
   attr
   children))

(defn xml->map [m]
  (walk/postwalk
   #(if (vector? %)
     (let [[head attr & [first-child :as children]] %]
       [head
        (cond
         (vector? first-child) (merge-children attr children)
         first-child first-child
         :else attr)])
     %)
   m))

(defn get-rss [edn]
  (-> edn xml->map (get-in [1 :rss :channel])))

(defn rss []
  (m/then-> (js/fetch "https://www.stuff.co.nz/rss/")
            (.text $)
            (->> $ xml/loadXml xml->clj get-rss)))
