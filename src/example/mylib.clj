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
