(ns easy-rpc.server
  (:require
    [easy-rpc.http.server :as http-server]
    [easy-rpc.web.server :as web-server]))

(defn on-message
  "Handles message by calling the api function. Throws NullPointerException if function f-name is not found in the rpc namespace."
  [server [f-name args]]
  (if-let [f (get (:api server) (symbol f-name))]
    (apply f args)
    (throw (NullPointerException. (str "Function " f-name " is not defined!")))))

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
  (cond
    (= :http (:transport server))
      (http-server/start server)
    (= :web (:transport server))
      (web-server/start server)))

#_(defn stop!
  "Returns nil; takes a server and stops it"
  [server]
  (http-server/stop! server))