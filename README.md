# Banking Application


## Быстрый старт


### Полный стек в Docker (включая app)

```bash
docker compose --profile full up --build
```

---

## Swagger UI

После запуска: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)


## Конфигурация

`src/main/resources/application.yml` — основные настройки.  
Переменные окружения для Docker:

| Переменная | Описание |
|-----------|----------|
| `SPRING_DATASOURCE_URL` | JDBC URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Логин БД |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД |
| `SPRING_DATA_REDIS_HOST` | Redis host |
| `SPRING_DATA_REDIS_PORT` | Redis port |
| `JWT_SECRET` | Секрет для подписи токенов |
| `JWT_EXPIRATION` | TTL токена в мс (default: 86400000 = 24ч) |
