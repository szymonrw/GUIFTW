(defproject guiftw "0.2.0-SNAPSHOT"
  :description "Declarative GUI framework with Swing and SWT backends."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :aot [#"guiftw\.*"]
  :dev-dependencies [[swank-clojure "1.3.0"]
		     [org.eclipse/swt-win32-win32-x86_64 "3.5.2"]])
