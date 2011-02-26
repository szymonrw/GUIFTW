(ns guiftw.styles
  (:require (guiftw [props :as props]
		    [events :as events]
		    [special :as special])))

(defprotocol CascadeSheet
  (cascade [original over])
  (applies-to? [sheet id groups]))

(defrecord Style [props events specials id groups]
  props/Property
  (property-name [this] (zipmap (keys this) (map props/property-name (vals this))))
  (get-value [this] this)
  (set-on [this subject]
	  (dorun (map #(props/set-on % subject)
		      (concat props events))))
  CascadeSheet
  (cascade [this other]
	   (let [{other-props :props, other-events :events, other-specials :specials}
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
	   (Style. new-props
		   (concat events other-events)
		   {;; Inherit id and groups only from the other one.
		    ;; Because when reducing styles, the last one will be always
		    ;; private object's style, only id and groups stored
		    ;; in there will be in final style.
		    :*id (:*id other-specials)
		    :*groups (:*groups other-specials)
		    :*cons (or (:*cons specials)
			       (:*cons other-specials))}
		   nil #{})))
  (applies-to? [this obj-id obj-groups]
	       (or (= id obj-id) (some groups obj-groups))))

(defn style-spec? [x]
  (sequential? x))

(defmacro style
  ([prop-value-pairs] `(style ~prop-value-pairs nil []))
  ([prop-value-pairs id groups]
     (let [{properties nil, events :event, specials :special}
	   (group-by (fn [[key]] (reduce #(or (%1 key) (%2 key))
					 [events/event-spec?
					  special/special-spec?]))
		     (partition 2 prop-value-pairs))]
       `(Style. (list ~@(map (fn [p] `(props/setter ~@p))
			     properties))
		(list ~@(map (fn [p] `(events/event-handler ~@p))
			     events))
		~(into {} (map (fn [[p v]] `[~(keyword p) '~v]) specials))
		'~id ~(set groups)))))

