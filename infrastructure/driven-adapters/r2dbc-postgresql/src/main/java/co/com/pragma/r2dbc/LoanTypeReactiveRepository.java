package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface LoanTypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, Long>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {

    @Query("SELECT * FROM loan_type WHERE :amount >= min_amount AND :amount <= max_amount LIMIT 1")
    Mono<LoanTypeEntity> findByAmountInRange(BigDecimal amount);
}
