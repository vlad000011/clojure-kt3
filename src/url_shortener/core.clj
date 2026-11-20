(ns url-shortener.core
  (:gen-class)
  (:require [url-shortener.cli :as cli]
            [url-shortener.server :as server]))

(defn- parse-port [value]
  (let [port (try (Integer/parseInt value) (catch Exception _ 8080))]
    (if (<= 1 port 65535) port 8080)))

(defn -main [& args]
  (if (seq args)
    (let [port (parse-port (last args))]
      (println (str "URL Shortener server запускается на порту " port))
      (server/start! port))
    (do
      (println "URL Shortener CLI")
      (cli/run))))
