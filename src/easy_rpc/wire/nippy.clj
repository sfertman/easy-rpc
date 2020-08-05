(ns easy-rpc.wire.nippy
  (:require
    [clojure.java.io :as io]
    [taoensso.nippy :as nippy]))

(defn serialize [data] (-> data nippy/freeze io/input-stream))
(defn deserialize [bytes] (-> bytes .bytes nippy/thaw))
