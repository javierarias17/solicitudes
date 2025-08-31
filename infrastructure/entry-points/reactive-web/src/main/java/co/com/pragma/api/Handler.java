package co.com.pragma.api;

import co.com.pragma.api.dto.ApplicationDTO;
import co.com.pragma.api.mapper.ApplicationDTOMapper;
import co.com.pragma.api.mapper.ApplicationSummaryDTOMapper;
import co.com.pragma.api.validator.ValidationHandler;
import co.com.pragma.api.validator.input.PaginationRequest;
import co.com.pragma.usecase.getpendingapplications.inport.GetPendingApplicationsUseCaseInPort;
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

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Handler {

    private final RegisterLoanApplicationUseCaseInPort registerLoanApplicationUseCaseInPort;
    private final GetPendingApplicationsUseCaseInPort getPendingApplicationsUseCaseInPort;
    private final ApplicationDTOMapper applicationDTOMapper;
    private final ApplicationSummaryDTOMapper applicationSummaryDTOMapper;
    private final ValidationHandler validationHandler;

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).APPLICANT.code)")
    public Mono<ServerResponse> listenRegisterLoanApplication(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ApplicationDTO.class)
                .flatMap(validationHandler::validate)
                .map(applicationDTOMapper::toModel)
                .flatMap(registerLoanApplicationUseCaseInPort::execute)
                .map(applicationDTOMapper::toResponse).flatMap(dto->ServerResponse.status(HttpStatus.CREATED).bodyValue(dto));
    }

    @PreAuthorize("hasAuthority(T(co.com.pragma.api.security.Role).ADVISOR.code)")
    public Mono<ServerResponse> listenGetPendingApplications(ServerRequest serverRequest) {
        Integer page = safeParseInt(serverRequest.queryParam("page"));
        Integer size =safeParseInt(serverRequest.queryParam("size"));

        PaginationRequest paginationRequest = new PaginationRequest(page, size);

        return validationHandler.validate(paginationRequest)
                .flatMap(params -> getPendingApplicationsUseCaseInPort.execute(params.page(), params.size())
                        .map(applicationSummaryDTOMapper::toResponse)
                        .collectList()
                        .flatMap(list -> ServerResponse.ok().bodyValue(list)));
    }

    private Integer safeParseInt(Optional<String> param) {
        try {
            return param.isPresent() ? Integer.parseInt(param.get()) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
