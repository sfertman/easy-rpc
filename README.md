# Easy RPC

Turn any Clojure api into a microservice<sup>*</sup>.

[![Clojars Project](https://img.shields.io/clojars/v/easy-rpc.svg)](https://clojars.org/easy-rpc)

## Motivation

So, I wrote a clojure library, `mylib`, that I use all over my codebase and now I want to make it a micro-service but I don't want to write too much api and client code and even more test code and I don't want to require overkill rpc frameworks and learn how to use any of them. And, what if one day I'll want to break out another lib into a microservice? More code to write, test and debug...

## Usage<sup>**</sup>
### Server (http)
I wish making a server would be as easy as this:
```clojure
(require '[easy-rpc.server :as rpc-server])

(def config {
  :ns "mylib"
  :transport :http
  :host "localhost"
  :port 3000})

(rpc-server/start! config)
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

(def mylib-rpc (rpc-client/client http-config))

;; Now find and replace every `mylib/<func-name>` with `mylib-rpc '<func-name>`
;; Examples:

(mylib-rpc 'myfunc x y z) ;; <- new
;(mylib/myfunc x y z) ;; <- old
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

(rpc-server/start! web-config)
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