(ns clojure_irc.parser
  "An IRC message parser, a great deal of this work is owed to flatland
  on github"
  (:require [clojure.string :as string]))

;; Message related functions, i.e. splitting them into their constituent parts

;; Messages will be hashmaps, with contents like so:
;; {:sender "irc.lolipower.org", :command :join,
;;  :text "Please wait while we process your connection"
;;  :raw ":irc.lolipower.org 439 * :Please wait while we process your connection..."}
;;  types can consist of: "PRIVMSG", "notice", "join", "part", "channel-mode", "invite", nil (unknown)
;;  or an integer if it's a command code



(defmacro loop-with-let [bindings body] ; tbd
  (let [[loop-bindings [_ & let-bindings]] (split-with #(not= :let %) bindings)]
    (if (empty? let-bindings)
      `(loop [~@loop-bindings]
         ~body)
      `(loop [~@loop-bindings]
         (let ~@let-bindings
           ~body)))))

(defn parse-int [n]
  "Takes a string an attempts to parse an integer.
  Returns nil if an exception is thrown"
  (try
    (Integer/parseInt n)
    (catch Exception e nil)))

(defn get-prefix [msg-seq]
  (when (= \: (ffirst msg-seq))
    (string/join (rest (ffirst msg-seq)))))

(defn get-text [msg-seq]
  (if (get-prefix msg-seq)
    (second (split-with #(if (= \: (first %)) %) (rest msg-seq)))
    (second (split-with #(if (= \: (first %)) %) msg-seq))))

(defn parse-parameters [msg-seq]
  (let [paramindex (if (get-prefix msg-seq) 2 1)]
    (nth-next paramindex)))


;(defn parse-message
  ;"Parse a line received from the IRC server.
  ;This means a raw line, not a message in the sense of text from a user"
  ;[message]
  ;^{:pre [(<= 512 (count message))]}
  ;(let [raw message
        ;message (string/trim message)
        ;components (string/split message #"\s+")]
    ;(loop-with-let [components (map-indexed vector components)
                    ;:let [[[index word]] components]]
      ;(if (empty? components)
        ;result
        ;(recur 
          ;(next components)
          ;(conj result
                ;(cond
                  ;(= index 0) (if (= \: (first component))
                                ;{:sender (first component)}
                                ;{:sender nil})
                  ;:else
                ;)))))))
;
