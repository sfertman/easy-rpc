(ns easy-rpc.wire.edn
  (:require
    [clojure.edn :as edn]
    [easy-rpc.web.encoding :as enc]))


(defn bytes->str
  [bs]
  (apply str (map char bs)))

(defn serialize
  [data] (str data))

(defn deserialize
  [bytes]
  (-> bytes
      bytes->str
      edn/read-string
      enc/decode-bytes))