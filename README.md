# FitPoint — Backend

REST API сервер для мобильного приложения FitPoint. Обеспечивает авторизацию, управление занятиями и пользователями с разграничением прав по ролям.

## Стек

| Категория | Технология |
|---|---|
| Язык | Kotlin (JVM 21) |
| Фреймворк | Ktor + Netty |
| БД | PostgreSQL 15 |
| ORM | Exposed 0.46 |
| DI | Koin 3.5 |
| Аутентификация | JWT (java-jwt 4.4) |
| Хеширование паролей | jBCrypt |
| Сериализация | kotlinx.serialization |
| Развёртывание | Docker + Docker Compose |

---

## Быстрый старт

### Требования

- Docker Desktop (или Docker Engine + Compose plugin)

### Запуск

```bash
docker-compose up --build -d
```

Команда:
1. Собирает JAR внутри Docker (multi-stage build, JDK 21)
2. Поднимает PostgreSQL 15
3. Ждёт готовности БД (`healthcheck`)
4. Запускает сервер на порту `8080`

### Остановка

```bash
docker-compose down        # остановить контейнеры
docker-compose down -v     # остановить + удалить данные БД
```

### Просмотр логов

```bash
docker-compose logs -f app
docker-compose logs -f db
```

---

## Конфигурация

Переменные окружения задаются в `docker-compose.yml`:

| Переменная | Значение по умолчанию | Описание |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://db:5432/fitness` | JDBC-строка подключения |
| `DB_USER` | `postgres` | Пользователь PostgreSQL |
| `DB_PASSWORD` | `1234` | Пароль PostgreSQL |
| `JWT_SECRET` | `change-me-in-production` | Секрет для подписи токенов |

> ⚠️ Обязательно смените `JWT_SECRET` и пароль БД перед деплоем в продакшн.

---

## Архитектура

```
src/main/kotlin/
  Application.kt                  # точка входа, подключение плагинов
  data/
    database/   DatabaseFactory   # инициализация Exposed, создание схемы
    repository/ *RepositoryImpl   # реализации репозиториев через Exposed
    seed/       UserTypeSeeder    # начальные данные (типы пользователей)
    tables/     *Table            # Exposed-таблицы (DDL)
  domain/
    model/                        # доменные модели (User, Booking, ...)
    repository/ *Repository       # интерфейсы репозиториев
    usecase/    *UseCase          # бизнес-логика
  presentation/
    dto/                          # Request/Response DTO
    plugins/
      di/       AppModule         # Koin-модуль
    routes/                       # роуты (Auth, Booking, Admin)
    util/                         # вспомогательные функции (role extractor)
```

### Схема БД

```
user_types        id, name (ADMIN | COACH | CLIENT)
users             id, fio, phone, email, password_hash, user_type_id
coach_types       id, name
coaches           id, user_id → users, coach_type_id → coach_types
clients           id, user_id → users, card_end_date
workouts          id, coach_type_id → coach_types, name, description, duration
bookings          id, coach_id → coaches, workout_id → workouts,
                  name, slots, time, extra
booking_clients   booking_id → bookings, client_id → clients
```

---

## API

**Base URL:** `http://localhost:8080`

Для защищённых маршрутов передавайте токен в заголовке:
```
Authorization: Bearer <token>
```

### Аутентификация

| Метод | Путь | Тело запроса | Ответ | Описание |
|---|---|---|---|---|
| POST | `/auth/login` | `{email, password}` | `{token}` | Вход |
| POST | `/auth/register` | `{fio, phone, email, password, userTypeId}` | `{token}` | Регистрация |
| GET | `/users/me/profile` | — | `{id, fio, phone, email, cardEndDate, userTypeId}` | Профиль текущего пользователя |
| PATCH | `/users/me/profile` | `{fio, phone, email}` | 200 OK | Обновить профиль |

### Занятия (клиент)

| Метод | Путь | Авторизация | Описание |
|---|---|---|---|
| GET | `/bookings` | Bearer | Все занятия |
| GET | `/bookings/my` | Bearer (CLIENT) | Занятия, на которые записан клиент |
| GET | `/bookings/search?q=` | Bearer | Поиск по названию |
| POST | `/bookings/{id}/join` | Bearer (CLIENT) | Записаться на занятие |

### Занятия (тренер)

| Метод | Путь | Авторизация | Описание |
|---|---|---|---|
| GET | `/bookings/coach` | Bearer (COACH) | Собственные занятия тренера |
| POST | `/bookings` | Bearer (COACH) | Создать занятие |
| PATCH | `/bookings/{id}` | Bearer (COACH) | Изменить своё занятие |
| DELETE | `/bookings/{id}` | Bearer (COACH) | Удалить своё занятие |
| GET | `/bookings/{id}/participants` | Bearer (COACH/ADMIN) | Список участников |

**POST / PATCH `/bookings` — тело запроса:**
```json
{
  "name": "Йога для начинающих",
  "slots": 10,
  "extra": "https://rutube.ru/video/abc123/",
  "time": "2026-06-01T10:00:00"
}
```
> `coachId` автоматически берётся из JWT — передавать не нужно.

### Администратор

Все маршруты требуют токен с ролью `ADMIN`.

| Метод | Путь | Описание |
|---|---|---|
| GET | `/admin/users` | Все пользователи с ролями |
| POST | `/admin/users` | Создать пользователя (CLIENT или COACH) |
| PATCH | `/admin/users/{id}` | Изменить данные пользователя |
| DELETE | `/admin/users/{id}` | Удалить пользователя |
| GET | `/admin/coach-types` | Все типы тренеров |
| POST | `/admin/coach-types` | Создать тип тренера |
| PATCH | `/admin/coach-types/{id}` | Изменить тип тренера |
| DELETE | `/admin/coach-types/{id}` | Удалить тип тренера |
| GET | `/admin/workouts` | Все типы занятий |
| POST | `/admin/workouts` | Создать тип занятия |
| PATCH | `/admin/workouts/{id}` | Изменить тип занятия |
| DELETE | `/admin/workouts/{id}` | Удалить тип занятия |

### Коды ответов

| Код | Значение |
|---|---|
| 200 | Успех |
| 201 | Создан |
| 400 | Неверный запрос |
| 401 | Не авторизован |
| 403 | Нет прав (Forbidden) |
| 404 | Не найдено |
| 409 | Конфликт (дубликат) |
| 500 | Ошибка сервера |

Ошибки возвращаются в формате:
```json
{ "error": "Описание ошибки" }
```

---

## Роли

| `userTypeId` | Роль | Возможности |
|---|---|---|
| 1 | ADMIN | Полный доступ ко всем маршрутам |
| 2 | COACH | Управление своими занятиями, просмотр участников |
| 3 | CLIENT | Запись на занятия, просмотр своих записей |

Роль извлекается из JWT-токена на каждый запрос — без дополнительного обращения к БД.

---

## Локальная разработка (без Docker)

### Требования

- JDK 21
- PostgreSQL запущен локально, БД `fitness` создана

### Настройка `application.yaml`

```yaml
database:
  url: jdbc:postgresql://localhost:5432/fitness
  user: postgres
  password: 1234
  driver: org.postgresql.Driver

jwt:
  secret: dev-secret
  issuer: fitpoint
  audience: fitpoint-users
```

### Запуск

```bash
./gradlew run
```

Сервер стартует на `http://0.0.0.0:8080`.

### Сборка JAR

```bash
./gradlew build -x test
java -jar build/libs/Backend-1.0.0-SNAPSHOT.jar
```

---

## Инициализация БД

При первом запуске `DatabaseFactory.init()` автоматически:
1. Создаёт все таблицы через `SchemaUtils.create()`
2. Запускает `UserTypeSeeder` — заполняет таблицу `user_types` записями `ADMIN`, `COACH`, `CLIENT`

Повторный запуск безопасен — таблицы и начальные данные не дублируются.

---

## Dockerfile

Используется multi-stage сборка:

```
Этап 1 — builder (eclipse-temurin:21-jdk)
  └─ кешируем зависимости Gradle отдельным слоем
  └─ ./gradlew build -x test → build/libs/*.jar

Этап 2 — runtime (eclipse-temurin:21-jre)
  └─ копируем только JAR
  └─ java -jar app.jar
```

Зависимости Gradle кешируются отдельным Docker-слоем — повторная сборка при изменении только исходников занимает ~30 секунд вместо нескольких минут.
