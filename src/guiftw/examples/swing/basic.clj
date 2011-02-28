(ns guiftw.examples.swing.basic
  (:gen-class)
  (:use (guiftw swing styles))
  (:import (javax.swing JOptionPane JFrame JButton)
	   (java.awt.event MouseListener)))

(def sheet (stylesheet
	    [:main-window] [:title "First GUIFTW Window!"
			    :size ^unroll (300 200)
			    :visible true]
	    [:super-button] [:text "This is a button! Click it!"
			     :mouse++clicked #(JOptionPane/showMessageDialog
					       nil (reduce str "Big Brother is watching you!\nEvent: "
							   (take 50 (str %))))]))
(def sheet2 (stylesheet
	     [:main-window] [:default-close-operation JFrame/EXIT_ON_CLOSE]))

(def gui (swing [JFrame [*id :main-window]
		 [JButton [*id :super-button]]]))

(defn -main [& args]
  (set-laf "Nimbus")
  (gui nil sheet sheet2))