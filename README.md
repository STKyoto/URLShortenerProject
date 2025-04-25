
# URL Shortener API

> Вебсервіс у вигляді REST API, призначений для перетворення довгих URL-адрес на короткі, з можливістю відстеження статистики переходів та керування посиланнями для зареєстрованих користувачів.

---

## Опис

Цей проєкт реалізує REST API для сервісу скорочення URL. Основні можливості включають:

* **Реєстрація та Автентифікація:** Користувачі можуть реєструватися та входити в систему. Автентифікація реалізована за допомогою JWT (JSON Web Tokens). Паролі зберігаються у хешованому вигляді (BCrypt).
* **Створення Коротких Посилань:** Автентифіковані користувачі можуть створювати короткі посилання для будь-яких валідних URL-адрес. Можна вказати термін дії посилання. Система генерує унікальний короткий ідентифікатор (6-8 символів).
* **Перехід за Посиланням:** Будь-який користувач (навіть неавтентифікований) може перейти за коротким посиланням. Система перенаправить його на оригінальний URL та оновить лічильник переходів.
* **Статистика:** Можна переглядати статистику переходів для будь-якого посилання через окремий ендпоїнт. Автентифіковані користувачі також мають доступ до списку своїх посилань .
* **Керування Посиланнями:** Автентифіковані користувачі можуть переглядати та (передбачається ТЗ, але не показано в контролерах) видаляти свої посилання.
* **API Документація:** Проєкт надає документацію API у форматі OpenAPI 3.0 (Swagger).
* **Версійність API:** Передбачено версійність API.

---

## Технології

* **Мова:** Java 17+
* **Фреймворк:** Spring Boot 3.2
* **Доступ до даних:** Spring Data JPA
* **База даних:** PostgreSQL
* **Міграції БД:** Flyway
* **Автентифікація:** Spring Security, JWT (бібліотека `com.auth0:java-jwt`)
* **API Документація:** OpenAPI 3.0 (Springdoc)
* **Тестування:** JUnit 5, Mockito, Testcontainers (для інтеграційних тестів з БД)
* **Збірка:** Gradle
* **Контейнеризація:** Docker, Docker Compose
* **CI/CD:** GitHub Actions
* **Інше:** Lombok

---

## Встановлення та Запуск

### Передумови

Перед початком роботи переконайтеся, що у вас встановлено:

* Java Development Kit (JDK) версії 17 або вище.
* Docker та Docker Compose.
* Git (для клонування репозиторію).
* (Опціонально) Встановлений клієнт PostgreSQL для прямого доступу до БД.

### Кроки встановлення

1. **Клонуйте репозиторій:**
    ```bash
    git clone <URL_вашого_репозиторію>
    cd <назва_директорії_проєкту>
    ```

2. **Налаштування змінних середовища:**
    ```dotenv
    POSTGRES_DB=url_shortener_db
    POSTGRES_USER=your_db_user
    POSTGRES_PASSWORD=your_db_password
    DB_HOST=db
    DB_PORT=5432
    DB_USERNAME=${POSTGRES_USER}
    DB_PASSWORD=${POSTGRES_PASSWORD}
    JWT_SECRET=ваш_дуже_секретний_ключ_для_jwt_мінімум_256_біт
    ```

3. **application.properties:**
    ```properties
    spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${POSTGRES_DB}
    spring.datasource.username=${DB_USERNAME}
    spring.datasource.password=${DB_PASSWORD}
    jwt.secret=${JWT_SECRET}
    ```

4. **Збірка (опціонально):**
    ```bash
    ./gradlew build
    ```

### Docker Compose (рекомендовано)

```bash
docker-compose up --build -d
docker-compose logs -f
docker-compose down
```

### Локальний запуск

```bash
./gradlew bootRun -Dspring.profiles.active=dev
```

---

## Swagger API

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Тести

```bash
./gradlew test
```

Звіти: `build/reports/tests/index.html`

---

## Профілі

```bash
export SPRING_PROFILES_ACTIVE=prod
# або
-Dspring.profiles.active=prod
```

---
