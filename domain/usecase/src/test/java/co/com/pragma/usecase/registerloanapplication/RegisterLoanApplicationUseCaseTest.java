package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.User;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.capacity.calculation.ActiveLoan;
import co.com.pragma.model.capacity.calculation.CapacityOut;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.outport.AuthenticationGateway;
import co.com.pragma.model.outport.AwsQueueGateway;
import co.com.pragma.model.outport.CapacityLambdaGateway;
import co.com.pragma.usecase.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RegisterLoanApplicationUseCaseTest {

    private static final BigDecimal APPLICATION_AMOUNT = BigDecimal.valueOf(1000.00);
    private static final Long APPLICATION_TERM = 12L;
    private static final Long APPLICATION_ID =1L;
    private static final Long APPROVED_STATUS_ID=4L;
    private static final Long REJECTED_STATUS_ID=2L;

    @InjectMocks
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;
    @Mock
    private AuthenticationGateway authenticationGateway;
    @Mock
    private CapacityLambdaGateway lambdaGateway;
    @Mock
    private AwsQueueGateway awsQueueGateway;

    @Test
    void shouldThrowValidationExceptionWhenLoanTypeDoesNotExist() {
        Application app = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .term(APPLICATION_TERM)
                .build();
        when(loanTypeRepository.findByAmountInRange(app.getAmount())).thenReturn(Mono.empty());

        Mono<Application> result = registerLoanApplicationUseCase.execute(app,"1061754493", "javierarias17.dll@gmail.com");

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                ((ValidationException) throwable).getMessage().equals("No loan type exists for the entered amount.")
                ).verify();
    }

    @Test
    void shouldCreateLoanApplicationWhenDataIsValid() {
        Application app = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .term(APPLICATION_TERM)
                .build();

        LoanType loanType = LoanType.builder().interestRate(12.0).id(10L).automaticValidation(Boolean.FALSE).build();

        when(loanTypeRepository.findByAmountInRange(app.getAmount())).thenReturn(Mono.just(loanType));
        when(applicationRepository.saveApplication(any(Application.class)))
                .thenReturn(Mono.just(app.toBuilder().id(APPLICATION_ID).build()));

        Mono<Application> result = registerLoanApplicationUseCase.execute(app, "1061754493", "javierarias17.dll@gmail.com");

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getId().equals(APPLICATION_ID))
                .verifyComplete();
    }

    @Test
    void shouldCreateLoanApplicationWhenDataIsValidWithAutomaticValidationApproved() {
        String identityDocument = "1061754493";
        String email = "javierarias17.dll@gmail.com";
        Application app = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .term(APPLICATION_TERM)
                .build();

        LoanType loanType = LoanType.builder().interestRate(12.0).id(10L).automaticValidation(Boolean.TRUE).build();
        ActiveLoan activeLoan = new ActiveLoan(new BigDecimal("1500000"),6L , 12.5);
        User user=User.builder().baseSalary(new BigDecimal("5000000")).build();
        CapacityOut capacityOut= new CapacityOut(APPROVED_STATUS_ID, "APPROVED", List.of());

        when(loanTypeRepository.findByAmountInRange(app.getAmount())).thenReturn(Mono.just(loanType));
        when(applicationRepository.findActiveLoan(identityDocument)).thenReturn(Flux.just(activeLoan));
        when(authenticationGateway.getUsersByIdentityDocuments(List.of(identityDocument))).thenReturn(Mono.just(List.of(user)));
        when(lambdaGateway.calculateCapacity(any())).thenReturn(Mono.just(capacityOut));
        when(applicationRepository.saveApplication(any(Application.class)))
                .thenReturn(Mono.just(app.toBuilder().id(APPLICATION_ID).email(email).statusId(APPROVED_STATUS_ID).build()));
        when(awsQueueGateway.sendLoanCapacityPaymentPlanQueue(APPLICATION_ID,email, List.of())).thenReturn(Mono.empty());
        when(awsQueueGateway.sendApprovedLoansQueue(APPLICATION_AMOUNT)).thenReturn(Mono.empty());

        Mono<Application> result = registerLoanApplicationUseCase.execute(app, identityDocument, email);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getId().equals(APPLICATION_ID) && saved.getStatusId().equals(APPROVED_STATUS_ID))
                .verifyComplete();
    }

    @Test
    void shouldCreateLoanApplicationWhenDataIsValidWithAutomaticValidationRejected() {
        String identityDocument = "1061754493";
        String email = "javierarias17.dll@gmail.com";
        Application app = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .term(APPLICATION_TERM)
                .build();

        LoanType loanType = LoanType.builder().interestRate(12.0).id(10L).automaticValidation(Boolean.TRUE).build();
        ActiveLoan activeLoan = new ActiveLoan(new BigDecimal("1500000"),6L , 12.5);
        User user=User.builder().baseSalary(new BigDecimal("5000000")).build();
        CapacityOut capacityOut= new CapacityOut(APPLICATION_ID, "REJECTED", List.of());

        when(loanTypeRepository.findByAmountInRange(app.getAmount())).thenReturn(Mono.just(loanType));
        when(applicationRepository.findActiveLoan(identityDocument)).thenReturn(Flux.just(activeLoan));
        when(authenticationGateway.getUsersByIdentityDocuments(List.of(identityDocument))).thenReturn(Mono.just(List.of(user)));
        when(lambdaGateway.calculateCapacity(any())).thenReturn(Mono.just(capacityOut));
        when(applicationRepository.saveApplication(any(Application.class)))
                .thenReturn(Mono.just(app.toBuilder().id(APPLICATION_ID).email(email).statusId(REJECTED_STATUS_ID).build()));

        Mono<Application> result = registerLoanApplicationUseCase.execute(app, identityDocument, email);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getId().equals(APPLICATION_ID) && saved.getStatusId().equals(REJECTED_STATUS_ID))
                .verifyComplete();
    }


    @Test
    void shouldFailWhenUserNotFoundDuringAutomaticValidation() {
        String identityDocument = "1061754493";
        String email = "julito123@outlook.com";
        Application app = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .term(APPLICATION_TERM)
                .build();

        LoanType loanType = LoanType.builder().interestRate(12.0).id(10L).automaticValidation(Boolean.TRUE).build();

        when(loanTypeRepository.findByAmountInRange(app.getAmount())).thenReturn(Mono.just(loanType));
        when(applicationRepository.findActiveLoan(identityDocument)).thenReturn(Flux.empty());
        when(authenticationGateway.getUsersByIdentityDocuments(List.of(identityDocument))).thenReturn(Mono.just(List.of()));

        Mono<Application> result = registerLoanApplicationUseCase.execute(app, identityDocument, email);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("No user found for the given identity document."))
                .verify();
    }
}