(ns easy-rpc.wire.core
  (:require
    [easy-rpc.wire.edn :as edn]
    [easy-rpc.wire.nippy :as nippy]
    [taoensso.nippy :as nippy-og]))

(defmulti serialize (fn [proto _] proto))
(defmulti deserialize (fn [proto _] proto))

(defmethod serialize :default [_ data] (serialize :nippy-1 data))
(defmethod deserialize :default [_ bytes] (deserialize :nippy-1 bytes))

(defmethod serialize :edn [_ data] (edn/serialize data))
(defmethod deserialize :edn [_ bytes] (edn/deserialize bytes))

(defmethod serialize :nippy [_ data] (nippy-og/freeze data))
(defmethod deserialize :nippy [_ bytes] (nippy-og/thaw bytes))
;; FIXME: ^ delete this

(defmethod serialize :nippy-1 [_ data] (nippy/serialize data))
(defmethod deserialize :nippy-1 [_ bytes] (nippy/deserialize bytes))
