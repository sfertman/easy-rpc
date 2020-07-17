(ns easy-rpc.proto.nippy
  (:require
    [easy-rpc.proto]
    [taoensso.nippy :as nippy]))

(defmethod easy-rpc.proto/serialize :nippy
  [data] (nippy/freeze data))

(defmethod easy-rpc.proto/deserialize :nippy
  [bytes] (nippy/thaw bytes))