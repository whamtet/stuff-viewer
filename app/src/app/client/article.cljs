(ns app.client.article
  (:require
    clojure.pprint
    [clojure.walk :as walk])
  (:require-macros
    [app.macros :as m]))

(defn query-all [el selector]
  (-> el (.querySelectorAll selector) .values es6-iterator-seq))

(defn inner-text [el selector]
  (-> el (.querySelector selector) .-innerText))
(defn inner-html-all [el selector]
  (map #(.-innerHTML %) (query-all el selector)))

(defn merge-seqs [s1 s2 f]
  (loop [done []
         [x1 & r1] s1
         [x2 & r2 :as s2] s2]
    (if x1
      (if-let [merged (f x1 x2)]
        (recur (conj done merged) r1 r2)
        (recur (conj done x1) r1 s2))
      done)))

(defn content [el]
  (for [result (query-all el "p,img")
        :let [type (-> result .-tagName .toLowerCase keyword)]]
    [type
     (case type
           :p (.-innerText result)
           :img nil)]))

(defn merge-content [[type :as v] src]
  (when (= :img type)
    (assoc v 1 src)))

(defn img->src [x]
  (-> x :imageTypes first :urlMap vals (nth 3)))

(defn article-data [html]
  (let [el (js/document.createElement "document")
        _ (set! (.-innerHTML el) html)
        scripts (inner-html-all el "script")
        start-script (some #(when (.includes % "__INITIAL_STATE__") %) scripts)
        _ (js/eval start-script)
        display-assets (-> js/__INITIAL_STATE__
                           js/JSON.parse
                           js->clj
                           walk/keywordize-keys
                           :news
                           vals
                           first
                           :news
                           :display_assets)
        lazy-images (filter :lazyImage display-assets)
        lazy-src (map img->src lazy-images)
        content (merge-seqs (content el) lazy-src merge-content)]
    {:title (inner-text el "h1")
     :content content}))

(defn fetch-article [href]
  (m/then-> (js/fetch href)
            (.text $)
            (article-data $)))
