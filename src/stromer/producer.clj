(ns stromer.producer
  (:use
    [twitter.oauth]
    [twitter.api.search]
    [clojure.java.io])
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac]
    [taoensso.carmine :as car])
  (:gen-class))


(def query_term "BBUZZ")

;; REDIS related stuff
(def pool (car/make-conn-pool)) ; See docstring for additional options
(def local-redis (car/make-conn-spec :host "localhost" :port 6379))
(defmacro wcar [& body] `(car/with-conn pool local-redis ~@body))
(defn publish-tweets [lasttweetid tweets]
  (doseq [tweet tweets]
    (wcar (car/publish query_term tweet))))

;; twitter helper
(def my-creds (make-oauth-creds "ca8kRgpi94kL0em62izqCw"                                                                                 "GtEbhIR6tIFb5s6fCza2u7JVRiXYOjafjOWEwIHuksU"
                                "2989931-zNVTgUS9Mlq5GVSIdL7Wf5n06GKHJJL3L2jv4GRlsR"
                                "EcGycCSt0DwLmpDtZk43sT4O3GIPWa7vaXfn6xuZjeo"))
(defn search-for [query-string]
  (search :oauth-creds my-creds
          :params {:q query-string :count "100" :until "2014-01-01"}))

(defn search-with-sinceid [query-string since_id]
  (search :oauth-creds my-creds :params {:q query-string :count "100" :since_id since_id}))

(defn search-with-maxid [query-string max_id]
  (search :oauth-creds my-creds :params {:q query-string :count  "100" :max_id max_id}))

(defn search-forward [statuses]
  (let [response (search-with-sinceid query_term (:id (last statuses)))
        tweets (:statuses (:body response))]
    (println "recur write json to file." (:id (last tweets)) (count tweets))
    (publish-tweets (:id (last tweets)) tweets)
    (recur tweets)))

(defn -main [& args]
  (let [response (search-for query_term)
        tweets (:statuses (:body response))]
    (println "Got tweets id/count:" (:id (first tweets)) (count tweets))
    (search-forward tweets)
    (publish-tweets (:id (last tweets)) tweets)))

