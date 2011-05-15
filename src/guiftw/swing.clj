(ns guiftw.swing
  "Functions for Happy Swing User."
  (:require (guiftw [tree :as tree]
		    [props :as props])))


(defn swing-create
  "Function that instantiates object in Swing-specific manner. Calls
  ctor using optionally :*cons from style as parameters to create
  object. Then calls parent.add(object) or parent.add(object,
  layout_constraints) if :*lay is present in style. Parent can be nil
  and then no adding happens. Returns created object."
  [ctor style parent parent-style]
  (let [specials (-> style props/get-value :specials)
	obj (apply ctor (:*cons specials))
	layout-data (:*lay specials)]
    (cond (and parent layout-data) (.add parent obj layout-data)
	  parent (.add parent obj))
    obj))

(defmacro swing
  "Parses GUI tree (struct) and return a function that creates GUI
  described by struct. For syntax of struct look into guiftw.tree
  doc.

  Uses *lay extra property to specify layout constraints (used when
  adding object to container) and *cons special property where the
  value is list of constructor parameters."
  [struct]
  `(tree/parse-gui swing-create ~struct))


(defn set-laf
  "Set look-and-feel by name. Throws exception if can't find laf."
  [laf]
  (->> (javax.swing.UIManager/getInstalledLookAndFeels)
       (filter #(-> % .getName (= laf)))
       first
       .getClassName
       javax.swing.UIManager/setLookAndFeel))

(defn lafs
  "Get available look-and-feel names."
  []
  (map #(.getName %)
       (javax.swing.UIManager/getInstalledLookAndFeels)))
