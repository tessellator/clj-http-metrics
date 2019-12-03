(ns clj-http.middleware.metrics-test
  (:require [clj-http.middleware.metrics :as metrics]
            [clojure.test :refer :all])
  (:import [java.net URI]))

(deftest test-title-from-url
  (let [url "https://subdomain.example.com/v1/some/path?key=value"
        title (metrics/title-from-url {:url url})]
    (is (= ["com" "example" "subdomain" "v1" "some" "path"]
           title))))

(deftest test-prefixed-title-fn
  (let [title-fn (constantly "some.value")
        new-title-fn (metrics/prefixed-title-fn title-fn "my.prefix")]
    (is (= ["my.prefix" "some.value"]
           (new-title-fn {:url "http://example.com"})))))

(deftest test-prefixed-title-from-url
  ;; This is just an integration test for title-from-url and prefixed-title-fn
  (let [url "https://subdomain.example.com/v1/some/path?key=value"
        title-fn (metrics/prefixed-title-fn metrics/title-from-url ["my" "prefix"])
        title (title-fn {:url url})]
    (is (= ["my" "prefix" "com" "example" "subdomain" "v1" "some" "path"]
           title))))
