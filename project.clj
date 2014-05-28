(defproject stromer "0.1.0-SNAPSHOT"
  :description "Twitter Redis publish scubscribe example."
  :url "https://github.com/sojoner/stromer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.taoensso/carmine "2.6.0"]
                 [twitter-api "0.7.5"]
                 [org.clojure/tools.logging "0.2.6"]
                 [clojurewerkz/elastisch "2.0.0-rc1"]
                 [com.google.guava/guava "17.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 ]
  :main stromer.core)
