(ns stromer.source-twitter
  (:use
    [twitter.oauth]
    [twitter.callbacks]
    [twitter.callbacks.handlers]
    [twitter.api.streaming])
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac])
  (:import
    (twitter.callbacks.protocols AsyncStreamingCallback)))

(defn start-twitter-stream [creds
                            tracker-query
                            on-bodypart
                            on-failure
                            on-exception]
    (let [cb (AsyncStreamingCallback. on-bodypart
                                      on-failure
                                      on-exception)]
          (statuses-filter :params {:track tracker-query}
                           :oauth-creds creds
                           :callbacks cb)))

(def my-creds (make-oauth-creds "ca8kRgpi94kL0em62izqCw"
                                "GtEbhIR6tIFb5s6fCza2u7JVRiXYOjafjOWEwIHuksU"
                                "2989931-zNVTgUS9Mlq5GVSIdL7Wf5n06GKHJJL3L2jv4GRlsR"
                                "EcGycCSt0DwLmpDtZk43sT4O3GIPWa7vaXfn6xuZjeo"))

(defn on-bodypart [twitter-stuff]
  "Implement Streamming here"
  (println twitter-stuff))

(defn on-failure [failure-stuff]
  (println failure-stuff))

(defn on-exception [exception]
  (println exception))

(defn -main [& args]
  (println "Start")
  (let [status-filter (start-twitter-stream my-creds "HSV" on-bodypart on-failure on-exception)])
  (println "Done"))

