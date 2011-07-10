(defproject guiftw "0.2.0-SNAPSHOT"
  :description "Declarative GUI framework with Swing and SWT backends."
  :dependencies [[org.clojure/clojure "[1.2,2.0)"]]
  :aot [#"guiftw\.*"]
  :dev-dependencies [[org.eclipse/swt-win32-win32-x86_64 "3.5.2"]])
