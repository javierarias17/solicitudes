package co.com.pragma.api;

import co.com.pragma.api.config.ApplicationPath;
import co.com.pragma.api.dto.ApplicationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final ApplicationPath applicationPath;
    private final Handler handler;


    @Bean
    @RouterOperation(operation = @Operation(operationId = "registerLoanApplication", summary = "Registrar una solicitud de prestamo", tags = { "Solicitud prestamo" },
            requestBody = @RequestBody(
                    description = "Objeto JSON con los campos de la solicitud de préstamo.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "amount": 3000000,
                                                      "term": 1,
                                                      "email": "javierarias17.dll@gmail.com",
                                                      "loanTypeId": 1,
                                                      "identityDocument": "1061758338"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = { @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationDTO.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "amount": 3000000,
                                                      "term": 1,
                                                      "email": "javierarias17.dll@gmail.com",
                                                      "statusId": 1,
                                                      "loanTypeId": 1,
                                                      "identityDocument": "1061758338"
                                                    }
                                                    """
                                    )
                            }
                    )),
                    @ApiResponse(responseCode = "400", description = "Bad Request",content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "fields": {
                                                            "amount": "Amount is required",
                                                            "identityDocument": "Identity document is required and cannot be empty",
                                                            "term": "Term is required",
                                                            "email": "Email is required and cannot be empty",
                                                            "loanTypeId": "Loan type is required"
                                                      }
                                                    }
                                                    """
                                    )
                            }                    )),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "An unexpected error occurred. Please contact the administrator."
                                                    }
                                                    """
                                    )
                            }
                    ))
            }
    ))
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(applicationPath.getApplication()), handler::listenSaveApplication);
    }
}
