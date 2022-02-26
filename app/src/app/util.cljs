(ns app.util)

(defn filter-vals
  ([m]
   (into {}
         (for [[k v] m :when v]
           [k v]))))
