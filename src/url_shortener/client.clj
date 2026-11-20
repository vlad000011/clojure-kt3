(ns url-shortener.client
  (:require [clj-http.client :as http]))

(defn base-url []
  (or (System/getenv "URL_SHORTENER_API") "http://localhost:8080"))

(defn- request [method path & [body]]
  (try
    (let [options (cond-> {:throw-exceptions false
                           :as :text
                           :socket-timeout 5000
                           :connection-timeout 5000}
                    (some? body) (assoc :body body
                                        :content-type "text/plain; charset=utf-8"))
          result (http/request (merge {:method method
                                       :url (str (base-url) path)}
                                      options))]
      {:status (:status result) :body (:body result)})
    (catch Exception _
      {:status 0 :body "Сервер недоступен"})))

(defn create-url [normal-url]
  (request :post "/api/urls" normal-url))

(defn show-url [short-code]
  (request :get (str "/api/urls/" (java.net.URLEncoder/encode short-code "UTF-8"))))

(defn update-url [short-code normal-url]
  (request :put (str "/api/urls/" (java.net.URLEncoder/encode short-code "UTF-8")) normal-url))

(defn delete-url [short-code]
  (request :delete (str "/api/urls/" (java.net.URLEncoder/encode short-code "UTF-8"))))
