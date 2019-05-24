(ns example.server
  (:require
    [clojure.edn :as edn]
    [easy-rpc.server :as rpc]))

(def rpc-config
  (-> "./src/example/rpc-config.edn" slurp edn/read-string))

(def server (atom nil))

(defn run-server!
  []
  (reset!
    server
    (rpc/start
      (rpc/create-server rpc-config))))
