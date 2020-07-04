(ns easy-rpc.client
  (:require
    [clojure.set :refer [map-invert]]
    [easy-rpc.http.client :as http-client]))

(defn client
  [cfg]
  (fn [f & args]
    (http-client/send-message cfg [f args])))

(def config {:http {:ns "example.mylib"
        :host "localhost"
        :transport :http
        :port 3101}
 :web {:ns "example.mylib"
       :host "localhost"
       :transport :web
       :port 3102}})

(defn get-ns-alias
  ([nn] (get-ns-alias nn *ns*))
  ([nn nspc]
    (let [nn* (ns-name (the-ns (symbol nn)))]
      (some
        (fn [[a n]] (if (= nn* (ns-name n)) a))
        (ns-aliases nspc)))))

(defmacro defclient
  [client-name client-conf]
  `(let [rpc-client# (client ~client-conf)
         rpc-ns# (create-ns '~client-name)
         fs# (keys (ns-publics (symbol (:ns ~client-conf))))]
    (doseq [f# fs#]
      (intern rpc-ns#
              (symbol f#)
              (fn [& args#] (apply rpc-client# f# args#))))))