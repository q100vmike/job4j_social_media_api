# Проект представляет собой серверную часть социальной сети.
пользователь отправляет заявку в друзья
  → становится подписчиком
  → второй пользователь принимает заявку
  → создаётся дружба
  → оба пользователя становятся подписчиками друг друга
  → система отправляет уведомления

Команда запуска mvnw.cmd spring-boot:run. После запуска откройте http://localhost:8080/api/ping

БД:
psql -h localhost -p 5432 -U postgres -d social_media

Docker:
docker compose up -d #поднять базу из файла C:\projects\job4j_social_media\docker-compose.yml
docker compose ps #проверить состояние сервисов
docker compose logs postgres #Посмотреть журнал базы
docker compose down #остановка базы с сохранением данных
docker compose down -v #остановка базы данные стираются

API-контракт
docs/api/openapi.yaml

Индексы таблиц
Таблица offer_friendships - индекс по получателю (to_user_id), состоянию и времени создания. 
Для исходящих — такой же индекс с идентификатором инициатора (from_user_id).
Используется для поиска заявок в друзья с определённым статусом, отсортированные от новых к старым.

Таблица friendships	- индекс по first_user_id и индекс по second_user_id
Используется для поиска друзей по user_id 

Таблица subscriptions - индекс по follower_id и индекс по followed_id
Используется для поиска исходящих либо входящих запросов в друзья по user_id 

Почему PostgreSQL а не H2
Репозиторий использует возможности PostgreSQL: тип UUID, TIMESTAMPTZ, ограничения и конструкцию INSERT ... RETURNING. H2 отличается от PostgreSQL синтаксисом и поведением. 
Тест может пройти на H2, а затем завершиться ошибкой в рабочей базе.
Testcontainers запускает временный PostgreSQL в Docker. Разработчик и CI получают одинаковую СУБД и изолированную базу без ручной настройки общего тестового сервера.

@Testcontainers подключает управление контейнерами к JUnit Jupiter;
@Container указывает, что поле описывает контейнер теста;
static запускает один PostgreSQL на весь тестовый класс;
@ServiceConnection передаёт Spring Boot URL, имя пользователя и пароль контейнера;
@SpringBootTest создаёт контекст приложения, запускает Liquibase и позволяет внедрить настоящий репозиторий.

Запуск тестов для Win
mvnw.cmd test
Результат
Maven выполнил все методы JdbcOfferFriendshipRepositoryTest, а Testcontainers остановил временный PostgreSQL