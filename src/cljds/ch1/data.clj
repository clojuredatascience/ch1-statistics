(ns cljds.ch1.data
  (:require [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.excel :as xls]))

(defmulti load-data identity)

(defmethod load-data :uk [_]
  (-> (io/resource "UK2010.xls")
      (str)
      (xls/read-xls)))

(defmethod load-data :uk-scrubbed [_]
  (->> (load-data :uk)
       (i/$where {"Election Year" {:$ne nil}})))

(defmethod load-data :uk-victors [_]
  (->> (load-data :uk-scrubbed)
       (i/$where {:Con {:$fn number?} :LD {:$fn number?}})
       (i/add-derived-column :victors       [:Con :LD] +)
       (i/add-derived-column :victors-share [:victors :Votes] /)
       (i/add-derived-column :turnout    [:Votes :Electorate] /)))

(defmethod load-data :ru [_]
  (i/conj-rows (-> (io/resource "Russia2011_1of2.xls")
                   (str)
                   (xls/read-xls))
               (-> (io/resource "Russia2011_2of2.xls")
                   (str)
                   (xls/read-xls))))

(defmethod load-data :ru-victors [_]
  (->> (load-data :ru)
       (i/rename-cols
        {"Number of voters included in voters list" :electorate
         "Number of valid ballots" :valid-ballots
         "United Russia" :victors})
       (i/add-derived-column :victors-share
                             [:victors :valid-ballots] i/safe-div)
       (i/add-derived-column :turnout
                             [:valid-ballots :electorate] /)))
