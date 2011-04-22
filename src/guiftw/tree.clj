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

(defn merge-guis [old new]
  {:ids (merge (:ids old) (:ids new))
   :groups (merge-with concat (:groups old) (:groups new))
   :root (or (:root old) (:root new))})

(defn gui-creator [instantiator constructor style children]
  (fn [& [gui & stylesheets]]
    {:pre [(or (nil? gui)
	       (instance? clojure.lang.IDeref gui))]}
    (let [gui (or gui (atom {}))
	  specials (-> style props/get-value :specials)
	  final-style (if-let [reduced (styles/reduce-stylesheet
					(cons (:*id specials) (:*groups specials))
					(apply concat stylesheets))]
			(styles/cascade reduced style)
			style)
	  parent (->> @gui :root)
	  obj (instantiator constructor parent final-style)]
      (props/set-on final-style gui obj)
      (let [id (if-let [id (:*id specials)]
		 {:ids {id obj}})
	    groups (if-let [groups (:*groups specials)]
		     {:groups (zipmap groups (repeat [obj]))})
	    root {:root obj}
	    gui-news (merge id groups root)]
	(swap! gui merge-guis gui-news)
	(dorun (map #(apply % gui stylesheets) children)))
      gui)))

(defmacro parse-gui
  "Parses GUI tree (struct) at compile time. Parsing is as abstract as
  possible, given instantiator function is concrete implementation of
  creating object and adding it as an child to it's parent.

  Instantiator function takes three arguments: a constructor
  function (generated multi-variant fn that represents all possible
  constructors for class at in this node), parent object (nil is
  possible) and style for object that will be created.

  Returns function that takes zero or more arguments: parent for object
  at tree root. After parent you can pass any amount of style sheets
  that will be applied to created objects. Created function will
  return root object.

  Use any of concrete implementations like guiftw.swing/swing or
  guiftw.swt/swt instead of this."
  [instantiator struct]
  (let [constructor `(constructor ~(first struct))
	has-props (styles/style-spec? (second struct))
	props `(styles/style ~(if has-props (second struct) []))
	children (if has-props
		   (rest (rest struct))
		   (rest struct))
	children-guis (for [x children] `(parse-gui ~instantiator ~x))]
    `(gui-creator ~instantiator ~constructor ~props (list ~@children-guis))))