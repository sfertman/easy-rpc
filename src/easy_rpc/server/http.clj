(ns easy-rpc.server.http
  (:require
    [clojure.java.io :as io]
    [easy-rpc.server.handler :refer [invoke-fn]]
    [easy-rpc.wire.core :refer [serialize deserialize]]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))

(def wrap-serialization
  {:name ::wrap-serialization
   :description "Deserializes request body before handler and serializes response body after handler"
   :wrap (fn [handler [deserialized-body serialized-body]]
          (fn [request]
            (let [response (handler (deserialized-body request))]
              (serialized-body response))))})

(defn- deserialize-nippy
  [bytes]
  (deserialize :nippy (.bytes bytes)))

(defn- serialize-nippy
  [data]
  (io/input-stream (serialize :nippy data)))

(defn deserialized-body
  [request]
  (update
    request
    :body
    deserialize-nippy))

(defn serialized-body
  [response]
  (update
    response
    :body
    serialize-nippy))

(defn api
  ([handler]
    (api handler [deserialized-body serialized-body]))
  ([handler serialization]
    (ring/ring-handler
      (ring/router [
        ["/" {
          :post {
            :middleware [[wrap-serialization serialization]]
            :handler handler}}]])
      (ring/routes (ring/create-default-handler)))))

(defn handler
  [config request]
  (try
    {:status 200
     :body (invoke-fn config (:body request)) }
    (catch Throwable e
      {:status 500
       :body e})))

(defn start!
  [config & {:keys [serialization]}]
  (let [h-fn (partial handler config)
        app (if serialization (api h-fn serialization) (api h-fn) )
        port (:port config)]
    (let [started-server (http/run-server app {:port port})]
      (println "easy-rpc http server listening on" port)
      started-server)))
