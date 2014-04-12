(ns stromer.core
  (:use
    [twitter.oauth]
    [twitter.api.search]
    [clojure.java.io])
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac])
  (:gen-class))


(def my-creds (make-oauth-creds "ca8kRgpi94kL0em62izqCw"
                                "GtEbhIR6tIFb5s6fCza2u7JVRiXYOjafjOWEwIHuksU"
                                "2989931-zNVTgUS9Mlq5GVSIdL7Wf5n06GKHJJL3L2jv4GRlsR"
                                "EcGycCSt0DwLmpDtZk43sT4O3GIPWa7vaXfn6xuZjeo"))

(defn search-for [query-string]
  (search :oauth-creds my-creds
          :params {:q query-string :count "100" :until "2014-01-01"}))

(defn search-with-sinceid [query-string since_id]
  (search :oauth-creds my-creds :params {:q        query-string,
                                         :count    "100",
                                         :since_id since_id}))

(defn search-with-maxid [query-string max_id]
  (search :oauth-creds my-creds :params {:q      query-string,
                                         :count  "100",
                                         :max_id max_id}))
(defn write-tweets [tweets_id tweets]
  (with-open [wrtr (writer (str tweets_id ".json"))]
    (.write wrtr (json/write-str tweets))))

(def query_term "BBUZZ")

(defn search-forward [statuses]
  (let [response (search-with-sinceid query_term (:id (last statuses)))
        tweets (:statuses (:body response))]
    (println "recur write json to file." (:id (last tweets)) (count tweets))
    (write-tweets (:id (last tweets)) tweets)
    (recur tweets)
    ))

(defn search-and-store-tweets []
  (let [response (search-for query_term)
        tweets (:statuses (:body response))]
    (println "Write json to file:" (:id (first tweets)) (count tweets))
    (search-forward tweets)
    (write-tweets (:id (last tweets)) tweets)
    ))
