(ns easy-rpc.server.web
  (:require
    [easy-rpc.server.handler :refer [invoke-fn]]
    [easy-rpc.wire.core :refer [serialize deserialize]]
    [easy-rpc.server.http :as http]))

(defn- deserialize-edn
  [bytes]
  (deserialize :edn bytes))

(defn- serialize-edn
  [data]
  (serialize :edn data))

(defn deserialized-body
  [request]
  (update
    request
    :body
    (fn [x] (-> x .bytes deserialize-edn))))

(defn serialized-body
  [response]
  (update
    response
    :body
    (fn [x] (-> x serialize-edn))))

(defn start!
  [config
   & {:keys [serialization]
      :or {serialization [deserialized-body serialized-body]}}]
  (http/start! config :serialization serialization))