(ns url-shortener.cli
  (:require [clojure.string :as str]
            [url-shortener.client :as client]))

(defn- print-result [result success-label]
  (if (<= 200 (:status result) 299)
    (println "Ответ:" (if success-label success-label (:body result)))
    (println "Ответ:" (if (= 0 (:status result)) "Ошибка: сервер недоступен" "Ошибка"))))

(defn- read-value [prompt]
  (print prompt)
  (flush)
  (some-> (read-line) str/trim))

(defn- create-action []
  (when-let [url (read-value "Введите обычный URL: ")]
    (println "Отправка запроса...")
    (print-result (client/create-url url) nil)))

(defn- show-action []
  (when-let [code (read-value "Введите короткий URL или код: ")]
    (println "Отправка запроса...")
    (print-result (client/show-url code) nil)))

(defn- update-action []
  (when-let [line (read-value "Введите короткий код и новый обычный URL через пробел: ")]
    (let [[code & url-parts] (str/split line #"\s+" 2)
          url (first url-parts)]
      (if (and (seq code) (seq url))
        (do (println "Отправка запроса...")
            (print-result (client/update-url code url) "OK"))
        (println "Ответ: Ошибка. Требуется два значения через пробел.")))))

(defn- delete-action []
  (when-let [code (read-value "Введите короткий URL или код: ")]
    (println "Отправка запроса...")
    (print-result (client/delete-url code) "OK")))

(defn run []
  (loop []
    (println)
    (println "=== URL Shortener ===")
    (println "1. Создать")
    (println "2. Показать")
    (println "3. Изменить")
    (println "4. Удалить")
    (println "5. Выйти")
    (case (read-value "Выберите действие: ")
      "1" (do (create-action) (recur))
      "2" (do (show-action) (recur))
      "3" (do (update-action) (recur))
      "4" (do (delete-action) (recur))
      "5" (println "Завершение работы.")
      (do (println "Неизвестная команда. Выберите пункт от 1 до 5.") (recur)))))
