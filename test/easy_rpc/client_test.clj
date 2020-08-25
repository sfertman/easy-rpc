(ns easy-rpc.client-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [easy-rpc.client :as sut]
    [easy-rpc.mylib]))

(deftest intern-multi-test
  (testing "Should intern multiple mappings"
    (sut/intern-multi *ns* {'x 42 'y 43 'z (constantly "spam!")})
    ;; ^ works with array of tuples or map
    (is (= 42 @(resolve 'x)))
    (is (= 43 @(resolve 'y)))
    (is (= "spam!" (@(resolve 'z))))))

(deftest upsert-alias-test
  (testing "Should create new alias if not exist"
    (is (not (contains? (ns-aliases *ns*) 'zzz)))
    (sut/upsert-alias 'zzz 'clojure.pprint)
    (is (contains? (ns-aliases *ns*) 'zzz)))
  (testing "Should overwrite existing alias if exists"
    (is (= 'clojure.pprint (.name (get (ns-aliases *ns*) 'zzz))))
    (sut/upsert-alias 'zzz 'easy-rpc.mylib)
    (is (= 'easy-rpc.mylib (.name (get (ns-aliases *ns*) 'zzz))))))
