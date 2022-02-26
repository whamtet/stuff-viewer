(ns app.client.article
  (:require-macros
    [app.macros :as m]))

(defn query-all [el selector]
  (-> el (.querySelectorAll selector) .values es6-iterator-seq))

(defn inner-html [el selector]
  (-> el (.querySelector selector) .-innerText))
(defn inner-html-all [el selector]
  (map #(.-innerText %) (query-all el selector)))

(defn article-data [html]
  (let [el (js/document.createElement "document")]
    (set! (.-innerHTML el) html)
    {:title (inner-html el "h1")
     :paragraphs (inner-html-all el "p")}))

(defn fetch-article [href]
  (m/then-> (js/fetch href)
            (.text $)
            (article-data $)))
