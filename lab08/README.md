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

-
