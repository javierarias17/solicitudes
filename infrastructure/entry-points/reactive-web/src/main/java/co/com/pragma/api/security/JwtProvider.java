package co.com.pragma.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expiration;

    public Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validate(String token){
        try {
            Jwts.parser()
                    .verifyWith(getKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("token expired");
        } catch (UnsupportedJwtException e) {
            LOGGER.error("token unsupported");
        } catch (MalformedJwtException e) {
            LOGGER.error("token malformed");
        } catch (IllegalArgumentException e) {
            LOGGER.error("illegal args");
        }catch (Exception e){
            LOGGER.error("Invalid token");
        }
        return false;
    }

    private SecretKey getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    public Mono<Map<String, String>> getTokenInfo() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getCredentials().toString())
                .map(this::getPayload)
                .flatMap(claims -> {
                    String identity = claims.get("identityDocument", String.class);
                    String email = claims.get("email", String.class);

                    if (identity == null || email == null) {
                        return Mono.error(new IllegalStateException("Required claims not found in token"));
                    }

                    Map<String, String> info = new HashMap<>();
                    info.put("identityDocument", identity);
                    info.put("email", email);

                    return Mono.just(info);
                });
    }

}

