(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'app.core
   :optimizations :whitespace
   :output-to "out/app.js"
   :output-dir "out"})
