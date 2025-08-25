package co.com.pragma.model.status.gateways;

import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Boolean> existsById(Long id);
}
