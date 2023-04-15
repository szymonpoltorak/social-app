package razepl.dev.socialappbackend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import razepl.dev.socialappbackend.config.interfaces.JwtServiceInterface;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static razepl.dev.socialappbackend.config.constants.Properties.ENCODING_KEY_PROPERTY;
import static razepl.dev.socialappbackend.config.constants.Properties.EXPIRATION_PROPERTY;

@Service
public class JwtService implements JwtServiceInterface {
    @Value(EXPIRATION_PROPERTY)
    private long expirationTime;

    @Value(ENCODING_KEY_PROPERTY)
    private String encodingKey;

    @Override
    public final String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public final <T> T getClaimFromToken(String token, Function<Claims, T> claimsHandler) {
        Claims claims = getAllClaims(token);

        return claimsHandler.apply(claims);
    }

    @Override
    public final String generateToken(UserDetails userDetails) {
        return generateToken(Collections.emptyMap(), userDetails);
    }

    @Override
    public final String generateToken(Map<String, Object> additionalClaims, UserDetails userDetails) {
        return buildToken(additionalClaims, userDetails,expirationTime);
    }

    @Override
    public final boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(buildSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(Map<String, Object> additionalClaims, UserDetails userDetails, long expiration) {
        long time = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(time))
                .setExpiration(new Date(time + expiration))
                .signWith(buildSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Key buildSignInKey() {
        byte[] decodedKey = Decoders.BASE64.decode(encodingKey);

        return Keys.hmacShaKeyFor(decodedKey);
    }
}