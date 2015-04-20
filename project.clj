(defproject cljds/ch1 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [incanter/incanter "1.5.5"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [quil "2.2.5"]]
  :aot [cljds.ch1.core]
  :main cljds.ch1.core
  :jvm-opts ["-Xmx2G"])