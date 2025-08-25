package co.com.pragma.model.loantype.gateways;

import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<Boolean> existsById(Long id);
}
