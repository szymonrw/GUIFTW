(ns guiftw.swing
  "Functions for Happy Swing User"
  (:require (guiftw [tree :as tree]
		    [props :as props])))

(defn swing-create [ctor parent style]
  (let [obj (apply ctor (-> style props/get-value :specials :*cons))]
    (if parent (.add parent obj))
    obj))

(defmacro swing [struct]
  `(tree/gui swing-create ~struct))

