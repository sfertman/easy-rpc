(ns easy-rpc.server.core
  (:require
    [easy-rpc.server.http :as http-server]
    [easy-rpc.server.web :as web-server]))

(defmulti start! :transport)

(defmethod start! :http
  [config]
  (http-server/start! config))

#_(defmethod start! :web
  [config]
  (web-server/start config))
