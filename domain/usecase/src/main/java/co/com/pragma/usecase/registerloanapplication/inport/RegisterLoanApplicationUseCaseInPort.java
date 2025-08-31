package co.com.pragma.usecase.registerloanapplication.inport;

import co.com.pragma.model.application.Application;
import reactor.core.publisher.Mono;

public interface RegisterLoanApplicationUseCaseInPort {
    Mono<Application> execute(Application application, String identityDocumentToken,  String emailToken );
}
