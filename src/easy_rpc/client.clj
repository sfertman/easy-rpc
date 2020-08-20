(ns easy-rpc.client
  (:require
    [clojure.walk :refer [postwalk]]
    [easy-rpc.client.http :as http]))

(defmulti client :transport)
(defmethod client :http [config] (http/client config))

(defn upsert-alias
  [a ns-sym]
  (if ((ns-aliases *ns*) a)
    (ns-unalias *ns* a))
  (alias a ns-sym))

(defn ns-fs [ns-sym] (keys (ns-publics (symbol ns-sym))))

(defn quote-inputs [inputs] (postwalk #(if (symbol? %) `(quote ~%) %) inputs))

(defn inputs->map [inputs] (apply hash-map (quote-inputs inputs)))

(defmacro defclient
  [client-name client-conf & inputs]
  `(let [inputs# ~(inputs->map inputs)
         lib-ns# (:ns ~client-conf)
         as# '~client-name
         refer# (get inputs# :refer)
         rpc-client# (client ~client-conf)
         rpc-client-ns-name# (gensym (str "easy-rpc.client$" as# "__"))
         rpc-ns# (create-ns rpc-client-ns-name#)
         fs# (ns-fs lib-ns#)]
    (doseq [f# fs#]
      (intern rpc-ns#
              (symbol f#)
              (partial rpc-client# f#)))
    (upsert-alias as# rpc-client-ns-name#)
    (doseq [f# (filter (set refer#) fs#)]
      (intern *ns*
              (symbol f#)
              (partial rpc-client# f#)))))
