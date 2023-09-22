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
