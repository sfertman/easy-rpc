# Easy RPC

Clojure RPC with one line of code.

[![Clojars Project](https://img.shields.io/clojars/v/easy-rpc.svg)](https://clojars.org/easy-rpc)

## What is this?
This is a library to make any clojure namespace remote-callable.

## Why?

So, I wrote a clojure library, `mylib`, that I use all over my codebase and now I want to turn it into a micro-service but I don't want to write too much api and client code and even more test code and I don't want to require overkill rpc frameworks and lock myself into rigid schemas while still iterating over api and logic. And, what if one day I'll want to break out another lib into a microservice? More code to write, test and debug...

## Usage

### Server

All you need to create and start an rpc server is a simple config:

```clojure
(require '[easy-rpc.server :as rpc-server])

(def config {
  :ns "mylib"
  :transport :http
  :host "localhost"
  :port 3000})

(rpc-server/start! config)
```
where:
- `:ns` is the namespace you want to call remotely
- `:transport` is the transport layer
  - `:http` for http communication between clojure services
  - `:web` for starting a [web server](#/web-server) to ping your functions from the browser
- `:host` is where your rpc server is going to be deployed
- `:port` is the communication port

### Client

On the client side things are just as simple. Use the same config from above to and pass it to `defclient` macro along with the name you want to use to invoke your rpc client:
```clojure
(require '[easy-rpc.client :refer [defclient]]))

(def http-config {
  :ns "mylib"
  :transport :http
  :host "localhost"
  :port 3000})
;; ^ looks familiar?

(defclient mylib http-config) ;; and that's it!
```
`defclient` will magically transform every local function call like `mylib/call-me` to a remote rpc call. No extra code required. In case you _do_ want to keep all your local calls the way they are and only change a few to be remote, simply pass a different name to `defclient`:
```clojure
(defclient mylib-rpc http-config)
```
and call your functions as if `mylib-rpc` was a namespace alias:
```clojure
(mylib/call-me ...) ;; local calls are unchanged
(mylib-rpc/call-me ...) ;; remote calls are just as easy!
```

### Error handling
If your function throws an exception, easy-rpc will catch that on the server side and re-throw it on the client side so your error handling remains the same. Thanks to nippy, we can serialize exceptions as well. The code below works the same whether `mylib/call-me` is local or remote.
```clojure
(try
  (mylib/call-me 1 2 3)
  (catch Exception e
    (handle-exception e)))
```

### Web server

A web server allows remote calling functions from any http client. This is a convenient way of trying things out without writing _any_ client code.

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

### Limitations
- only http transport for now
- (de)serialization is done with [nippy](https://github.com/ptaoussanis/nippy) and the entire message is serialized before it's sent; so, do _not_ use for passing large clojure objects
