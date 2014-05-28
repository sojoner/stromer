(ns stromer.core
  (:use [clojure.tools.logging :only (info)]
        [clojure.data.priority-map])

  (:import (com.google.common.hash Hashing)
           (com.google.common.base Charsets))
  (:require 
            [stromer.sources.twitter :as st]
            [stromer.sources.redis :as r]
            [stromer.sources.elastic-search :as es]
            [twitter.oauth :as oauth]
            [clojure.math.numeric-tower :as math]
            [clojure.data.json :as json]

            )
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

(def h1
  (let [m (Hashing/murmur3_128)]
    (fn ^Long [^String s]
      (-> (doto (.newHasher m)
            (.putString s Charsets/UTF_8))
          (.hash)
          (.asLong)))))

(def h2
  (let [m (Hashing/murmur3_128 5N)]
    (fn ^Long [^String s]
      (-> (doto (.newHasher m)
            (.putString s Charsets/UTF_8))
          (.hash)
          (.asLong)))))

(def bucket-size 1000N)
(def number-of-hashfn 10N)

(defn get-hash-function [hash-index]
    (fn [my-string] (+ (h1 my-string) (* hash-index (h2 my-string)))))

(defn h [hash-index]
   (fn [my-string]
      (mod  (math/abs ((get-hash-function hash-index) my-string)) bucket-size)))

; The structure
(def min-sketch (atom (make-array Integer/TYPE number-of-hashfn bucket-size)))

(defn scatch-value [a-token]
  "Adding to the structure"
  (doseq [hash-index (range number-of-hashfn)]
    (let [bucket-index ((h (bigint hash-index)) a-token)
          temp-sketch-value (aget @min-sketch hash-index bucket-index)]
      (aset-int @min-sketch hash-index bucket-index (inc temp-sketch-value)))
    ))


(defn get-scatched-value [a-token]
  "Reading from the sketch"
  (let [bucket-values (atom '())]
    (doseq [hash-index (range number-of-hashfn)]
      (let [bucket-index ((h (bigint hash-index)) a-token)
            current-values @bucket-values
            value (aget @min-sketch hash-index bucket-index)
            ]
        (reset! bucket-values (conj current-values value))))
    (apply min @bucket-values)))


(def hitter (atom (priority-map)))
(def top-n 30)

(defn add-to-hitter [a-token]
  (let [ length (count @hitter)
         result (get @hitter a-token)]
       (if (not (nil? result))
          ; Update
          (let [new-value (inc result)]
            (swap! hitter assoc a-token new-value))
          ; set
           (if (< length top-n)
             (swap! hitter assoc a-token 1)
             ; swap last
             (let[last-element-count (peek @hitter)]
               (swap! hitter pop)
               (swap! hitter assoc a-token  (inc (second last-element-count))))))))


(defn add-to-streamminer [token]
  (scatch-value token)
  (add-to-hitter token))

(defn print-read-top-k []
  (doseq [[k _] @hitter]
     (do (println k " : " (get-scatched-value k))
         )))

(def counter (atom 0))
(defn example3 []
  "Scorlling through an elasticsearch index."
  (let [response (es/do-match-all "bbuzz-hackday" "tweet")
        callback (fn [msg]
                   (doseq [tag (:hashtags (:entities (:_source msg)))]
                     (add-to-streamminer (:text tag)))
                   (swap! counter inc)
                   (if (> @counter 100)
                     (do (print-read-top-k)
                         (println "< ======== >")
                         (reset! counter 0))))]

       (es/stream-es-index response callback)))

(defn -main [& args]
  ;(example1)
  ;(example2)
  (example3)
  ;(r/import-line-tweet-file "/home/hagen/data/2013-12-31.json")
  ;(es/import-line-tweets-to-es "/home/hagen/data/2013-12-31.json")
  )
