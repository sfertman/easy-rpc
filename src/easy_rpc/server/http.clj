(ns easy-rpc.server.http
  (:require
    [clojure.java.io :as io]
    [easy-rpc.server.handler :refer [invoke-fn]]
    [easy-rpc.wire.core :refer [serialize deserialize]]
    ; [easy-rpc.server :refer [on-message start! stop! ARpcServer]]
    [org.httpkit.server :as http]
    [reitit.ring :as ring]))


(defn- deserialize-nippy
  [bytes]
  (deserialize :nippy bytes))

(defn- serialize-nippy
  [data]
  (serialize :nippy data))


; <<<<<<< HEAD
; (def wrap-deserialized-body
;   {:name ::wrap-deserialized-body
;    :description "turns bytes to clojure object"
;    :wrap (fn [handler]
;           (fn [request]
;             (-> request
;                 :body
;                 .bytes
;                 deserialize-nippy
;                 handler
;                 serialize-nippy)))})


(defn deserialized-body
  [request]
  (update
    request
    :body
    (fn [x] (-> x .bytes deserialize-nippy))))

(defn serialized-body
  [response]
  (update
    response
    :body
    (fn [x] (-> x serialize-nippy io/input-stream))))

(def wrap-serialization
  {:name ::wrap-serialization
   :description "Deserializes request body before handler and serializes response body after handler"
   :wrap (fn [handler]
          (fn [request]
            (let [response (handler (deserialized-body request))]
              (serialized-body response))))})


(defn api
  [handler]
  (ring/ring-handler
    (ring/router [
      ["/" {
        :post {
          :middleware [[wrap-serialization]]
          :handler handler}}]])
    (ring/routes (ring/create-default-handler))))
; =======
; (defn handler
;   [{f :path body :body} :as request]
;   (try
;     (let [args (deserialize :edn (.bytes body))
;           result (-> (on-message api f args) ;; need to get api somehow
;                       serialize)]
;       {:status 200
;       :body result}))
;     (catch Throwable err
;       {:status 500
;        :body (serialize err)}))

; (defn deserialize-nippy
;   [bytes]
;   (deserialize :nippy bytes))

; (defn serialize-nippy
;   [data]
;   (serialize :nippy data))

; (defn handler
;   [rpc-handler]
;   (fn [request ]
;     (pprint request)
;     (try
;       {:status 200
;         :body (->> (.bytes (:body request))
;                   deserialize-nippy
;                   rpc-handler
;                   serialize-nippy
;                   io/input-stream)}
;       (catch Throwable e
;         {:status 500
;           :body (-> e
;                     serialize
;                     io/input-stream )}))))



; (defn wrap-serialize-body
;   [handler method]
;   (fn [request]
;     (-> request
;         :body
;         (serialize method)
;         handler)))

; (defn wrap-deserialize-body
;   [handler method]
;   (fn [request]
;     (-> request
;         :body
;         (deserialize method)
;         handler)))

; ; (defn wrap-proto
; ;   [handler method]
; ;   (let [d (partial deserialize method)
; ;         s (partial serialize method)]
; ;     (fn [request]
; ;       (-> request
; ;           d
; ;           handler
; ;           s)))) ;; <- the `s` step shold also have io/intputStream conversion


; (defn wrap-response
;   [handler]
;   (fn [result]
;     (if (= "Throable" result)
;       {:status 500
;        :body (handler result)}
;       {:status 200
;        :body (handler result)} ) ))

; (defn wrap-middleware
;   [handler]
;   (fn [request]
;     (-> request
;         (wrap-deserialize-body :nippy)
;         handler
;         wrap-response
;         (wrap-serialize-body :nippy)
;         ))
;   )

; (defn api
;   [rpc-handler]
;     (ring/ring-handler
;       (ring/router
;         [[ "/"
;           {:post {
;             :handler (handler rpc-handler) }}]])
;       (ring/routes (ring/create-default-handler))))
; >>>>>>> wip -- reset me

; (defn rpc-caller
;   [config]
;   (partial invoke-fn config))


(defn handler
  [config request]
  (try
    {:status 200
     :body (invoke-fn config (:body request)) }
    (catch Throwable e
      {:status 500
       :body e})))

(defn start!
  [config]
  (let [app (api (partial handler config))
        port (:port config)]
    (let [started-server (http/run-server app {:port port})]
      (println "easy-rpc http server listening on" port)
      started-server)))

; (defmethod stop! :http
;   [_ server]
;   ;; not sure if we should save the server in an atom here on start and then stop it on stop. Need to think about it a bit more.
;   (when-not (nil? server)
;     ;; graceful shutdown: wait 100ms for existing requests to be finished
;     ;; :timeout is optional, when no timeout, stop immediately
;     (server :timeout 100)
;     (reset! server nil)))
