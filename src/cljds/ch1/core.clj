(ns cljds.ch1.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as s]
            [cljds.ch1.examples :refer :all]))

(defn parse-example [e]
  (when-let [matches (re-matches #"(\d+)\.(\d+)" e)]
    (->> matches
         (drop 1)
         (s/join "-")
         (str "ex-")
         (symbol)
         (ns-resolve 'cljds.ch1.examples))))

(def cli-options
  [["-e" "--example NUMBER" "The example to run"
    :parse-fn parse-example
    :validate [identity "Example must be in the form \"x.y\""]]])

(defn -main [& args]
  (let [parsed-args (parse-opts args cli-options)
        example (-> parsed-args
                    :options
                    :example)]
    (if example
      (prn (apply example []))
      (println "Couldn't find example to run"))))
