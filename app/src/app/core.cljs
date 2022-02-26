(ns app.core
  (:require
    [app.client :as client]
    [app.view.core :as view.core]
    [app.render :refer [html]])
  (:require-macros
    [app.macros :as m]))

(enable-console-print!)

(defn main []
  (println "main")
  (let [root (js/document.createElement "div")]
    (set! (.-id root) "root")
    (js/document.body.appendChild root)
    (m/then-> (view.core/home)
              (.appendChild root (html $)))))

(main)
