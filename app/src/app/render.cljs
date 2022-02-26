(ns app.render
  (:require
    [app.util :as util]
    [clojure.string :as string]))

(defn flatten-list [x]
  (filter (complement seq?)
          (rest (tree-seq seq? seq x))))

(defn- set-attrs [el attr]
  (doseq [[k v] attr
          :let [k (name k)]]
    (if (ifn? v)
      (.addEventListener el (.replace k "on" "") v)
      (.setAttribute el k v))))

(def r-pre #"[^.#]+")
(def r-id #"#([^.]+)")
(def r-class #"\.([^.]+)")
(defn- special-attrs [tag]
  (util/filter-vals
   {:id (second (re-find r-id tag))
    :class (->> tag (re-seq r-class) (map second) (string/join " "))}))

(defn html [hiccup]
  (if (vector? hiccup)
    (let [[tag attr & children] (-> hiccup seq flatten-list)
          tag (name tag)
          [attr children] (if (map? attr)
                            [attr children]
                            [{} (conj children attr)])
          attr (merge attr (special-attrs tag))
          tag (re-find r-pre tag)
          el (js/document.createElement tag)]
      (set-attrs el attr)
      (doseq [child children]
        (.append el (html child)))
      el)
    hiccup))
