(ns example.core
  (:require
    [clojure.edn :as edn]
    [easy-rpc.web.encoding :refer [decode-hex]]
    [easy-rpc.server :as rpc-server]
    [easy-rpc.client :as rpc-client :refer [client]]
    [example.mylib :as mylib]))

(def rpc-config
  (-> "./src/example/rpc-config.edn" slurp edn/read-string))

(defn run-server!
  [server config]
  (reset!
    server
    (rpc-server/start
      (rpc-server/create-server config))))

(def http-server (atom nil))
(def web-server (atom nil))

(def mylib-rpc (client (:http rpc-config)))

(defn -main
  [& args]
  (run-server! http-server (:http rpc-config))
  (run-server! web-server (:web rpc-config))
  (println "(mylib/minus 3 4)\n  =>" (mylib/minus 3 4))
  (println "(mylib-rpc 'minus 3 4)\n  =>" (mylib-rpc 'minus 3 4))
  (println "(mylib/mult 3 4)\n  =>" (mylib/mult 3 4))
  (println "(mylib-rpc 'mult 3 4)\n  =>" (mylib-rpc 'mult 3 4))
  (println "(mylib/div 3 2.5)\n  =>" (mylib/div 3 2.5))
  (println "(mylib-rpc 'div 3 2.5)\n  =>" (mylib-rpc 'div 3 2.5))
  (println "(mylib/bytes->hex-all (decode-hex {:hello [\"10948899abcdef\"] :dont-byte \"stringy-tring\"}))\n  =>" (mylib/bytes->hex-all {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"}))
  (println "(mylib-rpc 'bytes->hex-all (decode-hex {:hello [\"10948899abcdef\"] :dont-byte \"stringy-tring\"}))\n  =>" (mylib-rpc 'bytes->hex-all {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"}))
  ; (println "(mylib-rpc 'hi-p-crash)" (try (mylib-rpc 'hi-p-crash) (catch Exception e e)))
  ; (println "(mylib-rpc 'div 3 0)" (try (mylib-rpc 'div 3 0) (catch Exception e e)))
  ; (println "(mylib-rpc 'mult 3 4)\n  =>" (try (mylib-rpc 'wrong-mult 3 4) (catch Exception e e)))
  )


