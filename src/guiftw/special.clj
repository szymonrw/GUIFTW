(ns guiftw.special)

(defn special-spec? [spec]
  "Returns :special if spec is of an special property."
  [spec]
  (if (some #{\*} (name spec))
    :special))
