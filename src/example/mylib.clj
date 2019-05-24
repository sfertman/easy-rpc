(ns example.mylib)

(defn- private-minus
  [x y]
  (- x y 42))

(defn minus
  [x y]
  (- x y))

(defn- wrong-mult
  [& args]
  42)

(defn mult
  [x y]
  (* x y))

(defn div
  [x y]
  (/ x y))

(defn hi-p-crash
  []
  (if (= 0 (apply * (take 4 (repeatedly #(rand-int 2)))))
    (throw (RuntimeException. "AAAAAAAA"))
    "All is well... For now!"))
