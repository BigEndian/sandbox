(ns clojure_poll.models.poll)

; Options is a vector, each element being a map from the option's text to a set of votes

; Poll is a struct, containing a positive integer for the ID,
; a string for the title, and a map of strings used as keys to sets of votes used as values.
; Where a vote is a name, a string, and an IP, a string.
(defstruct poll :id :title :options)
(defstruct vote :name :ip)


; Define polls to be a set sorted by their respective IDs
(defonce polls (atom (sorted-set-by #(< (:id %1) (:id %2)))))
(defonce poll-id (atom 0))

(defn poll-id-fn 
  "Function used to generate a new poll-id"
  [id]
  (cond
    (integer? id) (+ id 1)
    :else 0))

; Validity functions
(defn- valid-poll? [pl]
  (let [votes (flatten (map #(vals %) (:options pl)))]
    (= (count votes) (distinct (count votes)))))

(defn- has-vote? [pl vt]
  (let [matches (filter #(= (identity %) vt))]
    (not (empty? matches))))

(defn- has-option? [pl option]
  (let [opttexts (keys (:options poll))
        matches (filter #(= % (key option)) opttexts)]
    (empty? matches)))

(defn valid-option? [option]
  (and (map? option) (= 1 (count option)) (string? (key option)) (set? (val option))))

; Shortcut functions

(defn- create-valid-option [opt]
  (cond 
    (string? opt) {opt #{}}
    (and (map? opt) (= 1 (count opt)) (string? (key opt)) (set? (val opt))) opt
    :else :invalid))

(defn- get-new-poll-id []
  (let [priorid poll-id]
    (do
      (swap! poll-id poll-id-fn)
      priorid)))

; Functions used with swap!
(defn- add-poll! [pls pl]
  (if (valid-poll? pl)
    (conj pls pl)
    false))


(defn- add-option! [pl option]
  (let [vldopt (create-valid-option option)]
    (if (has-option? pl vldopt)
      false
      (assoc poll :options (assoc (:options poll) (key vldopt) (val vldopt)))
      )))

; (defn- add-vote [pl vote]
  ; (
; Public functions used to create/manipulate polls

(defn create-poll "Create a poll, starting at ID 0, using the passed title and options to create it"
  [title options]
   (let [newpoll (atom (struct poll (get-new-poll-id) title options) :meta {:created (System/currentTimeMillis)})] ; @ is used to deref the atom
     (swap! polls add-poll! newpoll)))

(defn add-option [pl option]
  (let [option (create-valid-option option)]
    (swap! pl add-option! option)))

