(ns guiftw.swt
  "Functions for Happy SWT User"
  (:require (guiftw [tree :as tree]
		    [props :as props]))
  (:import (org.eclipse.swt SWT)
           (org.eclipse.swt.widgets Display MessageBox)))

(defn swt-create [ctor parent style]
  (apply ctor parent (-> style props/get-value :specials :*cons)))

(defmacro swt [struct]
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

(defn swt-thread []
  (.start (Thread. swt-loop)))

(defn async-exec
  "Put asynchronously function to evaluate in swt in near future."
  [f & args]
  (.asyncExec (default-display) #(apply f args)))

(defn dispose-safely [w]
  (async-exec #(if (ok? w) (.dispose w))))

(defn message [title body]
  (async-exec #(-> (default-display) .getShells first
		   (MessageBox. (reduce bit-or [SWT/ICON_INFORMATION, SWT/OK]))
		   (doto (.setText title) (.setMessage body) .open))))
