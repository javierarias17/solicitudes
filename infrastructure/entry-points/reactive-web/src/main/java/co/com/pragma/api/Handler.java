package co.com.pragma.api;

import co.com.pragma.api.dto.ApplicationDTO;
import co.com.pragma.api.mapper.ApplicationDTOMapper;
import co.com.pragma.usecase.exceptions.TechnicalException;
import co.com.pragma.usecase.exceptions.ValidationException;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterLoanApplicationUseCaseInPort registerLoanApplicationUseCaseInPort;
    private final ApplicationDTOMapper applicationDTOMapper;

    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ApplicationDTO.class)
                .flatMap(applicationDTO -> registerLoanApplicationUseCaseInPort.saveApplication(applicationDTOMapper.toModel(applicationDTO)))
                .flatMap(savedApplication -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(applicationDTOMapper.toResponse(savedApplication)))
                // Manejo de request malformado -> 400
                .onErrorResume(ServerWebInputException.class, e -> {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("Invalid request body:", e.getMostSpecificCause().getMessage());
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(errors);
                })
                // Manejo de validaciones -> 400
                .onErrorResume(ValidationException.class, e ->
                        ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(e.getErrors())
                )
                // Manejo de excepciones inesperadas -> 500
                .onErrorResume(TechnicalException.class, e ->
                        ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_PLAIN).bodyValue(e.getMessage())
                );
    }
}
