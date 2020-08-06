(ns easy-rpc.wire.core
  (:require
    [easy-rpc.wire.edn :as edn]
    [easy-rpc.wire.nippy :as nippy]))

(defmulti serialize (fn [proto _] proto))
(defmulti deserialize (fn [proto _] proto))

(defmethod serialize nil [_ data] (serialize :default data))
(defmethod deserialize nil [_ bytes] (deserialize :default bytes))

(defmethod serialize :default [_ data] (serialize :nippy data))
(defmethod deserialize :default [_ bytes] (deserialize :nippy bytes))

(defmethod serialize :edn [_ data] (edn/serialize data))
(defmethod deserialize :edn [_ bytes] (edn/deserialize bytes))

(defmethod serialize :nippy [_ data] (nippy/serialize data))
(defmethod deserialize :nippy [_ bytes] (nippy/deserialize bytes))
