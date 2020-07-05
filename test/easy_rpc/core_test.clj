(ns easy-rpc.core-test
  (:require
    [clojure.test :refer :all]
    [clojure.pprint :refer [pprint]]
    [easy-rpc.client :refer [defclient]]
    [easy-rpc.mylib :as mylib]
    [easy-rpc.mylib :as mylib-local]
    [easy-rpc.mylib :as mylib-remote]
    [easy-rpc.server :as server]
    [easy-rpc.web.encoding :refer [decode-hex]]))

(def rpc-config {
  :ns "easy-rpc.mylib"
  :host "localhost"
  :transport :http
  :port 3101})

(defclient mylib-added rpc-config)

(clojure.pprint/pprint ((ns-aliases *ns*) 'mylib-remote))
(defclient mylib-remote rpc-config)
(clojure.pprint/pprint ((ns-aliases *ns*) 'mylib-remote))

(deftest examples
  (server/start! rpc-config)
  (testing "add alias"
    (is (= (mylib-local/minus 3 4) (mylib-added/minus 3 4)))
    (is (= (mylib-local/mult 3 4) (mylib-added/mult 3 4)))
    (is (= (mylib-local/div 3 2.5) (mylib-added/div 3 2.5)))
    (is (= (mylib-local/bytes->hex-all
            {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"})
          (mylib-added/bytes->hex-all
            {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"})))
    (try
      (mylib-added/div 3 0)
      (is (= 1 0)) ;; will fail if exception not thrown
      (catch RuntimeException re
        (is (= 1 1)))))

  (testing "override alias"
    (is (= (mylib-local/minus 3 4) (mylib-remote/minus 3 4)))
    (is (= (mylib-local/mult 3 4) (mylib-remote/mult 3 4)))
    (is (= (mylib-local/div 3 2.5) (mylib-remote/div 3 2.5)))
    (is (= (mylib-local/bytes->hex-all
            {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"})
          (mylib-remote/bytes->hex-all
            {:hello [(decode-hex "10948899abcdef")] :dont-byte "stringy-tring"})))
    (try
      (mylib-remote/div 3 0)
      (is (= 1 0)) ;; will fail if exception not thrown
      (catch RuntimeException re
        (is (= 1 1)))))
)

