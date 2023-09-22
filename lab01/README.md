# Simple Encoder

- watched Request Headers in Burp for the first load and then after
- noticed that the cookie user_details was an Base64 encoded filename
- in combination of the content and the cooke user_details -> noticed that the bottom content of the page was loaded from a file, where usually the ip-address of the last visit was saved in
- based on the given information tried to use the file reading of the app to read a file where we don't usually have access
- used CyberChef to create a filename with its path as Base64 encoded + URL encoded
- tried many paths that would make sense for a basic php webapp/nginx
- found the index.php file and based on the content of the file found the flag

```md
../../opt/www/index.php

../../secret/flag.txt

flag: 78b8ae7f-3fc0-4065-8249-9789a86ff6fa
```

# SCHOGGI: API Mass Assignment

- watched Request and Response of the profile page of alice via Burp Proxy
- noticed the GET Request on `/api/account` which the server responded with a JSON

```json
{
  "address": "Boulder Avenue 11",
  "credit_card": "2034 8857 0007 8024",
  "phone": "+41 760 603 132",
  "picture": "user101.png",
  "role": "user",
  "uid": 101,
  "username": "alice"
}
```

- noticed by changing the address via the form on the profile page that a POST Request with a JSON is sent again to `/api/account`

```json
{ "address": "Boulder Avenue 10" }
```

- based on the given information tried to use the Burp Repeater to send a POST Request with a custom JSON where role is set to `admin`

```json
{ "role": "admin" }
```

- after the attack the role of `alice` was changed to `admin`
