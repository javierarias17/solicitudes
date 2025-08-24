package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase implements RegisterLoanApplicationUseCaseInPort {

    private final ApplicationRepository applicationRepository;

    @Override
    public Mono<Application> saveApplication(Application application) {
        return applicationRepository.saveApplication(application);
    }
}
