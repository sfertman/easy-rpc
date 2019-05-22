(ns easy-rpc.server
  (:require
    [easy-rpc.http.server :as http-server]))

(defn on-message
  "Handles message by calling the api function"
  [server [f-name args]]
  (let [f (get (:api server) (symbol f-name))]
    (apply f args)))

(defn create-server
  "Returns an rpc server config with lib functions attached"
  [config]
  (let [ns-name (:ns config)
        api (ns-publics (symbol ns-name))]
    (assoc config
      :api api
      :rpc-handler on-message)))

(defn start
  "Returns a started ring server; takes rpc server config"
  [server]
  (http-server/start server))

#_(defn stop!
  "Returns nil; takes a server and stops it"
  [server]
  (http-server/stop! server))