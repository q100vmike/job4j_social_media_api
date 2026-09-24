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

API:
POST /api/offer-friendships/accept
Content-Type: application/json

Тело HTTP-запроса:
{
"Id": 1,
"status": "ACCEPTED"
}

В результате заявка переводится в статус ACCEPTED:
HTTP/1.1 201 Updated
Content-Type: application/json

API:
POST /api/offer-friendships/reject
Content-Type: application/json

Тело HTTP-запроса:
{
"Id": 1,
"status": "REJECTED"
}

В результате заявка переводится в статус REJECTED:
HTTP/1.1 201 Updated
Content-Type: application/json

API:
GET /api/offer-friendships/incoming
Content-Type: application/json

Тело HTTP-запроса:
{
"Id": 1
}

GET /api/offer-friendships/outgoing;
