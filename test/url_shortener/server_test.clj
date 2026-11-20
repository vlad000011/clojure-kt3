(ns url-shortener.server-test
  (:require [clojure.test :refer :all]
            [url-shortener.db :as db]
            [url-shortener.server :as server]))

(defn with-temp-db [f]
  (let [file (str (java.io.File/createTempFile "url-shortener-http-test-" ".db"))]
    (System/setProperty "urlshortener.db" file)
    (try (db/init!) (f)
         (finally (System/clearProperty "urlshortener.db")
                  (.delete (java.io.File. file))))))

(use-fixtures :each with-temp-db)

(deftest rest-api-flow
  (let [created (server/app {:request-method :post
                             :uri "/api/urls"
                             :body "https://example.com/path"})]
    (is (= 201 (:status created)))
    (let [code (:body created)
          shown (server/app {:request-method :get
                             :uri (str "/api/urls/" code)})]
      (is (= 200 (:status shown)))
      (is (= "https://example.com/path" (:body shown)))
      (is (= 200 (:status (server/app {:request-method :put
                                       :uri (str "/api/urls/" code)
                                       :body "https://example.org/new"}))))
      (is (= "https://example.org/new"
             (:body (server/app {:request-method :get
                                 :uri (str "/api/urls/" code)}))))
      (is (= 200 (:status (server/app {:request-method :delete
                                       :uri (str "/api/urls/" code)}))))
      (is (= 404 (:status (server/app {:request-method :get
                                       :uri (str "/api/urls/" code)})))))))

(deftest rejects-invalid-url
  (is (= 400 (:status (server/app {:request-method :post
                                   :uri "/api/urls"
                                   :body "not-a-url"})))))
