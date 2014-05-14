(ns stromer.sources.elastic-search
  (:use [clojure.tools.logging :only (info error)])
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as doc]
            [clojurewerkz.elastisch.rest.response :refer [hits-from]]
            [clojurewerkz.elastisch.query :as q]
            )
	(:gen-class))

(def http-connection (esr/connect "http://es01.geekthink.de"))

(defn match-all [index-name type]
  (doc/search http-connection index-name type :query {:match_all {}}))

(defn do-match-all [index-name mapping-type]
 (doc/search http-connection index-name mapping-type
                        :query (q/match-all)
                        :search_type "query_and_fetch"
                        :scroll "5m"
                        :size 10))


(defn stream-es-index [response callback]
  (info "Calling elastisch")
  (let [scroll-id  (:_scroll_id response)
        hits  (hits-from response)
        next-page (doc/scroll http-connection scroll-id :scroll "1m")]
        (doseq [hit hits]
          (callback hit))
        (recur next-page callback)))

