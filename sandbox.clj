(ns sandbox
  (:require [clojure.string :as string])) ; test

(def ^:dynamic v 10)

(defn f1 []
  (println "f1: v " v))

(def ex_vector [1 2 3])
(def ex_list '(1 2 3))
(def ex_set #{1 2 3})
(def ex_symbol 'sym)
(def ex_string "string")
(def ex_keyword :keyword)

(defn append
  "Append values to a collection"
  [coll & values] 
  (eval `(list ~@coll ~@values)))


(defn map*
  "My implementation of map"
  [f vs]
  (loop [prc (empty vs), uprc vs]
    (if (empty? uprc)
      prc
      (recur (append prc (f (first uprc))) (rest uprc)))))


(defn average [vct]
  (if (empty? vct) 0.0
    (/ (apply + vct) (count vct))))

; (let [[x :x y :y] {:x 10 :y 10}] (+ x y))

(defn ellipsize [words]
  (let [split_words (string/split words #"\s+")]
    (str (string/join " " (take 3 split_words)) "...")))

(defn abbreviation [sentence]
  (apply str (map #(string/lower-case (first %)) (string/split sentence #"\s+"))))



(defn in-sets-of [amount coll]
  (if (empty? coll)
    coll
    (concat (conj (empty coll) (take amount coll)) (in-sets-of amount (drop amount coll)))))

(defn mk-set
  ([] #{})
  ([& others] (set (apply vector others))))

(defn to-vector
  ([values] (if (vector? values) values (apply vector values))))

(defn apply*
  "My implementation of apply"
  [f vs]
  (eval `(~f ~@vs)))
