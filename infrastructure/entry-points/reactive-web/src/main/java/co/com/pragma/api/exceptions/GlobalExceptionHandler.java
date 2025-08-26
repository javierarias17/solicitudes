package co.com.pragma.api.exceptions;

import co.com.pragma.usecase.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String STATUS = "status";
    private static final String FIELDS = "fields";
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final HashMap<Class<?>, HttpStatus> httpStatusCodes = new HashMap<>();

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  ApplicationContext applicationContext,
                                  org.springframework.http.codec.ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.setMessageReaders(serverCodecConfigurer.getReaders());

        httpStatusCodes.put(BusinessException.class, HttpStatus.BAD_REQUEST);
    }

    private Mono<ServerResponse> buildErrorResponse(ServerRequest request) {
        Throwable throwable = this.getError(request);

        HttpStatus responseCode = getResponseCode((Exception) throwable);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(STATUS, responseCode.value());

        if (throwable instanceof WebExchangeBindException bindException) {
            // Validation errors on DTO binding
            Map<String, String> fieldErrors = bindException.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            fe -> fe.getDefaultMessage(),
                            (first, second) -> first
                    ));
            responseBody.put(FIELDS, fieldErrors);

        } else if (throwable instanceof ConstraintViolationException violationException) {
            // Validation errors thrown manually with Validator
            Map<String, String> violations = violationException.getConstraintViolations()
                    .stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (first, second) -> first
                    ));
            responseBody.put(FIELDS, violations);

        } else if (throwable instanceof BusinessException businessException) {
            // Business exception with custom errors
            responseBody.put(FIELDS, businessException.getErrors());

        } else {
            logger.error("Error handled: {}", throwable.getMessage(), throwable);
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR;
            responseBody.put("message", "An unexpected error occurred. Please contact the administrator.");
        }

        return ServerResponse.status(responseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(responseBody);
    }

    private HttpStatus getResponseCode(Exception throwable) {
        HttpStatus statusCode = httpStatusCodes.get(throwable.getClass());
        return statusCode == null ? HttpStatus.BAD_REQUEST : statusCode;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
    }
}
