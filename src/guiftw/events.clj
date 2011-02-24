(ns guiftw.events
  "Functions and macros in guiftw.events handle generation of event
  handlers code."
  (:require (guiftw [props :as props]
		    [utils :as utils])
	    (clojure.contrib [string :as string])))

(defrecord EventHandler [spec handler listener f]
  props/Property
  (set-on [this subject] (f subject))
  (property-name [this] spec)
  (get-value [this] handler))

(defn event-spec?
  "Returns :event if spec is of an event handler."
  [spec]
  (if (some #{\+} (name spec))
    :event))

(defn listener&method-names
  "Extracts listener interface and method (event) to implement from
  spec (a key, symbol or string). Specification is expected in form:
    1) <listener>+<method>
      or
    2) <listener>++<method>
  First form is expanded to <listener>Listener class and <method>
  method. It is common that names of methods start with same word
  as listener name. So second form is a shortcut for that scenario.
  Method name is prefixed with listener name. Examples:
    1) :mouse+mouse-clicked -> MouseListener, mouseClicked
    2) :mouse++clicked -> same as above.
  Also, lispy-notation is translated to CamelCase."
  [spec]
  {:pre [(event-spec? spec)]}

  (let [[listener method method2] (->> spec name (reduce str) (string/split #"\+"))]
    [(-> listener utils/CamelCase (str "Listener"))
     (-> (if method2 (str listener "-" method2) method)
	 utils/camelCase-small)]))

(defmacro listener
  "Generates listener interface implementation given by spec. Only one
  method (given by spec) is implemented, rest is generated as empty
  methods."
  [spec handler]
  (let [[listener-name method] (listener&method-names spec)
	listener-class (symbol listener-name)]
    `(reify ~listener-class
	    (~(symbol method) [_# event#] (~handler event#))
	    
	    ;; Implement empty methods.
	    ;; Needed because reify would make them abstract.
	    ~@(for [m (try (->> listener-class resolve .getMethods
				(map #(.getName %))
				(filter #(not= % method))
				(map symbol))
			   (catch NullPointerException e
			     (throw (ClassNotFoundException. listener-name e))))]
		(list m ['_ '_] nil)))))

(defmacro adder
  "Generates a function that will
  .add<interface name from spec>Listener(listener-object)."
  [spec listener-object]
  `(fn [subject#] (~(->> spec listener&method-names
			 first (str ".add") symbol)
		   subject#
		   ~listener-object)))
	    
(defmacro event-handler
  "Macro that returns new EventHandler. Event specified by spec will
  be handled by handler function. Spec syntax is documented in
  listener&method-names macro documentation. Handler is a function of
  one argument (event)."
  [spec handler]
  (let [listener-object `(listener ~spec ~handler)
	adder-object `(adder ~spec ~listener-object)]
    `(EventHandler. ~spec ~handler ~listener-object ~adder-object)))