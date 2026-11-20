@echo off
echo Загрузка зависимостей...
lein deps
if errorlevel 1 exit /b 1
echo Запуск тестов...
lein test
if errorlevel 1 exit /b 1
echo Сборка standalone JAR...
lein uberjar
if errorlevel 1 exit /b 1
echo.
echo Сборка завершена: target\url-shortener-0.1.0-standalone.jar
