(ns easy-rpc.proto.edn)
  (:require
    [easy-rpc.proto]))

(defmethod easy-rpc.proto/serialize :edn
  [data] (str data))

(defmethod easy-rpc.proto/deserialize :edn
  [bytes]
  (-> bytes
      bytes->str
      edn/read-string
      enc/decode-bytes))