package co.com.pragma.usecase.registerloanapplication.inport;

import co.com.pragma.model.application.Application;
import reactor.core.publisher.Mono;

public interface RegisterLoanApplicationUseCaseInPort {
    Mono<Application> saveApplication(Application application);
}
