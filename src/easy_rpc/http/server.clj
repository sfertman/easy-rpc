(ns easy-rpc.http.server
  (:require
    [clojure.java.io :as io]
    [easy-rpc.util :as util]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))

(def wrap-deserialized-body
  {:name ::wrap-deserialized-body
   :description "turns bytes to clojure object"
   :wrap (fn [handler]
          (fn [request]
            (-> request
                :body
                .bytes
                util/deserialize
                handler)))})

(defn response
  [rpc-handler args]
  (try
    {:status 200
     :body (rpc-handler args)}
    (catch Throwable e
      {:status 500
       :body e})))

(defn serialized-body
  [response]
  (update
    response
    :body
    (fn [x] (-> x util/serialize io/input-stream))))

(defn handler
  [rpc-handler args]
  (serialized-body (response rpc-handler args)))

(defn api
  [rpc-handler]
  (ring/ring-handler
    (ring/router [
      ["/" {
        :post {
          :middleware [[wrap-deserialized-body]]
          :handler (partial handler rpc-handler )}}]])
          ;; ^ Make this a constant required from rpc-server or something;
          ;; api doesn't really need to be a function
    (ring/routes (ring/create-default-handler))))

(defn start
  [server]
  (let [rpc-handler (partial (:rpc-handler server) server)
        port (:port server)
        app (api rpc-handler)]
    (try
      (let [started-server (http/run-server app {:port port})]
      (println "easy-rpc http server listening on" port)
      started-server)
      (catch Exception e
        (prn "ERROR:" e)
        (throw e)))))


(defn stop!
  [server]
  (when-not (nil? server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (server :timeout 100)
    (reset! server nil)))
