(ns guiftw.props
  (:use (guiftw utils)))

(defprotocol Property
  (set-on [setter gui subject] "Set property value represented by this object on subject.")
  (property-name [setter] "Return property name.")
  (get-value [setter] "Gets value of property."))

(defn setter-name
  "Generate setter method name for key. Accepts strings, keywords
  and symbols.

  Notation :nice-property is translated to NiceProperty (CamelCase)."
  [key]
  (->> key name CamelCase (str ".set") symbol))

(deftype Setter [prop value f]
  Property
  (set-on [this _ subject] (f subject))
  (property-name [this] prop)
  (get-value [this] value)
  Object
  ;;(equals [this other] (= prop (property-name other)))
  (toString [this] (str "Setter: " (name prop) " := " value)))

(defmacro setter [property value]
  `(Setter. '~property '~value
	    ~(if (-> value meta :tag (= 'unroll))
	       `(fn [subject#]
		  (~(setter-name property) subject# ~@value))
	       `(fn [subject#]
		  (~(setter-name property) subject# ~value)))))
