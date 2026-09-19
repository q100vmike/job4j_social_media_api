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
