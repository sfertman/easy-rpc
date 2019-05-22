(ns example.client
  (:require
    [easy-rpc.client :as rpc-client]))

(def client-config {
  :host "localhost"
  :transport :http ;; only :http supported for now (ignored)
  :port 3101})


(def client (atom {}))
;; NOTE: we need this atom around because gotta make sure init happens after an rpc server is started; if we attempt to define and init the client in a simple def, init will fail because it need the server to do that. Another way is to avoid it is to create and init a new client on every rpc call but that's hardly efficient.

(defn- rpc-call-dont-do-that
  [& args]
  (apply
    (->
      client-config
      rpc-client/create-client
      rpc-client/init!
      :send-message)
   args))

(defn init! []
  (reset!
    client
    (-> client-config
        rpc-client/create-client
        rpc-client/init!)))

(defn rpc-call
  [& args]
  (apply (:send-message @client) args))