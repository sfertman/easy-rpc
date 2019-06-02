(ns example.core
  (:require
    [example.server :as server]
    [example.client :as client]
    [example.mylib :as mylib]))

(defn hex->bytes
  "Convert a hex-encoded string to bytes"
  [^String s]
  ;; XXX(acg): I am told this implementation has limitations but I
  ;; don't know what those are.
  ;; xxx gt will drop a byte if odd-numbered length
  ;; will also ignore leading
    (into-array Byte/TYPE
              (map (fn [[x y]]
                      (unchecked-byte (Integer/parseInt (str x y) 16)))
                    (partition 2 s))))


(defn -main
  [& args]
  (server/run-server!)
  (let [mylib-rpc client/rpc-call]
    (println "(mylib/minus 3 4)\n  =>" (mylib/minus 3 4))
    (println "(mylib-rpc 'minus 3 4)\n  =>" (mylib-rpc 'minus 3 4))
    (println "(mylib/mult 3 4)\n  =>" (mylib/mult 3 4))
    (println "(mylib-rpc 'mult 3 4)\n  =>" (mylib-rpc 'mult 3 4))
    (println "(mylib/div 3 2.5)\n  =>" (mylib/div 3 2.5))
    (println "(mylib-rpc 'div 3 2.5)\n  =>" (mylib-rpc 'div 3 2.5))
    (println "(mylib/bytes->hex-all (hex->bytes {:hello [\"10948899abcdef\"] :dont-byte \"stringy-tring\"}))\n  =>" (mylib/bytes->hex-all {:hello [(hex->bytes "10948899abcdef")] :dont-byte "stringy-tring"}))
    (println "(mylib-rpc 'bytes->hex-all (hex->bytes {:hello [\"10948899abcdef\"] :dont-byte \"stringy-tring\"}))\n  =>" (mylib-rpc 'bytes->hex-all {:hello [(hex->bytes "10948899abcdef")] :dont-byte "stringy-tring"}))
    ; (println "(mylib-rpc 'hi-p-crash)" (try (mylib-rpc 'hi-p-crash) (catch Exception e e)))
    ; (println "(mylib-rpc 'div 3 0)" (try (mylib-rpc 'div 3 0) (catch Exception e e)))
    ; (println "(mylib-rpc 'mult 3 4)\n  =>" (try (mylib-rpc 'wrong-mult 3 4) (catch Exception e e)))
    ))


