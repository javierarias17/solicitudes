package co.com.pragma.api;

import co.com.pragma.api.config.ApplicationPath;
import co.com.pragma.api.dto.ApplicationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final ApplicationPath applicationPath;
    private final Handler handler;


    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = { "application/json" },
                    method = RequestMethod.POST,
                    operation = @Operation(operationId = "registerLoanApplication", summary = "Registrar una solicitud de prestamo", tags = { "Loan application" },
                    requestBody = @RequestBody(
                            description = "Input data",
                            required = true,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApplicationDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                      "amount": 3000000,
                                                      "term": 1
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
                                                            "term": "Term is required"
                                                      }
                                                    }
                                                    """
                                            )
                                    }                    )),

                            @ApiResponse(
                                    responseCode = "403",
                                    description = "Forbidden",
                                    content = @Content(
                                            mediaType = "text/plain",
                                            examples = {
                                                    @ExampleObject(
                                                            value = "Access Denied"
                                                    )
                                            }
                                    )
                            ),
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
            )),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = { "application/json" },
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "listenGetPendingApplications",
                            summary = "Listar solicitudes para revision manual",
                            tags = { "Loan application" },
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "page",
                                            description = "Page number (zero-based)",
                                            required = true,
                                            schema = @Schema(type = "integer")
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "size",
                                            description = "Number of elements per page",
                                            required = true,
                                            schema = @Schema(type = "integer")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationDTO.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                                        [
                                                                            {
                                                                                "amount": 300000.00,
                                                                                "term": 12,
                                                                                "email": "javierarias17.dll@gmail.com",
                                                                                "fullName": "Javier Arias",
                                                                                "loanType": "Personal",
                                                                                "interestRate": 12.5,
                                                                                "status": "Pending review",
                                                                                "baseSalary": 350000.0,
                                                                                "totalMonthlyDebt": 58333.333333333333
                                                                            }
                                                                        ]
                                                                        """
                                                            )
                                                    }
                                    )),
                                    @ApiResponse(responseCode = "400", description = "Bad Request",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationDTO.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                                        {
                                                                            "message": "Validation errors",
                                                                            "fields": {
                                                                                "size": "Size must be greater than 0",
                                                                                "page": "Page must be greater than or equal to 0"
                                                                            }
                                                                        }
                                                                        """
                                                            )
                                                    }
                                    )),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Forbidden",
                                            content = @Content(
                                                    mediaType = "text/plain",
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = "Access Denied"
                                                            )
                                                    }
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = { "application/json" },
                    method = RequestMethod.PUT,
                    operation = @Operation(
                            operationId = "listenApproveOrRejectApplication",
                            summary = "Aprobar o rechazar solicitudes",
                            tags = { "Loan application" },
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "id",
                                            description = "Application id",
                                            required = true,
                                            schema = @Schema(type = "long")
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "statusId",
                                            description = "Status id",
                                            required = true,
                                            schema = @Schema(type = "long")
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationDTO.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                                        {
                                                                            "id": 1,
                                                                            "amount": 6000000.00,
                                                                            "term": 12,
                                                                            "email": "apedro@unicauca.edu.co",
                                                                            "statusId": 2,
                                                                            "loanTypeId": 2,
                                                                            "identityDocument": "34561194"
                                                                        }
                                                                        """
                                                            )
                                                    }
                                            )),
                                    @ApiResponse(responseCode = "400", description = "Bad Request",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApplicationDTO.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = """
                                                                        {
                                                                            "message": "Validation errors",
                                                                            "fields": {
                                                                                "id": "Application not found",
                                                                                "statusId": "Status not allowed. Only (4)APPROVED or (2)REJECTED are permitted"
                                                                            }
                                                                        }
                                                                        """
                                                            )
                                                    }
                                            )),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Forbidden",
                                            content = @Content(
                                                    mediaType = "text/plain",
                                                    examples = {
                                                            @ExampleObject(
                                                                    value = "Access Denied"
                                                            )
                                                    }
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(applicationPath.getApplication()), handler::listenRegisterLoanApplication)
                .andRoute(GET(applicationPath.getApplication()), handler::listenGetPendingApplications)
                .andRoute(PUT(applicationPath.getApplication()), handler::listenApproveOrRejectApplication);
    }
}
