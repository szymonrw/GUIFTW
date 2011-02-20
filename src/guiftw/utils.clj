(ns guiftw.utils
  (:require (clojure.contrib [string :as string])))

(defn camelCase-small
  "Translates a string in a form of \"nice-property\" to
  niceProperty (traditionally used in Java for method names)."
  [s]
  (string/replace-by #"\-\w" #(-> %1 last Character/toUpperCase str) s))

(defn CamelCase
  "Like camelCase-small but first letter is always uppercase (traditional
  way of naming classes in Java)."
  [s]
  (let [camel-cased (camelCase-small s)]
  (reduce str (-> camel-cased first Character/toUpperCase) (rest camel-cased))))


