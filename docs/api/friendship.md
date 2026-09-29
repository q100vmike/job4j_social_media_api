API:
GET /api/friendships?id=1;
Content-Type: application/json

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит идентификаторы друзей:
	[
	{ "id": 1 },
	{ "id": 2 },
	{ "id": 3 }
	]