#!/usr/bin/env bash
set -e
echo "Загрузка зависимостей..."
lein deps
echo "Запуск тестов..."
lein test
echo "Сборка standalone JAR..."
lein uberjar
echo
echo "Сборка завершена: target/url-shortener-0.1.0-standalone.jar"
