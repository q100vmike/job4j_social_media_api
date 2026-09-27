API:
GET /api/friendships;
Content-Type: application/json

	Тело HTTP-запроса:
		{
		"Id": 1
		}

	Ответ:
	HTTP/1.1 200 OK
	Content-Type: application/json

	Массив JSON-объектов, каждый из которых содержит идентификаторы друзей:
	[
	{ "Id": 1 },
	{ "Id": 2 },
	{ "Id": 3 }
	]