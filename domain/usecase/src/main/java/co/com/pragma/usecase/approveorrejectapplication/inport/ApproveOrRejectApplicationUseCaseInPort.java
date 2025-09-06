package co.com.pragma.usecase.approveorrejectapplication.inport;
import co.com.pragma.model.application.Application;
import reactor.core.publisher.Mono;

public interface ApproveOrRejectApplicationUseCaseInPort {
    Mono<Application> execute(Long id, Long statusId);
}
