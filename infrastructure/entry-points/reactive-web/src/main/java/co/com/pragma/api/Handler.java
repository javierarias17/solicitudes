package co.com.pragma.api;

import co.com.pragma.api.dto.ApplicationDTO;
import co.com.pragma.api.mapper.ApplicationDTOMapper;
import co.com.pragma.api.validator.ValidationHandler;
import co.com.pragma.usecase.registerloanapplication.inport.RegisterLoanApplicationUseCaseInPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterLoanApplicationUseCaseInPort registerLoanApplicationUseCaseInPort;
    private final ApplicationDTOMapper applicationDTOMapper;
    private final ValidationHandler validationHandler;

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).APPLICANT.code)")
    public Mono<ServerResponse> listenSaveApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ApplicationDTO.class)
                .flatMap(validationHandler::validate)
                .map(applicationDTOMapper::toModel)
                .flatMap(registerLoanApplicationUseCaseInPort::saveApplication)
                .map(applicationDTOMapper::toResponse).flatMap(dto->ServerResponse.status(HttpStatus.CREATED).bodyValue(dto));
    }
}
