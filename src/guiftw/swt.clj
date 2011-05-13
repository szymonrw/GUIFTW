(ns guiftw.swt
  "Functions for Happy SWT User."
  (:require (guiftw [tree :as tree]
		    [props :as props]))
  (:import (org.eclipse.swt SWT)
           (org.eclipse.swt.widgets Display MessageBox)))

(defn swt-create
  "Instantiates object in SWT-specific manner."
  [ctor parent style]
  (apply ctor parent (-> style props/get-value :specials :*cons)))

(defmacro swt
  "Parses GUI tree (struct) and return a function that creates GUI
  described by struct. For syntax of struct look into guiftw.tree
  doc.

  Uses *cons special property which value is a list of constructor
  parameters without first one (the parent), which is added
  automatically."
  [struct]
  `(tree/parse-gui swt-create ~struct))

(defn ok?
  "Check if w is not null and not disposed."
  [w]
  (and w (not (.isDisposed w))))

(defn default-display
  "Get default display"
  []
  (Display/getDefault))

(defn swt-loop
  "Loop that dispatches SWT events. Catches all exceptions and simply
  prints stack traces."
  ([shell]
     (let [display (default-display)]
       (try
	 (if-not (.readAndDispatch display)
	   (.sleep display))
	 (catch Exception e (.printStackTrace e)))
       (if (or (and shell (not (.isDisposed shell)))
	       (not shell))
	 (recur shell)
	 (.dispose display))))
  ([] (swt-loop nil)))

(defn swt-thread
  "Start SWT loop in background. Should be first thing to invoke when
  interacting with SWT in REPL to avoid wrong thread access
  exceptions."
  []
  (.start (Thread. swt-loop)))

(defn async-exec
  "Put asynchronously function to evaluate in SWT in near future."
  [f & args]
  (.asyncExec (default-display) #(apply f args)))

(defn dispose-safely [w]
  (async-exec #(if (ok? w) (.dispose w))))

(defn message
  "Create standard SWT message box."
  [title body]
  (async-exec #(-> (default-display) .getShells first
		   (MessageBox. (reduce bit-or [SWT/ICON_INFORMATION, SWT/OK]))
		   (doto (.setText title) (.setMessage body) .open))))
