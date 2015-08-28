(ns cljds.ch1.stats
  (:require [incanter.core :as i]))

(defn mean [as]
  (let [n (count as)]
    (* (/ 1 n)
       (apply + as))))

(defn mean [xs]
  (/ (apply + xs)
     (count xs)))

(defn median [xs]
  (let [n   (count xs)
        mid (int (/ n 2))]
    (if (odd? n)
      (nth (sort xs) mid)
      (->> (sort xs)
           (drop (dec mid))
           (take 2)
           (mean)))))

(defn variance [xs]
  (let [x-bar (mean xs)
        n     (count xs)
        square-deviation (fn [x]
                           (i/sq (- x x-bar)))]
    (mean (map square-deviation xs))))

(defn standard-deviation [xs]
  (i/sqrt (variance xs)))

(defn quantile [q xs]
  (let [n (dec (count xs))
        i (-> (* n q)
              (+ 1/2)
              (int))]
    (nth (sort xs) i)))

(defn bin [n-bins xs]
  (let [min-x    (apply min xs)
        max-x    (apply max xs)
        range-x  (- max-x min-x)
        bin-fn   (fn [x]
                   (-> x
                       (- min-x)
                       (/ range-x)
                       (* n-bins)
                       (int)
                       (min (dec n-bins))))]
    (map bin-fn xs)))

(defn as-pmf [bins]
  (let [histogram (frequencies bins)
        total     (reduce + (vals histogram))]
    (->> histogram
         (map (fn [[k v]]
                [k (/ v total)]))
         (into {}))))

