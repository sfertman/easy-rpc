(ns easy-rpc.http.client
  (:require
    [clojure.java.io :as io]
    [org.httpkit.client :as http]
    [easy-rpc.util :as util]))

(defn url [config] (str "http://" (:host config) ":" (:port config) "/"))

(defn send-message
  [config msg]
  (let [payload (-> msg util/serialize io/input-stream)
        result (-> @(http/post (url config)
                               {:as :stream
                                :body payload}))]
    (when (= 500 (:status result))
      (throw (RuntimeException.
        (str {
          :payload msg
          :error (-> result :body .bytes util/deserialize)}))))
      (util/deserialize (.bytes (:body result)))))
