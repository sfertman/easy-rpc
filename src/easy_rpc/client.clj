(ns easy-rpc.client
  (:require
    [easy-rpc.http.client :as http-client]))

(defn rpc-call
  [client func & args]
  (http-client/send-message client [func args]))

(defn client
  [cfg]
  (fn [f & args]
    (http-client/send-message cfg [f args])))
