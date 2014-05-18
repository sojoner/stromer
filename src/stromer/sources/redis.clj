(ns stromer.sources.redis
  (:require
      [clojure.data.json :as json]
      [taoensso.carmine :as car :refer (wcar)]
      [clojure.java.io :as io])
    (:gen-class))

;; connection
(def pool (car/make-conn-pool)) ; See docstring for additional options
(def local-redis (car/make-conn-spec :host "localhost" :port 6379))
(defmacro wcar* [& body]
  `(car/with-conn pool local-redis ~@body))

(def subscriber
  "Hold the listener"
  (atom nil))

(defn start-listener [channel callback]
  "Start subscription with call back."
  (reset! subscriber (wcar* (car/with-new-pubsub-listener (:spec local-redis)
                                       {channel (fn f1 [msg] (callback msg))}
                                       (car/subscribe  channel)))))
(defn stop-subscriber []
  "stop the subscriber."
  (wcar* (car/close-listener @subscriber)))

(defn publish-tweets [channel tweet]
  "Function to publish a tweet to a given channel."
  (wcar* (car/publish channel tweet)))

(def counter
  "Hold scan counter"
  (atom 0))

(defn scan [cursor callback max-loops]
  "A forever streaming function which returns tweets for ever."
  (let [result (wcar* (car/scan cursor))
        new-cursor (nth result 0)
        keys (nth result 1)]
      (swap! counter inc)
      (doseq [key keys]
        (callback (wcar* (car/get key))))
      (if (< @counter max-loops)
        (recur new-cursor callback max-loops))))


(defn handle-line [line]
  (println "Json Line: " line)
  (if (> (count line) 1)
    (let [tweet (json/read-str line)
          tweet_id (:id tweet)]
      (wcar* (car/set  tweet_id line)))
    )
  )


(defn import-line-tweet-file [path]
  "Helper function to import .json tweets to redis."
  (with-open [rdr (io/reader path)]
    (doseq [line (line-seq rdr)]
            (handle-line line))))