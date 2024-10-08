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
``
