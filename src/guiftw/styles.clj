(ns guiftw.styles
  "Styles handling. Look in style macro doc for syntax."
  (:require (guiftw [props :as props]
		    [events :as events]
		    [special :as special])))

(defprotocol CascadeSheet
  "CascadeSheet protocol. Defines sheet that can be cascaded (merged)
  with other sheet. Includes cascade and applies-to? methods."
  (cascade [original over] "Cascade (merge) two sheets.")
  (applies-to? [sheet applicants] "Returns true if sheet is destined
  to apply on given applicants (sequence of identifiers)."))


;; Implementation of Property and CascadeSheet protocols. Fields:
;; props (seq of guiftw.props/Setter objects), events (seq of
;; guiftw.events/EventHandler objects), specials (map of special
;; properties such as *cons, *ids and *groups (last two don't apply to
;; non-private style sheets), applicants (set of identifiers that are 
(defrecord Style [props events specials applicants]
  props/Property
  (property-name [this] (zipmap (keys this)
				(map props/property-name (vals this))))
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
		    ;; Because when reducing styles, the last one will
		    ;; be always private object's style, only id and
		    ;; groups stored in there will be in final style.
		    :*id (:*id other-specials)
		    :*groups (:*groups other-specials)
		    :*cons (or (:*cons specials)
			       (:*cons other-specials))}
		   #{}))) ; Applicants are rather meta-information, so
			  ; they're not inherited.
  (applies-to? [this symbols]
	       (some applicants symbols)))

(defn style-spec? [x]
  (sequential? x))

(defmacro style
  ([prop-value-pairs] `(style [] ~prop-value-pairs))
  ([applicants prop-value-pairs]
     (let [{properties nil, events :event, specials :special}
	   (group-by (fn [[key]] (reduce #(or (%1 key) (%2 key))
					 [events/event-spec?
					  special/special-spec?]))
		     (partition 2 prop-value-pairs))]
       `(Style. (list ~@(map (fn [p] `(props/setter ~@p))
			     properties))
		(list ~@(map (fn [p] `(events/event-handler ~@p))
			     events))
		~(into {} (map (fn [[p v]] `[~(keyword p) ~(if (sequential? v) `(list ~@v) v)]) specials))
		'~(set applicants)))))

(defmacro stylesheet [& ids-style-pairs]
  `(list ~@(for [[ids style] (partition 2 ids-style-pairs)]
	     `(style ~ids ~style))))

(defn reduce-stylesheet [ids sheet]
  (if (seq ids)
    (let [to-apply (filter #(applies-to? % ids) sheet)]
      (if (seq to-apply)
	(reduce cascade to-apply)))))