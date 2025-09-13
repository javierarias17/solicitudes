package co.com.pragma.usecase.approveorrejectapplication;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.gateways.ApplicationRepository;
import co.com.pragma.model.outport.AwsQueueGateway;
import co.com.pragma.usecase.approveorrejectapplication.inport.ApproveOrRejectApplicationUseCaseInPort;
import co.com.pragma.usecase.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public class ApproveOrRejectApplicationUseCase implements ApproveOrRejectApplicationUseCaseInPort {
    private static final Long REJECT_STATUS_ID=2L;
    private static final Long APPROVED_STATUS_ID=4L;
    private static final String STATUS_ID = "statusId";
    private static final String REJECTED = "REJECTED";
    private static final String APPROVED = "APPROVED";
    private final ApplicationRepository applicationRepository;
    private final AwsQueueGateway awsQueueGateway;

    @Override
    public Mono<Application> execute(Long id, Long statusId) {
        return applicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new ValidationException(
                        Map.of("id", "Application not found")
                )))
                .flatMap(application -> {

                    if (application.getStatusId().equals(statusId)) {
                        return Mono.error(new ValidationException(
                                Map.of(STATUS_ID, "Application already has status")
                        ));
                    }

                    if (!statusId.equals(REJECT_STATUS_ID) && !statusId.equals(APPROVED_STATUS_ID)) {
                        return Mono.error(new ValidationException(
                                Map.of(STATUS_ID, "Status not allowed. Only (4)APPROVED or (2)REJECTED are permitted")
                        ));
                    }

                    application.setStatusId(statusId);
                    return applicationRepository.saveApplication(application)
                            .flatMap(savedApp ->
                                    Mono.when(
                                            //HU06
                                            awsQueueGateway.sendNotificationQueue(
                                                savedApp.getEmail(),
                                                statusId.equals(APPROVED_STATUS_ID) ? APPROVED : REJECTED
                                            ).onErrorResume(e -> Mono.empty()),
                                            //HU08-09
                                            statusId.equals(APPROVED_STATUS_ID) ? awsQueueGateway.sendApprovedLoansQueue(savedApp.getAmount())
                                            .onErrorResume(e -> Mono.empty()): Mono.empty()
                                    ).thenReturn(savedApp)
                            );
                });
    }
}
