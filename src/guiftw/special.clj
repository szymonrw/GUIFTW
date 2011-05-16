(ns guiftw.special)

(defn special-spec?
  "Returns :special if spec is of an special property."
  [spec]
  (if (some #{\*} (name spec))
    :special))

(defn special-merge
  "Inherit id and groups only from the other one.
  Because when reducing styles, the last one will
  be always private object's style, only id and
  groups stored in there will be in final style."
  [specials other-specials]
  (merge (dissoc specials :*id :*groups) other-specials))
