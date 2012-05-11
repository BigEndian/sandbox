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
 (loop [acc (f (first vs)), vs (rest vs)]
  (if (seq vs)
   (recur (f acc (first vs)) (rest vs))
   acc)
 ))

(defn compare-calls [f1 f2 & vs]
   (do
      (print-str "Calling " (:name (meta f1)) \space)
      (eval `(time (~f1 ~@vs)))
      (print-str "Calling " (:name (meta f2)) \space)
      (eval `(time (~f2 ~@vs)))
      nil))

(def muted-writer-buf ())

(def muted-writer ; Only works for whatever was printed last unfortunately
 (proxy [java.io.Writer] [] ; latter vector is vector of arguments for superclass constructor
  (write [buf] (def muted-writer-buf (concat muted-writer-buf buf)))
  (close [] nil)
  (flush [] nil)))

(defmacro mute-print 
 [form]
 `(binding [*out* muted-writer]
   (list ~form ~(apply str muted-writer-buf))))
