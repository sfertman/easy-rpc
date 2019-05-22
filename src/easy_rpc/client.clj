(ns easy-rpc.client
  (:require
    [easy-rpc.http.client :as http-client]))

(defn rpc-call
  [client func & args]
    (http-client/send-message client [func args]))

(defn create-client
  [config]
  (assoc config :send-message (partial rpc-call config)))
