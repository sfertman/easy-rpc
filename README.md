# Easy RPC

Turn any Clojure api into a microservice<sup>*</sup>.

## Motivation

So, I wrote a clojure library, `mylib`, that I use all over my codebase and now I want to make it a micro-service but I don't want to write too much api and client code and even more test code and I don't want to require overkill rpc frameworks and learn how to use any of them. And, what if one day I'll want to break out another lib into a microservice? More code to write, test and debug...

## Usage<sup>**</sup>
### Server (http)
I wish making a server would be as easy as this:
```clojure
(require '[easy-rpc.server :refer [create-server start]])

(def server (atom nil))

(def config {
  :ns "mylib"
  :transport :http
  :host "localhost"
  :port 3000})

(reset! server (start (create-server config)))
```
### Client
Ok, but now I needed a client libray. It would be nice if I didn't write anything and have it all magically work based on my server config. Maybe something like this:
```clojure
(require '[easy-rpc.client :as rpc-client]))

(def http-config {
  :ns "mylib"
  :transport :http
  :host "localhost"
  :port 3000})
;; ^ looks familiar?

(def http-client (rpc-client/create-client rpc-config))
```
Hmmm, using a clojure library is easy; I just call my functions like `(mylib/myfunc x y z)`. If only I could somehow not change (almost) anything about my code and still use a remote library:
```clojure
;; def an rpc call wrapper
(defn mylib-rpc
  [& args]
  (apply (:send-message http-client) args))

;; find and replace every `mylib/<func-name>` with `mylib-rpc '<func-name>`
;; Examples:

(mylib-rpc 'myfunc x y z)
;(mylib/myfunc x y z)
```
### Web server
Well, now that I have my service running and client apps using it, it would be nice if I could simply check what my functions are returning without hacking into my clojure client or server (each is one line of code, I know, but still):

Make web server:
```clojure
(def web-config {
  :ns "mylib"
  :host "localhost"
  :transport :web ; <- note :web instead of :http
  :port 8080})

(def web-server (atom nil))

(run-server! web-server (start (create-server web-config)))
```
Send POST request to call functions:
```http
POST http://localhost:8080

["myfunc" [42 43 "abc"]]
```
Or
```shell
curl -d '["myfunc" [42 43 "abc"]]' http://localhost:8080
```

<sup>*</sup> Everything here is very much a work in progress and not more than a personal experimental project at this point. Suggestions are wellcome! Open an issue to start a discussion.

<sup>**</sup> See `example.core` for more fun use cases.