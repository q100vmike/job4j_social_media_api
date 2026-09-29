API:
POST /api/offer-friendships/create
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"fromUserId": 1,
		"toUserId": 2
		}

	В результате появляется предложение дружбы в состоянии PENDING:
		HTTP/1.1 201 Created
		Content-Type: application/json

		{
		"id": 10,
		"fromUserId": 1,
		"toUserId": 2,
		"status": "PENDING",
		"createdAt": "2026-09-22T10:15:00Z"
		}

	Создается подписка
		{
		"subscriber_id": 1,
		"target_id": 2,
		"createdAt": "2026-09-22T10:15:00Z"
		}

	Возможные ошибки:
		отсутствует получатель
			HTTP/1.1 404 Not Found
			Content-Type: application/problem+json
			{
			"title": "No Target User",
			"status": 404,
			"detail": "Целевой пользователь не найден",
			"instance": "/api/offer-friendships/create"
			}

		заявка на дружбу существует
			HTTP/1.1 204 No Content

API:
POST /api/offer-friendships/accept
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"Id": 1
		}

	В результате заявка переводится в статус ACCEPTED:
		HTTP/1.1 200 OK
		Content-Type: application/json

	Создается подписка
		{
		"subscriber_id": 1,
		"target_id": 2,
		"createdAt": "2026-09-22T10:15:00Z"
		}

	Возможные ошибки:
		Заявка не найдена
			HTTP/1.1 404 Not Found
			Content-Type: application/problem+json
			{
			"title": "No Friendship Exist",
			"status": 404,
			"detail": "Заявка не найдена",
			"instance": "/api/offer-friendships/accept"
			}

		Заявка не в статусе PENDING
			HTTP/1.1 409 Conflict
			Content-Type: application/problem+json
			{
			"title": "Friendship wrong status",
			"status": 404,
			"detail": "Заявка не в статусе PENDING",
			"instance": "/api/offer-friendships/accept"
			}

API:
POST /api/offer-friendships/reject
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"Id": 1
		}

	В результате заявка переводится в статус REJECTED:
		HTTP/1.1 200 OK
		Content-Type: application/json

	Возможные ошибки:
		Заявка не найдена
			HTTP/1.1 404 Not Found
			Content-Type: application/problem+json
			{
			"title": "No Friendship Exist",
			"status": 404,
			"detail": "Заявка не найдена",
			"instance": "/api/offer-friendships/accept"
			}

		Заявка не в статусе PENDING
			HTTP/1.1 409 Conflict
			Content-Type: application/problem+json
			{
			"title": "Friendship wrong status",
			"status": 404,
			"detail": "Заявка не в статусе PENDING",
			"instance": "/api/offer-friendships/accept"
			}

API:
GET /api/offer-friendships/incoming?id=1
Content-Type: application/json

	Ответ:
		HTTP/1.1 200 OK
		Content-Type: application/json

		Массив JSON-объектов, каждый из которых содержит идентификаторы 
		предложивших дружбу:
		[
		{ "id": 1 },
		{ "id": 2 },
		{ "id": 3 }
		]
API:
GET /api/offer-friendships/outgoing?id=1;
Content-Type: application/json

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит идентификаторы кому предложена дружба:
	[
	{ "id": 1 },
	{ "id": 2 },
	{ "id": 3 }
	]