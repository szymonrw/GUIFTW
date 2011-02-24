(ns guiftw.styles
  (:require (guiftw [props :as props]
		    [events :as events]
		    [special :as special])))

(defprotocol CascadeSheet
  (cascade [original over]))

(defrecord Style [props events specials]
  props/Property
  (property-name [this] (zipmap (keys this) (map props/property-name (vals this))))
  (get-value [this] this)
  (set-on [this subject]
	  (dorun (map #(props/set-on % subject)
		      (concat props events))))
  CascadeSheet
  (cascade [this other]
	   (let [{other-props :props, other-events :events}
		 (props/get-value other),
		 new-props
		 (reverse
		  (loop [keys #{}
			 source (reverse (concat props other-props))
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
	   (Style. new-props (concat events other-events) nil))))

(defmacro style [prop-value-pairs]
  (let [{properties nil, events :event, specials :special}
	(group-by (fn [[key]] (reduce #(or (%1 key) (%2 key))
				      [events/event-spec?
				       special/special-spec?]))
		  (partition 2 prop-value-pairs))]
    `(Style. (list ~@(map (fn [p] `(props/setter ~@p))
			  properties))
	     (list ~@(map (fn [p] `(events/event-handler ~@p))
			  events))
	     ~(into {} (map (fn [[p v]] `[~(keyword p) '~v]) specials)))))