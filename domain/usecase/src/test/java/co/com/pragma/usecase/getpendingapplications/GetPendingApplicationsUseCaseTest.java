package co.com.pragma.usecase.getpendingapplications;

import co.com.pragma.model.application.ApplicationSummary;
import co.com.pragma.model.application.User;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.outport.AuthenticationGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPendingApplicationsUseCaseTest {

    @InjectMocks
    private GetPendingApplicationsUseCase getPendingApplicationsUseCase;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private AuthenticationGateway authenticationGateway;

    @Test
    void shouldReturnApplicationsWithUserData() {
        ApplicationSummary app1 = ApplicationSummary.builder()
                .amount(BigDecimal.valueOf(5000000))
                .email("javierarias17.dll@gmail.com")
                .term(12L)
                .loanType("Mortgage")
                .interestRate(1.2)
                .status("Pending review")
                .identityDocument("1061754493")
                .build();

        ApplicationSummary app2 = ApplicationSummary.builder()
                .amount(BigDecimal.valueOf(1000000))
                .email("javierarias17.dll@gmail.com")
                .term(6L)
                .loanType("Personal")
                .interestRate(0.8)
                .status("Pending review")
                .identityDocument("1061754493")
                .build();

        User user = User.builder()
                .identityDocument("1061754493")
                .firstName("Javier")
                .lastName("Arias")
                .email("javierarias17.dll@gmail.com")
                .baseSalary(BigDecimal.valueOf(4000000))
                .build();

        when(applicationRepository.findPendingApplicationsPaged(10, 0))
                .thenReturn(Flux.just(app1, app2));

        when(authenticationGateway.getUsersByIdentityDocuments(List.of("1061754493")))
                .thenReturn(Mono.just(List.of(user)));

        Flux<ApplicationSummary> result = getPendingApplicationsUseCase.execute(0, 10);

        StepVerifier.create(result)
                .expectNextMatches(app ->
                        app.getFullName().equals(user.getFirstName()+" "+user.getLastName()) &&
                                app.getEmail().equals(app1.getEmail()) &&
                                app.getBaseSalary().equals(user.getBaseSalary()) &&
                                app.getIdentityDocument().equals(user.getIdentityDocument()) &&
                                app.getLoanType().equals(app1.getLoanType()) &&
                                app.getStatus().equals(app1.getStatus()))
                .expectNextMatches(app ->
                        app.getFullName().equals(user.getFirstName()+" "+user.getLastName()) &&
                                app.getEmail().equals(app2.getEmail()) &&
                                app.getBaseSalary().equals(user.getBaseSalary()) &&
                                app.getIdentityDocument().equals(app2.getIdentityDocument()) &&
                                app.getLoanType().equals(app2.getLoanType()) &&
                                app.getStatus().equals(app2.getStatus()))
                .verifyComplete();
    }

    @Test
    void shouldKeepOriginalApplicationDataWhenUserNotFound() {
        ApplicationSummary app1 = ApplicationSummary.builder()
                .amount(BigDecimal.valueOf(5000000))
                .email("javierarias17.dll@gmail.com")
                .term(12L)
                .loanType("Mortgage")
                .interestRate(1.2)
                .status("Pending review")
                .identityDocument("1061754493")
                .build();

        when(applicationRepository.findPendingApplicationsPaged(10, 0))
                .thenReturn(Flux.just(app1));

        when(authenticationGateway.getUsersByIdentityDocuments(List.of("1061754493")))
                .thenReturn(Mono.just(List.of()));

        Flux<ApplicationSummary> result = getPendingApplicationsUseCase.execute(0, 10);

        StepVerifier.create(result)
                .expectNextMatches(app ->
                        app.getFullName()==null &&
                                app.getEmail().equals(app1.getEmail()) &&
                                app.getBaseSalary()==null &&
                                app.getIdentityDocument().equals(app1.getIdentityDocument()) &&
                                app.getLoanType().equals(app1.getLoanType()) &&
                                app.getStatus().equals(app1.getStatus())).verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoApplications() {
        when(applicationRepository.findPendingApplicationsPaged(10, 0))
                .thenReturn(Flux.empty());

        Flux<ApplicationSummary> result = getPendingApplicationsUseCase.execute(0, 10);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenAuthenticationGatewayFails() {
        ApplicationSummary app1 = ApplicationSummary.builder()
                .amount(BigDecimal.valueOf(5000000))
                .email("javierarias17.dll@gmail.com")
                .term(12L)
                .loanType("Mortgage")
                .interestRate(1.2)
                .status("Pending review")
                .identityDocument("1061754493")
                .build();

        when(applicationRepository.findPendingApplicationsPaged(10, 0))
                .thenReturn(Flux.just(app1));

        when(authenticationGateway.getUsersByIdentityDocuments(List.of("1061754493")))
                .thenReturn(Mono.error(new RuntimeException("Auth service unavailable")));

        Flux<ApplicationSummary> result = getPendingApplicationsUseCase.execute(0, 10);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Auth service unavailable"))
                .verify();
    }

    @Test
    void shouldPropagateErrorWhenRepositoryFails() {
        when(applicationRepository.findPendingApplicationsPaged(10, 0))
                .thenReturn(Flux.error(new RuntimeException("Error databaste")));

        Flux<ApplicationSummary> result = getPendingApplicationsUseCase.execute(0, 10);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error databaste"))
                .verify();
    }






}