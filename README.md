# clj-http-metrics

[![Clojars Project](https://img.shields.io/clojars/v/tessellator/clj-http-metrics.svg)](https://clojars.org/tessellator/clj-http-metrics)

A [clj-http](https://github.com/dakrone/clj-http) middleware that reports timing
metrics.

NOTE: The middleware currently only supports synchronous operations.

## Usage

The following example uses the default Codahale metric registry and specifies
that the metric titles should be based upon the request URL and apply a prefix.

The metric title for the URL `http://subdomain.example.com/some/path` is
`my-prefix.com.example.subdomain.some.path` in the following setup. Timing
metrics will appear under that title.

```clojure
(require '[clj-http.client :as client])
(require '[clj-http.middleware.metrics :as m])

(def wrap-metrics
  (m/build-wrap-metrics (m/prefixed-title-fn m/title-from-url "my-prefix"))

(client/with-additional-middleware [#'wrap-metrics]
  (client/get ...))
```

## License

Copyright © 2019 Thomas C. Taylor

Distributed under the Eclipse Public License version 2.0.