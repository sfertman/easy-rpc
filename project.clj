(defproject easy-rpc "0.3.0"
  :description "Turn any Clojure api into a microservice accessible over the network"
  :license {
    :name "MIT"
    :url "https://opensource.org/licenses/mit-license.php"}
  :dependencies [
    [http-kit "2.3.0"]
    [org.clojure/clojure "1.9.0"]
    [com.taoensso/nippy "2.14.0"]
    [metosin/reitit-ring "0.3.1"]]
  :plugins [
    [lein-marginalia "0.9.1"]])
