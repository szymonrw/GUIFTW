(ns guiftw.tree
  (:require (guiftw [styles :as styles]
		    [props :as props])))

(defmacro constructor
  "Returns multi-variant function that reflects all constructors for given class."
  [class]
  `(fn ~@(map (fn [c]
		(let [syms (repeatedly c gensym)]
		  (list (vec syms) (cons (symbol (str (.getName class) "."))
					 syms))))
	      (distinct (map #(-> % .getParameterTypes count)
			     (.getConstructors (resolve class)))))))

(defn swing-create [ctor parent style]
  (let [obj (apply ctor (-> style props/get-value :specials :*cons))]
    (if parent (.add parent obj))
    obj))

(defn swt-create [ctor parent style]
  (apply ctor parent (-> style props/get-value :specials :*cons)))

(defmacro gui [creator struct]
  (let [class (first struct)
	has-props (styles/style-spec? (second struct))
	props `(styles/style ~(if has-props (second struct) []))
	children (if has-props
		   (rest (rest struct))
		   (rest struct))
	children-guis (for [x children] `(gui ~creator ~x))]
    `(fn [parent# & style-sheets#]
       (let [style# ~props
	     specials# (-> style# props/get-value :specials)
	     final-style# (if-let [reduced# (styles/reduce-stylesheet
					     (cons (:*id specials#) (:*groups specials#))
					     (apply concat style-sheets#))]
			    (styles/cascade reduced# style#)
			    style#)
	     obj# (~creator (constructor ~class) parent# final-style#)]
	 (dorun (map #(apply % obj# style-sheets#) (list ~@children-guis)))
	 (props/set-on final-style# obj#)
	 obj#))))