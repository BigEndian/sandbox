(ns sandbox
  (:require [clojure.string :as string]))

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

(defn apply*
<<<<<<< HEAD
  "My implementation of apply"
  [f vs]
  (loop [acc (f (first vs)), vs (rest vs)]
    (if (seq vs)
      (recur (f acc (first vs)) (rest vs))
      acc)
    ))

(defmacro compare-calls [fs & values]
  `(loop [fs# ~fs]
     (when (seq fs#)
       (print "Calling" (first fs#) ": ")
       (time ((first fs#) ~@values))
       (recur (rest fs#)))))
=======
 "My implementation of apply"
 [f vs]
 (loop [acc (f (first vs)), vs (rest vs)]
  (if (seq vs)
   (recur (f acc (first vs)) (rest vs))
   acc)
 ))

(defmacro compare-calls 
  [f1 f2 & vs]
  `(do
     (time (~f1 ~@vs))
     (time (~f2 ~@vs))))
>>>>>>> 2725d4113d3bfd9bbe5f64020d0af0fb2f6063a4

(def muted-writer-buf ())

(def muted-writer ; Only works for whatever was printed last unfortunately
  (proxy [java.io.Writer] [] ; latter vector is vector of arguments for superclass constructor
    (write [buf] (def muted-writer-buf (concat muted-writer-buf buf)))
    (close [] nil)
    (flush [] nil)))

(defmacro mute-print 
<<<<<<< HEAD
  [form]
  `(binding [*out* muted-writer]
     (list ~form ~(apply str muted-writer-buf))))



(defprotocol Stringifiable
  (stringify [object] "Return a string version of the object"))

(extend java.util.Date
  Stringifiable
  {:stringify (fn [date] (.toString date))})

(def x 10)


(comment 
  (def frame (java.awt.Frame.))

  (defn find-java-method [jclassinst name]
    (for [method (.getMethods jclassinst)
          :let [methodName (.getName method)]
          :when])))

=======
 [form]
 `(binding [*out* muted-writer]
   (list ~form (~apply ~str muted-writer-buf))))
>>>>>>> 2725d4113d3bfd9bbe5f64020d0af0fb2f6063a4
