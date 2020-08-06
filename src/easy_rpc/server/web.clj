(ns easy-rpc.server.web
  (:require
    [easy-rpc.server.handler :refer [invoke-fn]]
    [easy-rpc.wire.core :refer [serialize deserialize]]
    [easy-rpc.server.http :as http]))

(defn start!
  [config]
  (http/start! (assoc config :serialization :edn)))
  ;; NOTE: for now webserver will overwrite serialization with my custom made up one