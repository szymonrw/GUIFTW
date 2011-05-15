(ns guiftw.examples.swing.custom-adders
  (:gen-class)
  (:use (guiftw swing styles))
  (:import (javax.swing JFrame JButton JLabel
                        JTabbedPane JScrollPane
                        JSplitPane)))

(def window
  (swing [JFrame [:*id :main-window
                  :title "Custom Adders Demo"
                  :size ^unroll (500 400)
                  :visible true]
          [JSplitPane [:resize-weight 0.5]
           [JTabbedPane [:*lay JSplitPane/LEFT
                         :*adder (fn [parent parent-style child child-style]
                                   (.addTab parent
                                            (-> child-style :specials :*tab-title)
                                            child))]
            [JButton [:*tab-title "Tab title FTW!"
                      :text "This is a button."]]
            [JLabel [:*tab-title "Second TAB"
                     :text "YEAH!"]]]
           [JScrollPane [:*lay JSplitPane/RIGHT
                         :*adder (fn [parent parent-style child child-style]
                                   (.setViewportView parent child))]
            [JLabel [:text (str "<html><pre>"
                                (->> "Veeeeeeeery Looooooong Teeeeext"
                                     (interleave (repeat "\n   "))
                                     (reduce str))
                                "</pre></html>")]]]]]))

(defn -main [& args]
  (-> @(window) :root .validate))
