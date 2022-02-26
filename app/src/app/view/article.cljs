(ns app.view.article)

(defn article-view [{:keys [title paragraphs]}]
  [:div
   [:h1 title]
   (map #(vector :p %) paragraphs)])
