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
  (let [payload (serialize-nippy msg)
        response @(http/post (url config)
                               {:as :stream
                                :body payload})
        body (-> response :body deserialize-nippy)]
    (if (= 500 (:status response))
      (throw body))
    body))

(defn client
  [config]
  (fn [f & args]
    (send-message config [f args])))