(ns app.view.core
  (:require
    [app.client :as client]
    clojure.pprint)
  (:require-macros
    [app.macros :as m]))

(defn pprint [s]
  (with-out-str
   (clojure.pprint/pprint s)))

(defn rss-page [{:keys [image item]}]
  [:div
   [:a {:href "#"
        :onclick #(js/location.reload)}
    [:img {:src (:url image)}]]
   [:pre (pprint item)]])

(defn home []
  (if js/window.IS_ELECTRON
    (m/then-> (client/rss) (rss-page $))
    [:div "Stuff viewer must be viewed in the Electron App. "
     [:a {:href "https://github.com/whamtet/stuff-viewer"} "Source."]]))
