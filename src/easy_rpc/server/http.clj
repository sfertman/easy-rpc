(ns easy-rpc.server.http
  (:require
    [easy-rpc.server.handler :refer [invoke-fn]]
    [easy-rpc.wire.core :refer [serialize deserialize]]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))

(defn deserialized-body
  [wire request]
  (update
    request
    :body
    (partial deserialize wire)))

(defn serialized-body
  [wire response]
  (update
    response
    :body
    (partial serialize wire)))

(def wrap-serialization
  {:name ::wrap-serialization
   :description "Deserializes request body before handler and serializes response body after handler"
   :wrap (fn [handler config]
          (let [wire (:serialization config)]
            (fn [request]
              (let [response (handler (deserialized-body wire request))]
                (serialized-body wire response)))))})

(defn api
  [config handler]
  (ring/ring-handler
    (ring/router [
      ["/" {
        :post {
          :middleware [[wrap-serialization config]]
          :handler (partial handler config)}}]])
    (ring/routes (ring/create-default-handler))))

(defn handler
  [config request]
  (try
    {:status 200
     :body (invoke-fn config (:body request)) }
    (catch Throwable e
      {:status 500
       :body e})))

(defn start!
  [config]
  (let [app (api config handler)
        port (:port config)]
    (let [started-server (http/run-server app {:port port})]
      (println "easy-rpc http server listening on" port)
      started-server)))
