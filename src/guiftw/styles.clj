(ns guiftw.styles
  "Styles handling. Look in stylesheet macro doc for syntax."
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
  (set-on [this gui subject]
	  (dorun (map #(props/set-on % gui subject)
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
                   (special/special-merge specials other-specials)
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
		~(into {} (map (fn [[p v]] `[~(keyword p) ~v]) specials))
		'~(set applicants)))))

(defmacro stylesheet
  "Takes any amount of pairs of list of identifiers and list of
  properties. List of identifiers can contain unique object ids (as
  in :*id) and group ids (as in :*groups). Following list of
  properties will be applied to objects that matches these
  indetifiers. Returns list of Style objects that corresponds to these
  pairs of lists.

  List of properties contain pairs of property name and
  value. Property names maps directly to JavaBeans property
  names (setters). For example :text \"ASDF\" will map to
  setText(\"ASDF\"). Properties will applied on objects in order
  they're given. Property names can be of String, keyword or symbol
  type but keyword type is recommended.

  Style sheets can contain event handlers where property name is
  <listener-name-without-\"Listener\">+<method-name> of Listener that
  corresonds to the event you wan to handle. Example:
  mouse+mouse-clicked will correspond to MouseListener, method
  mouseClicked. Also short-hand for double prefix is ++. You can write
  mouse++clicked and it'll translate to same thing. Value for event
  handlers is an function that takes two arguments: GUI state (an atom
  as described in guiftw.tree doc) and event object.

  Toolkits (such as Swing) can impose some additional properties (not
  translated to setters but used in other places). All extra
  properties begin with * (asterisk).

  lispy-notation is translated to CamelCase, for
  example :default-close-operation -> setDefaultCloseOperation.
  CamelCase is left as-is.

  For setters that takes more than one argument you can tag value with
  ^unroll. Then :size ^unroll (300 200) will translate to setSize(300,
  200) instead of setSize((300 200))."
  [& ids-style-pairs]
  `(list ~@(for [[ids style] (partition 2 ids-style-pairs)]
	     `(style ~ids ~style))))

(defn reduce-stylesheet [ids sheet]
  (if (seq ids)
    (let [to-apply (filter #(applies-to? % ids) sheet)]
      (if (seq to-apply)
	(reduce cascade to-apply)))))
