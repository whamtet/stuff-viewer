(ns app.controller.core
  (:require
    [app.client.article :as client.article]
    [app.render :refer [html]]
    [app.view.article :as view.article])
  (:require-macros
    [app.macros :as m]))

(defn replace-root [view]
  (let [el (js/document.getElementById "subsection")]
    (set! (.-innerHTML el) "")
    (.append el (html view))))

(defn view-article [href]
      (m/then-> (client.article/fetch-article href)
                (-> $ view.article/article-view replace-root)))
