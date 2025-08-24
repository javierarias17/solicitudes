package co.com.pragma.api;

import co.com.pragma.model.application.Application;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterLoanApplicationUseCaseInPort registerLoanApplicationUseCaseInPort;

    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Application.class)
                .flatMap(registerLoanApplicationUseCaseInPort::saveApplication)
                .flatMap(savedApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(savedApplication))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\": \"" + e.getMessage() + "\"}")
                );
    }
}
