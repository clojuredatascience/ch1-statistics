(ns cljds.ch1.heatmap
  (:require [cljds.ch1.data :refer [uk-data clean-uk-data derive-uk-data ru-data rename-ru-cols derive-ru-data]]
            [quil.core :as q]
            [quil.middleware :as m]
            [clojure.string :as s]
            [incanter
             [core :refer [conj-rows $where rename-cols add-derived-column view $]]
             [charts :refer [scatter-plot histogram]]
             [excel :refer [read-xls]]]))

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

(defn ratio->grayscale [f]
  (-> f
      (Math/sqrt)
      (* 255)
      (int)
      (min 255)
      (max 0)
      (q/color)))

(defn ratio->heat [f]
  (let [colors [(q/color 0 0 255)   ;; blue
                (q/color 0 255 255) ;; turquoise
                (q/color 0 255 0)   ;; green
                (q/color 255 255 0) ;; yellow
                (q/color 255 0 0)
                (q/color 255 0 0)
                (q/color 255 0 0)
                (q/color 255 0 0)]  ;; red
        f (-> f
              (max 0.000)
              (min 0.999)
              (Math/sqrt)
              (* (dec (count colors))))]
    (q/lerp-color (nth colors f) (nth colors (inc f)) (rem f 1))))

(defn histogram-2d [xs ys n-bins]
  (-> (map vector (bin n-bins xs) (bin n-bins ys))
      (frequencies)))

(defn draw-histogram-2d [xs ys {:keys [n-bins size fill-fn]
                                :or {fill-fn ratio->grayscale}}]
  (let [data (histogram-2d xs ys n-bins)
        [width height] size
        x-scale (/ width n-bins)
        y-scale (/ height n-bins)
        max-value (apply max (vals data))
        setup (fn []
                (doseq [x (range n-bins)
                        y (range n-bins)]
                  (let [v (get data [x y] 0)
                        x-pos (* x x-scale)
                        y-pos (- height (* y y-scale))]
                    (q/fill (fill-fn (/ v max-value)))
                    (q/rect x-pos y-pos  x-scale y-scale)))
                (q/save "/tmp/heatmap.png"))]
    (q/sketch :setup setup :size size)))

(defn uk-histogram-2d []
  (let [data (->> (uk-data)
                  (clean-uk-data)
                  (derive-uk-data))]
    (histogram-2d ($ "Turnout" data) ($ "Victors Share" data) 5)))

(defn draw-uk-histogram-2d []
  (let [data (->> (uk-data)
                  (clean-uk-data)
                  (derive-uk-data))]
    (draw-histogram-2d ($ "Turnout" data) ($ "Victors Share" data)
                       {:n-bins 50 :size [250 250]})))

(defn draw-uk-heatmap-2d []
  (let [data (->> (uk-data)
                  (clean-uk-data)
                  (derive-uk-data))]
    (draw-histogram-2d ($ "Turnout" data) ($ "Victors Share" data)
                       {:n-bins 50 :size [250 250] :fill-fn ratio->heat})))

(defn ru-histogram-2d []
  (let [data (->> (ru-data)
                  (rename-ru-cols)
                  (derive-ru-data))]
    (draw-histogram-2d ($ "Turnout" data) ($ "Victors Share" data)
                       {:n-bins 50 :size [250 250]})))

(defn ru-heatmap-2d []
  (let [data (->> (ru-data)
                  (rename-ru-cols)
                  (derive-ru-data))]
    (draw-histogram-2d ($ "Turnout" data) ($ "Victors Share" data)
                       {:n-bins 50 :size [250 250] :fill-fn ratio->heat})))
