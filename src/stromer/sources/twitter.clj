(ns stromer.sources.twitter
  (:use
    [twitter.oauth]
    [twitter.callbacks]
    [twitter.callbacks.handlers]
    [twitter.api.streaming]
    [clojure.tools.logging :only (warn error)])
  (:require
    [clojure.data.json :as json]
    [http.async.client :as ac])
  (:import
    (twitter.callbacks.protocols AsyncStreamingCallback)))

(defn on-failure [failure-stuff & xargs]
  "Log failure massages as warning."
  (warn "on-failure" failure-stuff))

(defn on-exception [response throwable]
  "Log exception massages as error."
  (error ":response on-exception " response)  
  (error ":throwable on-exception " throwable))

(defn start-twitter-stream [creds tracker-query success-callback]
    "Opens a async stream to twitter to 
     get a stream and call the registered on-bodypart 
     function for every tweet."
    (let [cb (AsyncStreamingCallback. success-callback
                                      on-failure
                                      on-exception)]
          (statuses-filter :params {:track tracker-query}
                           :oauth-creds creds
                           :callbacks cb)))

(def http-stream 
  "Util atom to hold the http sream."
  (atom nil))

(defn start [creds query callback]
  "On the repl use this to start the stream."
  (reset! http-stream (start-twitter-stream
                        creds
                        query
                        callback)))

(defn stop []
  "Function that stops the http-async connection."
  ((:cancel (meta @http-stream))))



