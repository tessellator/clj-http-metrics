(ns clj-http.middleware.metrics
  "Functions to build clj-http middlewares that report timing metrics."
  (:require [clojure.string :as str]
            [metrics.core :as m]
            [metrics.timers :refer [time! timer]])
  (:import [java.net URI]))

;; -----------------------------------------------------------------------------
;; Middleware

(defn build-wrap-metrics
  "Creates a clj-http middleware that reports timing metrics.

  NOTE: The middleware produced currently only supports synchronous operations.

  `title-fn` is a function that accepts a request and returns a title. The title
  may be a dot-delimited string (e.g., `a.b.c`) or a sequence of strings (e.g.,
  `[\"a\" \"b\" \"c\"]`.

  `registry` is an optional parameter that is a
  `com.codahale.metrics.MetricRegistry`. If one is not provided, the
  `metrics.core/default-registry` will be used."
  ([title-fn]
   (build-wrap-metrics m/default-registry title-fn))
  ([registry title-fn]
   (fn [client]
     (fn [request]
       (time! (timer registry (title-fn request))
              (client request))))))

;; -----------------------------------------------------------------------------
;; Title functions

(defn title-from-url
  "Generates a metric title in sequential form from the provided URL.

  This function is designed to operate on a clj-http request and can be used as
  a `title-fn` for [[build-wrap-metrics]].

  The produced title will be in the following form contain the reversed hostname
  followed by the path, in sequential form. For example, the URL
  `http://subdomain.example.com/some/path` would be transformed into the
  following title:

  `[\"com\" \"example\" \"subdomain\" \"some\" \"path\"]`"
  [{:keys [url]}]
  (let [uri (URI. url)
        host-part (str/split (.getHost uri) #"\.")
        path-part (str/split (.getPath uri) #"\/")]
    (->> (concat (reverse host-part) path-part)
         (filter not-empty))))

(defn- ensure-sequential [x]
  (if (sequential? x) x [x]))

(defn prefixed-title-fn
  "Creates a new title-fn that applies the prefix to the output of title-fn."
  [title-fn prefix]
  (let [p (ensure-sequential prefix)]
    (fn [request]
      (let [title (title-fn request)]
        (concat p (ensure-sequential title))))))
