package co.com.pragma.api.security;

import co.com.pragma.api.exceptions.InvalidTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {

    private static final String INTERNAL_HEADER = "X-Internal-Request";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (path.startsWith("/webjars")
                || path.startsWith("/v3/")
                || path.startsWith("/swagger-ui/**")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/actuator/health")) {
            exchange.getAttributes().put("internal", true);
            return chain.filter(exchange);
        }

        // TODO Pendiente implementar el cifrado para invocaciones internas
        String internalHeader = request.getHeaders().getFirst(INTERNAL_HEADER);
        if ("true".equalsIgnoreCase(internalHeader)) {
            exchange.getAttributes().put("internal", true);
            return chain.filter(exchange);
        }

        if(path.contains("auth"))
            return chain.filter(exchange);
        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(auth == null)
            return Mono.error(new InvalidTokenException("Token not found"));
        if(!auth.startsWith("Bearer "))
            return Mono.error(new InvalidTokenException("Invalid auth"));

        String token = auth.replace("Bearer ", "");
        exchange.getAttributes().put("token", token);
        return chain.filter(exchange);
    }
}