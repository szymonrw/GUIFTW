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

(defmacro gui [struct]
  (let [class (first struct)
	has-props (styles/style-spec? (second struct))
	props (if has-props `(styles/style ~(second struct)))
	children (if has-props
		   (rest (rest struct))
		   (rest struct))]
    `(fn [& style-sheets#]
       (let [style# ~props
	     obj# (apply (constructor ~class)
			 (-> style# props/get-value :specials :*cons))
	     children-objs# (list ~@(for [x# children] `((gui ~x#))))]
	 (if style# (props/set-on style# obj#))
	 (dorun (map #(.add obj# %) children-objs# ))
	 [obj# children-objs#]))))
			       