package project.subscription.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    @Value("${jwt.access-expiration}")
    private long accessExpirationMs;
    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] key = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(key);
    }

    public String createToken(String userId, String type) {
        return Jwts.builder()
                .claim("sub", type)
                .claim("roles", List.of("ROLE_USER"))
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (type.equals("access") ? accessExpirationMs : refreshExpirationMs)))
                .signWith(secretKey)
                .compact();
    }

    public void validate(String token) {
        parseClaim(token);
    }

    public String getUserId(String token) {
        return parseClaim(token).get("userId").toString();
    }

    public String getTokenType(String token) {
        return parseClaim(token).getSubject();
    }

    private Claims parseClaim(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
