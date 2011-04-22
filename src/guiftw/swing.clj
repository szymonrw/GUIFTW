(ns guiftw.swing
  "Functions for Happy Swing User"
  (:require (guiftw [tree :as tree]
		    [props :as props])))


(defn swing-create
  ""
  [ctor parent style]
  (let [specials (-> style props/get-value :specials)
	obj (apply ctor (:*cons specials))
	layout-data (:*lay specials)]
    (cond (and parent layout-data) (.add parent obj layout-data)
	  parent (.add parent obj))
    obj))

(defmacro swing [struct]
  `(tree/parse-gui swing-create ~struct))


(defn set-laf
  "Set look-and-feel by name."
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