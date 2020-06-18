(ns easy-rpc.core-test
  (:require
    [clojure.test :refer :all]
    [easy-rpc.client :refer [client]]
    [easy-rpc.mylib :as mylib]
    [easy-rpc.server :refer [start!]]
    [easy-rpc.web.encoding :refer [decode-hex]]))

(def rpc-config {
  :ns "easy-rpc.mylib"
  :host "localhost"
  :transport :http
  :port 3101})

(def mylib-μ (client rpc-config))

(deftest examples
  (start! rpc-config)
  (is (= (mylib/minus 3 4) (mylib-μ 'minus 3 4)))
  (is (= (mylib/mult 3 4) (mylib-μ 'mult 3 4)))
  (is (= (mylib/div 3 2.5) (mylib-μ 'div 3 2.5)))
  (is (= (mylib/bytes->hex-all
           {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"})
         (mylib-μ 'bytes->hex-all
           {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"})))
  (try
    (mylib-μ 'div 3 0)
    (is (= 1 0)) ;; will fail if exception not thrown
    (catch RuntimeException re
      (is (= 1 1))))
)

