(ns easy-rpc.wire.core
  (:require
    [easy-rpc.wire.edn :as edn]
    [taoensso.nippy :as nippy]))

(defmulti serialize (fn [proto _] proto))
(defmulti deserialize (fn [proto _] proto))

(defmethod serialize :edn [_ data] (edn/serialize data))
(defmethod deserialize :edn [_ bytes] (edn/deserialize bytes))

(defmethod serialize :nippy [_ data] (nippy/freeze data))
(defmethod deserialize :nippy [_ bytes] (nippy/thaw bytes))
