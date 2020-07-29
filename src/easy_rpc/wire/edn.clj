(ns easy-rpc.wire.edn
  (:require
    [clojure.edn :as edn]
    [clojure.string]
    [clojure.walk]))

(defn decode-bin
  [s]
  (into-array
    Byte/TYPE
    (loop [[c1 c2 c3 c4 c5 c6 c7 c8 & cs] s
           result []]
      (let [b (str c1 c2 c3 c4 c5 c6 c7 c8)]
        (if (nil? c1)
          result
          (recur cs (conj result (unchecked-byte (Integer/parseInt b 2)))))))))

(defn decode-hex
  [s]
  (into-array
    Byte/TYPE
    (loop [[c1 c2 & cs] s
           result []]
      (let [b (str c1 c2)]
        (if (nil? c1)
          result
          (recur cs (conj result (unchecked-byte (Integer/parseInt b 16)))))))))

#_(defn decode-base64
  [s]
  44)

(defonce decoders
  {"bin" decode-bin
   "hex" decode-hex
   #_#_"base64" decode-base64})

(def enc (set (map name (keys decoders))))
(defn enc-prefix-len [encoding] (+ 5 (.length encoding)))
; (def max-prefix-len (+ 5 (apply max (map #(.length (str %)) enc))))
(def max-enc-prefix-len (apply max (map enc-prefix-len enc)))
(def patt (re-pattern (str "^:b:(" (clojure.string/join "|" enc) ")::(.*)")))


(defn get-encoding
  "Returns encoding of string s if s is of the form :b:<encoding>::<...> and nil otherwise."
  [^String s]
  (if-let [matches (re-matches patt (.substring s 0 (min max-enc-prefix-len (.length s))))]
    (second matches)))

(defn decode
  "Given string s and encoding, returns decoded bytes. Throws null pointer if encoding does not exist."
  [^String s encoding]
  (if-let [decoder (get decoders encoding)]
    (decoder (.substring s (enc-prefix-len encoding)))
    (throw (NullPointerException. (str "Encoding " encoding " is not defined!")))))


(defn- postwalk-decoder
  [s]
  (if (string? s)
    (if-let [encoding (get-encoding s)]
      (decode s encoding)
      s)
    s))

(defn decode-bytes
  [form]
  (clojure.walk/postwalk postwalk-decoder form))

(defn bytes->str
  [bs]
  (apply str (map char bs)))

(defn serialize
  [data] (str data))

(defn deserialize
  [bytes]
  (-> bytes
      bytes->str
      edn/read-string
      decode-bytes))