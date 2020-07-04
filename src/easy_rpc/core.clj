(ns easy-rpc.core
  (:require
    [easy-rpc.server :as server]
    [easy-rpc.client :as client]))


(defn start-server! [config] (server/start! config))

(defmacro defclient [config]
  `(hello!))