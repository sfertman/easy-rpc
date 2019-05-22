(ns example.core
  (:require
    [example.server :as server]
    [example.client :as client]
    [example.mylib :as mylib]))

(defn -main
  [& args]
  (server/run-server!)
  (client/init!)
  (let [mylib-rpc client/rpc-call]
    (println "(mylib/minus 3 4) => " (mylib/minus 3 4))
    (println "(mylib-rpc 'minus 3 4) => " (mylib-rpc 'minus 3 4))
    (println "(mylib/mult 3 4) => " (mylib/mult 3 4))
    (println "(mylib-rpc 'mult 3 4) => " (mylib-rpc 'mult 3 4))))


