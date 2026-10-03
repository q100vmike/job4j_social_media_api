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
	