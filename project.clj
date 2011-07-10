(defproject guiftw "0.2.0-SNAPSHOT"
  :description "Declarative GUI framework with Swing and SWT backends."
  :dependencies [[org.clojure/clojure "[1.2,2.0)"]]
  :aot [#"guiftw\.*"]
  ;; put your swt.jar in project's directory
  :extra-classpath-dirs ["swt.jar"])
