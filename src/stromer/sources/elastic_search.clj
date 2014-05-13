(ns stromer.sources.elastic-search
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as doc]
            [clojurewerkz.elastisch.rest.response :refer [hits-from]]
            [clojure.tools.logging :only (warn)])
	(:gen-class))

(def http-connection (esr/connect "http://es01.geekthink.de"))

(defn match-all [index-name type]
  (doc/search http-connection index-name type :query {:match_all {}}))

(defn stream-es-index [response callback]
  (let [scroll-id (:_scroll_id response)
        next-page  (doc/scroll http-connection scroll-id :scroll "10")
        hits (hits-from next-page)]
        (println scroll-id (count hits))
        (doseq [hit hits]
          (callback hit))
        (recur next-page callback)))

