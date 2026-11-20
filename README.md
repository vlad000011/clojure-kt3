# URL Shortener (Clojure)

REST API для сокращения URL-адресов. Проект содержит HTTP-сервер на Ring, консольный клиент и SQLite-хранилище.

## Возможности

- Создание короткого кода для HTTP/HTTPS URL.
- Получение исходного URL по короткому коду.
- Изменение исходного URL.
- Удаление записи.
- Сохранение данных в SQLite между запусками.
- Консольное меню на русском языке.
- Единый standalone JAR для клиента и сервера.

## Требования

- Java JDK 17 или новее.
- Leiningen 2.x для сборки из исходников.
- Интернет при первой сборке: Leiningen скачает зависимости.

Clojure отдельно устанавливать не нужно: Leiningen загружает Clojure и библиотеки проекта автоматически.

Проверить Java:

```bash
java -version
```

## Установка Leiningen

### macOS

Если установлен Homebrew:

```bash
brew install leiningen
lein version
```

Если Homebrew не установлен, установите его с официального сайта https://brew.sh/, затем выполните команду выше.

### Windows

Установите Java JDK 17+ и Leiningen для Windows по официальной инструкции:
https://leiningen.org/

После установки откройте новое окно PowerShell или CMD и проверьте:

```powershell
lein version
java -version
```

## Сборка

Откройте терминал в корневой папке проекта.

### macOS

```bash
chmod +x build.sh run-server.sh run-client.sh
./build.sh
```

### Windows (PowerShell или CMD)

```cmd
build.bat
```

После успешной сборки появится:

`target/url-shortener-0.1.0-standalone.jar`

## Запуск

Сервер и клиент запускаются в двух отдельных окнах терминала.

### macOS — сервер

```bash
./run-server.sh
```

Или напрямую:

```bash
java -jar target/url-shortener-0.1.0-standalone.jar 8080
```

### macOS — клиент

```bash
./run-client.sh
```

Или:

```bash
java -jar target/url-shortener-0.1.0-standalone.jar
```

### Windows — сервер

```cmd
run-server.bat
```

Или:

```cmd
java -jar target\url-shortener-0.1.0-standalone.jar 8080
```

### Windows — клиент

```cmd
run-client.bat
```

Или:

```cmd
java -jar target\url-shortener-0.1.0-standalone.jar
```

По умолчанию сервер слушает `http://localhost:8080`.

Чтобы использовать другой адрес сервера клиентом, задайте переменную окружения `URL_SHORTENER_API`, например:

macOS:

```bash
export URL_SHORTENER_API=http://localhost:8080
```

Windows PowerShell:

```powershell
$env:URL_SHORTENER_API = "http://localhost:8080"
```

## Использование клиента

После запуска клиента появится меню:

```text
=== URL Shortener ===
1. Создать
2. Показать
3. Изменить
4. Удалить
5. Выйти
```

Пример:

1. Выберите `1`.
2. Введите `https://www.example.com/some/long/path`.
3. Клиент выведет короткий код, например `a1b2c3d4`.
4. Выберите `2` и введите этот код — сервер вернёт исходный URL.
5. Для изменения выберите `3` и введите код, пробел и новый URL.
6. Для удаления выберите `4` и введите код.

## REST API

Все тела запросов и ответы — обычный текст UTF-8.

| Метод | Путь | Тело запроса | Успешный ответ |
|---|---|---|---|
| POST | `/api/urls` | Обычный URL | `201 Created`, короткий код |
| GET | `/api/urls/{code}` | — | `200 OK`, обычный URL |
| PUT | `/api/urls/{code}` | Новый обычный URL | `200 OK`, `OK` |
| DELETE | `/api/urls/{code}` | — | `200 OK`, `OK` |

Для отсутствующего кода сервер возвращает `404 Not Found`. Некорректный URL — `400 Bad Request`.

Примеры запросов:

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: text/plain" \
  --data "https://example.com/long/path"

curl http://localhost:8080/api/urls/a1b2c3d4

curl -X PUT http://localhost:8080/api/urls/a1b2c3d4 \
  -H "Content-Type: text/plain" \
  --data "https://example.org/updated"

curl -X DELETE http://localhost:8080/api/urls/a1b2c3d4
```

## База данных

SQLite-файл `url_shortener.db` создаётся автоматически в текущей рабочей директории при первом запуске сервера.

Можно задать другой путь:

macOS:

```bash
export URL_SHORTENER_DB=/полный/путь/к/url_shortener.db
```

Windows PowerShell:

```powershell
$env:URL_SHORTENER_DB = "C:\путь\к\url_shortener.db"
```

Не удаляйте файл базы данных, если хотите сохранить созданные ссылки.

## Тестирование

```bash
lein test
```

Тесты проверяют CRUD-операции базы данных, REST API и обработку некорректного URL.

## Структура проекта

```text
src/url_shortener/
  core.clj    Точка входа: выбор режима сервера или клиента
  db.clj      Работа с SQLite
  server.clj  Ring HTTP-сервер и REST API
  client.clj  HTTP-клиент
  cli.clj     Интерактивное меню

test/url_shortener/
  db_test.clj      Тесты базы данных
  server_test.clj  Тесты REST API
```

## Примечание

Короткий код — это идентификатор записи. Чтобы получить исходный URL, используйте операцию «Показать» или GET endpoint. Сервер не выполняет HTTP-перенаправление браузера, поскольку это не требуется заданием.
