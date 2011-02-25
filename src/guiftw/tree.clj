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
	cons `(constructor ~class)
	has-props (styles/style-spec? (second struct))
	props `(styles/style ~(if has-props (second struct) []))
	children (if has-props
		   (rest (rest struct))
		   (rest struct))
	children-guis (for [x children] `(gui ~x))]
    `(fn [& style-sheets#]
       (let [style# ~props
	     obj# (apply (constructor ~class)
 			 (-> style# props/get-value :specials :*cons))
	     children-objs# (list ~@(map list children-guis))] ;; TODO: add passing style-sheets to children
	 (props/set-on style# obj#)
	 (doseq [x# children-objs#]
	   (.add obj# x#))
	 obj#))))