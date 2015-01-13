(defproject openrada/collector "0.1.0-SNAPSHOT"
  :description "Openrada data collection component"
  :url "https://github.com/openrada/collector"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [enlive "1.1.5"]
                 [clj-http "1.0.1"]
                 [cuerdas "0.1.0"]
                 [clj-time "0.9.0"]]
  :target-path "target/%s")
