(ns app.render)

(defn- set-attrs [el attr]
  (doseq [[k v] attr
          :let [k (name k)]]
    (if (ifn? v)
      (.addEventListener el (.replace k "on" "") v)
      (.setAttribute el (name k) v))))

(defn html [hiccup]
  (if (vector? hiccup)
    (let [[tag attr & children] hiccup
          el (js/document.createElement (name tag))]
      (if (map? attr)
        (set-attrs el attr)
        (.append el (html attr)))
      (doseq [child children]
        (.append el (html child)))
      el)
    hiccup))
