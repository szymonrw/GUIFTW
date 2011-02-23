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
	   (Style. properties events)))

(defmacro style [prop-value-pairs]
  (let [{properties nil, events :event}
	(group-by #(events/event-spec? (first %))
		  (partition 2 prop-value-pairs))]
    `(Style. (list ~@(map (fn [p] `(props/setter ~@p))
			  properties))
	     (list ~@(map (fn [p] `(events/event-handler ~@p))
			  events)))))