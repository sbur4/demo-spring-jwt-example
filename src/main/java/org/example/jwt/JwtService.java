package org.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.spring.gradle.dependencymanagement.org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

//    @Value("${security.jwt.expiration-time}")
//    private long jwtExpiration;

    private long jwtExpiration = TimeUnit.HOURS.toMillis(24); // Token expiration time
    private long jwtNotBefore = TimeUnit.MINUTES.toMillis(5); // Delay token validity for 5 minutes

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        Date notBeforeDate = new Date(now.getTime() + jwtNotBefore);

        // Custom claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", "tester@epam.com");
        claims.put("authorities", new String[]{"USER"});

        return Jwts.builder()
                .subject(username)
                .issuedAt(expiryDate)
                .expiration(notBeforeDate)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .id(RandomStringUtils.randomAlphanumeric(12))
                .claims(claims)
                .setHeaderParam("typ", "JWT") // Set header parameter
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("authorities", List.class); // Ensure your claims include the roles correctly
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String name) {
        final String username = extractUsername(token);
        return (username.equals(name) && !isTokenExpired(token));
    }
}