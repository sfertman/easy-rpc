(ns easy-rpc.web.server
  (:require
    [clojure.edn :as edn]
    [easy-rpc.web.encoding :as enc]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))

(defn bytes->str
  [bs]
  (apply str (map char bs)))

(defn api
  [rpc-handler]
    (ring/ring-handler
      (ring/router
        [ ["/"
            {:post {
             :handler (fn [{msg :body}]
               (try
                 {:status 200
                  :body (-> msg
                            .bytes
                            bytes->str
                            edn/read-string
                            enc/decode-bytes
                            rpc-handler
                            str)}
                 (catch Throwable e
                   {:status 500
                    :body (-> e str)})))}}]])
      (ring/routes (ring/create-default-handler))))

(defn start
  [server]
  (let [rpc-handler (partial (:rpc-handler server) server)
        port (:port server)
        app (api rpc-handler)]
    (try
      (let [started-server (http/run-server app {:port port})]
      (println "easy-rpc web server listening on" port)
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