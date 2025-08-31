package co.com.pragma.r2dbc;

import co.com.pragma.model.application.ApplicationSummary;
import co.com.pragma.r2dbc.entity.ApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationEntity> {

    @Query("""
        SELECT a.amount,
               a.term,
               a.email,
               l.name AS loan_type,
               l.interest_rate,
               s.name AS status,
               a.identity_document,
               COALESCE((
                   SELECT SUM(a2.amount / a2.term)
                   FROM application a2
                   WHERE a2.identity_document = a.identity_document
                     AND a2.status_id = (SELECT status_id FROM status WHERE name = 'Approved')
               ), 0) AS total_monthly_debt
        FROM application a
        JOIN loan_type l ON a.loan_type_id = l.loan_type_id
        JOIN status s ON a.status_id = s.status_id
        WHERE s.name IN ('Pending review','Rejected','Manual review')
        ORDER BY a.application_id
        LIMIT :limit OFFSET :offset
        """)
    Flux<ApplicationSummary> findPendingApplicationsPaged(long limit, long offset);
}
