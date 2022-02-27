(ns app.view.core
  (:require
    [app.client :as client]
    [app.client.article :as client.article]
    [app.controller.core :as controller]
    clojure.pprint)
  (:require-macros
    [app.macros :as m]))

(defn pprint [s]
  (with-out-str
   (clojure.pprint/pprint s)))

(defn a-f [f & children]
  [:a {:href "javascript:void(0)"
       :onclick f} children])

(defn news-item [i {:keys [title link description enclosure]}]
  (let [click-article #(controller/view-article link)]
    [:div
     [:h3
      (a-f click-article title)
      [:div.d-none {:id (str "comments-" i)} "Comments"]]
     (a-f click-article [:img {:src (:url enclosure)}])
     [:p description]]))

(defn rss-page [{:keys [image item]}]
  [:div
   (a-f #(js/location.reload)
        [:img {:src (:url image)}])
   [:div#subsection
    (map-indexed news-item item)]])

(defn fetch-articles [{:keys [item]}]
  (dorun
   (map-indexed
    (fn [i {:keys [link]}]
      (m/then-> (client.article/allow-comments? link)
                (when $
                      (-> (str "comments-" i)
                          js/document.getElementById
                          (.setAttribute "class" "d-comment")))))
    item)))

(defn home []
  (if js/window.IS_ELECTRON
    (m/then-> (client/rss) (do (fetch-articles $) (rss-page $)))
    [:div "Stuff viewer must be viewed in the Electron App. "
     [:a {:href "https://github.com/whamtet/stuff-viewer"} "Source."]]))
