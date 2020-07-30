(ns easy-rpc.server.web
  (:require
    [easy-rpc.server.handler :refer [invoke-fn]]
    [easy-rpc.wire.core :refer [serialize deserialize]]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))

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

(def wrap-serialization
  {:name ::wrap-serialization
   :description "Deserializes request body before handler and serializes response body after handler"
   :wrap (fn [handler]
          (fn [request]
            (let [response (handler (deserialized-body request))]
              (serialized-body response))))})

(defn api
  [handler]
  (ring/ring-handler
    (ring/router [
      ["/" {
        :post {
          :middleware [[wrap-serialization]]
          :handler handler}}]])
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
  (let [app (api (partial handler config))
        port (:port config)]
    (let [started-server (http/run-server app {:port port})]
      (println "easy-rpc web server listening on" port)
      started-server)))
