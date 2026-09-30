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
		"offer_id": 10,
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

		заявка на дружбу существует в статусе PENDING
			HTTP/1.1 204 No Content

		заявка на дружбу существует в статусе ACCEPTED
			HTTP/1.1 204 No Content

		заявка на дружбу существует в статусе REJECTED
			HTTP/1.1 409 Conflict
                Content-Type: application/problem+json
                {
                "title": "Friendship wrong status",
                "status": 409,
                "detail": "Заявка в статусе REJECT",
                "instance": "/api/offer-friendships/create"
                }

API:
POST /api/offer-friendships/accept
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"offer_id": 1
		}

	В результате заявка в статусе PENDING переводится в статус ACCEPTED:
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

		Заявка в статусе ACCEPT
            HTTP/1.1 204 No Content
            Content-Type: application/json
			}

		Заявка не в статусе REJECT
			HTTP/1.1 409 Conflict
			Content-Type: application/problem+json
			{
			"title": "Friendship wrong status",
			"status": 409,
			"detail": "Заявка в статусе REJECT",
			"instance": "/api/offer-friendships/accept"
			}


API:
POST /api/offer-friendships/reject
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"offer_id": 1
		}

	Заявка в статусе PENDING переводится в статус REJECTED:
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
			"instance": "/api/offer-friendships/reject"
			}

        Заявка в статусе REJECT
            HTTP/1.1 204 No Content
            Content-Type: application/json

		Заявка в статусе ACCEPT
			HTTP/1.1 409 Conflict
			Content-Type: application/problem+json
			{
			"title": "Friendship wrong status",
			"status": 409,
			"detail": "Заявка в статусе ACCEPT",
			"instance": "/api/offer-friendships/reject"
			}

API:
GET /api/offer-friendships/incoming?user_id=1
Content-Type: application/json

	Ответ:
		HTTP/1.1 200 OK
		Content-Type: application/json

		Массив JSON-объектов, каждый из которых содержит идентификаторы 
		предложивших дружбу:
		[
		{ "user_id": 1 },
		{ "user_id": 2 },
		{ "user_id": 3 }
		]
API:
GET /api/offer-friendships/outgoing?user_id=1;
Content-Type: application/json

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит идентификаторы кому предложена дружба:
	[
	{ "user_id": 1 },
	{ "user_id": 2 },
	{ "user_id": 3 }
	]