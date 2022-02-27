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

(def cache (atom {}))

(defn set-cache [href html]
  (let [el (js/document.createElement "document")
        _ (set! (.-innerHTML el) html)
        scripts (inner-html-all el "script")
        start-script (some #(when (.includes % "__INITIAL_STATE__") %) scripts)
        _ (set! js/window.a
            (-> start-script
                (.substring (inc (count to-remove)))
                js/eval
                js/JSON.parse))
        to-remove (re-find #".*?=" start-script)
        data (-> start-script
                 (.substring (inc (count to-remove)))
                 js/eval
                 js/JSON.parse
                 js->clj
                 walk/keywordize-keys
                 (assoc
                   :title (inner-text el "h1")
                   :content (content el)))]
    ((swap! cache assoc href data) href)))

(defn article-data [{:keys [news title content]}]
  (let [lazy-src (->> news
                      vals
                      first
                      :news
                      :display_assets
                      (filter :lazyImage)
                      (map #(-> % :imageTypes first :urlMap vals (nth 3))))
        content (merge-seqs content lazy-src merge-content)]
    {:title title
     :content content}))

(defn read-cache [href]
  (or (@cache href)
      (m/then-> (js/fetch href) (.text $) (set-cache href $))))

(defn fetch-article [href]
  (m/then-> (read-cache href) (article-data $)))

(defn allow-comments? [href]
  (m/then-> (read-cache href)
            (-> $ :news vals first :news :allowComments)))
