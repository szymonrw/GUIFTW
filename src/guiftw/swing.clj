(ns guiftw.swing
  "Functions for Happy Swing User."
  (:require (guiftw [tree :as tree]
		    [props :as props]
                    [styles :as styles])))

(defn default-adder
  "Default \"adder\" for Swing. It can be overridden by :*adder
  special property. Honors :*lay special property to use as layout
  constraint."
  [parent parent-style child child-style]
  (let [layout-data (-> child-style :specials :*lay)]
    (cond (and parent layout-data) (.add parent child layout-data)
	  parent                   (.add parent child))))

(def ^{:doc "Quirks for Swing's JTabbedPane and JScrollPane in form of style sheet.

  JTabbedPane: adds children using addTab method. Honors special
  properties :*tab-title, :*tab-icon and :*tab-tip.

  JScrollPane: adds child using setViewportView method. Only one
  child (last) will be visible in effect."}
  swing-quirks
  (styles/stylesheet
   [javax.swing.JTabbedPane]
   [:*adder (fn [parent parent-style child child-style]
                            (let [specials (-> child-style :specials)]
                              (.addTab parent
                                       (:*tab-title specials)
                                       (:*tab-icon specials)
                                       child
                                       (:*tab-tip specials))))]
   [javax.swing.JScrollPane]
   [:*adder (fn [parent parent-style child child-style]
                            (.setViewportView parent child))]))

(defn swing-create
  "Function that instantiates object in Swing-specific manner. Calls
  ctor using optionally :*cons from style as parameters to create
  object. Then calls :*adder from parent-style or
  default-adder. Parent can be nil and then no adding happens. Returns
  created object."
  [ctor style parent parent-style]
  (let [obj-specials (-> style props/get-value :specials)
	obj (apply ctor (:*cons obj-specials))
        adder (or (-> parent-style :specials :*adder)
                  default-adder)]
    (adder parent parent-style obj style)
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
