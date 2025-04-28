# Требования
- OpenJDK 17
- PostgreSQL 11
- Consul

# Cтек технологий
- Java 17
- JDBC
- JAX-RS
- Spring Boot 3.2
- N2O Platform 6.1
- N2O UI Framework 7.28

# Структура проекта
- `config-api` - общие интерфейсы и модели
- `config-service` - общие классы имплементации для модуля `config-api`
- `config-web` - общие классы имплементации и конфигурационные файлы N2O
- `config-webapp` - запускаемый модуль UI
- `config-backend` - запускаемый модуль бэкенда

# Варианты сборки
Сборка всех модулей: maven-профиль `build-all-modules` (без сборки статики и без поддержки локализации).
```
mvn clean package -Pbuild-all-modules
```

# Параметры запуск
Задайте настройки запуска в переменные окружения:

| Код                                      | Описание                                              | Значение по умолчанию | 
|------------------------------------------|-------------------------------------------------------|-----------------------|
| `CONFIG_VALUE_VALIDATE_ENABLED`          | Включает валидацию значения настройки по его типу     | false                 |
| `CONFIG_VALUE_VALIDATE_DATE_PATTERN`     | Паттерн проверки значения настройки для типа DATE     | yyyy-MM-dd            |
| `CONFIG_VALUE_VALIDATE_TIME_PATTERN`     | Паттерн проверки значения настройки для типа TIME     | HH:mm:ss              |
| `CONFIG_VALUE_VALIDATE_DATETIME_PATTERN` | Паттерн проверки значения настройки для типа DATETIME | yyy-MM-dd'T'HH:mm:ss  |
