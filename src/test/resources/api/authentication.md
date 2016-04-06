# GraffiTab authentication

## Login endpoint - returns cookie for created session

* Provide username and password. Session cookie is returned in the Set-Cookie header

```
curl -i -H "Content-Type: application/json" -X POST -d '{"username":"juan","password":"password"}' http://localhost:8091/api/login
```

* Protected endpoint: provide cookie returned by login endpoint to authenticate. Without the cookie should give 401 Unauthorized

```
curl -i -H "Content-Type: application/json" --cookie "JSESSIONID=C7742099BBC578F3841F7E5C5552D5FF" http://localhost:8091/api/users/me
```

```
curl -i -H "Content-Type: application/json" -X PUT -d '{ "currentPassword": "password", "newPassword": "password1" }' --cookie "GRAFFITABSESSIONID=1E27F1CE519DF50938E862FE4D32DA40" http://localhost:8091/api/users/me/changepassword

```

## External provider authentication - return cookie for created session

 * Provide the externalId, accessToken??, externalProvider type and, if found, the underlying user is logged in

```
curl -i -H "Content-Type: application/json" -X POST -d '{ "externalProvider":{ "externalId":"123456789", "accessToken":"hsadoaoi98a7sdiausdi", "externalProviderType":"TWITTER"}}' http://localhost:8091/api/externalproviders/login
```

## Basic Authentication

```
curl -i -u username:password http://localhost:8090/api/users
```

## Logout - always provide the session cookie to log out from

```
curl -i --cookie "JSESSIONID=E9DFBB37EC490A08BAA19295360F1EFA" http://localhost:8091/api/logout
```

## More public endpoints

* Registration
```
curl -i -H "Content-Type: application/json" -X POST -d '{"user":{
        "externalId": "abc",
        "username": "john",
        "firstName": "John",
        "lastName": "Smith",
        "password": "jsmithpass",
        "email": "jsmith@gmail.com"
    }
}' http://localhost:8091/api/users
```


