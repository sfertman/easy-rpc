(ns easy-rpc.proto)

(defmulti serialize [proto _] proto)
(defmulti deserialize [proto _] proto)
