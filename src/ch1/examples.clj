(ns ch1.examples
  (:require [ch1.data :refer [uk-data clean-uk-data derive-uk-data filter-victor-constituencies ru-data rename-ru-cols derive-ru-data]]
            [ch1.stats :refer [mean median quantile bin pmf standard-deviation]]
            [incanter.core :refer [$ col-names view query-dataset to-map $where $rollup add-derived-column]]
            [incanter.distributions :refer [draw normal-distribution]]
            [incanter.charts :refer [histogram qq-plot xy-plot add-lines scatter-plot set-alpha]]
            [incanter.stats :as s :refer [skewness cdf-empirical cdf-normal]]))

(defn ex-1-1 []
  (col-names (uk-data)))

(defn ex-1-2 []
  ($ "Election Year" (uk-data)))

(defn ex-1-3 []
  (->> (uk-data)
       ($ "Election Year")
       distinct))

(defn ex-1-4 []
  (->> (uk-data)
       ($ "Election Year")
       frequencies))

(defn ex-1-5 []
  (->> (uk-data)
       ($where {"Election Year" {:$eq nil}})
       to-map))

(defn uk-electorate []
  (->> (uk-data)
       (clean-uk-data)
       ($ "Electorate")))

(defn ex-1-6 []
  (->> (uk-electorate)
       count))

(defn ex-1-7 []
  (->> (uk-electorate)
       mean))

(defn ex-1-8 []
  (->> (uk-electorate)
       median))

(defn ex-1-9 []
  (let [xs (uk-electorate)]
    (map (partial quantile xs) [0 1/4 1/2 3/4 1])))

(defn ex-1-10 []
  (->> (uk-electorate)
       (bin 10)
       (frequencies)))

(defn ex-1-11 []
  (-> (uk-electorate)
      histogram
      view))

(defn ex-1-12 []
  (-> (uk-electorate)
      (histogram :nbins 200)
      view))

(defn ex-1-13 []
  (->> (repeatedly rand)
       (take 10000)
       (histogram)
       (view)))

(defn ex-1-14 []
  (->> (repeatedly rand)
       (partition 10)
       (map mean)
       (take 10000)
       (histogram)
       (view)))

(defn ex-1-15 []
  (let [distribution (normal-distribution)]
    (->> (repeatedly #(draw distribution))
         (take 10000)
         (histogram)
         (view))))

(defn honest-baker []
  (let [distribution (normal-distribution 1000 30)]
    (repeatedly #(draw distribution))))

(defn ex-1-16 []
  (-> (take 10000 (honest-baker))
      (histogram :nbins 25)
      (view)))

(defn dishonest-baker []
  (let [distribution (normal-distribution 950 30)]
    (->> (repeatedly #(draw distribution))
         (partition 13)
         (map (partial apply max)))))

(defn ex-1-17 []
  (-> (take 10000 (dishonest-baker))
      (histogram :nbins 25)
      (view)))

(defn ex-1-18 []
  (let [weights (take 10000 (dishonest-baker))]
    {:mean (mean weights)
     :median (median weights)
     :skewness (skewness weights)}))

(defn ex-1-19 []
  (let [honest-weights (take 10000 (honest-baker))
        dishonest-weights (take 10000 (dishonest-baker))]
    (view (qq-plot honest-weights))
    (view (qq-plot dishonest-weights))))

(defn ex-1-20 []
  (let [data (take 10000 (dishonest-baker))
        ecdf (cdf-empirical data)]
    (-> (xy-plot data (map ecdf data))
        (view))))

(defn ex-1-21 []
  (let [dishonest-data (take 10000 (dishonest-baker))
        dishonest-ecdf (cdf-empirical dishonest-data)
        honest-data (take 10000 (honest-baker))
        honest-ecdf (cdf-empirical honest-data)]
    (-> (xy-plot dishonest-data (map dishonest-ecdf dishonest-data)
                 :x-label "Loaf Weight" :y-label "Probability"
                 :legend true :series-label "Dishonest")
        (add-lines honest-data (map honest-ecdf honest-data)
                   :series-label "Honest")
        (view))))

(defn ex-1-22 []
  (let [data (uk-electorate)]
    {:mean (s/mean data)
     :sd   (s/sd data)}))

(defn ex-1-23 []
  (let [data (uk-electorate)
        ecdf (cdf-empirical data)
        baseline (cdf-normal data
                             :mean (s/mean data)
                             :sd (s/sd data))]
    (-> (xy-plot data baseline
                 :series-label "Normal" :legend true
                 :x-label "Electorate" :y-label "Probability")
        (add-lines data (map ecdf data) :series-label "Empirical")
        (view))))

(defn ex-1-24 []
  (->> (uk-electorate)
       (qq-plot)
       (view)))

(defn ex-1-25 []
  (->> (uk-data)
       (clean-uk-data)
       ($rollup :sum "Electorate" ["Region"])
       (view)))

(defn ex-1-26 []
  (let [data (->> (uk-data)
                  (clean-uk-data)
                  ($rollup :sum "Electorate" ["Region"])
                  ($ "Electorate"))
        ecdf (cdf-empirical data)
        baseline (cdf-normal data
                             :mean (s/mean data)
                             :sd (s/sd data))]
    (-> (xy-plot data baseline
                 :series-label "Normal" :legend true
                 :x-label "Electorate" :y-label "Probability")
        (add-lines data (map ecdf data) :series-label "Empirical")
        (view))))

(defn ex-1-27 []
  (->> (uk-data)
       (clean-uk-data)
       ($where #(not (and (number? (% "Con"))
                          (number? (% "LD")))))
       ($ ["Region" "Con" "LD"])
       (view)))

(defn ex-1-28 []
  (->> (uk-data)
       (clean-uk-data)
       (filter-victor-constituencies)
       ($ ["Region" "Electorate" "Con" "LD"])
       (add-derived-column "Victors" ["Con" "LD"] +)
       (add-derived-column "Victors Share" ["Victors" "Electorate"] /)
       view))

(defn ex-1-29 []
  (col-names (ru-data)))

(defn ex-1-30 []
  (->> (ru-data)
       (rename-ru-cols)
       (derive-ru-data)
       ($ "Turnout")
       (histogram)
       (view)))

(defn ex-1-31 []
  (->> (ru-data)
       (rename-ru-cols)
       (derive-ru-data)
       ($ "Turnout")
       (qq-plot)
       (view)))

(defn ex-1-32 []
  (let [n-bins 20
        uk (->> (uk-data)
                (clean-uk-data)
                (derive-uk-data)
                ($ "Turnout")
                (bin n-bins)
                (pmf))
        ru (->> (ru-data)
                (rename-ru-cols)
                (derive-ru-data)
                ($ "Turnout")
                (bin n-bins)
                (pmf))]
    (-> (xy-plot (keys uk) (vals uk)
                 :series-label "UK":legend true
                 :x-label "Turnout Bins" :y-label "Probability")
        (add-lines (keys ru) (vals ru) :series-label "Russia")
        (view))))

(defn ex-1-33 []
  (let data (->> (uk-data)
                 (clean-uk-data)
                 (derive-uk-data))
    (-> (scatter-plot ($ "Turnout" data)
                      ($ "Victors Share" data)
                      :x-label "Turnout"
                      :y-label "Victor's Share")
        (view))))

(defn ex-1-34 []
  (let [data (->> (ru-data)
                  (rename-ru-cols)
                  (derive-ru-data))]
    (-> (scatter-plot ($ "Turnout" data)
                      ($ "Victors Share" data)
                      :x-label "Turnout"
                      :y-label "Victor's Share")
        (view))))

(defn ex-1-35 []
  (let [data (->> (ru-data)
                  (rename-ru-cols)
                  (derive-ru-data))]
    (-> (scatter-plot ($ "Turnout" data)
                      ($ "Victors Share" data)
                      :x-label "Turnout"
                      :y-label "Victor's Share")
        (set-alpha 0.005)
        (view))))
