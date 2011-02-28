(ns guiftw.examples.swt.basic
  (:gen-class)
  (:use (guiftw swt styles))
  (:import org.eclipse.swt.SWT
	   org.eclipse.swt.events.SelectionListener
	   org.eclipse.swt.layout.FillLayout
	   [org.eclipse.swt.widgets Shell Button]))

(def sheet (stylesheet
	    [:main-window] [:text "First GUIFTW Window!"
			    :size ^unroll (300 200)
			    :layout (FillLayout.)]
	    [:super-button] [:text "This is a button! Click it!"
			     :selection+widget-selected
			     #(message "Masssage" (reduce str "Big Brother is watching you!\nEvent: "
						   (take 50 (str %))))]))
(def gui (swt [Shell [*id :main-window]
	       [Button [*cons (SWT/PUSH) *id :super-button]]]))

(defn -main [& args]
  (let [shell (gui (default-display) sheet)]
    (.open shell)
    (swt-loop shell)))