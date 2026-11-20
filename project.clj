(defproject url-shortener "0.1.0"
  :description "A cross-platform URL shortener with a Ring REST API and CLI client"
  :url "https://github.com/"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.11.3"]
                 [ring/ring-core "1.12.2"]
                 [ring/ring-jetty-adapter "1.12.2"]
                 [clj-http "3.13.0"]
                 [org.xerial/sqlite-jdbc "3.46.1.3"]]
  :source-paths ["src"]
  :test-paths ["test"]
  :resource-paths ["resources"]
  :main url-shortener.core
  :aot [url-shortener.core]
  :uberjar-name "url-shortener-0.1.0-standalone.jar"
  :profiles {:uberjar {:aot :all
                       :uberjar-name "url-shortener-0.1.0-standalone.jar"}})
