(ns example.core
  (:require
    [example.server :as server]
    [example.client :as client]
    [example.mylib :as mylib]))

(defn -main
  [& args]
  (server/run-server!)
  (let [mylib-rpc client/rpc-call]
    (println "(mylib/minus 3 4) => " (mylib/minus 3 4))
    (println "(mylib-rpc 'minus 3 4) => " (mylib-rpc 'minus 3 4))
    (println "(mylib/mult 3 4) => " (mylib/mult 3 4))
    (println "(mylib-rpc 'mult 3 4) => " (mylib-rpc 'mult 3 4))
    (println "(mylib/div 3 2.5) => " (mylib/div 3 2.5))
    (println "(mylib-rpc 'div 3 2.5)" (mylib-rpc 'div 3 2.5))

    ; (println "(mylib-rpc 'hi-p-crash)" (try (mylib-rpc 'hi-p-crash) (catch Exception e e)))
    ; (println "(mylib-rpc 'div 3 0)" (try (mylib-rpc 'div 3 0) (catch Exception e e)))
    ; (println "(mylib-rpc 'mult 3 4) => " (try (mylib-rpc 'wrong-mult 3 4) (catch Exception e e)))
    ))


