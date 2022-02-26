(ns app.view.core)

(defn home []
  (if js/window.IS_ELECTRON
    [:div "starting!"]
    [:div "Stuff viewer must be viewed in the Electron App. "
     [:a {:href "https://github.com/whamtet/stuff-viewer"} "Source."]]))
