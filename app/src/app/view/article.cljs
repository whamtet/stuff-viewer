(ns app.view.article)

(defn article-view [{:keys [title content]}]
  [:div
   [:h1 title]
   (for [[tag details] content]
     [tag
      (if (= :img tag)
        {:src details}
        details)])])
