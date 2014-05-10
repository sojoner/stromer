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



(defn on-bodypart [response body]
  "Implement Streamming here"
  (prn (str body))
  ; (let [status (json/read-json (str body))])
)

(defn on-failure [failure-stuff & xargs]
  (println "on-failure" failure-stuff))

(defn on-exception [response throwable]
  (println "on-exp" response)
  (println "-------------------")
  (println "throwable" (def xxx throwable))
  )

(def http-stream (atom nil))

(defn start [query callback]
  (reset! http-stream (start-twitter-stream
                        my-creds
                        query
                        on-bodypart
                        on-failure
                        on-exception)))

(defn stop []
  ((:cancel (meta @http-stream))))



