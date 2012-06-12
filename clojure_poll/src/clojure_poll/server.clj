(ns clojure_poll.server
  (:require [noir.server :as server]))

(server/load-views "src/clojure_poll/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8081"))]
    (server/start port {:mode mode
                        :ns 'clojure_poll})))

