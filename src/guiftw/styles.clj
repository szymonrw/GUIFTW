(ns guiftw.styles
  (:require (guiftw [props :as props]
		    [events :as events])))

(defprotocol CascadeSheet
  (cascade [original over]))

(defrecord Style [properties events]
  props/Property
  (property-name [this]
		 {:props (map props/property-name properties)
		  :events (map props/property-name events)})
  (get-value [this] {:props properties
		     :events events})
  (set-on [this subject]
	  (dorun (map #(props/set-on % subject)
		      (concat properties events))))
  CascadeSheet
  (cascade [this other]
	   (let [{other-props :props, other-events :events}
		 (props/get-value other),
		 new-props
		 (reverse
		  (loop [keys #{}
			 source (reverse (concat properties other-props))
			 output ()]
		    (if (first source)
		      (let [item (first source)
			    prop (props/property-name item)]
			(if-not (keys prop)
			  (recur (conj keys prop)
				 (rest source)
				 (conj output item))
			  (recur keys (rest source) output)))
		      output)))]
	   (Style. new-props (concat events other-events)))))

(defmacro style [prop-value-pairs]
  (let [{properties nil, events :event}
	(group-by #(events/event-spec? (first %))
		  (partition 2 prop-value-pairs))]
    `(Style. (list ~@(map (fn [p] `(props/setter ~@p))
			  properties))
	     (list ~@(map (fn [p] `(events/event-handler ~@p))
			  events)))))