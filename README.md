# Требования
- OpenJDK 14
- PostgreSQL 11
- Consul 
- N2O Security Admin 4
- N2O Audit 2

# Cтек технологий
- Java 14
- JDBC
- JPA 2
- JAX-RS
- Spring Boot 2.1
- Spring Cloud Greenwich
- Liquibase 3.6.2
- N2O Platform 4
- N2O UI Framework 7

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
