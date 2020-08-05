(ns easy-rpc.client.http
  (:require
    [clojure.java.io :as io]
    [org.httpkit.client :as http]
    [easy-rpc.wire.core :refer [serialize deserialize]]))

(defn- deserialize-nippy
  [bytes]
  (deserialize :nippy (.bytes bytes)))

(defn- serialize-nippy
  [data]
  (io/input-stream (serialize :nippy data)))

(defn url [config] (str "http://" (:host config) ":" (:port config) "/"))

(defn send-message
  [config msg]
  (let [wire (or (:wire config) :default)
        req {:as :stream :body (serialize wire msg)}
        response @(http/post (url config) req)
        body (deserialize wire (:body response))]
    (if (= 500 (:status response))
      (throw body))
    body))

(defn client
  [config]
  (fn [f & args]
    (send-message config [f args])))