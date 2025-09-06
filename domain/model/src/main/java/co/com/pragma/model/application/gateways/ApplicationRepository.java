package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.ApplicationSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {
    Mono<Application> saveApplication(Application application);
    Flux<ApplicationSummary> findPendingApplicationsPaged(long limit, long offset);
    Mono<Application> findById(Long id);
}
