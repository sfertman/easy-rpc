(ns easy-rpc.server
  (:require
    [easy-rpc.server.http :as http-server]
    [easy-rpc.server.web :as web-server]))

(defmulti start! (fn [cfg & _] (:transport cfg)))

(defmethod start! :http
  [config & args]
  (apply http-server/start! config args))

(defmethod start! :web
  [config & args]
  (apply web-server/start! config args))

(defn stop! [server]
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))
