(ns stromer.core
  (:use [clojure.tools.logging :only (info)])
  (:require 
            [stromer.sources.twitter :as st]
            [stromer.sources.redis :as r]
            [twitter.oauth :as oauth])
  (:gen-class))


(defn example1 []
  "Direct twitter streaming example."
  (let [creds (oauth/make-oauth-creds ""
                                ""
                                ""
                                "")
        callback (fn [response, body]
                      (prn (str body)))
        ]
        (info "Start the stream")
        (st/start creds "BBUZZ,Clojure,Haskell" callback)
        (info "Wait a 10 secs") 
        (Thread/sleep (* 10 60000))
        (info "Stop the stream")
        (st/stop)))

(defn example2 []
  "Forever streaming values from redis.
   !!! Configure your REDIS instance in *stromer.sources.redis* "
  (let [callback (fn [msg]
                    (println msg))]
    (info "Start the redis stream.")
    ;   start your-stuff number-of-loops
    (r/scan 0 callback 1)))

(defn -main [& args]
  ;(example1)
  ;(example2)
  )
