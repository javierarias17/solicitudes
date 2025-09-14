package co.com.pragma.api.security;

import co.com.pragma.api.exceptions.InvalidTokenException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .flatMap(auth -> {
                    String token = auth.getCredentials().toString();

                    if (!jwtProvider.validate(token)) {
                        return Mono.error(new InvalidTokenException("Invalid token"));
                    }

                    return Mono.just(jwtProvider.getPayload(token));
                })
                .map(payload -> new UsernamePasswordAuthenticationToken(
                        payload.getSubject(),
                        authentication.getCredentials(),
                        Stream.of(payload.get("roles"))
                                .map(role -> (List<Map<String, String>>) role)
                                .flatMap(role -> role.stream()
                                        .map(r -> r.get("authority"))
                                        .map(SimpleGrantedAuthority::new))
                                .toList())
                );
    }
}