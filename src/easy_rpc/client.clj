(ns easy-rpc.client
  (:require
    [clojure.walk :refer [postwalk]]
    [easy-rpc.client.http :as http]))

(defmulti client :transport)
(defmethod client :http [config] (http/client config))

(defn- quote-inputs [inputs] (postwalk #(if (symbol? %) `(quote ~%) %) inputs))

(defn- inputs->map [inputs] (apply hash-map (quote-inputs inputs)))
;; poor man's destructure

(defn ns-fs [ns] (keys (ns-publics ns)))

(defn intern-multi
  "Interns multiple mappings. `mappings` map var names to values to intern into target ns."
  [ns-or-sym mappings]
  (doseq [[name value] mappings]
    (intern ns-or-sym name value)))

(defn fs-mappings [client fs] (for [f fs] [f (partial client f)]))

(defn upsert-alias
  [a ns-sym]
  (if ((ns-aliases *ns*) a)
    (ns-unalias *ns* a))
  (alias a ns-sym))

(defmacro defclient
  [client-name client-conf & inputs]
  `(let [inputs# ~(inputs->map inputs)
         alias# '~client-name
         refer# (get inputs# :refer)
         rpc-client# (client ~client-conf)
         rpc-client-ns-name# (gensym (str "easy-rpc.client$" alias# "__"))
         rpc-ns# (create-ns rpc-client-ns-name#)
         fs# (-> ~client-conf :ns symbol ns-fs)
         fsm# (fs-mappings rpc-client# fs#)]
    (intern-multi rpc-ns# fsm#)
    (upsert-alias alias# rpc-client-ns-name#)
    (intern-multi *ns* (filter #((set refer#) (first %)) fsm#))))
