(ns url-shortener.db
  (:require [clojure.java.io :as io])
  (:import (java.sql DriverManager)
           (java.util UUID)))

(defn database-path []
  (or (System/getProperty "urlshortener.db")
      (System/getenv "URL_SHORTENER_DB")
      "url_shortener.db"))

(defn- connection []
  (Class/forName "org.sqlite.JDBC")
  (let [file (io/file (database-path))
        parent (.getParentFile file)]
    (when parent (.mkdirs parent))
    (DriverManager/getConnection (str "jdbc:sqlite:" (.getAbsolutePath file)))))

(defn init! []
  (with-open [conn (connection)
              stmt (.createStatement conn)]
    (.execute stmt
              "CREATE TABLE IF NOT EXISTS urls (
                 short_code TEXT PRIMARY KEY,
                 normal_url TEXT NOT NULL,
                 created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
               )"))
  true)

(defn find-url [short-code]
  (with-open [conn (connection)
              stmt (.prepareStatement conn
                    "SELECT normal_url FROM urls WHERE short_code = ?")]
    (.setString stmt 1 short-code)
    (with-open [rs (.executeQuery stmt)]
      (when (.next rs)
        (.getString rs "normal_url")))))

(defn- code-exists? [conn code]
  (with-open [stmt (.prepareStatement conn
                     "SELECT 1 FROM urls WHERE short_code = ?")]
    (.setString stmt 1 code)
    (with-open [rs (.executeQuery stmt)]
      (.next rs))))

(defn create-url! [normal-url]
  (with-open [conn (connection)]
    (loop []
      (let [code (subs (str (UUID/randomUUID)) 0 8)]
        (if (code-exists? conn code)
          (recur)
          (with-open [stmt (.prepareStatement conn
                           "INSERT INTO urls(short_code, normal_url) VALUES (?, ?)")]
            (.setString stmt 1 code)
            (.setString stmt 2 normal-url)
            (.executeUpdate stmt)
            code))))))

(defn update-url! [short-code normal-url]
  (with-open [conn (connection)
              stmt (.prepareStatement conn
                    "UPDATE urls SET normal_url = ? WHERE short_code = ?")]
    (.setString stmt 1 normal-url)
    (.setString stmt 2 short-code)
    (pos? (.executeUpdate stmt))))

(defn delete-url! [short-code]
  (with-open [conn (connection)
              stmt (.prepareStatement conn
                    "DELETE FROM urls WHERE short_code = ?")]
    (.setString stmt 1 short-code)
    (pos? (.executeUpdate stmt))))
