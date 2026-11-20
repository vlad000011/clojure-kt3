(ns url-shortener.db-test
  (:require [clojure.test :refer :all]
            [url-shortener.db :as db]))

(defn with-temp-db [f]
  (let [file (str (java.io.File/createTempFile "url-shortener-test-" ".db"))]
    (System/setProperty "urlshortener.db" file)
    (try
      (db/init!)
      (f)
      (finally
        (System/clearProperty "urlshortener.db")
        (.delete (java.io.File. file))))))

(use-fixtures :each with-temp-db)

(deftest create-find-update-delete
  (let [code (db/create-url! "https://example.com/first")]
    (is (string? code))
    (is (= "https://example.com/first" (db/find-url code)))
    (is (true? (db/update-url! code "https://example.com/second")))
    (is (= "https://example.com/second" (db/find-url code)))
    (is (true? (db/delete-url! code)))
    (is (nil? (db/find-url code)))
    (is (false? (db/delete-url! code)))))
