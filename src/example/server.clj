(ns example.server
  (:require
    [easy-rpc.server :as rpc]))

(def server-config {
  :ns "example.mylib"
  :host "localhost"
  :transport :http ;; only :http supported for now (ignored)
  :port 3101})

(def server (atom nil))

(defn run-server!
  []
  (reset!
    server
    (rpc/start
      (rpc/create-server server-config))))
