(ns easy-rpc.client
  (:require
    [easy-rpc.http.client :as http-client]))

(defn client
  [cfg]
  (fn [f & args]
    (http-client/send-message cfg [f args])))

(defn upsert-alias
  [a ns-sym]
  (if ((ns-aliases *ns*) a)
    (ns-unalias *ns* a))
  (alias a ns-sym))

(defmacro defclient
  [client-name client-conf]
  `(let [rpc-client# (client ~client-conf)
         rpc-client-ns-name# (gensym (str "easy-rpc.client$" '~client-name "__"))
         rpc-ns# (create-ns rpc-client-ns-name#)
         fs# (keys (ns-publics (symbol (:ns ~client-conf))))]
    (doseq [f# fs#]
      (intern rpc-ns#
              (symbol f#)
              (fn [& args#] (apply rpc-client# f# args#))))
    (upsert-alias '~client-name rpc-client-ns-name#)))