package co.com.pragma.usecase.getpendingapplications.inport;

import co.com.pragma.model.application.ApplicationSummary;
import reactor.core.publisher.Flux;

public interface GetPendingApplicationsUseCaseInPort {
    Flux<ApplicationSummary> execute(int page, int size);
}
