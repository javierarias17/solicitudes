package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.capacity.calculation.ActiveLoan;
import co.com.pragma.model.capacity.calculation.CapacityIn;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.outport.AuthenticationGateway;
import co.com.pragma.model.outport.QueueGateway;
import co.com.pragma.model.outport.CapacityLambdaGateway;
import co.com.pragma.usecase.exceptions.ValidationException;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase implements RegisterLoanApplicationUseCaseInPort {

    private static final Long APPROVED_STATUS_ID=4L;
    private static final Long PENDING_REVIEW_STATUS_ID=1L;
    private final ApplicationRepository applicationRepository;
    private final AuthenticationGateway authenticationGateway;
    private final LoanTypeRepository loanTypeRepository;
    private final CapacityLambdaGateway lambdaGateway;
    private final QueueGateway queueGateway;

    @Override
    public Mono<Application> execute(Application application, String identityDocumentToken, String emailToken) {
        application.setIdentityDocument(identityDocumentToken);
        application.setEmail(emailToken);

        return loanTypeRepository.findByAmountInRange(application.getAmount())
                .switchIfEmpty(Mono.error(new ValidationException("No loan type exists for the entered amount.")))
                .flatMap(foundLoanType -> {
                    application.setId(null);
                    application.setLoanTypeId(foundLoanType.getId());
                    application.setStatusId(PENDING_REVIEW_STATUS_ID);

                    if (Boolean.TRUE.equals(foundLoanType.getAutomaticValidation())) {

                        Mono<List<ActiveLoan>> activeLoansMono =
                                applicationRepository.findActiveLoan(identityDocumentToken)
                                        .collectList();

                        Mono<BigDecimal> baseSalaryMono =
                                authenticationGateway.getUsersByIdentityDocuments(List.of(identityDocumentToken))
                                        .flatMap(users -> {
                                            if (users.isEmpty()) {
                                                return Mono.error(new RuntimeException("No user found for the given identity document."));
                                            }
                                            return Mono.just(users.get(0).getBaseSalary());
                                        });

                        return Mono.zip(baseSalaryMono, activeLoansMono)
                                .flatMap(tuple -> {
                                    BigDecimal baseSalary = tuple.getT1();
                                    List<ActiveLoan> activeLoan = tuple.getT2();

                                    CapacityIn capacityRequest = new CapacityIn(
                                            baseSalary,
                                            application.getAmount(),
                                            application.getTerm(),
                                            foundLoanType.getInterestRate(),
                                            activeLoan
                                    );

                                    return lambdaGateway.calculateCapacity(capacityRequest)
                                            .flatMap(capacityOut -> {
                                                application.setStatusId(capacityOut.statusId());
                                                return applicationRepository.saveApplication(application)
                                                        .flatMap(savedApp -> {

                                                            if (savedApp.getStatusId().equals(APPROVED_STATUS_ID)) {
                                                                //HU07
                                                                return queueGateway.sendLoanCapacityPaymentPlanQueue(
                                                                                savedApp.getId(),
                                                                                savedApp.getEmail(),
                                                                                capacityOut.paymentPlans()
                                                                        ).onErrorResume(e -> Mono.empty())
                                                                        //HU08-09
                                                                        .then(queueGateway.sendApprovedLoansQueue(savedApp.getAmount()))
                                                                        .onErrorResume(e -> Mono.empty())
                                                                        .thenReturn(savedApp);
                                                            }
                                                            return Mono.just(savedApp);
                                                        });
                                            });
                                });
                    }

                    return applicationRepository.saveApplication(application);
                });
    }

}
