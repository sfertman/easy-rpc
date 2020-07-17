(ns easy-rpc.server
  (:require
    [easy-rpc.http.server :as http-server]
    [easy-rpc.web.server :as web-server]))

(defn on-message
  [api [f-name args]]
  (if-let [f (get api (symbol f-name))]
    (apply f args)
    (throw (NullPointerException. (str "Function " f-name " is not defined!")))))

(defn api
  [config]
  (-> config :ns symbol ns-publics))

(defmulti start! (fn [config] (:transport config)))
(defmethod start! :http
  [config]
  (http-server/start (assoc config :api (api config) :rpc-handler on-message)))

(defmethod start! :web
  [config]
  (web-server/start (assoc config :api (api config) :rpc-handler on-message)))

#_(defn stop!
  "Returns nil; takes a server and stops it"
  [server]
  (http-server/stop! server))