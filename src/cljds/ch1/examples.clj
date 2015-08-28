(ns cljds.ch1.examples
  (:require [cljds.ch1.data :refer :all]
            [cljds.ch1.stats :refer :all]
            [clojure.string :as str]
            [incanter.charts :as c]
            [incanter.core :as i]
            [incanter.distributions :as d]
            [incanter.stats :as s]
            [incanter.svg :as svg]
            [me.raynes.fs :as fs]))

(defn ex-1-1 []
  (i/col-names (load-data :uk)))

(defn ex-1-2 []
  (i/$ "Election Year" (load-data :uk)))

(defn ex-1-3 []
  (->> (load-data :uk)
       (i/$ "Election Year")
       (distinct)))

(defn ex-1-4 []
  (->> (load-data :uk)
       (i/$ "Election Year")
       (frequencies)))

(defn ex-1-5 []
  (->> (load-data :uk)
       (i/$where {"Election Year" {:$eq nil}})
       (i/to-map)))

(defn ex-1-6 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (count)))

(defn ex-1-7 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (mean)))

(defn ex-1-8 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (median)))

(defn ex-1-9 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (standard-deviation)))

(defn ex-1-10 []
  (let [xs (->> (load-data :uk-scrubbed)
                (i/$ "Electorate"))
        f (fn [q]
            (quantile q xs))]
    (map f [0 1/4 1/2 3/4 1])))


(defn ex-1-11 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (bin 5)
       (frequencies)))

(defn ex-1-12 []
  (let [xs (->> (load-data :uk-scrubbed)
                (i/$ "Electorate"))]
    (-> (c/histogram xs :x-label "UK electorate")
        (i/view))))

(defn ex-1-13 []
  (-> (i/$ "Electorate" (load-data :uk-scrubbed))
      (c/histogram :x-label "UK electorate"
                   :nbins 200)
      (i/view)))

(defn ex-1-14 []
  (-> (i/$ "Electorate" (load-data :uk-scrubbed))
      (c/histogram :x-label "UK electorate"
                   :nbins 20)
      (i/view)))

(defn ex-1-15 []
  (let [xs (->> (repeatedly rand)
                (take 10000))]
    (-> (c/histogram xs
                     :x-label "Uniform distribution"
                     :nbins 20)
        (i/view))))

(defn ex-1-16 []
  (let [xs (->> (repeatedly rand)
                (partition 10)
                (map mean)
                (take 10000))]
    (-> (c/histogram xs
                     :x-label "Distribution of means"
                     :nbins 20)
        (i/view))))

(defn ex-1-17 []
  (let [distribution (d/normal-distribution)
        xs (->> (repeatedly #(d/draw distribution))
                (take 10000))]
    (-> (c/histogram xs
                     :x-label "Normal distribution"
                     :nbins 20)
        (i/view))))

(defn honest-baker [mean sd]
  (let [distribution (d/normal-distribution mean sd)]
    (repeatedly #(d/draw distribution))))

(defn ex-1-18 []
  (-> (take 10000 (honest-baker 1000 30))
      (c/histogram :x-label "Honest baker"
                   :nbins 25)
      (i/view)))

(defn dishonest-baker [mean sd]
  (let [distribution (d/normal-distribution mean sd)]
    (->> (repeatedly #(d/draw distribution))
         (partition 13)
         (map (partial apply max)))))

(defn ex-1-19 []
  (-> (take 10000 (dishonest-baker 950 30))
      (c/histogram :x-label "Dishonest baker"
                   :nbins 25)
      (i/view)))

(defn ex-1-20 []
  (let [weights (take 10000 (dishonest-baker 950 30))]
    {:mean (mean weights)
     :median (median weights)
     :skewness (s/skewness weights)}))

(defn ex-1-21 []
  (->> (honest-baker 1000 30)
       (take 10000)
       (c/qq-plot)
       (i/view))
  (->> (dishonest-baker 950 30)
       (take 10000)
       (c/qq-plot)
       (i/view)))

(defn ex-1-22 []
  (-> (c/box-plot (->> (honest-baker 1000 30)
                       (take 10000))
                  :legend true
                  :y-label "Loaf weight (g)"
                  :series-label "Honest baker")
      (c/add-box-plot (->> (dishonest-baker 950 30)
                           (take 10000))
                      :series-label "Dishonest baker")
      (i/view)))

(defn ex-1-23 []
  (let [sample-honest    (->> (honest-baker 1000 30)
                              (take 1000))
        sample-dishonest (->> (dishonest-baker 950 30)
                              (take 1000))
        ecdf-honest    (s/cdf-empirical sample-honest)
        ecdf-dishonest (s/cdf-empirical sample-dishonest)]
    (-> (c/xy-plot sample-honest (map ecdf-honest sample-honest)
                   :x-label "Loaf Weight"
                   :y-label "Probability"
                   :legend true
                   :series-label "Honest baker")
        (c/add-lines sample-dishonest
                     (map ecdf-dishonest sample-dishonest)
                     :series-label "Dishonest baker")
        (i/view))))

(defn ex-1-24 []
  (let [electorate (->> (load-data :uk-scrubbed)
                        (i/$ "Electorate"))
        ecdf   (s/cdf-empirical electorate)
        fitted (s/cdf-normal electorate
                             :mean (s/mean electorate)
                             :sd   (s/sd electorate))]
    (-> (c/xy-plot electorate fitted
                   :x-label "Electorate"
                   :y-label "Probability"
                   :series-label "Fitted"
                   :legend true)
        (c/add-lines electorate (map ecdf electorate)
                     :series-label "Empirical")
        (i/view))))

(defn ex-1-25 []
  (->> (load-data :uk-scrubbed)
       (i/$ "Electorate")
       (c/qq-plot)
       (i/view)))

(defn ex-1-26 []
  (->> (load-data :uk-scrubbed)
       (i/add-derived-column :victors [:Con :LD] +)))

(defn ex-1-27 []
  (->> (load-data :uk-scrubbed)
       (i/$where #(not-any? number? [(% "Con") (% "LD")]))
       (i/$ [:Region :Electorate :Con :LD])))

(defn ex-1-28 []
  (->> (load-data :uk-victors)
       (i/$ :victors-share)
       (c/qq-plot)
       (i/view)))

(defn ex-1-29 []
  (-> (load-data :ru)
      (i/col-names)))

(defn ex-1-30 []
  (-> (i/$ :turnout (load-data :ru-victors))
      (c/histogram :x-label "Russia turnout"
                   :nbins 20)
      (i/view)))

(defn ex-1-31 []
  (->> (load-data :ru-victors)
       (i/$ :turnout)
       (c/qq-plot)
       (i/view)))

(defn ex-1-32 []
  (let [n-bins 40
        uk (->> (load-data :uk-victors)
                (i/$ :turnout)
                (bin n-bins)
                (as-pmf))
        ru (->> (load-data :ru-victors)
                (i/$ :turnout)
                (bin n-bins)
                (as-pmf))]
    (-> (c/xy-plot (keys uk) (vals uk)
                   :series-label "UK"
                   :legend true
                   :x-label "Turnout Bins"
                   :y-label "Probability")
        (c/add-lines (keys ru) (vals ru)
                     :series-label "Russia")
        (i/view))))

(defn ex-1-33 []
  (let [data (load-data :uk-victors)]
    (-> (c/scatter-plot (i/$ :turnout data)
                        (i/$ :victors-share data)
                        :x-label "Turnout"
                        :y-label "Victor's Share")
        (i/view))))

(defn ex-1-34 []
  (let [data (load-data :ru-victors)]
    (-> (c/scatter-plot (i/$ :turnout data)
                        (i/$ :victors-share data)
                        :x-label "Turnout"
                        :y-label "Victor's Share")
        (i/view))))

(defn ex-1-35 []
  (let [data (-> (load-data :ru-victors)
                 (s/sample :size 10000))]
    (-> (c/scatter-plot (i/$ :turnout data)
                        (i/$ :victors-share data)
                        :x-label "Turnout"
                        :y-label "Victor Share")
        (c/set-alpha 0.05)
        (i/view))))
