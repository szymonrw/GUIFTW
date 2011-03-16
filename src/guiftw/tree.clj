(ns guiftw.tree
  "Core of GUI tree stucture parsing.

  Syntax for GUI structure:

  [class1 [prop1 value1, prop2 value2, ...]
   [class2 [prop3 value3, ...] ...]
   [class3 [prop4 value4, ...] ...]
   ...]

  Each node begins with class name followed by private style sheet
  and any number of children."
  (:require (guiftw [styles :as styles]
		    [props :as props])))

(defmacro constructor
  "Returns multi-variant function that reflects all constructors for
  given class."
  [class]
  `(fn ~@(map (fn [c]
		(let [syms (repeatedly c gensym)]
		  (list (vec syms) (cons (symbol (str (.getName class) "."))
					 syms))))
	      (distinct (map #(-> % .getParameterTypes count)
			     (.getConstructors (resolve class)))))))

(defmacro gui
  "Parses GUI tree (struct) at compile time. Parsing is as abstract as
  possible, given creator function is concrete implementation of
  creating object and adding it as an child to it's parent.

  Creator function takes three arguments: a constructor
  function (generated multi-variant fn that represents all possible
  constructors for class at in this node), parent object (nil is
  possible) and style for object that will be created.

  Returns function that takes at least one argument: parent for object
  at tree root. After parent you can pass any amount of style sheets
  that will be applied to created objects. Created function will
  return root object.

  Use any of concrete implementations like guiftw.swing/swing or
  guiftw.swt/swt instead of this."
  [creator struct]
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