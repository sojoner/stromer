(ns stromer.core
  (:use [clojure.tools.logging :only (info)])
  (:require 
            [stromer.sources.twitter :as st]
            [twitter.oauth :as oauth])
  (:gen-class))


(defn example1 []
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

(defn -main [& args]
  (example1))
