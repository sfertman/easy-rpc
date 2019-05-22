(ns easy-rpc.client
  (:require
    [easy-rpc.http.client :as http-client]))

(defn rpc-call
  [client func & args]
    (http-client/send-message client [func args]))

(defn create-client
  [config]
  (assoc config :send-message (partial rpc-call config)))

#_(defn- gen-ns!
  "Work in progress that might be impossible to achieve"
  [client ns-]
  (let [rpc-ns ns- #_(create-ns (symbol (str ns-)) #_(symbol ns-))]
    (loop [[f-name & f-names] (map symbol (:api client))]
      (intern
        rpc-ns
        f-name
        (partial client f-name))
      (if (some? f-names)
        (recur f-names)
        rpc-ns))))

(defn init!
  [client]
  (let [api-spec ((:send-message client) "get-api-spec")]
    (assoc client
      :api (:api api-spec)
      :ns (:ns api-spec))))