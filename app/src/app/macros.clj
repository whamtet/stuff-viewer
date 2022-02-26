(ns app.macros)

(defmacro then-> [step & steps]
  `(as-> ~step ~'$
    ~@(for [step steps]
       `(if (.-then ~'$)
         (.then ~'$ (fn [~'$] ~step))
         ~step))))
