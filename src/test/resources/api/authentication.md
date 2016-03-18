# GraffiTab authentication

## Login endpoint - returns cookie for created session

* Provide username and password. Session cookie is returned in the Set-Cookie header

```
curl -i -H "Content-Type: application/json" -X POST -d '{"username":"user","password":"password"}' http://localhost:8091/api/login
```

* Protected endpoint: provide cookie returned by login endpoint to authenticate. Without the cookie should give 401 Unauthorized

```
curl -i -H "Content-Type: application/json" --cookie "GRAFFITABSESSIONID=DB93559F55D32249D11C0E7EABC1083F" http://localhost:8091/api/users/me
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
curl -i --cookie "GRAFFITABSESSIONID=E9DFBB37EC490A08BAA19295360F1EFA" http://localhost:8091/api/logout
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


