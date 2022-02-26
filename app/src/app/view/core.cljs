(ns app.view.core
  (:require
    [app.client :as client]
    clojure.pprint)
  (:require-macros
    [app.macros :as m]))

(defn pprint [s]
  (with-out-str
   (clojure.pprint/pprint s)))

(defn a-f [f & children]
  [:a {:href "javascript:void(0)"
       :onclick f} children])

(defn news-item [{:keys [title link description enclosure]}]
  (let [click-article #(prn 'article link)]
    [:div
     (a-f click-article [:h3 title])
     (a-f click-article [:img {:src (:url enclosure)}])
     [:p description]]))

(defn rss-page [{:keys [image item]}]
  [:div
   (a-f #(js/location.reload)
        [:img {:src (:url image)}])
   (map news-item item)])

(defn home []
  (if js/window.IS_ELECTRON
    (m/then-> (client/rss) (rss-page $))
    [:div "Stuff viewer must be viewed in the Electron App. "
     [:a {:href "https://github.com/whamtet/stuff-viewer"} "Source."]]))
