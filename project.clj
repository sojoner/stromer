(defproject stromer "0.1.0-SNAPSHOT"
  :description "Twitter Redis publish scubscribe example."
  :url "https://github.com/sojoner/stromer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [twitter-api "0.7.5"]]
                 [com.taoensso/carmine "2.6.0"])
  :main stromer.source-twitter
