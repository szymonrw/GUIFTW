;; This is very basic example of GUI FTW usage. This code creates a
;; window filled with a single button. Clicking the button shows a
;; message.

(ns guiftw.examples.swing.basic
  ;; Let's declare a class to run example from command line.
  (:gen-class)
  
  ;; Now we import GUI FTW stuff. We want to use Swing and some
  ;; styling so guiftw.swing and guiftw.styles are imported. If, in
  ;; contrast we would use SWT instead, we will be importing
  ;; guiftw.swt instead of guiftw.swing.
  (:use (guiftw swing styles))

  ;; Import all Swing stuff used in this example.
  (:import (javax.swing JOptionPane JFrame JButton)
	   (java.awt.event MouseListener)))

;; Let's define our GUI structure.
(def gui
     ;; swing macro will return a function that creates
     ;; GUI. Parameters to this function will be parent object
     ;; (usually nil for main window) followed by any number of style
     ;; sheets.
     (swing
      ;; GUI structure is a tree. Each node begins with object's class
      ;; name, followed by private style sheet and children. In this
      ;; example JFrame will have only one child, a JButton.
      [JFrame [*id :main-window] ;; We could put properties directly
				 ;; here, but for exemplary purposes,
				 ;; we only set id.
       [JButton [*id :super-button]]]))

;; Properties that begin with * are not precisely properties (such as
;; text, color, font, etc.). *id is object's identifier. It'll be used
;; to find properties for it in style sheets. There's also *groups and
;; *cons special properties. *groups declares to which groups objects
;; belongs (which can be used in style sheets similarly to ids). *cons
;; declares constructor parameters.

;; Define style sheet number one.
(def sheet
     ;; Stylesheet macro (from guiftw.styles) returns a list of
     ;; precompiled styles.
     (stylesheet
      
      ;; Syntax of a style consits of list of ids and list of
      ;; properties. Ids are either identifiers of a single object or
      ;; group of objects.
      [:main-window] [;; Properties corresponds to setters, so you are
		      ;; not constrained to some fixed (by me) set of
		      ;; properties. Next line will be translated to
		      ;; .setTitle("...")
		      :title "First GUIFTW Window!"
		      
		      ;; unroll tag will tell GUI FTW that this setter
		      ;; have more than one argument. For example
		      ;; there is .setSize(width, height). Code below
		      ;; will be translated to .setSize(300, 200)
		      ;; instead of .setSize((300, 200)).
		      :size ^unroll (300 200)

		      ;; GUI FTW will prevent order in which
		      ;; properties are provided. This is the reason
		      ;; why they're not put in a map. Maps don't have
		      ;; particular order. So, window will be shown
		      ;; after setting all properties.
		      :visible true]
      
      [:super-button] [:text "This is a button! Click it!"

		       ;; Here is what GUI FTW understands as an event
		       ;; handler. Usually to handle events we have to
		       ;; create a new class that implements some
		       ;; interface, for example MouseListener and
		       ;; then use addMouseListener(o) on object. GUI
		       ;; FTW does just that for you!
		       ;; mouse+mouse-clicked will be understood as
		       ;; "Implement mouseClicked method from
		       ;; MouseListener interface". When method name
		       ;; have the same prefix as listener name the
		       ;; shorthand is mouse++clicked.
		       :mouse++clicked

		       ;; Event will be handled by function that takes
		       ;; one argument, the event.
		       (fn [event]
			 (JOptionPane/showMessageDialog
			  nil (reduce str "Big Brother is watching you!\nEvent: "
				      (take 50 (str event)))))]))

;; sheet2 is given separetly, because when applied, it'll shut your
;; JVM after closing the window -- not very wanted in REPL!
(def sheet2 (stylesheet
	     [:main-window] [;; lispy-notation is translated to
			     ;; CamelCase for you but if you want to,
			     ;; you can write defalutCloseOperation.
			     :default-close-operation JFrame/EXIT_ON_CLOSE]))

;; To show window in REPL use (gui nil sheet), to invoke from command
;; line type:
;; lein run -m guiftw.examples.swing.basic

;; This is static main method implementation for gen-class. gui
;; function (created by swing macro) will cascade all given style
;; sheets.
(defn -main [& args]
  (set-laf "Nimbus")
  (gui nil sheet sheet2))