(ns easy-rpc.server.handler)

(defn api
  [config]
  (-> config :ns symbol ns-publics))

(defn ex-fn-undefined
  [f-name]
  (NullPointerException. (str "Function " f-name " is not defined!")))

(defn invoke-fn
  [config [f-name args]]
  (if-let [f (get (api config) (symbol f-name))]
    (apply f args)
    (throw (ex-fn-undefined f-name))))