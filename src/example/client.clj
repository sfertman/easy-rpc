(ns example.client
  (:require
    [easy-rpc.client :as rpc-client]))

(def client-config {
  :host "localhost"
  :transport :http ;; only :http supported for now (ignored)
  :port 3101})

(def client (rpc-client/create-client client-config))

(defn rpc-call
  [& args]
  (apply (:send-message client) args))