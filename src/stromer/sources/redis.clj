(ns stromer.source-redis
  (:require
      [clojure.data.json :as json]
      [taoensso.carmine :as car])
    (:gen-class))

;; connection
(def pool (car/make-conn-pool)) ; See docstring for additional options
(def local-redis (car/make-conn-spec :host "localhost" :port 6379))
(defmacro wcar [& body] `(car/with-conn pool local-redis ~@body))

(defn print-handler [msg]
  (println msg))

(def listener
  (car/with-new-pubsub-listener (:spec local-redis)
                                {channel-name (print-handler msg)}
                                (car/subscribe  channel-name)))
(defn stop-subscriber []
  (car/close-listener listener))

(defn publish-tweets [lasttweetid tweets]
  (doseq [tweet tweets]
    (wcar (car/publish query_term tweet))))
