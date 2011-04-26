cd ..
java -cp "../autodoc-jukka/autodoc-0.8.0-SNAPSHOT-standalone.jar;src;lib/dev/swt-win32-win32-x86_64-3.5.2.jar" autodoc.autodoc --name "GUI FTW!" --description "Declarative GUI framework for Clojure" --web-src-dir http://github.com/santamon/GUIFTW/blob/ --external-doc-tmpdir autodoc/tmp --copyright "(c)2011 Szymon Witamborski, MIT Licence"
cd autodoc
