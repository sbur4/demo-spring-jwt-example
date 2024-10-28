POST http://localhost:8080/api/hello/{text}

The secret key must be an HMAC hash string of 256 bits; otherwise, the token generation will throw an error. 
I used this website to generate one.
https://www.devglan.com/online-tools/hmac-sha256-online?ref=blog.tericcabrel.com