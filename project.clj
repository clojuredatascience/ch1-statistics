(defproject cljds/ch1 "0.1.0"
  :description "Example code for the book Clojure for Data Science"
  :url "https://github.com/clojuredatascience/ch1-statistics"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [incanter/incanter "1.5.7"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [me.raynes/fs "1.4.6"]]
  :resource-paths ["data"]
  :aot [cljds.ch1.core]
  :main cljds.ch1.core
  :repl-options {:init-ns cljds.ch1.examples}
  :profiles {:dev {:dependencies [[org.clojure/tools.cli "1.0.194"]]}}
  
  :jvm-opts ["-Xmx2G"])
