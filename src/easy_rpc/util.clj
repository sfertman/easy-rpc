(ns easy-rpc.util
  (:require
    [taoensso.nippy :as nippy]))

(defn serialize
  [any-data]
  (nippy/freeze any-data))

(defn deserialize
  [any-data]
  (nippy/thaw any-data))

(defn preturn
  "Prints all args and returns the last one"
  [& args]
  (apply prn args)
  (last args))
