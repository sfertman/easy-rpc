(ns easy-rpc.http.server
  (:require
    [clojure.java.io :as io]
    [easy-rpc.util :as util]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))

(defn api
  [rpc-handler]
    (ring/ring-handler
      (ring/router
        [[ "/"
          {:post {
            :handler (fn [{msg :body}]
              (try
                {:status 200
                 :body (-> msg
                           .bytes
                           util/deserialize
                           rpc-handler
                           util/serialize
                           io/input-stream)}
                (catch Throwable e
                  {:status 500
                   :body (-> e
                             util/serialize
                             io/input-stream )})))}}]])
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
