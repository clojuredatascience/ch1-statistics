(ns cljds.ch1.stats)

(defn mean [as]
  (let [n (count as)]
    (* (/ 1 n)
       (apply + as))))

(defn mean [xs]
  (/ (apply + xs)
     (count xs)))

(defn median [xs]
  (let [xs  (sort xs)
        n   (count xs)
        mid (int (/ n 2))]
    (if (odd? n)
      (nth xs mid)
      (/ (+ (nth xs mid) (nth xs (dec mid))) 2))))

(defn quantile
  "Returns the value corresponding to the qth quantile
   where q is a value between 0 and 1"
  [xs q]
  (let [n (dec (count xs))
        i (-> (* n q) (+ 1/2) int)]
    (nth xs i)))

(defn percentile [value xs]
  (-> (sort xs)
      (.indexOf value)
      (/ (dec (count xs)))
      (* 100)))

(defn bin [n-bins xs]
  (let [min-x    (apply min xs)
        range-x  (- (apply max xs) min-x)
        max-bin  (dec n-bins)
        bin-fn   (fn [x]
                   (-> x
                       (- min-x)
                       (/ range-x)
                       (* n-bins)
                       int
                       (min max-bin)))]
    (map bin-fn xs)))

(defn variance [xs]
  (let [m (mean xs)
        square-error (fn [x]
                       (Math/pow (- x m) 2))]
    (mean (map square-error xs))))

(defn standard-deviation [xs]
  (Math/sqrt (variance xs)))

(defn normalize [histogram]
  (let [total (->> (vals histogram)
                   (apply +)
                   double)]
    (->> histogram
         (map (fn [[x f]] [x (/ f total)]))
         (into {}))))

(defn pmf [data]
  (-> data
      frequencies
      normalize))
