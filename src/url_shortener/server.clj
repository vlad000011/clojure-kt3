(ns url-shortener.server
  (:require [clojure.string :as str]
            [ring.adapter.jetty :as jetty]
            [url-shortener.db :as db]))

(defn valid-url? [value]
  (try
    (let [uri (java.net.URI. (str/trim (or value "")))]
      (and (#{"http" "https"} (some-> (.getScheme uri) str/lower-case))
           (some? (.getHost uri))))
    (catch Exception _ false)))

(defn- response
  ([status body] (response status body "text/plain; charset=utf-8"))
  ([status body content-type]
   {:status status
    :headers {"Content-Type" content-type
              "Access-Control-Allow-Origin" "*"
              "Access-Control-Allow-Methods" "GET, POST, PUT, DELETE, OPTIONS"
              "Access-Control-Allow-Headers" "Content-Type"}
    :body (or body "")}))

(defn- read-body [request]
  (when-let [body (:body request)]
    (if (string? body) body (slurp body))))

(defn- route-code [uri]
  (let [path (str/replace (or uri "") #"/+$" "")]
    (cond
      (str/starts-with? path "/api/urls/") (subs path (count "/api/urls/"))
      (and (not= path "") (not= path "/") (not (str/starts-with? path "/api/"))) (subs path 1)
      :else nil)))

(defn app [request]
  (let [method (:request-method request)
        uri (:uri request)
        code (some-> (route-code uri) (java.net.URLDecoder/decode "UTF-8"))
        body (some-> (read-body request) str/trim)]
    (cond
      (= method :options) (response 204 "")
      (and (= method :post) (or (= uri "/api/urls") (= uri "/")))
      (if (valid-url? body)
        (try
          (response 201 (db/create-url! body))
          (catch Exception e
            (response 500 "Ошибка при сохранении URL")))
        (response 400 "Некорректный URL"))

      (and (= method :get) code)
      (if-let [normal-url (db/find-url code)]
        (response 200 normal-url)
        (response 404 "Ошибка"))

      (and (= method :put) code)
      (if-not (valid-url? body)
        (response 400 "Некорректный URL")
        (if (db/update-url! code body)
          (response 200 "OK")
          (response 404 "Ошибка")))

      (and (= method :delete) code)
      (if (db/delete-url! code)
        (response 200 "OK")
        (response 404 "Ошибка"))

      :else (response 404 "Not found"))))

(defn start! [port]
  (db/init!)
  (jetty/run-jetty app {:port port :join? true}))

(defn stop! [server]
  (when server (.stop server)))
