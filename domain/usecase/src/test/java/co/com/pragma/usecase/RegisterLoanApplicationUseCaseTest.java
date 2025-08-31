package co.com.pragma.usecase;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.status.gateways.StatusRepository;
import co.com.pragma.usecase.exceptions.ValidationException;
import co.com.pragma.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterLoanApplicationUseCaseTest {

    private static final Long VALID_LOAN_TYPE_ID = 10L;
    private static final Long INVALID_LOAN_TYPE_ID = 21L;
    private static final Long PENDING_REVIEW_STATUS_ID = 1L;
    private static final BigDecimal APPLICATION_AMOUNT = BigDecimal.valueOf(1000.00);

    @InjectMocks
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Test
    void shouldCreateLoanApplicationWhenDataIsValid() {
        Application application = Application.builder()
                .id(null)
                .amount(APPLICATION_AMOUNT)
                .loanTypeId(VALID_LOAN_TYPE_ID)
                .statusId(PENDING_REVIEW_STATUS_ID)
                .build();

        when(loanTypeRepository.existsById(VALID_LOAN_TYPE_ID)).thenReturn(Mono.just(true));
        when(statusRepository.existsById(PENDING_REVIEW_STATUS_ID)).thenReturn(Mono.just(true));
        when(applicationRepository.saveApplication(any(Application.class)))
                .thenReturn(Mono.just(application.toBuilder().id(1L).build()));

        Mono<Application> result = registerLoanApplicationUseCase.saveApplication(application);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenLoanTypeDoesNotExist() {
        Application application = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .loanTypeId(INVALID_LOAN_TYPE_ID)
                .statusId(PENDING_REVIEW_STATUS_ID)
                .build();

        when(loanTypeRepository.existsById(INVALID_LOAN_TYPE_ID)).thenReturn(Mono.just(false));
        when(statusRepository.existsById(PENDING_REVIEW_STATUS_ID)).thenReturn(Mono.just(true));
        Mono<Application> result = registerLoanApplicationUseCase.saveApplication(application);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                ((ValidationException) throwable).getErrors().containsKey("loanTypeId")
                ).verify();
    }

    @Test
    void shouldFailWhenStatusDoesNotExist() {
        Application application = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .loanTypeId(VALID_LOAN_TYPE_ID)
                .statusId(PENDING_REVIEW_STATUS_ID)
                .build();

        when(loanTypeRepository.existsById(VALID_LOAN_TYPE_ID)).thenReturn(Mono.just(true));
        when(statusRepository.existsById(PENDING_REVIEW_STATUS_ID)).thenReturn(Mono.just(false));

        Mono<Application> result = registerLoanApplicationUseCase.saveApplication(application);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                ((ValidationException) throwable).getErrors().containsKey("statusId")
                ).verify();
    }

    @Test
    void shouldFailWhenLoanTypeAndStatusDoNotExist() {
        Application application = Application.builder()
                .amount(APPLICATION_AMOUNT)
                .loanTypeId(INVALID_LOAN_TYPE_ID)
                .statusId(PENDING_REVIEW_STATUS_ID)
                .build();

        when(loanTypeRepository.existsById(INVALID_LOAN_TYPE_ID)).thenReturn(Mono.just(false));
        when(statusRepository.existsById(PENDING_REVIEW_STATUS_ID)).thenReturn(Mono.just(false));

        Mono<Application> result = registerLoanApplicationUseCase.saveApplication(application);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                ((ValidationException) throwable).getErrors().containsKey("statusId") &&
                                ((ValidationException) throwable).getErrors().containsKey("loanTypeId")
                ).verify();
    }

}