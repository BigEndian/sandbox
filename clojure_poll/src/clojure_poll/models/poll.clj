(ns clojure_poll.models.poll)


(defrecord Vote [name ip option_id]
  Object
  (toString [this]
    (:name this)))

(defrecord Option [id text]
  Object
  (toString [this]
    (:text this)))


; int, string, sorted-set, set
(defrecord Poll [id title options votes]
  Object
  (toString [this]
    (str (:title this) ": " (count (:votes this)) " votes")))

(defonce polls (atom (sorted-set-by #(< (:id @%1) (:id @%2)))))
(defonce poll-id (atom 0))

(defn new-poll-id-fnc [id]
  (cond
    (integer? id) (inc id)
    :else 0))

(defn get-new-poll-id []
  (let [prior-id @poll-id]
    (do
      (swap! poll-id new-poll-id-fnc)
      prior-id)))

; Validity functions

(defn valid-option? [opt]
  (if (and (:id opt) (:text opt))
    true
    false))

(defn valid-vote? [vt]
  (and (:name vt) (:ip vt)))

(defn has-vote? [pl vt]
  (let [votes (:votes pl)
        duplicates (filter 
                     #(or (= (:ip %1) (:ip vt)) (= (:name %1) (:name vt))) votes)]
    (not (empty? duplicates))))

(defn has-option? [pl opt]
  (let [opt-texts (map :text (:options pl))
        opt-text  (if (valid-option? opt) (:text opt) opt)]
    (contains? opt-texts opt-text)))


; Shortcut functions
(defn create-option 
  ([opt-text] 
   (if (valid-option? opt-text)
     opt-text
     (Option. 0 opt-text)))
  ([id opt-text]
   (Option. id opt-text)))

(defn create-options [opt-texts]
  (let [options (map #(create-option %1 %2) (range (count opt-texts)) opt-texts)]
    (sorted-set-by #(< (:id %1) (:id %2)) options)))

; Side effect functions used by swap!

(defn add-poll! [polls poll]
  (conj polls poll))

(defn add-vote! [poll vote]
  (if (has-vote? poll vote)
    false
    (assoc poll :votes (conj (:votes poll) vote))))

; Public functions used to create/manipulate polls
(defn reset-polls! []
  (reset! polls (sorted-set-by #(< (:id @%1) (:id @%2)))))


(defn add-poll 
  "Takes a title and a sequence of titles, and then add it to the atomic set of polls,
  or a poll and adds it to the list if it's not an atomic poll"
  ([title option-texts]
   (let [new-poll (atom (Poll. (get-new-poll-id) title (create-options option-texts) []))]
     (swap! polls add-poll! new-poll)))
  ([poll]
   (let [new-poll (if (instance? clojure.lang.Atom poll) poll (atom poll))]
     (swap! polls add-poll! new-poll))))

(defn add-vote
  ([poll name ip optionid]
   (when
     (< optionid (count (:options @poll))) ; Make sure it's a valid ID
     (let [vote (Vote. name ip optionid)]
       (swap! poll add-vote! vote)))))

(defonce ex-poll  (Poll. (get-new-poll-id) "Does objective reality exist?" #{(Option. 0 "Yes") (Option. 1 "No")} []))
(defonce ex-poll2 (Poll. (get-new-poll-id) "Is reality entirely subjective?" #{(Option. 0 "Yes") (Option. 1 "No")} []))

(defonce ex-vote  (Vote. "Eric" "1.2.3.4" 0))
(defonce ex-vote2 (Vote. "John" "4.3.2.1" 1))

(defmacro intervene [pair expr]
  "Intercept a function before it's passed its arguments, in order to modify the arguments.
  The intervening function (second in the pair) will be apply'd to the arguments of the
  old function (first in the pair), the returned result will either be used with apply if
  the intervening function returns a collection, or just immediately applied as (old results)"
  (let [make-intervening-fn (fn [old intervener] 
                              (fn [& vals] 
                                (let [results (apply intervener vals)]
                                  (if (coll? results) ; Returned a collection, use apply
                                    (apply old results)
                                    (old results) ; Otherwise just pass it directly
                                    ))))]
    `(let [~(first pair) (~make-intervening-fn ~@pair)]
       ~expr)))
