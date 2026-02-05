package project.subscription.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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

    public String createToken(String username, String type) {
        return Jwts.builder()
                .claim("sub", username)
                .claim("roles", List.of("ROLE_USER"))
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (type.equals("access") ? accessExpirationMs : refreshExpirationMs)))
                .signWith(secretKey)
                .compact();
    }

    public void validate(String token) {
        parseClaim(token);
    }

    public String getUsername(String token) {
        return parseClaim(token).getSubject();
    }

    public String getType(String token) {
        return parseClaim(token).get("type").toString();
    }

    private Claims parseClaim(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
