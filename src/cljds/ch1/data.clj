(ns cljds.ch1.data
  (:require [incanter
             [core :refer [conj-rows $where rename-cols add-derived-column]]
             [excel :refer [read-xls] :as xls]]
            [clojure.java.io :as io]))

(defn uk-data []
  (-> (io/resource "UK2010.xls")
      (str)
      (xls/read-xls)))

(defn clean-uk-data [data]
  ($where {"Election Year" {:$ne nil}} data))

(defn filter-victor-constituencies [data]
  ($where {"Con" {:$fn number?} "LD" {:$fn  number?}} data))

(defn derive-uk-data [data]
  (->> data
       ($where {"Con" {:$fn number?} "LD" {:$fn number?}})
       (add-derived-column "Victors" ["Con" "LD"] +)
       (add-derived-column "Victors Share" ["Victors" "Votes"] /)
       (add-derived-column "Turnout" ["Votes" "Electorate"] /)))

(defn ru-data []
  (conj-rows (read-xls (str (io/resource "Russia2011_1of2.xls")))
             (read-xls (str (io/resource "Russia2011_2of2.xls")))))

(defn rename-ru-cols [data]
  (rename-cols
   {"Number of voters included in voters list" "Electorate"
    "The number of ballots received by the precinct election commission" "Ballots"
    "The number of canceled ballots" "Cancelled Ballots"
    "Number of valid ballots" "Valid Ballots"
    "United Russia" "Victors"}
   data))

(defn safe-divide [n d]
  (if (zero? d) 0
      (/ n d)))

(defn derive-ru-data [data]
  (->> data
       (add-derived-column "Victors Share" ["Victors" "Valid Ballots"] safe-divide)
       (add-derived-column "Turnout" ["Valid Ballots" "Electorate"] /)))
