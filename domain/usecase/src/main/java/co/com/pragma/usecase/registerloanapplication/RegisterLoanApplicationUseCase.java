package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.exceptions.ValidationException;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase implements RegisterLoanApplicationUseCaseInPort {

    private static final Long PENDING_REVIEW_STATUS_ID=1L;
    private final ApplicationRepository applicationRepository;
    private final StatusRepository statusRepository;
    private final LoanTypeRepository loanTypeRepository;

    @Override
    public Mono<Application> execute(Application application) {
        Mono<Boolean> existsLoanType = loanTypeRepository.existsById(application.getLoanTypeId());
        Mono<Boolean> existsStatus = statusRepository.existsById(PENDING_REVIEW_STATUS_ID);
        return Mono.zip(existsLoanType, existsStatus)
                .flatMap((Tuple2<Boolean, Boolean> tuple) -> {
                    Map<String, String> errors = new HashMap<>();
                    if (!tuple.getT1()) errors.put("loanTypeId", "Loan type does not exist");
                    if (!tuple.getT2()) errors.put("statusId", "Status does not exist");

                    if (!errors.isEmpty()) {
                        return Mono.error(new ValidationException(errors));
                    }
                    application.setId(null);
                    application.setStatusId(PENDING_REVIEW_STATUS_ID);
                    return applicationRepository.saveApplication(application);
                });
    }
}
