(ns app.macros)

(defmacro then-> [step & steps]
  `(-> ~step
    ~@(for [step steps]
       `(.then (fn [~'$] ~step)))))
