(ns easy-rpc.http.server
  (:require
    [clojure.java.io :as io]
    [org.httpkit.server :as http]
    [easy-rpc.util :as util]
    [reitit.ring :as ring]))

(defn api
  [rpc-handler]
  (let [rpc-handler- ;; TODO: make this handler return the entire response object, incl. status code 500. Make client throw the error on the calling side.
          (fn [& args]
            (try
              (apply rpc-handler args)
              (catch Exception e
                (prn "ERROR in rpc-handler:" e)
                e)))]
    (ring/ring-handler
      (ring/router
        [ "/"
          {:post {
            :parameters {:body some?}
            :responses {200 {:body some?}}
            :handler (fn [{msg :body}]
                        {:status 200
                          :body
                            (-> msg
                                .bytes
                                util/deserialize
                                rpc-handler-
                                util/serialize
                                io/input-stream
                                )})}}])
      (ring/routes (ring/create-default-handler)))))

(defn start
  [server]
  (let [rpc-handler (partial (:rpc-handler server) server)
        port (:port server)]
    (try
      (let [started-server (http/run-server (api rpc-handler) {:port port})]
      (println "easy-rpc server listening on" port)
      started-server)
      (catch Exception e
        (prn "ERROR:" e)
        (throw e)))))


#_(defn stop! []
  (when-not (nil? server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (server :timeout 100)
    (reset! server nil)))
