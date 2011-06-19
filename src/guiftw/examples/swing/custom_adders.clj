(ns guiftw.examples.swing.custom-adders
  (:gen-class)
  (:use (guiftw swing styles))
  (:import (javax.swing JFrame JButton JLabel
                        JTabbedPane JScrollPane
                        JSplitPane)))

;; This implementation is copied to guiftw.swing as swing-quirks.
(def adders
  (stylesheet
   [JTabbedPane] [:*adder (fn [parent parent-style child child-style]
                            (let [specials (-> child-style :specials)]
                              (.addTab parent
                                       (:*tab-title specials)
                                       (:*tab-icon specials)
                                       child
                                       (:*tab-tip specials))))]
   [JScrollPane] [:*adder (fn [parent parent-style child child-style]
                            (.setViewportView parent child))]))

(def window
  (swing [JFrame [:*id :main-window
                  :title "Custom Adders Demo"
                  :size ^unroll (500 400)
                  :visible true]
          [JSplitPane [:resize-weight 0.5]
           [JTabbedPane [:*lay JSplitPane/LEFT]
            [JButton [:*tab-title "Tab title FTW!"
                      :*tab-tip "Don't touch!"
                      :text "This is a button."]]
            [JLabel [:*tab-title "Second TAB"
                     :text "YEAH!"]]]
           [JScrollPane [:*lay JSplitPane/RIGHT
                         :*id :scroll]
            [JLabel [:text (str "<html><pre>"
                                (->> "Veeeeeeeery Looooooong Teeeeext"
                                     (interleave (repeat "\n   "))
                                     (reduce str))
                                "</pre></html>")]]]]]))

(defn -main [& args]
  (let [state (window adders)]
    (-> @state :root .validate)
    state))
