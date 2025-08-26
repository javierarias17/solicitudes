package co.com.pragma.api.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class ValidationHandler {
    private final Validator validator;

    public ValidationHandler(Validator validator) {
        this.validator = validator;
    }

    public <T> Mono<T> validate(T object) {
        return Mono.defer(() -> {
            Set<ConstraintViolation<T>> violations = validator.validate(object);
            if (!violations.isEmpty()) {
                return Mono.error(new ConstraintViolationException(violations));
            }
            return Mono.just(object);
        });
    }

    public <T> Mono<Object> validateValue(Class<T> beanType, String propertyName, Object value) {
        return Mono.defer(() -> {
            Set<ConstraintViolation<T>> violations = validator.validateValue(beanType, propertyName, value);
            if (!violations.isEmpty()) {
                return Mono.error(new ConstraintViolationException(violations));
            }
            return Mono.just(value);
        });
    }
}