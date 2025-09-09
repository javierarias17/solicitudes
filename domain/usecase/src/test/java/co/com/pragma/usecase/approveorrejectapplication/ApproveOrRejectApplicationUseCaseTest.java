package co.com.pragma.usecase.approveorrejectapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.outport.AwsQueueGateway;
import co.com.pragma.usecase.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApproveOrRejectApplicationUseCaseTest {

    @InjectMocks
    private ApproveOrRejectApplicationUseCase useCase;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private AwsQueueGateway notificationQueueGateway;

    private Application buildApplication(Long id, Long statusId, String email) {
        return Application.builder().id(id).statusId(statusId).email(email).build();
    }

    @Test
    void shouldReturnErrorWhenApplicationNotFound() {
        when(applicationRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(1L, 4L))
                .expectErrorMatches(error ->
                        error instanceof ValidationException &&
                                ((ValidationException) error).getErrors().equals(Map.of("id", "Application not found")))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenStatusIsTheSame() {
        Application app = buildApplication(1L, 4L, "javierarias17.dll@gmail.com");
        when(applicationRepository.findById(1L)).thenReturn(Mono.just(app));

        StepVerifier.create(useCase.execute(1L, 4L))
                .expectErrorMatches(error ->
                        error instanceof ValidationException &&
                                ((ValidationException) error).getErrors().equals(Map.of("statusId", "Application already has status")))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenStatusNotAllowed() {
        Application app = buildApplication(1L, 1L, "javierarias17.dll@gmail.com");
        when(applicationRepository.findById(1L)).thenReturn(Mono.just(app));

        StepVerifier.create(useCase.execute(1L, 3L))
                .expectErrorMatches(error ->
                        error instanceof ValidationException &&
                                ((ValidationException) error).getErrors().equals(Map.of("statusId", "Status not allowed. Only (4)APPROVED or (2)REJECTED are permitted")))
                .verify();
    }

    @Test
    void shouldApproveApplicationSuccessfully() {
        Application app = buildApplication(1L, 1L, "javierarias17.dll@gmail.com");
        Application savedApp = buildApplication(1L, 4L, "javierarias17.dll@gmail.com");

        when(applicationRepository.findById(1L)).thenReturn(Mono.just(app));
        when(applicationRepository.saveApplication(any(Application.class))).thenReturn(Mono.just(savedApp));
        when(notificationQueueGateway.sendNotification("javierarias17.dll@gmail.com", "APROBADO")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(1L, 4L))
                .expectNextMatches(result ->
                        result.getStatusId().equals(4L) &&
                                result.getEmail().equals("javierarias17.dll@gmail.com"))
                .verifyComplete();
    }

    @Test
    void shouldRejectApplicationSuccessfully() {
        Application app = buildApplication(1L, 1L, "javierarias17.dll@gmail.com");
        Application savedApp = buildApplication(1L, 2L, "javierarias17.dll@gmail.com");

        when(applicationRepository.findById(1L)).thenReturn(Mono.just(app));
        when(applicationRepository.saveApplication(any(Application.class))).thenReturn(Mono.just(savedApp));
        when(notificationQueueGateway.sendNotification("javierarias17.dll@gmail.com", "RECHAZADO")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(1L, 2L))
                .expectNextMatches(result ->
                        result.getStatusId().equals(2L) &&
                                result.getEmail().equals("javierarias17.dll@gmail.com"))
                .verifyComplete();
    }
}