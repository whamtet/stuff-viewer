(ns app.client
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
    (let [children (children x)]
      (if (empty? children)
        [(.-innerHTML x)]
        (map xml->clj children))))))

(defn rss []
  (m/then-> (js/fetch "https://www.stuff.co.nz/rss/")
            (.text $)
            (->> $ xml/loadXml xml->clj prn)))
