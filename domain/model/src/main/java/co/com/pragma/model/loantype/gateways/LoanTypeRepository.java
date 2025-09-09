package co.com.pragma.model.loantype.gateways;

import co.com.pragma.model.loantype.LoanType;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface LoanTypeRepository {
    Mono<LoanType> findByAmountInRange(BigDecimal amount);
}
