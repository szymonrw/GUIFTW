;; Interface for capturing PaintEvents:
(gen-interface
 :name guiftw.swing.PaintListener
 :extends [java.util.EventListener]
 :methods [[paint [java.awt.Graphics] Object]])

(ns guiftw.swing.Canvas
  (:gen-class
   :extends javax.swing.JPanel
   :main false
   :init init
   :state painters
   :methods [[addPaintListener [guiftw.swing.PaintListener] Object]
             [removePaintListener [guiftw.swing.PaintListener] Object]
             [getPaintListeners [] clojure.lang.PersistentVector]]))

(defn -init [& args]
  [args (atom [])])

(defn -addPaintListener [this listener]
  (swap! (.painters this) conj listener))

(defn -removePaintListener [this listener]
  (swap! (.painters this)
         #(into [] (filter (fn [x] (not= x %2)) %1))
         listener))

(defn -getPaintListeners [this]
  @(.painters this))

(defn -paintComponent [this g]
  (doseq [painter @(.painters this)]
    (.paint painter g)))
