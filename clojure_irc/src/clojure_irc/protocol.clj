(ns clojure_irc.protocol
  (:require [clojure.string :as string]))

;; Message related functions, i.e. splitting them into their constituent parts

;; Messages will be hashmaps, with contents like so:
;; {:sender "irc.lolipower.org", :command :join,
;;  :text "Please wait while we process your connection"
;;  :raw ":irc.lolipower.org 439 * :Please wait while we process your connection..."}
;;  types can consist of: :message, :notice, :join, :part, :channel-mode, :invite, :unknown, 
;;  or an integer if it's a command code


(defn parse-int [n]
  "Takes a string an attempts to parse an integer.
  Returns nil if an exception is thrown"
  (try
    (Integer/parseInt n)
    (catch Exception e nil)))

(defmacro loop-with-let [vect body] ; tbd
  (map class vect))

(defn parse-message
  "Parse a line received from the IRC server.
  This means a raw line, not a message in the sense of text from a user"
  [message]
  ^{:pre [(<= 512 (count message))]}
  (let [raw message
        message (string/trim message)
        components (string/split message #"\s+")]
    (loop [components components result {}]
      (when (not (empty? components))
        (recur 
          (next components)
          (conj result
                (cond
                  ;(and (empty? components) (= \: (first components))) ; prefix, aka sender
                    ;{:sender (apply str (next (first components)))}
                    ;{:sender nil}
                  :else (throw (Exception. "Invalid message")))
                ))))))

  ;(let [message (string/trim message)
        ;components (string/split message #"\s+")
        ;sender (string/replace-first (first message) ":" "") ;; Assume prefix is given
        ;command-val (try (Integer/parseInt (second message))
                      ;(catch Exception e (string/lower-case (second message))))
        ;text (string/join \space
                    ;(if (= \: (first (nth components 3)))
                      ;(nthnext components 3)
                      ;(nthnext components 4)))
        ;command (cond
                  ;(integer? command-val) command-val
                  ;(= \u0001 (first text) (last text)) :notice
                  ;(= command-val "PRIVMSG") :message
                  ;(= command-val "JOIN") :join
                  ;(= command-val "PART") :part
                  ;(= command-val "MODE") :channel-mode
                  ;(= command-val "INVITE") :invite
                  ;:else :unknown)]
                  ;{:sender sender :command command :text text :raw message}))



(defmulti handle-command-code (fn [message] message))
