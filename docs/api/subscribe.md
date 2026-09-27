API:
GET /api/subscribes/followers
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"Id": 1
		}

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит 
	идентификаторы пользователей подписанных на запрошенного:
		[
		{ "Id": 1 },
		{ "Id": 2 },
		{ "Id": 3 }
		]

API:
GET /api/subscribes
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"Id": 1
		}

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый содержит 
	идентификаторы пользователей на которых подписан запрошенный:
		[
		{ "Id": 1 },
		{ "Id": 2 },
		{ "Id": 3 }
		]