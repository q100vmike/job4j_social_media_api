API:
GET /api/friendships?user_id=1;
Content-Type: application/json

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит идентификаторы друзей:
	[
	{ "user_id": 1 },
	{ "user_id": 2 },
	{ "user_id": 3 }
	]