;; For full explanation of how GUI FTW works, see the Swing basic.clj
;; example. This one shows only differences in implementing the same
;; GUI in SWT.
(ns guiftw.examples.swt.basic
  (:gen-class)

  ;; Instead of using guiftw.swing we'll use gui.swt namespace.
  (:use (guiftw swt styles))

  ;; Import all SWT classes that we'll use here
  (:import (org.eclipse.swt.widgets Shell Button)
	   org.eclipse.swt.SWT
	   org.eclipse.swt.events.SelectionListener
	   org.eclipse.swt.layout.FillLayout))

;; swt macro works just as swing macro, but is SWT-specific.
(def gui (swt [Shell [*id :main-window]
	       [Button [*id :super-button]]]))

(def sheet (stylesheet
	    [:main-window] [:text "First GUIFTW Window!"
			    :size ^unroll (300 200)
			    ;; Set layout to new instance of
			    ;; FillLayout. This code won't be
			    ;; evaluated until applying property to
			    ;; object, so you don't have to worry when
			    ;; reusing this style.
			    :layout (FillLayout.)]
	    [:super-button] [;; Almost every widget in SWT needs its
			     ;; "style" parameter to constructor, so
			     ;; here it is: we want a PUSH
			     ;; button. Note that first argument is
			     ;; always parent object and it's not
			     ;; given explicitly by user, it's filled
			     ;; by GUI FTW when using SWT.
			     *cons (SWT/PUSH)
			     :text "This is a button! Click it!"

			     ;; Obviously, we have to implement
			     ;; different method from different
			     ;; Listener.
			     :selection+widget-selected
			     (fn [gui event]
			       ;; message is convenient function in
			       ;; guiftw.swt
			       (message "Masssage"
					(reduce str "Big Brother is watching you!\nEvent: "
							   (take 50 (str event)))))]))

;; SWT is a little more complicated from programmers perspective than
;; Swing. You have to manually write a loop that dispaches events and
;; all SWT-related stuff have to happen in this loops thread. GUI FTW
;; have wrappers for this circumstanses:
;;
;; 1) swt-loop function so you don't have your own. If supplied a
;; shell parameter, loop will work until that shell (a window) is
;; closed.
;;
;; 2) swt-thread function that starts swt-loop in separate
;; thread. Convenient to use in REPL. Sometimes (I don't know why at
;; the moment) it'll end up constantly spitting illegal thread access
;; exceptions and REPL have to be restarted.
;;
;; 3) async-exec -- takes a function (and its parameters) that will be
;; invoked inside SWT loop, so it's legal to do SWT stuff in it.
;;
;; Additionally, default-display returns (Display/getDefault)

;; To open a window in REPL you could write:
;; (swt-thread)
;; (async-exec #(.open (gui (default-display) sheet)))

(defn start-in-repl [] ;; remember to (swt-thread) first!
  (async-exec #(.open (gui (default-display) sheet))))


(defn -main [& args]
  (let [shell (gui (default-display) sheet)]
    (.open shell) ;; Is there another way to show a shell?
    (swt-loop shell))) ;; Start SWT main loop that will stop program
		       ;; after shell is closed.