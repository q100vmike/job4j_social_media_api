API:
GET /api/subscribes/followers?id=1
Content-Type: application/json

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит 
	идентификаторы пользователей подписанных на запрошенного:
		[
		{ "id": 1 },
		{ "id": 2 },
		{ "id": 3 }
		]

API:
GET /api/subscribes?id=1
Content-Type: application/json

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый содержит 
	идентификаторы пользователей на которых подписан запрошенный:
		[
		{ "id": 1 },
		{ "id": 2 },
		{ "id": 3 }
		]