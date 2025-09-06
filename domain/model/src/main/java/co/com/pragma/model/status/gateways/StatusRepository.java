package co.com.pragma.model.status.gateways;

import co.com.pragma.model.status.Status;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Boolean> existsById(Long id);
}
