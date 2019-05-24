(ns example.client
  (:require
    [clojure.edn :as edn]
    [easy-rpc.client :as rpc-client]))

(def rpc-config
  (-> "./src/example/rpc-config.edn" slurp edn/read-string))

(def client (rpc-client/create-client rpc-config))

(defn rpc-call
  [& args]
  (apply (:send-message client) args))